package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import HTTPConnect.Connection;


/* This is the analysis activity used to display an easy to read
 * pie chart to the user about their recent transactions. It uses the
 * achartengine framework to allow us to draw the pie chart and fill it with data.
 * Its pretty easy to use if you ignore all the viewer stuff...
 */

public class Analysis extends Activity implements GetNotification.OnNotiFetchedListener {

    /* -- Variales -- */
    private String username;
    private String date;
    private Set<String> groupSets;
    private List<String> accountStrings = new ArrayList<String>();
    private String selectedAccount;
    private Menu activityMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        /* Get the intent */
        Intent intent = getIntent();
        username = intent.getStringExtra(IntentConstants.USERNAME);
        date = intent.getStringExtra(IntentConstants.DATE);

        Log.d("USERNAME","|" + username + "|");

        /* populte the sets */
        getAccounts();
        getPrefs();

        /* Set values for the spinner */
        final Spinner accounts = (Spinner) findViewById(R.id.analysis_spinner);
        ArrayAdapter<String> a = new ArrayAdapter<String>(this, R.layout.spinner_text_colour, accountStrings);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accounts.setAdapter(a);

        /* Onclick listener for spinner */
        accounts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                /* Draws new pie chart */
                selectedAccount = accounts.getItemAtPosition(position).toString();
                getPrefs();
                drawChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //do nothing but needs overriding anyway
            }
        });

        /* set selected account to the first account number */
        if(selectedAccount == null)
        {
            if(accountStrings.size() != 0)
            {
                selectedAccount = accountStrings.get(0);
            }
            else
            {
                //error
                return;
            }

        }

        /* For debugging to see which set is used */
        if(groupSets == null)
        {
            Log.d("Returend Set", "NULL POINTER");

        }
        else if(groupSets.size() == 0)
        {
            Log.d("Returend Set", "EMPTY SET");
        }
        else
        {
            Log.d("Returend Set", "NOT NULL OR EMPTY");
        }

        /* Draw the pie chart */
        drawChart();
    }

    /* gets the set of account numbers */
    public void getPrefs()
    {
        SharedPreferences settings = getSharedPreferences(username, 0);
        groupSets = settings.getStringSet("ANALYSIS_GROUPS_" + selectedAccount, new HashSet<String>());
    }

    /* Draws pie chart */
    public void drawChart()
    {
        try {
            FrameLayout mainLayout = (FrameLayout) findViewById(R.id.chart_layout);
            GraphicalView chartView;
            //an array of doubles that is used to populate the pie chart
            //sectors.

            /* reset the view */
            mainLayout.removeAllViews();

            /* Error message */
            TextView errorMessage = (TextView) findViewById(R.id.Analysis_Error_Box);
            errorMessage.setText("There is no data available for this account, please go to your statement to add a transaction to a group");

            /* if there are no groups, show error, else show pie chart */
            if (groupSets.size() == 0 || selectedAccount == null) {
                mainLayout.setVisibility(View.INVISIBLE);
                errorMessage.setVisibility(View.VISIBLE);
                return;
            } else {
                errorMessage.setVisibility(View.INVISIBLE);
                mainLayout.setVisibility(View.VISIBLE);
            }

            double[] values = new double[groupSets.size()];

            //Adds all of the names for each of the sectors in a string array
            String[] sectors = new String[groupSets.size()];

            groupSets.remove("Choose Group");
            List<String> temp = new ArrayList<String>(groupSets);

            for (int i = 0; i < temp.size(); i++) {
                Log.d("Values",temp.get(i));
                sectors[i] = temp.get(i).split(":")[0];
                values[i] = Double.parseDouble(temp.get(i).split(":")[1]);
            }

            //colours for each of the sectors
            int[] colors = {Color.parseColor("#ff6cbb6c"), Color.parseColor("#ff347834"), Color.parseColor("#ff345834"), Color.parseColor("#ff114e11"), Color.parseColor("#ff81bb81"),
                    Color.parseColor("#ff4fb74f")};

            //setup a default renderer...
            DefaultRenderer render = new DefaultRenderer();

            //Set up some properties for the renderer
            render.setLabelsTextSize(30);
            render.setPanEnabled(false);
            render.setLabelsColor(getResources().getColor(android.R.color.black));
            render.setScale(0.85f);
            render.setZoomEnabled(false);
            render.setShowLabels(false);
            render.setLegendTextSize(40);

            //Catergory series... needed for the chart factory
            CategorySeries categories = new CategorySeries("Transactions");
            for (int i = 0; i < values.length; i++) {
                //adds the string with the values to the chart
                categories.add(sectors[i], values[i]);
            }


            //setup a legend
            for (int i = 0; i < values.length; i++) {
                //Sets up a renderer
                SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
                //sets the colours for the series
                seriesRenderer.setColor(colors[i]);
                //sets the display to true
                seriesRenderer.setDisplayChartValues(true);
                //Adds a series renderer
                render.addSeriesRenderer(seriesRenderer);
            }

            //creates a new graph view (part of the library) and creates a new pie chart view
            chartView = ChartFactory.getPieChartView(this, categories, render);

            //chartView.getLayoutParams().height = 70;
            //add this to the layout
            mainLayout.addView(chartView, 0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /* Resets the groups */
    public void btnClickReset(View view)
    {
        SharedPreferences sp = getApplicationContext().getSharedPreferences(username, 0);
        SharedPreferences.Editor edit = sp.edit();
        edit.putStringSet("ANALYSIS_GROUPS_" + selectedAccount, new HashSet<String>());
        edit.putStringSet("TRANS_ID_" + selectedAccount, new HashSet<String>());
        edit.commit();

        new CustomMessageBox(this, "Removed all groups");

        /* redraw the chart (Show error message) */
        getPrefs();
        drawChart();
    }


    void getAccounts()
    {
        /* What this method would really do is get the data from the webserver
         * for a certain users accounts. This will then take that data and display it
         * with all of the different ammounts in each account. However, this is not currently
         * set up on the web server
         */


        Connection hc = new Connection(this);// trying to pass the activity to the coonection (not sure if this is legal though)

        try {
            String result = hc.execute("TYPE","SAA",IntentConstants.USERNAME, username ).get();

            JSONObject jo = new JSONObject(result);


            if(jo.getString("expired").equals("true"))
            {

                //This uses the same code as the main menu does to start the login, only this time it is run when the user
                //has timed out
                AlertDialog.Builder msg = new AlertDialog.Builder(this);
                msg.setMessage("Your session has been timed out, please login again")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                autoLogout();
                                //dialogClosed = true;

                            }
                        });
                AlertDialog alert = msg.create();
                alert.show();

            }
            else {

                JSONArray jsonArray = jo.getJSONArray("accounts");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject insideObject = jsonArray.getJSONObject(i);
                    accountStrings.add(insideObject.getString("account_number"));
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void autoLogout()
    {
        Connection hc = new Connection(this);
        try
        {
            hc.execute("TYPE","LOGOUT", IntentConstants.USERNAME, username);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


        ((KillApp) this.getApplication()).setStatus(false);
        finish();
        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        //login again
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.getItem(1);
        GetNotification notif = new GetNotification();
        activityMenu = menu;
        notif.getNotifications(this, username, date);

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
            intent.putExtra(IntentConstants.USERNAME, username);
            intent.putExtra(IntentConstants.DATE, date);
            startActivity(intent);
            ((KillApp) this.getApplication()).setStatus(false);

        }
       /*else if (id == R.id.action_location) {
           return true;
       }*/
        return super.onOptionsItemSelected(item);
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

         /* Change color of button */
        if(MainActivity.getColour(this) == Color.WHITE)
        {
            (findViewById(R.id.Reset_Button_Analysis)).setBackground(new ColorDrawable(MainActivity.getColor()));
        }
        else
        {
            findViewById(R.id.Reset_Button_Analysis).setBackground(new ColorDrawable(MainActivity.getColour(this)));
        }

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

    @Override
    public void onAllTransactionDone(boolean result) {
        activityMenu.getItem(1).setIcon(result ? R.drawable.ic_action_notify : R.drawable.globe);
    }
}
