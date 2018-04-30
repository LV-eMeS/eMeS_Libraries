package lv.emes.libraries.tools.threading;

import lv.emes.libraries.utilities.MS_CodingUtils;
import lv.emes.libraries.utilities.MS_DateTimeUtils;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.TreeMap;

/**
 * An mechanism that allows to do actions in some period of time in future.
 * Precision of this scheduler is designed to be roughly till 100 milliseconds of delay (depends on workstation power),
 * which should be precise enough for human task simulation.
 * <p>Public methods:
 * <ul>
 * <li>schedule</li>
 * <li>terminate</li>
 * <li>waitFor</li>
 * </ul>
 * <p>Setters and getters:
 * <ul>
 * <li>withAction</li>
 * <li>withActionOnException</li>
 * <li>withActionOnInterruptedException</li>
 * <li>withTriggerTime</li>
 * <li>getScheduledEventCount</li>
 * <li>getSchedulerStartTime</li>
 * </ul>
 * <p><u>Examples</u>:
 * <p><b>Example 1</b>: simple scheduler with 2 events scheduled - first after 5 minutes, second - after 2 hours.
 * <p><code>
 * //keep reference of scheduler in case you are going to terminate it later on<br>
 * //and this is the easiest way, how to re-use scheduler start time inside lambda expressions<br>
 * MS_Scheduler scheduler = new MS_Scheduler();<br>
 * scheduler<br>
 * .withTriggerTime(ZonedDateTime.now().plusMinutes(5))<br>
 * .withTriggerTime(ZonedDateTime.now().plusHours(2))<br>
 * .withAction((time) -&gt; {<br>
 * if (time.isAfter(scheduler.getSchedulerStartTime().plusMinutes(30)))<br>
 * throw new Exception("Exception in scheduler's action");<br>
 * System.out.println("Successfully executed 1st event at time: " + MS_DateTimeUtils.formatDateTime(time));<br>
 * })<br>
 * .withActionOnException((e, time) -&gt; {<br>
 * System.out.println("Error while executing 2nd event at time: " + MS_DateTimeUtils.formatDateTime(time));<br>
 * e.printStackTrace();<br>
 * })<br>
 * .schedule();<br>
 * scheduler.waitFor(); //this will freeze app for ~2 hours
 * </code>
 * <p><b>Example 2</b>: never ending scheduler, which executes every 10 minutes.
 * Constructions must be created inside some class.
 * <p><code>
 * private MS_Scheduler cleaningJobScheduler;<br><br>
 * private void scheduleJob() {<br>
 * cleaningJobScheduler = new MS_Scheduler()<br>
 * .withTriggerTime(ZonedDateTime.now().plusMinutes(10))<br>
 * .withAction(this::runActualJobAndRescheduleIt)<br>
 * .withActionOnException((exception, eventExecutionTime) -&gt; {<br>
 * MS_Log4Java.getLogger("scheduleJob")<br>
 * .error(String.format("Job failed at [%s] due to exception", eventExecutionTime), exception);<br>
 * scheduleJob(); //to not to break scheduler if some error happens on execution<br>
 * })<br>
 * .withActionOnInterruptedException((eventExecutionTime) -&gt; {<br>
 * MS_Log4Java.getLogger("scheduleJob")<br>
 * .info(String.format("Job terminated at [%s]", eventExecutionTime));<br>
 * })<br>
 * .schedule();<br>
 * }<br><br>
 * private void runActualJobAndRescheduleIt(ZonedDateTime execTime) {<br>
 * System.out.println("Some action happened now and will happen after next 10 minutes");<br>
 * scheduleCleanupJob(); //at the end create new job to not to brake this endless loop<br>
 * }<br>
 * </code>
 *
 * @author eMeS
 * @version 1.1.
 */
public class MS_Scheduler {

    private static final String SCHEDULER_IS_STARTED = "Cannot perform operation - all events already scheduled.";

    private IFuncScheduledEvent action;
    private IFuncOnSomeSchedulerException onException;
    private IFuncOnScheduledEventInterruptedException onInterruption;
    private Map<ZonedDateTime, MS_FutureEvent> schedules = new TreeMap<>();
    private ZonedDateTime timeSchedulerStarted;
    private boolean scheduled = false;

    /**
     * Creates new thread and starts time countdown till event execution then executes event.
     *
     * @return reference to a scheduler itself.
     */
    public MS_Scheduler schedule() {
        if (scheduled)
            throw new IllegalStateException(SCHEDULER_IS_STARTED);
        timeSchedulerStarted = MS_DateTimeUtils.getCurrentDateTimeNow();
        filterSchedulesForFutureOnly(schedules);
        doScheduleAllTheEvents(schedules);
        scheduled = true;
        return this;
    }

    /**
     * Method in case there is need for scheduler stopping or restart to run it with different configurations.
     * Basically it just stops all the schedules and clears all the data for schedules.
     * <p><u>Note</u>: even schedules, that were not started to execute yet, are interrupted,
     * which means that action on interruption will be triggered.
     *
     * @return reference to a scheduler itself.
     */
    public MS_Scheduler terminate() {
        schedules.forEach((time, event) -> event.terminate());
        schedules.clear();
        scheduled = false;
        return this;
    }

    //Getters and Setters

    /**
     * Sets actions that has to be performed when scheduler is triggered.
     *
     * @param action an preferable lambda expression defining actions to be performed on execution.
     * @return reference to a scheduler itself.
     */
    public MS_Scheduler withAction(IFuncScheduledEvent action) {
        this.action = action;
        return this;
    }

    /**
     * @param action an preferable lambda expression defining actions to be performed on
     *               exception while executing event's action.
     * @return reference to a scheduler itself.
     */
    public MS_Scheduler withActionOnException(IFuncOnSomeSchedulerException action) {
        this.onException = action;
        return this;
    }

    /**
     * @param action an preferable lambda expression defining actions to be performed when event is interrupted.
     * @return reference to an event itself.
     */
    public MS_Scheduler withActionOnInterruptedException(IFuncOnScheduledEventInterruptedException action) {
        this.onInterruption = action;
        return this;
    }

    /**
     * Adds new trigger to trigger list.
     *
     * @param time date and time when event should be triggered.
     * @return reference to a scheduler itself.
     */
    public MS_Scheduler withTriggerTime(ZonedDateTime time) {
        if (scheduled)
            throw new IllegalStateException(SCHEDULER_IS_STARTED);
        if (time != null)
            schedules.put(time, new MS_FutureEvent());
        return this;
    }

    /**
     * Forces caller thread to wait for all scheduler's tasks to execute.
     * <p><u>Warning</u>: this is not a good idea to force caller thread to wait longer than few seconds, so if
     * there are tasks scheduled, that will take more than this time, that is strongly recommended not to wait for!
     * <p>As waiting is implemented in looping, checking and sleeping manner, sleep interval <b>sleepInterval</b>
     * should be indicated in order to define precision of waiting loops (there always will be some delay;
     * the question is, how long).
     *
     * @param sleepInterval sleeping interval between checker loop cycles.
     * @throws IllegalStateException if scheduler haven't started yet.
     */
    public void waitFor(long sleepInterval) throws IllegalStateException {
        if (scheduled) { //only if it's still scheduled
            while (getScheduledEventCount() > 0) {
                MS_CodingUtils.sleep(sleepInterval);
                clearOldEvents();
            }
            scheduled = false; //when its all done
        }
    }

    /**
     * Forces caller thread to wait for all scheduler's tasks to execute.
     * Waiting is implemented in looping, checking and sleeping manner.
     * Sleep interval between checker loop cycles is set to 1 second.
     *
     * @throws IllegalStateException if scheduler haven't started yet.
     */
    public void waitFor() throws IllegalStateException {
        waitFor(1000);
    }

    /**
     * @return count of events that are scheduled, but are not executed yet.
     */
    public synchronized int getScheduledEventCount() {
        return schedules.size();
    }

    public ZonedDateTime getSchedulerStartTime() {
        return timeSchedulerStarted;
    }

    //PRIVATE METHODS
    private void filterSchedulesForFutureOnly(Map<ZonedDateTime, MS_FutureEvent> schedules) {
        //delete ones that are in past already
        schedules.entrySet().removeIf(entry -> entry.getKey().isBefore(timeSchedulerStarted));
    }

    private void doScheduleAllTheEvents(Map<ZonedDateTime, MS_FutureEvent> schedules) {
        schedules.forEach(
                (runTime, event) -> event
                        .withThreadName("MS_Scheduler")
                        .withTimeTillExecution(runTime.toInstant().toEpochMilli() - timeSchedulerStarted.toInstant().toEpochMilli())
                        .withAction(() -> {
                            schedules.remove(runTime);
                            if (action != null)
                                action.execute(runTime);
                        })
                        .withActionOnException((exc) -> {
                            schedules.remove(runTime);
                            if (onException != null)
                                onException.doOnError(exc, runTime);
                        })
                        .withActionOnInterruptedException(() -> {
                            schedules.remove(runTime);
                            if (onInterruption != null)
                                onInterruption.doOnError(runTime);
                        })
                        .schedule()
        );
    }

    private void clearOldEvents() {
        schedules.entrySet().removeIf(entry -> entry.getKey().isBefore(MS_DateTimeUtils.getCurrentDateTimeNow()));
    }
}
