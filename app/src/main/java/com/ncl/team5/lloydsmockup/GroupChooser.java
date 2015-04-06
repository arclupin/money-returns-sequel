package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class GroupChooser extends Activity {

    private String username;
    private String date;
    private String accountNum;
    private Set<String> groupSets;
    private Set<String> transIDs;
    private String transId;
    private double transValue;
    private final int MAX_NUMBER_OF_GROUPS = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chooser);

        Intent i = getIntent();
        username = i.getStringExtra(IntentConstants.USERNAME);
        accountNum = i.getStringExtra("ACCOUNT_NUM");
        date = i.getStringExtra(IntentConstants.DATE);
        transValue = i.getDoubleExtra("VALUE", 0);
        transId = i.getStringExtra("TRANS_ID");
        /* I will need to populate the current groups here... probably by sending data to the server in some way
         * but need to wait for the tables ot be set up
         */

        /* Remove the space from the end of the account number */
        accountNum = accountNum.split(" ")[0];

        SharedPreferences settings = getSharedPreferences(username, 0);
        groupSets = settings.getStringSet("ANALYSIS_GROUPS_" + accountNum, new HashSet<String>());
        transIDs = settings.getStringSet("TRANS_ID_" + accountNum, new HashSet<String>());

        if(groupSets.size() == 0)
        {
            Log.d("SET VALS","New List");
        }

        groupSets.add("Choose Group");

        List<String> temp = new ArrayList<String>();
        temp.addAll(groupSets);

        for(int j = 0; j < temp.size(); j++)
        {
            temp.set(j, temp.get(j).split(":")[0]);
        }

        Spinner groupSpin = (Spinner) findViewById(R.id.groupNameSpinner);
        ArrayAdapter<String> a = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, temp);
        groupSpin.setAdapter(a);
        groupSpin.setSelection(a.getPosition("Choose Group"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Show notification icon in menu bar */
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.getItem(1);
        GetNotification notif = new GetNotification();


        if(notif.getNotifications(this, username, date))
        {
            Log.d("Notif Change", "IN HERE");
            item.setIcon(R.drawable.ic_action_notify);
        }
        else
        {
            Log.d("Notif Change", "IN There");
            item.setIcon(R.drawable.globe);
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
            intent.putExtra(IntentConstants.USERNAME, username);
            intent.putExtra(IntentConstants.DATE, date);
            startActivity(intent);
            ((KillApp) this.getApplication()).setStatus(false);
        }

        return super.onOptionsItemSelected(item);
    }



    public void btnClickAddGroup(View view)
    {
        //will need to make a selection about whether to choose group from text box or spinner... probably last
        //one they used and dynamically remove the data in the other box...

        Spinner groupSpin = (Spinner) findViewById(R.id.groupNameSpinner);
        TextView groupText = (TextView) findViewById(R.id.newGroupText);

        String groupName;

        if(groupSpin.getSelectedItem() == null || groupSpin.getSelectedItem().toString().equals("Choose Group"))
        {
            if(groupText.getText() == null || groupText.getText().toString().equals(""))
            {
                new CustomMessageBox(this, "Please select a group or enter a new one");
                return;
            }
            else
            {
                //use text box
                if(groupSets.size() <= MAX_NUMBER_OF_GROUPS) {
                    groupName = groupText.getText().toString();

                    List<String> temp = new ArrayList<String>(groupSets);

                    if(temp.size() == 0)
                    {
                        temp.add(groupName + ":" + transValue);
                    }
                    else {

                        for (int i = 0; i < temp.size(); i++) {
                            if (temp.get(i).split(":")[0].equals(groupName)) {
                                String splitStringName = temp.get(i).split(":")[0];
                                String splitStringAmount = temp.get(i).split(":")[1];
                                Double splitAmount = Double.parseDouble(splitStringAmount) + transValue;
                                splitStringAmount = splitAmount.toString();

                                temp.set(i, splitStringName + ":" + splitStringAmount);

                                Log.d("GROUP VALUES", temp.get(i));
                                i = temp.size();
                            } else if (i == temp.size() - 1) {
                                temp.add(groupName + ":" + transValue);
                            }
                        }
                    }

                    groupSets.clear();
                    groupSets.addAll(temp);

                }
                else
                {
                    new CustomMessageBox(this, "You cannot have that many groups");
                    return;
                }
            }
        }
        else
        {
            //use spinner

            groupName = groupSpin.getSelectedItem().toString();

            List<String> temp = new ArrayList<String>(groupSets);

            for(int i = 0; i < temp.size(); i++)
            {
                if(temp.get(i).split(":")[0].equals(groupName))
                {
                    String splitStringName = temp.get(i).split(":")[0];
                    String splitStringAmount = temp.get(i).split(":")[1];
                    Double splitAmount = Double.parseDouble(splitStringAmount) + transValue;
                    splitStringAmount = splitAmount.toString();

                    temp.set(i, splitStringName + ":" + splitStringAmount);

                    Log.d("GROUP VALUES",temp.get(i));
                }
            }

            groupSets.clear();
            groupSets.addAll(temp);

        }


        groupSets.remove("Choose Group");
        transIDs.add(transId);

        SharedPreferences sp = getApplicationContext().getSharedPreferences(username, 0);
        SharedPreferences.Editor edit = sp.edit();
        edit.putStringSet("ANALYSIS_GROUPS_" + accountNum, groupSets);
        edit.commit();
        edit.putStringSet("TRANS_ID_" + accountNum, transIDs);
        edit.commit();

        Toast msg = Toast.makeText(this, "Item Added Successfully", Toast.LENGTH_SHORT);
        msg.show();


        //add a new group here... probably save in a file somewhere
        ((KillApp) this.getApplication()).setStatus(false);
        this.finish();
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
