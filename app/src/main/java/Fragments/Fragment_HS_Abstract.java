package Fragments;

import android.support.v4.app.Fragment;

/**
 * Abstract base class for the home view and notification fragments
 *
 * Created by Thanh on 03-Apr-15.
 */
public abstract class Fragment_HS_Abstract extends Fragment {

    /**
     * By default the fragments in pager are not updated when switched to.
     * So use this method to achieve this
     */
    public abstract void update();
}
