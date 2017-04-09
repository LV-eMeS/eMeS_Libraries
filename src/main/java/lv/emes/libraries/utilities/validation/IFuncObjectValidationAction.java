package lv.emes.libraries.utilities.validation;

import lv.emes.libraries.tools.lists.MS_List;

/**
 * Use this to define behavior of validator for particular type <b>T</b> of object to validate.
 *
 * @author eMeS
 * @version 1.0.
 */
@FunctionalInterface
public interface IFuncObjectValidationAction<T> {
    void doValidationProcess(T objectToValidate, MS_List<MS_ValidationError<T>> validationErrorList);
}
