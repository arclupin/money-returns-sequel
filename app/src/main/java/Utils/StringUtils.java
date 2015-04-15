package Utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Various useful methods applying for data manipulation and validation
 *
 * Created by Thanh on 20-Mar-15.
 */
public class StringUtils {
public static final String POUND_SIGN = "\u00A3";


    /**
     * check whether a string is empty (without any visible characters) or not
     * @param input the input string
     * @return the result
     */
   public static boolean isFieldEmpty(String input) {
        return (input == null) || input.trim().isEmpty();
    }


    /**
     * get a Date object from a string or throw {@link ParseException} if the string can't be parsed
     * to a date
     * @param response the input string
     * @return the output Date object
     */
   public static Date getDateTimeFromServerDateResponse(String response) {
       Date date = null;
       try {
           date =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(response);
       }
       catch (ParseException e)
       {
           Log.e("date parsing error", e.getMessage(), e);
       }
       return date;
   }

    /**
     * get a Date object from a string (<u>Date</u> only) or throw {@link ParseException} if the
     * string can't be parsed to a date
     * @param response the input string
     * @return the output Date object
     */
    public static Date getDateFromServerDateResponse(String response) {
        Date date = null;
        String raw = response.trim();
        if (raw.equalsIgnoreCase("null"))
            return null;
        try {
            date =  new SimpleDateFormat("yyyy-MM-dd").parse(raw);
        }
        catch (ParseException e)
        {
            Log.e("date parsing error", e.getMessage(), e);
        }
        return date;
    }

    /**
     * get String date in desired format (also checks if the input string represent a valid date)
     * @param r the input string
     * @return the output
     */
    public static String getStringDate(String r, String currentFormat, String newFormat)
    {
        SimpleDateFormat format = new SimpleDateFormat(newFormat);
        try {
            return format.format(new SimpleDateFormat(currentFormat).parse(r)); // quite long winded
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * Return a simple string representing this date object in dd-MM-yyyy format
     * @param date
     * @return
     */
    public static String getGeneralDateString(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        StringBuilder dateString = new StringBuilder();
        dateString.append(calendar.get(Calendar.DAY_OF_MONTH)).append("-")
                .append(calendar.get(Calendar.MONTH) + 1).append("-")
                .append(calendar.get(Calendar.YEAR));

        return dateString.toString();
    }


    /**
     * get a date from a string
     * @param format the format of the date string
     * @param date the date string
     * @return the date parsed from the string
     */
    public static Date getDateFromString(String format, String date) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * return a string represenation of the format specified from the date input
     * @param date the date input
     * @param format the format
     * @return the string result
     */
    public static String getStringFromDate(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    /**
     * round a double to the correct format (2 decimal digits)
     * @param amount the double input
     * @return the rounded to 2 decimal places value
     */
    public static double roundAmount(double amount) {
        return Double.parseDouble(String.format("%.2f", amount));
    }



    /**
     * check whether a string represents a date in format dd/mm/yyyy or not
     * @param s the input string
     * @return the result
     */
    public static boolean isStringADate(String s) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        format.setLenient(false);
        try {
            format.parse(s);
            return true;
        } catch (ParseException e) {
           return false;
        }
    }



    /**
     * check whether this date has already passed
     * @param d the input string
     * @return the result
     */
    public static boolean hasDatePassed(Date d) {
        return new GregorianCalendar().getTime().compareTo(d) > 0;
    }

    /**
     * implode all strings using the glue as the separator
     * @param glue the divider between strings
     * @param strings the strings to be combined
     * @return the result string
     */
    public static String implode(String glue, String...strings) {
        String r = "";
        for (int i = 0; i < strings.length; i++) {
            r += strings[i] + (i == strings.length - 1 ? "" : glue);
        }
        return r;
    }

    /**
     * Check whether a string is a valid amount of money, that is, is string of format %.2f
     * @param s the input string to be tested
     * @return the result
     */
    public static boolean isStringValidAmount(String s) {
        return s.matches("^\\d+(\\.\\d{1,2})?");
    }

    /**
     * Get shortened string in form "abc..."
     * @param string the input string
     * @param limit the limit of the length
     */
    public static String getShortenedString(String string, int limit) {
        if (string.length() <= limit + 3) // 3 dots consume space as well, so need taking into account
            return string;
        else {
            return string.substring(0, limit) + "...";
        }

    }
}
