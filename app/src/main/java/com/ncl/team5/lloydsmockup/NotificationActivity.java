package com.ncl.team5.lloydsmockup;

import android.support.v4.app.FragmentActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import Fragment.Fragment_HS_Notification;
import HTTPConnect.Connection;
import HTTPConnect.Notification;
import HTTPConnect.Request_Params;
import HTTPConnect.Responses_Format;
import Utils.Animation;


/**
 * Created by Thanh on 29-Mar-15.<br/>
 *
 * Abstract activity (inherited from {@link android.support.v4.app.FragmentActivity}) with the notification feature <br/>
 * <u>NOTE</u> Any activity using the notification feature (which most will) should subclass this activity
 */
public abstract class NotificationActivity extends FragmentActivity implements Fragment_HS_Notification.OnFragmentInteractionListener_Notification {
    protected Menu menu;
    protected FragmentManager fragmentManager;
    protected boolean isNotiVisible;
    protected boolean hasNewNoti = false;
    protected String username;
    protected Intent i;

    // The View Container of the main page
        protected View main_view_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO set content view
        fragmentManager = getSupportFragmentManager();

        i = getIntent();
        username = i.getExtras().getString("ACCOUNT_USERNAME");

        isNotiVisible = false;
        checkNewNotification(); // check for new noti on start-up
        if (hasNewNoti && menu != null)
            menu.findItem(R.id.action_hs_noti).setIcon(R.drawable.globe_new);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_houseshare__home_view, menu);
        this.menu = menu;
        if (hasNewNoti)
            menu.findItem(R.id.action_hs_noti).setIcon(R.drawable.globe_new);
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
        } else if (id == R.id.action_add_user) {
            menu.findItem(R.id.action_hs_noti).setIcon(R.drawable.globe);
            item.setIcon(R.drawable.add_user_clicked);

            Fragment_HS_Notification f = (Fragment_HS_Notification) fragmentManager.findFragmentByTag("fragment_noti");
            if (f != null && f.isVisible())
                fragmentManager.beginTransaction().remove(f).commit();
            isNotiVisible = false;
            main_view_container.setAlpha(1.0f);
            main_view_container.setBackgroundColor(Color.WHITE);

            return true;
        } else if (id == R.id.action_hs_noti) {
            menu.findItem(R.id.action_add_user).setIcon(R.drawable.add_user);
            //check if the notification has already been shown or not and act accordingly
            if (!isNotiVisible) {
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
                main_view_container.setAlpha(0.5f);
                main_view_container.setBackgroundColor(Color.LTGRAY);
            } else {
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
                main_view_container.setAlpha(1.0f);
                main_view_container.setBackgroundColor(Color.WHITE);
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
        if (f != null && f.isVisible()) {
            menu.findItem(R.id.action_hs_noti).setIcon(R.drawable.globe);
            fragmentManager.beginTransaction().remove(f).commit();
            isNotiVisible = false;
            main_view_container.setAlpha(1.0f);
            main_view_container.setBackgroundColor(Color.WHITE);
        } else {
            super.onBackPressed();
        }
    }


    // method called when the fragment is called.
    protected String fetchHomeViewInfo() {
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
    public List<Notification> onNotificationViewSelected(Fragment_HS_Notification f) throws ParseException {
        List<Notification> l = fetchNotifications();
//        if ((Fragment_HS_Notification.lastNoti != null && l.get(0).getTimeOfNotification().compareTo(Fragment_HS_Notification.lastNoti) > 0) || Fragment_HS_Notification.isThereNewNoti(l) )
//        {
//            Fragment_HS_Notification.newNoti = true; // atm I think it's unnecessary
//        }
        if (!l.isEmpty())
            Fragment_HS_Notification.lastNoti = l.get(0).getTimeOfNotification();
        // update the lastNoti (this is needed as the app will send this info to the server in order to
        // check for new notifications (see the method checkNewNotification below)

        return l;
    }

    /**
     * this method just check if there is any new notification, it does not fetch any notification
     * this is needed on activity start-up.
     * <p/>
     * <i>when is there a new notification?</i> <br/>
     * <p/>
     * there is at least 1 noti in the db server holding NEW (see the Notification class)
     * ... To be added
     */
    public void checkNewNotification() {
        //TODO complete
        //Just need to send a simple request asking for the arrival of any new noti, the server will take care of the search
        Connection connect = new Connection(this);
        String result;

        try {
            /* Command required to make a payment, takes username, to account, from account, both sort codes and amount
             * Returns: JSON String */
            result = connect.execute(Request_Params.PARAM_TYPE, Request_Params.VAL_REF_NOTI, Request_Params.PARAM_USR, this.username).get();
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
            } else if (jo.getString("status").equals("true")) {

                hasNewNoti = true;
            } else if (jo.getString("status").equals("false")) {
                hasNewNoti = false;
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


    /**
     * This method will be invoked when and only when the user clicks on the noti icon
     *
     * @return
     */
    public List<Notification> fetchNotifications() {
        //TODO complete

//        // testing data
//        Notification n = new Notification(Notification.JOIN_ADM_VIEW);
//
//        n.addParam("Danh");
//        n.addParam("danh_nt_1");
//        n.addParam("2015-03-26 17:14:06");
//
//        Notification n1 = new Notification(Notification.JOIN_ADM_VIEW, true);
//        n1.addParam("Ben");
//        n1.addParam("ben_nt_1");
//        n1.addParam("2015-03-25 17:11:09");

        List<Notification> l = new ArrayList<Notification>();
//        l.add(n);
//        l.add(n1);
//        l.add(n);
        Connection connect = new Connection(this);
        String result;

        try {
            /* Command required to make a payment, takes username, to account, from account, both sort codes and amount
             * Returns: JSON String */
            result = connect.execute(Request_Params.PARAM_TYPE, Request_Params.VAL_FETCH_NOTI, Request_Params.PARAM_USR, this.username).get();
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
            } else if (jo.getString("status").equals("true")) {
                JSONArray Noti_js_arr = jo.getJSONArray(Responses_Format.RESPONSE_HS_CONTENT);
                for (int i = 0; i < Noti_js_arr.length(); i++) {
                    JSONArray noti_arr_in = Noti_js_arr.getJSONArray(i);

                    String id = noti_arr_in.getString(0);
                    boolean read = noti_arr_in.getString(noti_arr_in.length() - 1).equals("1");
                    int type = noti_arr_in.getInt(1);

                    Notification n = new Notification(type, read, id);
                    n.addParam(noti_arr_in.getString(Notification.HSID_POS + 2))
                            .addParam(noti_arr_in.getString(Notification.PARAM_POS + 2))
                            .addParam(noti_arr_in.getString(Notification.TIME_POS + 2));

                    l.add(n);
                }
            }
            /* There was an error indide the status return field, display appropriate error message */
            //TODO implement error messages
            else {
                /* give more info on the error here, no money taken from account */
                /* Use the status results to display certain error messages */
            }

        }
        /* Catch the exceptions */ catch (JSONException jse) {
            /* Error in the JSON response */
            new CustomMessageBox(this, "There was an error in the server response");
            Log.d("jse", jse.getMessage(), jse);
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

        return l;
    }

    public void onNotificationsSeen(Fragment_HS_Notification f) {
        Connection connect = new Connection(this);
        String result;

        List<String> request = new ArrayList<String>();
        request.add(Request_Params.PARAM_TYPE);
        request.add(Request_Params.MARK_NOTI_AS_SEEN_NOT_READ);
        request.add(Request_Params.PARAM_USR);
        request.add(username);

        for (int i = 0; i < Fragment_HS_Notification.data.size(); i++) {
            request.add(Request_Params.MARK_NOTI_AS_SEEN_NOT_READ_PARAM);
            request.add(Fragment_HS_Notification.data.get(i).getId());
        }
        Log.d("Request mark as seen", Arrays.toString(request.toArray(new String[request.size()])));

        try {

            /* Command required to make a payment, takes username, to account, from account, both sort codes and amount
             * Returns: JSON String */
            result = connect.execute(request.toArray(new String[request.size()])).get();
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
        }

//            else if (jo.getString("status").equals("true")) {
//                JSONArray Noti_js_arr = jo.getJSONArray(Responses_Format.RESPONSE_HS_CONTENT);
//                for (int i = 0; i < Noti_js_arr.length(); i++) {
//                    JSONArray noti_arr_in = Noti_js_arr.getJSONArray(i);
//
//                    String id = noti_arr_in.getString(0);
//                    boolean read = noti_arr_in.getString(noti_arr_in.length() - 1).equals("1");
//                    int type = noti_arr_in.getInt(1);
//
//                    Notification n = new Notification(type, read, id);
//                    n.addParam(noti_arr_in.getString(Notification.HSID_POS + 2))
//                            .addParam(noti_arr_in.getString(Notification.PARAM_POS + 2))
//                            .addParam(noti_arr_in.getString(Notification.TIME_POS + 2));
//
//                    l.add(n);
//                }
//            }
//            /* There was an error indide the status return field, display appropriate error message */
//            //TODO implement error messages
//            else {
//                /* give more info on the error here, no money taken from account */
//                /* Use the status results to display certain error messages */
//            }
//
//        }
        /* Catch the exceptions */
        catch (Exception e) {
            Log.d("jse", e.getMessage(), e);
        }

    }

    @Override
    public void onWelcomeButtonClicked(Fragment_HS_Notification f, String p, String noti_id) {
        //TODO Waiting for the server to be configured for this request
        Connection connect = new Connection(this);
        String result;

        try {
            /* Command required to make a payment, takes username, to account, from account, both sort codes and amount
             * Returns: JSON String */
            result = connect.execute(Request_Params.PARAM_TYPE, Request_Params.VAL_APPROVE_MEMBER, Request_Params.PARAM_USR, username,
                    Request_Params.VAL_APPROVE_MEMBER_PARAM, p,
                    "NOTI_ID", noti_id).get();
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

            else if (jo.getString("status").equals("true")) {
                new CustomMessageBox(this, "You have approved " + p.substring(0, p.length() - 2) + "!\nLet's welcome them!");
            }

            /* There was an error indide the status return field, display appropriate error message */
            //TODO implement error messages
            else {
                /* give more info on the error here, no money taken from account */
                /* Use the status results to display certain error messages */
            } }
        /* Catch the exceptions */ catch (JSONException jse) {
            /* Error in the JSON response */
            new CustomMessageBox(this, "There was an error in the server response");
            Log.d("jse", jse.getMessage(), jse);
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

    @Override
    public void onRefuseButtonClicked(Fragment_HS_Notification f, String p, String noti_id) {
        //TODO Waiting for the server to be configured for this request
        Connection connect = new Connection(this);
        String result;

        try {
            /* Command required to make a payment, takes username, to account, from account, both sort codes and amount
             * Returns: JSON String */
            result = connect.execute(Request_Params.PARAM_TYPE, Request_Params.VAL_REFUSE_MEMBER, Request_Params.PARAM_USR, this.username,
                    Request_Params.VAL_REFUSE_MEMBER_PARAM, p,
                    "NOTI_ID", noti_id).get();
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

            else if (jo.getString("status").equals("true")) {
                new CustomMessageBox(this, "You have refused the request of " + p.substring(0, p.length() - 2) + "!");
            }

            /* There was an error indide the status return field, display appropriate error message */
            //TODO implement error messages
            else {
                new CustomMessageBox(this, "Sorry. We coult not process your request at the moment \nIf you are experiencing this error constantly, please contact our team.");
                /* give more info on the error here, no money taken from account */
                /* Use the status results to display certain error messages */
            } }
        /* Catch the exceptions */ catch (JSONException jse) {
            /* Error in the JSON response */
            new CustomMessageBox(this, "There was an error in the server response");
            Log.d("jse", jse.getMessage(), jse);
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



    @Override
    public void checkEmptyNotification(Fragment_HS_Notification f) {
        if (isNotiVisible && f.isVisible()) {
            TableLayout l = (TableLayout) findViewById(R.id.layout_hs_notification_container);
            if (l.getChildCount() == 0) {
                LayoutInflater i = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                TableRow v = (TableRow) i.inflate(R.layout.hs_noti_empty, l, false);
                l.addView(v);
            }
        }
    }
}








