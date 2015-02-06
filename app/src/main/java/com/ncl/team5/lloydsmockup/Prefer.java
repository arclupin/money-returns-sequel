package com.ncl.team5.lloydsmockup;


import android.content.Intent;
import android.os.Bundle;

import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuItem;
import java.util.List;



public class Prefer extends PreferenceActivity {

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }
    @Override
    public boolean isValidFragment(String fragmentName) {
        if (Display.class.getName().equals(fragmentName)) {
            return(true);
        }
        return(false);
    }
    public static class Display extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_display);
        }
    }

   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefer);
    } */


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
            this.finish();
            //Intent intent = new Intent(this, MainActivity.class);
            //startActivity(intent);
        } else if (id == R.id.action_notifications) {
            Intent intent = new Intent(this, Notifications.class);
            startActivity(intent);

        } else if (id == R.id.action_location) {
            return true;
        }

            return super.onOptionsItemSelected(item);

    }
}
