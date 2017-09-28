package lv.emes.libraries.tools.date;

/**
 * Exceptions that are occurring while performing conversion of data in unacceptable format.
 *
 * @author eMeS
 */
public class MS_ConversionException extends RuntimeException {

    public MS_ConversionException(String message) {
        super(message);
    }
}
