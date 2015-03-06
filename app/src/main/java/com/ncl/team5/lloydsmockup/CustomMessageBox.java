package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Created by benlambert on 24/02/2015.
 */

/* This is just a class that displays a basic alert box, instead of
 * having this code cluttering up the main code base. you pass it the activity (usually this)
 * and pass it the string you want to display, and optioninally the string for the button
 * (Defaults to ok)
 */
public class CustomMessageBox {

    public CustomMessageBox(Activity a, String text)
    {
        AlertDialog.Builder msg = new AlertDialog.Builder(a);
        msg.setMessage(text)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = msg.create();
        alert.show();
    }

    public CustomMessageBox(Activity a, String text, String buttonText)
    {
        AlertDialog.Builder msg = new AlertDialog.Builder(a);
        msg.setMessage(text)
                .setCancelable(false)
                .setPositiveButton(buttonText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = msg.create();
        alert.show();
    }
}
