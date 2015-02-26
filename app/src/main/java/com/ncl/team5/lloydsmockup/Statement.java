package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import HTTPConnect.Connection;


public class Statement extends Activity {

    private List<String> statementList;
    private List<String> transInfo;
    private String username;
    private String accountNum;
    Statement s = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement);

        /* These next 2 lines create and intent, but instead of launching it,
         * they get the message from the last intent, which for now is just the
         * name of the account
         */
        Intent i = getIntent();
        username = i.getStringExtra("USERNAME");
        accountNum = i.getStringExtra("ACCOUNT_NUM");
        String balance = i.getStringExtra("BALANCE");


        /* Sets the account name from the result text */
        TextView accountName = (TextView) findViewById(R.id.txtChange);
        accountName.setTextSize(30);

        if(username.length()+balance.length() > 20)
        {
            accountName.setTextSize(25);
        }

        accountName.setText(username + ":" + balance);


        ListView transactions =(ListView)findViewById(R.id.listView);


        statementList = new ArrayList<String>();
        transInfo = new ArrayList<String>();

        //needs actual data from the other parts of the app adding to it
        getStatement();
        // Create The Adapter with passing ArrayList as 3rd parameter
        final ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, statementList);
        // Set The Adapter
        transactions.setAdapter(arrayAdapter);

        /* Creates a listener for a click on the statement, which will load a small
         * message box that the user will be able to see details about the transaction
         * and also be able to set groups for the transaction for the pie chart */
        transactions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                //Just sets up a basic alert box for now...
                AlertDialog.Builder errorBox = new AlertDialog.Builder(Statement.this);
                String details = transInfo.get(position);
                final String transId = details.split(" ~ ")[0];
                String amount = details.split(" ~ ")[1];
                String time = details.split(" ~ ")[2];
                String date = time.split(" ")[0];
                time = time.split(" ")[1];
                String to = details.split(" ~ ")[3];

                details = "ID : " + transId + "\nAmount : " + amount + "\nDate : " + date + "\nTime : " + time + "\nTo account : " + to;


                errorBox.setMessage(details)
                        .setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("Add to Group", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ((KillApp) s.getApplication()).setStatus(false);
                                Intent i = new Intent(s, GroupChooser.class);
                                i.putExtra("TRANS_ID", transId);
                                startActivity(i);
                            }
                        });
                AlertDialog alert = errorBox.create();
                alert.show();
            }
        });

    }

    void getStatement()
    {
        /* This method would work the same as the other get method in accounts,
         * but look for the transactions in the past 30 days for this account.
         */

        Connection hc = new Connection(this);

        try {
            String result = hc.execute("TYPE", "TRANSLIST", "USR", username, "ACC_NUMBER", accountNum).get();

            JSONObject jo = new JSONObject(result);

            if(jo.getString("expired").equals("true"))
            {
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
                    new CustomMessageBox(this, "Unfortunately you have no transaction information");
                    return;
                }

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject insideObject = jsonArray.getJSONObject(i);
                    if(insideObject.getString("Amount").matches("[0-9]+"))
                    {
                         amountString = insideObject.getString("Amount") + ".00";
                    }
                    else
                    {
                        amountString = insideObject.getString("Amount");
                    }
                    statementList.add(insideObject.getString("Transaction_ID") + " : £" + amountString);
                    transInfo.add(insideObject.getString("Transaction_ID") + " ~ £" + amountString + " ~ " + insideObject.getString("Time") + " ~ " + insideObject.getString("Payee"));
                }



                //Do something here to change the format of the JSON into a sort of map thing...
                //have to talk to danh about how the JSON is returned

            }

        }
        catch (Exception e)
        {

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            startActivity(intent);
            ((KillApp) this.getApplication()).setStatus(false);
        }


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

    private void autoLogout()
    {
        Connection hc = new Connection(this);
        try
        {
            hc.execute("TYPE","LOGOUT", "USR", username);
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
}
