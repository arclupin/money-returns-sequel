package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

import HTTPConnect.Connection;


public class Settings extends Activity {

    private ArrayList<String> optionsList;
    private String username;
    private String date;
    private List<String> accountStrings = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        /* Get intents */
        Intent i = getIntent();
        username = i.getStringExtra(IntentConstants.USERNAME);
        date = i.getStringExtra(IntentConstants.DATE);

        /* populate the list view */
        ListView optionList=(ListView)findViewById(R.id.listOptions);
        optionsList=  new ArrayList<String>();
        getOptions();
        ArrayAdapter<String> arrayAdapterO =
                new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, optionsList);
        optionList.setAdapter(arrayAdapterO);


        /* add on on click listener for the list */
        optionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,

                                   long id) {
                /* laucnh intents based on the position */
                if (position==0) {
                    Intent intent = new Intent(Settings.this, Prefer.class);
                    intent.putExtra(IntentConstants.USERNAME, username);
                    intent.putExtra(IntentConstants.DATE, date);
                    startActivity(intent);
                    ((KillApp) Settings.this.getApplication()).setStatus(false);
                }
                else if (position==1) {
                    Intent intent = new Intent(Settings.this, ChangeAccountName.class);
                    intent.putExtra(IntentConstants.USERNAME, username);
                    intent.putExtra(IntentConstants.DATE, date);
                    startActivity(intent);
                    ((KillApp) Settings.this.getApplication()).setStatus(false);
                }
                else if(position==2){
                    Intent intent = new Intent(Settings.this,ChangePassword.class);
                    intent.putExtra(IntentConstants.USERNAME, username);
                    intent.putExtra(IntentConstants.DATE, date);
                    startActivity(intent);
                    ((KillApp) Settings.this.getApplication()).setStatus(false);
                }
                else if(position == 3)
                {
                    getAccounts();

                    for(int i = 0; i < accountStrings.size(); i++)
                    {
                        SharedPreferences sp = getApplicationContext().getSharedPreferences(username, 0);
                        SharedPreferences.Editor edit = sp.edit();
                        edit.putStringSet("ANALYSIS_GROUPS_" + accountStrings.get(i), new HashSet<String>());
                        edit.putStringSet("TRANS_ID_" + accountStrings.get(i), new HashSet<String>());
                        edit.commit();
                    }


                    new CustomMessageBox(Settings.this, "Removed all groups");


                }
                else if(position==4){
                    Intent intent = new Intent(Settings.this, LegalMaps.class);
                    intent.putExtra(IntentConstants.USERNAME, username);
                    intent.putExtra(IntentConstants.DATE, date);
                    startActivity(intent);
                    ((KillApp) Settings.this.getApplication()).setStatus(false);
                }
            }
        });
    }


   /* gets the options for the preferences */
   void getOptions(){
       optionsList.add("Preferences");
       optionsList.add("Change Account Name");
       optionsList.add("Change Password");
       optionsList.add("Reset All Analysis Groups");
       optionsList.add("Legal Notices");
   }

    /* populates accountStrings with the users accounts and then displays them in the spinner */
    public void getAccounts() {

        /* clears account strings each time so the same accounts are not added every time */
        accountStrings.clear();

        /* Start a new connection */
        Connection hc = new Connection(this);

        try {
            /* Command to get the accounts, returns JSON string */
            String result = hc.execute("TYPE", "SAA", IntentConstants.USERNAME, username).get();

            /* Convert string to JSON object, can throw JSON Exception */
            JSONObject jo = new JSONObject(result);

            if(jo.getString("expired").equals("true"))
            {
              /* Display message box and auto logout user */
                final Connection temp_connect = new Connection(this);
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
            else {
            /* JSONArray needed as accounts are returned as an array */
                JSONArray jsonArray = jo.getJSONArray("accounts");

            /* Loop through the array */
                for (int i = 0; i < jsonArray.length(); i++) {
                /* Creates an object for each element in array, then strips the needed values from it */
                    JSONObject insideObject = jsonArray.getJSONObject(i);

                    accountStrings.add(insideObject.getString("account_number"));

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
