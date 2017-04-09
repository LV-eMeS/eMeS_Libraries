package lv.emes.libraries.utilities.validation;

import lv.emes.libraries.tools.lists.MS_List;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Designed to validate some object possible values.
 * <p>Public methods:
 * <ul>
 * <li>validate</li>
 * <li>errorsFound</li>
 * <li>object</li>
 * <li>errorsFound</li>
 * <li>getValidationErrors</li>
 * <li>containsError</li>
 * <li>onValidation</li>
 * </ul>
 * <p>Methods to override:
 * <ul>
 * <li>doValidation</li>
 * <li>addNullPointerValidationError</li>
 * <li>initAllPossibleValidationErrors</li>
 * </ul>
 * <p>Methods to use in successors:
 * <ul>
 * <li>initNewError</li>
 * <li>addErrorToList</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 */
public abstract class MS_ObjectValidator<T> {
    public static final int VALIDATION_ERROR_NULL_OBJECT = 0;
    public static final int VALIDATION_ERROR_EMPTY_VALUE = 1;
    public static final int VALIDATION_ERROR_SIZE_VIOLATED = 2;
    public static final int VALIDATION_ERROR_MINIMUM_SIZE_VIOLATED = 3;
    public static final int VALIDATION_ERROR_MAXIMUM_SIZE_VIOLATED = 4;
    public static final int VALIDATION_ERROR_WRONG_PATTERN = 5;

    private T fObject;
    private boolean isValidated = false;
    private MS_List<MS_ValidationError<T>> possibleValidationErrors = new MS_List<>();
    private MS_List<MS_ValidationError<T>> validationErrors = new MS_List<>();
    private FuncObjectValidationAction<T> fOnValidation;

    /**
     * This constructor is mandatory because all possible validation errors must be initialized here.
     */
    public MS_ObjectValidator() {
        initAllPossibleValidationErrors();
    }

    /**
     * Method that does all the validation stuff.
     *
     * @param objectToValidate    object that has to be validated.
     * @param validationErrorList list of validation errors occurred in validation process.
     *                            Add new validation errors as do the validation!
     */
    protected abstract void doValidation(T objectToValidate, MS_List<MS_ValidationError<T>> validationErrorList);

    /**
     * Override this method to add different validation error when object is null or to disable this operation at all.
     */
    protected void addNullPointerValidationError() {
        validationErrors.add(new MS_ValidationErrorImpl<>(VALIDATION_ERROR_NULL_OBJECT));
    }

    /**
     * Validates if the object matches predefined constraints.
     *
     * @param object object to be validated.
     * @return referenece to validator itself.
     */
    public final MS_ObjectValidator validate(T object) {
        this.fObject = object;
        isValidated = true;
        //also check automatically for null pointer
        if (object == null)
            addNullPointerValidationError();
        else
            doValidation(object, validationErrors);

        if (fOnValidation != null)
            fOnValidation.doValidationProcess(object, validationErrors);
        return this;
    }

    /**
     * Sets object to be validated.
     *
     * @param object object to be validated
     * @return referenece to validator itself.
     */
    public MS_ObjectValidator object(T object) {
        this.fObject = object;
        return this;
    }

    /**
     * Checks whether current object to validate is valid.
     * <br><u>Note</u>: if no object is initialized then this method will still return true because object to validate is null and
     * by default (if <b>addNullPointerValidationError</b> is not touched) null objects are invalid.
     *
     * @return true if it is valid, false, if there are some validation errors.
     */
    public Boolean errorsFound() {
        if (!isValidated)
            validate(fObject);
        return validationErrors.count() > 0;
    }

    /**
     * Checks whether given object <b>object</b> is valid.
     *
     * @param object object to be validated
     * @return true if it is valid, false, if there are some validation errors.
     */
    public boolean errorsFound(T object) {
        this.fObject = object;
        return errorsFound();
    }

    /**
     * After <b>validate</b> returns all the errors for current object.
     * @return validation error collection.
     */
    public MS_List<MS_ValidationError<T>> getValidationErrors() {
        return validationErrors;
    }

    /**
     * Checks whether presented validation error with number exists in validator's error list.
     * @param errorNumber number of error we are looking for.
     * @return true if error found, else otherwise.
     */
    public boolean containsError(Integer errorNumber) {
        final AtomicReference<Boolean> errorFound = new AtomicReference<>(false);
        validationErrors.forEachItem((error, index) -> {
            if (error.getNumber().equals(errorNumber)) {
                errorFound.set(true);
                validationErrors.breakDoWithEveryItem();
            }
        });
        return errorFound.get();
    }

    /**
     * Checks whether presented validation error exists in validator's error list.
     * Both number and message of validation error are checked for perfect match.
     * @param error validation error we are looking for.
     * @return true if error found, else otherwise.
     */
    public boolean containsError(MS_ValidationError error) {
        if (error == null)
            return false;

        final AtomicReference<Boolean> errorFound = new AtomicReference<>(false);
        validationErrors.forEachItem((err, index) -> {
            if (err.getNumber().equals(error.getNumber()) && err.getMessage().equals(error.getMessage())) {
                errorFound.set(true);
                validationErrors.breakDoWithEveryItem();
            }
        });
        return errorFound.get();
    }

//    /**
//     * Clears the list of validation errors created by objects.
//     * @return
//     */
//    public MS_ObjectValidator clearErrors() {
//        this.validationErrors.clear();
//        return this;
//    }

    /**
     * Set additional validation methods to do when <b>validate</b> method is called.
     * @param validate actions that checks validation object for errors and
     *                 appends validation error list with particular error number for
     *                 error that occurs in that particular case.
     * @return reference to validator itself.
     */
    public final MS_ObjectValidator onValidation(FuncObjectValidationAction<T> validate) {
        fOnValidation = validate;
        return this;
    }

    /**
     * Override this to init all possible validation errors for this class.
     * This is point where to set all errors and custom error messages for those errors.
     * It is mandatory to use <b>initNewError</b> method here, because only that method
     * will create new error correctly and append possible error list with it.
     * <br><u>Note</u>: do not use <b>super</b> to use implementation of parent
     * class unless you need to init exactly all the errors with same numbers and messages as parent class!
     */
    protected abstract void initAllPossibleValidationErrors();

    /**
     * Creates and initializes new validation error and appends possible error list with it.
     * @param number error number.
     * @return new validation error.
     */
    @SuppressWarnings("unchecked")
    protected final MS_ValidationError initNewError(Integer number) {
        MS_ValidationError anError = new MS_ValidationErrorImpl<T>(number);
        possibleValidationErrors.add(anError);
        return anError;
    }

    /**
     * Adds error from possible validation error list to regular validation error list.
     * This method should be called while overriding <b>doValidation</b> method to point that in some condition
     * this particular error is met.
     * @param number number of error.
     * @param objectToValidate object that is going to be validated.
     */
    protected final void addErrorToList(Integer number, T objectToValidate) {
        possibleValidationErrors.forEachItem((possibleError, i) -> {
            if (possibleError.getNumber().equals(number)) {
                this.validationErrors.add(possibleError);
                possibleError.setObject(objectToValidate);
                possibleValidationErrors.breakDoWithEveryItem();
            }
        });
    }
}
