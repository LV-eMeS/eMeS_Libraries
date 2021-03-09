package lv.emes.libraries.utilities.validation;

import lv.emes.libraries.tools.lists.MS_List;
import lv.emes.libraries.utilities.MS_StringUtils;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author eMeS
 * @version 1.0.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MS_ObjectValidatorTest {

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
                this.initNewError(1).withErrorMessageFormingAction(() -> ERROR1);
                this.initNewError(2).withErrorMessageFormingAction(() -> ERROR2);
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
                this.initNewError(100).withErrorMessageFormingAction(() ->
                        String.format(BAD_VALUE_MSG, name));
            }
            @Override
            protected void doValidation(String objectToValidate, MS_List<MS_ValidationError<String>> validationErrorList) {
                if (objectToValidate.equals(BAD_VALUE))
                    this.addErrorToList(100, objectToValidate);
                name = MS_StringUtils.substring(objectToValidate, 5, 12);
            }
        };
    }

    @Test
    public void test01NoValidationError() {
        intValidator.validate(3);
        assertThat(intValidator.errorsFound()).isFalse();
        assertThat(intValidator.getValidationErrors().count()).isEqualTo(0);
        assertThat(intValidator.containsError(1)).isFalse();
        assertThat(intValidator.containsError(5)).isFalse(); //test even non existing error
    }

    @Test
    public void test02SimpleErrors() {
        intValidator.validate(0);
        assertThat(intValidator.errorsFound()).isTrue();
        assertThat(intValidator.getValidationErrors().count()).isEqualTo(1);
        assertThat(intValidator.containsError(1)).isTrue();
        assertThat(intValidator.containsError(5)).isFalse();

        intValidator.validate(10);
        assertThat(intValidator.errorsFound()).isTrue();
        assertThat(intValidator.getValidationErrors().count()).isEqualTo(2);
        assertThat(intValidator.containsError(1)).isTrue();
        assertThat(intValidator.containsError(2)).isTrue();
    }

    @Test
    public void test03StringValidationNoError() {
        stringValidator.validate("normal text");
        assertThat(stringValidator.errorsFound()).isFalse();
        assertThat(stringValidator.getValidationErrors().count()).isEqualTo(0);
        assertThat(stringValidator.containsError(100)).isFalse();
    }

    @Test
    public void test04StringValidationError() {
        stringValidator.validate(BAD_VALUE);
        assertThat(stringValidator.errorsFound()).isTrue();
        assertThat(stringValidator.getValidationErrors().count()).isEqualTo(1);
        assertThat(stringValidator.containsError(100)).isTrue();
    }

    @Test
    public void test05StringValidationErrorMessage() {
        stringValidator.validate(BAD_VALUE);
        assertThat(stringValidator.containsError(100)).isTrue();
        assertThat(stringValidator.getValidationErrors().get(0).getMessage()).isEqualTo(String.format(BAD_VALUE_MSG, "invalid"));
    }
}
