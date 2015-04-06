package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by benlambert on 24/02/2015. <br/>
 * Edited by Thanh on 05/04/2015.
 */

/* This is just a class that displays a basic alert box, instead of
 * having this code cluttering up the main code base. you pass it the activity (usually this)
 * and pass it the string you want to display, and optionally the string for the button
 * (Defaults to okay)
 */
public class CustomMessageBox {

    static DialogInterface d;

    /**
     * Create a message box with specified text in the body
     * @param a the the calling activity
     * @param text the text in the body of the dialog
     */
    public CustomMessageBox(Activity a, String text) {
        AlertDialog.Builder msg = new AlertDialog.Builder(a);
        View v = prepareDialog(a, text);
        msg.setView(v);
        AlertDialog alert = msg.create();
        alert.show();
        d = alert;

    }

    /**
     * Create a message box like above plus the specified button text
     * @param a the calling activity
     * @param text the text in the body of the dialog
     * @param buttonText the button text
     */
    public CustomMessageBox(Activity a, String text, String buttonText) {
        AlertDialog.Builder msg = new AlertDialog.Builder(a);
        View v = prepareDialog(a, text);
        ((TextView) v.findViewById(R.id.dialog_okay)).setText(buttonText);
        msg.setView(v);
        AlertDialog alert = msg.create();
        alert.show();
        d = alert;
    }

    /**
     * prepare the dialog view
     * @param a the calling activity
     * @param text the text in the body of the dialog
     * @return the view of the dialog
     */
    public static View prepareDialog(Activity a, String text) {

        LayoutInflater inflater = a.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_fragment, null);
        ((TextView) v.findViewById(R.id.title)).setText("Notice");
        ((TextView) v.findViewById(R.id.content)).setText(text);
        v.findViewById(R.id.dialog_cancel).setVisibility(View.INVISIBLE);
        v.findViewById(R.id.dialog_okay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.cancel();
            }
        });

        return v;
    }
}
