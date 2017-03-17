package lv.emes.libraries.communication.db;

/**
 * This functional interface is for database exception handling purposes.
 * @author eMeS
 */
@FunctionalInterface
public interface IFuncOnNullPointerException {
	void doOnError(NullPointerException exception);
}