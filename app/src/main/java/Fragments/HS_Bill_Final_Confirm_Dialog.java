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
 * Dialog for concluding a bill
 *
 *
 * Created by Thanh on 23-Mar-15.
 *
 */
public class HS_Bill_Final_Confirm_Dialog extends DialogFragment{

    public interface FinalConfirmBillDialogListener {

        /**
         * The bill owner confirms that all of the shares have been paid and this bill should be concluded.
         * @param f the dialog itself (for dismissing the bill)
         */
        public void onFinalConfirmClick(HS_Bill_Final_Confirm_Dialog f, Bill bill);
    }

    private FinalConfirmBillDialogListener mListener;

    // assign the event listener to the host activity
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (FinalConfirmBillDialogListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement FinalConfirmBillDialogListener");
        }
    }


    public static HS_Bill_Final_Confirm_Dialog initialise(Bill bill) {
        HS_Bill_Final_Confirm_Dialog o = new HS_Bill_Final_Confirm_Dialog();
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
        ((TextView) v.findViewById(R.id.title)).setText("Finalising this bill");
        ((TextView) v.findViewById(R.id.content)).setText("Would you like confirm this bill as paid?" +
                "\nAll members will be notified and the bill will be deactivated.");
        v.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HS_Bill_Final_Confirm_Dialog.this.dismiss();
            }
        });

        (v.findViewById(R.id.dialog_okay)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onFinalConfirmClick(HS_Bill_Final_Confirm_Dialog.this,
                        (Bill) getArguments().getParcelable(IntentConstants.BILL));
            }
        });
        builder.setView(v);
        return builder.create();
    }
}
