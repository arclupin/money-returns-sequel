package com.ncl.team5.lloydsmockup;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.LinearLayout;

import Fragments.Fragment_HS_Abstract;
import Fragments.Fragment_HS_Home;
import Fragments.Fragment_HS_Notification;
import Fragments.HS_Home_FragPagerAdapter;
import Fragments.HS_New_Bill_Dialog;
import HTTPConnect.Connection;
import HTTPConnect.Responses_Format;

/**
 * Class providing the home view for the house share service
 */
public class Houseshare_HomeView extends FragmentActivity implements Fragment_HS_Home.OnFragmentInteractionListener_HomeView,
        ActionBar.TabListener, Fragment_HS_Notification.OnNotificationInteraction, HS_New_Bill_Dialog.NewBillDialogListener {
    private FragmentManager fragmentManager;

    private String house_name;
    private String username;
    private String hsid;
    private static String view_type; // joined house or sent request?

    private Intent i;
    private static Menu menu;
    private HS_Home_FragPagerAdapter mAdapter;
    private ViewPager pager;
    private  ActionBar actionBar;
    private LinearLayout l;

    private static final int HOME_VIEW_TAB_POSITION = 0;
    private static final int NOTIFICATION_TAB = 1;

    private Fragment_HS_Notification noti_listener;


    @Override
    public void onCreate(Bundle savedInstanceState)         {
            super.onCreate(savedInstanceState);
            if (savedInstanceState != null) {
                username = savedInstanceState.getString(IntentConstants.USERNAME);
                house_name = savedInstanceState.getString(IntentConstants.HOUSE_NAME);
                view_type = savedInstanceState.getString(IntentConstants.HOME_VIEW_TYPE);
                hsid = savedInstanceState.getString(IntentConstants.HOUSESHARE_ID);
            }

        requestWindowFeature(Window.FEATURE_ACTION_BAR);
            setContentView(R.layout.activity_houseshare__home_view);
                actionBar = getActionBar();
                Log.d("action Bar", actionBar.toString());
                actionBar.setDisplayShowHomeEnabled(false);
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
                actionBar.setSplitBackgroundDrawable(new ColorDrawable((getResources().getColor(R.color.dark_green))));

            i = getIntent();
            if (i != null) {
                if (i.getStringExtra(IntentConstants.USERNAME) != null)
                    username = i.getStringExtra(IntentConstants.USERNAME);
                if (i != null && i.getStringExtra(IntentConstants.HOUSE_NAME) != null)
                    house_name = i.getStringExtra(IntentConstants.HOUSE_NAME);
                if (i != null && i.getStringExtra(IntentConstants.HOME_VIEW_TYPE) != null)
                    view_type = i.getStringExtra(IntentConstants.HOME_VIEW_TYPE);
                hsid = i.getStringExtra(IntentConstants.HOUSESHARE_ID);
            }

        Log.d("In state home view", username + " - " + house_name + " - " + view_type);


            fragmentManager = getSupportFragmentManager();
            mAdapter = new HS_Home_FragPagerAdapter(fragmentManager, username, hsid, house_name, view_type);
            pager = (ViewPager) findViewById(R.id.home_view_pager);
            pager.setAdapter(mAdapter);
            pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    // When swiping between different app sections, select the corresponding tab.
                    // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                    // Tab.
                    actionBar.setSelectedNavigationItem(position);
                    if (position == 1) {
                        invalidateOptionsMenu();
                        getMenuInflater().inflate(R.menu.menu_hs_noti, menu);
                    } else {
                        invalidateOptionsMenu();
                        Houseshare_HomeView.this.onCreateOptionsMenu(menu);
                    }

                    Log.d("pager", Integer.toString(position));

                    Fragment_HS_Abstract f = (Fragment_HS_Abstract) fragmentManager.getFragments().get(position);
                    Log.d("new frag", f.toString());
                    f.update();
                }
            });




            for (int i = 0; i < mAdapter.getCount(); i++) {
                // Create a tab with text corresponding to the page title defined by the adapter.
                // Also specify this Activity object, which implements the TabListener interface, as the
                // listener for when this tab is selected.
                actionBar.addTab(
                        actionBar.newTab()
                                .setTabListener(this).setIcon(i == 0 ? R.drawable.hs_tab_home : R.drawable.hs_tab_noti));

            }

        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("view_type onMenu", "view type: " + view_type);

        if (view_type.equals(Responses_Format.RESPONSE_HOUSESHARE_JOINED_HOUSE))
             getMenuInflater().inflate(R.menu.menu_houseshare__home_view, menu);
        else //TODO potential bug here
             getMenuInflater().inflate(R.menu.menu_homeview_search, menu);
        Houseshare_HomeView.menu = menu;
        return true;
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("home view resume", "tab " + pager.getCurrentItem());
        if (fragmentManager.getFragments() != null) {
            ((Fragment_HS_Abstract) fragmentManager.getFragments().get(pager.getCurrentItem())).update();
            Log.d("new frag resume",  fragmentManager.getFragments().get(pager.getCurrentItem()).toString());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(IntentConstants.USERNAME, username);
        outState.putString(IntentConstants.HOUSE_NAME, house_name);
        outState.putString(IntentConstants.HOME_VIEW_TYPE, view_type);
        Log.d("Out state home view", username + " - " + house_name + " - " + view_type);
    }

    /**
     * Prepare the Screen's standard options menu to be displayed.  This is
     * called right before the menu is shown, every time it is shown.  You can
     * use this method to efficiently enable/disable items or otherwise
     * dynamically modify the contents.
     * <p/>
     * <p>The default implementation updates the system menu items based on the
     * activity's state.  Deriving classes should always call through to the
     * base class implementation.
     *
     * @param menu The options menu as last shown or first initialized by
     *             onCreateOptionsMenu().
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (pager.getCurrentItem() == 1) {
            menu.removeItem(R.id.action_add_user);
            menu.removeItem(R.id.action_hs_new_bill);
            getMenuInflater().inflate(R.menu.menu_hs_noti, menu);
        }
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

       switch (id){
           case R.id.action_hs_new_bill: {
              HS_New_Bill_Dialog dialog = new HS_New_Bill_Dialog();
               dialog.show(getFragmentManager(), "billDialogFrag");
               break;
           }
           case R.id.search: {
               Log.d("display_search item", "clicked");
               Intent i = new Intent(this, Houseshare_Search.class);
               i.putExtra(IntentConstants.USERNAME, username);
               i.putExtra(IntentConstants.HOUSE_NAME, house_name);
               startActivity(i);
               break;
           }
           case R.id.action_refresh: {
               Fragment_HS_Notification f = (Fragment_HS_Notification) fragmentManager.getFragments().get(1);
               if (f.isVisible())
               {
                   f.getmRefreshView().setRefreshing(true);
                   f.refresh();
               }
               break;
           }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * method called when the home view fragment is created (fetch data from server) <br/>
     * NOTE: there are various solutions to this.
     *
     * @param f the fragment
     */
    @Override
    public void onHomeViewCreated(Fragment_HS_Home f) {

    }

    /**
     * Called when a tab enters the selected state.
     *
     * @param tab The tab that was selected
     * @param ft  A {@link android.app.FragmentTransaction} for queuing fragment operations to execute
     *            during a tab switch. The previous tab's unselect and this tab's select will be
     *            executed in a single transaction. This FragmentTransaction does not support
     */
    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
        pager.setCurrentItem(tab.getPosition());
    }

    /**
     * Called when a tab exits the selected state.
     *
     * @param tab The tab that was unselected
     * @param ft  A {@link android.app.FragmentTransaction} for queuing fragment operations to execute
     *            during a tab switch. This tab's unselect and the newly selected tab's select
     *            will be executed in a single transaction. This FragmentTransaction does not
     */
    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
        if (noti_listener == null)
            noti_listener = (Fragment_HS_Notification) fragmentManager.getFragments().get(1);
        Log.d("Noti_listener", "// " + noti_listener.toString());
        if (tab.getPosition() == 1) {
                // let the server know that the notifications have been seen
                new Connection(this).execute(noti_listener.onNotificationsSeen((Fragment_HS_Notification)fragmentManager.getFragments().get(1)));
                tab.setIcon(R.drawable.hs_tab_noti);

            }

    }

    /**
     * Called when a tab that is already selected is chosen again by the user.
     * Some applications may use this action to return to the top level of a category.
     *
     * @param tab The tab that was reselected.
     * @param ft  A {@link android.app.FragmentTransaction} for queuing fragment operations to execute
     *            once this method returns. This FragmentTransaction does not support
     */
    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
        if (tab.getPosition() == 1) {

            tab.setIcon(R.drawable.hs_tab_noti);

        }
    }

    @Override
    public void onNewNotiReceived() {
        actionBar.getTabAt(NOTIFICATION_TAB).setIcon(R.drawable.hs_tab_noti_new);
    }

    @Override
    public void onBackPressed() {
        Log.d("back pressed", "Parent of home view is: " + NavUtils.getParentActivityName(this));
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(MainActivity.RESUME_FROM_INSIDE, true);
        NavUtils.navigateUpTo(this, i);
    }

    /**
     * User clicks on 1 option to click a new bill
     *
     * @param f the fragment itself
     * @param bill_type the type of the dialog
     */
    @Override
    public void onNewBillOptionClicked(HS_New_Bill_Dialog f, HS_New_Bill_Dialog.BILL_TYPE bill_type) {
       Log.d("new bill clicked", bill_type.name());
        f.dismiss();
        Intent i = new Intent(this, bill_type == HS_New_Bill_Dialog.BILL_TYPE.AUTO ? NewBillAuto.class : NewBillManual.class);
        i.putExtra(IntentConstants.USERNAME, username);
        i.putExtra(IntentConstants.HOUSE_NAME, house_name);
        i.putExtra(IntentConstants.HOUSESHARE_ID, hsid);
        startActivity(i);

    }

//    /**
//     * method called when the notification fragment is created (fetch data from server) <br/>
//     * NOTE: there are various solutions to this.
//     *
//     * @param f the fragment
//     */
//    @Override
//    public List<Notification> onNotificationViewSelected(Fragment_HS_Notification f) throws ParseException {
//        List<Notification> l = fetchNotifications();
////        if ((Fragment_HS_Notification.lastNoti != null && l.get(0).getTimeOfNotification().compareTo(Fragment_HS_Notification.lastNoti) > 0) || Fragment_HS_Notification.isThereNewNoti(l) )
////        {
////            Fragment_HS_Notification.newNoti = true; // atm I think it's unnecessary
////        }
//        if (!l.isEmpty())
//            Fragment_HS_Notification.lastNoti = l.get(0).getTimeOfNotification();
//        // update the lastNoti (this is needed as the app will send this info to the server in order to
//        // check for new notifications (see the method checkNewNotification below)
//
//        return l;
//    }





    /**
     *
     */

}