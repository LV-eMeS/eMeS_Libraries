package lv.emes.libraries.utilities;

/**
 * An exception indicating that some kind of operation failed to execute.
 *
 * @author eMeS
 */
public class MS_ObjectRetrievalFailureException extends RuntimeException {

    public MS_ObjectRetrievalFailureException(String messsage, Throwable cause) {
        super(messsage, cause);
    }
}
