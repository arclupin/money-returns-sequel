package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;


public class GroupChooser extends Activity {

    private String username;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chooser);

        Intent i = getIntent();
        username = i.getStringExtra("ACCOUNT_USERNAME");
        date = i.getStringExtra("DATE");
        /* I will need to populate the current groups here... probably by sending data to the server in some way
         * but need to wait for the tables ot be set up
         */
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Show notification icon in menu bar */
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.getItem(1);
        GetNotification notif = new GetNotification();


        if(notif.getNotifications(this, username)) {
            Log.d("Notif Change", "IN HERE");
            item.setIcon(R.drawable.ic_action_notify);
        }
        else
        {
            Log.d("Notif Change", "IN There");
            item.setIcon(R.drawable.ic_action_email);
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



    public void btnClickAddGroup(View view)
    {
        //will need to make a selection about whether to choose group from text box or spinner... probably last
        //one they used and dynamically remove the data in the other box...

        Spinner groupSpin = (Spinner) findViewById(R.id.groupNameSpinner);
        TextView nameBox = (TextView) findViewById(R.id.nameText);
        TextView groupText = (TextView) findViewById(R.id.newGroupText);

        if(groupSpin.getSelectedItem().toString() == null)
        {
            if(groupText.getText() == null)
            {
                new CustomMessageBox(this, "Please select a group or enter a new one");
            }
            else
            {
                //use text box
            }
        }
        else
        {
            //use spinner
        }

        //add a new group here... probably save in a file somewhere
                ((KillApp) this.getApplication()).setStatus(false);
        this.finish();
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
