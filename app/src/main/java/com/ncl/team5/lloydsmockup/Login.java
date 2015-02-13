package com.ncl.team5.lloydsmockup;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ViewFlipper;

import HTTPConnect.Connection;


public class Login extends Activity {

    //Variables needed for the image flipper
    private EditText password;
    private ViewFlipper slider;
    private int count = 0;
    private boolean netProbs = false;

    /* sets max count to 3, this can be changed if needed */
    private final int MAX_COUNT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //default stuff, hides the action bar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getActionBar();
        actionBar.hide();

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
            String message = username;
            i.putExtra("ACCOUNT_USERNAME", message);
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
                if (netProbs == false) {
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
                else
                {
                     /* Network problems detected... dont let user in but dont increment count either */
                    AlertDialog.Builder errorBox = new AlertDialog.Builder(this);
                    errorBox.setMessage("Poor network conditions detected. Please check your connection and try again")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = errorBox.create();
                    alert.show();
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
        Connection connection = new Connection();


        // Need this for the exceptions that are throw (there are many!)
        try {

            /* This is where the main action happens. This is where the connection is
             * started, which will run in the background, and then wait for it to finish
             * and gets its result. This is then evaluated (as it returns a boolean)
             * and logs you in if true, doesnt if false. the execute method is used by
             * the thread, it actually calls doInBackground int the HTTPConnect class,
             * slightly confusing :P
             */

            /* Ok, this look a bit weird... i mean it returns an int right! should it not be boolean??
             * Well, if it returns 0, it is true, 1 is false, and 2 is poor network connections
             * As i needed 3 results... really should use an enum but i can do that some other day*/
            String result = connection.execute("USR", username, "PWD", password).get();

            if(result.equals("error"))
            {
                netProbs = true;
                return false;
            }
            else
            {
                result = result.split(",")[0].split(":")[1].substring(1, result.split(",")[0].split(":")[1].length() - 2);

                if(result.equals("true"))
                {
                    return true;
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
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Intent intent = new Intent(getApplicationContext(), Login.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//    }
}
