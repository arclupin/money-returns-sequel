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

import com.ncl.team5.lloydsmockup.Houseshares.SubBill;
import com.ncl.team5.lloydsmockup.IntentConstants;
import com.ncl.team5.lloydsmockup.R;

import java.util.Map;
import java.util.Set;

import Utils.StringUtils;

/**
 * Dialog showing the participants
 *
 * Created by Thanh on 10-April-15.
 */
public class HS_Bill_Participants_Dialog extends DialogFragment{

    // static factory
    public static HS_Bill_Participants_Dialog initialise(String[] participants, double[] shares, boolean[] states) {
        HS_Bill_Participants_Dialog o = new HS_Bill_Participants_Dialog();
        Bundle b = new Bundle();
        b.putStringArray(IntentConstants.PARTICIPANTS, participants);
        b.putDoubleArray(IntentConstants.PARTICIPANT_SHARES, shares);
        b.putBooleanArray(IntentConstants.PARTICIPANT_STATUS, states);
        o.setArguments(b);
        return o;
    }

    @Override
    public Dialog onCreateDialog(Bundle saveState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        RelativeLayout v = (RelativeLayout) inflater.inflate(R.layout.dialog_list_bill_participants, null);
        TableLayout l = (TableLayout) v.findViewById(R.id.bill_participants);
        String[] participants = getArguments().getStringArray(IntentConstants.PARTICIPANTS);
        double[] shares = getArguments().getDoubleArray(IntentConstants.PARTICIPANT_SHARES);
        boolean[] states = getArguments().getBooleanArray(IntentConstants.PARTICIPANT_STATUS);
        for (int i = 0; i < participants.length; i++) {

            String participant = participants[i];
            boolean status = states[i];
            // craft a new row for the current sub bill
            View participant_view = inflater.inflate(R.layout.hs_participant_row, null);
            //set the data for this sub bill (name + charge)
            ((TextView) participant_view.findViewById(R.id.participant_name)).setText(participant);
            ((TextView) participant_view.findViewById(R.id.share)).
                    setText(StringUtils.POUND_SIGN + shares[i]);
            TextView status_view =  (TextView) participant_view.findViewById(R.id.participant_status);

               status_view.setText(status ? "C" : "N");
                status_view.setTextColor(status ? getResources().getColor(R.color.dark_green) :
                        getResources().getColor(android.R.color.holo_red_light));


               // add the sub bill view to the table
            l.addView(participant_view);
        }

        //set listeners for buttons
        v.findViewById(R.id.dialog_okay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HS_Bill_Participants_Dialog.this.dismiss();
            }
        });
        builder.setView(v);
        Dialog d = builder.create();
        d.setCancelable(false);
        return d;
    }
}
