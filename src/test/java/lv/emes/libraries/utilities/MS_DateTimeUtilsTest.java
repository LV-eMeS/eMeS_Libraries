package lv.emes.libraries.utilities;

import org.junit.Test;

import java.text.ParseException;
import java.time.*;
import java.util.Date;

import static lv.emes.libraries.utilities.MS_DateTimeUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

public class MS_DateTimeUtilsTest {

    @Test
    public void testToString() {
        Date simpleDate = new Date();
        LocalDateTime dateTime = LocalDateTime.ofInstant(simpleDate.toInstant(), ZoneId.systemDefault());
        LocalTime time = LocalTime.from(dateTime);
        LocalDate date = LocalDate.from(dateTime);

        assertThat(dateTimeToStr(dateTime)).isEqualTo(dateTimeToStr(simpleDate));
        assertThat(timeToStr(time)).isEqualTo(timeToStr(simpleDate));
        assertThat(dateToStr(date)).isEqualTo(dateToStr(simpleDate));

        //test in different formats
        String simpleDateStr, localDateTimeStr, localTimeStr, localDateStr;
        simpleDateStr = dateTimeToStr(simpleDate, _CUSTOM_DATE_TIME_FORMAT_LV);
        localDateTimeStr = dateTimeToStr(dateTime, _CUSTOM_DATE_TIME_FORMAT_LV);
        assertThat(localDateTimeStr).isEqualTo(simpleDateStr);

        simpleDateStr = dateTimeToStr(simpleDate, _CUSTOM_TIME_FORMAT_LV);
        localDateTimeStr = dateTimeToStr(dateTime, _CUSTOM_TIME_FORMAT_LV);
        localTimeStr = timeToStr(time, _CUSTOM_TIME_FORMAT_LV);
        assertThat(localDateTimeStr).isEqualTo(simpleDateStr);
        assertThat(localTimeStr).isEqualTo(simpleDateStr); //compare just time parts

        simpleDateStr = dateTimeToStr(simpleDate, _CUSTOM_DATE_FORMAT_LV);
        localDateTimeStr = dateTimeToStr(dateTime, _CUSTOM_DATE_FORMAT_LV);
        localDateStr = dateToStr(date, _CUSTOM_DATE_FORMAT_LV);
        assertThat(localDateTimeStr).isEqualTo(simpleDateStr);
        assertThat(localDateStr).isEqualTo(simpleDateStr); //compare just date parts
    }


    @Test
    public void testToString_DefaultFormat() {
        ZonedDateTime dateAndTimeNow = getCurrentDateTimeNow();
        String expected = formatDateTime(dateAndTimeNow, _DEFAULT_DATE_TIME_FORMAT);
        String actual = formatDateTime(dateAndTimeNow);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testToString_CustomFormat() {
        ZonedDateTime dateAndTimeCustom = ZonedDateTime.of(2017, 9, 27, 11, 34, 3, 990123456, ZoneId.systemDefault());
        String expected;
        String actual;

        //format: "yyyy-MM-dd'T'HH:mm:ss.SSSXXX'['VV']'"; XXX = +03:00 and VV = [Europe/Helsinki]
        expected = "2017-09-27T11:34:03.990123456" + getZoneOffsetText(dateAndTimeCustom)
                + "[" + getZoneIdText(dateAndTimeCustom) + "]";
        actual = formatDateTime(dateAndTimeCustom, _DATE_TIME_FORMAT_NANOSEC_ZONE_OFFSET_ID);
        assertThat(actual).isEqualTo(expected);

        //format: "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"; XXX = +03:00
        expected = "2017-09-27T11:34:03.990123456" + getZoneOffsetText(dateAndTimeCustom);
        actual = formatDateTime(dateAndTimeCustom, _DATE_TIME_FORMAT_NANOSEC_ZONE_OFFSET);
        assertThat(actual).isEqualTo(expected);

        //format: "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"; XXX = +03:00
        expected = "2017-09-27T11:34:03.990" + getZoneOffsetText(dateAndTimeCustom);
        actual = formatDateTime(dateAndTimeCustom, _DATE_TIME_FORMAT_MILLISEC_ZONE_OFFSET);
        assertThat(actual).isEqualTo(expected);

        //test if nano seconds are returned correctly
        expected = "990123456";
        actual = formatDateTime(dateAndTimeCustom, "SSSSSSSSS");
        assertThat(actual).isEqualTo(expected);

        //format: yyyy-MM-dd'T'HH:mm:ss
        expected = "2017-09-27T11:34:03";
        actual = formatDateTime(dateAndTimeCustom, _DATE_TIME_FORMAT_SECONDS);
        assertThat(actual).isEqualTo(expected);

        //format: yyyy-MM-dd
        expected = "2017-09-27";
        actual = formatDateTime(dateAndTimeCustom, _DATE_FORMAT_DATE_ONLY);
        assertThat(actual).isEqualTo(expected);

        //format: HH:mm:ss
        expected = "11:34:03";
        actual = formatDateTime(dateAndTimeCustom, _TIME_FORMAT_TIME_ONLY);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testToDateTime() {
        String dateAndOrTimeString;
        ZonedDateTime expected;
        ZonedDateTime actual;

        ZoneId.getAvailableZoneIds();
        expected = ZonedDateTime.of(2017, 9, 27, 12, 39, 45, 123456789, ZoneId.of("Europe/Helsinki"));
        dateAndOrTimeString = "2017-09-27T12:39:45.123456789+03:00[Europe/Helsinki]";
        actual = formatDateTime(dateAndOrTimeString, _DATE_TIME_FORMAT_NANOSEC_ZONE_OFFSET_ID);
        assertThat(actual).isEqualTo(expected);

        expected = ZonedDateTime.of(2017, 9, 27, 12, 39, 45, 123456789, ZoneOffset.of("+03:00"));
        dateAndOrTimeString = "2017-09-27T12:39:45.123456789+03:00";
        actual = formatDateTime(dateAndOrTimeString, _DATE_TIME_FORMAT_NANOSEC_ZONE_OFFSET);
        assertThat(actual).isEqualTo(expected);

        expected = ZonedDateTime.of(2017, 9, 27, 12, 39, 45, 123000000, ZoneOffset.of("+03:00"));
        dateAndOrTimeString = "2017-09-27T12:39:45.123+03:00";
        actual = formatDateTime(dateAndOrTimeString, _DATE_TIME_FORMAT_MILLISEC_ZONE_OFFSET);
        assertThat(actual).isEqualTo(expected);

        //milliseconds will be zero
        expected = ZonedDateTime.of(2017, 9, 27, 12, 39, 45, 0, ZoneOffset.of("+03:00"));
        dateAndOrTimeString = "2017-09-27T12:39:45+03:00";
        actual = formatDateTime(dateAndOrTimeString, _DATE_TIME_FORMAT_SECONDS_ZONE_OFFSET);
        assertThat(actual).isEqualTo(expected);

        //using default time zone
        expected = ZonedDateTime.of(2017, 9, 27, 12, 39, 45, 0, ZoneOffset.systemDefault()).withFixedOffsetZone();
        dateAndOrTimeString = "2017-09-27T12:39:45";
        actual = formatDateTime(dateAndOrTimeString, _DATE_TIME_FORMAT_SECONDS);
        assertThat(actual).isEqualTo(expected);

        //using same date without any time part
        expected = ZonedDateTime.of(2017, 9, 27, 0, 0, 0, 0, ZoneOffset.systemDefault()).withFixedOffsetZone();
        dateAndOrTimeString = "2017-09-27";
        actual = formatDateTime(dateAndOrTimeString, _DATE_FORMAT_DATE_ONLY);
        assertThat(actual).isEqualTo(expected);

        //date of Epoch day part plus concrete time
        expected = ZonedDateTime.of(1970, 1, 1, 1, 4, 3, 0, ZoneOffset.systemDefault()).withFixedOffsetZone();
        dateAndOrTimeString = "1:4:3"; //01:04:03
        actual = formatDateTime(dateAndOrTimeString, _TIME_FORMAT_TIME_ONLY);
        assertThat(actual).isEqualTo(expected);

        //date of Epoch day part plus concrete time
        expected = ZonedDateTime.of(1970, 1, 1, 1, 34, 0, 0, ZoneOffset.systemDefault()).withFixedOffsetZone();
        dateAndOrTimeString = "1:34";
        actual = formatDateTime(dateAndOrTimeString, _TIME_FORMAT_TIME_ONLY_HH_MM);
        assertThat(actual).isEqualTo(expected);

        //custom format LV
        expected = ZonedDateTime.of(2017, 10, 8, 18, 48, 11, 345000000, ZoneOffset.systemDefault()).withFixedOffsetZone();
        dateAndOrTimeString = "08.10.2017 18:48:11:345";
        actual = formatDateTime(dateAndOrTimeString, _CUSTOM_DATE_TIME_FORMAT_LV);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testParseDate() throws ParseException {
        String dateAndOrTimeString;
        Date expected;
        Date actual;

        dateAndOrTimeString = "2017-09-27T12:39:45";
        expected = zonedDateTimeToDate(ZonedDateTime.of(2017, 9, 27, 12, 39, 45, 0, ZoneOffset.systemDefault())) ;
        actual = parseDate(dateAndOrTimeString, _DATE_TIME_FORMAT_SECONDS);
        assertThat(actual).isEqualTo(expected);

        //using same date without any time part
        expected = zonedDateTimeToDate(ZonedDateTime.of(2017, 9, 27, 0, 0, 0, 0, ZoneOffset.systemDefault()));
        dateAndOrTimeString = "2017-09-27";
        actual = parseDate(dateAndOrTimeString, _DATE_FORMAT_DATE_ONLY);
        assertThat(actual).isEqualTo(expected);

        //default date format (_CUSTOM_DATE_TIME_FORMAT_LV)
        expected = zonedDateTimeToDate(ZonedDateTime.of(2017, 9, 27, 0, 32, 12, 0, ZoneOffset.systemDefault())) ;
        dateAndOrTimeString = "27.09.2017 00:32:12:000";
        actual = parseDate(dateAndOrTimeString);
        assertThat(actual).isEqualTo(expected);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFormatStringToZonedDateTimeException() {
        formatDateTime("", "incorrect format");
    }

}
