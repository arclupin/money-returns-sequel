package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Houseshare_Create_House extends Activity {
    private final String fields[] = {"Name of house", "House Number", "Street", "City", "Description"};
    private String username;
    private Map<String, String> contents = new HashMap<String, String>();
    private boolean isBottom = false;
    private android.app.ActionBar actionBar;
    private TextWatcher watcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_houseshare_create_house);

        TextView a = (TextView)  findViewById(R.id.houseshare_create_button);

        if (isInformationSufficient())
            a.setTextColor(Color.DKGRAY);
        else  a.setTextColor(Color.LTGRAY);
        ScrollView scrollContainer = (ScrollView) findViewById(R.id.hs_inputs_scroll);
        scrollContainer.setSmoothScrollingEnabled(true);

        Intent intent = getIntent();
        username = intent.getExtras().getString("ACCOUNT_USERNAME");

        watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                TextView a = (TextView)  findViewById(R.id.houseshare_create_button);
                if (isInformationSufficient())
                    a.setTextColor(Color.DKGRAY);
               else  a.setTextColor(Color.LTGRAY);

        }};

        setTextWatchers();

//        actionBar = getActionBar();
//        Log.d("Action Bar: ", (String) actionBar.getTitle());

        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_houseshare__create__house, menu);
        return true;
    }

    public boolean isInformationSufficient() {
        LinearLayout container = (LinearLayout) findViewById(R.id.houseshare_create_input_container);
        boolean r = true;
        for (int i = 0; i < container.getChildCount() - 2; i+=2 )
        {
            EditText child = (EditText) container.getChildAt(i);
            if (child.getText().toString().trim().isEmpty() && i != (container.getChildCount() - 3)) {
                r = false;
                return r;
            }
        }
        return r;
    }

    public void setTextWatchers() {
        LinearLayout container = (LinearLayout) findViewById(R.id.houseshare_create_input_container);
        for (int i = 0; i < container.getChildCount() - 2; i+=2 )
        {
            EditText child = (EditText) container.getChildAt(i);
            child.addTextChangedListener(watcher);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void Houseshare_create_house(View v) {
        TextView a = (TextView)  findViewById(R.id.houseshare_create_button);
        if (!isInformationSufficient() && a.getCurrentTextColor() == Color.LTGRAY)
            Toast.makeText(this, "Please supply enough information.", Toast.LENGTH_SHORT).show();
        else  Toast.makeText(this, "Creating", Toast.LENGTH_SHORT).show();
    }
}
