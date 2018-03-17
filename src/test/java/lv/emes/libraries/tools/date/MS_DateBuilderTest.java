package lv.emes.libraries.tools.date;

import lv.emes.libraries.utilities.MS_DateTimeUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author eMeS
 * @version 1.0.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MS_DateBuilderTest {

    @Test
    public void test01ReferenceDateToday() {
        ZonedDateTime referenceTimeFuture = MS_DateTimeUtils.getCurrentDateTimeNow().plusSeconds(1); //tests will be run for 1 second max, so this
        //is reference date time to compare with built dates
        ZonedDateTime referenceTimePast = referenceTimeFuture.minusSeconds(2);
        ZonedDateTime builtTime;

        //Create new date for tomorrow
        builtTime = new MS_DateBuilder().withQuantity(1).withTimeUnit("minute").build().dateTime();
        assertTrue(referenceTimeFuture.isBefore(builtTime));
        assertTrue(referenceTimeFuture.plusMinutes(1).isAfter(builtTime));

        builtTime = new MS_DateBuilder().withQuantity(1).withTimeUnit("hour").build().dateTime();
        assertTrue(referenceTimeFuture.isBefore(builtTime));
        assertTrue(referenceTimeFuture.plusHours(1).isAfter(builtTime));

        builtTime = new MS_DateBuilder().withQuantity(1).withTimeUnit("day").build().dateTime();
        assertTrue(referenceTimeFuture.isBefore(builtTime));
        assertTrue(referenceTimeFuture.plusDays(1).isAfter(builtTime));

        builtTime = new MS_DateBuilder().withQuantity(1).withTimeUnit("week").build().dateTime();
        assertTrue(referenceTimeFuture.isBefore(builtTime));
        assertTrue(referenceTimeFuture.plusWeeks(1).isAfter(builtTime));

        builtTime = new MS_DateBuilder().withQuantity(1).withTimeUnit("month").build().dateTime();
        assertTrue(referenceTimeFuture.isBefore(builtTime));
        assertTrue(referenceTimeFuture.plusMonths(1).isAfter(builtTime));

        builtTime = new MS_DateBuilder().withQuantity(1).withTimeUnit("year").build().dateTime();
        assertTrue(referenceTimeFuture.isBefore(builtTime));
        assertTrue(referenceTimeFuture.plusYears(1).isAfter(builtTime));


        builtTime = new MS_DateBuilder().withQuantity(-1).withTimeUnit("minute").build().dateTime();
        assertTrue(referenceTimePast.isAfter(builtTime));
        assertTrue(referenceTimePast.minusMinutes(1).isBefore(builtTime));

        builtTime = new MS_DateBuilder().withQuantity(-1).withTimeUnit("hour").build().dateTime();
        assertTrue(referenceTimePast.isAfter(builtTime));
        assertTrue(referenceTimePast.minusHours(1).isBefore(builtTime));

        builtTime = new MS_DateBuilder().withQuantity(-1).withTimeUnit("day").build().dateTime();
        assertTrue(referenceTimePast.isAfter(builtTime));
        assertTrue(referenceTimePast.minusDays(1).isBefore(builtTime));

        builtTime = new MS_DateBuilder().withQuantity(-1).withTimeUnit("week").build().dateTime();
        assertTrue(referenceTimePast.isAfter(builtTime));
        assertTrue(referenceTimePast.minusWeeks(1).isBefore(builtTime));

        builtTime = new MS_DateBuilder().withQuantity(-1).withTimeUnit("month").build().dateTime();
        assertTrue(referenceTimePast.isAfter(builtTime));
        assertTrue(referenceTimePast.minusMonths(1).isBefore(builtTime));

        builtTime = new MS_DateBuilder().withQuantity(-1).withTimeUnit("year").build().dateTime();
        assertTrue(referenceTimePast.isAfter(builtTime));
        assertTrue(referenceTimePast.minusYears(1).isBefore(builtTime));


        builtTime = new MS_DateBuilder().fromText("today").build().dateTime();
        assertTrue(referenceTimePast.isBefore(builtTime));
        assertTrue(referenceTimeFuture.isAfter(builtTime));
    }

    @Test
    public void test02ReferenceDateGiven() {
        final ZonedDateTime REFERENCE_DATE = MS_DateTimeUtils.formatDateTime("2017-10-08T15:05:00",
                MS_DateTimeUtils._DATE_TIME_FORMAT_SECONDS);
        MS_DateBuilder builder;
        String expected;
        String actual;

        builder = new MS_DateBuilder(REFERENCE_DATE).withPrefix("before").withQuantity(2).withTimeUnit("hours")
                .withFormat(MS_DateTimeUtils._DATE_TIME_FORMAT_SECONDS).build();
        expected = "2017-10-08T13:05:00";
        actual = builder.string();
        assertEquals(expected, actual);

        builder = new MS_DateBuilder(REFERENCE_DATE).withPrefix("after").withQuantity(30).withTimeUnit("seconds")
                .withFormat(MS_DateTimeUtils._DATE_TIME_FORMAT_SECONDS).build();
        expected = "2017-10-08T15:05:30";
        actual = builder.string();
        assertEquals(expected, actual);

        builder = new MS_DateBuilder(REFERENCE_DATE).fromText("yesterday")
                .withFormat(MS_DateTimeUtils._DATE_TIME_FORMAT_SECONDS).build();
        expected = "2017-10-07T15:05:00";
        actual = builder.string();
        assertEquals(expected, actual);

        builder = new MS_DateBuilder(REFERENCE_DATE).fromText("tomorrow")
                .withFormat(MS_DateTimeUtils._DATE_TIME_FORMAT_SECONDS).build();
        expected = "2017-10-09T15:05:00";
        actual = builder.string();
        assertEquals(expected, actual);

        MS_DateBuilder anotherBuilder = new MS_DateBuilder(builder);
        assertEquals(builder.string(), anotherBuilder.string());
        assertEquals(builder.dateTime(), anotherBuilder.dateTime());
    }

    @Test(expected = MS_ConversionException.class)
    public void test03FromInvalidText1Part() {
        new MS_DateBuilder().fromText("anything");
    }

    @Test(expected = MS_ConversionException.class)
    public void test04FromInvalidText2Parts() {
        new MS_DateBuilder().fromText("anything incorrect");
    }

    @Test(expected = MS_ConversionException.class)
    public void test05FromInvalidText3Parts() {
        new MS_DateBuilder().fromText("any fake text");
    }
}
