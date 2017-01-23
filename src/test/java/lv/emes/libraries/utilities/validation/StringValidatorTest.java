package lv.emes.libraries.utilities.validation;

import lv.emes.libraries.tools.lists.MS_List;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static lv.emes.libraries.utilities.validation.ObjectValidator.*;
import static org.junit.Assert.*;

/**
 * @author eMeS
 * @version 1.0.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StringValidatorTest {

    private static StringValidator validator;
    private String textToValidate;
    private MS_List<ValidationError<String>> errors;

    @Before
    //Before even start testing do some preparations!
    public void initTestPreConditions() {
        validator = new StringValidator();
    }

    @Test
    public void test01NoValidationErrors() {
        textToValidate = "Normal text";
        assertFalse(validator.validate(textToValidate).errorsFound());
    }

    @Test
    public void test02NullString() {
        textToValidate = null;
        assertTrue(validator.validate(textToValidate).errorsFound());
        errors = validator.getValidationErrors();
        assertEquals(1, errors.count());
        assertEquals(VALIDATION_ERROR_NULL_OBJECT, errors.get(0).getNumber().intValue());
    }

    @Test
    public void test03EmptyString() {
        textToValidate = "";
        assertTrue(validator.validate(textToValidate).errorsFound());
        errors = validator.getValidationErrors();
        assertEquals(1, errors.count());
        assertEquals(VALIDATION_ERROR_EMPTY_VALUE, errors.get(0).getNumber().intValue());
    }

    @Test
    public void test04MinimumSizeViolated() {
        validator.setMinLength(5);
        validator.setMaxLength(10);
        textToValidate = "Min";
        assertTrue(validator.validate(textToValidate).errorsFound());
        errors = validator.getValidationErrors();
        assertEquals(2, errors.count());
        assertTrue(validator.containsError(VALIDATION_ERROR_MINIMUM_SIZE_VIOLATED));
        assertTrue(validator.containsError(VALIDATION_ERROR_SIZE_VIOLATED));
        assertFalse(validator.containsError(VALIDATION_ERROR_MAXIMUM_SIZE_VIOLATED));
    }

    @Test
    public void test05MaximumSizeViolated() {
        validator.setMinLength(5);
        validator.setMaxLength(10);
        textToValidate = "Maximum length of string is violated here.";
        assertTrue(validator.validate(textToValidate).errorsFound());
        errors = validator.getValidationErrors();
        assertEquals(2, errors.count());
        assertTrue(validator.containsError(VALIDATION_ERROR_MAXIMUM_SIZE_VIOLATED));
        assertTrue(validator.containsError(VALIDATION_ERROR_SIZE_VIOLATED));
        assertFalse(validator.containsError(VALIDATION_ERROR_MINIMUM_SIZE_VIOLATED));
    }

    @Test
    public void test06BothSizeContraintsViolated() {
        validator.setMinLength(10); //tricky, but possible
        validator.setMaxLength(5);
        textToValidate = "Six ch";
        assertTrue(validator.validate(textToValidate).errorsFound());
        errors = validator.getValidationErrors();
        assertEquals(3, errors.count());
        assertTrue(validator.containsError(VALIDATION_ERROR_MINIMUM_SIZE_VIOLATED));
        assertTrue(validator.containsError(VALIDATION_ERROR_MAXIMUM_SIZE_VIOLATED));
        assertTrue(validator.containsError(VALIDATION_ERROR_SIZE_VIOLATED));
    }

    @Test
    public void test07NoSizeContraintAtAll() {
        validator.setMinLength(null);
        validator.setMaxLength(null);
        textToValidate = "Six ch";
        assertFalse(validator.validate(textToValidate).errorsFound());
        errors = validator.getValidationErrors();
        assertEquals(0, errors.count());
        assertFalse(validator.containsError(VALIDATION_ERROR_MINIMUM_SIZE_VIOLATED));
        assertFalse(validator.containsError(VALIDATION_ERROR_MAXIMUM_SIZE_VIOLATED));
        assertFalse(validator.containsError(VALIDATION_ERROR_SIZE_VIOLATED));
    }
}
