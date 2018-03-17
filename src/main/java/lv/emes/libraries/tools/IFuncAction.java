package lv.emes.libraries.tools;

/**
 * Action to do.
 * In case of exceptions while performing this action, exception is thrown.
 *
 * @author eMeS
 * @version 1.0.
 * @see IFuncEvent
 */
@FunctionalInterface
public interface IFuncAction {

    void execute() throws Exception;
}