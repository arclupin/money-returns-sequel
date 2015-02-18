package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import HTTPConnect.Connection;


public class Payments extends Activity {

    private String username;
    private List<String> accountStrings = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        username = i.getStringExtra("ACCOUNT_USERNAME");

        getAccounts();

        setContentView(R.layout.activity_payments);
        Spinner s = (Spinner) findViewById(R.id.spinner3);
        ArrayAdapter<String> a = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, accountStrings);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(a);
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


    public void btnMakePay(View view) {

        Connection connect = new Connection(this);
        String result;

        try
        {
            result = connect.execute("TYPE", "PAY", "USR", username, "PAYTO", "56984521", "PAYFROM", "48596365", "AMOUNT", "1000.00").get();


            JSONObject jo = new JSONObject(result);

            if(jo.getString("expired").equals("true"))
            {
                //login agian
            }
            else if (jo.getString("status").equals("true"))
            {
                //show payment made screen
            }
            else
            {
                //give more info on the error here, no money taken from account
            }

        }
        catch (Exception e)
        {

        }

        Toast.makeText(getBaseContext(), "Payment Made",
                Toast.LENGTH_SHORT).show();
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


    public void getAccounts()
    {
        Connection hc = new Connection(this);// trying to pass the activity to the coonection (not sure if this is legal though)

        try {
            String result = hc.execute("TYPE","SAA","USR", username ).get();


            JSONObject jo = new JSONObject(result);

            JSONArray jsonArray = jo.getJSONArray("accounts");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject insideObject = jsonArray.getJSONObject(i);
                accountStrings.add(insideObject.getString("account_number"));
            }



        }
        catch (Exception e)
        {
            //give the user a message about being unable to connect. maybe log them out
            e.printStackTrace();
        }
    }
}
