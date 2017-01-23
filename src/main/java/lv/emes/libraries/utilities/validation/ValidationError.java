package lv.emes.libraries.utilities.validation;

/**
 * Actions for error that occur in validation process.
 *
 * @author eMeS
 * @version 1.0.
 */
public interface ValidationError<T> {
    ValidationError setErrorMessageForming(FuncFormValidationErrorMessage action);

    /**
     * Returns message of validation error using pre-defined method to form message.
     * @return formatted message describing essence of this particular validation error.
     */
    String getMessage();
    Integer getNumber();

    T getObject();
    ValidationError setObject(T object);

    static <T> ValidationError newInstance(Integer number) {
        return new ValidationErrorImpl<T>(number);
    }
}
