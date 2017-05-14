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
    public final static String _ERROR_PARAMETER_COUNT = "Wrong count of passed parameters. Expecting at least: %d";
    public final static String _ERROR_FAILED_TO_SHOW_WINDOW = "Failed to show application window with name: %s";
    public final static String _ERROR_FAILED_TO_HIDE_WINDOW = "Failed to hide application window with name: %s";
    public final static String _ERROR_FAILED_TO_SWITCH_MONITOR = "Failed to switch monitor on or off due to invalid syntax. " +
            "Correct syntax is: 'monitor#on#' or 'monitor#off#'";
    public final static String _ERROR_WRONG_NUMBER_INPUT = "Failed to set number with value '%s' as parameter to command";
    public final static String _ERROR_WRONG_MOUSE_WHEEL_COMMAND = "Failed to do mouse wheel action with parameters '%s' as parameter to command" +
            "Expecting something like: 'WHEEL#CLICK', 'WHEEL#UP&1#' or 'WHEEL#DOWN&3#'";
    public final static String _WARNING_USER_VARIABLE_OVERRIDDEN = "Warning: parameter '%s' already existed. Previous value '%s' was overridden to '%s'";

	//CONSTRUCTORS
    public ScriptParsingError(String message) {
        super(message);
    }
}
