package lv.emes.libraries.utilities.validation;

import lv.emes.libraries.tools.MS_StringTools;
import lv.emes.libraries.tools.lists.MS_List;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

/**
 * @author eMeS
 * @version 1.0.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MSObjectValidatorTest {

    private static MS_ObjectValidator<Integer> intValidator;
    private static MS_ObjectValidator<String> stringValidator;
    private static final String ERROR1 = "Too small value";
    private static final String ERROR2 = "Value too big";
    private static final String BAD_VALUE = "I am invalid value";
    private static final String BAD_VALUE_MSG = "Object with name '%s' is invalid.";

    @Before
    //Before even start testing do some preparations!
    public void initTestPreConditions() {
        intValidator = new MS_ObjectValidator<Integer>() {
            @Override
            protected void initAllPossibleValidationErrors() {
                this.initNewError(1).setErrorMessageForming(() -> ERROR1);
                this.initNewError(2).setErrorMessageForming(() -> ERROR2);
            }
            @Override
            protected void doValidation(Integer objectToValidate, MS_List validationErrorList) {
                if (objectToValidate < 3)
                    this.addErrorToList(1, objectToValidate);
                if (objectToValidate > 5)
                    this.addErrorToList(2, objectToValidate);
            }
        };

        stringValidator = new MS_ObjectValidator<String>() {
            String name = "";

            @Override
            protected void initAllPossibleValidationErrors() {
                this.initNewError(100).setErrorMessageForming(() ->
                        String.format(BAD_VALUE_MSG, name));
            }
            @Override
            protected void doValidation(String objectToValidate, MS_List<MS_ValidationError<String>> validationErrorList) {
                if (objectToValidate.equals(BAD_VALUE))
                    this.addErrorToList(100, objectToValidate);
                name = MS_StringTools.substring(objectToValidate, 5, 12);
            }
        };
    }

    @Test
    public void test01NoValidationError() {
        intValidator.validate(3);
        assertFalse(intValidator.errorsFound());
        assertEquals(0, intValidator.getValidationErrors().count());
        assertFalse(intValidator.containsError(1));
        assertFalse(intValidator.containsError(5)); //test even non existing error
    }

    @Test
    public void test02SimpleErrors() {
        intValidator.validate(0);
        assertTrue(intValidator.errorsFound());
        assertEquals(1, intValidator.getValidationErrors().count());
        assertTrue(intValidator.containsError(1));
        assertFalse(intValidator.containsError(5));

        intValidator.validate(10);
        assertTrue(intValidator.errorsFound());
        assertEquals(2, intValidator.getValidationErrors().count());
        assertTrue(intValidator.containsError(1));
        assertTrue(intValidator.containsError(2));
    }

    @Test
    public void test03StringValidationNoError() {
        stringValidator.validate("normal text");
        assertFalse(stringValidator.errorsFound());
        assertEquals(0, stringValidator.getValidationErrors().count());
        assertFalse(stringValidator.containsError(100));
    }

    @Test
    public void test04StringValidationError() {
        stringValidator.validate(BAD_VALUE);
        assertTrue(stringValidator.errorsFound());
        assertEquals(1, stringValidator.getValidationErrors().count());
        assertTrue(stringValidator.containsError(100));
    }

    @Test
    public void test05StringValidationErrorMessage() {
        stringValidator.validate(BAD_VALUE);
        assertTrue(stringValidator.containsError(100));
        assertEquals(String.format(BAD_VALUE_MSG, "invalid"), stringValidator.getValidationErrors().get(0).getMessage());
    }
}
