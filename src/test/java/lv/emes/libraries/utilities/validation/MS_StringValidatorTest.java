package lv.emes.libraries.utilities.validation;

import lv.emes.libraries.tools.lists.MS_List;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static lv.emes.libraries.utilities.validation.MS_ObjectValidator.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author eMeS
 * @version 1.0.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MS_StringValidatorTest {

    private static MS_StringValidator validator;
    private String textToValidate;
    private MS_List<MS_ValidationError<String>> errors;

    @Before
    //Before even start testing do some preparations!
    public void initTestPreConditions() {
        validator = new MS_StringValidator();
    }

    @Test
    public void test01NoValidationErrors() {
        textToValidate = "Normal text";
        assertThat(validator.validate(textToValidate).errorsFound()).isFalse();
    }

    @Test
    public void test02NullString() {
        textToValidate = null;
        assertThat(validator.validate(textToValidate).errorsFound()).isTrue();
        errors = validator.getValidationErrors();
        assertThat(errors.count()).isEqualTo(1);
        assertThat(errors.get(0).getNumber().intValue()).isEqualTo(VALIDATION_ERROR_NULL_OBJECT);
    }

    @Test
    public void test03EmptyString() {
        textToValidate = "";
        assertThat(validator.validate(textToValidate).errorsFound()).isTrue();
        errors = validator.getValidationErrors();
        assertThat(errors.count()).isEqualTo(1);
        assertThat(errors.get(0).getNumber().intValue()).isEqualTo(VALIDATION_ERROR_EMPTY_VALUE);
    }

    @Test
    public void test04MinimumSizeViolated() {
        validator.setMinLength(5);
        validator.setMaxLength(10);
        textToValidate = "Min";
        assertThat(validator.validate(textToValidate).errorsFound()).isTrue();
        errors = validator.getValidationErrors();
        assertThat(errors.count()).isEqualTo(2);
        assertThat(validator.containsError(VALIDATION_ERROR_MINIMUM_SIZE_VIOLATED)).isTrue();
        assertThat(validator.containsError(VALIDATION_ERROR_SIZE_VIOLATED)).isTrue();
        assertThat(validator.containsError(VALIDATION_ERROR_MAXIMUM_SIZE_VIOLATED)).isFalse();
    }

    @Test
    public void test05MaximumSizeViolated() {
        validator.setMinLength(5);
        validator.setMaxLength(10);
        textToValidate = "Maximum length of string is violated here.";
        assertThat(validator.validate(textToValidate).errorsFound()).isTrue();
        errors = validator.getValidationErrors();
        assertThat(errors.count()).isEqualTo(2);
        assertThat(validator.containsError(VALIDATION_ERROR_MAXIMUM_SIZE_VIOLATED)).isTrue();
        assertThat(validator.containsError(VALIDATION_ERROR_SIZE_VIOLATED)).isTrue();
        assertThat(validator.containsError(VALIDATION_ERROR_MINIMUM_SIZE_VIOLATED)).isFalse();
    }

    @Test
    public void test06BothSizeContraintsViolated() {
        validator.setMinLength(10); //tricky, but possible
        validator.setMaxLength(5);
        textToValidate = "Six ch";
        assertThat(validator.validate(textToValidate).errorsFound()).isTrue();
        errors = validator.getValidationErrors();
        assertThat(errors.count()).isEqualTo(3);
        assertThat(validator.containsError(VALIDATION_ERROR_MINIMUM_SIZE_VIOLATED)).isTrue();
        assertThat(validator.containsError(VALIDATION_ERROR_MAXIMUM_SIZE_VIOLATED)).isTrue();
        assertThat(validator.containsError(VALIDATION_ERROR_SIZE_VIOLATED)).isTrue();
    }

    @Test
    public void test07NoSizeContraintAtAll() {
        validator.setMinLength(null);
        validator.setMaxLength(null);
        textToValidate = "Six ch";
        assertThat(validator.validate(textToValidate).errorsFound()).isFalse();
        errors = validator.getValidationErrors();
        assertThat(errors.count()).isEqualTo(0);
        assertThat(validator.containsError(VALIDATION_ERROR_MINIMUM_SIZE_VIOLATED)).isFalse();
        assertThat(validator.containsError(VALIDATION_ERROR_MAXIMUM_SIZE_VIOLATED)).isFalse();
        assertThat(validator.containsError(VALIDATION_ERROR_SIZE_VIOLATED)).isFalse();
    }
}
