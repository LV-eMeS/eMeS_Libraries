package lv.emes.libraries.tools.platform;

/** 
 * An exception class for <b>MS_ScriptRunner</b>.
 * This kind of exception is thrown when script runner failed to parse some command correctly.
 * @version 1.0.
 * @author eMeS
 * @see MS_ScriptRunner
 */
public class ScriptParsingError extends Exception {
	//PUBLIC STRUCTURES, EXCEPTIONS, PROPERTIES AND CONSTANTS
    public final static String ERROR_PARAMETER_COUNT = "Wrong count of passed parameters. Expecting: %d";
    public final static String ERROR_FAILED_TO_SHOW_WINDOW = "Failed to show application window with name: %s";
    public final static String ERROR_FAILED_TO_HIDE_WINDOW = "Failed to hide application window with name: %s";
    public final static String WARNING_USER_VARIABLE_OVERRIDDEN = "Warning: parameter '%s' already existed. Previous value '%s' was overridden to '%s'";

	//CONSTRUCTORS
    public ScriptParsingError(String message) {
        super(message);
    }
}
