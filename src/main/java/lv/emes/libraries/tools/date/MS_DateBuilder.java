package lv.emes.libraries.tools.date;

import lv.emes.libraries.utilities.MS_DateTimeUtils;

import java.time.ZonedDateTime;

/**
 * Builds ZonedDateTime object from text parts.
 *
 * @author eMeS
 * @see ZonedDateTime
 * @version 1.0.
 */
public class MS_DateBuilder {

    private ZonedDateTime date;
    private String format = MS_DateTimeUtils._DATE_FORMAT_DATE_ONLY;
    private String prefix = "";
    private Integer quantity = 0;
    private MS_TimeUnitEnum timeUnit = MS_TimeUnitEnum.DAY;

    public MS_DateBuilder() {
        this.date = MS_DateTimeUtils.getCurrentDateTimeNow();
    }

    public MS_DateBuilder(ZonedDateTime referenceDate) {
        this.date = referenceDate;
    }

    public MS_DateBuilder(MS_DateBuilder anotherBuilder) {
        this.date = anotherBuilder.date;
        this.format = anotherBuilder.format;
        this.prefix = anotherBuilder.prefix;
        this.quantity = anotherBuilder.quantity;
        this.timeUnit = anotherBuilder.timeUnit;
    }

    /**
     * Sets builder's date format for resulting string when <b>build</b> method will be called.
     * @param dateFormat some date format representing date and / or time part.
     * @return reference to a builder itself.
     */
    public MS_DateBuilder withFormat(String dateFormat) {
        format = dateFormat;
        return this;
    }

    /**
     * @param prefix 'before' or 'after' (case insensitive, because it will be anyways converted to uppercase).
     * @return reference to a builder itself.
     */
    public MS_DateBuilder withPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    /**
     * Sets builder's quantity of time unit.
     * @param quantity when <b>build</b> method will be called
     *                 positive quantity will add currently set time unit to current date,
     *                 negative quantity will decrease current date for specified <b>quantity</b>.
     * @return reference to a builder itself.
     */
    public MS_DateBuilder withQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    /**
     * Sets builder's time unit such as second, minute, hour, day, week, month or year.
     * @param timeUnit text matching <b>MS_TimeUnitEnum</b> enumeration value (case insensitive, because it will be anyways converted to uppercase).
     * @return reference to a builder itself.
     */
    public MS_DateBuilder withTimeUnit(String timeUnit) {
        timeUnit = timeUnit.toUpperCase();
        this.timeUnit = Enum.valueOf(MS_TimeUnitEnum.class, timeUnit);
        return this;
    }

    private void throwInvalidTextException(String inputText) {
        throw new MS_ConversionException("Invalid text '"+inputText+"' representing date parts.\n" +
                "Example of correct date: [today][yesterday][tomorrow][after 2 days][before 1 second]");
    }

    /**
     * Prepares builder with parameters from presented <b>textWithDateParts</b>.
     * @param textWithDateParts text in format 'PREF QUANT TIME_UNIT' (case insensitive, because it will be anyways converted to uppercase).
     *                          PREF - 'before' or 'after';
     * @return reference to a builder itself.
     */
    public MS_DateBuilder fromText(String textWithDateParts) {
        String[] parts = textWithDateParts.toUpperCase().trim().split("\\s+");
            if (parts.length == 1) {
                //yesterday, today or tomorrow
                switch (parts[0]) {
                    case "YESTERDAY":quantity = -1; break;
                    case "TOMORROW":quantity = 1; break;
                    case "TODAY": break;
                    default: throwInvalidTextException(textWithDateParts);
                }
            } else if (parts.length == 3) {
                try {
                    this.withPrefix(parts[0]).withQuantity(Integer.parseInt(parts[1])).withTimeUnit(parts[2]);
                } catch (Exception e) {
                    throwInvalidTextException(textWithDateParts);
                }
            } else {
                throwInvalidTextException(textWithDateParts);
            }
        return this;
    }

    /**
     * Builds date as text from previously set parameters (prefix, quantity and timeUnit).
     * Date is formed in format according to <b>format</b> that have been set.
     * @return date in format according to <b>format</b>.
     */
    public MS_DateBuilder build() {
        prefix = prefix.toUpperCase();
        if (prefix.equals("BEFORE")) {
            quantity = quantity * -1;
        }

        switch (timeUnit) {
            case DAY:
            case DAYS:
                date = date.plusDays(quantity);
                break;
            case WEEK:
            case WEEKS:
                date = date.plusWeeks(quantity);
                break;
            case MONTH:
            case MONTHS:
                date = date.plusMonths(quantity);
                break;
            case YEAR:
            case YEARS:
                date = date.plusYears(quantity);
                break;
            case SECOND:
            case SECONDS:
                date = date.plusSeconds(quantity);
                break;
            case MINUTE:
            case MINUTES:
                date = date.plusMinutes(quantity);
                break;
            case HOUR:
            case HOURS:
                date = date.plusHours(quantity);
                break;
        }
        return this;
    }

    public String string() {
        return MS_DateTimeUtils.formatDateTime(date, format);
    }

    public ZonedDateTime dateTime() {
        return date;
    }
}
