package lv.emes.libraries.tools.threading;

import lv.emes.libraries.tools.MS_IBuilder;
import lv.emes.libraries.tools.lists.MS_List;

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
 * @version 1.1.
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
    private IFuncOnSomeException onRuntimeException = null;

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
        interrupted = false;
        workCompleted = false;
        started = true;
        thread = new Thread(this, threadName);
        thread.start();
        return getThis();
    }

    /**
     * Makes current thread (the one, which called this method) wait for execution of this thread.
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
            interrupted = true; //this actually should never happen, because by design it happens on run method
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
     *
     * @throws InterruptedException if some method in <b>doOnExecution</b> method throws an InterruptedException
     *                              it can be send directly to this thread's handler to make thread's state as interrupted without throwing
     *                              RuntimeException.
     * @throws RuntimeException     if some actions in this method throws some unchecked exception, it is handled as
     *                              <b>onRuntimeException</b> event.
     * @see MS_Thread#withActionOnRuntimeException(IFuncOnSomeException)
     */
    protected abstract void doOnExecution() throws InterruptedException, RuntimeException;

    /**
     * Invoked on thread's run method:
     * <ul>
     * <li>after successful execution;</li>
     * <li>after execution that was interrupted;</li>
     * <li>but NOT in case if <b>doOnExecution</b> thrown RuntimeException.</li>
     * </ul>
     * <p>Useful for internal actions that checks, if work is completed and / or if thread's work is interrupted.
     * Override only if necessary!
     */
    protected void doAfterExecution() {
    }

    @Override
    public final void run() {
        try {
            doOnExecution();
            started = false;
            workCompleted = true;
        } catch (InterruptedException ie) {
            workCompleted = false;
            interrupted = true;
            started = false;
        } catch (RuntimeException e) { //any other exception that wasn't handled in doOnExecution method
            workCompleted = false;
            interrupted = true;
            started = false;
            if (onRuntimeException != null)
                onRuntimeException.doOnError(e);
        }
        doAfterExecution();
    }

    //Setters (builder pattern) and Getters

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
     * @param action an preferable lambda expression defining actions to be performed when method
     *               <b>doOnExecution</b> raises some unchecked exception.
     * @return reference to a thread itself.
     */
    public T withActionOnRuntimeException(IFuncOnSomeException action) {
        this.onRuntimeException = action;
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

    /**
     * @param name name of threads that we are looking for (there can be running many threads with same name).
     * @return all the threads by given name.
     */
    public static MS_List<Thread> getThreadsByName(String name) {
        MS_List<Thread> res = new MS_List<>();
        Thread.getAllStackTraces().forEach((thread, traceEl) -> {
            if (name.equals(thread.getName())) res.add(thread);
        });
        return res;
    }
}
