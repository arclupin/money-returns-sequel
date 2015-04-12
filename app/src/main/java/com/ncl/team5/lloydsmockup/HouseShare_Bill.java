package com.ncl.team5.lloydsmockup;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.ncl.team5.lloydsmockup.Houseshares.Bill;

import Utils.StringUtils;


public class HouseShare_Bill extends Activity {

    private ActionBar actionBar;
    private TextView billName_TextView;
    private TextView billCreationDetails_TextView;
    private TextView billStatus_TextView;

    private String billCreator_Name_Display; // the name to be dislayed (not necessarily be the full name
    // as the name might be trimmed if there is not enough space
    private Bill bill;
    private Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_share__bill);

        actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setLogo(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_green)));
        }

        i = getIntent();
        bill = (Bill) i.getParcelableArrayListExtra(IntentConstants.BILL_PARCEL).get(0);

        billName_TextView = (TextView) findViewById(R.id.bill_basic_info);
        billCreationDetails_TextView = (TextView) findViewById(R.id.bill_creation_details);
        billStatus_TextView = (TextView) findViewById(R.id.bill_status);

        //set up text views data
        billName_TextView.setText(bill.getBillName());
        billCreationDetails_TextView.setText("Created by " +
                (bill.amICreator() ? "you" :
                        StringUtils.getShortenedString(bill.getBillCreator().getUsername(), 15)
                        + " on " + StringUtils.getGeneralDateString(bill.getDateCreated())));
        if (!bill.isPaid()) {
            billStatus_TextView.setText("This bill is due " +
                    StringUtils.getGeneralDateString(bill.getDueDate()) + ".");
        }
        else {
            billStatus_TextView.setText("This bill has been paid on " +
                    StringUtils.getGeneralDateString(bill.getDatePaid()) + ".");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_house_share__bill, menu);
        return true;
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
}
