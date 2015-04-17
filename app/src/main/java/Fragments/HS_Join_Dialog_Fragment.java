package Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ncl.team5.lloydsmockup.IntentConstants;
import com.ncl.team5.lloydsmockup.R;

/**
 * Dialog for confirming before the request to a house is sent.
 *
 * Created by Thanh on 23-Mar-15.
 */
public class HS_Join_Dialog_Fragment extends DialogFragment{

    public interface JoinDialogListener {
        void onJoinButtonClick(String house_name, HS_Join_Dialog_Fragment f, int view_id);
        void onCancelButtonClick(HS_Join_Dialog_Fragment f);
    }

    private JoinDialogListener mJoinDialogListener;

    // assign the event listener to the host activity
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mJoinDialogListener = (JoinDialogListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement JoinDialogListener");
        }
    }


    public static HS_Join_Dialog_Fragment initialise(String house_name, int view_id) {
        HS_Join_Dialog_Fragment o = new HS_Join_Dialog_Fragment();
        Bundle b = new Bundle();
        b.putString(IntentConstants.HOUSE_NAME, house_name);
        b.putInt(IntentConstants.VIEW_ID, view_id);
        o.setArguments(b);
        return o;
    }

    @Override
    public Dialog onCreateDialog(Bundle saveState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final String house_name = getArguments().getString(IntentConstants.HOUSE_NAME);
        Log.d("house name from frag", "/" + house_name);

        //set up views
        LayoutInflater inflater = getActivity().getLayoutInflater();
        RelativeLayout v = (RelativeLayout) inflater.inflate(R.layout.dialog_normal_fragment, null);
        ((TextView) v.findViewById(R.id.title)).setText("Confirmation");
        ((TextView) v.findViewById(R.id.content)).setText("Would you like to join " + house_name + "?");
        ((TextView) v.findViewById(R.id.dialog_okay)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mJoinDialogListener.onJoinButtonClick(house_name, HS_Join_Dialog_Fragment.this, getArguments().getInt(IntentConstants.VIEW_ID));

            }
        });
        ((TextView) v.findViewById(R.id.dialog_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mJoinDialogListener.onCancelButtonClick(HS_Join_Dialog_Fragment.this);

            }
        });
        builder.setView(v);
        return builder.create();
    }
}
