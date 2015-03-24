package Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.ncl.team5.lloydsmockup.R;

/**
 * Created by Thanh on 23-Mar-15.
 */
public class HS_Cancel_Request_Dialog_Fragment extends DialogFragment{

    public interface CancelRequestDialogListener {

        // might need view_id for fast access to the view invoking the call (this might prove useful in many cases I guess)
        public void onCancelRequestButtonClick(String house_name, HS_Cancel_Request_Dialog_Fragment f, int view_id);
        public void onCancelButtonClick(HS_Cancel_Request_Dialog_Fragment f);
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

    // static factory
    public static HS_Cancel_Request_Dialog_Fragment initialise(String house_name, int view_id) {
        HS_Cancel_Request_Dialog_Fragment o = new HS_Cancel_Request_Dialog_Fragment();
        Bundle b = new Bundle();
        b.putString("house_name", house_name);
        b.putInt("view_id", view_id);
        o.setArguments(b);
        return o;
    }

    @Override
    public Dialog onCreateDialog(Bundle saveState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Cancel Request")
                .setMessage("Would you like to cancel your request to " + getArguments().getString("house_name") + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCancelRequestDialogListener.onCancelRequestButtonClick(getArguments().getString("house_name"), HS_Cancel_Request_Dialog_Fragment.this, getArguments().getInt("view_id"));


                    }
                })
                .setNegativeButton(R.string.houseshare_search_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCancelRequestDialogListener.onCancelButtonClick(HS_Cancel_Request_Dialog_Fragment.this);
                    }
                });

        return builder.create();
    }
}
