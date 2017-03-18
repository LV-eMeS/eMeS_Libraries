package lv.emes.libraries.tools;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/** 
 * Class consists of static methods only and is designed to operate with time and date.
 * It includes typical modifications and actions with time formats.
 * @version 1.1.
 * @author eMeS
 */
public final class MS_TimeTools {
	public static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm:ss:SSS";
	public static final String TIME_FORMAT = "HH:mm:ss:SSS";
	public static final String DATE_FORMAT = "dd.MM.yyyy";

	private MS_TimeTools() {}

	/**
	 * Converts date and time to text.
	 * @param aDate a date in format: "dd.MM.yyyy HH:mm:ss:SSS".
	 * @return a text representing passed date and time.
	 */
	public static String dateTimeToStr(Date aDate) {
		SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT);
		return format.format(aDate);
	}

	/**
	 * Converts local date and time to text.
	 * @param aDate a date in format: "dd.MM.yyyy HH:mm:ss:SSS".
	 * @return a text representing passed date and time.
	 */
	public static String dateTimeToStr(LocalDateTime aDate) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
		return aDate.format(format);
	}

	/**
	 * Converts time to text.
	 * @param aDate a date in format: "HH:mm:ss:SSS".
	 * @return a text representing passed time.
	 */
	public static String timeToStr(Date aDate) {
		SimpleDateFormat format = new SimpleDateFormat(TIME_FORMAT);
		return format.format(aDate);
	}

	/**
	 * Converts time to text.
	 * @param time a date in format: "HH:mm:ss:SSS".
	 * @return a text representing passed time.
	 */
	public static String timeToStr(LocalTime time) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern(TIME_FORMAT);
		return time.format(format);
	}

	/**
	 * Converts date without time to text.
	 * @param aDate a date in format: "dd.MM.yyyy".
	 * @return a text representing passed date.
	 */
	public static String dateToStr(Date aDate) {
		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
		return format.format(aDate);
	}

	/**
	 * Converts date without time to text.
	 * @param aDate a date in format: "dd.MM.yyyy".
	 * @return a text representing passed date.
	 */
	public static String dateToStr(LocalDate aDate) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern(DATE_FORMAT);
		return aDate.format(format);
	}
}