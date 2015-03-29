package com.ncl.team5.lloydsmockup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import Fragment.Fragment_HS_Notification;
import HTTPConnect.Connection;
import HTTPConnect.Request_Params;
import HTTPConnect.Responses_Format;
import Utils.StringUtils;

/**
 * Class providing the home view for the house share service
 */
public class Houseshare_HomeView extends NotificationActivity implements Fragment_HS_Notification.OnFragmentInteractionListener_Notification {
    private FragmentManager fragmentManager;

    private String response_content;

    private String house_name;

    private TextView viewName;
    private TextView viewAddressText;
    private TextView viewDescription;
    private ScrollView home_view;

    /* Used for the list view */
    private ArrayList<String> testData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_houseshare__home_view);

        house_name = i.getExtras().getString("HOUSE_NAME");

        // find the necessary views
        main_view_container = findViewById(R.id.home_view_main_container);
        viewName = (TextView) findViewById(R.id.viewName);
        viewAddressText = (TextView) findViewById(R.id.viewAddressText);
        viewDescription = (TextView) findViewById(R.id.viewAddress);
        

        /* Get the list view */
        ListView billList = (ListView) findViewById(R.id.listBills);
        testData = new ArrayList<String>();
        testData.add("Bill 1");
        testData.add("Bill 2");
        testData.add("Bill 3");
        testData.add("Bill 4");
        testData.add("Bill 5");
        testData.add("Bill 6");
        testData.add("Bill 7");
        testData.add("Bill 8");
        testData.add("Bill 9");

        // Create The Adapter with passing ArrayList as 3rd parameter
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, testData);
        // Set The Adapter
        billList.setAdapter(arrayAdapter);

        initialiseData();

//        Fragment_HS_Home fragment_hs_home = Fragment_HS_Home.newInstance("");
//        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.add(R.id.home_view_main_container, fragment_hs_home, "fragment_home_view");
//        //TODO: Investigate this
//        // added the frag to the back stack allowing the user to go back to it by pressing the Back button
////        transaction.addToBackStack("added the home view frag");
//        transaction.commit();
//        Animation.fade_in(this.findViewById(R.id.home_view_main_container), this, Animation.SHORT, Animation.POST_EFFECT.PERMANENTLY);

/*

        isNotiVisible = false;

        checkNewNotification(); // check for new noti on start-up
        if (hasNewNoti && menu != null)
            menu.findItem(R.id.action_hs_noti).setIcon(R.drawable.globe_new);
*/

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    // method called when the fragment is called.
    protected String fetchHomeViewInfo() {
        Connection connect = new Connection(this);
        String result = "DEFAULT INFO";
        connect.setMode(Connection.MODE.LONG_TASK);
        connect.setDialogMessage("Collecting your house details");

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
            } else {
//               TextView tv = (TextView) findViewById(R.id.hs_hv_response);
                jo.getString(Responses_Format.RESPONSE_HS_CONTENT); //TODO
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
        return result;
    }

    public void displayContent() {
        try {
           JSONObject j = new JSONObject(response_content).getJSONObject(Responses_Format.RESPONSE_HS_CONTENT);
            Log.d("jo", j.toString());
           JSONArray house_Array = j.getJSONArray("basic_info");

           viewName.setText(house_Array.getString(0));
           viewAddressText.setText(StringUtils.implode(" ", house_Array.getString(1), house_Array.getString(2), house_Array.getString(3)));
           viewDescription.setText(house_Array.getString(house_Array.length() - 1));

            //TODO Set up the bill
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialise data
     */
    private void initialiseData() {
        response_content = fetchHomeViewInfo();
        displayContent();
    }

}