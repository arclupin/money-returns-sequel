package Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ncl.team5.lloydsmockup.R;

/**
 * fragment for view pager showing the introductory page in houseshare service
 *
 * Created by Thanh on 07-Mar-15.
 */

public class Fragment_HS_Welcome_2 extends Fragment {
    int height;

    public static Fragment_HS_Welcome_2  getFragment_HS_Welcome_2(int height) {
        Fragment_HS_Welcome_2 f = new Fragment_HS_Welcome_2();
        Bundle bundle = new Bundle();
        bundle.putInt("parent_height", height) ;
        f.setArguments(bundle);
        Log.d("height", Integer.toString(bundle.getInt("parent_height")));
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle saveInstance) {

        // If we use parent.getHeight() directly, it would equate to 0 in case the activity is
        // resumed after losing focus, so need to save the height first.
        if (saveInstance != null)
            height = saveInstance.getInt("parent_height");
        else
            height = parent.getHeight();
        Log.d("On creating height: ", Integer.toString(height));

        // TODO with saveInstance? Perhaps save some input on losing focus
        View view = inflater.inflate(R.layout.fragment_hs_welcome_2, parent, false);

        TextView textView = (TextView) view.findViewById(R.id.Welcome_HS_2_TextView);

        // reposition the views
        textView.setY(height * 3 / 10);
        textView.setLineSpacing(0, 1.1f);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("parent_height", this.height);
        Log.d("On leaving height: ", Integer.toString(height));
    }
}
