package lv.emes.libraries.utilities.validation;

import lv.emes.libraries.tools.lists.MS_List;

/**
 * Validates String objects for size and fulfillment.
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_StringValidator extends MS_ObjectValidator<String> {
    private Integer minLength;
    private Integer maxLength;

    @Override
    protected void doValidation(String objectToValidate, MS_List<MS_ValidationError<String>> validationErrorList) {
        if (objectToValidate.equals(""))
            addErrorToList(VALIDATION_ERROR_EMPTY_VALUE, objectToValidate);

        //size contraints
        boolean sizeViolated = false;
        if (minLength != null && objectToValidate.length() < minLength) {
            addErrorToList(VALIDATION_ERROR_MINIMUM_SIZE_VIOLATED, objectToValidate);
            sizeViolated = true;
        }
        if (maxLength != null && objectToValidate.length() > maxLength) {
            addErrorToList(VALIDATION_ERROR_MAXIMUM_SIZE_VIOLATED, objectToValidate);
            sizeViolated = true;
        }
        if (sizeViolated)
            addErrorToList(VALIDATION_ERROR_SIZE_VIOLATED, objectToValidate);
    }

    @Override
    protected void initAllPossibleValidationErrors() {
        initNewError(VALIDATION_ERROR_EMPTY_VALUE)
//                .withErrorMessageFormingAction(() -> "some message")
        ;
        initNewError(VALIDATION_ERROR_MINIMUM_SIZE_VIOLATED);
        initNewError(VALIDATION_ERROR_MAXIMUM_SIZE_VIOLATED);
        initNewError(VALIDATION_ERROR_SIZE_VIOLATED);
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }
}
