package lv.emes.libraries.utilities.validation;

/**
 * Actions for error that occur in validation process.
 *
 * @author eMeS
 * @version 1.2.
 */
public interface MS_ValidationError<T> {

    MS_ValidationError withErrorMessageFormingAction(IFuncFormValidationErrorMessage action);

    /**
     * Returns message of validation error using pre-defined method to form message.
     * @return formatted message describing essence of this particular validation error.
     */
    String getMessage();
    Integer getNumber();

    T getObject();

    /**
     * @param object an object to validate.
     * @return reference to validation error itself.
     */
    MS_ValidationError withObject(T object);
}
