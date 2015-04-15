package Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ncl.team5.lloydsmockup.CustomMessageBox;
import com.ncl.team5.lloydsmockup.HouseShare_Bill_Member;
import com.ncl.team5.lloydsmockup.Houseshare_Payments;
import com.ncl.team5.lloydsmockup.IntentConstants;
import com.ncl.team5.lloydsmockup.R;

import java.util.Map;

import Utils.StringUtils;

/**
 * Dialog for confirming the creation of a bill
 *
 * Created by Thanh on 10-April-15.
 */
public class HS_Payment_Method_Dialog extends DialogFragment{

    public interface PaymentMethodDialogListener {

        // methods needed to interact with the host activity
        void onMethodChosenClicked(HS_Payment_Method_Dialog f, int which, String other);
    }

    private PaymentMethodDialogListener mListener;

    // assign the event listener to the host activity
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (PaymentMethodDialogListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement PaymentMethodDialogListener");
        }
    }

    // static factory
    public static HS_Payment_Method_Dialog initialise() {
        HS_Payment_Method_Dialog o = new HS_Payment_Method_Dialog();
        return o;
    }

    @Override
    public Dialog onCreateDialog(Bundle saveState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_payment_method, null);
        TextView bank = (TextView) v.findViewById(R.id.bank_transfer);
        bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onMethodChosenClicked(HS_Payment_Method_Dialog.this,
                        Houseshare_Payments.BANK_TRANSFER, null);
            }
        });

        TextView cash = (TextView) v.findViewById(R.id.cash);
        cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onMethodChosenClicked(HS_Payment_Method_Dialog.this, Houseshare_Payments.CASH,
                        null);
            }
        });

        //set listeners for buttons
        v.findViewById(R.id.dialog_okay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View tv) {
                String other_method = ((EditText) v.findViewById(R.id.other_method_value))
                        .getText().toString();
                if (StringUtils.isFieldEmpty(other_method))
                    new CustomMessageBox.MessageBoxBuilder(getActivity(),
                            "Please specify the name of the method.").build();
               else {
                mListener.onMethodChosenClicked(HS_Payment_Method_Dialog.this,
                        Houseshare_Payments.OTHER_METHODS, other_method);
                }
        }});
        v.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              HS_Payment_Method_Dialog.this.dismiss();

            }
        });
        builder.setView(v);
        return builder.create();
    }
}
