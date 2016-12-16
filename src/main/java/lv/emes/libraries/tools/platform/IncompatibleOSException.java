package lv.emes.libraries.tools.platform;

/**
 * Can be thrown when trying to perform an action that is OS dependent in incompatible OS.
 * @author eMeS
 * @version 1.0.
 */
public class IncompatibleOSException extends Exception {
    public IncompatibleOSException(String message) {
        super(message);
    }
}
