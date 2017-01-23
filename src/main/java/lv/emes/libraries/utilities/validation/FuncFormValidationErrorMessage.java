package lv.emes.libraries.utilities.validation;

/**
 * Use this to define behavior when validation error message is being formed.
 *
 * @author eMeS
 * @version 1.0.
 */
@FunctionalInterface
public interface FuncFormValidationErrorMessage {
    String formMessage();
}
