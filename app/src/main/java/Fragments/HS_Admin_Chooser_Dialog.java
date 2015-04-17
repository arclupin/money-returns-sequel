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

import java.util.ArrayList;

import Utils.StringUtils;

/**
 * Dialog showing the members of this house
 * <p/>
 * Created by Thanh on 10-April-15.
 */
public class HS_Admin_Chooser_Dialog extends DialogFragment {
private AdminChooserDialogListener mListener;


    public interface AdminChooserDialogListener {
        void onAdminChosen(HS_Admin_Chooser_Dialog f,  String hsid);
    }

    // static factory
    public static HS_Admin_Chooser_Dialog initialise(ArrayList<Member> members, String username) {
        HS_Admin_Chooser_Dialog o = new HS_Admin_Chooser_Dialog();
        Bundle b = new Bundle();
        b.putParcelableArrayList(IntentConstants.MEMBERS, members);
        b.putString(IntentConstants.USERNAME, username);

        o.setArguments(b);
        return o;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (AdminChooserDialogListener) activity;
        }
        catch (IllegalStateException e)
        {
            throw  new IllegalStateException("Illegal cast");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle saveState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        RelativeLayout v = (RelativeLayout) inflater.inflate(R.layout.dialog_admin_chooser, null);
        TableLayout l = (TableLayout) v.findViewById(R.id.members_table);
        final ArrayList<Member> members = getArguments().getParcelableArrayList(IntentConstants.MEMBERS);

        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).getUsername().equals(getArguments().getString(IntentConstants.USERNAME)))
                continue;
            View view = inflater.inflate(R.layout.hs_member_row, null);
            ((TextView) view.findViewById(R.id.member_name)).setText(members.get(i).getUsername());
            ((TextView) view.findViewById(R.id.joined_since)).setText
                    (StringUtils.getStringDate(members.get(i).getJoined_since(), "yyyy-MM-dd", "dd/MM/yyyy"));
            final String hs_id = members.get(i).getHouseshare_id();
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onAdminChosen(HS_Admin_Chooser_Dialog.this, hs_id);
                }
            });
            // add the sub bill view to the table
            l.addView(view);
        }

        //set listeners for buttons
        v.findViewById(R.id.dialog_okay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HS_Admin_Chooser_Dialog.this.dismiss();
            }
        });
        builder.setView(v);
        Dialog d = builder.create();
        d.setCancelable(false);
        return d;
    }
}
