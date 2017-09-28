package lv.emes.libraries.tools.threading;

import lv.emes.libraries.tools.IFuncEvent;

/**
 * This functional interface is meant for thread interruption error handling purposes.
 * Set it to define what happens if you run a thread, but then it's interrupted before it finished execution.
 * @author eMeS
 */
public interface IFuncOnInterruptedException extends IFuncEvent {
}
