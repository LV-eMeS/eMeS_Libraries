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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author eMeS
 * @version 1.0.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MS_SchedulerTest {

    private final int DEFAULT_SLEEPING_TIME = 300; //fair enough time to run 2 (in future) scheduled events
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

        assertEquals(2, scheduler.getScheduledEventCount());
        scheduler.waitFor(DEFAULT_SLEEPING_TIME);
        assertEquals(2, executionTimes);
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

        assertEquals(0, executionTimes);
        scheduler.waitFor(DEFAULT_SLEEPING_TIME);
        assertEquals(1, executionTimes);
        assertEquals("Exception in scheduler's action", occurredException.getMessage());
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
        assertEquals(0, executionTimes);
        scheduler.terminate();
        MS_CodingUtils.sleep(DEFAULT_SLEEPING_TIME); //let it interrupt
        assertEquals("Specific event didn't interrupt", timeFuture, timeCheck);
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

        assertEquals(2, scheduler.getScheduledEventCount());
        scheduler.waitFor(DEFAULT_SLEEPING_TIME);
        assertEquals(2, executionTimes);
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
            assertTrue(String.format("Execution time (%d milliseconds) slightly differs from configured",
                    timeDifference), timeDifference <= 100);
        }
    }
}
