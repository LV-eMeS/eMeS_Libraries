package lv.emes.libraries.tools.logging;

/**
 * An enumeration for different event types to log.
 * This enum is used in loggers to determine, what type of event is being logged and depending on logger implementation
 * enum name can be part of logged message.
 *
 * @author eMeS
 * @version 1.0.
 */
public enum LoggingEventTypeEnum {

    INFO, WARN, ERROR, UNSPECIFIED
}
