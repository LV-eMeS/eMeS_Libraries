package lv.emes.libraries.tools.lists;

/**
 * An exception that indicates that there are problems accessing some repository or doing manipulations with
 * items in it. Mostly this exception occurs when dealing with remote repositories.
 *
 * @author eMeS
 * @version 1.0.
 */
public class RepositoryDataExchangeException extends RuntimeException {

    /**
     * Default constructor.
     */
    public RepositoryDataExchangeException() {
        super();
    }

    /**
     * Create exception with error message.
     *
     * @param message the error message for this exception.
     */
    public RepositoryDataExchangeException(String message) {
        super(message);
    }

    /**
     * Create exception based on an existing Throwable.
     *
     * @param cause the throwable on which we'll base this exception.
     */
    public RepositoryDataExchangeException(Throwable cause) {
        super(cause);
    }

    /**
     * Create an exception with custom message and throwable info.
     *
     * @param message the message.
     * @param cause   the target Throwable.
     */
    public RepositoryDataExchangeException(String message, Throwable cause) {
        super(message, cause);
    }
}
