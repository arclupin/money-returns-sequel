package com.ncl.team5.lloydsmockup;

/* This class will allow the user to look at all of their accounts together
 * on one page, and then let the user select and account to see all of the
 * transactions on that account in the last 30 days (stored in the database somewhere).
 * Inside the statements, a user will be able to select more info on each transaction.
 * All of the data from this will be retrieved the same way as the login was done, possibly
 * at the same time, and just pass the data needed for each part of the app through the intents
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class Accounts extends Activity {

    /* Used for the list view */
    private String[] items;
    private ArrayList<String> accountStrings;
    private String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        /* Get the list view */
        ListView accountsList=(ListView)findViewById(R.id.listView);

        Intent i = getIntent();
        username = i.getStringExtra("ACCOUNT_USERNAME");

        accountStrings = new ArrayList<String>();
        getAccounts(username);
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
                String message = accountStrings.get(position);
                intent.putExtra("com.example.ListViewTest.MESSAGE", message);
                startActivity(intent);

                ((KillApp) Accounts.this.getApplication()).setStatus(false);
            }
        });
    }



    void getAccounts(String username)
    {
        /* What this method would really do is get the data from the webserver
         * for a certain users accounts. This will then take that data and display it
         * with all of the different ammounts in each account. However, this is not currently
         * set up on the web server
         */

        /*
        HTTPConnect hc = new HTTPConnect();
        JSONObject jo = hc.execute("SAA", username);

         * this is where the JSON object will be split up... think im going to remove it from the
         * HTTPConnect class and into these classes... or return a list of stings instead. not sure yet
         */

        accountStrings.add("Account 1 : £100.00");
        accountStrings.add("Account 2 : £607.76");
        accountStrings.add("Account 3 : £5098.49");
        accountStrings.add("Account 4 : £0.01");
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
