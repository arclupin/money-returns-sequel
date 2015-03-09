package FragPager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ncl.team5.lloydsmockup.R;

/**
 * Created by Thanh on 07-Mar-15.
 */
//Old Account Fragment
public class FragmentOldAccount extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle saveInstance) {
        // TODO with saveInstance? Perhaps save some input on losing focus
        View view = inflater.inflate(R.layout.fragment_old_account, parent, false);
        return view; //
    }
}
