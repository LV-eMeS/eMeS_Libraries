package lv.emes.libraries.tools.threading;

import lv.emes.libraries.tools.IFuncAction;
import lv.emes.libraries.tools.lists.MS_List;
import lv.emes.libraries.tools.lists.MS_StringList;
import lv.emes.libraries.utilities.MS_CodingUtils;
import lv.emes.libraries.utilities.MS_ExecutionFailureException;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * An event in new thread that is going to happen after some short time measured in milliseconds.
 * Most common use cases are user actions or operation with GUI that has to be done in some
 * known time.
 * <p>Class is made so that it could be easily used like builder and there is no need to create
 * local variable for that.
 * Just create new instance, use setters to configure time till execution and action on execution!
 * Optionally actions on exceptions can be set to define, what will happen if exception occurs
 * during event execution.
 * <p>Setters and getters:
 * <ul>
 * <li>withTimeTillExecution</li>
 * <li>withTimeout</li>
 * <li>withAction</li>
 * <li>withActionOnInterruptedException</li>
 * <li>withActionOnException</li>
 * <li>isFinished</li>
 * <li>isInterrupted</li>
 * <li>getThreadName</li>
 * </ul>
 * <p>Public methods:
 * <ul>
 * <li>schedule</li>
 * <li>terminate</li>
 * </ul>
 * <p>Static methods:
 * <ul>
 * <li>joinEvents</li>
 * <li>waitUntilOneFinished</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.2.
 */
public class MS_FutureEvent {

    private final WorkerThread worker = new WorkerThread().withThreadName("MS_FutureEvent");

    /**
     * Sets time till execution (default is 0 meaning that event will execute instantly).
     *
     * @param milliseconds non-negative value defining amount of time till the execution of
     *                     defined actions.
     * @return reference to an event itself.
     */
    public MS_FutureEvent withTimeTillExecution(long milliseconds) {
        worker.executeAfter = milliseconds;
        return this;
    }

    /**
     * Sets timeout vale for execution of event's action.
     * Default is set to '0', which means that caller thread will wait as long as it will take for this event
     * to finish its job.
     *
     * @param timeout non-negative value defining maximal amount of time that
     *                execution should take before it will be interrupted.
     * @return reference to an event itself.
     */
    public MS_FutureEvent withTimeout(long timeout) {
        worker.withTimeout(timeout);
        return this;
    }

    /**
     * Sets actions that has to be performed on this event.
     *
     * @param action an preferable lambda expression defining actions to be performed on execution.
     * @return reference to an event itself.
     */
    public MS_FutureEvent withAction(IFuncAction action) {
        worker.actionOnExecution = action;
        return this;
    }

    /**
     * Sets action to be performed when future event thread is interrupted by timeout or another thread.
     *
     * @param action an preferable lambda expression defining actions to be performed on
     *               interrupted thread exception.
     * @return reference to an event itself.
     */
    public MS_FutureEvent withActionOnInterruptedException(IFuncOnInterruptedException action) {
        worker.onInterruption = action;
        return this;
    }

    /**
     * @param action an preferable lambda expression defining actions to be performed on
     *               exception while executing event's action.
     * @return reference to an event itself.
     */
    public MS_FutureEvent withActionOnException(IFuncOnSomeException action) {
        worker.actionOnException = action;
        return this;
    }

    /**
     * @param name new name of the thread.
     * @return reference to an event itself.
     */
    public MS_FutureEvent withThreadName(String name) {
        worker.withThreadName(name);
        return this;
    }

    /**
     * @return defined thread name or "MS_FutureEvent" if name is not set.
     */
    public String getThreadName() {
        return worker.getThreadName();
    }

    /**
     * @return true if event happened, e.g. thread's work is completed or it's interrupted.
     */
    public boolean isFinished() {
        return worker.isWorkCompleted() || worker.isInterrupted();
    }

    /**
     * @return true if event has been stopped or its thread has been interrupted.
     */
    public boolean isInterrupted() {
        return worker.isInterrupted();
    }

    /**
     * Creates new thread and starts time countdown till event execution then executes event.
     *
     * @return reference to an event itself.
     */
    public MS_FutureEvent schedule() {
        if (worker.getTimeout() > 0) {
            new InterrupterThread(worker)
                    .withTimeout(worker.getTimeout()) //time till thread interruption
                    .start();
        }
        worker.start();
        return this;
    }

    /**
     * Stops event if its not started yet.
     * Due to this termination InterruptedException may arise.
     *
     * @return reference to an event itself.
     */
    public MS_FutureEvent terminate() {
        worker.stop();
        return this;
    }

    //*** Static methods ***

    /**
     * Forces current thread to wait until all of events completes their jobs or gets interrupted.
     *
     * @param sleepInterval     sleeping interval (in milliseconds) between checker loop cycles.
     * @param maxIterationCount maximum count of iterations to perform.
     *                          If this number is reached then {@link MS_ExecutionFailureException} is thrown.
     * @param events            collection of events.
     * @throws MS_ExecutionFailureException if failed to join given <b>events</b> in given time
     *                                      <b>sleepInterval</b> * <b>maxIterationCount</b>.
     */
    public static void joinEvents(long sleepInterval, int maxIterationCount, MS_FutureEvent... events)
            throws MS_ExecutionFailureException {

        joinEvents(MS_List.newInstance(events), sleepInterval, maxIterationCount);
    }

    /**
     * Forces current thread to wait until all of events completes their jobs or gets interrupted.
     *
     * @param sleepInterval     sleeping interval (in milliseconds) between checker loop cycles.
     * @param maxIterationCount maximum count of iterations to perform.
     *                          If this number is reached then {@link MS_ExecutionFailureException} is thrown.
     * @param events            collection of events.
     * @throws MS_ExecutionFailureException if failed to join given <b>events</b> in given time
     *                                      <b>sleepInterval</b> * <b>maxIterationCount</b>.
     */
    public static void joinEvents(MS_List<MS_FutureEvent> events, long sleepInterval, int maxIterationCount)
            throws MS_ExecutionFailureException {

        if (maxIterationCount > 0)
            joinEvents(sleepInterval, maxIterationCount, events);
    }

    /**
     * Forces current thread to wait until at least one of events completes its job.
     *
     * @param sleepInterval     sleeping interval between checker loop cycles.
     * @param maxIterationCount maximum count of iterations to perform.
     *                          If this number is reached then {@link MS_ExecutionFailureException} is thrown.
     * @param stopOthers        flag to stop others when one event finished its job.
     * @param events            collection of events.
     * @throws MS_ExecutionFailureException if failed to join given <b>events</b> in given time
     *                                      <b>sleepInterval</b> * <b>maxIterationCount</b>.
     */
    public static void waitUntilOneFinished(long sleepInterval, int maxIterationCount, boolean stopOthers, MS_FutureEvent... events)
            throws MS_ExecutionFailureException {

        waitUntilOneFinished(MS_List.newInstance(events), sleepInterval, maxIterationCount, stopOthers);
    }

    /**
     * Forces current thread to wait until at least one of events completes its job.
     *
     * @param sleepInterval     sleeping interval between checker loop cycles.
     * @param maxIterationCount maximum count of iterations to perform.
     *                          If this number is reached then {@link MS_ExecutionFailureException} is thrown.
     * @param stopOthers        flag to stop others when one event finished its job.
     * @param events            collection of events.
     * @throws MS_ExecutionFailureException if failed to join given <b>events</b> in given time
     *                                      <b>sleepInterval</b> * <b>maxIterationCount</b>.
     *                                      <p><u>Warning</u>: in this case all events are continuing their work,
     *                                      as it is not intended to stop them. If it's necessary to stop them,
     *                                      it can be done in catch block when {@link MS_ExecutionFailureException}
     *                                      is caught.
     */
    public static void waitUntilOneFinished(MS_List<MS_FutureEvent> events, long sleepInterval, int maxIterationCount, boolean stopOthers)
            throws MS_ExecutionFailureException {

        if (maxIterationCount > 0)
            waitUntilOneFinished(sleepInterval, maxIterationCount, stopOthers, events);
    }

    //*** Private methods and classes ***

    private static void joinEvents(long sleepInterval, int maxIterationCount, MS_List<MS_FutureEvent> events)
            throws MS_ExecutionFailureException {

        events.removeIf(event -> event == null || event.isFinished());
        if (events.size() > 0) {
            MS_CodingUtils.sleep(sleepInterval);
            if (maxIterationCount == 0) {
                MS_StringList notJoinedEventThreadNames = new MS_StringList(',');
                events.forEach((event) -> notJoinedEventThreadNames.add(event.getThreadName()));
                throw new MS_ExecutionFailureException("Failed to join given events [" +
                        notJoinedEventThreadNames.toStringWithNoLastDelimiter() + "] in given time");
            }
            joinEvents(sleepInterval, maxIterationCount - 1, events);
        }
    }

    private static void waitUntilOneFinished(long sleepInterval, int maxIterationCount, boolean stopOthers, MS_List<MS_FutureEvent> events)
            throws MS_ExecutionFailureException {

        if (events.size() > 0) {
            MS_CodingUtils.sleep(sleepInterval);
            AtomicInteger finishedEventIndex = new AtomicInteger(-1);
            events.forEachItem((event, i) -> {
                if (event.isFinished()) {
                    events.breakOngoingForLoop();
                    finishedEventIndex.set(i);
                }
            });

            if (finishedEventIndex.get() != -1) {
                if (stopOthers) {
                    events.remove(finishedEventIndex.get());
                    events.forEach(MS_FutureEvent::terminate);
                }
                return;
            }

            if (maxIterationCount == 0) {
                MS_StringList notJoinedEventThreadNames = new MS_StringList(',');
                events.forEach((event) -> notJoinedEventThreadNames.add(event.getThreadName()));
                throw new MS_ExecutionFailureException("Following events [" +
                        notJoinedEventThreadNames.toStringWithNoLastDelimiter() + "] didn't manage to finish in given time");
            }
            waitUntilOneFinished(sleepInterval, maxIterationCount - 1, stopOthers, events);
        }
    }

    private static class WorkerThread extends MS_Thread<WorkerThread> {

        private long executeAfter = 0;
        private IFuncAction actionOnExecution = null;
        private IFuncOnSomeException actionOnException = null;
        IFuncOnInterruptedException onInterruption = null;

        @Override
        protected void doOnExecution() throws InterruptedException {
            Thread.sleep(executeAfter);
            // essence of this kind of event is to run actual action only after sleep
            if (actionOnExecution != null) {
                try {
                    actionOnExecution.execute();
                } catch (Exception e) {
                    if (actionOnException != null)
                        actionOnException.doOnError(e);
                }
            }
        }

        @Override
        protected void doAfterExecution() {
            if (this.isInterrupted()) {
                if (onInterruption != null)
                    onInterruption.execute();
            }
        }

        @Override
        public WorkerThread getThis() {
            return this;
        }
    }

    private static class InterrupterThread extends MS_Thread<InterrupterThread> {

        private WorkerThread threadToInterrupt;

        InterrupterThread(WorkerThread threadToInterrupt) {
            this.threadToInterrupt = threadToInterrupt;
        }

        @Override
        protected void doOnExecution() throws InterruptedException {
            Thread.sleep(this.getTimeout());
            //when time ends do interruption if thread is still active
            if (threadToInterrupt.isStarted() && !threadToInterrupt.isWorkCompleted()) {
                threadToInterrupt.stop();
            }
        }

        @Override
        public InterrupterThread getThis() {
            return this;
        }
    }
}
