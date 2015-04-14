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
 *
 * Dialog for confirming that the join request has been sent.
 *
 *
 * Created by Thanh on 23-Mar-15.
 *
 */
public class HS_Bill_Delete_Dialog extends DialogFragment{

<<<<<<< HEAD
    public interface BillDeleteDialogListener {

        /**
         * User confirms that he wants to delete this bill
         * Only creators have this permission
         * @param f the dialog itself (for dismission)
         *          @param billID the id of the bill
         */
        public void onButtonClickDeleteBill(HS_Bill_Delete_Dialog f, String billID);
    }

    private BillDeleteDialogListener mBillDeleteDialogListener;
=======
    public interface CancelConfirmedDialogListener {

        /**
         * User confirms that his join request has been cancelled -> redirect the home page
         * @param f the dialog itself (for dismission)
         * @param username the username
         * @param hs_name the house name that user has sent request to
         */
        public void onButtonClickCancel(HS_Bill_Delete_Dialog f, String username, String hs_name);
    }

    private CancelConfirmedDialogListener mCancelConfirmedDialogListener;
>>>>>>> 8fd0a3671b3741c315ec32af4dab0c697336be3b

    // assign the event listener to the host activity
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
<<<<<<< HEAD
            mBillDeleteDialogListener = (BillDeleteDialogListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BillDeleteDialogListener");
=======
            mCancelConfirmedDialogListener = (CancelConfirmedDialogListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CancelConfirmedDialogListener");
>>>>>>> 8fd0a3671b3741c315ec32af4dab0c697336be3b
        }
    }


<<<<<<< HEAD
    public static HS_Bill_Delete_Dialog initialise(String billID) {
        HS_Bill_Delete_Dialog o = new HS_Bill_Delete_Dialog();
        Bundle b = new Bundle();
        b.putString(IntentConstants.BILL_ID, billID);
=======
    public static HS_Bill_Delete_Dialog initialise(String house_name, String username) {
        HS_Bill_Delete_Dialog o = new HS_Bill_Delete_Dialog();
        Bundle b = new Bundle();
        b.putString(IntentConstants.USERNAME, username );
        b.putString(IntentConstants.HOUSE_NAME, house_name);
>>>>>>> 8fd0a3671b3741c315ec32af4dab0c697336be3b
        o.setArguments(b);
        return o;
    }

    @Override
    public Dialog onCreateDialog(Bundle saveState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

<<<<<<< HEAD
        LayoutInflater inflater = getActivity().getLayoutInflater();
        RelativeLayout v = (RelativeLayout) inflater.inflate(R.layout.dialog_normal_fragment, null);
        ((TextView) v.findViewById(R.id.title)).setText("Confirmation");
        ((TextView) v.findViewById(R.id.content)).setText("Would you like to delete this bill permanently?");
        v.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HS_Bill_Delete_Dialog.this.dismiss();
            }
        });
        ((TextView) v.findViewById(R.id.dialog_okay)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBillDeleteDialogListener.onButtonClickDeleteBill
                        (HS_Bill_Delete_Dialog.this, getArguments().getString(IntentConstants.BILL_ID));
=======
//        builder.setTitle("Request to " + getArguments().getString(IntentConstants.HOUSE_NAME)+ " cancelled")
//                .setMessage("Your request has been cancelled. \nGood bye!")
//                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        mCancelConfirmedDialogListener.onButtonClickCancel(HS_Cancel_Confirm_Dialog.this, getArguments().getString(IntentConstants.USERNAME), getArguments().getString(IntentConstants.HOUSE_NAME));
//                    }
//                });
        LayoutInflater inflater = getActivity().getLayoutInflater();
        RelativeLayout v = (RelativeLayout) inflater.inflate(R.layout.dialog_normal_fragment, null);
        ((TextView) v.findViewById(R.id.title)).setText("Confirmation");
        ((TextView) v.findViewById(R.id.content)).setText("Your request has been cancelled. \nGood bye!");
        v.findViewById(R.id.dialog_cancel).setVisibility(View.INVISIBLE);
        ((TextView) v.findViewById(R.id.dialog_okay)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mCancelConfirmedDialogListener.onButtonClickCancel(HS_Bill_Delete_Dialog.this, getArguments().getString(IntentConstants.USERNAME), getArguments().getString(IntentConstants.HOUSE_NAME));

>>>>>>> 8fd0a3671b3741c315ec32af4dab0c697336be3b
            }
        });

        builder.setView(v);
        return builder.create();
    }
}
