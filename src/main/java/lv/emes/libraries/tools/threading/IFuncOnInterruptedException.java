package lv.emes.libraries.tools.threading;

/**
 * This functional interface is for thread interruption error handling purposes.
 * Set it to define what happens if you run a thread, but then it's interrupted before it finished execution.
 * @author eMeS
 */
//@FunctionalInterface
public interface IFuncOnInterruptedException {
    void doOnError(InterruptedException exception);
}
