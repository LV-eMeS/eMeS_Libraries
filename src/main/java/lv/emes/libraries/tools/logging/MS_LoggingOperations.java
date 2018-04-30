package lv.emes.libraries.tools.logging;

/**
 * Describes operations common for all eMeS loggers.
 * <p>Public methods:
 * <ul>
 * <li>info</li>
 * <li>warn</li>
 * <li>error</li>
 * <li>line</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.1.
 */
public interface MS_LoggingOperations {

    /**
     * Default line text for logging content separator line.
     */
    String _LINE = "------------------------------------------------------------------------";

    /**
     * Log informative message <b>msg</b>.
     * Word "INFO" will be added before this message while performing logging operation.
     *
     * @param msg informative text to be added to logger entries.
     */
    void info(String msg);

    /**
     * Log warn with message <b>msg</b>.
     * Word "WARN" will be added before this message while performing logging operation.
     *
     * @param msg informative text to be added to logger entries.
     */
    void warn(String msg);

    /**
     * Log error with message <b>msg</b>.
     * Word "ERROR" will be added before this message while performing logging operation.
     *
     * @param msg informative text to be added to logger entries.
     */
    void error(String msg);

    /**
     * Log error with message <b>msg</b> and add <b>error</b> stack trace as well.
     *
     * @param msg informative text to be added to logger entries.
     * @param error exception that occurred by this error (stack trace will be logged if error is not null).
     */
    void error(String msg, Exception error);

    /**
     * Log some line of repeating amount specific symbols (for example, dashes) to separate logging content.
     */
    void line();
}
