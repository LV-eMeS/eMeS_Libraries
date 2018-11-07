package lv.emes.libraries.communication.http;

import lv.emes.libraries.tools.MS_BadSetupException;
import lv.emes.libraries.utilities.MS_ExecutionFailureException;
import org.junit.Test;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MS_PollingTest {

    private static final Integer INT_FINISHED = 376006;
    private static final Integer INT_PENDING = 37600606;
    private static final String STR_PENDING = "PENDING";
    private static final String STR_FINISHED = "DONE";

    @Test
    public void testSuccessfulPollScenario() throws MS_ExecutionFailureException {
        AtomicReference<String> status = new AtomicReference<>(STR_PENDING);
        startThread(status, 600L);
        AtomicInteger attempts = new AtomicInteger(0);

        String res = new MS_Polling<String>()
                //will run for MAX of [10 * 100`000 microseconds] = [10 * 100 milliseconds] = [1 second]
                .withMaxPollingAttempts(10).withSleepInterval(100L, ChronoUnit.MILLIS)
                .withAction(status::get)
                .withCheck(STR_FINISHED::equals)
                .withActionBeforeEachRetry(p -> attempts.incrementAndGet()) //optional part (used only to maintain attempt count)
                .poll();

        assertThat(res).isEqualTo(STR_FINISHED);
        // attempt count in which poll got desired result depends on computer performance (might be
        // faster, as thread is started before poll creation), but it should be around 600 / 100 = 6
        assertThat(attempts.get()).isLessThanOrEqualTo(9);
    }

    @Test
    public void testExpiredPollScenario() {
        AtomicReference<String> status = new AtomicReference<>(STR_PENDING);
        startThread(status, 1000L);

        assertThatThrownBy(() -> {
            new MS_Polling<String>()
                    .withMaxPollingAttempts(2).withSleepInterval(100L)
                    .withAction(status::get)
                    .withCheck(STR_FINISHED::equals)
                    .poll();
        }).isInstanceOf(MS_ExecutionFailureException.class);
    }

    @Test
    public void testGetters() throws MS_ExecutionFailureException {
        AtomicInteger attempt = new AtomicInteger(0);
        MS_Polling<Integer> poll = new MS_Polling<Integer>().withMaxPollingAttempts(3).withSleepInterval(4L);

        assertThat(poll.getMaxPollingAttempts()).isEqualTo(3);
        assertThat(poll.getAttemptsLeft()).isEqualTo(3);
        assertThat(poll.getSleepInterval()).isEqualTo(4L);
        assertThat(poll.getTimeSlept()).isEqualTo(0L);
        assertThat(poll.getMaximumPollingTime()).isEqualTo(12L);
        assertThat(poll.getCurrentAttemptNumber()).isEqualTo(attempt.get());
        assertThat(poll.getResult()).isNull();

        poll
                .withAction(() -> {
                    if (attempt.get() == 1) {
                        assertThat(poll.getAttemptsLeft()).isEqualTo(3); //not changed until this attempt is done
                        assertThat(poll.getTimeSlept()).isEqualTo(0L);
                        assertThat(poll.getCurrentAttemptNumber()).isEqualTo(attempt.get());
                        return INT_PENDING;
                    } else {
                        //after successful poll
                        assertThat(poll.getAttemptsLeft()).isEqualTo(2);
                        assertThat(poll.getTimeSlept()).isEqualTo(4L);
                        assertThat(poll.getCurrentAttemptNumber()).isEqualTo(attempt.get());
                        return INT_FINISHED;
                    }
                })
                .withCheck(INT_FINISHED::equals)
                .withActionBeforeEachRetry(p -> attempt.incrementAndGet())
                .poll()
        ;

        assertThat(poll.getMaxPollingAttempts()).isEqualTo(3);
        assertThat(poll.getAttemptsLeft()).isEqualTo(1); //after success there still is 1 potential running attempt left
        assertThat(poll.getSleepInterval()).isEqualTo(4L); //nothing changed here
        assertThat(poll.getTimeSlept()).isEqualTo(4L);
        assertThat(poll.getMaximumPollingTime()).isEqualTo(12L); //nothing changed here
        assertThat(poll.getCurrentAttemptNumber()).isEqualTo(attempt.get());
        assertThat(poll.getResult()).isEqualTo(INT_FINISHED);
    }

    @Test
    public void testAssertionErrorWhilePolling() {
        assertThatThrownBy(() -> {
            new MS_Polling<Integer>().withMaxPollingAttempts(2)
                    .withAction(() -> {
                        assertThat(true)
                                .withFailMessage("Imitating action failure due to an assertion error")
                                .isFalse();
                        return 9; //return something, but we won't get there anyways
                    })
                    .withCheck(Predicate.isEqual(INT_FINISHED))
                    .poll();
        })
                .isInstanceOf(AssertionError.class);
    }

    @Test
    public void testExceptionWhilePolling() {
        assertThatThrownBy(() -> {
            new MS_Polling<Integer>().withMaxPollingAttempts(2)
                    .withAction(() -> {
                        throw new MS_BadSetupException("Imitating action failure due to an bad use of methods inside executable action");
                    })
                    .withCheck(Predicate.isEqual(INT_FINISHED))
                    .poll();
        })
                .isInstanceOf(MS_ExecutionFailureException.class)
                .hasCauseExactlyInstanceOf(MS_BadSetupException.class);
    }

    private void startThread(AtomicReference<String> status, long timeTillStatusChange) {
        //Prepare and run thread that will eventually change status value to "DONE"
        Runnable action = () -> {
            try {
                Thread.sleep(timeTillStatusChange);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            status.set(STR_FINISHED);
        };
        Thread thread = new Thread(action);
        thread.start();
    }
}
