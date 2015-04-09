package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
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
import java.util.HashMap;
import java.util.HashSet;
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


public class NewBillManual extends Activity {
    private RelativeLayout layout;
    private EditText billName_view;
    private EditText dueDate_view;
    private EditText amount_view;
    private CheckBox addAll_view;
    private TableLayout members_table_view;

    private String billName;
    private String dueDate;
    private String totalAmount;
    private Map<String, Member> members = new TreeMap<String, Member>();
    private static String username;
    private static String housename;
    private RelativeLayout loading;


    private Set<Member> involved_members = new TreeSet<Member>();
    private Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bill_manual);
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

        dueDate_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                dueDate = s.toString();
            }
        });

        amount_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                totalAmount = s.toString();
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                members.put(arr_in.getString(1), new Member(arr_in.getString(0), arr_in.getString(1), arr_in.getString(2)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showMembers() {
        clearViews();
        int i = 1;
        for (final String memberName : members.keySet())
        {
            View v = members.get(memberName).craftView(getLayoutInflater());
            members_table_view.addView(v, i++);
            ((CheckBox) v.findViewById(R.id.checkBox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Member m = members.get(
                            ((TextView) ((RelativeLayout) buttonView.getParent()).findViewById(R.id.username_select)).getText().toString());

                    if (isChecked) {
                        involved_members.add(m);
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

}
