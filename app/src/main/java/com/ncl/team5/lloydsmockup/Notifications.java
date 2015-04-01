package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import HTTPConnect.Connection;


public class Notifications extends Activity {

    private String username;
    private List<String> notif = new ArrayList<String>();
    private String logDate;
    private List<String> accountNums = new ArrayList<String>();
    private List<String> displayStrings = new ArrayList<String>();
    private List<Boolean> toNotify = new ArrayList<Boolean>();
    private String logoutTime;
    private final int NOTIFICATION_MAX = 10;
    private int recentCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        Intent intent = getIntent();
        username = intent.getStringExtra("ACCOUNT_USERNAME");
        logDate = intent.getStringExtra("DATE");


        SharedPreferences settings = getSharedPreferences(username, 0);
        logoutTime = settings.getString("LOGOUT_TIME", "");

        if(logoutTime.equals(""))
        {
            logoutTime = logDate;
        }

        /* Show all transactions in the account */
        if(logDate != null && logDate.equals("not available"))
        {
            //TODO populate the list without notifiactions I think
        }

        getAllAccounts();
        getNotif();

        for(int i = 0; i < notif.size(); i++)
        {
            displayStrings.add(notif.get(i).split("\t")[0] + " " + notif.get(i).split("\t")[1]);
        }

        ListView lv = (ListView) findViewById(R.id.Notification_List_View);
        lv.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return displayStrings.size();
            }

            @Override
            public Object getItem(int i) {
                return displayStrings.get(i);
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {

                if (view == null)
                {
                    view = new TextView(Notifications.this);
                    view.setPadding(10, 30, 10, 10);
                    ((TextView)view).setTextColor(Color.WHITE);
                }

                if(toNotify.get(i)) {
                    view.setBackgroundColor(Color.RED);
                    ((TextView) view).setText((String) getItem(i));
                }
                else
                {
                    view.setBackgroundColor(Color.GRAY);
                    ((TextView) view).setText((String) getItem(i));
                }

                viewGroup.setVerticalScrollBarEnabled(true);
                view.setClickable(true);
                ((TextView) view).setHeight(150);
                ((TextView) view).setTextSize(18);




                return view;
            }
        });

        SharedPreferences sp = getSharedPreferences("transinsession", 0);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean("IN_SESSION", true);
        edit.commit();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_noti, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_backHome) {
            this.finish();
            ((KillApp) this.getApplication()).setStatus(false);
            //Intent intent = new Intent(this, MainActivity.class);
            //startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /* Get all of the account numbers for the account, so notifications can be taken for all of them */
    public void getAllAccounts()
    {
        Connection hc = new Connection(this);

        try {
            String result = hc.execute("TYPE","SAA","USR", username ).get();

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
                    accountNums.add(insideObject.getString("account_number"));
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /* Compare the times of transactions with the last login time, any time after last login
     * is flagged as new notification */
    public void getNotif() {

        /* Clear the list so it gets reset each time the activity is visited */
        notif.clear();
        toNotify.clear();

        for (int k = 0; k < accountNums.size(); k++) {

            String accountNum = accountNums.get(k);

            SharedPreferences settings = getSharedPreferences("transinsession", 0);
            boolean transInSession = settings.getBoolean("IN_SESSION", false);



            /* Start the connection */
            Connection hc = new Connection(this);

            try {
                /* This is the command needed for the transactions, takes username and account number, returns JSON String */
                String result = hc.execute("TYPE", "TRANSLIST", "USR", username, "ACC_NUMBER", accountNum).get();

                /* Tries to convert to JSON Object, can throw JSON Exception */
                JSONObject jo = new JSONObject(result);

                /* Check if account has expired (very unlikely as this is called after get accounts) */
                if (jo.getString("expired").equals("true")) {
                    /* Display error message and log user out as they have expired */
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
                    /* Array needed as transaction returned inside JSON array */
                    JSONArray jsonArray = jo.getJSONArray("transactions");

                    /* There are no transactions if the array length is zero */
                    if (jsonArray.length() == 0) {
                        /* there are no transactions, so no notifications */
                        new CustomMessageBox(this, "There have been no transactions for your account");
                        this.finish();
                        return;
                    }

                    /* add the last three payees to the spinner */
                    for (int i = 0; i < jsonArray.length(); i++) {

                        /* Gets an object of each element in the array */
                        JSONObject insideObject = jsonArray.getJSONObject(i);

                        /* Gets the date field and parse it into a date variable, can throw an exception but never should... */
                        String date = insideObject.getString("Time");
                        Date loginTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(logDate);
                        Date timeFromResponse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
                        Date logoutTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(this.logoutTime);


                        if(timeFromResponse.compareTo(loginTime) > 0) {

                            if (insideObject.getString("Payee").equals(accountNum)) {
                                notif.add("Into Account:\t" + "£" + insideObject.getString("Amount") + " From " + insideObject.getString("Payer") + "\t" + insideObject.getString("Time"));
                                recentCount++;
                                toNotify.add(false);
                            } else {
                                notif.add("Out Account:\t" + "£" + insideObject.getString("Amount") + " To " + insideObject.getString("Payer") + "\t" + insideObject.getString("Time"));
                                recentCount++;
                                toNotify.add(false);
                            }
                        }
                        else if (notif.size() == 0 && timeFromResponse.compareTo(logoutTime) > 0 && timeFromResponse.compareTo(loginTime) < 0 && !transInSession)
                        {
                            if(insideObject.getString("Payee").equals(accountNum))
                            {
                                notif.add("Into Account:\t" + "£" + insideObject.getString("Amount") + " From " + insideObject.getString("Payer") + "\t" + insideObject.getString("Time"));
                                toNotify.add(true);
                            }
                            else
                            {
                                notif.add("Out Account:\t" + "£" + insideObject.getString("Amount") + " To " + insideObject.getString("Payer") + "\t" + insideObject.getString("Time"));
                                toNotify.add(true);
                            }
                        }
                        else if(notif.size() == 0 && timeFromResponse.compareTo(logoutTime) < 0)
                        {
                            if(recentCount < NOTIFICATION_MAX) {

                                if (insideObject.getString("Payee").equals(accountNum)) {
                                    notif.add("Into Account:\t" + "£" + insideObject.getString("Amount") + " From " + insideObject.getString("Payer") + "\t" + insideObject.getString("Time"));
                                    recentCount++;
                                    toNotify.add(false);
                                } else {
                                    notif.add("Out Account:\t" + "£" + insideObject.getString("Amount") + " To " + insideObject.getString("Payer") + "\t" + insideObject.getString("Time"));
                                    recentCount++;
                                    toNotify.add(false);
                                }
                            }
                        }
                        else
                        {
                            for(int c = 0; c < notif.size(); c++) {
                                Date fromListTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(notif.get(c).split("\t")[2]);

                                if (timeFromResponse.compareTo(logoutTime) > 0 && timeFromResponse.compareTo(fromListTime) > 0 && !transInSession) {
                                    if (insideObject.getString("Payee").equals(accountNum)) {
                                        notif.add(c, "Into Account:\t" + "£" + insideObject.getString("Amount") + " From " + insideObject.getString("Payer") + "\t" + insideObject.getString("Time"));
                                        toNotify.add(c, true);
                                        c = notif.size();

                                    } else {
                                        notif.add(c, "Out Account:\t" + "£" + insideObject.getString("Amount") + " To " + insideObject.getString("Payer") + "\t" + insideObject.getString("Time"));
                                        toNotify.add(c, true);
                                        c = notif.size();
                                    }
                                } else if (timeFromResponse.compareTo(fromListTime) > 0) {
                                    if (insideObject.getString("Payee").equals(accountNum)) {
                                        notif.add(c, "Into Account:\t" + "£" + insideObject.getString("Amount") + " From " + insideObject.getString("Payer") + "\t" + insideObject.getString("Time"));
                                        toNotify.add(c, false);
                                        c = notif.size();
                                        recentCount++;
                                    } else {
                                        notif.add(c, "Out Account:\t" + "£" + insideObject.getString("Amount") + " To " + insideObject.getString("Payer") + "\t" + insideObject.getString("Time"));
                                        toNotify.add(c, false);
                                        c = notif.size();
                                        recentCount++;
                                    }
                                } else if (c == notif.size() - 1) {
                                    if (insideObject.getString("Payee").equals(accountNum)) {
                                        notif.add("Into Account:\t" + "£" + insideObject.getString("Amount") + " From " + insideObject.getString("Payer") + "\t" + insideObject.getString("Time"));
                                        toNotify.add(c, false);
                                        c = notif.size();
                                        recentCount++;
                                    } else {
                                        notif.add("Out Account:\t" + "£" + insideObject.getString("Amount") + " To " + insideObject.getString("Payer") + "\t" + insideObject.getString("Time"));
                                        toNotify.add(c, false);
                                        c = notif.size();
                                        recentCount++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            /* Catch the exceptions */
            catch (JSONException jse) {
            /* Exception for when the JSON cannot be parsed correctly */
                new CustomMessageBox(this, "There was an error in the server response");
                jse.printStackTrace();
            } catch (InterruptedException interex) {
            /* Caused when the connection is interrupted */
                new CustomMessageBox(this, "Connection has been interrupted");
                interex.printStackTrace();
            } catch (ExecutionException ee) {
            /* No idea when this is caused but it throws it... */
                new CustomMessageBox(this, "Execution Error");
                ee.printStackTrace();
            } catch (Exception e) {
            /* Failsafe if something goes utterly wrong */
                new CustomMessageBox(this, "An unknown error occurred");
                e.printStackTrace();
            }
        }
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

    public void onPause()
    {
         /* Set the logout time so it can easily get it later */
        SharedPreferences sp = getSharedPreferences(username, 0);
        SharedPreferences.Editor edit = sp.edit();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String logout = df.format(Calendar.getInstance().getTime());
        edit.putString("TEMP_LOGOUT_TIME", logout);
        edit.commit();

        super.onPause();
    }

    /* Called from inside the error box that appears on timeout */
    private void autoLogout() {

        /* Start a new connection */
        Connection hc = new Connection(this);
        try {
            /* try to execute a logout on the server */
            hc.execute("TYPE", "LOGOUT", "USR", username);
        }
        catch (Exception e) {
            /* Doesnt really need a detailed error as user is logged out anyway, just print stack trace */
            e.printStackTrace();
        }

        /* Has to kill the app whether it has managed to send a logout or not */
        ((KillApp) this.getApplication()).setStatus(false);
        finish();
        Intent intent1 = new Intent(getApplicationContext(), Login.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent1);
    }
}
