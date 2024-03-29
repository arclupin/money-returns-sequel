package Fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * Main pager adapter for the houseshare homeview
 *
 * Created by Thanh on 01-Apr-15.
 */
public class HS_Home_FragPagerAdapter extends FragmentStatePagerAdapter {

    private String username;
    private String hs_name;
    private String hsid;
    // should be enum
    private String view_type;

    public static final int FRAGS = 2;

    public Map<Integer, String> getTags() {
        return tags;
    }

    private Map<Integer, String> tags = new HashMap<Integer, String>();

    public HS_Home_FragPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public HS_Home_FragPagerAdapter(FragmentManager fm, String username, String hsid, String hs_name, String view_type) {
        super(fm);
        this.username = username;
        this.hsid = hsid;
        this.hs_name = hs_name;
        this.view_type = view_type;
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position the currently visible fragment
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: {
                return Fragment_HS_Home.newInstance(username, hsid, hs_name, view_type);
            }
            case 1: {
                return Fragment_HS_Notification.newInstance(username, hs_name, hsid);
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

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object o = super.instantiateItem(container, position);
            // record the fragment tag here.
        Log.d("instantiate", o.toString());
            Fragment f = (Fragment) o;
            String tag = f.getTag();
            tags.put(position, tag);
        return o;

    }
}
