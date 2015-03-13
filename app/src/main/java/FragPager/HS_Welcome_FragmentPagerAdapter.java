package FragPager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ncl.team5.lloydsmockup.R;

/**
 * Created by Thanh on 07-Mar-15.
 */

/*
Adapter for pages to be displayed in the payments activity
 */
public class HS_Welcome_FragmentPagerAdapter extends FragmentPagerAdapter {

    public final static int FRAGMENT_VIEWS = 4;

    public HS_Welcome_FragmentPagerAdapter(FragmentManager manager) { // not sure what this's for, probably needed for getting back a FragmentManager later on.
        super(manager);
    }

    @Override
    public Fragment getItem(int number) {
 switch (number) {
     case 0: {
         return new Fragment_HS_Welcome_1(); // existing recipient

     }
     case 1: {
         return new Fragment_HS_Welcome_2(); // existing recipient
     }
     case 2: {
         return new Fragment_HS_Welcome_3(); // existing recipient
     }
     case 3: {
         return new Fragment_HS_Welcome_4(); // existing recipient
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
