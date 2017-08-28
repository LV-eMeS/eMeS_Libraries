package lv.emes.libraries.tools.threading;

import lv.emes.libraries.tools.MS_IBuilder;

/**
 * A class that combines together Runnable and thread.
 * It's main purpose is reduce amount of code needed to create simple thread task.
 * It also has builder pattern based structure to set necessary information for thread to work.
 * It should be extended and necessary abstract methods should be implemented.
 * <p>Public methods:
 * <ul>
 * <li>start</li>
 * <li>waitFor</li>
 * <li>stop</li>
 * </ul>
 * <p>Methods to implement:
 * <ul>
 * <li>doOnExecution</li>
 * <li>getThis</li>
 * </ul>
 * <p>Setters and getters:
 * <ul>
 * <li>withThreadName</li>
 * <li>withTimeout</li>
 * <li>getThreadName</li>
 * <li>getTimeout</li>
 * <li>isInterrupted</li>
 * <li>isWorkCompleted</li>
 * <li>isStarted</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 */
public abstract class MS_Thread<T extends MS_Thread<T>> implements Runnable, MS_IBuilder<T> {

    private static final String THREAD_IS_NOT_STARTED = "Cannot perform operation - thread didn't started execution yet.";
    private static final String THREAD_IS_STARTED = "Cannot perform operation - thread is already started.";

    private Thread thread;
    private String threadName = "MS_Thread";
    private long timeout = 0L;
    private boolean interrupted = false;
    private boolean workCompleted = false;
    private boolean started = false;

    /**
     * Starts execution of thread by calling inner thread object's method <b>run</b>,
     * which will then call method <b>doOnExecution</b>.
     *
     * @return reference to a thread itself.
     * @throws IllegalStateException when attempt is made to start thread that is already started.
     */
    public final T start() {
        if (started)
            throw new IllegalStateException(THREAD_IS_STARTED);
        thread = new Thread(this, threadName);
        thread.start();
        interrupted = false;
        workCompleted = false;
        started = true;
        return getThis();
    }

    /**
     * Makes current thread wait for execution of this thread.
     * If <b>timeout</b> is set and is greater than 0 then waiting will be performed
     * for specified time in milliseconds, after that thread will be interrupted if it will not finish in that time.
     *
     * @return reference to a thread itself.
     * @throws IllegalStateException when attempt is made to wait for thread that hasn't even started.
     */
    public final T waitFor() throws IllegalStateException {
        if (!started)
            throw new IllegalStateException(THREAD_IS_NOT_STARTED);
        try {
            thread.join(timeout);
            interrupted = !workCompleted;
        } catch (InterruptedException e) {
            interrupted = true;
        }
        started = false;
        return getThis();
    }

    /**
     * Stops thread's work. Thread is interrupted and it's state is changed to interrupted.
     *
     * @return reference to a thread itself.
     * @throws IllegalStateException when attempt is made to stop thread that hasn't even started.
     */
    public final T stop() {
        if (!started)
            throw new IllegalStateException(THREAD_IS_NOT_STARTED);
        thread.interrupt();
        interrupted = true;
        workCompleted = false;
        started = false;
        return getThis();
    }

    /**
     * All the actions that needs to be performed while thread is running.
     * When this method will finish it's work without throwing an exception the status
     * will be changed to "isWorkCompleted".
     */
    protected abstract void doOnExecution();

    @Override
    public final void run() {
        try {
            doOnExecution();
            started = false;
            workCompleted = true;
        } catch (Exception e) {
            interrupted = true;
            started = false;
            throw new RuntimeException(e);
        }
    }

    /**
     * @return defined thread name or "MS_Thread" if name is not set.
     */
    public String getThreadName() {
        return threadName;
    }

    /**
     * @return amount of time in milliseconds which will will be waited for thread to finish.
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * Changes name of thread.
     *
     * @param threadName new name of thread.
     * @return reference to a thread itself.
     */
    public T withThreadName(String threadName) {
        this.threadName = threadName;
        return getThis();
    }

    /**
     * Sets timeout for execution of thread's work.
     *
     * @param timeout time in milliseconds.
     * @return reference to a thread itself.
     */
    public T withTimeout(long timeout) {
        this.timeout = timeout;
        return getThis();
    }

    /**
     * Flag that thread has been started but was interrupted by an another thread.
     *
     * @return true if <b>stop()</b> has been called or another thread interrupted this one.
     */
    public boolean isInterrupted() {
        return interrupted;
    }

    /**
     * Flag that thread is completed execution successfully.
     *
     * @return true if <b>start()</b> has been called
     * and thread managed to complete <b>doOnExecution</b> method without throwing an exception.
     */
    public boolean isWorkCompleted() {
        return workCompleted;
    }

    /**
     * Flag that thread is started already.
     *
     * @return true if <b>start()</b> has been called.
     */
    public boolean isStarted() {
        return started;
    }
}
