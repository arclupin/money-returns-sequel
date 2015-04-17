package Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ncl.team5.lloydsmockup.IntentConstants;
import com.ncl.team5.lloydsmockup.R;

import java.util.Date;

import Utils.StringUtils;

/**
 * Dialog showing the members of this house
 *
 * Created by Thanh on 10-April-15.
 */
public class HS_Members_Dialog extends DialogFragment{

    // static factory method
    public static HS_Members_Dialog initialise(String[] members, String[] dates) {
        HS_Members_Dialog o = new HS_Members_Dialog();
        Bundle b = new Bundle();
        b.putStringArray(IntentConstants.MEMBERS, members);
        b.putStringArray(IntentConstants.DATE, dates);
        o.setArguments(b);
        return o;
    }

    @Override
    public Dialog onCreateDialog(Bundle saveState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        RelativeLayout v = (RelativeLayout) inflater.inflate(R.layout.dialog_list_members, null);
        TableLayout l = (TableLayout) v.findViewById(R.id.members_table);
        String[] members = getArguments().getStringArray(IntentConstants.MEMBERS);
        String[] dates = getArguments().getStringArray(IntentConstants.DATE);
        for (int i = 0; i < members.length; i++) {
            View view = inflater.inflate(R.layout.hs_member_row, null);
            ((TextView) view.findViewById(R.id.member_name)).setText(members[i]);
            ((TextView) view.findViewById(R.id.joined_since)).setText(dates[i]);
               // add the sub bill view to the table
            l.addView(view);
        }

        //set listeners for buttons
        v.findViewById(R.id.dialog_okay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HS_Members_Dialog.this.dismiss();
            }
        });
        builder.setView(v);
        Dialog d = builder.create();
        d.setCancelable(false);
        return d;
    }
}
