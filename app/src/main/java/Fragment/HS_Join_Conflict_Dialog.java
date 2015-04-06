package Fragment;

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
 *
 * Dialog for showing that the user has already sent a request when attempting to send another request
 *
 *
 * Created by Thanh on 23-Mar-15.
 *
 */
public class HS_Join_Conflict_Dialog extends DialogFragment{

    public interface JoinConflictDialogListener {
        public void onPositiveButtonClick(HS_Join_Conflict_Dialog f, String username, String old_hs_name, String new_hs_name);
        public void onNegativeButtonClick(HS_Join_Conflict_Dialog f, String username, String old_hs_name, String new_hs_name);
    }

    private JoinConflictDialogListener mJoinConflictDialogListener;

    // assign the event listener to the host activity
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mJoinConflictDialogListener = (JoinConflictDialogListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement JoinDialogListener");
        }
    }


    public static HS_Join_Conflict_Dialog initialise(String old_hs_name, String new_hs_name, String username) {
        HS_Join_Conflict_Dialog o = new HS_Join_Conflict_Dialog();
        Bundle b = new Bundle();
        b.putString(IntentConstants.USERNAME, username );
        b.putString(IntentConstants.OLD_HOUSE_NAME, old_hs_name);
        b.putString(IntentConstants.NEW_HOUSE_NAME, new_hs_name);
        o.setArguments(b);
        return o;
    }

    @Override
    public Dialog onCreateDialog(Bundle saveState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final String usr = getArguments().getString(IntentConstants.USERNAME);
        final String old_hsn =  getArguments().getString(IntentConstants.OLD_HOUSE_NAME);
        final String new_hsn = getArguments().getString(IntentConstants.NEW_HOUSE_NAME);

//        builder.setTitle("Confirmation")
//                .setMessage("Your old request to "+old_hsn+" will be cancelled. \nWould you like to join " + new_hsn + " anyway?")
//                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        mJoinConflictDialogListener.onPositiveButtonClick(HS_Join_Conflict_Dialog.this, usr, old_hsn, new_hsn);
//                    }
//                })
//        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                mJoinConflictDialogListener.onNegativeButtonClick(HS_Join_Conflict_Dialog.this, usr, old_hsn, new_hsn);
//            }
//        });

        LayoutInflater inflater = getActivity().getLayoutInflater();
        RelativeLayout v = (RelativeLayout) inflater.inflate(R.layout.dialog_normal_fragment, null);
        ((TextView) v.findViewById(R.id.title)).setText("Confirmation");
        ((TextView) v.findViewById(R.id.content)).setText("Your old request to "+old_hsn+" will be cancelled. \nWould you like to join " + new_hsn + " anyway?");
        ((TextView) v.findViewById(R.id.dialog_okay)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mJoinConflictDialogListener.onPositiveButtonClick(HS_Join_Conflict_Dialog.this, usr, old_hsn, new_hsn);
        }
        });
        ((TextView) v.findViewById(R.id.dialog_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mJoinConflictDialogListener.onNegativeButtonClick(HS_Join_Conflict_Dialog.this, usr, old_hsn, new_hsn);

            }
        });
        builder.setView(v);

        return builder.create();
    }
}
