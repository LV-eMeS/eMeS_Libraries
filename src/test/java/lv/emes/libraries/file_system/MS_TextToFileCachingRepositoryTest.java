package lv.emes.libraries.file_system;

import lv.emes.libraries.testdata.TestData;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * These tests are partly integration tests with {@link lv.emes.libraries.storage.MS_Cache} class.
 *
 * @author eMeS
 */
public class MS_TextToFileCachingRepositoryTest {

    private static final String PROJECT_NAME = TestData.TEMP_DIR + "eMeS_Libraries/";
    private static final String REPOSITORY_ROOT = PROJECT_NAME + "MS_TextToFileCachingRepositoryTest";
    private static final String FIRST_FILE = "first.txt";
    private static final String SECOND_FILE = "second.txt";
    private static final String THIRD = "Third object ## HAS some deli#miter characters inside & used, but it should still be ok.";

    private static CachingTestHelper<String, String> helper;

    @BeforeClass
    public static void init() {
        helper = new CachingTestHelper<>();
        helper.init(new MS_TextToFileCachingRepository(REPOSITORY_ROOT, FIRST_FILE));
    }

    @AfterClass
    public static void finalizeTestConditions() {
        MS_FileSystemTools.deleteDirectory(PROJECT_NAME);
    }

    @Test
    public void test01CheckSetParams() {
        helper.checkSetParams();
    }
//
//    @Test
//    public void test11StoreAndRetrieveFirst() throws MS_ExecutionFailureException {
//        operThread = cache.cache(FIRST, ++lastId, 0L);
//        MS_FutureEvent.joinEvents(5, 20, operThread);
//        assertEquals(1, cache.getRepository().size());
//
//        assertEquals(FIRST, cache.get(lastId));
//        verifyCurrentLogCount(0);
//    }
//
//    @Test
//    public void test12StoreSecondAndBothAreStillThere() throws MS_ExecutionFailureException {
//        operThread = cache.cache(SECOND, ++lastId);
//        MS_FutureEvent.joinEvents(5, 20, operThread);
//        assertEquals(2, cache.getRepository().size());
//
//        assertEquals(SECOND, cache.get(lastId));
//        assertEquals(FIRST, cache.get(lastId - 1));
//        verifyCurrentLogCount(0);
//    }
//
//    @Test
//    public void test13CleanupAfter1SecondSecondObjectIsGoneBut1stRemains() {
//        assertNotNull("Performance issues: Second object shouldn't be expired yet", cache.get(lastId));
//        MS_CodingUtils.sleep(1000);
//        assertNull("Second object is still in cache after 1 second, which was its TTL. ", cache.get(lastId));
//        //if this call will be fast enough we might still find object in real cache
//        assertNotNull("Performance issues: As expired object cleanup takes some time " +
//                        "while new thread is created, actual repository should've been returned second object," +
//                        "which still should've been there.",
//                cache.getRepository().get(lastId));
//
//        assertEquals("First object should stay in cache forever", FIRST, cache.get(lastId - 1));
//        verifyCurrentLogCount(0);
//
//        MS_CodingUtils.sleep(100); //sleep a bit more until cleanup will be completed
//        assertNull("Performance issues: After additional waiting second object should've been already removed" +
//                        "from cache repository completely, but it doesn't",
//                cache.getRepository().get(lastId));
//        verifyCurrentLogCount(0); //hopefully, Concurrent modification exception will not arise here!
//    }
//
//    @Test
//    public void test14ClearingBunchOfObjectsHappensInSameThread() throws MS_ExecutionFailureException {
//        assertEquals(1, cache.getRepository().size()); //first element is still there
//        MS_List<MS_FutureEvent> cachingThreads = new MS_List<>();
//        operThread = cache.cache(RandomStringUtils.randomAlphabetic(10), ++lastId);
//        cachingThreads.add(operThread);
//        operThread = cache.cache(RandomStringUtils.randomAlphabetic(10), ++lastId, 0L);
//        cachingThreads.add(operThread);
//        operThread = cache.store(RandomStringUtils.randomAlphabetic(10), ++lastId);
//        cachingThreads.add(operThread);
//        operThread = cache.store(RandomStringUtils.randomAlphabetic(10), ++lastId, 54L);
//        cachingThreads.add(operThread);
//
//        MS_FutureEvent.joinEvents(cachingThreads, 5, 20);
//        assertEquals(5, cache.getRepository().size());
//        operThread = cache.removeAll();
//        MS_FutureEvent.joinEvents(5, 20, operThread);
//        assertEquals("After clearing cache there should not have been any object left",
//                0, cache.getRepository().size());
//        assertNull(cache.get(1));
//        assertNull(cache.get(lastId));
//        verifyCurrentLogCount(0);
//        lastId = 0; //restart ID counter, cause at this point all logs are cleared
//    }
//
//    @Test
//    public void test21UnsupportedActions() throws MS_ExecutionFailureException {
//        MS_Cache<String, Integer> cacheForUnsupportedActions = newCacheForUnsupportedActions();
//        cacheForUnsupportedActions.setLogger(logger);
//        verifyCurrentLogCount(0);
//
//        operThread = cacheForUnsupportedActions.store(FIRST, 1);
//        MS_FutureEvent.joinEvents(5, 20, operThread);
//        verifyCurrentLogCount(1);
//
//        cacheForUnsupportedActions.retrieve(1);
//        MS_CodingUtils.sleep(50);
//        verifyCurrentLogCount(2);
//
//        operThread = cacheForUnsupportedActions.clear();
//        MS_FutureEvent.joinEvents(5, 20, operThread);
//        verifyCurrentLogCount(3);
//        logs.getEventList().clear(); //restart logger, because in next steps we will need clean logs
//    }
//
//    @Test
//    public void test22repositoryDataExceptions() throws MS_ExecutionFailureException {
//        MS_Cache<String, Integer> cacheForUnsupportedActions = newCacheForRepositoryDataException();
//        cacheForUnsupportedActions.setLogger(logger);
//        verifyCurrentLogCount(0);
//
//        operThread = cacheForUnsupportedActions.store(FIRST, 1);
//        MS_FutureEvent.joinEvents(5, 20, operThread);
//        verifyCurrentLogCount(1);
//
//        cacheForUnsupportedActions.retrieve(1);
//        MS_CodingUtils.sleep(50);
//        verifyCurrentLogCount(2);
//
//        operThread = cacheForUnsupportedActions.clear();
//        MS_FutureEvent.joinEvents(5, 20, operThread);
//        verifyCurrentLogCount(3);
//        logs.getEventList().clear(); //restart logger, because in next steps we will need clean logs
//    }
//
//    @Test
//    public void test31ConcurrencyNonConflictingIds() throws MS_ExecutionFailureException {
//        MS_List<MS_FutureEvent> threads1_100 = newThreadListToStoreAndRetrieveObjects(1, 100);
//        MS_List<MS_FutureEvent> threads101_200 = newThreadListToStoreAndRetrieveObjects(101, 200);
//        MS_List<MS_FutureEvent> threads201_300 = newThreadListToStoreAndRetrieveObjects(201, 300);
//        MS_List<MS_FutureEvent> threads301_400 = newThreadListToStoreAndRetrieveObjects(301, 400);
//        MS_List<MS_FutureEvent> threads401_500 = newThreadListToStoreAndRetrieveObjects(401, 500);
//
//        MS_List<MS_FutureEvent> allThreadsTogether = new MS_List<>();
//        allThreadsTogether.concatenate(threads1_100);
//        allThreadsTogether.concatenate(threads101_200);
//        allThreadsTogether.concatenate(threads201_300);
//        allThreadsTogether.concatenate(threads301_400);
//        allThreadsTogether.concatenate(threads401_500);
//
//        //wait until last thread executes
//        MS_FutureEvent.joinEvents(allThreadsTogether, 25, 30);
//        verifyCurrentLogCount(0);
//        assertEquals(500, cache.getRepository().size());
//    }
//
//    @Test
//    public void test32ConcurrencyConflictingIds() throws MS_ExecutionFailureException {
//        cache.clear();
//        MS_List<MS_FutureEvent> threads1 = newThreadListToStoreAndRetrieveObjects(1, 100);
//        MS_List<MS_FutureEvent> threads2 = newThreadListToStoreAndRetrieveObjects(20, 120);
//        MS_List<MS_FutureEvent> threads3 = newThreadListToStoreAndRetrieveObjects(40, 140);
//        MS_List<MS_FutureEvent> threads4 = newThreadListToStoreAndRetrieveObjects(60, 160);
//        MS_List<MS_FutureEvent> threads5 = newThreadListToStoreAndRetrieveObjects(80, 180);
//        MS_List<MS_FutureEvent> threads6 = newThreadListToStoreAndRetrieveObjects(100, 200);
//
//        MS_List<MS_FutureEvent> allThreadsTogether = new MS_List<>();
//        allThreadsTogether.concatenate(threads1);
//        allThreadsTogether.concatenate(threads2);
//        allThreadsTogether.concatenate(threads3);
//        allThreadsTogether.concatenate(threads4);
//        allThreadsTogether.concatenate(threads5);
//        allThreadsTogether.concatenate(threads6);
//
//        //wait until last thread executes
//        MS_FutureEvent.joinEvents(allThreadsTogether, 25, 35);
//        verifyCurrentLogCount(0);
//        assertEquals(200, cache.getRepository().size());
//    }
}