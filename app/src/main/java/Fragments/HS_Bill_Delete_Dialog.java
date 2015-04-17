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

import com.ncl.team5.lloydsmockup.IntentConstants;
import com.ncl.team5.lloydsmockup.R;

/**
 *
 * Dialog for confirming that the user (the billl creator) wants to delete a bill
 *
 * Created by Thanh on 23-Mar-15.
 *
 */
public class HS_Bill_Delete_Dialog extends DialogFragment{
    private BillDeleteDialogListener mBillDeleteDialogListener;

    public interface BillDeleteDialogListener {

        /**
         * User confirms that he wants to delete this bill
         * Only creators have this permission
         * @param f the dialog itself (for dismission)
         *          @param billID the id of the bill
         */
        void onButtonClickDeleteBill(HS_Bill_Delete_Dialog f, String billID);
    }



    // assign the event listener to the host activity
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {

            mBillDeleteDialogListener = (BillDeleteDialogListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BillDeleteDialogListener");
        }
    }


    //static factory method
    public static HS_Bill_Delete_Dialog initialise(String billID) {
        HS_Bill_Delete_Dialog o = new HS_Bill_Delete_Dialog();
        Bundle b = new Bundle();
        b.putString(IntentConstants.BILL_ID, billID);
        o.setArguments(b);
        return o;
    }

    @Override
    public Dialog onCreateDialog(Bundle saveState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        RelativeLayout v = (RelativeLayout) inflater.inflate(R.layout.dialog_normal_fragment, null);
        ((TextView) v.findViewById(R.id.title)).setText("Confirmation");
        ((TextView) v.findViewById(R.id.content)).setText("Would you like to delete this bill permanently?");
        v.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HS_Bill_Delete_Dialog.this.dismiss();
            }
        });
        v.findViewById(R.id.dialog_okay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBillDeleteDialogListener.onButtonClickDeleteBill
                        (HS_Bill_Delete_Dialog.this, getArguments().getString(IntentConstants.BILL_ID));
            }});

        builder.setView(v);
        return builder.create();
    }
}
