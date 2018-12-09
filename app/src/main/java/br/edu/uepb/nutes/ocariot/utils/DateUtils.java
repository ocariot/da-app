package br.edu.uepb.nutes.ocariot.utils;

import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Provides useful methods for handling date/time.
 *
 * @author @copyright Copyright (c) 2017, NUTES UEPB
 */
public final class DateUtils {
    public static final String DATE_FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    /**
     * Get/Retrieve calendar instance.
     *
     * @return The Calendar.
     */
    public static Calendar getCalendar() {
        return GregorianCalendar.getInstance();
    }

    /**
     * Returns the current datetime in format of format_date string passed as parameter.
     * If "format_date" is null the default value: "yyyy-MM-dd HH:mm:ss" will be used.
     *
     * @param format_date The datetime format
     * @return Datetime formatted
     */
    public static String getCurrentDatetime(String format_date) {
        if (format_date == null)
            format_date = "yyyy-MM-dd HH:mm:ss";

        Calendar calendar = GregorianCalendar.getInstance();

        DateFormat dateFormat = new SimpleDateFormat(format_date, Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    /**
     * Returns the current datetime in milliseconds.
     *
     * @return long
     */
    private static long getCurrentDatetime() {
        return GregorianCalendar.getInstance().getTimeInMillis();
    }

    /**
     * Returns the current timestamp.
     * - millis - long
     * - timezone - Object (TimeZone)
     * - value - String (yyyy-MM-dd HH:mm:ss)
     *
     * @return JSONObject
     * @throws JSONException
     */
    public static JSONObject getCurrentTimestamp() throws JSONException {
        return new JSONObject().put("timezone", TimeZone.getDefault())
                .put("mills", DateUtils.getCurrentDatetime())
                .put("value", DateUtils.getCurrentDatetime(null)); // yyyy-MM-dd HH:mm:ss
    }

    /**
     * Retrieve a date/time passed in milliseconds to the format passed as a parameter.
     *
     * @param milliseconds long
     * @param format_date  String
     * @return String
     */
    public static String formatDate(long milliseconds, String format_date) {
        if (format_date == null)
            return null;

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        DateFormat dateFormat = new SimpleDateFormat(format_date, Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    /**
     * Convert string date in string format.
     *
     * @param date        {@link String}
     * @param format_date {@link String}
     * @return String
     */
    public static String formatDate(String date, @Nullable String format_date) {
        if (format_date == null || format_date.length() == 0)
            format_date = "yyyy-MM-dd";

        return getFormatDataTime(date, format_date, true, false);
    }

    /**
     * Convert string date in string format.
     *
     * @param date        {@link String}
     * @param format_date {@link String}
     * @return String
     */
    public static String formatDateHour(String date, @Nullable String format_date) {
        if (format_date == null || format_date.length() == 0)
            format_date = "HH:mm:ss";

        return getFormatDataTime(date, format_date, false, true);
    }

    private static String getFormatDataTime(String date_input, String format_date, boolean date, boolean time) {
        String result = "";

        if (date && !time) {
            result = getDataTime(Integer.parseInt(date_input.substring(0, 4)),
                    Integer.parseInt(date_input.substring(5, 7)) - 1,
                    Integer.parseInt(date_input.substring(8, 10)),
                    0, 0, format_date);
        } else if (time && !date) {
            result = getDataTime(0, 0, 0,
                    Integer.parseInt(date_input.substring(11, 13)),
                    Integer.parseInt(date_input.substring(14, 16)), format_date);
        } else {
            result = getDataTime(Integer.parseInt(date_input.substring(0, 4)),
                    Integer.parseInt(date_input.substring(5, 7)) - 1,
                    Integer.parseInt(date_input.substring(8, 10)),
                    Integer.parseInt(date_input.substring(11, 13)),
                    Integer.parseInt(date_input.substring(14, 16)),
                    format_date);
        }

        return result;
    }

    /**
     * Returns datetime according to the parameters.
     *
     * @param year        The year
     * @param month       The month
     * @param day         The day
     * @param hourOfDay   The hour
     * @param minute      The minute
     * @param format_date The format date output.
     * @return The string datetime.
     */
    private static String getDataTime(int year, int month, int day, int hourOfDay, int minute, String format_date) {
        Calendar calendar = GregorianCalendar.getInstance();
        // Value to be used for MONTH field. 0 is January
        calendar.set(year, month, day, hourOfDay, minute, 1);

        DateFormat dateFormat = new SimpleDateFormat(format_date, Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }


    /**
     * Format date in ISO 8601 for the format passed in the "format_date" parameter.
     * If the time zone is null, UTC is used.
     *
     * @param str_date    String
     * @param format_date String
     * @param timeZone    {@link TimeZone}
     * @return String
     */
    public static String formatDateISO8601(String str_date, String format_date, TimeZone timeZone) {
        if (str_date == null || format_date == null)
            return null;

        if (timeZone == null)
            timeZone = TimeZone.getTimeZone("UTC");

        DateFormat dateFormat = new SimpleDateFormat(format_date, Locale.getDefault());
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(DateUtils.fromISO8601(str_date));
    }

    /**
     * Retrieve the current date according to timezone.
     * If the time zone is null, UTC is used.
     *
     * @param timeZone {@link TimeZone}
     * @return String
     */
    public static String getCurrentDateISO8601(TimeZone timeZone) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtils.DATE_FORMAT_ISO_8601,
                Locale.getDefault());
        if (timeZone != null)
            dateFormat.setTimeZone(timeZone);
        else dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        return dateFormat.format(new Date());
    }

    /**
     * Convert string datetime in UTC ISO 8601
     *
     * @param str_date String datetime.
     * @return String
     */
    private static Date fromISO8601(String str_date) {
        if (str_date == null)
            return null;

        DateFormat dateFormat = new SimpleDateFormat(DateUtils.DATE_FORMAT_ISO_8601,
                Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            return dateFormat.parse(str_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Add days to calendar.
     *
     * @param days (-) to decrease the days (+) to advance
     * @return Calendar
     */
    public static Calendar addDays(int days) {
        Calendar c = GregorianCalendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, days);
        c.set(Calendar.HOUR_OF_DAY, 0); // 0 hours
        c.set(Calendar.MINUTE, 0); // 0 minute
        c.set(Calendar.SECOND, 1); // 1 second

        return c;
    }

    /**
     * Add days in string date.
     *
     * @param days (-) to decrease the days (+) to advance
     * @return Calendar
     */
    public static String addDaysToDateString(String date, int days) {
        Calendar c = convertStringDateToCalendar(date, null);

        c.add(Calendar.DAY_OF_YEAR, days);
        c.set(Calendar.HOUR_OF_DAY, 0); // 0 hours
        c.set(Calendar.MINUTE, 0); // 0 minute
        c.set(Calendar.SECOND, 1); // 1 second

        return formatDate(c.getTimeInMillis(), "yyyy-MM-dd");
    }

    public static Calendar convertStringDateToCalendar(String date, String format) {
        if (format == null || format.length() == 0)
            format = "yyyy-MM-dd";

        Calendar calendar = Calendar.getInstance();
        try {
            DateFormat df = new SimpleDateFormat(format, Locale.getDefault());
            calendar.setTime(df.parse(date));
        } catch (ParseException e) {
        }

        return calendar;
    }

    /**
     * Add minutes in datetime.
     *
     * @param datetime     Datetime.
     * @param milliseconds Total in milliseconds.
     * @return String
     */
    public static String addMillisecondsToString(String datetime, int milliseconds) {
        Calendar calendar = convertStringDateToCalendar(datetime, DATE_FORMAT_ISO_8601);
        calendar.add(Calendar.MILLISECOND, milliseconds);

        return formatDate(calendar.getTimeInMillis(), DATE_FORMAT_ISO_8601);
    }

    /**
     * Add minutes in datetime.
     *
     * @param datetime Datetime.
     * @param minutes  Total in minutes.
     * @return String
     */
    public static String addMinutesToString(String datetime, int minutes) {
        Calendar calendar = convertStringDateToCalendar(datetime, DATE_FORMAT_ISO_8601);
        calendar.add(Calendar.MINUTE, minutes);

        return formatDate(calendar.getTimeInMillis(), DATE_FORMAT_ISO_8601);
    }


    /**
     * returns the current year in milliseconds.
     * Example YEAR-01-01 00:00:00
     *
     * @return long
     */
    public static long getCurrentYear() {
        Calendar c = GregorianCalendar.getInstance();

        c.set(Calendar.DAY_OF_YEAR, c.getActualMinimum(Calendar.DAY_OF_YEAR));
        c.set(Calendar.HOUR_OF_DAY, c.getActualMinimum(Calendar.HOUR_OF_DAY));
        c.set(Calendar.MINUTE, c.getActualMinimum(Calendar.MINUTE));
        c.set(Calendar.SECOND, c.getActualMinimum(Calendar.SECOND));
        c.set(Calendar.MILLISECOND, c.getActualMinimum(Calendar.MILLISECOND));

        return c.getTimeInMillis();
    }

    /**
     * Validate date.
     *
     * @param date   String
     * @param format String
     * @return boolean
     */
    public static boolean isDateValid(String date, @Nullable String format) {
        if (format == null || format.length() == 0)
            format = "yyyy-MM-dd";

        try {
            DateFormat df = new SimpleDateFormat(format, Locale.getDefault());
            df.setLenient(false);
            df.parse(date);

            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Checks if the date passed as parameter is today.
     *
     * @param dateMills long
     * @return boolean
     */
    public static boolean isToday(long dateMills) {
        Calendar c1 = GregorianCalendar.getInstance();
        Calendar c2 = GregorianCalendar.getInstance();
        c1.setTimeInMillis(dateMills);
        c2.setTimeInMillis(getCurrentDatetime());

        return c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH) &&
                c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) &&
                c2.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
    }

    /**
     * Checks if the date passed as parameter is this year.
     *
     * @param dateMills long
     * @return boolean
     */
    public static boolean isYear(long dateMills) {
        Calendar c1 = GregorianCalendar.getInstance();
        Calendar c2 = GregorianCalendar.getInstance();
        c1.setTimeInMillis(dateMills);
        c2.setTimeInMillis(getCurrentDatetime());

        return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR));
    }

    /**
     * Checks if the date passed as parameter is this year.
     *
     * @param str_date String
     * @return boolean
     */
    public static boolean isYear(String str_date) {
        Calendar c1 = GregorianCalendar.getInstance();
        Calendar c2 = GregorianCalendar.getInstance();
        c2.setTimeInMillis(getCurrentDatetime());

        Calendar c = Calendar.getInstance();
        c1.setTime(DateUtils.fromISO8601(str_date));

        return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR));
    }
}
