package lv.emes.libraries.tools;

/**
 * An exception that occurs due to wrong class setup, misconfiguration, missing arguments or some contract violation.
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_BadSetupException extends RuntimeException {

    public MS_BadSetupException() {
        super();
    }

    public MS_BadSetupException(String message) {
        super(message);
    }

    public MS_BadSetupException(String message, Throwable cause) {
        super(message, cause);
    }

    public MS_BadSetupException(Throwable cause) {
        super(cause);
    }

    protected MS_BadSetupException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
