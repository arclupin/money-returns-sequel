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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import Fragments.HS_Bill_Confirmation_Dialog;
import HTTPConnect.ConcurrentConnection;
import HTTPConnect.Connection;
import HTTPConnect.Request;
import HTTPConnect.RequestQueue;
import HTTPConnect.Request_Params;
import HTTPConnect.Response;
import HTTPConnect.Responses_Format;
import Utils.StringUtils;
import Utils.Utilities;
import Utils.Validator;

/**
 * Activity allowing user to type in the shares for each participant.
 */
public class NewBillManual_SubBill extends Activity implements
        HS_Bill_Confirmation_Dialog.BillConfirmationDialogListener{

    private TableLayout subbill_table;
    private CheckBox option_shared_equally;
    private TextView net_bill_tv;
    private TextView expected_bill_tv;
    private ActionBar actionBar;

    private String billName;
    private String dueDate;
    private String message;
    private static String username;
    private static String housename;
    private static String hsid;
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
        //get data from the intent
        if (i != null) {
            username = i.getStringExtra(IntentConstants.USERNAME);
            housename = i.getStringExtra(IntentConstants.HOUSE_NAME);
            hsid = i.getStringExtra(IntentConstants.HOUSESHARE_ID);
            billName = i.getStringExtra(IntentConstants.BILL_NAME);

            dueDate = i.getStringExtra(IntentConstants.BILL_DUE_DATE);
            expectedBill = Double.parseDouble(i.getStringExtra(IntentConstants.BILL_AMOUNT));
            message = i.getStringExtra(IntentConstants.BILL_MESSAGE);

            // get the members list passed from the NewBillManual
            ArrayList<Parcelable> m = i.getParcelableArrayListExtra(IntentConstants.MEMBERS);
            for (Parcelable parcel : m) {
                involved_members.add((Member) parcel);
                Log.d("Parcel from intent", parcel.toString());
            }
            share = StringUtils.roundAmount(expectedBill / involved_members.size()); // get the equal share value
            Log.d("share", "/" + share);
        }

        // get views on the layout
        subbill_table = (TableLayout) findViewById(R.id.table_sub_bills);

        // set listener for check box
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

        //get views for bill value indicators
        net_bill_tv = (TextView) findViewById(R.id.net_bill_amount_tv);
        net_bill_tv.setEnabled(false);

        expected_bill_tv = (TextView) findViewById(R.id.expected_bill_amount_tv);
        expected_bill_tv.setText(String.valueOf(expectedBill));
        expected_bill_tv.setEnabled(false);

        // initialise the hash map storing the sub bills
        subbills = new HashMap<String, Double>();

        // initialise the sub bill map with initial values (0.0)
        for (Member m : involved_members)
            subbills.put(m.getUsername(), 0.0);

        // show members
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
                if (isDataValid()) {
                    //show the confirmation dialog
                    HS_Bill_Confirmation_Dialog confirmation_dialog =
                            HS_Bill_Confirmation_Dialog.initialise(billName, dueDate,
                                    String.valueOf(expectedBill), message);
                    confirmation_dialog.show(getFragmentManager(), "bill_confirm_dialog");
                    net_bill_tv.setText(String.valueOf(netBill));
                    net_bill_tv.setTextColor(getResources().getColor(R.color.light_green));

                } else {
                    net_bill_tv.setText(String.valueOf(netBill));
                    net_bill_tv.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                    new CustomMessageBox.MessageBoxBuilder(this,
                            "Please review your data and supply correct information.")
                            .setTitle("Warning").build();

                }
                Log.d("NetBill", String.valueOf(netBill));
                return true;

            }
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * display members for sub bills
     */
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

            //add the sub bill row the the table row
            subbill_table.addView(v, i);
        }

    }

    /**
     * Clear all views in the sub bill table <u>EXCEPT</u> the last one which contains the required views
     * (option for sharing the bill equally, expected bill amount text view etc.)
     */
    private void clearViews() {
        for (int i = 0; i < subbill_table.getChildCount() - 1; i++)
            subbill_table.removeViewAt(i);
    }

    /**
     * UI listener for sub bills validity
     */
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


    /**
     * Check the state of the sub bills to see if data is okay to proceed
     */
    private boolean isDataValid() {
        double netTotal = 0;
        for (int i = 0; i < subbill_table.getChildCount() - 1; i++) {
            EditText t = (EditText) subbill_table.getChildAt(i).findViewById(R.id.sub_bill_amount);
            if (!StringUtils.isStringValidAmount(t.getText().toString()))
                return false;
            double amt = Double.parseDouble(t.getText().toString());
            Log.d("amount" + i, String.valueOf(amt));
            netTotal += amt;
            netBill = netTotal;
            subbills.put(((TextView) ((View) t.getParent()).findViewById(R.id.username_sub_bill))
                    .getText().toString(),amt);
            Log.d("net total after" + i, String.valueOf(netTotal));
        }
        if (netBill != expectedBill)
            return false;
        return true;
    }


    /*
     * Overriding bill confirmation dialog interacting methods
     */
    @Override
    public void onBillConfirmedButtonClick(String bill_name, HS_Bill_Confirmation_Dialog f) {
        //TODO server connection
        f.dismiss();
        Request creating_request = new Request(Request.TYPE.POST);
        creating_request.addParam(Request_Params.PARAM_TYPE, Request_Params.HS_CREATE_BILL)
                .addParam(Request_Params.PARAM_USR, username)
                .addParam(Request_Params.HS_CREATE_BILL_NAME, billName)
                .addParam(Request_Params.HS_CREATE_BILL_DUE_DATE, StringUtils.getStringDate(dueDate, "dd/MM/yyyy", "yyyy-MM-dd"))
                .addParam(Request_Params.HS_CREATE_BILL_AMOUNT, String.valueOf(expectedBill))
                .addParam(Request_Params.HS_CREATE_BILL_MESSAGE, message);

        // add sub bill values to the request
        for (Member m : involved_members) {
            creating_request.addParam(Request_Params.HS_CREATE_BILL_MEMBERS, m.getHouseshare_id()); // add member id to the param array
            creating_request.addParam(m.getHouseshare_id(), String.valueOf(subbills.get(m.getUsername()))); // add a param for each sub bill
        }

       new BillCreation_Worker(this, true).execute(new RequestQueue().addRequest(creating_request).toList());

    }

    @Override
    public void onBillCancelButtonClick(HS_Bill_Confirmation_Dialog f) {
        f.dismiss();
    }

    /**
     * Simply return the sub bill map to the dialog
     * @return the sub bill map
     */
    @Override
    public Map<String, Double> getSubBills() {
        return subbills;
    }


    /**
     * Worker for submitting a bill to the server
     */
    class BillCreation_Worker extends ConcurrentConnection {


        public BillCreation_Worker(Activity a) {
            super(a);
        }

        public BillCreation_Worker(Activity a, boolean showDialog) {
            super(a, showDialog);
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<Response> responses) {
            super.onPostExecute(responses);
            assert responses.size() == 1;
            if (responses.get(0).getToken(Responses_Format.RESPONSE_EXPIRED).equals("true")) {
                /* Display message box and auto logout user */
                final Connection temp_connect = new Connection(NewBillManual_SubBill.this);
                // experimenting a new message box builder
                CustomMessageBox.MessageBoxBuilder builder = new CustomMessageBox.MessageBoxBuilder(NewBillManual_SubBill.this, "Your session has been timed out, please login again");
                builder.setTitle("Expired")
                        .setActionOnClick(new CustomMessageBox.ToClick() {
                            @Override
                            public void DoOnClick() {
                                temp_connect.autoLogout(username);
                            }
                        }).build();
            }
                else if (responses.get(0).getToken(Responses_Format.RESPONSE_STATUS).equals("true")){
                    new CustomMessageBox.MessageBoxBuilder(NewBillManual_SubBill.this, "Your bill has been created. All target members will be notified soon." +
                            "\nWe will let you know if any update on the bill is available.")
                            .setTitle("Bill " + billName + " confirmed").build();
                }
            else {
                new CustomMessageBox.MessageBoxBuilder(NewBillManual_SubBill.this, "Sorry, we could not process this bill at the moment. \nPlease try again later.")
                        .setTitle("Error :(").build();
            }
            }
    }
}
