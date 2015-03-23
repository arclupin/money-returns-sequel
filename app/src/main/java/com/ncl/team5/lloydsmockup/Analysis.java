package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;


/* This is the analysis activity used to display an easy to read
 * pie chart to the user about their recent transactions. It uses the
 * achartengine framework to allow us to draw the pie chart and fill it with data.
 * Its pretty easy to use if you ignore all the viewer stuff...
 */

public class Analysis extends Activity {

    private String username;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        Intent intent = getIntent();
        username = intent.getStringExtra("ACCOUNT_USERNAME");
        date = intent.getStringExtra("DATE");

        //Just creates a layout so that the chart can be displayed on it.
        //Uses the analysis activity xml file to display the graph on
        FrameLayout mainLayout = (FrameLayout) findViewById(R.id.chart_layout);



        //an array of doubles that is used to populate the pie chart
        //sectors.
        double[] values = { 1, 1, 1, 1, 1, 5 } ;

        //Adds all of the names for each of the sectors in a string array
        String[] sectors = new String[] {
                "Shopping", "Bills", "Rent", "Food",
                "University", "Other"
        };

        //colours for each of the sectors
        int[] colors = { Color.BLUE, Color.MAGENTA, Color.GREEN, Color.CYAN, Color.RED,
                Color.YELLOW };

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
        for(int i=0 ; i < values.length; i++){
            //adds the string with the values to the chart
            categories.add(sectors[i], values[i]);
        }



        //setup a legend
        for(int i = 0 ; i < values.length; i++){
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
        GraphicalView chartView = ChartFactory.getPieChartView(this, categories, render);

        //chartView.getLayoutParams().height = 70;
        //add this to the layout
        mainLayout.addView(chartView, 0);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.getItem(1);
        GetNotification notif = new GetNotification();

        if(notif.getNotifications(this, username)) {
            Log.d("Notif Change", "IN HERE");
            item.setIcon(R.drawable.ic_action_notify);
        }
        else
        {
            Log.d("Notif Change", "IN There");
            item.setIcon(R.drawable.ic_action_email);
        }

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
            intent.putExtra("ACCOUNT_USERNAME", username);
            intent.putExtra("DATE", date);
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
