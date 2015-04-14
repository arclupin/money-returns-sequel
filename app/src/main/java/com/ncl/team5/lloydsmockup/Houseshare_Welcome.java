package com.ncl.team5.lloydsmockup;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import Fragments.HS_Welcome_FragmentPagerAdapter;
import HTTPConnect.Connection;
import HTTPConnect.Request_Params;
import HTTPConnect.Responses_Format;
import Utils.Houseshares;


public class Houseshare_Welcome extends FragmentActivity {
    ViewPager pager;
    HS_Welcome_FragmentPagerAdapter pager_adapter;
    List<Integer> swipe_indicators = new ArrayList<Integer>();
    int currentItem = 0;
    private static String username;
    private static String hsid;

    // STATIC var keeps track of the registration status
    // this prevents the app from sending the registration request again after already making a registration.
    public static boolean registered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_houseshare_welcome);

        Intent intent = getIntent();
        username = intent.getExtras().getString(IntentConstants.USERNAME);
        // hide the action bar at welcome page
        ActionBar actionBar = getActionBar();
        if (actionBar != null) actionBar.hide();

        pager = (ViewPager) findViewById(R.id.HS_Welcome_Slider);
        pager_adapter = new HS_Welcome_FragmentPagerAdapter(getSupportFragmentManager());
        TextView skip_text_view = (TextView) findViewById(R.id.HS_Welcome_Skip);

        // get the ids of all swipe indicators (circle things at the bottom)
        LinearLayout swipe_indicators_container = (LinearLayout) findViewById(R.id.HS_Welcome_swipe_indicators);
        for (int i = 0; i < pager_adapter.getCount(); i++) {
            this.swipe_indicators.add(swipe_indicators_container.getChildAt(i).getId());
        }

        // some simple animation for the SKIP button
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_fade_in);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setStartTime(System.currentTimeMillis());
        skip_text_view.startAnimation(animation);
        skip_text_view.setClickable(true);
        skip_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(HS_Welcome_FragmentPagerAdapter.FRAGMENT_VIEWS - 1);
            }
        });


        final Houseshare_Welcome temp_this = this; // temp var for the activity
        // I dont know the method to override in order to get the position before the switch (something like onBeforeChange or something)}
        // so I use a variable to hold this position value.
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // reset the circles color corresponding to pages involved in the switch
                findViewById(swipe_indicators.get(currentItem)).setBackgroundResource(R.drawable.swipe_circle_unfocused);
                findViewById(swipe_indicators.get(position)).setBackgroundResource(R.drawable.swipe_circle_focused);
                currentItem = position;
                if (position < pager_adapter.getCount() - 1)
                    findViewById(R.id.HS_Welcome_Start).setVisibility(View.GONE);
                else {findViewById(R.id.HS_Welcome_Start).setVisibility(View.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(temp_this, R.anim.anim_fade_in);
                    animation.setInterpolator(new AccelerateInterpolator());
                    animation.setStartTime(System.currentTimeMillis());
                    findViewById(R.id.HS_Welcome_Start).startAnimation(animation);
//                    Source: Android SDK
                }
            }}
);}
               @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_houseshare__welcome, menu);
        pager.setAdapter(pager_adapter);

        /* TODO/ this is not really the best way to do it but as I want to make use of the getHeight method
          TODO/   in the onCreateView method of the Fragment Classes which would be not yet available if I set the adapter within the onCreate method of this activity
          TODO/   (Because all views are not yet drawn as long as the app is still inside onCreate)
          TODO/    could use treeObserve as a better way I guess */
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

        return super.onOptionsItemSelected(item);
    }

    public void start_bttn(View v) {
        Log.d("registration welcome", Boolean.toString(registered));

        // check the registration status
        if (this.username.equals("test")) {
            Intent i = new Intent(this, Houseshare_HomeView.class);
            i.putExtra(IntentConstants.USERNAME, username);
            i.putExtra(IntentConstants.HOUSE_NAME, "My House Test");
            startActivity(i);
            ((KillApp) this.getApplication()).setStatus(false);
        }
        else if (registered){
            Houseshares.hs_intents_home_view(this, Houseshare_HomeView.class, "", username, hsid, Responses_Format.RESPONSE_HOUSESHARE_JOINED_SERVICE);
        }
    else register();
    }

    public void register() {
            Connection connect = new Connection(this);
            String result;

            try {
            /* Command required to make a payment, takes username, to account, from account, both sort codes and amount
             * Returns: JSON String */
                result = connect.execute(Request_Params.PARAM_TYPE, Request_Params.VAL_HS_REGISTER, Request_Params.PARAM_USR, this.username).get();
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
                    hsid = jo.getString(Responses_Format.RESPONSE_HOUSESHARE_ID);
                    Houseshares.hs_intents_home_view(this, Houseshare_HomeView.class, "", username, hsid, Responses_Format.RESPONSE_HOUSESHARE_JOINED_SERVICE);
                }
                else {
                    new CustomMessageBox(this, "We are sorry. We could not perform your registration at the moment. Try again later.");
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

