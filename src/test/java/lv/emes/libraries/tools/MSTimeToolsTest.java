package lv.emes.libraries.tools;

import org.junit.Assert;
import org.junit.Test;

import java.time.*;
import java.util.Date;

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

		Assert.assertEquals(MS_TimeTools.dateTimeToStr(simpleDate), MS_TimeTools.dateTimeToStr(dateTime));
		Assert.assertEquals(MS_TimeTools.timeToStr(simpleDate), MS_TimeTools.timeToStr(time));
		Assert.assertEquals(MS_TimeTools.dateToStr(simpleDate), MS_TimeTools.dateToStr(date));
	}
}
