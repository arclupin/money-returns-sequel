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

import Utils.StringUtils;

/**
 *
 * Dialog for displaying the message left by the bill creator
 *
 *
 * Created by Thanh on 23-Mar-15.
 *
 */
public class HS_Bill_Message_Dialog extends DialogFragment{

    public static HS_Bill_Message_Dialog initialise(String authorName, String message) {
        HS_Bill_Message_Dialog o = new HS_Bill_Message_Dialog();
        Bundle b = new Bundle();
        b.putString(IntentConstants.AUTHOR_NAME, authorName );
        b.putString(IntentConstants.BILL_MESSAGE, message);
        o.setArguments(b);
        return o;
    }

    @Override
    public Dialog onCreateDialog(Bundle saveState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        RelativeLayout v = (RelativeLayout) inflater.inflate(R.layout.dialog_bill_message_fragment, null);
        ((TextView) v.findViewById(R.id.title)).setText("Message");
        String message = getArguments().getString(IntentConstants.BILL_MESSAGE);
        Log.d("message", message);
                ((TextView) v.findViewById(R.id.content)).setText("\"" +
                (StringUtils.isFieldEmpty(message) ? "No message." : message) + "\""); //example
        ((TextView) v.findViewById(R.id.bill_creator_message_author)).
                setText(getArguments().getString(IntentConstants.AUTHOR_NAME));
         v.findViewById(R.id.dialog_okay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HS_Bill_Message_Dialog.this.dismiss();
       }
        });
        builder.setView(v);
        return builder.create();
    }
}
