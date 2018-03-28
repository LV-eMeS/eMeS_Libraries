package lv.emes.libraries.utilities;

/**
 * An exception indicating that some kind of operation failed to execute.
 *
 * @author eMeS
 */
public class MS_ExecutionFailureException extends Exception {

    public MS_ExecutionFailureException() {
    }

    public MS_ExecutionFailureException(String messsage, Throwable cause) {
        super(messsage, cause);
    }

    public MS_ExecutionFailureException(String message) {
        super(message);
    }
}
