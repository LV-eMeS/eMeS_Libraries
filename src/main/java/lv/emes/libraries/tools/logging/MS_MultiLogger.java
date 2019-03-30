package lv.emes.libraries.tools.logging;

import lv.emes.libraries.tools.threading.MS_FutureEvent;
import org.threeten.bp.ZonedDateTime;

/**
 * This class allows to log events to different repositories simultaneously.
 * {@link MS_MultiLoggingSetup} is needed in order to set up all the repositories, against which logging operations
 * will be performed. For each repository event logging will be performed asynchronously in separate thread for each.
 * <p>Public methods:
 * <ul>
 * <li>info</li>
 * <li>warn</li>
 * <li>error</li>
 * <li>line</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.4.
 */
public class MS_MultiLogger implements MS_LoggingOperations {

    private MS_MultiLoggingSetup setup;

    /**
     * Creates multi logger with defined configuration <b>setup</b>.
     *
     * @param setup non-Null configuration for logger to define the way, how to work and which repositories to use.
     */
    public MS_MultiLogger(MS_MultiLoggingSetup setup) {
        this.setup = setup;
    }

    @Override
    public void info(String msg) {
        logEventMessageToAllRepos(msg, null, MS_LoggingEventTypeEnum.INFO);
    }

    @Override
    public void warn(String msg) {
        logEventMessageToAllRepos(msg, null, MS_LoggingEventTypeEnum.WARN);
    }

    @Override
    public void warn(String msg, Exception error) {
        logEventMessageToAllRepos(msg, error, MS_LoggingEventTypeEnum.WARN);
    }

    @Override
    public void error(String msg) {
        logEventMessageToAllRepos(msg, null, MS_LoggingEventTypeEnum.ERROR);
    }

    @Override
    public void error(String msg, Exception error) {
        logEventMessageToAllRepos(msg, error, MS_LoggingEventTypeEnum.ERROR);
    }

    @Override
    public void line() {
        if (setup.getDelimiterLineText() != null)
            logEventMessageToAllRepos(setup.getDelimiterLineText(), null, MS_LoggingEventTypeEnum.UNSPECIFIED);
    }

    private void logEventMessageToAllRepos(String message, Exception error, MS_LoggingEventTypeEnum eventType) {
        MS_LoggingEvent event = new MS_LoggingEvent()
                .withTime(ZonedDateTime.now())
                .withType(eventType)
                .withMessage(message)
                .withError(error);

        setup.getRepositories().forEachItem((repo, i) ->
                new MS_FutureEvent()
                        .withThreadName("MS_MultiLogger_" + i)
                        .withActionOnException((e) -> MS_Log4Java.getLogger(MS_MultiLogger.class)
                                .error("Concrete event logging to repository with index [" + i + "] have been failed due to an exception", e))
                        .withTimeout(setup.getMaxloggingOperationExecutionTime())
                        .withActionOnInterruptedException(() -> MS_Log4Java.getLogger(MS_MultiLogger.class)
                                .warn("Concrete event logging to repository with index [" + i + "] have been interrupted (timeout or another thread stopped it)"))
                        .withAction(() -> repo.logEvent(event))
                        .schedule()
        );
    }
}
