package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class Settings extends Activity {

    private ArrayList<String> optionsList;
    private String username;
    private String date;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Intent i = getIntent();
        username = i.getStringExtra("ACCOUNT_USERNAME");
        date = i.getStringExtra("DATE");

        ListView optionList=(ListView)findViewById(R.id.listOptions);
        optionsList=  new ArrayList<String>();
        getOptions();
        ArrayAdapter<String> arrayAdapterO =
                new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, optionsList);
        optionList.setAdapter(arrayAdapterO);

        optionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                if (position==0) {
                    Intent intent = new Intent(Settings.this, Prefer.class);
                    String message = optionsList.get(position);
                    intent.putExtra("com.example.ListViewTest.MESSAGE", message);
                    startActivity(intent);
                    ((KillApp) Settings.this.getApplication()).setStatus(false);
                }else if (position==1) {
                    Intent intent = new Intent(Settings.this, ChangeAccountName.class);
                    String message = optionsList.get(position);
                    intent.putExtra("com.example.ListViewTest.MESSAGE", message);
                    startActivity(intent);
                    ((KillApp) Settings.this.getApplication()).setStatus(false);
                }else if(position==2){
                    Intent intent = new Intent(Settings.this,ChangePassword.class);
                    String message = optionsList.get(position);
                    intent.putExtra("com.example.ListViewTest.MESSAGE", message);
                    startActivity(intent);
                    ((KillApp) Settings.this.getApplication()).setStatus(false);
                }else if(position==3){
                        Intent intent = new Intent(Settings.this, LegalMaps.class);
                        String message = optionsList.get(position);
                        intent.putExtra("com.example.ListViewTest.MESSAGE", message);
                        startActivity(intent);
                        ((KillApp) Settings.this.getApplication()).setStatus(false);
                }
            }
        });

    }

       void getOptions(){
           optionsList.add("Preferences");
           optionsList.add("Change Account Name");
           optionsList.add("Change Password");
           optionsList.add("Legal Notices");
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
            intent.putExtra("ACCOUNT_USERNAME", username);
            intent.putExtra("DATE", date);
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
}
