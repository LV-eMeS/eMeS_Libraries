package lv.emes.libraries.tools;

/**
 * This functional interface is for some event execution.
 * Set it to define behavior of some event.
 * @author eMeS
 * @see IFuncAction
 */
@FunctionalInterface
public interface IFuncEvent {

    void execute();
}
