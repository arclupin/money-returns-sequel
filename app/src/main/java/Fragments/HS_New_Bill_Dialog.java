package Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.ncl.team5.lloydsmockup.R;

/**
 *
 * Dialog for confirming that the join request has been sent.
 *
 *
 * Created by Thanh on 23-Mar-15.
 *
 */
public class HS_New_Bill_Dialog extends DialogFragment{

    public static enum BILL_TYPE {AUTO, MANUAL};
    public interface NewBillDialogListener {

        /**
         * User clicks on 1 option to click a new bill
         * @param bill_type the type of the dialog
         */
        public void onNewBillOptionClicked(HS_New_Bill_Dialog f, BILL_TYPE bill_type);
    }

    private NewBillDialogListener mNewBillDialogListener;

    // assign the event listener to the host activity
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mNewBillDialogListener = (NewBillDialogListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NewBillDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle saveState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        RelativeLayout v = (RelativeLayout) inflater.inflate(R.layout.dialog_list_fragment, null);

        //set up listeners for options
        v.findViewById(R.id.bill_create_auto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNewBillDialogListener.onNewBillOptionClicked(HS_New_Bill_Dialog.this, BILL_TYPE.AUTO );

            }
        });
        v.findViewById(R.id.bill_create_manual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNewBillDialogListener.onNewBillOptionClicked(HS_New_Bill_Dialog.this, BILL_TYPE.MANUAL);

            }
        });

        builder.setView(v);
        return builder.create();
    }
}
