//package com.ncl.team5.lloydsmockup;
//
//import android.support.v4.app.FragmentActivity;
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Color;
//import android.support.v4.app.FragmentManager;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.TableLayout;
//import android.widget.TableRow;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.text.ParseException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.concurrent.ExecutionException;
//
//import Fragment.Fragment_HS_Notification;
//import HTTPConnect.Connection;
//import HTTPConnect.Notification;
//import HTTPConnect.Request_Params;
//import HTTPConnect.Responses_Format;
//import Utils.Animation;
//
//
///**
// * Created by Thanh on 29-Mar-15.<br/>
// *
// * Abstract activity (inherited from {@link android.support.v4.app.FragmentActivity}) with the notification feature <br/>
// * <u>NOTE</u> Any activity using the notification feature (which most will) should subclass this activity
// */
//public abstract class NotificationActivity extends FragmentActivity implements Fragment_HS_Notification.OnFragmentInteractionListener_Notification {
//    protected Menu menu;
//    protected FragmentManager fragmentManager;
//    protected boolean isNotiVisible;
//    protected boolean hasNewNoti = false;
//    protected String username;
//    protected Intent i;
//
//    // The View Container of the main page
//        protected View main_view_container;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //TODO set content view
//        fragmentManager = getSupportFragmentManager();
//
//        i = getIntent();
//        username = i.getExtras().getString(IntentConstants.USERNAME);
//
//        isNotiVisible = false;
////        checkNewNotification(); // check for new noti on start-up
//        if (hasNewNoti && menu != null)
//            menu.findItem(R.id.action_notifications).setIcon(R.drawable.globe_new);
//
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_houseshare__home_view, menu);
//        this.menu = menu;
//        if (hasNewNoti)
//            menu.findItem(R.id.action_notifications).setIcon(R.drawable.globe_new);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        } else if (id == R.id.action_add_user) {
//            menu.findItem(R.id.action_hs_noti).setIcon(R.drawable.globe);
//            item.setIcon(R.drawable.add_user_clicked);
//
//            Fragment_HS_Notification f = (Fragment_HS_Notification) fragmentManager.findFragmentByTag("fragment_noti");
//            if (f != null && f.isVisible())
//                fragmentManager.beginTransaction().remove(f).commit();
//            isNotiVisible = false;
//            main_view_container.setAlpha(1.0f);
//            main_view_container.setBackgroundColor(Color.WHITE);
//
//            return true;
//        } else if (id == R.id.action_hs_noti) {
//            menu.findItem(R.id.action_add_user).setIcon(R.drawable.add_user);
//            //check if the notification has already been shown or not and act accordingly
//            if (!isNotiVisible) {
//                item.setIcon(R.drawable.globe_clicked);
//                //showing the notification
//                Fragment_HS_Notification fragment_hs_noti = Fragment_HS_Notification.newInstance("", "");
//                android.support.v4.app.FragmentTransaction t = fragmentManager.beginTransaction();
//                //NOTE: We dont add the noti frag to the stack as it would not make sense to get back to the noti frag using back button
////                t.replace(R.id.home_view_main_container, fragment_hs_noti, "fragment_noti");
////                t.add(R.id.noti_main_container, fragment_hs_noti, "fragment_noti");
//                t.commit();
////                Animation.fade_in(this.findViewById(R.id.home_view_main_container), this, Animation.SHORT, Animation.POST_EFFECT.PERMANENTLY);
//                isNotiVisible = true;
//                main_view_container.setAlpha(0.5f);
//                main_view_container.setBackgroundColor(Color.LTGRAY);
//            } else {
//                item.setIcon(R.drawable.globe);
////                Fragment_HS_Home fragment_hs_home = Fragment_HS_Home.newInstance("");
////                android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
////                transaction.replace(R.id.home_view_main_container, fragment_hs_home, "fragment_home_view");
////               //TODO: Investigate this
////                // added the frag to the back stack allowing the user to go back to it by pressing the Back button
//////                transaction.addToBackStack("added the home view frag to stack");
////                transaction.commit();
////                Animation.fade_in(this.findViewById(R.id.home_view_main_container), this, Animation.SHORT, Animation.POST_EFFECT.PERMANENTLY);
//
//                Fragment_HS_Notification f = (Fragment_HS_Notification) fragmentManager.findFragmentByTag("fragment_noti");
//                if (f != null && f.isVisible())
//                    fragmentManager.beginTransaction().remove(f).commit();
//                isNotiVisible = false;
//                main_view_container.setAlpha(1.0f);
//                main_view_container.setBackgroundColor(Color.WHITE);
//            }
//
//
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    /**
//     * Override the onBackPressed to achieve the desired behaviour
//     * (Not sure why addBackStack got some problem when emptying the stack, so I overrode this method directly)
//     */
//    @Override
//    public void onBackPressed() {
////            Toast.makeText(this, Integer.toString(fragmentManager.getBackStackEntryCount()), Toast.LENGTH_SHORT).show();
////        if (isNotiVisible && findViewById(R.id.layout_hs_notification_container) != null) {
////            menu.findItem(R.id.action_hs_noti).setIcon(R.drawable.globe);
////            Fragment_HS_Home fragment_hs_home = Fragment_HS_Home.newInstance("");
////            android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
////            transaction.replace(R.id.home_view_main_container, fragment_hs_home, "fragment_home_view");
////            // added the frag to the back stack allowing the user to go back to it by pressing the Back button
//////                transaction.addToBackStack("added the home view frag to stack");
////            transaction.commit();
////            Animation.fade_in(this.findViewById(R.id.home_view_main_container), this, Animation.SHORT, Animation.POST_EFFECT.PERMANENTLY);
////
////            isNotiVisible = false;
//        Fragment_HS_Notification f = (Fragment_HS_Notification) fragmentManager.findFragmentByTag("fragment_noti");
//        if (f != null && f.isVisible()) {
//            menu.findItem(R.id.action_hs_noti).setIcon(R.drawable.globe);
//            fragmentManager.beginTransaction().remove(f).commit();
//            isNotiVisible = false;
//            main_view_container.setAlpha(1.0f);
//            main_view_container.setBackgroundColor(Color.WHITE);
//        } else {
//            super.onBackPressed();
//        }
//    }
//
//
//
//
////    /* TODO: I'm not really sure when would be the best time to fetch data from the server (In the main activity or on some of the fragment initialising methods?
////    * so I would go with the onCreate method in the fragment because placing it as close the UI creation of the home view as possible might prove useful in some cases
////    */
////     /**
////     * method called when the home view fragment is created (fetch data from server) <br/>
////     * NOTE: there are various solutions to this.
////     *
////     * @param f the fragment
////     */
////    @Override
////    public String onHomeViewCreated(Fragment_HS_Home f) {
////        return processInfo();
////    }
//
//
//}
//
//
//
//
//
//
//
//
