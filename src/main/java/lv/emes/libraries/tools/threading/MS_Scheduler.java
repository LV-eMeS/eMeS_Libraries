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
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
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
     * As waiting is implemented in looping, checking and sleeping manner, sleep interval <b>sleepInterval</b>
     * should be indicated in order to define precision of waiting loops (there always will be some delay;
     * the question is, how long).
     *
     * @param sleepInterval sleeping interval between checker loop cycles.
     * @throws IllegalStateException if scheduler haven't started yet.
     */
    public void waitFor(long sleepInterval) throws IllegalStateException {
        if (!scheduled)
            throw new IllegalStateException(SCHEDULER_IS_STARTED);
        while (getScheduledEventCount() > 0) {
            MS_CodingUtils.sleep(sleepInterval);
            clearOldEvents();
        }
        scheduled = false; //when its all done
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

    //PRIVATE METHODS
    private void filterSchedulesForFutureOnly(Map<ZonedDateTime, MS_FutureEvent> schedules) {
//        //sort schedules from past to future
//        schedules.sort((timeCurrent, timeInList) -> {
//            if (timeCurrent.isBefore(timeInList))
//                return -1;
//            else if (timeCurrent.isAfter(timeInList))
//                return 1;
//            else
//                return 0;
//        });

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
