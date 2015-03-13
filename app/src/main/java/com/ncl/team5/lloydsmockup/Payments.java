package com.ncl.team5.lloydsmockup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import FragPager.FragmentOldAccount;
import FragPager.Payment_FragmentPagerAdapter;
import HTTPConnect.Connection;


public class Payments extends FragmentActivity{

    /* Private methods needed for rest of class */
    private static String username;
    public static List<String> accountStrings = new ArrayList<String>();
    private Payment_FragmentPagerAdapter fragmentPagerAdapter;
    private ViewPager pager;
    private TabHost tabs;
    public static List<String> recentAcc = new ArrayList<String>();
    private Spinner s2;

    private String username;
    private List<String> accountStrings = new ArrayList<String>();
    private TabHost tabs;
    private List<String> recentAcc = new ArrayList<String>();
    private Spinner s2;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //get the username via the intent
        Intent intent = getIntent();
        username = intent.getStringExtra("ACCOUNT_USERNAME");

        getAccounts();
        setContentView(R.layout.activity_payments);
        // set up layout (tab view + swipe view for pages)
        // My idea is that we achieve tab view using TabHost (As the solution with ActionBar is deprecated)
        // and we achieve swipe view using ViewPager so basically the FrameLayout of TabHost is just a placeholder for the tab view.


//        tabs.setup();
        /*setTabColour(tabs);*/
        tabs=(TabHost)findViewById(R.id.tabhost);


        TabHost tabs = (TabHost) findViewById(R.id.tabhost);
        tabs.setup();

        TabHost.TabSpec spec = tabs.newTabSpec("tag1"); // add tag 1


        spec.setContent(R.id.tab1);
        spec.setIndicator("Existing Recipient");
        tabs.addTab(spec);
        Spinner s = (Spinner) findViewById(R.id.spinnerFrom);
        s2 = (Spinner) findViewById(R.id.spinnerFromPayments);
        ArrayAdapter<String> a = new ArrayAdapter<String>(this, R.layout.spinner_text_colour, accountStrings);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(a);
        s2.setAdapter(a);

        //event handler for the spinner on the second tab
        s2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                getRecentTrans(s2.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //NOTHING IN HERE
                //Just needed it for the event handler :/

            }
        });
    }

        TabHost.TabSpec spec2 = tabs.newTabSpec("tag2");// add tag 2 (we could keep spec objects separate for easy later reference
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("New Recipient");
        tabs.addTab(spec2);

        // set up tab's UI
        for (int i = 0; i < 2; i++) {
            LinearLayout childView = (LinearLayout) tabs.getTabWidget().getChildTabViewAt(i); // get the tab view
            childView.setBackgroundResource(R.drawable.tab_color); // set up the tab's bg
            for (int m = 0; m < childView.getChildCount(); m++) {
                Log.d("child " + m, childView.getChildAt(m).toString()); // some debugging logs for the tab view
            }
            TextView temp = (TextView) childView.getChildAt(1);
            temp.setTextColor(Color.parseColor("#1E2710")); // set up the tab text's color
        }

        // set up the view pager for displaying pages (swipe view)
        pager = (ViewPager) findViewById(R.id.payment_pager);
        fragmentPagerAdapter = new Payment_FragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(fragmentPagerAdapter); // set up the data for views

        // temp_ vars for registering events
        final TabHost temp_tabs = tabs;
        final ViewPager temp_pager = pager;

        //registering tab and page switch events (tab switch <-> view switch)
        // 1. on tab switch -> view switch
        // (needed because we don't use the default frame layout of TabHost as we want to achieve the swipe view hassle-free using the ViewPager)
        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                temp_pager.setCurrentItem(temp_tabs.getCurrentTab(), true); // change view
            }
        });

        // 2. on view switch -> tab switch (trigger tab switch on page switch)
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                temp_tabs.setCurrentTab(position); // change tab
            }
        });



  /*  public void setTabColour(TabHost tab) {
        TabHost tabsH=(TabHost)findViewById(R.id.tabhost);
        int total = tab.getTabWidget().getChildCount();
        for(int i=0;i<total;i++) {
                tabsH.getTabWidget().setStripEnabled(true);
                tabsH.getTabWidget().setBackgroundResource(R.drawable.tab_select);

        }
    }*/




        //event handler for the spinner on the second tab
//        s2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                getRecentTrans(s2.getItemAtPosition(position).toString());
//
//                //s2.setAdapter(a2);
//            }
//push branch
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                //NOTHING IN HERE
//                //Just needed it for the event handler :/
//
//            }
//        });
    }




/**/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
/*
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
            startActivity(intent);
            ((KillApp) this.getApplication()).setStatus(false);
        }

        return super.onOptionsItemSelected(item);
    }


    /* My Functions are under here */


    //this is ran when the user presses make a payment
    public void btnMakePay(View view) {

        int tabNo = tabs.getCurrentTab();
        String toAccountNum;
        String sortCode;
        String amount;
        String fromAccountNum;

        if(tabNo == 0)
        {
            //gets the values of all of the UI components
            toAccountNum = ((TextView)findViewById(R.id.amountText)).getText().toString();
            sortCode = ((TextView)findViewById(R.id.sortCodeTxt)).getText().toString();
            amount = ((TextView)findViewById(R.id.amountPay)).getText().toString();
            fromAccountNum = ((Spinner)findViewById(R.id.spinnerFrom)).getSelectedItem().toString();
        }
        else
        {
            toAccountNum = ((Spinner)findViewById(R.id.spinnerTo)).getSelectedItem().toString();
            //will probably need to get the last 3 transactions or something to populate the spinner as well
            sortCode = "202020";
            amount = ((TextView)findViewById(R.id.amountTextPay)).getText().toString();
            fromAccountNum = ((Spinner)findViewById(R.id.spinnerFromPayments)).getSelectedItem().toString();
        }


        Log.d("PaymentStuff", toAccountNum + ":" + sortCode + ":" + amount + ":" + fromAccountNum);

        //checks all of the inputs to make sure they are all correct
        if (!(toAccountNum.length() == 8)) {
            //error
            new CustomMessageBox(this, "Account number is not in the correct format");
            return;
        }
        if(!sortCode.matches("[0-9][0-9][-]?[0-9][0-9][-]?[0-9][0-9]"))
        {
            //error
            new CustomMessageBox(this, "Sort code is not in the correct format");
            return;
        }

        if (!amount.matches("[£]?[0-9]+([.][0-9][0-9])?")) {
            //error
            new CustomMessageBox(this, "Incorrect amount specified");
            return;
        }


        //start up the connection
        Connection connect = new Connection(this);
        String result;

        try {
            //now works with the ui and passed that values of the text boxes
            result = connect.execute("TYPE", "PAY", "USR", username, "PAYTO", toAccountNum, "PAYFROM", fromAccountNum, "AMOUNT", amount).get();


            JSONObject jo = new JSONObject(result);

            if (jo.getString("expired").equals("true")) {

                //This uses the same code as the main menu does to start the login, only this time it is run when the user
                //has timed out
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


                //login again
            } else if (jo.getString("status").equals("true")) {
                //show payment made screen
                //Uses the CustomMessageBox class that I made to make the
                //code easier to read
                new CustomMessageBox(this, "Your payment has been made successfully");
            } else {
                //give more info on the error here, no money taken from account
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //populates a list with the users accounts and then displays them in the spinner
    public void getAccounts() {
        Connection hc = new Connection(this);
        try {
            String result = hc.execute("TYPE", "SAA", "USR", username).get();


            JSONObject jo = new JSONObject(result);

            JSONArray jsonArray = jo.getJSONArray("accounts");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject insideObject = jsonArray.getJSONObject(i);
                accountStrings.add(insideObject.getString("account_number"));
            }


        } catch (Exception e) {
            //give the user a message about being unable to connect. Take them back
            //to the main menu i think...
            e.printStackTrace();
        }

    }

    private void getRecentTrans(String accountNum)
    {
        /* This method would work the same as the other get method in accounts,
         * but look for the transactions in the past 30 days for this account.
         */

        Connection hc = new Connection(this);

        try {
            String result = hc.execute("TYPE", "TRANSLIST", "USR", username, "ACC_NUMBER", accountNum).get();

            JSONObject jo = new JSONObject(result);

    //gets the recent trancastions for the account, which is used to get the most recent
    //payees
    public void getRecentTrans(String accountNum) {
        /* This method would work the same as the other get method in accounts,
         * but look for the transactions in the past 30 days for this account.
         */

        recentAcc.clear();

        Connection hc = new Connection(this);

        try {
            String result = hc.execute("TYPE", "TRANSLIST", "USR", username, "ACC_NUMBER", accountNum).get();

            JSONObject jo = new JSONObject(result);

            if (jo.getString("expired").equals("true")) {
                //logout
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
            else
            {

                String amountString;
                JSONArray jsonArray = jo.getJSONArray("transactions");

                if(jsonArray.length() == 0)
                {

                    return;
                }

                    //add the last three payees to the spinner, currently not working
                for (int i = 0; i < jsonArray.length(); i++)
                {

                    JSONObject insideObject = jsonArray.getJSONObject(i);
                    String date = insideObject.getString("Time");

                    Date formatted = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);

                    if (recentAcc.size() < 3) {
                        if (insideObject.getString("Amount").matches("[0-9]+")) {
                            amountString = insideObject.getString("Amount") + ".00";
                        } else {
                            amountString = insideObject.getString("Amount");
                        }

                        for(int j = 0; j < 3; j++)
                        {
                            if(recentAcc.size() == 0)
                            {
                                if(insideObject.getString("Payer").equals(accountNum)) {
                                    recentAcc.add(insideObject.getString("Payee"));
                                }
                            }

                            if(!recentAcc.get(i).toString().split(" ~ ")[0].toString().equals(insideObject.getString("Payee")))
                            {
                                if(insideObject.getString("Payer").equals(accountNum)) {
                                    recentAcc.add(insideObject.getString("Payee"));
                                }
                            }
                        }




                    } else {
                        for (int j = 0; j < 3; j++) {
                            String tempDate = recentAcc.get(i).toString().split(" ~ ")[2].toString();
                            Date temp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tempDate);

                            if (temp.compareTo(formatted) < 0) {
                                if (insideObject.getString("Amount").matches("[0-9]+")) {
                                    amountString = insideObject.getString("Amount") + ".00";
                                } else {
                                    amountString = insideObject.getString("Amount");
                                }

                                if(insideObject.getString("Payer").equals(accountNum)) {
                                    recentAcc.add(insideObject.getString("Payee"));
                                }

                            }
                        }
                    }



                }



                //Do something here to change the format of the JSON into a sort of map thing...
                //have to talk to danh about how the JSON is returned

            }


        } catch (Exception e) {
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
            //only finish is needed for all other apps apart from the main screen
            //as the login screen only needs to be called once, and by calling finish
            //it creates a domino affect to all of the other activities
            finish();
        } else {
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


    private void autoLogout() {
        Connection hc = new Connection(this);
        try {
            hc.execute("TYPE", "LOGOUT", "USR", username);
        } catch (Exception e) {
            e.printStackTrace();
        }


        ((KillApp) this.getApplication()).setStatus(false);
        finish();
        Intent intent1 = new Intent(getApplicationContext(), Login.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent1);
        //login again
    }
}

