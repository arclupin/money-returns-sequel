package com.ncl.team5.lloydsmockup;

/* This class will allow the user to look at all of their accounts together
 * on one page, and then let the user select and account to see all of the
 * transactions on that account in the last 30 days (stored in the database somewhere).
 * Inside the statements, a user will be able to select more info on each transaction.
 * All of the data from this will be retrieved the same way as the login was done, possibly
 * at the same time, and just pass the data needed for each part of the app through the intents
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import HTTPConnect.Connection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Accounts extends Activity {

    /* Used for the list view */
    private ArrayList<String> accountStrings;
    private String username;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        /* Get the list view */
        ListView accountsList=(ListView)findViewById(R.id.listView);

        Intent i = getIntent();
        username = i.getStringExtra(IntentConstants.USERNAME);
        date = i.getStringExtra(IntentConstants.DATE);

        //Log.d("Username", username);

        accountStrings = new ArrayList<String>();
        getAccounts();
        // Create The Adapter with passing ArrayList as 3rd parameter
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, accountStrings);
        // Set The Adapter
        accountsList.setAdapter(arrayAdapter);

        /* Setup a listener if a user clicks one of the accounts, so they
         * Can go and find more information about that account, launches the
         * statement option.
         */
        accountsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(Accounts.this, Statement.class);
                String message = accountStrings.get(position).split(":")[0];
                String balance = accountStrings.get(position).split(":")[1];
                intent.putExtra("USERNAME", username);
                intent.putExtra("ACCOUNT_NUM", message);
                intent.putExtra("BALANCE", balance);
                intent.putExtra(IntentConstants.DATE, date);
                startActivity(intent);

                ((KillApp) Accounts.this.getApplication()).setStatus(false);
            }
        });
    }



    void getAccounts()
    {
        /* What this method would really do is get the data from the webserver
         * for a certain users accounts. This will then take that data and display it
         * with all of the different ammounts in each account. However, this is not currently
         * set up on the web server
         */


        Connection hc = new Connection(this);// trying to pass the activity to the coonection (not sure if this is legal though)

        try {
            String result = hc.execute("TYPE","SAA",IntentConstants.USERNAME, username ).get();

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
                                autoLogout();
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
                    accountStrings.add(insideObject.getString("account_number") + " : £" + insideObject.getString("avail_balance") + "  >");
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        /* Show notification icon in menu bar */
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.getItem(1);
        GetNotification notif = new GetNotification();


        if(notif.getNotifications(this, username, date)) {
            Log.d("Notif Change", "IN HERE");
            item.setIcon(R.drawable.ic_action_notify);
        }
        else
        {
            Log.d("Notif Change", "IN There");
            item.setIcon(R.drawable.globe);
        }

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
            this.finish();
            //Intent intent = new Intent(this, MainActivity.class);
            //startActivity(intent);
        }
        else if (id == R.id.action_notifications) {
            Intent intent = new Intent(this, Notifications.class);
            intent.putExtra(IntentConstants.USERNAME, username);
            intent.putExtra(IntentConstants.DATE, date);
            startActivity(intent);
            ((KillApp) this.getApplication()).setStatus(false);
        }
        /*else if (id == R.id.action_location) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
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
            //only finish is needed for all other apps apart from the main screen
            //as the login screen only needs to be called once, and by calling finish
            //it creates a domino affect to all of the other activities
            finish();
        }
        else
        {
            //each time the app resumes and it wasnt killed, the variable needs to be reset
            ((KillApp) this.getApplication()).setStatus(true);
            invalidateOptionsMenu();
        }

        super.onResume();

    }

    //overriding the on back pressed method (for the built in back button) so
    //its status can be set to false, so it doesnt launch the login on on resume
    @Override
    public void onBackPressed() {
        ((KillApp) this.getApplication()).setStatus(false);
        finish();

    }

    /* This is the code needed to log the user out if their session has timed out...
     * because android is totally asyncronous it wont let me just put it after the
     * popup box code, and it wont let me put it inside the box close stuff... no idea
     * why... but its here anyway
     */
    private void autoLogout()
    {
        Connection hc = new Connection(this);
        try
        {
            hc.execute("TYPE","LOGOUT", IntentConstants.USERNAME, username);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


        ((KillApp) this.getApplication()).setStatus(false);
        finish();
        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        //login again
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
}
