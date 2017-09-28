package lv.emes.libraries.file_system;

import lv.emes.libraries.utilities.MS_DateTimeUtils;

import java.time.LocalDateTime;

import static lv.emes.libraries.utilities.MS_StringUtils._LINE_BRAKE;

/**
 * A text file writer that just appends specific file with specifically formatted text.
 * <p>Public methods:
 * <ul>
 * <li>info</li>
 * <li>warning</li>
 * <li>error</li>
 * <li>line</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.1.
 */
public class MS_Logger {

    public static final String _LINE = "------------------------------------------------------------------------";

    private MS_TextFile actualFile;

    /**
     * Creates logger and binds it to file with presented name.
     *
     * @param fileName name of logger file.
     */
    public MS_Logger(String fileName) {
        this.actualFile = new MS_TextFile(fileName);
    }

    private void pAppendLineWithPrefix(String prefix, String text) {
        if (prefix == null)
            pAppendLine(text);
        else
            pAppendLine(prefix + " " + text);
    }

    private void pAppendLine(String text) {
        actualFile.appendln(text);
        actualFile.close();
    }

    private void pAppendLineWithLeadingDate(String text) {
        LocalDateTime time = LocalDateTime.now();
        pAppendLineWithPrefix(MS_DateTimeUtils.dateTimeToStr(time, MS_DateTimeUtils._CUSTOM_DATE_TIME_FORMAT_EN), text);
    }

    /**
     * Opens logger file and appends it with message <b>msg</b> starting with word "INFO".
     * After appending is done logger file is closed.
     *
     * @param msg arbitrary text to be added to logger lines.
     */
    public void info(String msg) {
        pAppendLineWithLeadingDate("INFO " + msg);
    }

    /**
     * Opens logger file and appends it with message <b>msg</b> starting with word "WARN".
     * After appending is done logger file is closed.
     *
     * @param msg arbitrary text to be added to logger lines.
     */
    public void warning(String msg) {
        pAppendLineWithLeadingDate("WARN " + msg);
    }

    /**
     * Opens logger file and appends it with message <b>msg</b> starting with word "ERROR".
     * After appending is done logger file is closed.
     *
     * @param msg arbitrary text to be added to logger lines.
     */
    public void error(String msg) {
        pAppendLineWithLeadingDate("ERROR " + msg);
    }

    /**
     * Opens logger file and appends it with message <b>msg</b> starting with word "ERROR".
     * After message in new line error <b>error</b> is printed.
     * After appending is done logger file is closed.
     *
     * @param msg arbitrary text to be added to logger lines.
     * @param error exception that occurred by this error.
     */
    public void error(String msg, Exception error) {
        pAppendLineWithLeadingDate("ERROR " + msg + _LINE_BRAKE + error.toString());
    }

    /**
     * Opens logger file and appends it with dash symbol line that helps to delimit sections of file.
     * After appending is done logger file is closed.
     */
    public void line() {
        pAppendLine(_LINE);
    }
}
