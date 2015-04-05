package com.ncl.team5.lloydsmockup;
/**
 *Danh comment
 * Danh comment
 * Danh comment
 * Danh comment
 */

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ViewFlipper;

import org.json.JSONException;
import org.json.JSONObject;

import HTTPConnect.Connection;


public class Login extends Activity {

    //Variables needed for the image flipper
    private EditText password_field;
    private EditText username_field;
    private String username;
    private String password;

    private ViewFlipper slider;

    private static final int OKAY = 0;
    private static final int WRONG = 1;
    private static final int WARNING = 2;
    private static final int LOCKED = 3;
    private static final int NETPROBS = 4;
    private String date = "";


    /**
     * Class authenticator extending the Connection <br/>
     * Purpose: improve user experience. <br/>
     * The earlier method of logging in involves some unnecessary steps which could be stripped away in order to improve performance.
     */
    public class Authenticator extends Connection {

        public Authenticator(Activity a) {
            super(a);
        }

        @Override
        public void onPostExecute(String r) {
            int status = 4;
            try {
               JSONObject j = new JSONObject(r);
                status = j.getInt("status");
                if (status == OKAY)
                    date = j.getString("last_login");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(r);
            authenticate(status);



        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //default stuff, hides the action bar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getActionBar();
        actionBar.hide();
        Log.d("id login", Integer.toString(actionBar.getHeight()) );
        Houseshare_Welcome.registered = false; // false the registered variable (this might save us 1 connection later)

        username_field = (EditText) findViewById(R.id.username);
        username_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                username = s.toString();
            }
        });

        //Set up the picture slide show of the adverts
        password_field = (EditText) findViewById(R.id.password);
        password_field.setTypeface(Typeface.DEFAULT);
        password_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                password = s.toString();
            }
        });

        slider = (ViewFlipper) findViewById(R.id.sliding_advert);
        runSlider();

    }

    /* Guessing this moves the adverts on the front screen, with a small delay
     */
    private void runSlider() {
        slider.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
        slider.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_left));
        slider.setAutoStart(true);
        slider.setFlipInterval(5000);
        slider.startFlipping();
    }

    public void launchMaps(View view) {
        Intent i = new Intent(this, LocationsLogin.class);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);
    }


    public void launchMain(View view) {
        //Gets the strings from the username and password_field boxes, and then authenticates them
        String username = ((EditText) findViewById(R.id.username)).getText().toString();
        String password = ((EditText) findViewById(R.id.password)).getText().toString();

        //can use this to login to the app while the server is down...
        //MUST COMMENT OUT ON RELEASE
        if (username.equals("test")) {
            //Starts an intent to launch the main menu
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("ACCOUNT_USERNAME", username);
            i.putExtra("DATE", "N/A"); // prevent the app from crashing due to null pointer
            startActivity(i);
            return;
        } else {
            new Authenticator(this).setMode(Connection.MODE.LONG_TASK)
                    .setDialogMessage("Logging in")
                    .execute("TYPE", "LOGIN", "USR", username, "PWD", password);
        }


    }

    /**
     * parse the json response and act accordingly
     *
     * @param status the server response from the authenticator
     */
    private void authenticate(int status) {
        /* This is where the application would go out to the server to
         * authenticate the username and password_field combination. The server
         * should then return true or false as to whether the combination is
         * correct. This method should also send the phone data, as this is
         * also used to authenticate.
         */
        // Need this for the exceptions that are throw (there are many!)

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
                switch (status) {
                    case OKAY: {
                        Intent i = new Intent(this, MainActivity.class);
                        i.putExtra("ACCOUNT_USERNAME", username);
                        i.putExtra("DATE", date);
                        startActivity(i);
                        break;
                    }
                    case WARNING: {
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
                        break;
                    }
                    case LOCKED: {
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
                        break;
                    }

                    case WRONG: {
                        new CustomMessageBox(this, "Incorrect Username and Password Combination.");
                    }

                    default:{
                            new CustomMessageBox(this, "Something wrong. Please try again");
                    }
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
