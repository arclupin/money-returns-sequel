package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
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

    //functional interface for the action on click
    // Lambda expression could not be used unfortunately
    public interface ToClick {
        public void DoOnClick();
    }


    /**
     * Build the message box through an instance of this class
     */
    public static class MessageBoxBuilder {
        //required params
        private Activity a;
        private String text;

        //additional params
        private String title = "Notice";
        private String buttonText = "Okay";
        private ToClick action = null;


        public MessageBoxBuilder(Activity a, String text) {
            this.a = a;
            this.text = text;
        }

        public MessageBoxBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public MessageBoxBuilder setButtonText(String buttonText) {
            this.buttonText = buttonText;
            return this;
        }

        public MessageBoxBuilder setActionOnClick(ToClick action) {
            this.action = action;
            return this;
        }

        public CustomMessageBox build() {
            return new CustomMessageBox(this);
        }

    }

    /**
     * Constructor using new type builder
     * @param builder the configured builder
     */
    public CustomMessageBox(MessageBoxBuilder builder) {
       AlertDialog alert = makeDialogInternal(builder.a, prepareDialogView(builder.a, builder));
        d = alert;
        alert.show();

    }


    //*****************************************TRADITIONAL CONSTRUCTORS****************************************

    /**
     * There are a number of choices for constructors.
     * The builder itself should be sufficient considering a lot of optional information are present.
     * However these constructors are all kept for code backward-compatibility.
     */

    /**
     * Create a message box with specified text in the body
     *
     * @param a    the the calling activity
     * @param text the text in the body of the dialog
     */
    public CustomMessageBox(Activity a, String text) {
        View v = prepareDialogView(a, text);
        AlertDialog alert = makeDialogInternal(a, v);
        alert.show();
        d = alert;
    }

    /**
     * Create a message box like above plus the specified button text
     *
     * @param a          the calling activity
     * @param text       the text in the body of the dialog
     * @param buttonText the button text
     */
    public CustomMessageBox(Activity a, String text, String buttonText) {
        View v = prepareDialogView(a, text);
        ((TextView) v.findViewById(R.id.dialog_okay)).setText(buttonText);
        AlertDialog alert = makeDialogInternal(a, v);
        alert.show();
        d = alert;
    }

    /**
     * Create a message box like above plus the specified button text plus a title for the box
     *
     * @param a          the calling activity
     * @param text       the text in the body of the dialog
     * @param buttonText the button text
     *                   @param title the title of the dialog
     */
    public CustomMessageBox(Activity a, String text, String buttonText, String title) {
        View v = prepareDialogView(a, text, title, buttonText);
        AlertDialog alert = makeDialogInternal(a, v);
        alert.show();
        d = alert;
    }


    //*****************************************PRIVATE HELPER METHODS****************************************

    /**
     * shared internal method of dialog creating methods
     * @param a the the calling activity
     * @param v the view of the dialog
     * @return the crafted dialog
     */
    private AlertDialog makeDialogInternal(Activity a, View v) {
        AlertDialog.Builder msg = new AlertDialog.Builder(a);
        msg.setView(v);
        AlertDialog alert = msg.create();
        return alert;

    }


    /**
     * prepare the dialog view - <u>NOT</u> the dialog
     *
     * @param a    the calling activity
     * @param text the text in the body of the dialog
     * @return the view of the dialog
     */
    private static View prepareDialogView(Activity a, String text) {

        LayoutInflater inflater = a.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_normal_fragment, null);
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



    /**
     * prepare the dialog view like above plus a specified title
     *
     * @param a    the calling activity
     * @param text the text in the body of the dialog
     * @return the view of the dialog
     */
    private static View prepareDialogView(Activity a, String text, String title) {

        View v = prepareDialogView(a, text);
        ((TextView) v.findViewById(R.id.title)).setText(title);

        return v;
    }

    /**
     * prepare the dialog view like above plus a specified button text
     *
     * @param a    the calling activity
     * @param text the text in the body of the dialog
     * @return the view of the dialog
     */
    private static View prepareDialogView(Activity a, String text, String title, String buttonText) {

        View v = prepareDialogView(a, text, title);
        ((TextView) v.findViewById(R.id.dialog_okay)).setText(buttonText);
        return v;
    }

    /**
     * prepare the a FULLY configured dialog view
     *
     * @param a    the calling activity
     * @param text the text in the body of the dialog
     * @param action what to do when the user click the button
     * @return the view of the dialog
     */
    private static View prepareDialogView(Activity a, String text, String title, String buttonText, final ToClick action ) {

        View v = prepareDialogView(a, text, title, buttonText);
        v.findViewById(R.id.dialog_okay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.cancel();
                if (action != null)
                    action.DoOnClick(); // perform the action after the dialog is dismissed
            }
        });

        return v;
    }

    private static View prepareDialogView(Activity a, MessageBoxBuilder builder) {
        return prepareDialogView(builder.a, builder.text, builder.title, builder.buttonText, builder.action);
    }




}
