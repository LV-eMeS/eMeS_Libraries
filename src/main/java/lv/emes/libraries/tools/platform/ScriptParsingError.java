package lv.emes.libraries.tools.platform;

/**
 * An exception class for <b>MS_ScriptRunner</b>.
 * This kind of exception is thrown when script runner failed to parse some command correctly.
 *
 * @author eMeS
 * @version 1.2.
 * @see MS_ScriptRunner
 */
class ScriptParsingError extends Exception {

    //PUBLIC STRUCTURES, EXCEPTIONS, PROPERTIES AND CONSTANTS
    final static String _ERROR_PARAMETER_COUNT = "Wrong count of passed parameters. Expecting at least: %d";
    final static String _ERROR_EXACT_PARAMETER_COUNT = "Wrong count of passed parameters. Expecting exactly: %d";
    final static String _ERROR_FAILED_TO_SHOW_WINDOW = "Failed to show application window with name: %s";
    final static String _ERROR_FAILED_TO_HIDE_WINDOW = "Failed to hide application window with name: %s";
    final static String _ERROR_FAILED_TO_KILL_TASK = "Failed to kill task with name: %s";
    final static String _ERROR_FAILED_TO_SWITCH_MONITOR = "Failed to switch monitor on or off due to invalid syntax. " +
            "Correct syntax is: 'monitor#on#' or 'monitor#off#'";
    final static String _ERROR_WRONG_NUMBER_INPUT = "Failed to set number with value '%s' as parameter to command";
    final static String _ERROR_WRONG_MOUSE_WHEEL_COMMAND = "Failed to do mouse wheel action with parameters '%s' as parameter to command" +
            "Expecting something like: 'WHEEL#CLICK', 'WHEEL#UP&1#' or 'WHEEL#DOWN&3#'";
    final static String _WARNING_USER_VARIABLE_OVERRIDDEN = "Warning: parameter '%s' already existed. Previous value '%s' was overridden to '%s'";
    final static String _ERROR_FAILED_TO_WRITE_TO_FILE = "Failed to write a line into file. \nLine: %s\nFile name: %s";
    final static String _ERROR_UNSUPPORTED_SLEEPING_CMD_SYNTAX = "Unsupported sleeping operation %s '%s'. Expected something similar to:\n" +
            "['sleep#500#' or 'sleep#till&23:55#', or 'sleep#till&2018-04-30T21:42:28#'] 'Sleep till...' supports following date/time formats:\n" +
            "['HH:mm', 'HH:mm:ss', 'yyyy-MM-dd'T'HH:mm:ss'], where 'T' means simply character T.";

    //CONSTRUCTORS
    ScriptParsingError(String message, Object... msgParams) {
        super(String.format(message, msgParams));
    }
}
