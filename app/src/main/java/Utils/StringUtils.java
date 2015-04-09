package Utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Thanh on 20-Mar-15.
 */
public class StringUtils {

    // check whether the field is empty (no characters or all chars are whitespaces)
   public static boolean isFieldEmpty(String input) {
        if (input == null)
            return true;
       return input.trim().isEmpty();
    }

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

    public static String implode(String glue, String...strings) {
        String r = "";
        for (int i = 0; i < strings.length; i++) {
            r += strings[i] + (i == strings.length - 1 ? "" : glue);
        }
        return r;
    }

}
