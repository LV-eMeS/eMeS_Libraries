package lv.emes.libraries.communication.db;

/**
 * An exception that indicates that SQL query is made incorrectly.
 * Widely used in {@link MS_SQLQuery}.
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_BadSQLSyntaxException extends RuntimeException {

    /**
     * Create exception with error message.
     *
     * @param message the error message for this exception.
     */
    public MS_BadSQLSyntaxException(String message) {
        super(message);
    }
}
