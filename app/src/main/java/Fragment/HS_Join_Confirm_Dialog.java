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
 * Dialog for confirming that the join request has been sent.
 *
 *
 * Created by Thanh on 23-Mar-15.
 *
 */
public class HS_Join_Confirm_Dialog extends DialogFragment{

    public interface JoinConfirmedDialogListener {

        /**
         * User confirms that his join request has been sent -> redirect the home page
         * @param f the dialog itself (for dismission)
         * @param username the username
         * @param hs_name the house name that user has sent request to
         */
        public void onButtonClick( HS_Join_Confirm_Dialog f, String username, String hs_name);
    }

    private JoinConfirmedDialogListener mJoinConfirmedDialogListener;

    // assign the event listener to the host activity
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mJoinConfirmedDialogListener = (JoinConfirmedDialogListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement JoinDialogListener");
        }
    }


    public static HS_Join_Confirm_Dialog initialise(String house_name, String username) {
        HS_Join_Confirm_Dialog o = new HS_Join_Confirm_Dialog();
        Bundle b = new Bundle();
        b.putString(IntentConstants.USERNAME, username );
        b.putString(IntentConstants.HOUSE_NAME, house_name);
        o.setArguments(b);
        return o;
    }

    @Override
    public Dialog onCreateDialog(Bundle saveState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        RelativeLayout v = (RelativeLayout) inflater.inflate(R.layout.dialog_normal_fragment, null);
        ((TextView) v.findViewById(R.id.title)).setText("Request sent");
        ((TextView) v.findViewById(R.id.content)).setText("Your request has been sent to the house admin. \n" +
                      "You will be notified soon.");
        v.findViewById(R.id.dialog_cancel).setVisibility(View.INVISIBLE);
        ((TextView) v.findViewById(R.id.dialog_okay)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mJoinConfirmedDialogListener.onButtonClick(HS_Join_Confirm_Dialog.this, getArguments().getString(IntentConstants.USERNAME), getArguments().getString(IntentConstants.HOUSE_NAME));
       }
        });
        builder.setView(v);
        return builder.create();
    }
}
