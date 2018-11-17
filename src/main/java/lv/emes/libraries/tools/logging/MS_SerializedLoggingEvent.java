package lv.emes.libraries.tools.logging;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import lv.emes.libraries.tools.MS_ObjectWrapper;
import lv.emes.libraries.tools.MS_ObjectWrapperHelper;
import lv.emes.libraries.utilities.MS_DateTimeUtils;
import lv.emes.libraries.utilities.MS_StringUtils;

import java.util.Objects;

/**
 * Serialized logging event, which wraps real {@link MS_LoggingEvent}.
 * All instance members are of type String so that it can easily moved outside application.
 * <p>Fields <b>time</b> and <b>error</b> are being serialized with {@link JsonWriter}.
 * Field <b>type</b> is being serialized just by taking its enum constant name {@link MS_LoggingEventTypeEnum#name()}.
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
public class MS_SerializedLoggingEvent implements MS_ObjectWrapper<MS_LoggingEvent> {

    private String time, type, message, error;
    private MS_LoggingEvent wrappedObject;

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }

    public MS_SerializedLoggingEvent withTime(String time) {
        this.time = time;
        return this;
    }

    public MS_SerializedLoggingEvent withType(String type) {
        this.type = type;
        return this;
    }

    public MS_SerializedLoggingEvent withMessage(String message) {
        this.message = message;
        return this;
    }

    public MS_SerializedLoggingEvent withError(String error) {
        this.error = error;
        return this;
    }

    @Override
    public String toString() {
        return "MS_SerializedLoggingEvent{" +
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

        MS_SerializedLoggingEvent that = (MS_SerializedLoggingEvent) o;

        //time nanoseconds part might be altered when new logging event is created
        return (time != null ? MS_StringUtils.getSubstring(time, 0, 22)
                .equals(MS_StringUtils.getSubstring(that.time, 0, 22)) : that.time == null) &&
                Objects.equals(type, that.type) &&
                (message != null ? message.equals(that.message) : that.message == null) &&
                (error != null ? error.equals(that.error) : that.error == null);
    }

    @Override
    public int hashCode() {
        int result = time != null ? time.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (error != null ? error.hashCode() : 0);
        return result;
    }

    @Override
    public void wrap(MS_LoggingEvent event) {
        time = MS_DateTimeUtils.formatDateTime(event.getTime(), MS_DateTimeUtils._DATE_TIME_FORMAT_NANOSEC_ZONE_OFFSET);
        error = JsonWriter.objectToJson(event.getError());
        type = event.getType().name();
        message = event.getMessage();
        wrappedObject = event;
    }

    @Override
    public MS_LoggingEvent unwrap() {
        return wrappedObject = new MS_LoggingEvent()
                .withTime(time == null ? null : MS_DateTimeUtils.formatDateTimeBackported(time, MS_DateTimeUtils._DATE_TIME_FORMAT_NANOSEC_ZONE_OFFSET))
                .withError(error == null ? null : (Exception) JsonReader.jsonToJava(error))
                .withType(MS_LoggingEventTypeEnum.valueOf(type))
                .withMessage(message)
                ;
    }

    @Override
    public MS_LoggingEvent getWrappedObject() {
        return MS_ObjectWrapperHelper.getWrappedObject(this, wrappedObject);
    }
}
