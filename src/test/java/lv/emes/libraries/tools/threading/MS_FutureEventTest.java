package lv.emes.libraries.tools.threading;

import lv.emes.libraries.utilities.MS_CodingUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.rmi.activation.ActivationException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author eMeS
 * @version 1.0.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MS_FutureEventTest {

    private static final int DEFAULT_SLEEP_TIME = 10;
    private boolean threadExecuted = false;

    @Test
    public void test01Execute() {
        new MS_FutureEvent()
                .withThreadName("MS_FutureEventTest")
                .withAction(() -> {
                    threadExecuted = true;
                })
                .schedule();
        MS_CodingUtils.sleep(DEFAULT_SLEEP_TIME); //it takes some time for event thread to execute
        assertTrue(threadExecuted);
    }

    @Test
    public void test02DidntExecuteInTime() {
        new MS_FutureEvent()
                .withAction(() -> {
                    MS_CodingUtils.sleep(DEFAULT_SLEEP_TIME * 2);
                    threadExecuted = true;
                })
                .schedule();
        MS_CodingUtils.sleep(DEFAULT_SLEEP_TIME);
        assertFalse(threadExecuted);
    }

    @Test
    public void test03DidntEvenStartedExecution() {
        new MS_FutureEvent()
                .withAction(() -> {
                    threadExecuted = true;
                })
                .withTimeTillExecution(DEFAULT_SLEEP_TIME * 2)
                .schedule();
        MS_CodingUtils.sleep(DEFAULT_SLEEP_TIME);
        assertFalse(threadExecuted);
    }

    @Test(expected = ActivationException.class)
    public void test04ExecuteWithException() throws Exception {
        //setting some random exception as initial value, and this should be changes in withActionOnException method
        AtomicReference<Exception> expectedException = new AtomicReference<>(new ClassCastException());
        new MS_FutureEvent()
                .withThreadName("MS_FutureEventTest")
                .withAction(() -> {
                    throw new ActivationException();
                })
                .withActionOnException(expectedException::set)
                .schedule();
        MS_CodingUtils.sleep(DEFAULT_SLEEP_TIME * 3);
        throw expectedException.get();
    }

    @Test
    public void test05WithTimeout() {
        //this event should execute for 20 milliseconds, but will be timeouted even before
        new MS_FutureEvent()
                .withAction(() -> {
                    MS_CodingUtils.sleep(DEFAULT_SLEEP_TIME * 2);
                    threadExecuted = true;
                })
                .withActionOnInterruptedException(
                        () -> threadExecuted = false)
                .withTimeout(DEFAULT_SLEEP_TIME)
                .schedule();
        MS_CodingUtils.sleep(DEFAULT_SLEEP_TIME * 3); //lets give much more time for this event to try to execute!
        assertFalse(threadExecuted);
    }

    @Test
    public void test06Terminate() {
        //this event should execute for 20 milliseconds, but will be terminated before its started
        threadExecuted = true; //to be sure that flag is changed by test logic
        new MS_FutureEvent()
                .withAction(() -> {
                    MS_CodingUtils.sleep(DEFAULT_SLEEP_TIME * 2);
                    threadExecuted = true;
                })
                .withActionOnInterruptedException(
                        () -> threadExecuted = false)
                .withTimeout(DEFAULT_SLEEP_TIME)
                .schedule()
                .terminate();
        MS_CodingUtils.sleep(DEFAULT_SLEEP_TIME * 3); //lets give much more time for this event to try to execute!
        assertFalse(threadExecuted);
    }
}
