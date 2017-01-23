package lv.emes.libraries.utilities.validation;

/**
 * An error that occur in validation process.
 *
 * @author eMeS
 * @version 1.0.
 */
public class ValidationErrorImpl<T> implements ValidationError<T> {
    private Integer number;
    private T object;
    private FuncFormValidationErrorMessage actionToFormatMessage;

    public ValidationErrorImpl(Integer number) {
        if (number == null)
            number = 0;
        this.number = number;
    }

    public T getObject() {
        return object;
    }

    public ValidationError setObject(T object) {
        this.object = object;
        return this;
    }

    @Override
    public Integer getNumber() {
        return number;
    }

    @Override
    public String getMessage() {
        return actionToFormatMessage != null ? actionToFormatMessage.formMessage() : "";
    }

    public ValidationError setErrorMessageForming(FuncFormValidationErrorMessage action) {
        actionToFormatMessage = action;
        return this;
    }
}
