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
 * Dialog for confirming that the join request has been sent.
 *
 *
 * Created by Thanh on 23-Mar-15.
 *
 */
public class HS_Bill_Primary_Action_Dialog extends DialogFragment{

    public interface BillPrimaryActionDialogListener {

        /**
         * User confirms this bill or activate this bill (in case of bill owners)
         * @param f the dialog itself (for dismissing the bill)
         * @param bill the bill to be confirmed
         */
        public void onButtonClickConfirmBill(HS_Bill_Primary_Action_Dialog f, Bill bill);
    }

    private BillPrimaryActionDialogListener mBillPrimaryActionDialogListener;

    // assign the event listener to the host activity
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mBillPrimaryActionDialogListener = (BillPrimaryActionDialogListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BillPrimaryActionDialogListener");
        }
    }


    public static HS_Bill_Primary_Action_Dialog initialise(Bill bill) {
        HS_Bill_Primary_Action_Dialog o = new HS_Bill_Primary_Action_Dialog();
        Bundle b = new Bundle();
        b.putParcelable(IntentConstants.BILL, bill);
        o.setArguments(b);
        return o;
    }

    @Override
    public Dialog onCreateDialog(Bundle saveState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        RelativeLayout v = (RelativeLayout) inflater.inflate(R.layout.dialog_normal_fragment, null);
        ((TextView) v.findViewById(R.id.title)).setText("Confirmation");
        ((TextView) v.findViewById(R.id.content)).setText("Do you want to confirm this bill?");
        v.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HS_Bill_Primary_Action_Dialog.this.dismiss();
            }
        });
        ((TextView) v.findViewById(R.id.dialog_okay)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBillPrimaryActionDialogListener.onButtonClickConfirmBill
                        (HS_Bill_Primary_Action_Dialog.this,
                                (Bill) getArguments().getParcelable(IntentConstants.BILL));
            }
        });

        builder.setView(v);
        return builder.create();
    }
}
