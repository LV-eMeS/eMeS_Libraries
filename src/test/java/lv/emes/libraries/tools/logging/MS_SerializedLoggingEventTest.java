package lv.emes.libraries.tools.logging;

import com.cedarsoftware.util.io.JsonWriter;
import lv.emes.libraries.tools.MS_ObjectWrapperHelper;
import lv.emes.libraries.utilities.MS_DateTimeUtils;
import lv.emes.libraries.utilities.MS_TestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author eMeS
 * @version 1.0.
 */
public class MS_SerializedLoggingEventTest {

    @Test
    public void test01SerializationAndDeserialization() {
        MS_LoggingEvent event = newEvent();
        MS_SerializedLoggingEvent wrapper = MS_ObjectWrapperHelper.wrap(event, MS_SerializedLoggingEvent.class);
        MS_LoggingEvent wrappedEvent = wrapper.getWrappedObject();
        assertEquals(event, wrappedEvent);
    }

    @Test
    public void test02UnwrapObject() {
        MS_SerializedLoggingEvent wrapper = new MS_SerializedLoggingEvent();
        ZonedDateTime timeNow = ZonedDateTime.now();
        wrapper.withTime(MS_DateTimeUtils.formatDateTime(timeNow, MS_DateTimeUtils._DATE_TIME_FORMAT_NANOSEC_ZONE_OFFSET));
        wrapper.withError(JsonWriter.objectToJson(null));
        wrapper.withType(MS_LoggingEventTypeEnum.UNSPECIFIED.name());
        wrapper.withMessage(RandomStringUtils.randomAlphabetic(10));

        MS_LoggingEvent wrappedEvent = wrapper.unwrap();

        assertNull(wrappedEvent.getError());
        assertEquals(wrapper.getMessage(), wrappedEvent.getMessage());
        assertEquals(wrapper.getType(), wrappedEvent.getType().name());
        assertEquals(timeNow.toInstant(), wrappedEvent.getTime().truncatedTo(ChronoUnit.MILLIS).toInstant());

        assertEquals(wrapper, MS_ObjectWrapperHelper.wrap(wrappedEvent, MS_SerializedLoggingEvent.class));
    }

    private MS_LoggingEvent newEvent() {
        return new MS_LoggingEvent()
                .withTime(ZonedDateTime.now())
                .withMessage(RandomStringUtils.randomAlphabetic(10))
                .withType(MS_LoggingEventTypeEnum.ERROR)
                .withError(new MS_TestUtils.MS_CheckedException())
                ;
    }
}