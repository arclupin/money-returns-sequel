package com.ncl.team5.lloydsmockup;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcelable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ncl.team5.lloydsmockup.Houseshares.Member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import Utils.StringUtils;
import Utils.Utilities;
import Utils.Validator;


public class NewBillManual_SubBill extends Activity {

    private TableLayout subbill_table;
    private CheckBox option_shared_equally;
    private TextView net_bill_tv;
    private TextView expected_bill_tv;
    private ActionBar actionBar;

    private String billName;
    private String dueDate;
    private static String username;
    private static String housename;
    private Set<Member> involved_members = new TreeSet<Member>();
    private Intent i;

    private double netBill;
    private double expectedBill;
    private Map<String, Double> subbills;
    private double share;
    private boolean isSubBillsValid = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bill_manual__sub_bill);
        actionBar = getActionBar();
        if (actionBar != null)
            actionBar.setSplitBackgroundDrawable(new ColorDrawable((getResources().getColor(R.color.dark_green))));

        i = getIntent();
        if (i != null) {
            username = i.getStringExtra(IntentConstants.USERNAME);
            housename = i.getStringExtra(IntentConstants.HOUSE_NAME);
            billName = i.getStringExtra(IntentConstants.BILL_NAME);
            expectedBill = Double.parseDouble(i.getStringExtra(IntentConstants.BILL_AMOUNT));
            dueDate = i.getStringExtra(IntentConstants.BILL_DUE_DATE);
            ArrayList<Parcelable> m = i.getParcelableArrayListExtra(IntentConstants.MEMBERS);
            for (Parcelable parcel : m) {
                involved_members.add((Member) parcel);
                Log.d("Parcel from intent", parcel.toString());
            }
            share = expectedBill / involved_members.size();
            Log.d("share", "/" + share);
        }

        subbill_table = (TableLayout) findViewById(R.id.table_sub_bills);
        option_shared_equally = (CheckBox) findViewById(R.id.checkBox_share_equally);
        option_shared_equally.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    // change net bill tv
                    netBill = expectedBill;
                    net_bill_tv.setText(String.valueOf(netBill));
                    net_bill_tv.setTextColor(getResources().getColor(R.color.light_green));

                    for (int i = 0; i < subbill_table.getChildCount() - 1; i++) {
                        EditText tv = (EditText) subbill_table.getChildAt(i).findViewById(R.id.sub_bill_amount);
                        tv.setEnabled(false);
                        tv.setText(String.format("%.2f", share));
                        tv.setTextColor(Color.LTGRAY); // blur the edit text
                        //TODO background
                    }

                    //reset the map storing sub bills
                    for (Map.Entry<String, Double> entry : subbills.entrySet()) {
                        entry.setValue(share);
                    }
                }
                else {
                    for (int i = 0; i < subbill_table.getChildCount() - 1; i++) {
                        EditText v = (EditText)subbill_table.getChildAt(i).findViewById(R.id.sub_bill_amount);
                        v.setTextColor(Color.parseColor("#323842")); // return the normal color for text
                        v.setEnabled(true);
                    }
                }
            }
        });

        net_bill_tv = (TextView) findViewById(R.id.net_bill_amount_tv);
        net_bill_tv.setEnabled(false);

        expected_bill_tv = (TextView) findViewById(R.id.expected_bill_amount_tv);
        expected_bill_tv.setText(String.valueOf(expectedBill));
        expected_bill_tv.setEnabled(false);

        subbills = new HashMap<String, Double>();

        // initialise the sub bill map
        for (Member m : involved_members)
            subbills.put(m.getUsername(), 0.0);


        showMembers();





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_bill_manual__sub_bill, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_back: {
                onBackPressed();
                return true;
            }
            case R.id.action_create_bill: {

                if (isDataValid())
                    Toast.makeText(this, "Dialog to show up", Toast.LENGTH_SHORT).show();
                else new CustomMessageBox.MessageBoxBuilder(this, "Please review your data and supply correct information.")
                        .setTitle("Warning").build();
                return  true;
            }
        }


        return super.onOptionsItemSelected(item);
    }

    private void showMembers() {
        clearViews();
        int i = 0;
        for (Iterator<Member> iterator = involved_members.iterator(); iterator.hasNext(); i++) {
            final Member m = iterator.next();
            View v = m.craftViewSubBill(getLayoutInflater());
            //TODO this sort of validation might be applied for many edit texts => reusable?
            EditText tv = (EditText) v.findViewById(R.id.sub_bill_amount);
            Utilities.registerValidator(tv, new Validator() {
                @Override
                public boolean isDataValid(String s) {
                    return StringUtils.isStringValidAmount(s);
                }
            });
            tv.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override

                public void afterTextChanged(Editable s) {
                    // only activate the text listener if the add all check box is not checked
                    if (!option_shared_equally.isChecked()) {
                        String input = s.toString();
                        if (StringUtils.isStringValidAmount(input)) {
                            double i = Double.parseDouble(input);
                            netBill -= subbills.get(m.getUsername()); // subtract the old value
                            netBill += i; // add the new value
                            subbills.put(m.getUsername(), i); // update the map
                            Log.d("netBill after txtchng", String.valueOf(netBill));
                            net_bill_tv.setText(String.valueOf(netBill)); // update the view
                            checkBillsValidity(); // check is the total added bill has equated to the expected bill
                        } else {
                            netBill -= subbills.get(m.getUsername()); // subtract the old value
                            net_bill_tv.setText(String.valueOf(netBill)); // update the view
                            subbills.put(m.getUsername(), 0.0); // reset the sub bill for this user if the lastest value is invalid
                        }
                    }
                }
            });

            subbill_table.addView(v, i);

        }

    }

    private void clearViews() {
        for (int i = 0; i < subbill_table.getChildCount() - 1; i++)
            subbill_table.removeViewAt(i);
    }

    private void checkBillsValidity() {
        if (netBill == expectedBill)
        {
            isSubBillsValid = true;
            net_bill_tv.setTextColor(getResources().getColor(R.color.light_green));
        }
        else {
            isSubBillsValid = false;
            net_bill_tv.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }
    }

    private boolean isDataValid() {

        if (Double.parseDouble(net_bill_tv.getText().toString()) == expectedBill)
             return true;
        return false;
    }


}
