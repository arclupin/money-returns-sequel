package com.ncl.team5.lloydsmockup;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.zip.Inflater;

import Fragment.Fragment_HS_Home;
import Fragment.Fragment_HS_Notification;
import HTTPConnect.Connection;
import HTTPConnect.Notification;
import HTTPConnect.Request_Params;
import HTTPConnect.Responses_Format;
import Utils.Animation;
import Utils.StringUtils;

/**
 * Class providing the home view for the house share service
 */
public class Houseshare_HomeView extends NotificationActivity implements Fragment_HS_Notification.OnFragmentInteractionListener_Notification {
    private FragmentManager fragmentManager;
    private String response;
    private TextView tv;
    private String house_name;

    private ScrollView home_view;

    /* Used for the list view */
    private ArrayList<String> testData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_houseshare__home_view);

        house_name = i.getExtras().getString("HOUSE_NAME");
        main_view_container = findViewById(R.id.home_view_main_container);
        

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


/*
        Intent i = this.getIntent();
        username = i.getExtras().getString("ACCOUNT_USERNAME");
        house_name = i.getExtras().getString("HOUSE_NAME");
        response = fetchHomeViewInfo();

        home_view = (ScrollView) findViewById(R.id.home_view_main_container);
        tv = (TextView) findViewById(R.id.home_view_text_view);
        tv.setText(response);

        ActionBar a = getActionBar();
        if (a != null)
            a.setTitle(StringUtils.isFieldEmpty(house_name) ? "My house" : house_name);

        fragmentManager = getSupportFragmentManager();*/


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

}