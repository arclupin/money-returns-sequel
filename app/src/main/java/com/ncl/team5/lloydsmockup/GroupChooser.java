package com.ncl.team5.lloydsmockup;

/* This is the group chooser class. This is where the
 * user can select a group for each transaction to go into
 */

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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class GroupChooser extends Activity {

    /* -- Variables -- */
    /* Strings */
    private String username;
    private String date;
    private String accountNum;
    private String currentTransId;

    /* Collections */
    private Set<String> groupSets;
    private Set<String> allTransId;

    /* Numbers */
    private double transValue;
    private final int MAX_NUMBER_OF_GROUPS = 6;

    /* Runs when activity is created */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chooser);

        /* get the intents from the statement class */
        Intent i = getIntent();
        username = i.getStringExtra(IntentConstants.USERNAME);
        accountNum = i.getStringExtra("ACCOUNT_NUM");
        date = i.getStringExtra(IntentConstants.DATE);
        transValue = i.getDoubleExtra("VALUE", 0);
        currentTransId = i.getStringExtra("TRANS_ID");

        /* Remove the space from the end of the account number */
        accountNum = accountNum.split(" ")[0];

        /* get the list of transaction ids that are in a group and a list of the groups */
        SharedPreferences settings = getSharedPreferences(username, 0);
        groupSets = settings.getStringSet("ANALYSIS_GROUPS_" + accountNum, new HashSet<String>());
        allTransId = settings.getStringSet("TRANS_ID_" + accountNum, new HashSet<String>());

        /* Just for debugging, new list has been created */
        if(groupSets.size() == 0)
        {
            Log.d("SET VALS","New List");
        }

        /* Add a choose group option to display */
        groupSets.add("Choose Group");

        /* Create a list from the set */
        List<String> groupsList = new ArrayList<String>();
        groupsList.addAll(groupSets);

        /* remove the amount from the group */
        for(int j = 0; j < groupsList.size(); j++)
        {
            groupsList.set(j, groupsList.get(j).split(":")[0]);
        }

        /* get the spinner and set the adapter */
        Spinner groupSpin = (Spinner) findViewById(R.id.groupNameSpinner);
        ArrayAdapter<String> a = new ArrayAdapter<String>(this, R.layout.spinner_text_colour, groupsList);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupSpin.setAdapter(a);

        /* set the default position to be choose group */
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

    /* Runs when an item is selected */
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

    /*===============================================================================================
                                    Start of user defined methods
     *===============================================================================================*/

    /*---------------------------------------------
     * addGroupButton Click method
     *
     * @params : Current view
     *
     * @return : void
     *
     * @usage : adds a group to the set. Takes
     *          the value in the text box if there
     *          is a value there, if not uses the
     *          spinner. Saves sets to shared
     *          preferences and closes activity
     *---------------------------------------------*/

    public void btnClickAddGroup(View view)
    {
        /* Get the spinner and the text view */
        Spinner groupSpin = (Spinner) findViewById(R.id.groupNameSpinner);
        TextView groupText = (TextView) findViewById(R.id.newGroupText);

        /* where the new group name is saved to */
        String newGroupName;

        /* checks if the spinner value has changed */
        if(groupSpin.getSelectedItem() == null || groupSpin.getSelectedItem().toString().equals("Choose Group"))
        {
            /* Checks if text box is empty */
            if(groupText.getText() == null || groupText.getText().toString().equals(""))
            {
                /* Error message */
                new CustomMessageBox(this, "Please select a group or enter a new one");
                return;
            }
            else
            {
                /* use text box */

                /* check that there are not too many groups */
                if(groupSets.size() <= MAX_NUMBER_OF_GROUPS)
                {
                    newGroupName = groupText.getText().toString();

                    /* list from the set */
                    List<String> groupList = new ArrayList<String>(groupSets);

                    /* if no values in list */
                    if(groupList.size() == 0)
                    {
                        groupList.add(newGroupName + ":" + transValue);
                    }
                    else {
                        /* Loop through list */
                        for (int i = 0; i < groupList.size(); i++) {

                            /* Need to check the name of each group without the value */
                            if (groupList.get(i).split(":")[0].equals(newGroupName)) {
                                /* Add the transValue onto the value currently in the list */
                                String splitStringName = groupList.get(i).split(":")[0];
                                String splitStringAmount = groupList.get(i).split(":")[1];
                                Double splitAmount = Double.parseDouble(splitStringAmount) + transValue;
                                splitStringAmount = splitAmount.toString();

                                groupList.set(i, splitStringName + ":" + splitStringAmount);

                                /* exit the loop */
                                i = groupList.size();
                            } else if (i == groupList.size() - 1) {
                                groupList.add(newGroupName + ":" + transValue);
                            }
                        }
                    }

                    /* clear the group and then add the list into it */
                    groupSets.clear();
                    groupSets.addAll(groupList);

                }
                else
                {
                    /* Too many groups */
                    new CustomMessageBox(this, "You cannot have that many groups");
                    return;
                }
            }
        }
        else
        {
            /* use spinner */

            newGroupName = groupSpin.getSelectedItem().toString();

            /* Same as above */
            List<String> groupList = new ArrayList<String>(groupSets);

            /* Loop through list and add to the current value
             * @see above for more info */
            for(int i = 0; i < groupList.size(); i++)
            {
                if(groupList.get(i).split(":")[0].equals(newGroupName))
                {
                    String splitStringName = groupList.get(i).split(":")[0];
                    String splitStringAmount = groupList.get(i).split(":")[1];
                    Double splitAmount = Double.parseDouble(splitStringAmount) + transValue;
                    splitStringAmount = splitAmount.toString();

                    groupList.set(i, splitStringName + ":" + splitStringAmount);
                }
            }

            /* clear the set and add the list to it */
            groupSets.clear();
            groupSets.addAll(groupList);

        }

        /* remove the choose group option */
        groupSets.remove("Choose Group");

        /* add the trans id to the set */
        allTransId.add(currentTransId);

        /* save the sets to shared preferences */
        SharedPreferences sp = getApplicationContext().getSharedPreferences(username, 0);
        SharedPreferences.Editor edit = sp.edit();
        edit.putStringSet("ANALYSIS_GROUPS_" + accountNum, groupSets);
        edit.putStringSet("TRANS_ID_" + accountNum, allTransId);
        edit.commit();

        /* Give the user a message */
        Toast msg = Toast.makeText(this, "Item Added Successfully", Toast.LENGTH_SHORT);
        msg.show();

        /* return to statement */
        ((KillApp) this.getApplication()).setStatus(false);
        this.finish();
    }




    /* This is where the test is done to see whether the KillApp variable is true, and if it is, to call
     * the login class. It also clears the activity stack so the back button cannot be used to go back
     *
     * @see below for more info */
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

/* Kill App is how the application knows if it has been stopped by an intent or by an
 * external source (i.e. home button, phone call etc). Each time an intent is called, it
 * sets an application global variable denoted as KillApp to false. This means that when a new
 * activity is opened, it does not want to restart the application. However if no intent is
 * fired (i.e. phonecall, home button pressed) KillApp will have the value true so it will
 * restart back to the login activity.
 */