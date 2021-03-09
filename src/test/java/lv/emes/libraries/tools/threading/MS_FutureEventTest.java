package lv.emes.libraries.tools.threading;

import lv.emes.libraries.tools.lists.MS_List;
import lv.emes.libraries.utilities.MS_CodingUtils;
import lv.emes.libraries.utilities.MS_ExecutionFailureException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.rmi.activation.ActivationException;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(threadExecuted).isTrue();
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
        assertThat(threadExecuted).isFalse();
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
        assertThat(threadExecuted).isFalse();
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
        assertThat(threadExecuted).isFalse();
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
        assertThat(threadExecuted).isFalse();
    }

    @Test
    public void test21WaitUntilOneFinishedWithStopping() throws MS_ExecutionFailureException {
        threadExecuted = false;
        MS_FutureEvent longLastingEvent = new MS_FutureEvent()
                .withThreadName("Test MS_FutureEvent longLastingEvent")
                .withAction(() -> {
                    MS_CodingUtils.sleep(DEFAULT_SLEEP_TIME * 2000);
                    threadExecuted = true;
                })
                .schedule();

        MS_FutureEvent normalEvent = new MS_FutureEvent()
                .withThreadName("Test MS_FutureEvent normalEvent")
                .withAction(() -> {
                    MS_CodingUtils.sleep(DEFAULT_SLEEP_TIME);
                    threadExecuted = true;
                })
                .schedule();

        MS_FutureEvent.waitUntilOneFinished(DEFAULT_SLEEP_TIME / 2, 5, true,
                longLastingEvent, normalEvent);
        assertThat(threadExecuted).as("After waiting thread should've been executed").isTrue();
        assertThat(normalEvent.isFinished()).as("After waiting normalEvent should've been finished its work").isTrue();
        assertThat(longLastingEvent.isFinished()).as("longLastingEvent should've been stopped at this point").isTrue();
    }

    @Test
    public void test22WaitUntilOneFinishedWithoutStopping() throws MS_ExecutionFailureException {
        threadExecuted = false;
        MS_FutureEvent longLastingEvent = new MS_FutureEvent()
                .withThreadName("Test MS_FutureEvent longLastingEvent")
                .withAction(() -> {
                    MS_CodingUtils.sleep(DEFAULT_SLEEP_TIME * 2000);
                    threadExecuted = true;
                })
                .schedule();

        MS_FutureEvent normalEvent = new MS_FutureEvent()
                .withThreadName("Test MS_FutureEvent normalEvent")
                .withAction(() -> {
                    MS_CodingUtils.sleep(DEFAULT_SLEEP_TIME);
                    threadExecuted = true;
                })
                .schedule();

        MS_FutureEvent.waitUntilOneFinished(DEFAULT_SLEEP_TIME, 10, false,
                longLastingEvent, normalEvent);
        assertThat(threadExecuted).as("After waiting thread should've been executed").isTrue();
        assertThat(normalEvent.isFinished()).as("After waiting normalEvent should've been finished its work").isTrue();
        assertThat(longLastingEvent.isFinished()).as("longLastingEvent should've been stopped at this point").isFalse();
        longLastingEvent.terminate();
    }

    @Test(expected = MS_ExecutionFailureException.class)
    public void test23WaitUntilOneFinishedWithoutStopping() throws MS_ExecutionFailureException {
        MS_FutureEvent longLastingEvent = new MS_FutureEvent()
                .withAction(() -> {
                    MS_CodingUtils.sleep(DEFAULT_SLEEP_TIME * 20);
                })
                .schedule();

        MS_FutureEvent normalEvent = new MS_FutureEvent()
                .withAction(() -> {
                    MS_CodingUtils.sleep(DEFAULT_SLEEP_TIME * 20);
                })
                .schedule();

        MS_FutureEvent.waitUntilOneFinished(DEFAULT_SLEEP_TIME, 1, false,
                longLastingEvent, normalEvent);
    }

    @Test
    public void test31GetThreadByFutureEventName() {
        String threadName = "test31GetThreadByFutureEventName";
        MS_FutureEvent event = new MS_FutureEvent()
                .withThreadName(threadName)
                .withAction(() -> MS_CodingUtils.sleep(DEFAULT_SLEEP_TIME * 20))
                .schedule();

        MS_List<Thread> threads = MS_Thread.getThreadsByName(threadName);
        assertThat(threads.size()).isEqualTo(1);
        Thread eventThread = threads.get(0);
        assertThat(eventThread.isAlive()).isTrue();
        assertThat(eventThread.getName()).isEqualTo(event.getThreadName());
    }
}
