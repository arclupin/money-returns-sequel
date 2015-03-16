package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import HTTPConnect.Connection;
import HTTPConnect.Request_Params;


public class MainActivity extends Activity {


    private String username;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //gets the username that is passed from the login so the connection can stay open
        Intent i = getIntent();
        username = i.getStringExtra("ACCOUNT_USERNAME");
        date = i.getStringExtra("DATE");

        TextView dateText = (TextView)findViewById(R.id.lastLoginTextView);

        //need to change this to the actual login time response stuff
        if(date.equals("Not Available"))
        {
            //do some fancy first logon stuff :)
            dateText.setText("");
        }
        else
        {
            dateText.setText(username + " : Last Login on " + date);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
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


    public void btnClickPayments(View view) {
        Intent i = new Intent(this, Payments.class);
        i.putExtra("ACCOUNT_USERNAME", username);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);

    }

    public void btnClickTransfers(View view) {
        Intent i = new Intent(this, Transfers.class);
        i.putExtra("ACCOUNT_USERNAME", username);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);
    }

    public void btnClickAccounts(View view) {
        Intent i = new Intent(this, Accounts.class);
        //Log.d("USERNAME", username);
        i.putExtra("ACCOUNT_USERNAME", username);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);
    }

    public void btnClickAnalysis(View view) {
        Intent i = new Intent(this, Analysis.class);
        i.putExtra("ACCOUNT_USERNAME", username);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);
    }

    //TODO unfinished function
    public void btnClickHouseShare(View view) {
        Connection connection = new Connection(this);
        if (this.username.equals("test")) {
            Intent i = new Intent(this, Houseshare_Welcome.class);
            i.putExtra("ACCOUNT_USERNAME", username);
            startActivity(i);
            ((KillApp) this.getApplication()).setStatus(false);
        } else {
            Connection connect = new Connection(this);
            String result;

            try {
            /* Command required to make a payment, takes username, to account, from account, both sort codes and amount
             * Returns: JSON String */
                result = connect.execute(Request_Params.PARAM_TYPE, Request_Params.VAL_HS_INIT, Request_Params.PARAM_USR, this.username).get();
            /* Turns String into JSON object, can throw JSON Exception */
                JSONObject jo = new JSONObject(result);

            /* Check if the user has timed out */
                if (jo.getString("expired").equals("true")) {

                /* Display message box and auto logout user */
                    AlertDialog.Builder errorBox = new AlertDialog.Builder(this);
                    final Connection temp_connect = connect;
                    final String temp_usr = username;
                    errorBox.setMessage("Your session has been timed out, please login again")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    temp_connect.autoLogout(temp_usr);
                                }
                            });
                    AlertDialog alert = errorBox.create();
                    alert.show();
                }
                // TODO unfinished, the server will send a more detailed message i.e. 'registered and joined house' or 'registered and not joined house'
                else if (jo.getString("status").equals("true")) { // true means registered
                    Intent i = new Intent(this, Houseshare_Search.class);
                    i.putExtra("ACCOUNT_USERNAME", username);
                    startActivity(i);
                    ((KillApp) this.getApplication()).setStatus(false);
                }
                // TODO unfinished, the server will send a more detailed message i.e.
                else if (jo.getString("status").equals("false")) { // false means not registered
                    Intent i = new Intent(this, Houseshare_Welcome.class);
                    i.putExtra("ACCOUNT_USERNAME", username);
                    startActivity(i);
                    ((KillApp) this.getApplication()).setStatus(false);
                }
            /* There was an error indide the status return field, display appropriate error message */
                //TODO implement error messages
                else {
                /* give more info on the error here, no money taken from account */
                /* Use the status results to display certain error messages */
                }

            }
        /* Catch the exceptions */ catch (JSONException jse) {
            /* Error in the JSON response */
                new CustomMessageBox(this, "There was an error in the server response");
                jse.printStackTrace();
            } catch (InterruptedException interex) {
            /* Caused when the connection is interrupted */
                new CustomMessageBox(this, "Connection has been interrupted");
                interex.printStackTrace();
            } catch (ExecutionException ee) {
            /* No idea when this is caused but it throws it... */
                new CustomMessageBox(this, "Execution Error");
                ee.printStackTrace();
            } catch (Exception e) {
            /* Failsafe if something goes utterly wrong */
                new CustomMessageBox(this, "An unknown error occurred");
                e.printStackTrace();
            }


        }
    }

    public void btnClickOffers(View view) {
        Intent i = new Intent(this, Locations.class);
        i.putExtra("ACCOUNT_USERNAME", username);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);
    }

    public void btnClickProducts(View view) {
        Intent i = new Intent(this, Products.class);
        i.putExtra("ACCOUNT_USERNAME", username);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);
    }

    public void btnClickSettings(View view) {
        Intent i = new Intent(this, Settings.class);
        i.putExtra("ACCOUNT_USERNAME", username);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);
    }

    public void btnLogout(View view) {
        Connection connect = new Connection(this);
        try
        {
            connect.execute("TYPE","LOGOUT", "USR", username);
        }
        finally
        {
            ((KillApp) this.getApplication()).setStatus(false);
            this.finish();

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
        if(((KillApp) this.getApplication()).getStatus())
        {
            //APP IS KILLED :O

            Connection connect = new Connection(this);
            try
            {
                connect.execute("TYPE","LOGOUT", "USR", username);
            }
            finally
            {
                ((KillApp) this.getApplication()).setStatus(false);
                finish();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }


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
        Connection connect = new Connection(this);
        try
        {
            connect.execute("TYPE","LOGOUT", "USR", username);
        }
        finally
        {
            ((KillApp) this.getApplication()).setStatus(false);
            this.finish();
        }
    }
}
