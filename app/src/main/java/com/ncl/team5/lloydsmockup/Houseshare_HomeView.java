package com.ncl.team5.lloydsmockup;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ScrollView;
import android.widget.TextView;

import Utils.StringUtils;

/**
 * Class providing the home view for the house share service
 */
public class Houseshare_HomeView extends NotificationActivity {

    private String house_name;
    private String response;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_houseshare__home_view);

        //specific to home view
        house_name = i.getExtras().getString("HOUSE_NAME");
        response = fetchHomeViewInfo();
        main_view_container = (ScrollView) findViewById(R.id.home_view_main_container);
        tv = (TextView) findViewById(R.id.home_view_text_view);
        tv.setText(response);
        ActionBar a = getActionBar();
        if (a != null)
            a.setTitle(StringUtils.isFieldEmpty(house_name) ? "My house" : house_name);


//        Fragment_HS_Home fragment_hs_home = Fragment_HS_Home.newInstance("");
//        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.add(R.id.home_view_main_container, fragment_hs_home, "fragment_home_view");
//        //TODO: Investigate this
//        // added the frag to the back stack allowing the user to go back to it by pressing the Back button
////        transaction.addToBackStack("added the home view frag");
//        transaction.commit();
//        Animation.fade_in(this.findViewById(R.id.home_view_main_container), this, Animation.SHORT, Animation.POST_EFFECT.PERMANENTLY);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       return super.onCreateOptionsMenu(menu);
    }

}







