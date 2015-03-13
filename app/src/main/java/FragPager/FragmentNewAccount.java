package FragPager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ncl.team5.lloydsmockup.Payments;
import com.ncl.team5.lloydsmockup.R;

/**
 * Created by Thanh on 07-Mar-15.
 */

//New Account Fragment
public class FragmentNewAccount extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle saveInstance) {
        // TODO with saveInstance? Perhaps save some input on losing focus
        View view = inflater.inflate(R.layout.fragment_new_account, parent, false);

        Spinner newS = (Spinner) view.findViewById(R.id.Payment_New_spinner1);


        ArrayAdapter<String> a = new ArrayAdapter<String>(this.getActivity(), R.layout.spinner_text_colour, Payments.accountStrings);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        newS.setAdapter(a);

        return view; //

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);


    }
}
