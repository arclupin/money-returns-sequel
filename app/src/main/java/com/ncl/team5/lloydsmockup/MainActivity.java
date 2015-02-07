package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends Activity {

    private boolean back = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
         else if (id == R.id.action_location) {
             return true;
         }
        return super.onOptionsItemSelected(item);
    }


    public void btnClickPayments(View view) {
        Intent i = new Intent(this, Payments.class);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);

    }

    public void btnClickTransfers(View view) {
        Intent i = new Intent(this, Transfers.class);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);
    }

    public void btnClickAccounts(View view) {
        Intent i = new Intent(this, Accounts.class);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);
    }

    public void btnClickAnalysis(View view) {
        Intent i = new Intent(this, Analysis.class);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);
    }

    public void btnClickAchievements(View view) {
        Intent i = new Intent(this , Achievements.class);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);
    }

    public void btnClickOffers(View view) {
        Intent i = new Intent(this, Offers.class);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);
    }

    public void btnClickProducts(View view) {
        Intent i = new Intent(this, Products.class);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);
    }

    public void btnClickSettings(View view) {
        Intent i = new Intent(this, Settings.class);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);
    }

    public void btnLogout(View view) {
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
            finish();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

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
