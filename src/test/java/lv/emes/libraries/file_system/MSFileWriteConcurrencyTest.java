package lv.emes.libraries.file_system;

import lv.emes.libraries.tools.lists.MS_List;
import lv.emes.libraries.tools.threading.MS_FutureEvent;
import lv.emes.libraries.utilities.MS_CodingUtils;
import org.junit.*;
import org.junit.runners.MethodSorters;

import static lv.emes.libraries.file_system.MS_FileSystemTools.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MSFileWriteConcurrencyTest {

    private final static String FILE_NAME = getTmpDirectory() + "MSFileWriteConcurrencyTest/MSFileWriteConcurrencyTest.log";
    private final static int THREAD_COUNT = 20;
    private final static int WAITING_TIMEOUT = 2000;
    private final static int MAX_WAITING_TIMES = 4; //after double timeout all threads must finish their work for sure

    private static MS_List<MS_Logger> loggersForTest = new MS_List<>();
    private MS_List<MS_FutureEvent> threads = new MS_List<>();
    private static Boolean exceptionOnThreadLevel = false;
    private static Boolean interruptedExceptionOnThreadLevel = false;
    private int waitedXTimesForThreadsToFinish;
    private int threadsFinishedTheirWorkCount;

    @BeforeClass
    public static void initiate() {
        for (int i = 0; i < THREAD_COUNT; i++) {
            loggersForTest.add(new MS_Logger(FILE_NAME));
        }
    }

    @AfterClass
    public static void cleanUp() {
        assertTrue(deleteDirectory(directoryUp(FILE_NAME)));
        assertFalse(exceptionOnThreadLevel);
        assertFalse(interruptedExceptionOnThreadLevel);
    }

    @Before
    public void setup() {
        waitedXTimesForThreadsToFinish = 0;
        threadsFinishedTheirWorkCount = 0;
        for (int i = 0; i < THREAD_COUNT; i++) {
            threads.add(
                    new MS_FutureEvent()
                            .withThreadName("MSFileWriteConcurrencyTest_thread_" + (i + 1))
                            .withTimeout(WAITING_TIMEOUT)
                            .withActionOnException(exception -> {
                                exception.printStackTrace();
                                exceptionOnThreadLevel = true;
                            })
                            .withActionOnInterruptedException(() -> {
                                interruptedExceptionOnThreadLevel = true;
                            })
            );
        }
    }

    @Test
    public void test01LogInfoSimultaneously() {
        threads.forEachItem((thread, i) -> {
            thread.withAction(() -> {
                        loggersForTest.get(i).info("Test thread with name " + thread.getThreadName() + " started");
                        loggersForTest.get(i).warning("Current thread number: " + (i+1));
                        loggersForTest.get(i).error("If there had an error, it would look like this: ", new Exception("Some fake error."));
                        loggersForTest.get(i).line();
                    }
            ).schedule();
        });

        while (waitedXTimesForThreadsToFinish < MAX_WAITING_TIMES && threadsFinishedTheirWorkCount < THREAD_COUNT) {
            threadsFinishedTheirWorkCount = 0;
            threads.forEachItem((thread, i) -> {
                if (thread.isFinished()) threadsFinishedTheirWorkCount++;
            });
            MS_CodingUtils.sleep(WAITING_TIMEOUT / 2);
        }
    }
}