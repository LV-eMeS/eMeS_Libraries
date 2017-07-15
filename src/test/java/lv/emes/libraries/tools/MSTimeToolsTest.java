package lv.emes.libraries.tools;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import static lv.emes.libraries.tools.MS_TimeTools.*;
import static org.junit.Assert.assertEquals;

public class MSTimeToolsTest {
	Date simpleDate;
	LocalDateTime dateTime;
	LocalTime time;
	LocalDate date;

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
		simpleDateStr = dateTimeToStr(simpleDate, _DATE_TIME_FORMAT);
		localDateTimeStr = dateTimeToStr(dateTime, _DATE_TIME_FORMAT);
		assertEquals(simpleDateStr, localDateTimeStr);

		simpleDateStr = dateTimeToStr(simpleDate, _TIME_FORMAT);
		localDateTimeStr = dateTimeToStr(dateTime, _TIME_FORMAT);
		localTimeStr = timeToStr(time, _TIME_FORMAT);
		assertEquals(simpleDateStr, localDateTimeStr);
		assertEquals(simpleDateStr, localTimeStr); //compare just time parts

		simpleDateStr = dateTimeToStr(simpleDate, _DATE_FORMAT);
		localDateTimeStr = dateTimeToStr(dateTime, _DATE_FORMAT);
		localDateStr = dateToStr(date, _DATE_FORMAT);
		assertEquals(simpleDateStr, localDateTimeStr);
		assertEquals(simpleDateStr, localDateStr); //compare just date parts
	}
}
