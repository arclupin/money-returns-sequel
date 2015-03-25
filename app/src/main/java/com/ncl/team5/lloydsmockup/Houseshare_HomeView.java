package com.ncl.team5.lloydsmockup;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import HTTPConnect.Connection;
import HTTPConnect.Request_Params;
import HTTPConnect.Responses_Format;
import Utils.StringUtils;


public class Houseshare_HomeView extends Activity {
    private String username;
    private String house_name;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_houseshare__home_view);

        Intent i = this.getIntent();
        username = i.getExtras().getString("ACCOUNT_USERNAME");
        house_name = i.getExtras().getString("HOUSE_NAME");
        ActionBar a = getActionBar();
        if (a != null)
            a.setTitle(StringUtils.isFieldEmpty(house_name) ? "My house" : house_name);


        fetchHomeViewInfo();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_houseshare__home_view, menu);
        this.menu = menu;
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

        else if (id == R.id.action_add_user) {
            menu.findItem(R.id.action_hs_noti).setIcon(R.drawable.globe);
            item.setIcon(R.drawable.add_user_clicked);

            return true;
        }

        else if (id == R.id.action_hs_noti) {
            menu.findItem(R.id.action_add_user).setIcon(R.drawable.add_user);
            item.setIcon(R.drawable.globe_clicked);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchHomeViewInfo() {
        Connection connect = new Connection(this);
        String result;

        try {
            /* Command required to make a payment, takes username, to account, from account, both sort codes and amount
             * Returns: JSON String */
            result = connect.execute(Request_Params.PARAM_TYPE, Request_Params.VAL_HS_2_FETCH_HOUSE_DETAIL, Request_Params.PARAM_USR, this.username).get();
            /* Turns String into JSON object, can throw JSON Exception */
            JSONObject jo = new JSONObject(result);

            /* Check if the user has timed out */
            if (jo.getString("expired").equals("true")) {

                /* Display message box and auto logout user */
                AlertDialog.Builder errorBox = new AlertDialog.Builder(this);
                final Connection temp_connect = connect;
                final String temp_usr = username;
                errorBox.setMessage("Your session has been timed out, please login again")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                temp_connect.autoLogout(temp_usr);
                            }
                        });
                AlertDialog alert = errorBox.create();
                alert.show();
            }
            else {
               TextView tv = (TextView) findViewById(R.id.hs_hv_response);
                tv.setText(jo.getString(Responses_Format.RESPONSE_HS_CONTENT).toString());
            }

        }
        /* Catch the exceptions */ catch (JSONException jse) {
            /* Error in the JSON response */

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
