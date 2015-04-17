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
 * Dialog for refusing a join request from a user
 *
 * Created by Thanh on 23-Mar-15.
 */
public class HS_Cancel_Request_Dialog_Fragment extends DialogFragment{

    public interface CancelRequestDialogListener {

        // might need view_id for fast access to the view invoking the call (this might prove useful in many cases I guess)
        void onCancelRequestButtonClick(String house_name, HS_Cancel_Request_Dialog_Fragment f, int view_id);
        void onCancelButtonClick(HS_Cancel_Request_Dialog_Fragment f);
    }

    private CancelRequestDialogListener mCancelRequestDialogListener;

    // assign the event listener to the host activity
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCancelRequestDialogListener = (CancelRequestDialogListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CancelDialogListener");
        }
    }

    // static factory method
    public static HS_Cancel_Request_Dialog_Fragment initialise(String house_name, int view_id) {
        HS_Cancel_Request_Dialog_Fragment o = new HS_Cancel_Request_Dialog_Fragment();
        Bundle b = new Bundle();
        b.putString(IntentConstants.HOUSE_NAME, house_name);
        b.putInt(IntentConstants.VIEW_ID, view_id);
        o.setArguments(b);
        return o;
    }

    @Override
    public Dialog onCreateDialog(Bundle saveState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        RelativeLayout v = (RelativeLayout) inflater.inflate(R.layout.dialog_normal_fragment, null);

        ((TextView) v.findViewById(R.id.title)).setText("Cancelling request");
        ((TextView) v.findViewById(R.id.content)).setText("Would you like to cancel your request to " + getArguments().getString(IntentConstants.HOUSE_NAME) + "?");
        ((TextView) v.findViewById(R.id.dialog_okay)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCancelRequestDialogListener.onCancelRequestButtonClick(getArguments().getString(IntentConstants.HOUSE_NAME), HS_Cancel_Request_Dialog_Fragment.this, getArguments().getInt(IntentConstants.VIEW_ID));
            }
        });
        ((TextView) v.findViewById(R.id.dialog_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCancelRequestDialogListener.onCancelButtonClick(HS_Cancel_Request_Dialog_Fragment.this);

            }
        });
        builder.setView(v);
        return builder.create();
    }
}
