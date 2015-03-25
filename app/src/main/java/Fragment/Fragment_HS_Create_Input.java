package Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.ncl.team5.lloydsmockup.R;

/**
 * Created by Thanh on 20-Mar-15.
 */
public class Fragment_HS_Create_Input extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle saveInstance) {
        // TODO with saveInstance? Perhaps save some input on losing focus

        /* Create the view */
        View view = inflater.inflate(R.layout.fragment_hs_create_inputs, parent, false);
        ScrollView temp = (ScrollView) view;
        temp.setSmoothScrollingEnabled(true);
        return view;
    }

}
