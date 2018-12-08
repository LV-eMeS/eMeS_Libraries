package lv.emes.libraries.utilities;

import lv.emes.libraries.tools.lists.MS_StringList;

import java.text.ParseException;
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
 * @version 1.4.
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
    public static final String _TIME_FORMAT_TIME_ONLY_HH_MM = "HH:mm";
    public static final String _DEFAULT_DATE_TIME_FORMAT = _DATE_TIME_FORMAT_SECONDS_ZONE_OFFSET;
    //Custom formats
    public static final String _CUSTOM_TIME_FORMAT_SECONDS_LV = "HH:mm:ss";
    public static final String _CUSTOM_TIME_FORMAT_LV = "HH:mm:ss:SSS";
    public static final String _CUSTOM_DATE_FORMAT_LV = "dd.MM.yyyy";
    public static final String _CUSTOM_DATE_TIME_FORMAT_LV = "dd.MM.yyyy HH:mm:ss:SSS";
    public static final String _CUSTOM_DATE_TIME_FORMAT_EN = "yyyy-MM-dd HH:mm:ss,SSS";
    public static final String _TIME_FORMAT_EN = "HH:mm:ss,SSS";

    private MS_DateTimeUtils() {
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
     * Converts date and time to text.
     *
     * @param date a date in format: "dd.MM.yyyy HH:mm:ss:SSS".
     * @return a text representing passed date and time.
     */
    public static String dateTimeToStr(Date date) {
        return dateTimeToStr(date, _CUSTOM_DATE_TIME_FORMAT_LV);
    }

    /**
     * Converts time to text.
     *
     * @param date a date in format: "HH:mm:ss:SSS".
     * @return a text representing passed time.
     */
    public static String timeToStr(Date date) {
        return dateTimeToStr(date);
    }

    /**
     * Converts date without time part to text.
     *
     * @param date a date in format: "dd.MM.yyyy".
     * @return a text representing passed date.
     */
    public static String dateToStr(Date date) {
        return dateTimeToStr(date);
    }

    public static Date parseDate(String date, String format) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.parse(date);
    }

    public static Date parseDate(String date) throws ParseException {
        return parseDate(date, _CUSTOM_DATE_TIME_FORMAT_LV);
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
     * BACKPORTED VERSION OF METHOD.
     * Converts local date and time to text in presented format.
     *
     * @param date   a date containing time part.
     * @param format format for string representation of date.
     * @return a text representing passed date and time.
     */
    public static String dateTimeToStr(org.threeten.bp.LocalDateTime date, String format) {
        org.threeten.bp.format.DateTimeFormatter formatter = org.threeten.bp.format.DateTimeFormatter.ofPattern(format);
        return date.format(formatter);
    }

    /**
     * BACKPORTED VERSION OF METHOD.
     * Converts local date and time to text.
     *
     * @param date a date containing time part in format: "dd.MM.yyyy HH:mm:ss:SSS".
     * @return a text representing passed date and time.
     */
    public static String dateTimeToStr(org.threeten.bp.LocalDateTime date) {
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
     * @throws IllegalArgumentException in case <b>format</b> is illegal for formatter to handle formatting.
     * @throws DateTimeParseException   in case date <b>dateTime</b> couldn't be parsed in given format <b>format</b>.
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
            case _TIME_FORMAT_TIME_ONLY_HH_MM:
                //1. fix passed time if it's missing leading zeros, like, e.g. "1:4:3", which actually is "01:04:03"
                MS_StringList parts = new MS_StringList(dateTime, ':');
                if (parts.size() > 1) {
                    parts.forEachItem((timePart, i) -> {
                        if (timePart.length() == 1)
                            parts.edit(i, "0" + timePart);
                    });
                    dateTime = parts.toStringWithNoLastDelimiter();
                } //if there is no at least 2 elements (like hour and minute part) then string is wrong - let parsing part fail immediately

                //2. try to parse time as string
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
     * BACKPORTED VERSION OF METHOD.
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
    public static String formatDateTime(org.threeten.bp.ZonedDateTime dateTime, String format) {
        if (dateTime == null || format == null) return null;
        org.threeten.bp.format.DateTimeFormatter formatter = org.threeten.bp.format.DateTimeFormatter.ofPattern(format);
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
     * @throws IllegalArgumentException in case <b>format</b> is illegal for formatter to handle formatting.
     * @throws DateTimeParseException   in case date <b>dateTime</b> couldn't be parsed in given format <b>format</b>.
     */
    public static org.threeten.bp.ZonedDateTime formatDateTimeBackported(String dateTime, String format) throws IllegalArgumentException, DateTimeParseException {
        switch (format) {
            case _DATE_TIME_FORMAT_MILLISEC_ZONE_OFFSET:
            case _DATE_TIME_FORMAT_SECONDS_ZONE_OFFSET:
            case _DATE_TIME_FORMAT_NANOSEC_ZONE_OFFSET:
            case _DATE_TIME_FORMAT_NANOSEC_ZONE_OFFSET_ID:
                return org.threeten.bp.ZonedDateTime.parse(dateTime);
            case _DATE_TIME_FORMAT_SECONDS:
                org.threeten.bp.LocalDateTime localDateTime = org.threeten.bp.LocalDateTime.parse(dateTime);
                return org.threeten.bp.ZonedDateTime.of(localDateTime, org.threeten.bp.ZoneId.systemDefault()).withFixedOffsetZone();
            case _DATE_FORMAT_DATE_ONLY:
                org.threeten.bp.LocalDate localDate = org.threeten.bp.LocalDate.parse(dateTime);
                org.threeten.bp.LocalTime localTime = org.threeten.bp.LocalTime.of(0, 0);
                return org.threeten.bp.ZonedDateTime.of(localDate, localTime, org.threeten.bp.ZoneId.systemDefault()).withFixedOffsetZone();
            case _TIME_FORMAT_TIME_ONLY:
            case _TIME_FORMAT_TIME_ONLY_HH_MM:
                //1. fix passed time if it's missing leading zeros, like, e.g. "1:4:3", which actually is "01:04:03"
                MS_StringList parts = new MS_StringList(dateTime, ':');
                if (parts.size() > 1) {
                    parts.forEachItem((timePart, i) -> {
                        if (timePart.length() == 1)
                            parts.edit(i, "0" + timePart);
                    });
                    dateTime = parts.toStringWithNoLastDelimiter();
                } //if there is no at least 2 elements (like hour and minute part) then string is wrong - let parsing part fail immediately

                //2. try to parse time as string
                localTime = org.threeten.bp.LocalTime.parse(dateTime);
                localDate = org.threeten.bp.LocalDate.ofEpochDay(0); //this date part SHOULD NOT be used later on
                return org.threeten.bp.ZonedDateTime.of(localDate, localTime, org.threeten.bp.ZoneId.systemDefault()).withFixedOffsetZone();
            case _CUSTOM_DATE_TIME_FORMAT_LV:
                String datePartFromDateTime = MS_StringUtils.substring(dateTime, 0, 10);
                String timePartFromDateTime = MS_StringUtils.substring(dateTime, 11, 23);
                org.threeten.bp.format.DateTimeFormatter dateFormatter = org.threeten.bp.format.DateTimeFormatter.ofPattern(_CUSTOM_DATE_FORMAT_LV);
                org.threeten.bp.format.DateTimeFormatter timeFormatter = org.threeten.bp.format.DateTimeFormatter.ofPattern(_CUSTOM_TIME_FORMAT_LV);
                localDate = org.threeten.bp.LocalDate.parse(datePartFromDateTime, dateFormatter);
                localTime = org.threeten.bp.LocalTime.parse(timePartFromDateTime, timeFormatter);
                return org.threeten.bp.ZonedDateTime.of(localDate, localTime, org.threeten.bp.ZoneId.systemDefault()).withFixedOffsetZone();
            default:
                org.threeten.bp.format.DateTimeFormatter formatter = org.threeten.bp.format.DateTimeFormatter.ofPattern(format);
                return org.threeten.bp.ZonedDateTime.parse(dateTime, formatter);
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

    public static Instant instantFromBackported(org.threeten.bp.Instant backported) {
        return Instant.ofEpochSecond(backported.getEpochSecond(), backported.getNano());
    }

    public static org.threeten.bp.Instant instantToBackported(Instant instant) {
        return org.threeten.bp.Instant.ofEpochSecond(instant.getEpochSecond(), instant.getNano());
    }

    public static Date zonedDateTimeToDate(ZonedDateTime zdt) {
        return new Date(zdt.toInstant().toEpochMilli());
    }

    public static class ZonedDateTimeBuilder {

        private int year = 1970;
        private int month = 1;
        private int dayOfMonth = 1;
        private int hour = 0;
        private int minute = 0;
        private int second = 0;
        private int nanoOfSecond = 0;
        private ZoneId zone = ZoneId.systemDefault();

        public ZonedDateTimeBuilder() {
        }

        public static ZonedDateTimeBuilder newBuilder() {
            return new ZonedDateTimeBuilder();
        }

        public ZonedDateTimeBuilder withYear(int year) {
            this.year = year;
            return this;
        }

        public ZonedDateTimeBuilder withMonth(int month) {
            this.month = month;
            return this;
        }

        public ZonedDateTimeBuilder withDayOfMonth(int dayOfMonth) {
            this.dayOfMonth = dayOfMonth;
            return this;
        }

        public ZonedDateTimeBuilder withHour(int hour) {
            this.hour = hour;
            return this;
        }

        public ZonedDateTimeBuilder withMinute(int minute) {
            this.minute = minute;
            return this;
        }

        public ZonedDateTimeBuilder withSecond(int second) {
            this.second = second;
            return this;
        }

        public ZonedDateTimeBuilder withNanoSecond(int nanoOfSecond) {
            this.nanoOfSecond = nanoOfSecond;
            return this;
        }

        public ZonedDateTimeBuilder withZone(ZoneId zone) {
            if (zone != null) this.zone = zone;
            return this;
        }

        public ZonedDateTimeBuilder from(ZonedDateTime other) {
            this.year = other.getYear();
            this.month = other.getMonthValue();
            this.dayOfMonth = other.getDayOfMonth();
            this.hour = other.getHour();
            this.minute = other.getMinute();
            this.second = other.getSecond();
            this.nanoOfSecond = other.getNano();
            this.zone = other.getZone();
            return this;
        }

        public ZonedDateTime build() {
            return ZonedDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond, zone);
        }
    }
}