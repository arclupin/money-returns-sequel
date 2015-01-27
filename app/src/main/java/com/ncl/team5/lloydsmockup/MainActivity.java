package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends Activity {

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
            this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
            //Intent intent = new Intent(this, MainActivity.class);
            //startActivity(intent);
        }
        else if (id == R.id.action_notifications) {
            Intent intent = new Intent(this, Notifications.class);
            startActivity(intent);

        }
         else if (id == R.id.action_location) {
             return true;
         }
        return super.onOptionsItemSelected(item);
    }


    public void btnClickPayments(View view) {
        Intent i = new Intent(this, Payments.class);
        startActivity(i);

    }

    public void btnClickTransfers(View view) {
        Intent i = new Intent(this, Transfers.class);
        startActivity(i);
    }

    public void btnClickAccounts(View view) {
        Intent i = new Intent(this, Accounts.class);
        startActivity(i);
    }

    public void btnClickAnalysis(View view) {
        Intent i = new Intent(this, Analysis.class);
        startActivity(i);
    }

    public void btnClickAchievements(View view) {
        Intent i = new Intent(this , Achievements.class);
        startActivity(i);
    }

    public void btnClickOffers(View view) {
        Intent i = new Intent(this, Offers.class);
        startActivity(i);
    }

    public void btnClickProducts(View view) {
        Intent i = new Intent(this, Products.class);
        startActivity(i);
    }

    public void btnClickSettings(View view) {
        Intent i = new Intent(this, Settings.class);
        startActivity(i);
    }

    public void btnLogout(View view) {
        this.finish();
    }
}
