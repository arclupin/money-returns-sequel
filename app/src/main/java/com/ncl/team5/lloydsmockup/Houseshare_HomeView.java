package com.ncl.team5.lloydsmockup;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
public class Houseshare_HomeView extends FragmentActivity implements Fragment_HS_Notification.OnFragmentInteractionListener_Notification {
    private String username;
    private String house_name;
    private int current_menu_item = 0;
    private Menu menu;
    private FragmentManager fragmentManager;
    private String response;
    private boolean isNotiVisible;
    private TextView tv;

    private ScrollView home_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_houseshare__home_view);

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

        fragmentManager = getSupportFragmentManager();

//        Fragment_HS_Home fragment_hs_home = Fragment_HS_Home.newInstance("");
//        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.add(R.id.home_view_main_container, fragment_hs_home, "fragment_home_view");
//        //TODO: Investigate this
//        // added the frag to the back stack allowing the user to go back to it by pressing the Back button
////        transaction.addToBackStack("added the home view frag");
//        transaction.commit();
//        Animation.fade_in(this.findViewById(R.id.home_view_main_container), this, Animation.SHORT, Animation.POST_EFFECT.PERMANENTLY);


        isNotiVisible = false;


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
            current_menu_item = 0;
            return true;
        }

        else if (id == R.id.action_add_user) {
            menu.findItem(R.id.action_hs_noti).setIcon(R.drawable.globe);
            item.setIcon(R.drawable.add_user_clicked);

            return true;
        }

        else if (id == R.id.action_hs_noti) {
            menu.findItem(R.id.action_add_user).setIcon(R.drawable.add_user);
            //check if the notification has already been shown or not and act accordingly
            if (!isNotiVisible ) {


                item.setIcon(R.drawable.globe_clicked);
                //showing the notification
                Fragment_HS_Notification fragment_hs_noti = Fragment_HS_Notification.newInstance("");
                android.support.v4.app.FragmentTransaction t = fragmentManager.beginTransaction();
                //NOTE: We dont add the noti frag to the stack as it would not make sense to get back to the noti frag using back button
//                t.replace(R.id.home_view_main_container, fragment_hs_noti, "fragment_noti");
                t.add(R.id.noti_main_container, fragment_hs_noti, "fragment_noti");
                t.commit();
//                Animation.fade_in(this.findViewById(R.id.home_view_main_container), this, Animation.SHORT, Animation.POST_EFFECT.PERMANENTLY);
               Animation.fade_in(this.findViewById(R.id.noti_main_container), this, Animation.SHORT, Animation.POST_EFFECT.PERMANENTLY);
                isNotiVisible = true;
                home_view.setAlpha(0.5f);
                home_view.setBackgroundColor(Color.LTGRAY);
            }
            else {
                item.setIcon(R.drawable.globe);
//                Fragment_HS_Home fragment_hs_home = Fragment_HS_Home.newInstance("");
//                android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
//                transaction.replace(R.id.home_view_main_container, fragment_hs_home, "fragment_home_view");
//               //TODO: Investigate this
//                // added the frag to the back stack allowing the user to go back to it by pressing the Back button
////                transaction.addToBackStack("added the home view frag to stack");
//                transaction.commit();
//                Animation.fade_in(this.findViewById(R.id.home_view_main_container), this, Animation.SHORT, Animation.POST_EFFECT.PERMANENTLY);

                Fragment_HS_Notification f = (Fragment_HS_Notification) fragmentManager.findFragmentByTag("fragment_noti");
                if (f != null && f.isVisible())
                    fragmentManager.beginTransaction().remove(f).commit();
                isNotiVisible = false;
                home_view.setAlpha(1.0f);
                home_view.setBackgroundColor(Color.WHITE);
            }


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Override the onBackPressed to achieve the desired behaviour
     * (Not sure why addBackStack got some problem when emptying the stack, so I overrode this method directly)
     */
    @Override
    public void onBackPressed() {
//            Toast.makeText(this, Integer.toString(fragmentManager.getBackStackEntryCount()), Toast.LENGTH_SHORT).show();
//        if (isNotiVisible && findViewById(R.id.layout_hs_notification_container) != null) {
//            menu.findItem(R.id.action_hs_noti).setIcon(R.drawable.globe);
//            Fragment_HS_Home fragment_hs_home = Fragment_HS_Home.newInstance("");
//            android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
//            transaction.replace(R.id.home_view_main_container, fragment_hs_home, "fragment_home_view");
//            // added the frag to the back stack allowing the user to go back to it by pressing the Back button
////                transaction.addToBackStack("added the home view frag to stack");
//            transaction.commit();
//            Animation.fade_in(this.findViewById(R.id.home_view_main_container), this, Animation.SHORT, Animation.POST_EFFECT.PERMANENTLY);
//
//            isNotiVisible = false;
        Fragment_HS_Notification f = (Fragment_HS_Notification) fragmentManager.findFragmentByTag("fragment_noti");
        if (f != null && f.isVisible()){
            menu.findItem(R.id.action_hs_noti).setIcon(R.drawable.globe);
            fragmentManager.beginTransaction().remove(f).commit();
            isNotiVisible = false;
            home_view.setAlpha(1.0f);
            home_view.setBackgroundColor(Color.WHITE);
        }
        else {
            super.onBackPressed();
        }
    }



    // method called when the fragment is called.
    private String fetchHomeViewInfo() {
        Connection connect = new Connection(this);
        String result = "DEFAULT INFO";

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
                result = jo.getString(Responses_Format.RESPONSE_HS_CONTENT).toString(); //TODO
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

//    /* TODO: I'm not really sure when would be the best time to fetch data from the server (In the main activity or on some of the fragment initialising methods?
//    * so I would go with the onCreate method in the fragment because placing it as close the UI creation of the home view as possible might prove useful in some cases
//    */
//     /**
//     * method called when the home view fragment is created (fetch data from server) <br/>
//     * NOTE: there are various solutions to this.
//     *
//     * @param f the fragment
//     */
//    @Override
//    public String onHomeViewCreated(Fragment_HS_Home f) {
//        return fetchHomeViewInfo();
//    }

    /**
     * method called when the notification fragment is created (fetch data from server) <br/>
     * NOTE: there are various solutions to this.
     *
     * @param f the fragment
     */
    @Override
    public List<Notification> onNotificationViewSelected(Fragment_HS_Notification f) {

        // testing data
        Notification n = new Notification(Notification.JOIN_ADM_VIEW);
        n.addParam("Danh");
        List<Notification> l = new ArrayList<Notification>();
        l.add(n);
        l.add(n);
        l.add(n);

        return l;
        }

    }

