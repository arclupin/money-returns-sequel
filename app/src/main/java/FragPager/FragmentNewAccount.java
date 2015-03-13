package FragPager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ncl.team5.lloydsmockup.Payments;
import com.ncl.team5.lloydsmockup.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thanh on 07-Mar-15.
 */

//New Account Fragment
public class FragmentNewAccount extends Fragment {
    private List<String> accountStrings = new ArrayList<String>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle saveInstance) {
        // TODO with saveInstance? Perhaps save some input on losing focus

        View view = inflater.inflate(R.layout.fragment_new_account, parent, false);
        Spinner s = (Spinner) view.findViewById(R.id.Payment_Old_spinner1);
        ArrayAdapter<String> a = new ArrayAdapter<String>(this.getActivity(), R.layout.spinner_text_colour, accountStrings);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Payments host = (Payments) this.getActivity();
        host.getAccounts();

        return view; //
    }
}
