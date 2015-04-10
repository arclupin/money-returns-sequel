package Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ncl.team5.lloydsmockup.Houseshares.Member;
import com.ncl.team5.lloydsmockup.IntentConstants;
import com.ncl.team5.lloydsmockup.R;

import java.util.Map;

import Utils.StringUtils;

/**
 * Dialog for confirming the creation of a bill
 *
 * Created by Thanh on 10-April-15.
 */
public class HS_Bill_Confirmation_Dialog extends DialogFragment{

    public interface BillConfirmationDialogListener {

        // methods needed to interact with the host activity
        public void onBillConfirmedButtonClick(String bill_name, HS_Bill_Confirmation_Dialog f);
        public void onBillCancelButtonClick(HS_Bill_Confirmation_Dialog f);
        public Map<String, Double> getSubBills();
    }

    private BillConfirmationDialogListener mBillConfirmationDialogListener;

    // assign the event listener to the host activity
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mBillConfirmationDialogListener = (BillConfirmationDialogListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CancelDialogListener");
        }
    }

    // static factory
    public static HS_Bill_Confirmation_Dialog initialise(String bill_name, String due_date, String amount) {
        HS_Bill_Confirmation_Dialog o = new HS_Bill_Confirmation_Dialog();
        Bundle b = new Bundle();
        b.putString(IntentConstants.BILL_NAME, bill_name);
        b.putString(IntentConstants.BILL_DUE_DATE, due_date);
        b.putString(IntentConstants.BILL_AMOUNT, amount);
        // I avoid passing parcelables here for performance reason
        // (also the host activity already has a reference to involved users which is cheaper to get)

        o.setArguments(b);
        return o;
    }

    @Override
    public Dialog onCreateDialog(Bundle saveState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        RelativeLayout v = (RelativeLayout) inflater.inflate(R.layout.dialog_list_bill_confirmation, null);
        TableLayout l = (TableLayout) v.findViewById(R.id.bill_confirmation_shares_table);
        ((TextView) v.findViewById(R.id.title)).setText("Confirming " + getArguments().getString(IntentConstants.BILL_NAME));

        Map<String, Double> subbills = mBillConfirmationDialogListener.getSubBills();

        for (Map.Entry<String, Double> entry : subbills.entrySet()) {

            // craft a new row for the current sub bill
            View sub_bill = inflater.inflate(R.layout.hs_bill_confirmation_sub_bill_row, null);

            //set the data for this sub bill (name + charge)
            ((TextView) sub_bill.findViewById(R.id.username_sub_title)).setText(entry.getKey());
            ((TextView) sub_bill.findViewById(R.id.user_sub_bill_value)).setText(StringUtils.POUND_SIGN + String.format("%.2f", entry.getValue()));

            // add the sub bill view to the table
            l.addView(sub_bill);


        }

        //set basic info of the bill
        ((TextView) v.findViewById(R.id.bill_name_value)).setText(getArguments().getString(IntentConstants.BILL_NAME));
        ((TextView) v.findViewById(R.id.due_date_value)).setText(getArguments().getString(IntentConstants.BILL_DUE_DATE));
        ((TextView) v.findViewById(R.id.total_amount_value)).setText(StringUtils.POUND_SIGN + getArguments().getString(IntentConstants.BILL_AMOUNT));


        //set listeners for buttons
        v.findViewById(R.id.dialog_okay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBillConfirmationDialogListener.onBillConfirmedButtonClick(getArguments().getString(IntentConstants.BILL_NAME), HS_Bill_Confirmation_Dialog.this);
            }
        });
        v.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBillConfirmationDialogListener.onBillCancelButtonClick(HS_Bill_Confirmation_Dialog.this);

            }
        });
        builder.setView(v);
        return builder.create();
    }
}
