package com.ncl.team5.lloydsmockup;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcelable;
import android.support.v4.app.NavUtils;
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
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ncl.team5.lloydsmockup.Houseshares.Member;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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


public class NewBillManual extends Activity {
    private RelativeLayout layout;
    private EditText billName_view;
    private EditText dueDate_view;
    private EditText amount_view;
    private EditText message_view;
    private CheckBox addAll_view;
    private TableLayout members_table_view;
    private ActionBar actionBar;

    private String billName;
    private String dueDate;
    private String totalAmount;
    private String message;
    private Map<String, Member> members = new TreeMap<String, Member>();
    private static String username;
    private static String housename;
    private RelativeLayout loading;

    private boolean isDueDateSupplied = false;
    private boolean isAmountSupplied = false;
    //no need for bill name as we have the text watcher do this job


    private Set<Member> involved_members = new TreeSet<Member>();
    private Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bill_manual);
        actionBar = getActionBar();
        if (actionBar != null)
            actionBar.setSplitBackgroundDrawable(new ColorDrawable((getResources().getColor(R.color.dark_green))));

        i = getIntent();
        if (i.getStringExtra(IntentConstants.USERNAME) != null)
            username = i.getStringExtra(IntentConstants.USERNAME);
        if (i.getStringExtra(IntentConstants.HOUSE_NAME) != null)
            housename = i.getStringExtra(IntentConstants.HOUSE_NAME);

        layout = (RelativeLayout) findViewById(R.id.layout);
        members_table_view = (TableLayout) layout.findViewById(R.id.table_users);
        billName_view = (EditText) layout.findViewById(R.id.bill_name_value);
        dueDate_view = (EditText) layout.findViewById(R.id.due_date_value);
        amount_view = (EditText) layout.findViewById(R.id.total_amount_value);
        message_view = (EditText) layout.findViewById(R.id.bill_message_value);

        loading = (RelativeLayout) layout.findViewById(R.id.loading_notice);
        addAll_view = (CheckBox) layout.findViewById(R.id.checkBox_all);
        addAll_view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    setAllChecked();
                else
                    setAllUnchecked();
            }
        });

        // add text listener for bill name
        billName_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                billName = s.toString();
            }
        });

        // register data validator on UI
        Utilities.registerValidator(dueDate_view, new Validator() {
            @Override
            public boolean isDataValid(String s) {
                return StringUtils.isStringADate(s);
            }
        });
        dueDate_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (StringUtils.isStringADate(s.toString())) {
                    dueDate = s.toString();
                    isDueDateSupplied = true;
                } else
                    isDueDateSupplied = false; // false it
            }
        });

        // register data validator on UI
        Utilities.registerValidator(amount_view, new Validator() {
            @Override
            public boolean isDataValid(String s) {
                return StringUtils.isStringValidAmount(s);
            }
        });

        // TODO _FIXME I still haven't figured out a way to make this code snippet reusable
        amount_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (StringUtils.isStringValidAmount(s.toString())) {
                    totalAmount = s.toString();
                    isAmountSupplied = true;
                }
                else
                    isAmountSupplied = false; // false it
        }});

        message_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                message = s.toString();
            }
        });


        Request r = new Request(Request.TYPE.POST);
        r.addParam(Request_Params.PARAM_TYPE, Request_Params.HS_ALL_MEMBERS)
                .addParam(Request_Params.PARAM_USR, username);
        Log.d("request", r.toString());
        new BillCreator_Worker(this, false).execute(new RequestQueue().
                addRequest(r).toList());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_bill_manual, menu);
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

            case android.R.id.home: {
                Intent i = new Intent(this, Houseshare_HomeView.class);
                i.putExtra(IntentConstants.USERNAME, username);
                i.putExtra(IntentConstants.HOUSE_NAME, housename);
                NavUtils.navigateUpTo(this, i);
                return true;
            }
            case R.id.action_back: {
                Intent i = new Intent(this, Houseshare_HomeView.class);
                i.putExtra(IntentConstants.USERNAME, username);
                i.putExtra(IntentConstants.HOUSE_NAME, housename);
                NavUtils.navigateUpTo(this, i);
                return true;
            }
            case R.id.action_forward: {
                bttn_next_sub_bill();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void bttn_next_sub_bill() {
        if (isDataSupplied()) {
            Intent i = new Intent(this, NewBillManual_SubBill.class);
            i.putExtra(IntentConstants.USERNAME, username);
            i.putExtra(IntentConstants.HOUSE_NAME, housename);
            i.putExtra(IntentConstants.BILL_NAME, billName);
            i.putExtra(IntentConstants.BILL_DUE_DATE, dueDate);
            i.putExtra(IntentConstants.BILL_AMOUNT, totalAmount);
            i.putExtra(IntentConstants.BILL_MESSAGE, message);
            Log.d("involved intent", ""+ involved_members.size());
            i.putParcelableArrayListExtra(IntentConstants.MEMBERS, new ArrayList<Parcelable>(involved_members));
            startActivity(i);
        }

        else {
            // users have not chosen any user
            if (involved_members.size() == 0)
                 new CustomMessageBox.MessageBoxBuilder(this, "Please select at least one target member for this bill.")
                    .setTitle("Warning").build();
            else // something wrong with the data
                new CustomMessageBox.MessageBoxBuilder(this, "Please review your data and supply correct information.")
                        .setTitle("Warning").build();
        }

    }

    private void filterMembers(String r) {
        try {
            JSONObject j = new JSONObject(r);
            if (j.getString("expired").equals("true")) {

                 /* Display message box and auto logout user */
                final Connection temp_connect = new Connection(this);
                // experimenting a new message box builder
                CustomMessageBox.MessageBoxBuilder builder = new CustomMessageBox.MessageBoxBuilder(this, "Your session has been timed out, please login again");
                builder.setTitle("Expired")
                        .setActionOnClick(new CustomMessageBox.ToClick() {
                            @Override
                            public void DoOnClick() {
                                temp_connect.autoLogout(username);
                            }
                        }).build();
                return;
            }
            JSONArray arr_out = j.getJSONArray(Responses_Format.RESPONSE_MEMBERS);
            for (int i = 0; i < arr_out.length(); i++) {
                JSONArray arr_in = arr_out.getJSONArray(i);
                members.put(arr_in.getString(0), new Member(arr_in.getString(0), arr_in.getString(1), arr_in.getString(2)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showMembers() {
        clearViews();
        int i = 1;
        for (final String houseshareID : members.keySet())
        {
            Log.d("member", houseshareID + members.get(houseshareID) + " hash: " + members.get(houseshareID).hashCode());
            View v = members.get(houseshareID).craftViewInfo(getLayoutInflater());
            members_table_view.addView(v, i++);
            ((CheckBox) v.findViewById(R.id.checkBox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Member m = members.get(getHouseshareID(
                            ((TextView) ((RelativeLayout) buttonView.getParent()).findViewById(R.id.username_select)).getText().toString()));
                    Log.d("member checked changed", m.toString());
                    if (isChecked) {
                        Log.d("add okay?" , "/" + involved_members.add(m));
                        Log.d("involved", Arrays.toString(involved_members.toArray(new Member[involved_members.size()])));

                        if (involved_members.size() == members.size()) {
                            Log.d("full", involved_members.size() + " - " + members.size());
                            addAll_view.setChecked(true);
                        }
                        else
                            Log.d("not full", involved_members.size() + " - " + members.size());
                    }
                    else {
                        involved_members.remove(m);
                        Log.d("involved", Arrays.toString(involved_members.toArray(new Member[involved_members.size()])));
                        if (addAll_view.isChecked())
                            addAll_view.setChecked(false);
                    }

                }
            });
        }
    }

    private void clearViews() {
        for (int i = 0; i < members_table_view.getChildCount() - 1; i++)
            members_table_view.removeViewAt(i);
    }

    public String getBillName() {
        return billName;
    }

    public void setBillName(String billName) {
        this.billName = billName;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Map<String, Member> getMembers() {
        return members;
    }

    public void setMembers(Map<String, Member> members) {
        this.members = members;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        NewBillManual.username = username;
    }

    public static String getHousename() {
        return housename;
    }

    public static void setHousename(String housename) {
        NewBillManual.housename = housename;
    }
    public Set<Member> getInvolved_members() {
        return involved_members;
    }

    public void setInvolved_members(Set<Member> involved_members) {
        this.involved_members = involved_members;
    }

    public class BillCreator_Worker extends ConcurrentConnection {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
        }

        public BillCreator_Worker(Activity a) {
            super(a);
        }
        public BillCreator_Worker(Activity a, boolean b) {
            super(a, b);
        }
        @Override
        protected void onPostExecute(List<Response> responses) {
            super.onPostExecute(responses);
            loading.setVisibility(View.GONE);
            filterMembers(responses.get(0).getRaw_response());
            showMembers();
        }
    }

    private void setAllChecked() {
        for (int i = 1; i < members_table_view.getChildCount(); i++) {

            ((CheckBox) members_table_view.getChildAt(i).findViewById(R.id.checkBox)).setChecked(true);

        }
        involved_members.addAll(members.values());
        Log.d("involved", Arrays.toString(involved_members.toArray(new Member[involved_members.size()])));
    }

    private void setAllUnchecked() {
        for (int i = 1; i < members_table_view.getChildCount(); i++) {
            ((CheckBox) members_table_view.getChildAt(i).findViewById(R.id.checkBox)).setChecked(false);
        }
        involved_members.clear();

        Log.d("involved", Arrays.toString(involved_members.toArray(new Member[involved_members.size()])));
    }

    private boolean isDataSupplied() {
        Log.d("validate date", billName + " / amount supplied: " + isAmountSupplied + "/ due date supplied: " + isDueDateSupplied);
        return billName != null && isDueDateSupplied && isAmountSupplied && involved_members.size() > 0;
    }

    private String getHouseshareID(String username) {

        String id = null;
        for (Map.Entry<String, Member> entry : members.entrySet())
            if (entry.getValue().getUsername().equals(username.trim()))
                return entry.getKey();
        return id;
    }
}
