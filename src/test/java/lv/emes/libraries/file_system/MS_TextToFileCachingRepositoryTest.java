package lv.emes.libraries.file_system;

import lv.emes.libraries.storage.MS_Cache;
import lv.emes.libraries.storage.MS_RepositoryDataExchangeException;
import lv.emes.libraries.testdata.TestData;
import lv.emes.libraries.tools.lists.MS_List;
import lv.emes.libraries.tools.logging.MS_InMemoryLoggingRepository;
import lv.emes.libraries.tools.logging.MS_MultiLogger;
import lv.emes.libraries.tools.logging.MS_MultiLoggingSetup;
import lv.emes.libraries.tools.threading.MS_FutureEvent;
import lv.emes.libraries.utilities.MS_CodingUtils;
import lv.emes.libraries.utilities.MS_ExecutionFailureException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

/**
 * These tests are partly integration tests with {@link lv.emes.libraries.storage.MS_Cache} class.
 *
 * @author eMeS
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MS_TextToFileCachingRepositoryTest {

    private static final String PROJECT_NAME = TestData.TEMP_DIR + "eMeS_Libraries/";
    private static final String REPOSITORY_ROOT = PROJECT_NAME + "MS_TextToFileCachingRepositoryTest";
    private static final String FIRST_FILE = "first_test_file.txt";
    private static final String FIRST = "First object will stay there until it will be removed manually";
    private static final String SECOND = "Second object will become expired after 1 second";
    private static final String THIRD = "Third object ## HAS some deli#miter characters inside & used, but it should still be ok.";
    private static final String THIRD_AFTER_PUT = "Object content changed with put method";
    private static final int TTL = 1000;

    private static MS_Cache<String, String> cache;
    private static MS_MultiLogger logger;
    private static MS_InMemoryLoggingRepository logs;
    private static Map<Integer, String> idStorage; //to keep IDs for stored objects and retrieve them by Integer ID
    private static MS_TextToFileCachingRepository fileRepository;

    private MS_FutureEvent operThread;

    @BeforeClass
    public static void init() {
        idStorage = new ConcurrentHashMap<>();
        for (int i = 1; i <= 500; i++)
            idStorage.put(i, String.valueOf(i));

        fileRepository = new MS_TextToFileCachingRepository(
                REPOSITORY_ROOT, FIRST_FILE, true);
        logs = new MS_InMemoryLoggingRepository();
        logger = new MS_MultiLogger(new MS_MultiLoggingSetup().withRepository(logs));
        cache = new MS_Cache<>(fileRepository);
        cache.setDefaultTTL(TTL / 1000);
        cache.setLogger(logger);
    }

    @AfterClass
    public static void finalizeTestConditions() {
        assertTrue("Cleanup failed because folder cannot be deleted ATM",
                MS_FileSystemTools.deleteDirectory(PROJECT_NAME));
    }

    @Test
    public void test01CheckSetParams() {
        assertEquals(TTL / 1000, cache.getDefaultTTL());
        assertEquals(logger, cache.getLogger());
        assertTrue("Repository is not initialized, but it should've been on init() step",
                cache.getRepository().isInitialized());
    }

    @Test
    public void test11StoreAndRetrieveFirst() throws MS_ExecutionFailureException {
        operThread = cache.cache(FIRST, idStorage.get(1), 0L);
        MS_FutureEvent.joinEvents(TTL, 10, operThread);
        assertEquals("After first object caching there should be 1 object cached",
                1, cache.getRepository().size());

        assertEquals(FIRST, cache.get(idStorage.get(1)));
        verifyThatCurrentLogCountIs0();
    }

    @Test
    public void test12StoreSecondAndBothAreStillThere() throws MS_ExecutionFailureException {
        operThread = cache.cache(SECOND, idStorage.get(2));
        MS_FutureEvent.joinEvents(TTL / 10, 10, operThread);
        assertEquals(2, cache.getRepository().size());

        verifyThatCurrentLogCountIs0();
        assertEquals("Second object suddenly disappeared from repository", SECOND, cache.get(idStorage.get(2)));
        assertEquals("First object is not removed manually, so it still should've been there",
                FIRST, cache.get(idStorage.get(1)));
    }

    @Test
    public void test13CleanupAfter1SecondSecondObjectIsGoneBut1stRemains() {
        assertNotNull("Performance issues: Second object shouldn't be expired yet", cache.get(idStorage.get(2)));
        MS_CodingUtils.sleep(TTL * 2);
        assertNull("Second object is still in cache after 2 seconds, which was its TTL. ", cache.get(idStorage.get(2)));
        //if this call will be fast enough we might still find object in real cache
        assertNotNull("Performance issues: As expired object cleanup takes some time " +
                        "while new thread is created, actual repository should've been returned second object," +
                        "which still should've been there.",
                cache.getRepository().get(idStorage.get(2)));

        assertEquals("First object should stay in cache forever", FIRST, cache.get(idStorage.get(1)));
        verifyThatCurrentLogCountIs0();

        verifyThatCurrentLogCountIs0(); //hopefully, Concurrent modification exception will not arise here!
    }

    @Test
    public void test14ClearingBunchOfObjectsHappensInSameThread() throws MS_ExecutionFailureException {
        MS_List<MS_FutureEvent> cachingThreads = new MS_List<>();
        operThread = cache.cache(RandomStringUtils.randomAlphabetic(10), idStorage.get(141));
        cachingThreads.add(operThread);
        operThread = cache.cache(RandomStringUtils.randomAlphabetic(10), idStorage.get(142), 0L);
        cachingThreads.add(operThread);
        operThread = cache.store(RandomStringUtils.randomAlphabetic(10), idStorage.get(143));
        cachingThreads.add(operThread);
        operThread = cache.store(RandomStringUtils.randomAlphabetic(10), idStorage.get(144), 54L);
        cachingThreads.add(operThread);

        MS_FutureEvent.joinEvents(cachingThreads, TTL / 200, 10);
        assertEquals("After successful caching and waiting for threads to finish there should be number of objects in cache",
                5, cache.getRepository().size());
        operThread = cache.removeAll();
        MS_FutureEvent.joinEvents(TTL / 200, 10, operThread);
        assertEquals("After clearing cache there should not have been any object left",
                0, cache.getRepository().size());
        assertNull(cache.get(idStorage.get(1)));
        assertNull(cache.get(idStorage.get(144)));
        verifyThatCurrentLogCountIs0();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test21UnsupportedActionDoAdd() {
        fileRepository.doAdd(null, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test22UnsupportedActionDoFind() {
        fileRepository.doFind(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test23UnsupportedActionDoremove() {
        fileRepository.doRemove(null);
    }

    @Test
    public void test41AddAndPutComparison() {
        fileRepository.add(idStorage.get(401), Pair.of(THIRD, LocalDateTime.now().plusHours(1)));
        assertEquals(THIRD, fileRepository.get(idStorage.get(401)).getLeft());
        fileRepository.put(idStorage.get(401), Pair.of(THIRD_AFTER_PUT, LocalDateTime.now().plusHours(1)));
        assertEquals(THIRD_AFTER_PUT, fileRepository.get(idStorage.get(401)).getLeft());
        //test that add will not overwrite existing object
        fileRepository.add(idStorage.get(401), Pair.of(THIRD, LocalDateTime.now().plusHours(1)));
        assertEquals("Calling 'add' on existing ID should not do a thing",
                THIRD_AFTER_PUT, fileRepository.get(idStorage.get(401)).getLeft());
    }

    @Test(expected = MS_RepositoryDataExchangeException.class)
    public void test999repositoryDataExceptionWhenFileCorrupted() {
        MS_TextFile fileCorrupter = new MS_TextFile(REPOSITORY_ROOT + MS_FileSystemTools._SLASH + FIRST_FILE);
        fileCorrupter.writeln("Some really bad file content", true);
        fileRepository.findAll();
    }

    //*** Private methods ***

    private void verifyThatCurrentLogCountIs0() {
        assertEquals("Error and warning log count differs from expected at this point",
                0, logs.getEventList().size());
    }
}