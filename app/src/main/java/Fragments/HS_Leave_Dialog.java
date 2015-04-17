package Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ncl.team5.lloydsmockup.Houseshares.Bill;
import com.ncl.team5.lloydsmockup.IntentConstants;
import com.ncl.team5.lloydsmockup.R;

/**
 *
 * Dialog for leaving house
 *
 * Created by Thanh on 23-Mar-15.
 *
 */
public class HS_Leave_Dialog extends DialogFragment{

    public interface HouseLeavingDialogListener {
        /**
         * The user wants to leave the house
         * @param f the dialog itself (for dismissing the bill)
         */
        void onLeavingConfirmClick(HS_Leave_Dialog f);
    }

    private HouseLeavingDialogListener mListener;

    // assign the event listener to the host activity
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (HouseLeavingDialogListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement HouseLeavingDialogListener");
        }
    }

    //static factory method
    public static HS_Leave_Dialog initialise(String housename) {
        HS_Leave_Dialog o = new HS_Leave_Dialog();
        Bundle b = new Bundle();
        b.putString(IntentConstants.HOUSE_NAME, housename);
        o.setArguments(b);
        return o;
    }

    @Override
    public Dialog onCreateDialog(Bundle saveState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        RelativeLayout v = (RelativeLayout) inflater.inflate(R.layout.dialog_normal_fragment, null);

        //set up views
        ((TextView) v.findViewById(R.id.title)).setText("Please confirm");
        ((TextView) v.findViewById(R.id.content)).setText("Would you like to leave " +
                getArguments().getString(IntentConstants.HOUSE_NAME) +
                "?\nEveryone will miss you.");
        v.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HS_Leave_Dialog.this.dismiss();
            }
        });

        (v.findViewById(R.id.dialog_okay)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLeavingConfirmClick(HS_Leave_Dialog.this);
            }
        });
        builder.setView(v);
        return builder.create();
    }
}
