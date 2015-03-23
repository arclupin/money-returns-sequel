package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import org.apache.http.impl.cookie.DateParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import HTTPConnect.Connection;
import HTTPConnect.Request_Params;
import HTTPConnect.Responses_Format;


public class MainActivity extends Activity {


    private String username;
    private String date;
    private List<String> accountNums = new ArrayList<String>();
    private String logoutTime;
    private Menu activityMenu;
    private String tempLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //gets the username that is passed from the login so the connection can stay open
        Intent i = getIntent();
        username = i.getStringExtra("ACCOUNT_USERNAME");
        date = i.getStringExtra("DATE");

        TextView dateText = (TextView)findViewById(R.id.lastLoginTextView);

        //need to change this to the actual login time response stuff
        if(date.equals("Not Available"))
        {
            //do some fancy first logon stuff :)
            dateText.setText("");
        }
        else
        {
            dateText.setText(username + " : Last Login on " + date);
        }

        SharedPreferences settings = getSharedPreferences(username, 0);
        logoutTime = settings.getString("LOGOUT_TIME", "");
        tempLogout = settings.getString("TEMP_LOGOUT_TIME", "");


        if(logoutTime.equals("") || tempLogout.equals(""))
        {
            logoutTime = date;
        }
        else
        {
            try {
                Date normalLogout = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(logoutTime);
                Date lastTempLogout = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tempLogout);

                if(normalLogout.compareTo(lastTempLogout) < 0)
                {
                    logoutTime = tempLogout;
                }
            }
            catch (ParseException pe)
            {
                logoutTime = date;
            }


        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        activityMenu = menu;

        /* Show notification icon in menu bar */
        getMenuInflater().inflate(R.menu.menu_main, activityMenu);

        MenuItem item = activityMenu.getItem(0);


//        if(getNotif()) {
//            Log.d("Notif Change", "IN HERE");
//            item.setIcon(R.drawable.ic_action_notify);
//        }
//        else
//        {
//            Log.d("Notif Change", "IN There");
//            item.setIcon(R.drawable.ic_action_email);
//        }

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

         if (id == R.id.action_backHome) {
            ((KillApp) this.getApplication()).setStatus(false);
            this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
            //Intent intent = new Intent(this, MainActivity.class);
            //startActivity(intent);
        }
        else if (id == R.id.action_notifications) {
            Intent intent = new Intent(this, Notifications.class);
            intent.putExtra("ACCOUNT_USERNAME", username);
            intent.putExtra("LAST_LOGIN", date);
            startActivity(intent);
            ((KillApp) this.getApplication()).setStatus(false);
        }
        return super.onOptionsItemSelected(item);
    }


    public void btnClickPayments(View view) {
        Intent i = new Intent(this, Payments.class);
        i.putExtra("ACCOUNT_USERNAME", username);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);

    }

    public void btnClickTransfers(View view) {
        Intent i = new Intent(this, Transfers.class);
        i.putExtra("ACCOUNT_USERNAME", username);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);
    }

    public void btnClickAccounts(View view) {
        Intent i = new Intent(this, Accounts.class);
        //Log.d("USERNAME", username);
        i.putExtra("ACCOUNT_USERNAME", username);
        i.putExtra("DATE", date);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);
    }

    public void btnClickAnalysis(View view) {
        Intent i = new Intent(this, Analysis.class);
        i.putExtra("ACCOUNT_USERNAME", username);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);
    }

    //TODO unfinished function
    public void btnClickHouseShare(View view) {
        if (this.username.equals("test")) {
            Intent i = new Intent(this, Houseshare_Welcome.class);
            i.putExtra("ACCOUNT_USERNAME", username);
            startActivity(i);
            ((KillApp) this.getApplication()).setStatus(false);
        } else {
            Connection connect = new Connection(this);
            connect.setMode(Connection.MODE.SMALL_TASK);
            String result;

            try {
             /* Returns: JSON String */
                result = connect.execute(Request_Params.PARAM_TYPE, Request_Params.VAL_HS_INIT, Request_Params.PARAM_USR, this.username).get();
            /* Turns String into JSON object, can throw JSON Exception */
                JSONObject jo = new JSONObject(result);

            /* Check if the user has timed out */
                if (jo.getString("expired").equals("true")) {

                /* Display message box and auto logout user */
                    AlertDialog.Builder errorBox = new AlertDialog.Builder(this);
                    final Connection temp_connect = connect;
                    final String temp_usr = username;
                    errorBox.setMessage("Your session has been timed out, please login again")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    temp_connect.autoLogout(temp_usr);
                                }
                            });
                    AlertDialog alert = errorBox.create();
                    alert.show();
                }
                // TODO unfinished, the server will send a more detailed message i.e. 'registered and joined house' or 'registered and not joined house'
                else if (jo.getString("status").equals(Responses_Format.RESPONSE_HOUSESHARE_NOT_JOINED)) { // if not registered -> redirect to the welcome page
                    //need to see how the progress dialog works so also need to delay the start of the homeview activity.
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            hs_intents(Houseshare_Welcome.class);
                        }}, 2150); //150ms offset so that the dialog would not lag (if this was the same as in the Connection (2000s)
                       // then we might end up having 2 tasks to be posted at nearly the same time => the dialog might get interrupted resulting in a graphic lag when it disappears
                       // (my guess) - could use some other function to put this task right after the dialog task (which I dont know).
                }
                else if (jo.getString("status").equals(Responses_Format.RESPONSE_HOUSESHARE_JOINED_SERVICE)) { // else if not joined a house -> redirect to the search page
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            hs_intents(Houseshare_Search.class);
                        }}, 2150);
                }
                // TODO unfinished, the server will send a more detailed message i.e.


               else if (jo.getString("status").equals(Responses_Format.RESPONSE_HOUSESHARE_JOINED_HOUSE)) { // else if joined a house -> redirect to main home page
                    Toast.makeText(this, "Registered, Joined house, To redirect to main page", Toast.LENGTH_SHORT).show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            hs_intents(Houseshare_HomeView.class);
                        }}, 2150);
                }

                else {
                    Toast.makeText(this, "Some unknown error on the houseshare service on the server", Toast.LENGTH_SHORT).show();
                }
            /* There was an error indide the status return field, display appropriate error message */
                //TODO implement error messages

            }
        /* Catch the exceptions */ catch (JSONException jse) {
            /* Error in the JSON response */
                new CustomMessageBox(this, "There was an error in the server response");
                jse.printStackTrace();
            } catch (InterruptedException interex) {
            /* Caused when the connection is interrupted */
                new CustomMessageBox(this, "Connection has been interrupted");
                interex.printStackTrace();
            } catch (ExecutionException ee) {
            /* No idea when this is caused but it throws it... */
                new CustomMessageBox(this, "Execution Error");
                ee.printStackTrace();
            } catch (Exception e) {
            /* Failsafe if something goes utterly wrong */
                new CustomMessageBox(this, "An unknown error occurred");
                e.printStackTrace();
            }


        }
    }

    private void hs_intents(Class c) {
        Intent i = new Intent(this, c);
        i.putExtra("ACCOUNT_USERNAME", username);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);
    }

    public void btnClickOffers(View view) {
        Intent i = new Intent(this, Locations.class);
        i.putExtra("ACCOUNT_USERNAME", username);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);
    }

    public void btnClickProducts(View view) {
        Intent i = new Intent(this, Products.class);
        i.putExtra("ACCOUNT_USERNAME", username);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);
    }

    public void btnClickSettings(View view) {
        Intent i = new Intent(this, Settings.class);
        i.putExtra("ACCOUNT_USERNAME", username);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);
    }

    public void btnLogout(View view) {
        Connection connect = new Connection(this);
        try
        {
            connect.execute("TYPE","LOGOUT", "USR", username);
        }
        finally
        {
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
        if(((KillApp) this.getApplication()).getStatus())
        {
            //APP IS KILLED :O

            Connection connect = new Connection(this);
            try
            {
                connect.execute("TYPE","LOGOUT", "USR", username);
            }
            finally
            {
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


        }
        else
        {
            //each time the app resumes and it wasnt killed, the variable needs to be reset
            ((KillApp) this.getApplication()).setStatus(true);

            invalidateOptionsMenu();

        }

        super.onResume();

    }

    public void onPause()
    {
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
        try
        {
            connect.execute("TYPE","LOGOUT", "USR", username);
        }
        finally
        {
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

    /* Get all of the account numbers for the account, so notifications can be taken for all of them */
    private void getAllAccounts()
    {
        Connection hc = new Connection(this);

        try {
            String result = hc.execute("TYPE","SAA","USR", username ).get();

            JSONObject jo = new JSONObject(result);


            if(jo.getString("expired").equals("true"))
            {

                //This uses the same code as the main menu does to start the login, only this time it is run when the user
                //has timed out
                AlertDialog.Builder msg = new AlertDialog.Builder(this);
                msg.setMessage("Your session has been timed out, please login again")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                //autoLogout();
                                //dialogClosed = true;

                            }
                        });
                AlertDialog alert = msg.create();
                alert.show();


            }
            else {

                JSONArray jsonArray = jo.getJSONArray("accounts");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject insideObject = jsonArray.getJSONObject(i);
                    accountNums.add(insideObject.getString("account_number"));
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /* Compare the times of transactions with the last login time, any time after last login
     * is flagged as new notification */
    public boolean getNotif() {

        getAllAccounts();

        for (int k = 0; k < accountNums.size(); k++) {

            String accountNum = accountNums.get(k);

            SharedPreferences settings = getSharedPreferences("transinsession", 0);
            boolean transInSession = settings.getBoolean("IN_SESSION", false);



            /* Start the connection */
            Connection hc = new Connection(this);

            try {
                /* This is the command needed for the transactions, takes username and account number, returns JSON String */
                String result = hc.execute("TYPE", "TRANSLIST", "USR", username, "ACC_NUMBER", accountNum).get();

                /* Tries to convert to JSON Object, can throw JSON Exception */
                JSONObject jo = new JSONObject(result);

                /* Check if account has expired (very unlikely as this is called after get accounts) */
                if (jo.getString("expired").equals("true")) {
                    /* Display error message and log user out as they have expired */
                    AlertDialog.Builder errorBox = new AlertDialog.Builder(this);
                    errorBox.setMessage("You have been timed out, please login again")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    //autoLogout();
                                }
                            });
                    AlertDialog alert = errorBox.create();
                    alert.show();
                } else {
                    /* Array needed as transaction returned inside JSON array */
                    JSONArray jsonArray = jo.getJSONArray("transactions");

                    /* There are no transactions if the array length is zero */
                    if (jsonArray.length() == 0) {
                        /* there are no transactions, so no notifications */
                       
                        return false;
                    }

                    /* add the last three payees to the spinner */
                    for (int i = 0; i < jsonArray.length(); i++) {

                        /* Gets an object of each element in the array */
                        JSONObject insideObject = jsonArray.getJSONObject(i);

                        /* Gets the date field and parse it into a date variable, can throw an exception but never should... */
                        String date = insideObject.getString("Time");
                        Date timeFromResponse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
                        Date logoutTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(this.logoutTime);


                        if(timeFromResponse.compareTo(logoutTime) > 0 && !transInSession)
                        {
                            return true;
                        }
                    }

                    return false;
                }
            }
            /* Catch the exceptions */
            catch (JSONException jse) {
            /* Exception for when the JSON cannot be parsed correctly */
                new CustomMessageBox(this, "There was an error in the server response");
                jse.printStackTrace();
            } catch (InterruptedException interex) {
            /* Caused when the connection is interrupted */
                new CustomMessageBox(this, "Connection has been interrupted");
                interex.printStackTrace();
            } catch (ExecutionException ee) {
            /* No idea when this is caused but it throws it... */
                new CustomMessageBox(this, "Execution Error");
                ee.printStackTrace();
            } catch (Exception e) {
            /* Failsafe if something goes utterly wrong */
                new CustomMessageBox(this, "An unknown error occurred");
                e.printStackTrace();
            }
        }

        return false;
    }
}
