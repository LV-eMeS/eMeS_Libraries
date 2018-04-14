package lv.emes.libraries.utilities;

/**
 * An exception indicating that some kind of operation failed to execute due to runtime exception.
 *
 * @author eMeS
 */
public class MS_RuntimeExecutionFailureException extends RuntimeException {

    public MS_RuntimeExecutionFailureException() {
    }

    public MS_RuntimeExecutionFailureException(String messsage, Throwable cause) {
        super(messsage, cause);
    }

    public MS_RuntimeExecutionFailureException(String message) {
        super(message);
    }
}
