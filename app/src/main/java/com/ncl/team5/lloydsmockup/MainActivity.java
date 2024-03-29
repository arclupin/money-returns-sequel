package com.ncl.team5.lloydsmockup;
// not anymore

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import HTTPConnect.Connection;
import HTTPConnect.Request_Params;
import HTTPConnect.Responses_Format;
import Utils.Houseshares;


public class MainActivity extends Activity implements GetNotification.OnNotiFetchedListener {

    private static String username;
    private static String houseshare_id;

    // use static for date would help us not have to pass the date around as the value of the date wont be lost on activity change.
    private static String date;



    private List<String> accountNums = new ArrayList<String>();
    private String logoutTime;
    private Menu activityMenu;
    private String tempLogout;
    private MainConnectionWorker mWorker;
    private Menu menu;

    public static final String RESUME_FROM_INSIDE = "0";

    @Override
    public void onAllTransactionDone(boolean result) {
        activityMenu.getItem(1).setIcon(result ? R.drawable.ic_action_notify : R.drawable.globe);
    }

    private enum BUTTON {PAYMENT, HOUSESHARE}

    ; //TODO complete

    /**
     * Class MainConnectionWorker responsible for working with the server
     */
    public class MainConnectionWorker extends Connection {
        private BUTTON b;

        public MainConnectionWorker(Activity a) {
            super(a);
        }

        public MainConnectionWorker(Activity a, BUTTON b) {
            super(a);
            this.b = b;
        }

        public MainConnectionWorker setButton(BUTTON b) {
            this.b = b;
            return this;
        }

        @Override
        public void onPostExecute(String r) {
            super.onPostExecute(r);

            switch (b) {
                case HOUSESHARE: {
                    initialiseHouseshare(r);
                    mWorker = mWorker.newInstance(); // reset the mWorker
                    break;
                }
                //TODO more
            }
        }

        //Asynct task cant be reused so we might need this
        public MainConnectionWorker newInstance() {
            return new MainConnectionWorker(MainActivity.this);
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //gets the username that is passed from the login so the connection can stay open
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = df.format(Calendar.getInstance().getTime());
        Intent i = getIntent();

        username = i.getStringExtra(IntentConstants.USERNAME) != null ? i.getStringExtra(IntentConstants.USERNAME) : username;

        if (i.getStringExtra(IntentConstants.DATE) != null) {// check for null (date coming from login)
            date = i.getStringExtra(IntentConstants.DATE);
        }// assign the date for the first and also the last time of the session
        else
        {
            date = currentDate;
            //TODO this would cause the last login time to be constantly updated whenever the activity is created perhaps?
        }

        TextView dateText = (TextView) findViewById(R.id.lastLoginTextView);

        //need to change this to the actual login time response stuff
        if (date.equals("Not Available")) {
            //Get the current date and time for first logon (no notifications)
            dateText.setText(username + " : Logged in on " + date);
        } else {
            dateText.setText(username + " : Last Login on " + date);
        }

        date = currentDate;

        SharedPreferences settings = getSharedPreferences(username, 0);
        logoutTime = settings.getString("LOGOUT_TIME", "");
        tempLogout = settings.getString("TEMP_LOGOUT_TIME", "");

        if (logoutTime.equals("") || tempLogout.equals("")) {
            logoutTime = date;
        } else {
            try {
                Date normalLogout = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(logoutTime);
                Date lastTempLogout = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tempLogout);

                if (normalLogout.compareTo(lastTempLogout) < 0) {
                    logoutTime = tempLogout;
                }
            } catch (ParseException pe) {
                logoutTime = date;
            }


        }

        // set up new mWorker
        mWorker = new MainConnectionWorker(this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        activityMenu = menu;

        /* Show notification icon in menu bar */
        getMenuInflater().inflate(R.menu.menu_main, activityMenu);

        MenuItem item = activityMenu.getItem(1);
        GetNotification notif = new GetNotification();

        notif.getNotifications(this, username, date);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_help) {
            Intent intent = new Intent(this, HelpActivity.class);
            intent.putExtra(IntentConstants.USERNAME, username);
            intent.putExtra(IntentConstants.DATE, date);
            startActivity(intent);
            ((KillApp) this.getApplication()).setStatus(false);
        } else if (id == R.id.action_notifications) {
            Intent intent = new Intent(this, Notifications.class);
            intent.putExtra(IntentConstants.USERNAME, username);
            intent.putExtra(IntentConstants.DATE, date);
            startActivity(intent);
            ((KillApp) this.getApplication()).setStatus(false);
        }
        return super.onOptionsItemSelected(item);
    }


    /* All of the button click methods... each is passed the username and the date */

    public void btnClickPayments(View view) {
        Intent i = new Intent(this, Payments.class);
        i.putExtra(IntentConstants.USERNAME, username);
        i.putExtra(IntentConstants.DATE, date);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);

    }

    public void btnClickTransfers(View view) {
        Intent i = new Intent(this, Transfers.class);
        i.putExtra(IntentConstants.USERNAME, username);
        i.putExtra(IntentConstants.DATE, date);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);
    }

    public void btnClickAccounts(View view) {
        Intent i = new Intent(this, Accounts.class);
        i.putExtra(IntentConstants.USERNAME, username);
        i.putExtra(IntentConstants.DATE, date);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);
    }

    public void btnClickAnalysis(View view) {
        Intent i = new Intent(this, Analysis.class);
        i.putExtra(IntentConstants.USERNAME, username);
        i.putExtra(IntentConstants.DATE, date);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);
    }



    public void btnClickOffers(View view) {
        Intent i = new Intent(this, Locations.class);
        i.putExtra(IntentConstants.USERNAME, username);
        i.putExtra(IntentConstants.DATE, date);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);
    }

    public void btnClickProducts(View view) {
        Intent i = new Intent(this, Products.class);
        i.putExtra(IntentConstants.USERNAME, username);
        i.putExtra(IntentConstants.DATE, date);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);
    }

    public void btnClickSettings(View view) {
        Intent i = new Intent(this, Settings.class);
        i.putExtra(IntentConstants.USERNAME, username);
        i.putExtra(IntentConstants.DATE, date);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);
    }

    //TODO unfinished function
    public void btnClickHouseShare(View view) {
        if (this.username.equals("test")) {
            Intent i = new Intent(this, Houseshare_Welcome.class);
            i.putExtra(IntentConstants.USERNAME, username);
            startActivity(i);
            ((KillApp) this.getApplication()).setStatus(false);
        } else {
            mWorker.setButton(BUTTON.HOUSESHARE)
                    .setMode(Connection.MODE.LONG_TASK)
                    .setDialogMessage("Initialising service data")
                    .execute("TYPE", Request_Params.VAL_HS_INIT, Request_Params.PARAM_USR, username);
        }
    }

    public void btnLogout(View view) {
        Connection connect = new Connection(this);
        try {
            connect.execute("TYPE", "LOGOUT", IntentConstants.USERNAME, username);
        } finally {
            /* Set the logout time so it can easily get it later */
            SharedPreferences sp = getSharedPreferences("logoutpref", 0);
            SharedPreferences.Editor edit = sp.edit();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String logout = df.format(Calendar.getInstance().getTime());
            edit.putString("LOGOUT_TIME", logout);
            edit.commit();

            sp = getSharedPreferences("transinsession", 0);
            edit = sp.edit();
            edit.putBoolean("IN_SESSION", false);
            edit.commit();

            ((KillApp) this.getApplication()).setStatus(false);
            this.finish();

        }
    }

    /* This is how the application knows if it has been stopped by an intent or by an
     * external source (i.e. home button, phone call etc). Each time an intent is called, it
     * sets an application global variable denoted as KillApp to false. This means that when a new
     * activity is opened, it does not want to restart the application. However if no intent is
     * fired (i.e. phonecall, home button pressed) KillApp will have the value true so it will
     * restart back to the login activity.
     */

    /* This is where the test is done to see whether the KillApp variable is true, and if it is, to call
     * the login class. It also clears the activity stack so the back button cannot be used to go back */
    @Override
    protected void onResume() {

        getActionBar().setBackgroundDrawable(new ColorDrawable(MainActivity.getColour(this)));

        Log.d("On main resume", "Intent:" );
        if (getIntent().getBooleanExtra(RESUME_FROM_INSIDE, false)) {
            super.onResume();
        }
        else {
            if (((KillApp) this.getApplication()).getStatus()) {
                //APP IS KILLED :O

                Connection connect = new Connection(this);
                try {
                    connect.execute("TYPE", "LOGOUT", IntentConstants.USERNAME, username);
                } finally {
                /* Set the logout time so it can easily get it later */
                    SharedPreferences sp = getSharedPreferences(username, 0);
                    SharedPreferences.Editor edit = sp.edit();
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String logout = df.format(Calendar.getInstance().getTime());
                    edit.putString("LOGOUT_TIME", logout);
                    edit.commit();

                    sp = getSharedPreferences("transinsession", 0);
                    edit = sp.edit();
                    edit.putBoolean("IN_SESSION", false);
                    edit.commit();

                    ((KillApp) this.getApplication()).setStatus(false);
                    finish();
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }


            } else {
                //each time the app resumes and it wasnt killed, the variable needs to be reset
                ((KillApp) this.getApplication()).setStatus(true);

                invalidateOptionsMenu();

            }

            super.onResume();
        }
    }

    public void onPause() {
         /* Set the logout time so it can easily get it later */
        SharedPreferences sp = getSharedPreferences(username, 0);
        SharedPreferences.Editor edit = sp.edit();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String logout = df.format(Calendar.getInstance().getTime());
        edit.putString("TEMP_LOGOUT_TIME", logout);
        edit.commit();

        super.onPause();
    }

    //overriding the on back pressed method (for the built in back button) so
    //its status can be set to false, so it doesnt launch the login on on resume
    @Override
    public void onBackPressed() {
        Connection connect = new Connection(this);
        try {
            connect.execute("TYPE", "LOGOUT", IntentConstants.USERNAME, username);
        } finally {
            /* Set the logout time so it can easily get it later */
            SharedPreferences sp = getSharedPreferences(username, 0);
            SharedPreferences.Editor edit = sp.edit();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String logout = df.format(Calendar.getInstance().getTime());
            edit.putString("LOGOUT_TIME", logout);
            edit.commit();

            sp = getSharedPreferences("transinsession", 0);
            edit = sp.edit();
            edit.putBoolean("IN_SESSION", false);
            edit.commit();

            ((KillApp) this.getApplication()).setStatus(false);
            this.finish();
        }
    }

    private void initialiseHouseshare(String response) {
        try {
            JSONObject jo = new JSONObject(response);
            /* Check if the user has timed out */
            if (jo.getString("expired").equals("true")) {
                /* Display message box and auto logout user */
                final Connection temp_connect = new Connection(MainActivity.this);
                // experimenting a new message box builder
                CustomMessageBox.MessageBoxBuilder builder = new CustomMessageBox.MessageBoxBuilder(this, "Your session has been timed out, please login again");
                builder.setTitle("Expired")
                        .setActionOnClick(new CustomMessageBox.ToClick() {
                    @Override
                    public void DoOnClick() {
                        temp_connect.autoLogout(username);
                    }
                }).build();
            }

            // if not registered -> redirect to the welcome page
            else if (jo.getString("status").equals(Responses_Format.RESPONSE_HOUSESHARE_NOT_JOINED)) {
                Houseshares.hs_intents(this, Houseshare_Welcome.class, username);

            }

            else {
                final String hs_name = (jo.has(Responses_Format.RESPONSE_HS_CONTENT) ? jo.getString(Responses_Format.RESPONSE_HS_CONTENT) : "");
                houseshare_id = (jo.getString(Responses_Format.RESPONSE_HOUSESHARE_ID));
                Houseshares.hs_intents_home_view(this, Houseshare_HomeView.class, hs_name, username, houseshare_id, jo.getString("status"));
            }
        }


//            // TODO unfinished, the server will send a more detailed message i.e.
//
//
//            else if (jo.getString("status").equals(Responses_Format.RESPONSE_HOUSESHARE_JOINED_HOUSE)) { // else if joined a house -> redirect to main home page
////                    Toast.makeText(this, "Registered, Joined house, To redirect to main page", Toast.LENGTH_SHORT).show();
//                final String hs_name = jo.getString(Responses_Format.RESPONSE_HS_CONTENT);
//                Houseshares.hs_intents_home_view(this, Houseshare_HomeView.class, hs_name, username, Responses_Format.RESPONSE_HOUSESHARE_JOINED_HOUSE);
//
//            }
//            else if (jo.getString("status").equals(Responses_Format.RESPONSE_HOUSESHARE_SENT_REQ)){
//                final String hs_name = jo.getString(Responses_Format.RESPONSE_HS_CONTENT);
//                Houseshares.hs_intents_home_view(this, Houseshare_HomeView.class, hs_name, username, Responses_Format.RESPONSE_HOUSESHARE_SENT_REQ);
//            }
//            else {
//                Toast.makeText(MainActivity.this, "Some unknown error on the houseshare service on the server", Toast.LENGTH_SHORT).show();
//            }
            /* There was an error indide the status return field, display appropriate error message */
            //TODO implement error messages


        /* Catch the exceptions */ catch (JSONException jse) {
            /* Error in the JSON response */
            new CustomMessageBox(MainActivity.this, "There was an error in the server response");
            jse.printStackTrace();
        } catch (Exception e) {
            /* Failsafe if something goes utterly wrong */
            new CustomMessageBox(MainActivity.this, "An unknown error occurred");
            e.printStackTrace();
        }
    }


    /* This is the method that gets the color from the shared preferences
     * @params : context
     * @returns : int */
    public static int getColour(Context c)
    {
        SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(c);

        String s2 = s.getString("colourList", PreferenceConstants.COLOUR_DEFAULT);


        if(s2.equals("error") || s2.equals(PreferenceConstants.COLOUR_DEFAULT))
        {
            return Color.WHITE;
        }
        else if(s2.equals(PreferenceConstants.COLOUR_BLUE))
        {
            return Color.parseColor("#3498db");
        }
        else if(s2.equals(PreferenceConstants.COLOUR_YELLOW))
        {
            return Color.parseColor("#F9FF59");
        }
        else if(s2.equals(PreferenceConstants.COLOUR_RED))
        {
            return Color.parseColor("#FF4242");
        }


        return R.color.white;

    }

    public static int getColor()
    {
        return Color.parseColor("#105842");
    }
}


