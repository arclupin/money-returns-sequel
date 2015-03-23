package com.ncl.team5.lloydsmockup;

/* This is the payments class, which deals with the payments screen and issuing the
 * command to the server to pay a particular user. It uses a tab view so the user can choose
 * to add a new payee or to select an old one from their last 3 payees.
 */

//TODO: need to edit and test getRecentTrans method, needs more database data

/* Lots of imports */
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import Fragment.Payment_FragmentPagerAdapter;
import HTTPConnect.Connection;


/* Extends fragment activity so we can use fragment views for the tabs */
public class Payments extends FragmentActivity {

    /* Private methods needed for rest of class */
    private String username;
    private List<String> accountStrings = new ArrayList<String>();
    private Payment_FragmentPagerAdapter fragmentPagerAdapter;
    private ViewPager pager;
    private TabHost tabs;
    private List<String> recentAcc = new ArrayList<String>();
    private List<String> fromSC = new ArrayList<String>();

    /* Runs when the activity is started */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        /* Just some boiler plate stuff */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        /* Get the username via the intent */
        Intent intent = getIntent();
        username = intent.getStringExtra("ACCOUNT_USERNAME");

        /* Gets all of the users accounts from the server, and populates accountStrings with them */
        getAccounts();

        /* set up layout (tab view + swipe view for pages) */
        tabs = (TabHost) findViewById(R.id.tabhost);
        TabHost tabs = (TabHost) findViewById(R.id.tabhost);
        tabs.setup();

        /* Set up the two tabs */
        TabHost.TabSpec spec = tabs.newTabSpec("tag1"); // add tag 1
        spec.setContent(R.id.tab1);
        spec.setIndicator("Existing Recipient");
        tabs.addTab(spec);

        TabHost.TabSpec spec2 = tabs.newTabSpec("tag2");// add tag 2 (we could keep spec objects separate for easy later reference
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("New Recipient");
        tabs.addTab(spec2);

        /* set up tab's UI */
        for (int i = 0; i < 2; i++) {
            LinearLayout childView = (LinearLayout) tabs.getTabWidget().getChildTabViewAt(i); // get the tab view
            childView.setBackgroundResource(R.drawable.tab_color); // set up the tab's bg
            for (int m = 0; m < childView.getChildCount(); m++) {
                Log.d("child " + m, childView.getChildAt(m).toString()); // some debugging logs for the tab view
            }
            TextView temp = (TextView) childView.getChildAt(1);
            temp.setTextColor(Color.parseColor("#1E2710")); // set up the tab text's color
        }

        /* set up the view pager for displaying pages (swipe view) */
        pager = (ViewPager) findViewById(R.id.payment_pager);
        fragmentPagerAdapter = new Payment_FragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(fragmentPagerAdapter); // set up the data for views

        /* temp_ vars for registering events */
        final TabHost temp_tabs = tabs;
        final ViewPager temp_pager = pager;

        //registering tab and page switch events (tab switch <-> view switch)
        // 1. on tab switch -> view switch
        // (needed because we don't use the default frame layout of TabHost as we want to achieve the swipe view hassle-free using the ViewPager)

        /* Event Handler for the tabs */
        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                temp_pager.setCurrentItem(temp_tabs.getCurrentTab(), true); // change view
            }
        });

       /* Event handler for the pager to start the tab change listener */
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                temp_tabs.setCurrentTab(position); // change tab
            }
        });
    }

    /* Sets up the buttons in the action bar */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /* Back button in bar pressed, dont kill app */
        if (id == R.id.action_backHome) {
            ((KillApp) this.getApplication()).setStatus(false);
            this.finish();
        }
        /* Launch notifications activity, dont kill app */
        else if (id == R.id.action_notifications) {
            Intent intent = new Intent(this, Notifications.class);
            startActivity(intent);
            ((KillApp) this.getApplication()).setStatus(false);
        }

        return super.onOptionsItemSelected(item);
    }


    /* Creates the menu at the top of the activity */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu; this adds items to the action bar if it is present. */
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /* this is ran when the user presses the payment button, nothing returned, message box show be shown */
    public void btnMakePay(View view) {

        SharedPreferences sp = getSharedPreferences("transinsession", 0);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean("IN_SESSION", true);
        edit.commit();

        /* Set up some variables */
        int tabNo = tabs.getCurrentTab();
        String toAccountNum;
        String sortCode;
        String amount;
        String fromAccountNum;
        String fromSort;

        /* Tab is 0 when user presses button in Recent payee tab */
        if (tabNo == 0) {
            /* gets the values of all of the UI components */
            toAccountNum = ((Spinner) findViewById(R.id.Payment_Old_spinner2)).getSelectedItem().toString();
            int pos = ((Spinner) findViewById(R.id.Payment_Old_spinner2)).getSelectedItemPosition();
            sortCode = fromSC.get(pos);
            amount = ((TextView) findViewById(R.id.Payment_Old_TextField_Amount)).getText().toString();
            fromAccountNum = ((Spinner) findViewById(R.id.Payment_Old_spinner1)).getSelectedItem().toString();
            pos = ((Spinner) findViewById(R.id.Payment_Old_spinner1)).getSelectedItemPosition();
            fromSort = fromSC.get(pos);
        }
        /* Tab is 1 when user presses button in new payee tab */
        else {
            /* gets the values of all of the UI components */
            toAccountNum = ((TextView) findViewById(R.id.Payment_New_Payto_Acc_TextField)).getText().toString();
            sortCode = ((TextView) findViewById(R.id.Payment_New_Payto_SC_TextField)).getText().toString();
            amount = ((TextView) findViewById(R.id.Payment_New_TextField_Amount)).getText().toString();
            fromAccountNum = ((Spinner) findViewById(R.id.Payment_New_spinner1)).getSelectedItem().toString();
            int pos = ((Spinner) findViewById(R.id.Payment_Old_spinner1)).getSelectedItemPosition();
            fromSort = fromSC.get(pos);
        }

        /* Log of the info */
        Log.d("PaymentStuff", toAccountNum + ":" + sortCode + ":" + amount + ":" + fromAccountNum);

        /* checks all of the inputs to make sure they are all correct */
        /* Account number must be length 8 and all numbers*/
        if (!(toAccountNum.length() == 8) && toAccountNum.matches("[0-9]+]")) {
            //error
            new CustomMessageBox(this, "Account number is not in the correct format");
            return;
        }
        /* Checks sort code, allows both dashes and no dashes */
        if (!sortCode.matches("[0-9][0-9][-]?[0-9][0-9][-]?[0-9][0-9]") && sortCode.length() <= 8) {
            //error
            new CustomMessageBox(this, "Sort code is not in the correct format");
            return;
        }
        /* Checks the amount */
        if (!amount.matches("[£]?[0-9]+([.][0-9][0-9])?")) {
            //error
            new CustomMessageBox(this, "Incorrect amount specified");
            return;
        }

        /* start up the connection */
        Connection connect = new Connection(this);
        String result;

        try {
            /* Command required to make a payment, takes username, to account, from account, both sort codes and amount
             * Returns: JSON String */
            result = connect.execute("TYPE", "PAY", "USR", username, "PAYTO", toAccountNum, "PAYFROM", fromAccountNum, "PAYFROM_SC", fromSort, "PAYTO_SC", sortCode,  "AMOUNT", amount).get();

            /* Turns String into JSON object, can throw JSON Exception */
            JSONObject jo = new JSONObject(result);

            /* Check if the user has timed out */
            if (jo.getString("expired").equals("true")) {

                /* Display message box and auto logout user */
                AlertDialog.Builder errorBox = new AlertDialog.Builder(this);
                errorBox.setMessage("Your session has been timed out, please login again")
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
            /* Payment was successful, show message box */
            else if (jo.getString("status").equals("true")) {
                /* Clear all text boxes */
                ((TextView) findViewById(R.id.Payment_New_Payto_SC_TextField)).setText("");
                ((TextView) findViewById(R.id.Payment_New_Payto_Acc_TextField)).setText("");
                ((TextView) findViewById(R.id.Payment_New_TextField_Amount)).setText("");
                ((TextView) findViewById(R.id.Payment_Old_TextField_Amount)).setText("");

                /* show payment made message */
                new CustomMessageBox(this, "Your payment has been made successfully");
            }
            /* There was an error indide the status return field, display appropriate error message */
            else
            {
                /* give more info on the error here, no money taken from account */
                /* Use the cause results to display certain error messages */
                if(jo.getString("cause").equals("insufficient"))
                {
                    /* insufficient funds in account */
                    new CustomMessageBox(this, "There are not enough funds in your account for this transaction");
                }
                else
                {
                    /* Unknown error :( */
                    new CustomMessageBox(this, "An unknown error occurred, the transaction was not completed ");
                }
            }

        }
        /* Catch the exceptions */
        catch (JSONException jse)
        {
            /* Error in the JSON response */
            new CustomMessageBox(this, "There was an error in the server response");
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

    /* populates accountStrings with the users accounts and then displays them in the spinner */
    public void getAccounts() {

        /* clears account strings each time so the same accounts are not added every time */
        accountStrings.clear();

        /* Start a new connection */
        Connection hc = new Connection(this);

        try {
            /* Command to get the accounts, returns JSON string */
            String result = hc.execute("TYPE", "SAA", "USR", username).get();

            /* Convert string to JSON object, can throw JSON Exception */
            JSONObject jo = new JSONObject(result);

            if(jo.getString("expired").equals("true"))
            {
                /* Display error message and log user out as they have expired */
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
            else {
            /* JSONArray needed as accounts are returned as an array */
                JSONArray jsonArray = jo.getJSONArray("accounts");

            /* Loop through the array */
                for (int i = 0; i < jsonArray.length(); i++) {
                /* Creates an object for each element in array, then strips the needed values from it */
                    JSONObject insideObject = jsonArray.getJSONObject(i);

                    accountStrings.add(insideObject.getString("account_number"));
                    fromSC.add(insideObject.getString("sort_code"));
                }
            }
        }
        /* Catch any errors */
        catch (JSONException jse)
        {
            /* Exception for when the JSON cannot be parsed correctly */
            new CustomMessageBox(this, "There was an error in the server response");
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

    /* Basic get methods for the two lists so that the fragments can access them, without needing the
     * variables to be public */
    public List<String> getAccountString()
    {
        return accountStrings;
    }

    public List<String> getRecentAccString()
    {
        return recentAcc;
    }


    /* gets the last 3 recent transactions for the account, which is used to get the most recent payees */
    //TODO Properly test when database has been populated with more data
    public void getRecentTrans(String accountNum) {
        /* Clear the list so it gets reset each time the activity is visited */
        recentAcc.clear();

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
                                autoLogout();
                            }
                        });
                AlertDialog alert = errorBox.create();
                alert.show();
            }
            else {
                /* Array needed as transaction returned inside JSON array */
                JSONArray jsonArray = jo.getJSONArray("transactions");

                /* There are no transactions if the array length is zero */
                if (jsonArray.length() == 0) {
                    /* Swaps it onto the second tab if there are no transactions */
                    tabs.setCurrentTab(1);
                    return;
                }

                /* add the last three payees to the spinner */
                for (int i = 0; i < jsonArray.length(); i++) {

                    /* Gets an object of each element in the array */
                    JSONObject insideObject = jsonArray.getJSONObject(i);

                    /* Gets the date field and parse it into a date variable, can throw an exception but never should... */
                    String date = insideObject.getString("Time");
                    Date formatted = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);

                    /* If size is less than three then just add the account number into the recentAcc list */
                    if (recentAcc.size() < 3) {

                        /* If size is zero then just add the object if the payer is the account */
                        //TODO needs properly testing when database has been filled with more data
                        if (recentAcc.size() == 0) {

                            /* If payer is the current account then add */
                            if (insideObject.getString("Payer").equals(accountNum)) {
                                recentAcc.add(insideObject.getString("Payee"));
                            }
                        }
                        /* if the size > 0 but the account is already in the list, then dont add */
                        //TODO, edit this method, think it needs some sort of loop outside of the if...
                        else if (i < recentAcc.size() && !recentAcc.get(i).toString().equals(insideObject.getString("Payee"))) {
                            if (insideObject.getString("Payer").equals(accountNum)) {
                                recentAcc.add(insideObject.getString("Payee"));
                            }
                        }
                    }
                    /* Size > 3 */
                    else {
                        /* Compare dates and then add them to recentAcc in date order */
                        //TODO needs properly testing when more data added
                        for (int j = 0; j < 3; j++) {

                            String tempDate = recentAcc.get(i).toString().split(" ~ ")[2].toString();
                            Date temp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tempDate);

                            if (temp.compareTo(formatted) < 0) {
                                if (insideObject.getString("Payer").equals(accountNum)) {
                                    recentAcc.add(insideObject.getString("Payee"));
                                }
                            }
                        }
                    }
                }
            }
        }
        /* Catch the exceptions */
        catch (JSONException jse)
        {
            /* Exception for when the JSON cannot be parsed correctly */
            new CustomMessageBox(this, "There was an error in the server response");
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
    public void onResume() {

        if (((KillApp) this.getApplication()).getStatus()) {
            /* Kills the app if kill app is true */
            finish();
        }
        else {
            /* each time the app resumes and it wasnt killed, the variable needs to be reset */
            ((KillApp) this.getApplication()).setStatus(true);
        }

        /* Has to call the super method */
        super.onResume();
    }


    /* overriding the on back pressed method (for the built in back button) so
     * its status can be set to false, so it doesnt launch the login on on resume */
    @Override
    public void onBackPressed() {
        ((KillApp) this.getApplication()).setStatus(false);
        finish();
    }


    /* Called from inside the error box that appears on timeout */
    private void autoLogout() {

        /* Start a new connection */
        Connection hc = new Connection(this);
        try {
            /* try to execute a logout on the server */
            hc.execute("TYPE", "LOGOUT", "USR", username);
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