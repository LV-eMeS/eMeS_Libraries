package lv.emes.libraries.tools.logging;

import lv.emes.libraries.file_system.MS_TextFile;
import lv.emes.libraries.utilities.MS_DateTimeUtils;
import org.threeten.bp.LocalDateTime;

import static lv.emes.libraries.utilities.MS_StringUtils._LINE_BRAKE;

/**
 * A text file writer that just appends specific file with specifically formatted text.
 * It also can be used as repository to log events to files via {@link MS_MultiLogger}.
 * <p>Public methods:
 * <ul>
 * <li>info</li>
 * <li>warn</li>
 * <li>error</li>
 * <li>line</li>
 * <li>logEvent</li>
 * </ul>
 *
 * @author eMeS
 * @version 2.2.
 */
public class MS_FileLogger implements MS_LoggingOperations, MS_LoggingRepository {

    private MS_TextFile actualFile;

    /**
     * Creates logger and binds it to file with presented name.
     *
     * @param fileName name of logger file.
     */
    public MS_FileLogger(String fileName) {
        this.actualFile = new MS_TextFile(fileName);
    }

    /**
     * Opens logger file and appends it with message <b>msg</b> starting with word "INFO".
     * After appending is done logger file is closed.
     *
     * @param msg arbitrary text to be added to logger lines.
     */
    public void info(String msg) {
        pAppendLine(pGetTimePart(LocalDateTime.now()) + pGetMainPart(MS_LoggingEventTypeEnum.INFO, msg));
    }

    /**
     * Opens logger file and appends it with message <b>msg</b> starting with word "WARN".
     * After appending is done logger file is closed.
     *
     * @param msg arbitrary text to be added to logger lines.
     */
    public void warn(String msg) {
        pAppendLine(pGetTimePart(LocalDateTime.now()) + pGetMainPart(MS_LoggingEventTypeEnum.WARN, msg));
    }

    /**
     * Opens logger file and appends it with message <b>msg</b> starting with word "ERROR".
     * After appending is done logger file is closed.
     *
     * @param msg arbitrary text to be added to logger lines.
     */
    public void error(String msg) {
        pAppendLine(pGetTimePart(LocalDateTime.now()) + pGetMainPart(MS_LoggingEventTypeEnum.ERROR, msg));
    }

    /**
     * Opens logger file and appends it with message <b>msg</b> starting with word "ERROR".
     * After message in new line error <b>error</b> is printed.
     * After appending is done logger file is closed.
     *
     * @param msg   arbitrary text to be added to logger lines.
     * @param error exception that occurred by this error.
     */
    public void error(String msg, Exception error) {
        if (error == null)
            error(msg);
        else
            pAppendLine(
                    pGetTimePart(LocalDateTime.now())
                            + pGetMainPart(MS_LoggingEventTypeEnum.ERROR, msg)
                            + pGetErrorPart(error));
    }

    /**
     * Opens logger file and appends it with dash symbol separator line that helps to delimit sections of file.
     * After appending is done logger file is closed.
     */
    public void line() {
        pAppendLine(_LINE);
    }

    @Override
    public void logEvent(MS_LoggingEvent event) {
        if (MS_LoggingEventTypeEnum.UNSPECIFIED.equals(event.getType()))
            pAppendLine(event.getMessage());
        else
            pAppendLine(pGetTimePart(event.getTime().toLocalDateTime())
                    + pGetMainPart(event.getType(), event.getMessage()) + pGetErrorPart(event.getError()));
    }

    //*** PRIVATE METHODS ***

    private String pGetTimePart(LocalDateTime time) {
        return MS_DateTimeUtils.dateTimeToStr(time, MS_DateTimeUtils._CUSTOM_DATE_TIME_FORMAT_EN) + " ";
    }

    private String pGetMainPart(MS_LoggingEventTypeEnum eventType, String message) {
        return eventType.name() + " " + message;
    }

    private String pGetErrorPart(Exception error) {
        return error == null ? "" : _LINE_BRAKE + error.toString();
    }

    private void pAppendLine(String text) {
        actualFile.appendln(text, true);
    }
}
