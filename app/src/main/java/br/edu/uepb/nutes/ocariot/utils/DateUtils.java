package br.edu.uepb.nutes.ocariot.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Provides useful methods for handling date/time.
 *
 * @author @copyright Copyright (c) 2017, NUTES UEPB
 */
public final class DateUtils {
    private static final String DATE_FORMAT_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private DateUtils() {
        throw new IllegalStateException("Utility class. Does not allow inheritance or instances to be created!");
    }

    private static Calendar convertStringDateToCalendar(String date, String format) {
        if (format == null || format.length() == 0) format = DATE_FORMAT;

        Calendar calendar = Calendar.getInstance();
        try {
            DateFormat df = new SimpleDateFormat(format, Locale.getDefault());
            calendar.setTime(df.parse(date));
        } catch (ParseException e) {
            return calendar;
        }
        return calendar;
    }

    /**
     * Retrieve a date/time passed in milliseconds to the format passed as a parameter.
     *
     * @param milliseconds long
     * @param formatDate   String
     * @return String
     */
    private static String formatDate(long milliseconds, String formatDate) {
        if (formatDate == null) return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        DateFormat dateFormat = new SimpleDateFormat(formatDate, Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    /**
     * Convert date time in Object Date.
     *
     * @param datetime {@link String} Date time in format yyy-MM-dd'T'HH:mm:ss
     * @return Date time in format yyy-MM-dd'T'HH:mm:ss
     */
    public static Date convertDateTime(String datetime) {
        if (datetime == null) return null;

        try {
            DateFormat formatUTC = new SimpleDateFormat(DateUtils.DATE_FORMAT_DATE_TIME, Locale.getDefault());
            return formatUTC.parse(datetime);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Convert date time in UTC format.
     *
     * @param datetime {@link String} Date time in format yyy-MM-dd'T'HH:mm:ss
     * @return Date time in format yyy-MM-dd'T'HH:mm:ss
     */
    public static String convertDateTimeToUTC(String datetime) {
        if (datetime == null) return null;

        Date dateUTC = null;
        DateFormat formatUTC = new SimpleDateFormat(DateUtils.DATE_FORMAT_DATE_TIME, Locale.getDefault());

        try {
            dateUTC = formatUTC.parse(datetime);
            formatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
        } catch (ParseException e) {
            return "";
        }

        return formatUTC.format(dateUTC);
    }

    /**
     * Convert date and time in UTC to local time and date.
     *
     * @param datetime   {@link String} Date time in format yyy-MM-dd'T'HH:mm:ss
     * @param formatDate {@link String} Output format. Default yyy-MM-dd'T'HH:mm:ss
     * @param timezone   {@link TimeZone} Your timezone
     * @return Date time in format yyy-MM-dd'T'HH:mm:ss
     */
    public static String convertDateTimeUTCToLocale(String datetime, String formatDate, TimeZone timezone) {
        if (datetime == null) return null;
        if (timezone == null) timezone = TimeZone.getDefault();
        if (formatDate == null) formatDate = DateUtils.DATE_FORMAT_DATE_TIME;

        Date dateUTC = null;
        DateFormat formatLocale = new SimpleDateFormat(formatDate, Locale.getDefault());
        formatLocale.setTimeZone(timezone);

        try {
            DateFormat utcFormat = new SimpleDateFormat(DateUtils.DATE_FORMAT_DATE_TIME, Locale.getDefault());
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            dateUTC = utcFormat.parse(datetime);
        } catch (ParseException e) {
            return "";
        }

        return formatLocale.format(dateUTC);
    }

    /**
     * Convert date and time in UTC to local time and date.
     *
     * @param datetime   {@link String} Date time in format yyy-MM-dd'T'HH:mm:ss
     * @param formatDate {@link String} Output format. Default yyy-MM-dd'T'HH:mm:ss
     * @return Date time in format yyy-MM-dd'T'HH:mm:ss
     */
    public static String convertDateTimeUTCToLocale(String datetime, String formatDate) {
        return DateUtils.convertDateTimeUTCToLocale(datetime, formatDate, null);
    }

    /**
     * Retrieve the current date according to timezone UTC.
     *
     * @return String
     */
    public static String getCurrentDate() {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(new Date());
    }

    /**
     * Retrieve the current date according to timezone UTC.
     *
     * @return String
     */
    public static String getCurrentDatetimeUTC() {
        SimpleDateFormat format = new SimpleDateFormat(DateUtils.DATE_FORMAT_DATE_TIME, Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(new Date());
    }

    /**
     * Add days in string datetime.
     *
     * @param days (-) to decrease the days (+) to advance
     * @return Calendar
     */
    public static String addDaysToDatetimeString(String date, int days) {
        Calendar c = convertStringDateToCalendar(date, DATE_FORMAT_DATE_TIME);
        c.add(Calendar.DAY_OF_YEAR, days);
        return formatDate(c.getTimeInMillis(), DATE_FORMAT_DATE_TIME);
    }

    /**
     * Add minutes in datetime.
     *
     * @param datetime     Datetime.
     * @param milliseconds Total in milliseconds.
     * @return String
     */
    public static String addMillisecondsToString(String datetime, int milliseconds) {
        Calendar calendar = convertStringDateToCalendar(datetime, DATE_FORMAT_DATE_TIME);
        calendar.add(Calendar.MILLISECOND, milliseconds);

        return formatDate(calendar.getTimeInMillis(), DATE_FORMAT_DATE_TIME);
    }

    /**
     * Add minutes in datetime.
     *
     * @param datetime Datetime.
     * @param minutes  Total in minutes.
     * @return String
     */
    public static String addMinutesToString(String datetime, int minutes) {
        Calendar calendar = convertStringDateToCalendar(datetime, DATE_FORMAT_DATE_TIME);
        calendar.add(Calendar.MINUTE, minutes);

        return formatDate(calendar.getTimeInMillis(), DATE_FORMAT_DATE_TIME);
    }

    /**
     * Add month in date.
     *
     * @param date   Date.
     * @param months Month.
     * @return String
     */
    public static String addMonths(String date, int months) {
        Calendar calendar = convertStringDateToCalendar(date, null);
        calendar.add(Calendar.MONTH, months);
        return formatDate(calendar.getTimeInMillis(), DATE_FORMAT);
    }
}
