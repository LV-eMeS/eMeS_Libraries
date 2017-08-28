package lv.emes.libraries.utilities;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Class consists of static methods only and is designed to operate with time and date.
 * It includes typical modifications and actions with time formats.
 *
 * @author eMeS
 * @version 1.2.
 */
public final class MS_TimeUtils {

    public static final String _DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm:ss:SSS";
    public static final String _TIME_FORMAT = "HH:mm:ss:SSS";
    public static final String _DATE_FORMAT = "dd.MM.yyyy";
    public static final String _DATE_TIME_FORMAT_EN = "yyyy-MM-dd HH:mm:ss,SSS";
    public static final String _DATE_FORMAT_EN = "yyyy-MM-dd";
    public static final String _TIME_FORMAT_EN = "HH:mm:ss,SSS";

    private MS_TimeUtils() {
    }

    /**
     * Converts date and time to text.
     *
     * @param date a date in format: "dd.MM.yyyy HH:mm:ss:SSS".
     * @return a text representing passed date and time.
     */
    public static String dateTimeToStr(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(_DATE_TIME_FORMAT);
        return formatter.format(date);
    }

    /**
     * Converts time to text.
     *
     * @param date a date in format: "HH:mm:ss:SSS".
     * @return a text representing passed time.
     */
    public static String timeToStr(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(_TIME_FORMAT);
        return formatter.format(date);
    }

    /**
     * Converts date without time part to text.
     *
     * @param date a date in format: "dd.MM.yyyy".
     * @return a text representing passed date.
     */
    public static String dateToStr(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(_DATE_FORMAT);
        return formatter.format(date);
    }

    /**
     * Converts date to text in desired format.
     *
     * @param date  a date with or without time part.
     * @param format format for string representation of date.
     * @return a text representing passed date.
     */
    public static String dateTimeToStr(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    /**
     * Converts local date and time to text in presented format.
     *
     * @param date  a date containing time part.
     * @param format format for string representation of date.
     * @return a text representing passed date and time.
     */
    public static String dateTimeToStr(LocalDateTime date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return date.format(formatter);
    }

    /**
     * Converts local date and time to text.
     *
     * @param date a date containing time part in format: "dd.MM.yyyy HH:mm:ss:SSS".
     * @return a text representing passed date and time.
     */
    public static String dateTimeToStr(LocalDateTime date) {
        return dateTimeToStr(date, _DATE_TIME_FORMAT);
    }

    /**
     * Converts time to text.
     *
     * @param time   a time without date part.
     * @param format format for string representation of time.
     * @return a text representing passed time.
     */
    public static String timeToStr(LocalTime time, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return time.format(formatter);
    }

    /**
     * Converts time to text.
     *
     * @param time a date in format: "HH:mm:ss:SSS".
     * @return a text representing passed time.
     */
    public static String timeToStr(LocalTime time) {
        return timeToStr(time, _TIME_FORMAT);
    }

    /**
     * Converts date without time to text.
     *
     * @param date   a time without time part.
     * @param format format for string representation of date.
     * @return a text representing passed date.
     */
    public static String dateToStr(LocalDate date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return date.format(formatter);
    }

    /**
     * Converts date without time to text.
     *
     * @param date a date in format: "dd.MM.yyyy".
     * @return a text representing passed date.
     */
    public static String dateToStr(LocalDate date) {
        return dateToStr(date, _DATE_FORMAT);
    }
}