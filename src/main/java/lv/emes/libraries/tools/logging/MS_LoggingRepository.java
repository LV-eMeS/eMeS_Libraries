package lv.emes.libraries.tools.logging;

/**
 * A repository, where textual data and information about exception (mostly stack traces) can be stored as entries.
 * <p>Public methods:
 * <ul>
 * <li>logEvent</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.2.
 */
public interface MS_LoggingRepository {

    /**
     * Stores textual information about some event based on presented parameters into the logging repository.
     *
     * @param event event that is being logged.
     */
    void logEvent(MS_LoggingEvent event);
}
