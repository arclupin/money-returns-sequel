package FragPager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ncl.team5.lloydsmockup.Houseshare_Welcome;
import com.ncl.team5.lloydsmockup.R;

/**
 * Created by Thanh on 07-Mar-15.
 */
//Old Account Fragment
public class Fragment_HS_Welcome_2 extends Fragment {


//public static Fragment_HS_Welcome_2 getFragment_HS_Welcome_1(int layout) {
//        Fragment_HS_Welcome_2 f = new Fragment_HS_Welcome_2();
//        Bundle bundle = new Bundle();
//        bundle.putInt("layout", layout) ;
//        f.setArguments(bundle);
//        Log.d("Layout", Integer.toString(bundle.getInt("layout")));
//        return f;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle saveInstance) {
        // TODO with saveInstance? Perhaps save some input on losing focus
        View view = inflater.inflate(R.layout.fragment_hs_welcome_2, parent, false);

        TextView textView = (TextView) view.findViewById(R.id.Welcome_HS_2_TextView);
//        Log.d("TextView", Integer.toString(this.getArguments().getInt("lay));
        Log.d("view height", Integer.toString(this.getActivity().findViewById(R.id.HS_Welcome_Container).getHeight()));
        textView.setY(parent.getHeight() * 3 / 10);
        textView.setLineSpacing(0, 1.1f);


        return view; //
    }
}
