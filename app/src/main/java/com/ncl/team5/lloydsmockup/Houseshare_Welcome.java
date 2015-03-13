package com.ncl.team5.lloydsmockup;

import android.app.ActionBar;
import android.app.Activity;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import FragPager.HS_Welcome_FragmentPagerAdapter;


public class Houseshare_Welcome extends FragmentActivity {
    ViewPager pager;
    HS_Welcome_FragmentPagerAdapter pager_adapter;
    List<Integer> swipe_indicators = new ArrayList<Integer>();
    int currentItem = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_houseshare__welcome);
        // hide the action bar at welcome page
        ActionBar actionBar = getActionBar();
        if (actionBar != null) actionBar.hide();



        pager = (ViewPager) findViewById(R.id.HS_Welcome_Slider);
        pager_adapter = new HS_Welcome_FragmentPagerAdapter(getSupportFragmentManager());

        // get the ids of all swipe indicators (circle things at the bottom)
        LinearLayout swipe_indicators_container = (LinearLayout) findViewById(R.id.HS_Welcome_swipe_indicators);
        for (int i = 0; i < pager_adapter.getCount(); i++) {
            this.swipe_indicators.add(swipe_indicators_container.getChildAt(i).getId());
        }
        final Houseshare_Welcome temp_this = this;
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
                    Animation animation = AnimationUtils.loadAnimation(temp_this, R.anim.hs_welcome_start_fade);
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
}
