package lv.emes.libraries.tools.logging;

import lv.emes.libraries.utilities.MS_CodingUtils;
import org.threeten.bp.ZonedDateTime;

/**
 * Model for logging events.
 * <p>Setters and getters:
 * <ul>
 * <li>getTime</li>
 * <li>getType</li>
 * <li>getMessage</li>
 * <li>getError</li>
 * <li>withTime</li>
 * <li>withType</li>
 * <li>withMessage</li>
 * <li>withError</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.1.
 */
public class MS_LoggingEvent {

    /**
     * Time when event happened (data type is backported to support Android API < 26).
     */
    private ZonedDateTime time;
    /**
     * Type of event that is being logged.
     * If {@link MS_LoggingEventTypeEnum#UNSPECIFIED} then raw event message (without event type, time and error parts) should be logged.
     */
    private MS_LoggingEventTypeEnum type;
    /**
     * Specific message that will be stored in repository as entry.
     */
    private String message;
    /**
     * Specific exception which occurred during event
     * and which will be stored as additional information about event failure.
     * Can be <i>null</i> if this event is not related with errors.
     */
    private Exception error;

    /**
     * @return time when event happened.
     */
    public ZonedDateTime getTime() {
        return time;
    }

    /**
     * @return type of event that is being logged.
     * If {@link MS_LoggingEventTypeEnum#UNSPECIFIED} then raw event message (without event type, time and error parts) should be logged.
     */
    public MS_LoggingEventTypeEnum getType() {
        return type;
    }

    /**
     * @return specific message that will be stored in repository as entry.
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return specific exception which occurred during event
     * and which will be stored as additional information about event failure.
     * Can be <i>null</i> if this event is not related with errors.
     */
    public Exception getError() {
        return error;
    }

    /**
     * Sets time when event happened.
     *
     * @param time time when event happened.
     * @return reference to logging event itself.
     */
    public MS_LoggingEvent withTime(ZonedDateTime time) {
        if (time == null)
            time = ZonedDateTime.now();
        //to make this time more like ID by making it more unique from another times generated in same way
        //this doesn't change actual value, because ZonedDateTime.now() precision is till milliseconds
        //if this action is already done for presented time, don't increase nanoseconds anymore
        if (MS_CodingUtils.fractionalPart((double) time.getNano() / 10000, 9) == 0)
            time = time.plusNanos(MS_CodingUtils.randomNumber(1, 4444));
        this.time = time;
        return this;
    }

    /**
     * Sets type of event.
     *
     * @param eventType type of event that is being logged.
     *                  If {@link MS_LoggingEventTypeEnum#UNSPECIFIED} then raw event
     *                  message (without event type, time and error parts) should be logged.
     * @return reference to logging event itself.
     */
    public MS_LoggingEvent withType(MS_LoggingEventTypeEnum eventType) {
        this.type = eventType;
        return this;
    }

    /**
     * Sets message regarding to happened event.
     *
     * @param message specific message that will be stored in repository as entry.
     * @return reference to logging event itself.
     */
    public MS_LoggingEvent withMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * Sets with event related error.
     *
     * @param error specific exception which occurred during event
     *              and which will be stored as additional information about event failure.
     *              Can be <i>null</i> if this event is not related with errors.
     * @return reference to logging event itself.
     */
    public MS_LoggingEvent withError(Exception error) {
        this.error = error;
        return this;
    }

    @Override
    public String toString() {
        return "MS_LoggingEvent{" +
                "time=" + time +
                ", type=" + type +
                ", message='" + message + '\'' +
                ", error=" + error +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MS_LoggingEvent that = (MS_LoggingEvent) o;

        return (time != null ? time.toInstant().equals(that.time.toInstant()) : that.time == null)
                && type == that.type && (message != null ? message.equals(that.message) : that.message == null)
                && (error != null ? error.equals(that.error) : that.error == null);
    }

    @Override
    public int hashCode() {
        int result = time != null ? time.toInstant().hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (error != null ? error.hashCode() : 0);
        return result;
    }
}
