package com.ncl.team5.lloydsmockup;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ViewFlipper;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Scanner;


public class Login extends Activity {

    //Variables needed for the image flipper
    private EditText password;
    private ViewFlipper slider;
    private int count = 0;
    private boolean netProbs = false;

    InputStream is;

    /* sets max count to 3, this can be changed if needed */
    private final int MAX_COUNT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //default stuff, hides the action bar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getActionBar();
        actionBar.hide();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder() .detectAll().penaltyLog().build();
        StrictMode.setThreadPolicy(policy);

        //Set up the picture slide show of the adverts
        password = (EditText) findViewById(R.id.password);
        password.setTypeface(Typeface.DEFAULT);

        slider = (ViewFlipper) findViewById(R.id.sliding_advert);
        runSlider();

    }

    /* Guessing this moves the adverts on the front screen, with a small delay
     */
    private void runSlider()
    {
        slider.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
        slider.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_left));
        slider.setAutoStart(true);
        slider.setFlipInterval(2800);
        slider.startFlipping();
    }


    public void launchMain(View view) {
        //Gets the strings from the username and password boxes, and then authenticates them
        String username = ((EditText)findViewById(R.id.username)).getText().toString();
        String password = ((EditText)findViewById(R.id.password)).getText().toString();



        if(authenticate(username, password))
        {
            //Starts an intent to launch the main menu
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
        else
        {
            /* This is a message displaying a failure to login message
             * so the user knows they logged in incorrectly. There is a maximum number of
             * failed attempts allowed, just implemented by a simple counter. After a
             * user is locked out, they should not be able to access their account again
             * until authenticated with their bank. This information could maybe be called
             * on the server (i.e. have a locked field for the user) which can be set
             * on the phone, but only unset via a bank. Currently it DOES NOT do this
             */

            if (count == MAX_COUNT) {

                /* Failed more than the max count and are locked out */

                AlertDialog.Builder errorBox = new AlertDialog.Builder(this);
                errorBox.setMessage("Too many login attempts, Please contact your bank to unlock your application")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = errorBox.create();
                alert.show();
            }
            else
            {

                /* Username and password not authenticated by server, add 1 to count */
                if (!netProbs) {
                    count++;
                    AlertDialog.Builder errorBox = new AlertDialog.Builder(this);
                    errorBox.setMessage("Username and Password incorrect")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = errorBox.create();
                    alert.show();
                }
            }
        }


    }

    private boolean authenticate(String username, String password)
    {
        /* This is where the application would go out to the server to
         * authenticate the username and password combination. The server
         * should then return true or false as to whether the combination is
         * correct. This method should also send the phone data, as this is
         * also used to authenticate. For now this method just returns true
         */

        /* Code below uses the HTTP class to connect to the internet, but
         * it doesnt work... run poor network conditions detected every
         * time... dont know what the problem is... also it is NOT secure bty any means
         */


        HTTPConnect test = new HTTPConnect();

        try {
            return test.getLogin();
        }catch (Exception e)
        {
            netProbs = true;
            AlertDialog.Builder errorBox = new AlertDialog.Builder(this);
            errorBox.setMessage("Network Problems Detected")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = errorBox.create();
            alert.show();
            return false;
        }
    }
}
