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
        if (input == null)
            return true;
       return input.trim().isEmpty();
    }


    /**
     * get a Date object from a string or throw {@link ParseException} if the string can't be parsed to a date
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
     * get a Date object from a string (<u>Date</u> only) or throw {@link ParseException} if the string can't be parsed to a date
     * @param response the input string
     * @return the output Date object
     */
    public static Date getDateFromServerDateResponse(String response) {
        Date date = null;
        try {
            date =  new SimpleDateFormat("yyyy-MM-dd").parse(response);
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
     * round a double to the correct format (2 decimal digits)
     * @param amount
     * @return
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

}
