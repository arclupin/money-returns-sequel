package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class Statement extends Activity {

    ArrayList<String> statementList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement);

        /* These next 2 lines create and intent, but instead of launching it,
         * they get the message from the last intent, which for now is just the
         * name of the account
         */
        Intent i = getIntent();
        String result = i.getStringExtra("com.example.ListViewTest.MESSAGE");

        /* Sets the account name from the result text */
        TextView accountName = (TextView) findViewById(R.id.txtChange);
        accountName.setText(result);


        ListView transactions =(ListView)findViewById(R.id.listView);


        statementList = new ArrayList<String>();
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
                String details = getTransDetails();
                errorBox.setMessage(details)
                        .setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
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
        statementList.add("StarBucks : -£3.67");
        statementList.add("Rent : -£350.00");
        statementList.add("Pay : +£60.00");
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
        else if (id == R.id.action_location) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String getTransDetails()
    {
        return "Date: 01/01/2015\n" + "Time: 15:00:00\n" + "Location: Newcastle\n" + "Amount: £20.00";
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
}
