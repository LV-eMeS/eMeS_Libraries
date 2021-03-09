package lv.emes.libraries.tools.date;

import lv.emes.libraries.utilities.MS_DateTimeUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(referenceTimeFuture.isBefore(builtTime)).isTrue();
        assertThat(referenceTimeFuture.plusMinutes(1).isAfter(builtTime)).isTrue();

        builtTime = new MS_DateBuilder().withQuantity(1).withTimeUnit("hour").build().dateTime();
        assertThat(referenceTimeFuture.isBefore(builtTime)).isTrue();
        assertThat(referenceTimeFuture.plusHours(1).isAfter(builtTime)).isTrue();

        builtTime = new MS_DateBuilder().withQuantity(1).withTimeUnit("day").build().dateTime();
        assertThat(referenceTimeFuture.isBefore(builtTime)).isTrue();
        assertThat(referenceTimeFuture.plusDays(1).isAfter(builtTime)).isTrue();

        builtTime = new MS_DateBuilder().withQuantity(1).withTimeUnit("week").build().dateTime();
        assertThat(referenceTimeFuture.isBefore(builtTime)).isTrue();
        assertThat(referenceTimeFuture.plusWeeks(1).isAfter(builtTime)).isTrue();

        builtTime = new MS_DateBuilder().withQuantity(1).withTimeUnit("month").build().dateTime();
        assertThat(referenceTimeFuture.isBefore(builtTime)).isTrue();
        assertThat(referenceTimeFuture.plusMonths(1).isAfter(builtTime)).isTrue();

        builtTime = new MS_DateBuilder().withQuantity(1).withTimeUnit("year").build().dateTime();
        assertThat(referenceTimeFuture.isBefore(builtTime)).isTrue();
        assertThat(referenceTimeFuture.plusYears(1).isAfter(builtTime)).isTrue();


        builtTime = new MS_DateBuilder().withQuantity(-1).withTimeUnit("minute").build().dateTime();
        assertThat(referenceTimePast.isAfter(builtTime)).isTrue();
        assertThat(referenceTimePast.minusMinutes(1).isBefore(builtTime)).isTrue();

        builtTime = new MS_DateBuilder().withQuantity(-1).withTimeUnit("hour").build().dateTime();
        assertThat(referenceTimePast.isAfter(builtTime)).isTrue();
        assertThat(referenceTimePast.minusHours(1).isBefore(builtTime)).isTrue();

        builtTime = new MS_DateBuilder().withQuantity(-1).withTimeUnit("day").build().dateTime();
        assertThat(referenceTimePast.isAfter(builtTime)).isTrue();
        assertThat(referenceTimePast.minusDays(1).isBefore(builtTime)).isTrue();

        builtTime = new MS_DateBuilder().withQuantity(-1).withTimeUnit("week").build().dateTime();
        assertThat(referenceTimePast.isAfter(builtTime)).isTrue();
        assertThat(referenceTimePast.minusWeeks(1).isBefore(builtTime)).isTrue();

        builtTime = new MS_DateBuilder().withQuantity(-1).withTimeUnit("month").build().dateTime();
        assertThat(referenceTimePast.isAfter(builtTime)).isTrue();
        assertThat(referenceTimePast.minusMonths(1).isBefore(builtTime)).isTrue();

        builtTime = new MS_DateBuilder().withQuantity(-1).withTimeUnit("year").build().dateTime();
        assertThat(referenceTimePast.isAfter(builtTime)).isTrue();
        assertThat(referenceTimePast.minusYears(1).isBefore(builtTime)).isTrue();


        builtTime = new MS_DateBuilder().fromText("today").build().dateTime();
        assertThat(referenceTimePast.isBefore(builtTime)).isTrue();
        assertThat(referenceTimeFuture.isAfter(builtTime)).isTrue();
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
        assertThat(actual).isEqualTo(expected);

        builder = new MS_DateBuilder(REFERENCE_DATE).withPrefix("after").withQuantity(30).withTimeUnit("seconds")
                .withFormat(MS_DateTimeUtils._DATE_TIME_FORMAT_SECONDS).build();
        expected = "2017-10-08T15:05:30";
        actual = builder.string();
        assertThat(actual).isEqualTo(expected);

        builder = new MS_DateBuilder(REFERENCE_DATE).fromText("yesterday")
                .withFormat(MS_DateTimeUtils._DATE_TIME_FORMAT_SECONDS).build();
        expected = "2017-10-07T15:05:00";
        actual = builder.string();
        assertThat(actual).isEqualTo(expected);

        builder = new MS_DateBuilder(REFERENCE_DATE).fromText("tomorrow")
                .withFormat(MS_DateTimeUtils._DATE_TIME_FORMAT_SECONDS).build();
        expected = "2017-10-09T15:05:00";
        actual = builder.string();
        assertThat(actual).isEqualTo(expected);

        MS_DateBuilder anotherBuilder = new MS_DateBuilder(builder);
        assertThat(anotherBuilder.string()).isEqualTo(builder.string());
        assertThat(anotherBuilder.dateTime()).isEqualTo(builder.dateTime());
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
