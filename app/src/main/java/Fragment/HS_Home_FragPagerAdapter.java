package Fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ncl.team5.lloydsmockup.Houseshare_HomeView;

/**
 * Created by Thanh on 01-Apr-15.
 */
public class HS_Home_FragPagerAdapter extends FragmentStatePagerAdapter {

    public static final int FRAGS = 2;

    public HS_Home_FragPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: {
                return Fragment_HS_Home.newInstance(Houseshare_HomeView.username, Houseshare_HomeView.house_name);
            }
            case 1: {
                return Fragment_HS_Notification.newInstance(Houseshare_HomeView.username, Houseshare_HomeView.house_name);
            }
            default: {
                return null;
            }
        }
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return FRAGS;
    }

    @Override
    public int getItemPosition(Object item) {
        Fragment fragment = (Fragment) item;
            return POSITION_NONE;
    }
}
