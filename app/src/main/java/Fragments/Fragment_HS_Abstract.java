package Fragments;

import android.support.v4.app.Fragment;

/**
 * Abstract base class for house share fragments that has updating content from the server as part
 * of their jobs (Ex: Home page, Notifications etc.)
 *
 * Created by Thanh on 03-Apr-15.
 */
public abstract class Fragment_HS_Abstract extends Fragment {

    /**
     * update method
     * Every fragment that needs updating on some particular occasions (like onResume, onCreate)
     * should subclass this abstract class
     */
    public abstract void update();
}
