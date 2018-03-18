package lv.emes.libraries.utilities;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.zone.ZoneRules;
import java.util.Date;

/**
 * Class consists of static methods only and is designed to operate with time and date.
 * It includes typical modifications and actions with time formats.
 *
 * @author eMeS
 * @version 1.3.
 */
public final class MS_DateTimeUtils {

    //Fully supported formats
    public static final String _DATE_TIME_FORMAT_NANOSEC_ZONE_OFFSET_ID = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSXXX'['VV']'";
    public static final String _DATE_TIME_FORMAT_NANOSEC_ZONE_OFFSET = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSXXX";
    public static final String _DATE_TIME_FORMAT_MILLISEC_ZONE_OFFSET = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    public static final String _DATE_TIME_FORMAT_SECONDS_ZONE_OFFSET = "yyyy-MM-dd'T'HH:mm:ssXXX";
    public static final String _DATE_TIME_FORMAT_SECONDS = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String _DATE_FORMAT_DATE_ONLY = "yyyy-MM-dd";
    public static final String _TIME_FORMAT_TIME_ONLY = "HH:mm:ss";
    public static final String _CUSTOM_DATE_TIME_FORMAT_LV = "dd.MM.yyyy HH:mm:ss:SSS";
    public static final String _DEFAULT_DATE_TIME_FORMAT = _DATE_TIME_FORMAT_SECONDS_ZONE_OFFSET;
    //Custom formats
    public static final String _CUSTOM_TIME_FORMAT_LV = "HH:mm:ss:SSS";
    public static final String _CUSTOM_DATE_FORMAT_LV = "dd.MM.yyyy";
    public static final String _CUSTOM_DATE_TIME_FORMAT_EN = "yyyy-MM-dd HH:mm:ss,SSS";
    public static final String _TIME_FORMAT_EN = "HH:mm:ss,SSS";

    private MS_DateTimeUtils() {
    }

    /**
     * Converts date and time to text.
     *
     * @param date a date in format: "dd.MM.yyyy HH:mm:ss:SSS".
     * @return a text representing passed date and time.
     */
    public static String dateTimeToStr(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(_CUSTOM_DATE_TIME_FORMAT_LV);
        return formatter.format(date);
    }

    /**
     * Converts time to text.
     *
     * @param date a date in format: "HH:mm:ss:SSS".
     * @return a text representing passed time.
     */
    public static String timeToStr(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(_CUSTOM_TIME_FORMAT_LV);
        return formatter.format(date);
    }

    /**
     * Converts date without time part to text.
     *
     * @param date a date in format: "dd.MM.yyyy".
     * @return a text representing passed date.
     */
    public static String dateToStr(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(_CUSTOM_DATE_FORMAT_LV);
        return formatter.format(date);
    }

    /**
     * Converts date to text in desired format.
     *
     * @param date   a date with or without time part.
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
     * @param date   a date containing time part.
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
        return dateTimeToStr(date, _CUSTOM_DATE_TIME_FORMAT_LV);
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
        return timeToStr(time, _CUSTOM_TIME_FORMAT_LV);
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
        return dateToStr(date, _CUSTOM_DATE_FORMAT_LV);
    }


    /**
     * @return current date and time with fixed offset zone (no "Europe/Helsinki" included in zone part).
     */
    public static ZonedDateTime getCurrentDateTimeNow() {
        return ZonedDateTime.now().withFixedOffsetZone();
    }

    /**
     * Returns ZonedDateTime as string formatted in default format "yyyy-MM-dd'T'HH:mm:ssXXX".
     *
     * @param dateTime date together with time part as ZonedDateTime.
     * @return string type representation of given date.
     */
    public static String formatDateTime(ZonedDateTime dateTime) {
        return formatDateTime(dateTime, _DEFAULT_DATE_TIME_FORMAT);
    }

    /**
     * Returns ZonedDateTime as string formatted in given format.
     *
     * @param dateTime date together with time part as ZonedDateTime.
     * @param format   a date format that can be one of following:
     *                 <ul>
     *                 <li>_DEFAULT_DATE_TIME_FORMAT</li>
     *                 <li>DATE_TIME_FORMAT_WITHOUT_MILISEC</li>
     *                 <li>DATE_TIME_FORMAT_YYYY_MM_DD</li>
     *                 </ul>
     * @return string type representation of given date.
     */
    public static String formatDateTime(ZonedDateTime dateTime, String format) {
        if (dateTime == null || format == null) return null;
        DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern(format);
        return dateTime.format(formatter);
    }

    /**
     * Returns ZonedDateTime from string formatted in presented format <b>format</b>.
     * Only 5 default formats are supported.
     * <br>For {@link MS_DateTimeUtils#_DATE_FORMAT_DATE_ONLY} time part is considered as 00:00:00.
     * <br>For {@link MS_DateTimeUtils#_TIME_FORMAT_TIME_ONLY} date part is considered as Epoch day.
     *
     * @param dateTime date as string represented in presented format date time format.
     * @param format   one of supported date formats:
     *                 <ul>
     *                 <li>{@link MS_DateTimeUtils#_DATE_TIME_FORMAT_MILLISEC_ZONE_OFFSET};</li>
     *                 <li>{@link MS_DateTimeUtils#_DATE_TIME_FORMAT_SECONDS_ZONE_OFFSET};</li>
     *                 <li>{@link MS_DateTimeUtils#_DATE_TIME_FORMAT_NANOSEC_ZONE_OFFSET};</li>
     *                 <li>{@link MS_DateTimeUtils#_DATE_TIME_FORMAT_NANOSEC_ZONE_OFFSET_ID} - this might return wrong offset;</li>
     *                 <li>{@link MS_DateTimeUtils#_DATE_TIME_FORMAT_SECONDS};</li>
     *                 <li>{@link MS_DateTimeUtils#_DATE_FORMAT_DATE_ONLY};</li>
     *                 <li>{@link MS_DateTimeUtils#_TIME_FORMAT_TIME_ONLY};</li>
     *                 <li>{@link MS_DateTimeUtils#_CUSTOM_DATE_TIME_FORMAT_LV}.</li>
     *                 </ul>
     * @return ZonedDateTime object.
     * @throws IllegalArgumentException in case <b>format</b> is illegal.
     * @throws DateTimeParseException   in case date couldn't be parsed in given format <b>format</b>.
     */
    public static ZonedDateTime formatDateTime(String dateTime, String format) throws IllegalArgumentException, DateTimeParseException {
        switch (format) {
            case _DATE_TIME_FORMAT_MILLISEC_ZONE_OFFSET:
            case _DATE_TIME_FORMAT_SECONDS_ZONE_OFFSET:
            case _DATE_TIME_FORMAT_NANOSEC_ZONE_OFFSET:
            case _DATE_TIME_FORMAT_NANOSEC_ZONE_OFFSET_ID:
                return ZonedDateTime.parse(dateTime);
            case _DATE_TIME_FORMAT_SECONDS:
                LocalDateTime localDateTime = LocalDateTime.parse(dateTime);
                return ZonedDateTime.of(localDateTime, ZoneId.systemDefault()).withFixedOffsetZone();
            case _DATE_FORMAT_DATE_ONLY:
                LocalDate localDate = LocalDate.parse(dateTime);
                LocalTime localTime = LocalTime.of(0, 0);
                return ZonedDateTime.of(localDate, localTime, ZoneId.systemDefault()).withFixedOffsetZone();
            case _TIME_FORMAT_TIME_ONLY:
                localTime = LocalTime.parse(dateTime);
                localDate = LocalDate.ofEpochDay(0); //this date part SHOULD NOT be used later on
                return ZonedDateTime.of(localDate, localTime, ZoneId.systemDefault()).withFixedOffsetZone();
            case _CUSTOM_DATE_TIME_FORMAT_LV:
                String datePartFromDateTime = MS_StringUtils.substring(dateTime, 0, 10);
                String timePartFromDateTime = MS_StringUtils.substring(dateTime, 11, 23);
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(_CUSTOM_DATE_FORMAT_LV);
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(_CUSTOM_TIME_FORMAT_LV);
                localDate = LocalDate.parse(datePartFromDateTime, dateFormatter);
                localTime = LocalTime.parse(timePartFromDateTime, timeFormatter);
                return ZonedDateTime.of(localDate, localTime, ZoneId.systemDefault()).withFixedOffsetZone();
            default:
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                return ZonedDateTime.parse(dateTime, formatter);
        }
    }

    /**
     * Returns zone offset as text from date time object.
     *
     * @param dateTime zoned date time object.
     * @return +03:00 for Europe/Helsinki.
     */
    public static String getZoneOffsetText(ZonedDateTime dateTime) {
        ZoneRules zoneRules = dateTime.getZone().getRules();
        return zoneRules.getOffset(dateTime.toInstant()).getId();
    }

    /**
     * Returns zone offset as text from date time object.
     *
     * @param dateTime zoned date time object.
     * @return +03:00 for Europe/Helsinki.
     */
    public static String getZoneIdText(ZonedDateTime dateTime) {
        return dateTime.getZone().getId();
    }
}