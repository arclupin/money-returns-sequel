package Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ncl.team5.lloydsmockup.Houseshares.Payment;
import com.ncl.team5.lloydsmockup.IntentConstants;
import com.ncl.team5.lloydsmockup.R;

import Utils.StringUtils;

/**
 * Dialog showing the confirmed and unconfirmed
 *
 * Created by Thanh on 10-April-15.
 */
public class HS_Bill_Payments_Dialog extends DialogFragment {

    interface BillPaymentConfirmationListener {
        void onUnconfirmedPaymentClicked(HS_Bill_Payments_Dialog f, Payment p,
                                         String username, String houseshareID, String billID);
    }

    // static factory
    public static HS_Bill_Payments_Dialog initialise(Payment[] payments, String[] users) {
        HS_Bill_Payments_Dialog o = new HS_Bill_Payments_Dialog();
        Bundle b = new Bundle();
        b.putParcelableArray(IntentConstants.PAYMENTS, payments);
        b.putStringArray(IntentConstants.PARTICIPANTS, users);
        o.setArguments(b);
        return o;
    }

    @Override
    public Dialog onCreateDialog(Bundle saveState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        ScrollView v = (ScrollView) inflater.inflate(R.layout.dialog_list_bill_payments, null);
        TableLayout c = (TableLayout) v.findViewById(R.id.confirmed_payments_table);
        TableLayout u = (TableLayout) v.findViewById(R.id.unconfirmed_payments_table);

        Payment[] payments = (Payment[]) getArguments().getParcelableArray(IntentConstants.PAYMENTS);
        String[] users = getArguments().getStringArray(IntentConstants.PARTICIPANTS);

        for (int i = 0; i < payments.length; i++) {
            // craft a new row for the current sub bill

            View payment_view = inflater.inflate(R.layout.hs_payment_row, null);
            //set the data for this sub bill (name + charge)
            ((TextView) payment_view.findViewById(R.id.participant_name)).setText(users[i]);
            ((TextView) payment_view.findViewById(R.id.share)).
                    setText(StringUtils.POUND_SIGN + payments[i].getAmount());
            TextView status_view = (TextView) payment_view.findViewById(R.id.payment_status);
            status_view.setText(payments[i].isConfirmed() ? "OK" : "!");
            status_view.setTextColor(payments[i].isConfirmed() ? getResources().getColor(R.color.dark_green) :
                    getResources().getColor(android.R.color.holo_red_light));
            // add the sub bill view to the table

            if (payments[i].isConfirmed()) {
                c.addView(payment_view);
            } else {
                u.addView(payment_view);
                payment_view.setClickable(true);
                payment_view.setBackground(getActivity().getResources().getDrawable(R.drawable.button_light_color));
                payment_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO show the payment confirmation page
                        Toast.makeText(getActivity(), "Show payment confirmation page", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        if (c.getChildCount() == 0) {
            View empty_view = inflater.inflate(R.layout.hs_empty_payment_row, null);
            c.addView(empty_view);
        }
        if (u.getChildCount() == 0) {
            View empty_view = inflater.inflate(R.layout.hs_empty_payment_row, null);
            u.addView(empty_view);
        }

            //set listeners for buttons
            v.findViewById(R.id.dialog_okay).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HS_Bill_Payments_Dialog.this.dismiss();
                }
            });
            builder.setView(v);
            Dialog d = builder.create();
            d.setCancelable(false);
            return d;
        }
    }

