package com.ncl.team5.lloydsmockup;

/* Statement Class
 * This Activity displays the users statement, which is all of the 
 * transactions that have occured on this account. 
 *
 * Created by Ben Lambert
 * Last edit: 8/4/15 by Ben Lambert
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import HTTPConnect.Connection;


public class Statement extends Activity {

    /* --- Variables --- */
    /* Strings */
    private String username;
    private String accountNum;
    private String dateLogout;
    
    /* statement object */
    private Statement statement = this;
    
    /* List view object */
    private ListView transactions;
    
    /* Collection objects */
    private Set<String> transIds;
    private List<String> statementList = new ArrayList<String>();
    private List<String> transInfo = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement);

        /* Get the intent messages from accounts activity */
        Intent i = getIntent();
        username = i.getStringExtra("USERNAME");
        accountNum = i.getStringExtra("ACCOUNT_NUM");
        dateLogout = i.getStringExtra(IntentConstants.DATE);
        String balance = i.getStringExtra("BALANCE");

        /* remove the space from the end of the account number */
        accountNum = accountNum.split(" ")[0];

        /* Get the transaction ids from shared preferences, so we know which transactions have been added
         * to a group */
        SharedPreferences settings = getSharedPreferences(username, 0);
        transIds = settings.getStringSet("TRANS_ID_" + accountNum, new HashSet<String>());


        /* Sets the account name from the result text */
        TextView accountName = (TextView) findViewById(R.id.txtChange);
        accountName.setTextSize(30);

        /* Changes the size of the text at the top of the screen depending on how long the username and balance are */
        if(username.length()+balance.length() > 20)
        {
            accountName.setTextSize(25);
        }

        /* Sets the text */
        accountName.setText(username + ":" + balance);

        /* gets the transactions list */
        transactions =(ListView)findViewById(R.id.listView);

        /* gets the recent transactions from the server
         * @see getStatement() */
        getStatement();

        /* Create a custom adapter so we can change the colours displayed */
        transactions.setAdapter(new BaseAdapter() {
            /* Needs to return the size of the list */
            @Override
            public int getCount() {
                return statementList.size();
            }

            /* Returns the item that is at the position selected */
            @Override
            public Object getItem(int i) {
                return statementList.get(i);
            }

            /* gets the id of the item at the position selected */
            @Override
            public long getItemId(int i) {
                return i;
            }

            /* Used to display the list (called when list is created) */
            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {

                /* Variable used for inside the on click handler */
                final int position = i;

                /* Creates a new view if it is null */
                if (view == null)
                {
                    view = new TextView(Statement.this);
                    view.setPadding(10, 30, 10, 10);
                    ((TextView)view).setTextColor(Color.WHITE);
                }

                /* Sets the colour depending on if the transaction is already in a group */
                if(transIds.contains(statementList.get(i).split(":")[0].split(" ")[0])) {
                    view.setBackgroundColor(Color.DKGRAY);
                    ((TextView) view).setText((String) getItem(i));
                }
                else
                {
                    view.setBackgroundColor(Color.GRAY);
                    ((TextView) view).setText((String) getItem(i));
                }

                /* More settings for the list in general, such as size, is scrollable etc */
                viewGroup.setVerticalScrollBarEnabled(true);
                view.setClickable(true);
                ((TextView) view).setHeight(150);
                ((TextView) view).setTextSize(18);

                /* The on click listener for each view */
                view.setOnClickListener(new View.OnClickListener() {
                    /* On click */
                    @Override
                    public void onClick(View view) {
                        /* If user clicked on transaction that is already in a group, show error message */
                        if(transIds.contains(statementList.get(position).split(":")[0].split(" ")[0])) {
                            new CustomMessageBox(Statement.this, "This transaction has already been added to a group, go into settings to remove it from a group.");
                            return;
                        }

                        /* create a more info dialog box */
                        AlertDialog.Builder displayBox = new AlertDialog.Builder(Statement.this);
                        String details = transInfo.get(position);
                        final String transId = details.split(" ~ ")[0];
                        final String amount = details.split(" ~ ")[1];
                        String to = details.split(" ~ ")[3];

                        /* Extract date and time */
                        String time = details.split(" ~ ")[2];
                        String date = time.split(" ")[0];
                        time = time.split(" ")[1];

                        /* Create the string */
                        details = "ID : " + transId + "\nAmount : " + amount + "\nDate : " + date + "\nTime : " + time + "\nTo account : " + to;

                        /* Set the handlers */
                        displayBox.setMessage(details)
                                .setCancelable(true)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    /* Positive button (OK) */
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("Add to Group", new DialogInterface.OnClickListener() {
                                    /* Negative button (Add to group) */
                                    public void onClick(DialogInterface dialog, int id) {
                                        /* Launch the group chooser */
                                        ((KillApp) statement.getApplication()).setStatus(false);
                                        Intent i = new Intent(statement, GroupChooser.class);
                                        i.putExtra("TRANS_ID", transId);
                                        i.putExtra("ACCOUNT_USERNAME", username);
                                        i.putExtra("DATE", dateLogout);
                                        i.putExtra("ACCOUNT_NUM", accountNum);
                                        i.putExtra("VALUE", Double.parseDouble(amount.substring(1)));
                                        startActivity(i);
                                    }
                                });

                        /* Show the alert */
                        AlertDialog alert = displayBox.create();
                        alert.show();
                    }
                });//end of on click event listener

                /* Needs to return the view */
                return view;
            }
        });
    }

    /* Create the options menu */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Show notification icon in menu bar */
        getMenuInflater().inflate(R.menu.main, menu);

        /* Gets the menu item */
        MenuItem item = menu.getItem(R.id.action_notifications);
        /* Creates a get notification object */
        GetNotification notif = new GetNotification();

        /* Checks if there are any notifications, if there are, show red icon */
        if(notif.getNotifications(this, username, dateLogout)) {
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

    /* On item selected for menu bar */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        /* Gets the item id that was clicked */
        int id = item.getItemId();

        /* back button clicked */
        if (id == R.id.action_backHome) {
            ((KillApp) this.getApplication()).setStatus(false);
            this.finish();
        }
        /* notifications clicked */
        else if (id == R.id.action_notifications) {
            Intent intent = new Intent(this, Notifications.class);
            intent.putExtra("USERNAME", username);
            intent.putExtra(IntentConstants.DATE, dateLogout);
            startActivity(intent);
            ((KillApp) this.getApplication()).setStatus(false);
        }

        return super.onOptionsItemSelected(item);
    }


    /*=============================================================================================================
                                            Start of user defined methods
     *============================================================================================================*/

    /*-------------------------------------------------
     * getStatement
     *
     * @params : none
     *
     * @return : void
     *
     * @usage : Connects to the server and gets the
     *          transactions for the account number,
     *          populates statementList and transInfo
     *------------------------------------------------- */

    private void getStatement() {

        /* Create a new connection */
        Connection hc = new Connection(this);

        /* Start of error catching */
        try {
            /* Get transactions for the account */
            String result = hc.execute("TYPE", "TRANSLIST", "USERNAME", username, "ACC_NUMBER", accountNum).get();

            /* Create JSON object from the returned string */
            JSONObject jo = new JSONObject(result);

            /* Log user out */
            if (jo.getString("expired").equals("true")) {
                /* logout message */
                AlertDialog.Builder errorBox = new AlertDialog.Builder(this);
                errorBox.setMessage("You have been timed out, please login again")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                autoLogout();
                            }
                        });
                AlertDialog alert = errorBox.create();
                alert.show();
            }
            else {
                /* Amount string to hold formatted amount and get transactions array */
                String amountString;
                JSONArray jsonArray = jo.getJSONArray("transactions");

                /* No past transactions on the account */
                if (jsonArray.length() == 0) {
                    new CustomMessageBox(this, "Unfortunately you have no transaction information");
                    return;
                }

                /* Loop through the array */
                for (int i = 0; i < jsonArray.length(); i++) {

                    /* Create a JSON object from each element */
                    JSONObject insideObject = jsonArray.getJSONObject(i);

                    /* User regex to parse the amount (in case it comes with £ attached) */
                    if (insideObject.getString("Amount").matches("[0-9]+")) {
                        amountString = insideObject.getString("Amount") + ".00";
                    }
                    else {
                        amountString = insideObject.getString("Amount");
                    }

                    if (insideObject.getString("Payer").equals(accountNum)) {
                        /* Payment INTO account */
                        statementList.add(insideObject.getString("Transaction_ID") + " : + £" + amountString);
                        transInfo.add(insideObject.getString("Transaction_ID") + " ~ £" + amountString + " ~ " + insideObject.getString("Time") + " ~ " + insideObject.getString("Payee"));

                    } else {
                        /* Payment OUT OF account */
                        statementList.add(insideObject.getString("Transaction_ID") + " : - £" + amountString);
                        transInfo.add(insideObject.getString("Transaction_ID") + " ~ £" + amountString + " ~ " + insideObject.getString("Time") + " ~ " + insideObject.getString("Payee"));
                    }
                }
            }
        }
        /* Catch the exceptions */
        catch (JSONException jse)
        {
            /* Exception for when the JSON cannot be parsed correctly */
            new CustomMessageBox(this, "There was an error in the server response");
            jse.printStackTrace();
        }
        catch (InterruptedException interex)
        {
            /* Caused when the connection is interrupted */
            new CustomMessageBox(this, "Connection has been interrupted");
            interex.printStackTrace();
        }
        catch (ExecutionException ee)
        {
            /* No idea when this is caused but it throws it... */
            new CustomMessageBox(this, "Execution Error");
            ee.printStackTrace();
        }
        catch (Exception e) {
            /* Failsafe if something goes utterly wrong */
            new CustomMessageBox(this, "An unknown error occurred");
            e.printStackTrace();
        }
    }

    /*-------------------------------------------------
     * autoLogout
     *
     * @params : none
     *
     * @return : void
     *
     * @usage : Auto logs out the user from the on close
     *          inside the dialog box, as connections
     *          cannot be started inside message boxes
     *------------------------------------------------- */
    private void autoLogout()
    {
        /* Create a new connection */
        Connection hc = new Connection(this);
        /* try to send logout to server */
        try
        {
            hc.execute("TYPE","LOGOUT", "USERNAME", username);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        /* Logout either way */
        ((KillApp) this.getApplication()).setStatus(false);
        finish();
        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /*=============================================================================================================
                                  Start of overridden non standard android methods
     *============================================================================================================*/

    /* This is where the test is done to see whether the KillApp variable is true, and if it is, to call
     * the login class. It also clears the activity stack so the back button cannot be used to go back,
     *
     * @see bottom of class for more details */
    @Override
    protected void onResume() {

        /* Redraw the list (for colour changes) */
        transactions.invalidateViews();

        if(((KillApp) this.getApplication()).getStatus())
        {
            /* Finish this activity without setting kill app so it starts a domino affect to main */
            finish();
        }
        else
        {
            /* each time the app resumes and it wasnt killed, the variable needs to be reset */
            ((KillApp) this.getApplication()).setStatus(true);
        }

        super.onResume();
    }

    /* overriding the on back pressed method (for the built in back button) so
     * its status can be set to false, so it doesnt launch the login on on resume */
    @Override
    public void onBackPressed() {
        ((KillApp) this.getApplication()).setStatus(false);
        finish();
    }
}


/* KillApp is how the application knows if it has been stopped by an intent or by an
 * external source (i.e. home button, phone call etc). Each time an intent is called, it
 * sets an application global variable denoted as KillApp to false. This means that when a new
 * activity is opened, it does not want to restart the application. However if no intent is
 * fired (i.e. phonecall, home button pressed) KillApp will have the value true so it will
 * restart back to the login activity.*/
