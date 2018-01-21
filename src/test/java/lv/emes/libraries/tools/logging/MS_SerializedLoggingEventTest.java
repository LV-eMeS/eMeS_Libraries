package lv.emes.libraries.tools.logging;

import com.cedarsoftware.util.io.JsonWriter;
import lv.emes.libraries.tools.MS_ObjectWrapperHelper;
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
    public void test01SerializationAndDeserialization() throws Exception {
        MS_LoggingEvent event = newEvent();
        MS_SerializedLoggingEvent wrapper = MS_ObjectWrapperHelper.wrap(event, MS_SerializedLoggingEvent.class);
        MS_LoggingEvent wrappedEvent = wrapper.getWrappedObject();
        assertEquals(event, wrappedEvent);
    }

    @Test
    public void test02UnwrapObject() throws Exception {
        MS_SerializedLoggingEvent wrapper = new MS_SerializedLoggingEvent();
        ZonedDateTime timeNow = ZonedDateTime.now();
        wrapper.withTime(JsonWriter.objectToJson(timeNow));
        wrapper.withError(JsonWriter.objectToJson(null));
        wrapper.withType(LoggingEventTypeEnum.UNSPECIFIED.name());
        wrapper.withMessage(RandomStringUtils.randomAlphabetic(10));

        MS_LoggingEvent wrappedEvent = wrapper.unwrap();

        assertNull(wrappedEvent.getError());
        assertEquals(wrapper.getMessage(), wrappedEvent.getMessage());
        assertEquals(wrapper.getType(), wrappedEvent.getType().name());
        assertEquals(timeNow.truncatedTo(ChronoUnit.MILLIS), wrappedEvent.getTime().truncatedTo(ChronoUnit.MILLIS));

        assertEquals(wrapper, MS_ObjectWrapperHelper.wrap(wrappedEvent, MS_SerializedLoggingEvent.class));
    }

    private MS_LoggingEvent newEvent() {
        return new MS_LoggingEvent()
                .withTime(ZonedDateTime.now())
                .withMessage(RandomStringUtils.randomAlphabetic(10))
                .withType(LoggingEventTypeEnum.ERROR)
                .withError(new MS_TestUtils.MS_CheckedException())
                ;
    }
}