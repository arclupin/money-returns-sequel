package com.ncl.team5.lloydsmockup;

/* Transfers class
 * Allows a user to transfer money between their own accounts
 * only allows a user on if they have more than one account */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import HTTPConnect.Connection;


public class Transfers extends Activity {

    /* -- Variables -- */
    /* Strings */
    private String username;
    private String date;

    /* Collections */
    private List<String> accountStrings = new ArrayList<String>();
    private List<String> sortCodes = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* gets the username from the intent */
        Intent i = getIntent();
        username = i.getStringExtra(IntentConstants.USERNAME);
        date = i.getStringExtra(IntentConstants.DATE);

        /* Gets all of the account numbers for the user */
        getAccounts();

        /* Setup the spinners to use the account number data */
        setContentView(R.layout.activity_transfers);
        Spinner s = (Spinner) findViewById(R.id.spinnerFrom);
        Spinner s2 = (Spinner) findViewById(R.id.spinnerTo);
        ArrayAdapter<String> a = new ArrayAdapter<String>(this, R.layout.spinner_text_colour, accountStrings);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(a);
        s2.setAdapter(a);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
            //item.setIcon(R.drawable.ic_action_email);
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

        return super.onOptionsItemSelected(item);
    }

    /* ====================================================
     * onClick for make transfers button
     *
     * @params : View
     *
     * @returns : void
     *
     * @use : Uses the payments stuff, but just allows
     *        transfers between own accounts, as that
     *        is all that can be selected in the spinners
     * ==================================================== */
    public void btnMakeTrans(View view) {
        /* gets all of the data from the spinners and the sort code lists */
        String fromAccount = ((Spinner)findViewById(R.id.spinnerFrom)).getSelectedItem().toString();
        String toAccount = ((Spinner)findViewById(R.id.spinnerTo)).getSelectedItem().toString();
        String amount = ((TextView)findViewById(R.id.amountText)).getText().toString();
        int pos = ((Spinner) findViewById(R.id.spinnerFrom)).getSelectedItemPosition();
        String fromSort = sortCodes.get(pos);
        pos = ((Spinner) findViewById(R.id.spinnerTo)).getSelectedItemPosition();
        String sortCode = sortCodes.get(pos);

        /* Put in checks to make sure the account numbers and such are correct */
        if(fromAccount.equals(toAccount))
        {
            //error
            //cannot transfer money between same account
            new CustomMessageBox(this, "Cannot transfer money between the same account");
            return;
        }
        if(!amount.matches("[£]?[0-9]+([.][0-9][0-9])?"))
        {
            //error
            new CustomMessageBox(this, "Incorrect amount specified");
            return;
        }

        /* start up the connection */
        Connection connect = new Connection(this);
        String result;

        try
        {
            /* Get the result from the web server */
            result = connect.execute("TYPE", "PAY", IntentConstants.USERNAME, username, "PAYTO", toAccount, "PAYFROM", fromAccount, "AMOUNT", amount, "PAYFROM_SC", fromSort, "PAYTO_SC", sortCode).get();

            /* Create a JSON object */
            JSONObject jo = new JSONObject(result);

            /* Check expired */
            if(jo.getString("expired").equals("true"))
            {

                //This uses the same code as the main menu does to start the login, only this time it is run when the user
                //has timed out
                AlertDialog.Builder errorBox = new AlertDialog.Builder(this);
                errorBox.setMessage("You have been timed out, please login again")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                autoLogout();
                            }
                        });
                AlertDialog alert = errorBox.create();
                alert.show();
            }
            /* Transfer has been made */
            else if (jo.getString("status").equals(StatusConstants.OK))
            {
                /* show transfer made screen */
                new CustomMessageBox(this, "Your transaction has been made successfully");
                ((TextView) findViewById(R.id.amountText)).setText("");
            }
            else
            {
                /* give more info on the error here, no money taken from account */
                if(jo.getString("cause").equals(StatusConstants.UNKNOWN))
                {
                    /* Unknown error :( */
                    new CustomMessageBox(this, "An unknown error occurred, the transaction was not completed ");
                }
                else if (jo.getString("cause").equals(StatusConstants.ACCOUNT_INFO))
                {
                    /* incorrect account info */
                    new CustomMessageBox(this, "Incorrect account information");
                }
                else if(jo.getString("cause").equals(StatusConstants.INSUFFICIENT))
                {
                    /* insufficient funds in account */
                    new CustomMessageBox(this, "There are not enough funds in your account for this transaction");
                }

            }

        }
        /* Catch the exceptions */
        catch (JSONException jse)
        {
            /* Error in the JSON response */
            //new CustomMessageBox(this, "There was an error in the server response");
            jse.printStackTrace();
        }
        catch (InterruptedException interex)
        {
            /* Caused when the connection is interrupted */
            new CustomMessageBox(this, "Connection has been interrupted");
            interex.printStackTrace();
        }
        catch (ExecutionException ee)
        {
            /* No idea when this is caused but it throws it... */
            new CustomMessageBox(this, "Execution Error");
            ee.printStackTrace();
        }
        catch (Exception e) {
            /* Failsafe if something goes utterly wrong */
            new CustomMessageBox(this, "An unknown error occurred");
            e.printStackTrace();
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

         /* Change color of button */
        if(MainActivity.getColour(this) == Color.WHITE)
        {
            (findViewById(R.id.button1)).setBackground(new ColorDrawable(MainActivity.getColor()));
        }
        else
        {
            findViewById(R.id.button1).setBackground(new ColorDrawable(MainActivity.getColour(this)));
        }

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

    /* ====================================================
     * GetAccounts method
     *
     * @params : none
     *
     * @returns : void
     *
     * @use : Gets ll of the accounts for the current user
     *        and stores them in accountStrings, as well
     *        as the sort code for each account in sortCodes
     * ==================================================== */
    public void getAccounts()
    {
        /* Create new connection */
        Connection hc = new Connection(this);// trying to pass the activity to the coonection (not sure if this is legal though)

        try {
            /* get the resut from the server */
            String result = hc.execute("TYPE","SAA",IntentConstants.USERNAME, username ).get();

            /* create a JSON object from the result */
            JSONObject jo = new JSONObject(result);

            /* Get the account number and sort code from the JSON array */
            JSONArray jsonArray = jo.getJSONArray("accounts");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject insideObject = jsonArray.getJSONObject(i);
                accountStrings.add(insideObject.getString("account_number"));
                sortCodes.add(insideObject.getString("sort_code"));
            }

            /* need more than 1 account to make transfers */
            if(accountStrings.size() < 2)
            {
                final Transfers t = this;

                /* Kill activity (back to main menu) */
                AlertDialog.Builder errorBox = new AlertDialog.Builder(this);
                errorBox.setMessage("You must have more than one account to make transfers")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                ((KillApp) t.getApplication()).setStatus(false);
                                t.finish();
                            }
                        });
                AlertDialog alert = errorBox.create();
                alert.show();
            }
        }
        /* Catch the exceptions */
        catch (JSONException jse)
        {
            /* Error in the JSON response */
            //new CustomMessageBox(this, "There was an error in the server response");
            jse.printStackTrace();
        }
        catch (InterruptedException interex)
        {
            /* Caused when the connection is interrupted */
            new CustomMessageBox(this, "Connection has been interrupted");
            interex.printStackTrace();
        }
        catch (ExecutionException ee)
        {
            /* No idea when this is caused but it throws it... */
            new CustomMessageBox(this, "Execution Error");
            ee.printStackTrace();
        }
        catch (Exception e) {
            /* Failsafe if something goes utterly wrong */
            new CustomMessageBox(this, "An unknown error occurred");
            e.printStackTrace();
        }
    }

    /* Called from inside the error box that appears on timeout */
    private void autoLogout() {

        /* Start a new connection */
        Connection hc = new Connection(this);
        try {
            /* try to execute a logout on the server */
            hc.execute("TYPE", "LOGOUT", IntentConstants.USERNAME, username);
        }
        catch (Exception e) {
            /* Doesnt really need a detailed error as user is logged out anyway, just print stack trace */
            e.printStackTrace();
        }

        /* Has to kill the app whether it has managed to send a logout or not */
        ((KillApp) this.getApplication()).setStatus(false);
        finish();
        Intent intent1 = new Intent(getApplicationContext(), Login.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent1);
    }
}
