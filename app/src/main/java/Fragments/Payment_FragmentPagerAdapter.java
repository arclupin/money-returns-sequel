package Fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Adapter for pages to be displayed in the payments activity
 *
 * Created by Thanh on 07-Mar-15.
 */

public class Payment_FragmentPagerAdapter extends FragmentPagerAdapter {

    final static int FRAGMENT_VIEWS = 2;

    public Payment_FragmentPagerAdapter(FragmentManager manager) { // not sure what this's for, probably needed for getting back a FragmentManager later on.
        super(manager);
    }

    @Override
    public Fragment getItem(int number) {
 switch (number) {
     case 0: {
         return new FragmentOldAccount(); // existing recipient
     }
     case 1: {
         return new FragmentNewAccount(); // new recipient
     }
     default : {
         return null; //TODO
     }
 }
    }

    @Override
    public int getCount() {
        return FRAGMENT_VIEWS;
    }

}
