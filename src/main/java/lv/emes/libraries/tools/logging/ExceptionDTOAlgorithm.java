package lv.emes.libraries.tools.logging;

/**
 * Algorithm for Exception mapping based on Throwable mapping algorithm.
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.2.4.
 */
public class ExceptionDTOAlgorithm extends ThrowableDTOAlgorithm {

    protected Exception newThrowable(String message, Throwable cause) {
        return new Exception(message, cause);
    }
}
