package lv.emes.libraries.utilities.validation;

/**
 * An error that occur in validation process.
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_ValidationErrorImpl<T> implements MS_ValidationError<T> {
    private Integer number;
    private T object;
    private FuncFormValidationErrorMessage actionToFormatMessage;

    public MS_ValidationErrorImpl(Integer number) {
        if (number == null)
            number = 0;
        this.number = number;
    }

    public T getObject() {
        return object;
    }

    public MS_ValidationError setObject(T object) {
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

    public MS_ValidationError setErrorMessageForming(FuncFormValidationErrorMessage action) {
        actionToFormatMessage = action;
        return this;
    }
}
