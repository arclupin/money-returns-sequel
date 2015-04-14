package Utils;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.EditText;

import com.ncl.team5.lloydsmockup.CustomMessageBox;

import java.util.Date;
import java.util.GregorianCalendar;

import HTTPConnect.Connection;

/**
 * Created by Thanh on 01-Apr-15. <br/>
 * Class providing some utility methods regarding time and other stuff
 */
public class Utilities {

public static long DAY_TO_MILLI = 86400000;


    public static void delay(long milli) {
        long now = System.currentTimeMillis();
        long end = now + milli;

        while (now < end) {
            now = System.currentTimeMillis();
        }
        return;
    }

    /**
     * DElay until the timeStamp
     * @param timeStamp some time in the future
     */
    public static void delayUntil(long timeStamp) {
        long now = System.currentTimeMillis();
        while (now < timeStamp) {
            now = System.currentTimeMillis();
        }
    }

    /**
     * register a validator for an edit text
     * @param text the edit text
     * @param validator the validator
     */
    public static void registerValidator(EditText text, final Validator validator) {
        text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText view = (EditText) v;
                String input = view.getText().toString();
                //the edit text is losing focus
                if (!hasFocus) {
                    if (!validator.isDataValid(input)) {
                        view.setTextColor(Color.parseColor("#ffff4444")); // indicate data invalidity by text color change (holo red light used)
                    }
                }
                else // the edit text is having focus
                    view.setTextColor(Color.parseColor("#323842")); // very dark grey
            }
        });
    }

    /**
     * get the time remaining in days
     *
     * @param dueDate the due date
     * @return the time diff in days
     */
    public static long getDaysLeftUntilDueDate(Date dueDate) {
        Date now = new GregorianCalendar().getTime(); // get time now
        long timeDiffInDays = (dueDate.getTime() - now.getTime()) / (DAY_TO_MILLI); // get time diff in hours
        return timeDiffInDays;
    }

    /**
     * Display a dialog telling the user that his session has been expired
     * @param a the activity from which this dialog is called
     * @param username the username
     */
    public static void showAutoLogoutDialog(Activity a, final String username) {
        final Connection temp_connect = new Connection(a);
        // experimenting a new message box builder
        CustomMessageBox.MessageBoxBuilder builder =
                new CustomMessageBox.MessageBoxBuilder(a,
                        "Your session has been timed out, please login again");
        builder.setTitle("Expired")
                .setActionOnClick(new CustomMessageBox.ToClick() {
                    @Override
                    public void DoOnClick() {
                        temp_connect.autoLogout(username);
                    }
                }).build();
    }


 }
