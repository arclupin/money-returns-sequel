package com.ncl.team5.lloydsmockup;
/**
*Danh comment
 * Danh comment
 * Danh comment
 * Danh comment
 */
import  android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import HTTPConnect.Connection;
import HTTPConnect.CookieStorage;


public class Login extends Activity {

    //Variables needed for the image flipper
    private EditText password;
    private ViewFlipper slider;
    private boolean netProbs = false;
    private boolean warning = false;
    private boolean locked = false;
    private String date = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //default stuff, hides the action bar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getActionBar();
        actionBar.hide();
        Houseshare_Welcome.registered = false; // false the registered variable (this might save us 1 connection later)

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
        slider.setFlipInterval(5000);
        slider.startFlipping();
    }


    public void launchMain(View view) {
        //Gets the strings from the username and password boxes, and then authenticates them
        String username = ((EditText)findViewById(R.id.username)).getText().toString();
        String password = ((EditText)findViewById(R.id.password)).getText().toString();

        //can use this to login to the app while the server is down...
        //MUST COMMENT OUT ON RELEASE
        if(username.equals("test"))
        {
            //Starts an intent to launch the main menu
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("ACCOUNT_USERNAME", username);
            i.putExtra("DATE","N/A"); // prevent the app from crashing due to null pointer
            startActivity(i);
            return;
        }

        if(authenticate(username, password))
        {
            //Starts an intent to launch the main menu
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("ACCOUNT_USERNAME", username);
            i.putExtra("DATE",date);
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

            if (locked) {

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
                locked = false;
            }
            else if (warning)
            {
                AlertDialog.Builder errorBox = new AlertDialog.Builder(this);
                errorBox.setMessage("Warning: one attempt remaining")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = errorBox.create();
                alert.show();
                warning = false;
            }
            else
            {

                /* Username and password not authenticated by server, add 1 to count */
                if (netProbs == false) {
//                    AlertDialog.Builder errorBox = new AlertDialog.Builder(this);
//                    errorBox.setMessage("Username and Password incorrect")
//                            .setCancelable(false)
//                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    dialog.cancel();
//                                }
//                            });
//                    AlertDialog alert = errorBox.create();
//                    alert.show();

                    new CustomMessageBox(this, "Username and password incorrect");
                }
                else
                {
                     /* Network problems detected... dont let user in but dont increment count either */

                    new CustomMessageBox(this, "Poor network conditions detected. Please check your connection and try again");
                    netProbs = false;
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
         * also used to authenticate.
         */

        /* creates a http object (my own class) to run in the background */
        Connection connection = new Connection(this);


        // Need this for the exceptions that are throw (there are many!)
        try {


            /* This is where the main action happens. This is where the connection is
             * started, which will run in the background, and then wait for it to finish
             * and gets its result. This is then evaluated (as it returns a boolean)
             * and logs you in if true, doesnt if false. the execute method is used by
             * the thread, it actually calls doInBackground in the HTTPConnect class,
             * slightly confusing :P
             *
             * HTTPConnect has now been moved into a different package, it doesnt change the
             * syntax but requires an import at the start of the file.
             */

            /* Ok, this look a bit weird... i mean it returns string right! should it not be boolean??
             * Well, if it returns 4 different messages from the server, not just true or false */
            String result = connection.execute("TYPE", "LOGIN" ,"USR", username, "PWD", password).get();


            if(result.equals("false"))
            {
                return false;
            }

            if(result.equals("error"))
            {
                netProbs = true;
                return false;
            }
            else
            {
                JSONObject jo = new JSONObject(result);


                if(jo.getString("status").equals("true"))
                {
                    date = jo.getString("last_login");
                    return true;
                }
                if(jo.getString("status").equals("warning"))
                {
                    warning = true;
                    return false;
                }
                if(jo.getString("status").equals("locked"))
                {
                    locked = true;
                    return false;
                }
                else
                {
                    return false;
                }
            }



        }
        catch (Exception e)
        {
            //If network problems are detected, return false but dont increment the counter for failed attempts
            netProbs = true;
            return false;
        }


    }

    // This removes the text in the text boxes when the focus comes back to this app..
    // However, it isnt fully working as it skips a few frames when it does it (i.e. hangs a little)
    // UPDATE: on restart instead of on resume seems to have fixed the problem with the
    // hang.

    /* This runs when the app has been restarted after it has stopped */
    @Override
    protected void onRestart() {
        super.onRestart();
        ((EditText) findViewById(R.id.username)).setText("");
        ((EditText) findViewById(R.id.password)).setText("");
    }


}
