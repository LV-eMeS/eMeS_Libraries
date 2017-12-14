package lv.emes.libraries.tools.logging;

import lv.emes.libraries.tools.threading.MS_FutureEvent;

import java.time.ZonedDateTime;

/**
 * This class allows to log events to different repositories simultaneously.
 * {@link MS_MultiLoggingSetup} is needed in order to set up all the repositories, against which logging operations
 * will be performed. For each repository event logging will be performed asynchronously in separate thread for each.
 * <p>Public methods:
 * <ul>
 * <li>info</li>
 * <li>warning</li>
 * <li>error</li>
 * <li>line</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_MultiLogger implements ILoggingOperations {

    private MS_MultiLoggingSetup config;

    /**
     * Creates multi logger with defined configuration <b>setup</b>.
     *
     * @param setup configuration for logger to define the way, how to work and which repositories to use.
     */
    public MS_MultiLogger(MS_MultiLoggingSetup setup) {
        this.config = setup;
    }

    @Override
    public void info(String msg) {
        logEventMessageToAllRepos(msg, null, LoggingEventTypeEnum.INFO);
    }

    @Override
    public void warning(String msg) {
        logEventMessageToAllRepos(msg, null, LoggingEventTypeEnum.WARN);
    }

    @Override
    public void error(String msg) {
        logEventMessageToAllRepos(msg, null, LoggingEventTypeEnum.ERROR);
    }

    @Override
    public void error(String msg, Exception error) {
        logEventMessageToAllRepos(msg, error, LoggingEventTypeEnum.ERROR);
    }

    @Override
    public void line() {
        if (config.getDelimiterLineText() != null)
            logEventMessageToAllRepos(config.getDelimiterLineText(), null, LoggingEventTypeEnum.UNSPECIFIED);
    }

    private void logEventMessageToAllRepos(String message, Exception error, LoggingEventTypeEnum eventType) {
        ZonedDateTime timeNow = ZonedDateTime.now();
        config.getRepositories().forEachItem((repo, i) ->
                new MS_FutureEvent()
                        .withThreadName("MS_MultiLogger_" + i)
                        .withActionOnException((e) -> MS_Log4Java.getLogger(MS_MultiLogger.class)
                                .error("Event logging to repository with index [" + i + "] have been terminated due to an exception", e))
                        .withTimeout(5000)
                        .withActionOnInterruptedException(() -> MS_Log4Java.getLogger(MS_MultiLogger.class)
                                .warn("Event logging to repository with index [" + i + "] have been interrupted"))
                        .withAction(() -> repo.logEvent(new MS_LoggingEvent().withTime(timeNow)
                                .withType(eventType).withMessage(message).withError(error)))
                        .schedule()
        );
    }
}
