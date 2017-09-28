package lv.emes.libraries.tools.threading;

/**
 * This functional interface is for any kind of error handling purposes.
 * Set it to define what happens if some error occurs.
 * @author eMeS
 */
@FunctionalInterface
public interface IFuncOnSomeException {
    void doOnError(Exception exception);
}
