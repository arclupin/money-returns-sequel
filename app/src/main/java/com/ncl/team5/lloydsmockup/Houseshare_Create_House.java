package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import HTTPConnect.Connection;
import HTTPConnect.Request_Params;
import HTTPConnect.Responses_Format;
import Utils.Houseshares;
import Utils.StringUtils;
 //TODO : ADD REAL-TIME NAME UNIQUENESS CHECKER

public class Houseshare_Create_House extends Activity {
    private final int FIELDS = 6;
    private String username;
    private boolean isBottom = false;
    private android.app.ActionBar actionBar;
    private TextWatcher watcher;
    private String housename;
    private String hsid;

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
        username = intent.getExtras().getString(IntentConstants.USERNAME);
        hsid = intent.getStringExtra(IntentConstants.HOUSESHARE_ID);

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

        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_houseshare__create__house, menu);
        return true;
    }

    /**
     * check if the information is sufficient for creating a house
     * @return true if that's the case, false oftherwise
     */
    public boolean isInformationSufficient() {
        LinearLayout container = (LinearLayout) findViewById(R.id.houseshare_create_input_container);
        for (int i = 0; i < container.getChildCount() - 2; i+=2 )
        {
            EditText child = (EditText) container.getChildAt(i);
            if (StringUtils.isFieldEmpty(child.getText().toString()) && i != (container.getChildCount() - 3)) {
                return false;
            }
        }
        return true;
    }

    /**
     * set text watchers for childs
     */
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

    /**
     * prepare the request for house creating
     * @return the list of string containing all params for the request
     */
    private List<String> fetchDetailsForRequest() {
        List<String> details = new ArrayList<String>(FIELDS);
        LinearLayout container = (LinearLayout) findViewById(R.id.houseshare_create_input_container);
        for (int i = 0; i < container.getChildCount() - 2 ; i+=2 )
        {
            EditText a = (EditText) container.getChildAt(i);
            details.add(a.getText().toString());
        }

        return details;
    }

    /**
     * On-click function for create button
     * @param v the create button
     */
    public void Houseshare_create_house(View v) {
        TextView a = (TextView)  findViewById(R.id.houseshare_create_button);
        if (!isInformationSufficient() && a.getCurrentTextColor() == Color.LTGRAY)
            Toast.makeText(this, "Please supply enough information.", Toast.LENGTH_SHORT).show();
        else
        {
            Toast.makeText(this, "Your house is being processed. Stay put...", Toast.LENGTH_LONG ).show();
            List<String> l = fetchDetailsForRequest();
            Connection c = new Connection(this);
            String result;

            try {
                result = c.execute(Request_Params.PARAM_TYPE, Request_Params.VAL_HS_CREATE_HOUSE,
                        Request_Params.PARAM_USR, username,
                        Request_Params.HS_CREATE_HOUSE_NAME, l.get(0),
                        Request_Params.HS_CREATE_HOUSE_HSNO, l.get(1),
                        Request_Params.HS_CREATE_HOUSE_STREET, l.get(2),
                        Request_Params.HS_CREATE_HOUSE_CITY, l.get(3),
                        Request_Params.HS_CREATE_HOUSE_POSTCODE, l.get(4),
                        Request_Params.HS_CREATE_HOUSE_DESCRIPTION, l.get(5)).get();
            /* Turns String into JSON object, can throw JSON Exception */
                JSONObject jo = new JSONObject(result);

            /* Check if the user has timed out */
                if (jo.getString(Responses_Format.RESPONSE_EXPIRED).equals("true")) {

                /* Display message box and auto logout user */
                    AlertDialog.Builder errorBox = new AlertDialog.Builder(this);
                    final Connection temp_connect = c;
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
                else if (jo.getString(Responses_Format.RESPONSE_STATUS).equals("true")) {
                   new CustomMessageBox.MessageBoxBuilder(this, "Your house has been created. Let's start sharing.")
                           .setTitle("Confirmation").build();
                    Houseshares.hs_intents_home_view(this, Houseshare_HomeView.class, l.get(0), username,
                            hsid, Responses_Format.RESPONSE_HOUSESHARE_JOINED_HOUSE);
                    //TODO SET UP THE HOME VIEW FOR THE HOUSE (Should be done in background)

                }
                else if (jo.getString(Responses_Format.RESPONSE_STATUS).equals(Responses_Format.RESPONSE_FAILED_NAME_NOT_UNQ)) {
                    Toast.makeText(this, "The house name is not unique. Please choose another name.", Toast.LENGTH_SHORT ).show();
                }
            /* There was an error indide the status return field, display appropriate error message */
                else {
                    //TODO implement error messages
                    Toast.makeText(this, "A unexpected error occcured. Try again", Toast.LENGTH_SHORT ).show();
                }

            }
        /* Catch the exceptions */
            catch (JSONException jse) {
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
}
