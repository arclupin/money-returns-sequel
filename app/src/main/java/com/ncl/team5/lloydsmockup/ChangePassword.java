package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import HTTPConnect.Connection;


public class ChangePassword extends Activity {

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
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
       /* else if (id == R.id.action_location) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    public void btnChangePassword(View view) {

        String oldPass = ((TextView)findViewById(R.id.previousPass)).getText().toString();
        String newPass = ((TextView)findViewById(R.id.editTextNew)).getText().toString();
        String newPass2 = ((TextView)findViewById(R.id.editTextAgain)).getText().toString();

        if(!newPass.equals(newPass2))
        {
            new CustomMessageBox(this, "Passwords do not match");
            return;
        }
        else if (!newPass.matches("[a-zA-Z0-9]{4,20}"))
        {
            new CustomMessageBox(this, "Password but be between 4 and 20 characters and cannot contain special characters");
            return;
        }

        Connection c = new Connection(this);

        try
        {
            String result = c.execute("TYPE", "CPASS", "PASSWORD", oldPass, "PASSWORD_NEW", newPass, IntentConstants.USERNAME, username).get();

            JSONObject jo = new JSONObject(result);

             /* Check if the user has timed out */
            if (jo.getString("expired").equals("true")) {

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
            /* Payment was successful, show message box */
            else if (jo.getString("status").equals(StatusConstants.OK)) {


                Toast.makeText(getBaseContext(), "Password Changed",
                        Toast.LENGTH_SHORT).show();

                ((KillApp) this.getApplication()).setStatus(false);
                finish();
            }
            else
            {
                new CustomMessageBox(this, "There has been an error, your password was not changed");
            }

        }
        catch(Exception e){
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
