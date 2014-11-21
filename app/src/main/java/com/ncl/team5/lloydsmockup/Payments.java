package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;


public class Payments extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);
        Spinner s = (Spinner) findViewById(R.id.spinner3);
        ArrayAdapter<CharSequence> a = ArrayAdapter.createFromResource(this, R.array.accountslist, android.R.layout.simple_spinner_item);
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
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
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


    public void btnMakePay(View view) {
        Toast.makeText(getBaseContext(), "Payment Made",
                Toast.LENGTH_SHORT).show();
    }
}
