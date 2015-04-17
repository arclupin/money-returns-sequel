package Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Class for showing a date picking dialog
 *
 * Created by Thanh on 15-Apr-15.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{


   // listener instance
    private DatePickerListener mListener;

    // host activity interacting listener
    public interface DatePickerListener {
        void onDatePicked(Date date);
    }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }


    /**
     * cast the host activity to the listner
     * @param activity the host activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (DatePickerListener) activity;
        }
        catch (Exception e) {
            Log.e("Class cast exception", e.getMessage(), e);
        }
    }

    /**
     * @param view        The view associated with this listener.
     * @param year        The year that was set.
     * @param monthOfYear The month that was set (0-11) for compatibility
     *                    with {@link Calendar}.
     * @param dayOfMonth  The day of the month that was set.
     */
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mListener.onDatePicked(new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime());
    }
}

