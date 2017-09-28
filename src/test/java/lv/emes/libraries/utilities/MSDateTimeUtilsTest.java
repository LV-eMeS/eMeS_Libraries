package lv.emes.libraries.utilities;

import org.junit.Test;

import java.time.*;
import java.util.Date;

import static lv.emes.libraries.utilities.MS_DateTimeUtils.*;
import static org.junit.Assert.assertEquals;

public class MSDateTimeUtilsTest {

	private Date simpleDate;
	private LocalDateTime dateTime;
	private LocalTime time;
	private LocalDate date;

	@Test
	public void testToString() {
		simpleDate = new Date();
		dateTime = LocalDateTime.ofInstant(simpleDate.toInstant(), ZoneId.systemDefault());
		time = LocalTime.from(dateTime);
		date = LocalDate.from(dateTime);

		assertEquals(dateTimeToStr(simpleDate), dateTimeToStr(dateTime));
		assertEquals(timeToStr(simpleDate), timeToStr(time));
		assertEquals(dateToStr(simpleDate), dateToStr(date));

		//test in different formats
		String simpleDateStr, localDateTimeStr, localTimeStr, localDateStr;
		simpleDateStr = dateTimeToStr(simpleDate, _CUSTOM_DATE_TIME_FORMAT_LV);
		localDateTimeStr = dateTimeToStr(dateTime, _CUSTOM_DATE_TIME_FORMAT_LV);
		assertEquals(simpleDateStr, localDateTimeStr);

		simpleDateStr = dateTimeToStr(simpleDate, _CUSTOM_TIME_FORMAT_LV);
		localDateTimeStr = dateTimeToStr(dateTime, _CUSTOM_TIME_FORMAT_LV);
		localTimeStr = timeToStr(time, _CUSTOM_TIME_FORMAT_LV);
		assertEquals(simpleDateStr, localDateTimeStr);
		assertEquals(simpleDateStr, localTimeStr); //compare just time parts

		simpleDateStr = dateTimeToStr(simpleDate, _CUSTOM_DATE_FORMAT_LV);
		localDateTimeStr = dateTimeToStr(dateTime, _CUSTOM_DATE_FORMAT_LV);
		localDateStr = dateToStr(date, _CUSTOM_DATE_FORMAT_LV);
		assertEquals(simpleDateStr, localDateTimeStr);
		assertEquals(simpleDateStr, localDateStr); //compare just date parts
	}


	@Test
	public void testToString_DefaultFormat() {
		ZonedDateTime dateAndTimeNow = MS_DateTimeUtils.getCurrentDateTimeNow();
		String expected = MS_DateTimeUtils.formatDateTime(dateAndTimeNow, MS_DateTimeUtils._DEFAULT_DATE_TIME_FORMAT);
		String actual = MS_DateTimeUtils.formatDateTime(dateAndTimeNow);
		assertEquals(expected, actual);
	}

	@Test
	public void testToString_CustomFormat() {
		ZonedDateTime dateAndTimeCustom = ZonedDateTime.of(2017, 9, 27, 11, 34, 3, 990123456, ZoneId.systemDefault());
		String expected;
		String actual;

		//format: "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"; XXX = +03:00
		expected = "2017-09-27T11:34:03.990" + MS_DateTimeUtils.getZoneText(dateAndTimeCustom);
		actual = MS_DateTimeUtils.formatDateTime(dateAndTimeCustom, MS_DateTimeUtils._DATE_TIME_FORMAT_WITH_MILLISEC_AND_ZONE);
		assertEquals(expected, actual);

		//test if nano seconds are returned correctly
		expected = "990123456";
		actual = MS_DateTimeUtils.formatDateTime(dateAndTimeCustom, "SSSSSSSSS");
		assertEquals(expected, actual);

		//format: yyyy-MM-dd'T'HH:mm:ss
		expected = "2017-09-27T11:34:03";
		actual = MS_DateTimeUtils.formatDateTime(dateAndTimeCustom, MS_DateTimeUtils._DATE_TIME_FORMAT_WITHOUT_MILLISEC_AND_ZONE);
		assertEquals(expected, actual);

		//format: yyyy-MM-dd
		expected = "2017-09-27";
		actual = MS_DateTimeUtils.formatDateTime(dateAndTimeCustom, MS_DateTimeUtils._DATE_FORMAT_DATE_ONLY);
		assertEquals(expected, actual);

		//format: HH:mm:ss
		expected = "11:34:03";
		actual = MS_DateTimeUtils.formatDateTime(dateAndTimeCustom, MS_DateTimeUtils._TIME_FORMAT_TIME_ONLY);
		assertEquals(expected, actual);
	}

	@Test
	public void testToDateTime() {
		String dateAndOrTimeString;
		ZonedDateTime expected = ZonedDateTime.of(2017, 9, 27, 12, 39, 45, 123000000, ZoneOffset.of("+03:00"));
		ZonedDateTime actual;

		dateAndOrTimeString = "2017-09-27T12:39:45.123+03:00";
		actual = MS_DateTimeUtils.formatDateTime(dateAndOrTimeString, MS_DateTimeUtils._DATE_TIME_FORMAT_WITH_MILLISEC_AND_ZONE);
		assertEquals(expected, actual);

		//milliseconds will be zero
		dateAndOrTimeString = "2017-09-27T12:39:45+03:00";
		expected = ZonedDateTime.of(2017, 9, 27, 12, 39, 45, 0, ZoneOffset.of("+03:00"));
		actual = MS_DateTimeUtils.formatDateTime(dateAndOrTimeString, MS_DateTimeUtils._DATE_TIME_FORMAT_WITHOUT_MILLISEC);
		assertEquals(expected, actual);

		//using default time zone
		dateAndOrTimeString = "2017-09-27T12:39:45";
		expected = ZonedDateTime.of(2017, 9, 27, 12, 39, 45, 0, ZoneOffset.systemDefault()).withFixedOffsetZone();
		actual = MS_DateTimeUtils.formatDateTime(dateAndOrTimeString, MS_DateTimeUtils._DATE_TIME_FORMAT_WITHOUT_MILLISEC_AND_ZONE);
		assertEquals(expected, actual);

		//using same date without any time part
		dateAndOrTimeString = "2017-09-27";
		expected = ZonedDateTime.of(2017, 9, 27, 0, 0, 0, 0, ZoneOffset.systemDefault()).withFixedOffsetZone();
		actual = MS_DateTimeUtils.formatDateTime(dateAndOrTimeString, MS_DateTimeUtils._DATE_FORMAT_DATE_ONLY);
		assertEquals(expected, actual);

		//date of Epoch day part plus concrete time
		dateAndOrTimeString = "11:34:03";
		expected = ZonedDateTime.of(1970, 1, 1, 11, 34, 3, 0, ZoneOffset.systemDefault()).withFixedOffsetZone();
		actual = MS_DateTimeUtils.formatDateTime(dateAndOrTimeString, MS_DateTimeUtils._TIME_FORMAT_TIME_ONLY);
		assertEquals(expected, actual);

		//custom format LV
		dateAndOrTimeString = "08.10.2017 18:48:11:345";
		expected = ZonedDateTime.of(2017, 10, 8, 18, 48, 11, 345000000, ZoneOffset.systemDefault()).withFixedOffsetZone();
		actual = MS_DateTimeUtils.formatDateTime(dateAndOrTimeString, MS_DateTimeUtils._CUSTOM_DATE_TIME_FORMAT_LV);
		assertEquals(expected, actual);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFormatStringToZonedDateTimeException() {
		MS_DateTimeUtils.formatDateTime("", "incorrect format");
	}

}
