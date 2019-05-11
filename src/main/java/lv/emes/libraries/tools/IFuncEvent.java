package lv.emes.libraries.tools;

/**
 * This functional interface is for some event execution.
 * Set it to define behavior of some event.
 *
 * @author eMeS
 * @version 1.0.
 * @see IFuncAction
 */
@FunctionalInterface
public interface IFuncEvent {

    void execute();
}
