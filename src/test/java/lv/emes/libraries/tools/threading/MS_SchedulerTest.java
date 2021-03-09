package lv.emes.libraries.tools.threading;

import lv.emes.libraries.utilities.MS_CodingUtils;
import lv.emes.libraries.utilities.MS_DateTimeUtils;
import lv.emes.libraries.utilities.MS_TestUtils;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MS_SchedulerTest {

    private final int DEFAULT_SLEEPING_TIME = 350; //fair enough time to run 2 (in future) scheduled events
    private ZonedDateTime timePresent;
    private ZonedDateTime timePast;
    private ZonedDateTime timeFuture;

    private int executionTimes = 0;
    private Exception occurredException;
    private ZonedDateTime timeCheck;

    @Before
    //Before every test do initial setup!
    public void setUpForEachTest() {
        timePresent = MS_DateTimeUtils.getCurrentDateTimeNow().plus(DEFAULT_SLEEPING_TIME / 2, ChronoUnit.MILLIS);
        timePast = timePresent.minusDays(1);
        timeFuture = timePresent.plus(DEFAULT_SLEEPING_TIME / 8, ChronoUnit.MILLIS);
    }

    @Test
    public void test01Schedule() {
        MS_Scheduler scheduler = new MS_Scheduler()
                .withTriggerTime(timePresent)
                .withTriggerTime(timePast)
                .withTriggerTime(timeFuture)
                .withAction((time) -> executionTimes++)
                .schedule();

        assertThat(scheduler.getScheduledEventCount()).isEqualTo(2);
        scheduler.waitFor(DEFAULT_SLEEPING_TIME);
        assertThat(executionTimes).isEqualTo(2);
    }

    @Test(expected = MS_TestUtils.MS_CheckedException.class)
    public void test02ActionOnException() throws Exception {
        MS_Scheduler scheduler = new MS_Scheduler()
                .withTriggerTime(timeFuture)
                .withAction((time) -> {
                    executionTimes++;
                    throw new MS_TestUtils.MS_CheckedException("Exception in scheduler's action");
                })
                .withActionOnException((e, time) -> occurredException = e)
                .schedule();

        assertThat(executionTimes).isEqualTo(0);
        scheduler.waitFor(DEFAULT_SLEEPING_TIME);
        assertThat(executionTimes).isEqualTo(1);
        assertThat(occurredException.getMessage()).isEqualTo("Exception in scheduler's action");
        throw occurredException;
    }

    @Test
    public void test03InterruptedException() {
        MS_Scheduler scheduler = new MS_Scheduler()
                .withTriggerTime(timeFuture = timeFuture.plusDays(1)) //to not to happen soon enough
                .withAction((time) -> executionTimes++)
                .withActionOnInterruptedException((time) -> timeCheck = time)
                .schedule();

        MS_CodingUtils.sleep(DEFAULT_SLEEPING_TIME); //let it initialize
        assertThat(executionTimes).isEqualTo(0);
        scheduler.terminate();
        MS_CodingUtils.sleep(DEFAULT_SLEEPING_TIME); //let it interrupt
        assertThat(timeCheck).as("Specific event didn't interrupt").isEqualTo(timeFuture);
    }

    @Test(expected = IllegalStateException.class)
    public void test11ScheduleTwice() {
        new MS_Scheduler()
                .withTriggerTime(timeFuture)
                .schedule()
                .schedule();
    }

    @Test(expected = IllegalStateException.class)
    public void test12ScheduleAndAddNewTriggerTimeAfterThat() {
        new MS_Scheduler()
                .withTriggerTime(timeFuture)
                .schedule()
                .withTriggerTime(timeFuture);
    }

    @Test
    public void test13TwoSchedulesAtTheSameTime() {
        MS_Scheduler scheduler = new MS_Scheduler()
                .withTriggerTime(timePresent) //first
                .withTriggerTime(timePast)
                .withTriggerTime(timeFuture)
                .withTriggerTime(timePresent) //second
                .withAction((time) -> executionTimes++)
                .withActionOnException((e, t) -> e.printStackTrace())
                .withActionOnInterruptedException((t) -> System.out.println("Interrupted."))
                .schedule();

        assertThat(scheduler.getScheduledEventCount()).isEqualTo(2);
        scheduler.waitFor(DEFAULT_SLEEPING_TIME);
        assertThat(executionTimes).isEqualTo(2);
    }

    @Test
    public void test21PrecisionTest() {
        MS_Scheduler scheduler = new MS_Scheduler()
                .withTriggerTime(timeFuture)
                .withAction((time) -> timeCheck = MS_DateTimeUtils.getCurrentDateTimeNow())
                .schedule();

        scheduler.waitFor(DEFAULT_SLEEPING_TIME);
        if (timeCheck == null) {
            throw new MS_TestUtils.MS_UnCheckedException1("Event didn't manage to execute in default sleeping time.");
        } else {
            long timeDifference = timeCheck.toInstant().toEpochMilli() - timeFuture.toInstant().toEpochMilli();
            assertThat(timeDifference <= 100).as(String.format("Execution time (%d milliseconds) slightly differs from configured",
                    timeDifference)).isTrue();
        }
    }
}
