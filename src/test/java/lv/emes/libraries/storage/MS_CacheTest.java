package lv.emes.libraries.storage;

import lv.emes.libraries.tools.lists.MS_List;
import lv.emes.libraries.tools.logging.MS_InMemoryLoggingRepository;
import lv.emes.libraries.tools.logging.MS_MultiLogger;
import lv.emes.libraries.tools.logging.MS_MultiLoggingSetup;
import lv.emes.libraries.tools.threading.MS_FutureEvent;
import lv.emes.libraries.utilities.MS_CodingUtils;
import lv.emes.libraries.utilities.MS_ExecutionFailureException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * These tests are using {@link MS_InMemoryLoggingRepository} in order to test caching functionality with some
 * realistic type of cache repository.
 *
 * @author eMeS
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MS_CacheTest {

    public static final String FIRST = "First object will stay there until it will be removed manually";
    public static final String SECOND = "Second object will become expired after 1 second";

    private static MS_Cache<String, Integer> cache;
    private static MS_MultiLogger logger;
    private static MS_InMemoryLoggingRepository logs;
    private static Integer lastId = 0;

    private MS_FutureEvent operThread;

    @BeforeClass
    public static void init() {
        MS_CachingRepository<String, Integer> inMemoryRepository = new MS_InMemoryCachingRepository<>(
                MS_InMemoryCachingRepository._DEFAULT_REPOSITORY_ROOT_NAME,
                MS_InMemoryCachingRepository._DEFAULT_CACHE_NAME);
        logs = new MS_InMemoryLoggingRepository();
        logger = new MS_MultiLogger(new MS_MultiLoggingSetup().withRepository(logs));
        cache = new MS_Cache<>(inMemoryRepository);
        cache.setDefaultTTL(1);
        cache.setLogger(logger);
    }

    @Test
    public void test01CheckSetParams() {
        assertThat(cache.getDefaultTTL()).isEqualTo(1);
        assertThat(cache.getLogger()).isEqualTo(logger);
        assertThat(cache.getRepository().isInitialized()).as("Repository is not initialized, but it should've been on init() step").isTrue();
    }

    @Test(expected = NullPointerException.class)
    public void test02CreateCacheWithNullRepositoryFailure() {
        new MS_Cache<>(null);
    }

    @Test
    public void test11StoreAndRetrieveFirst() throws Exception {
        operThread = cache.cache(FIRST, ++lastId, 0L);
        MS_FutureEvent.joinEvents(5, 20, operThread);
        assertThat(cache.getRepository().size()).isEqualTo(1);

        assertThat(cache.get(lastId)).isEqualTo(FIRST);
        verifyCurrentLogCount(0);
    }

    @Test
    public void test12StoreSecondAndBothAreStillThere() throws MS_ExecutionFailureException {
        operThread = cache.cache(SECOND, ++lastId);
        MS_FutureEvent.joinEvents(5, 20, operThread);
        assertThat(cache.getRepository().size()).isEqualTo(2);

        assertThat(cache.get(lastId)).isEqualTo(SECOND);
        assertThat(cache.get(lastId - 1)).isEqualTo(FIRST);
        verifyCurrentLogCount(0);
    }

    @Test
    public void test13CleanupAfter1SecondSecondObjectIsGoneBut1stRemains() {
        assertThat(cache.get(lastId)).as("Performance issues: Second object shouldn't be expired yet").isNotNull();
        MS_CodingUtils.sleep(1000);
        assertThat(cache.get(lastId)).as("Second object is still in cache after 1 second, which was its TTL. ").isNull();
        //if this call will be fast enough we might still find object in real cache
        assertThat(cache.getRepository().get(lastId)).as("Performance issues: As expired object cleanup takes some time " +
                "while new thread is created, actual repository should've been returned second object," +
                "which still should've been there.").isNotNull();

        assertThat(cache.get(lastId - 1)).as("First object should stay in cache forever").isEqualTo(FIRST);
        verifyCurrentLogCount(0);

        MS_CodingUtils.sleep(100); //sleep a bit more until cleanup will be completed
        assertThat(cache.getRepository().get(lastId)).as("Performance issues: After additional waiting second object should've been already removed" +
                "from cache repository completely, but it doesn't").isNull();
        verifyCurrentLogCount(0); //hopefully, Concurrent modification exception will not arise here!
    }

    @Test
    public void test14ClearingBunchOfObjectsHappensInSameThread() throws MS_ExecutionFailureException {
        assertThat(cache.getRepository().size()).isEqualTo(1); //first element is still there
        MS_List<MS_FutureEvent> cachingThreads = new MS_List<>();
        operThread = cache.cache(RandomStringUtils.randomAlphabetic(10), ++lastId);
        cachingThreads.add(operThread);
        operThread = cache.cache(RandomStringUtils.randomAlphabetic(10), ++lastId, 0L);
        cachingThreads.add(operThread);
        operThread = cache.store(RandomStringUtils.randomAlphabetic(10), ++lastId);
        cachingThreads.add(operThread);
        operThread = cache.store(RandomStringUtils.randomAlphabetic(10), ++lastId, 54L);
        cachingThreads.add(operThread);

        MS_FutureEvent.joinEvents(cachingThreads, 5, 20);
        assertThat(cache.getRepository().size()).isEqualTo(5);
        operThread = cache.removeAll();
        MS_FutureEvent.joinEvents(5, 20, operThread);
        assertThat(cache.getRepository().size()).as("After clearing cache there should not have been any object left").isEqualTo(0);
        assertThat(cache.get(1)).isNull();
        assertThat(cache.get(lastId)).isNull();
        verifyCurrentLogCount(0);
        lastId = 0; //restart ID counter, cause at this point all logs are cleared
    }

    @Test
    public void test21UnsupportedActions() throws MS_ExecutionFailureException {
        MS_Cache<String, Integer> cacheForUnsupportedActions = newCacheForUnsupportedActions();
        cacheForUnsupportedActions.setLogger(logger);
        verifyCurrentLogCount(0);

        operThread = cacheForUnsupportedActions.store(FIRST, 1);
        MS_FutureEvent.joinEvents(6, 20, operThread);
        verifyCurrentLogCount(1);

        cacheForUnsupportedActions.retrieve(1);
        MS_CodingUtils.sleep(60);
        verifyCurrentLogCount(2);

        operThread = cacheForUnsupportedActions.clear();
        MS_FutureEvent.joinEvents(5, 20, operThread);
        verifyCurrentLogCount(3);
        logs.getEventList().clear(); //restart logger, because in next steps we will need clean logs
    }

    @Test
    public void test22repositoryDataExceptions() throws MS_ExecutionFailureException {
        MS_Cache<String, Integer> cacheForUnsupportedActions = newCacheForRepositoryDataException();
        cacheForUnsupportedActions.setLogger(logger);
        verifyCurrentLogCount(0);

        operThread = cacheForUnsupportedActions.store(FIRST, 1);
        MS_FutureEvent.joinEvents(6, 20, operThread);
        verifyCurrentLogCount(1);

        cacheForUnsupportedActions.retrieve(1);
        MS_CodingUtils.sleep(60);
        verifyCurrentLogCount(2);

        operThread = cacheForUnsupportedActions.clear();
        MS_FutureEvent.joinEvents(6, 20, operThread);
        verifyCurrentLogCount(3);
        logs.getEventList().clear(); //restart logger, because in next steps we will need clean logs
    }

    @Test
    public void test31ConcurrencyNonConflictingIds() throws MS_ExecutionFailureException {
        MS_List<MS_FutureEvent> threads1_100 = newThreadListToStoreAndRetrieveObjects(1, 100);
        MS_List<MS_FutureEvent> threads101_200 = newThreadListToStoreAndRetrieveObjects(101, 200);
        MS_List<MS_FutureEvent> threads201_300 = newThreadListToStoreAndRetrieveObjects(201, 300);
        MS_List<MS_FutureEvent> threads301_400 = newThreadListToStoreAndRetrieveObjects(301, 400);
        MS_List<MS_FutureEvent> threads401_500 = newThreadListToStoreAndRetrieveObjects(401, 500);

        MS_List<MS_FutureEvent> allThreadsTogether = new MS_List<>();
        allThreadsTogether.concatenate(threads1_100);
        allThreadsTogether.concatenate(threads101_200);
        allThreadsTogether.concatenate(threads201_300);
        allThreadsTogether.concatenate(threads301_400);
        allThreadsTogether.concatenate(threads401_500);

        //wait until last thread executes
        MS_FutureEvent.joinEvents(allThreadsTogether, 35, 30);
        verifyCurrentLogCount(0);
        assertThat(cache.getRepository().size()).isEqualTo(500);
    }

    @Test
    public void test32ConcurrencyConflictingIds() throws MS_ExecutionFailureException {
        cache.clear();
        MS_List<MS_FutureEvent> threads1 = newThreadListToStoreAndRetrieveObjects(1, 100);
        MS_List<MS_FutureEvent> threads2 = newThreadListToStoreAndRetrieveObjects(20, 120);
        MS_List<MS_FutureEvent> threads3 = newThreadListToStoreAndRetrieveObjects(40, 140);
        MS_List<MS_FutureEvent> threads4 = newThreadListToStoreAndRetrieveObjects(60, 160);
        MS_List<MS_FutureEvent> threads5 = newThreadListToStoreAndRetrieveObjects(80, 180);
        MS_List<MS_FutureEvent> threads6 = newThreadListToStoreAndRetrieveObjects(100, 200);

        MS_List<MS_FutureEvent> allThreadsTogether = new MS_List<>();
        allThreadsTogether.concatenate(threads1);
        allThreadsTogether.concatenate(threads2);
        allThreadsTogether.concatenate(threads3);
        allThreadsTogether.concatenate(threads4);
        allThreadsTogether.concatenate(threads5);
        allThreadsTogether.concatenate(threads6);

        //wait until last thread executes
        MS_FutureEvent.joinEvents(allThreadsTogether, 45, 45);
        verifyCurrentLogCount(0);
        assertThat(cache.getRepository().size()).isEqualTo(200);
    }

    //*** Private methods ***

    private void verifyCurrentLogCount(int expectedCount) {
        assertThat(logs.getEventList().size()).as("Error and warning log count differs from expected at this point").isEqualTo(expectedCount);
    }

    private MS_List<MS_FutureEvent> newThreadListToStoreAndRetrieveObjects(int idStartingFrom, int idEndingAt) {
        MS_List<MS_FutureEvent> createdThreads = new MS_List<>();
        for (int i = idStartingFrom; i <= idEndingAt; i++) {
            MS_FutureEvent storeThread = cache.store(RandomStringUtils.randomAlphabetic(10), i, 1L);
            createdThreads.add(storeThread);
        }
        return createdThreads;
    }

    private MS_Cache<String, Integer> newCacheForUnsupportedActions() {
        return new MS_Cache<>(
                new MS_CachingRepository<String, Integer>("", "", true) {
                    @Override
                    protected void doAdd(Integer identifier, Pair<String, LocalDateTime> object) {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    protected Pair<String, LocalDateTime> doFind(Integer identifier) {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    protected void doRemove(Integer identifier) {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    protected void doRemoveAll() {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public boolean isInitialized() {
                        return true;
                    }

                    @Override
                    protected void doInitialize() {
                    }

                    @Override
                    protected Map<Integer, Pair<String, LocalDateTime>> doFindAll() {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    protected int doGetSize() {
                        throw new UnsupportedOperationException();
                    }
                });
    }

    private MS_Cache<String, Integer> newCacheForRepositoryDataException() {
        return new MS_Cache<>(
                new MS_CachingRepository<String, Integer>("", "", true) {
                    @Override
                    protected void doAdd(Integer identifier, Pair<String, LocalDateTime> object) {
                        throw new MS_RepositoryDataExchangeException();
                    }

                    @Override
                    protected Pair<String, LocalDateTime> doFind(Integer identifier) {
                        throw new MS_RepositoryDataExchangeException();
                    }

                    @Override
                    protected void doRemove(Integer identifier) {
                        throw new MS_RepositoryDataExchangeException();
                    }

                    @Override
                    protected void doRemoveAll() {
                        throw new MS_RepositoryDataExchangeException();
                    }

                    @Override
                    public boolean isInitialized() {
                        return true;
                    }

                    @Override
                    protected void doInitialize() {
                    }

                    @Override
                    protected Map<Integer, Pair<String, LocalDateTime>> doFindAll() {
                        throw new MS_RepositoryDataExchangeException();
                    }

                    @Override
                    protected int doGetSize() {
                        throw new MS_RepositoryDataExchangeException();
                    }
                });
    }
}