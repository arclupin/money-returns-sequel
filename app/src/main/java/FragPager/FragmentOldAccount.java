package FragPager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ncl.team5.lloydsmockup.Payments;
import com.ncl.team5.lloydsmockup.R;

import java.util.List;

/**
 * Created by Thanh on 07-Mar-15.
 */
//Old Account Fragment
public class FragmentOldAccount extends Fragment {

    public Spinner s2;
    public Spinner s;
    Payments pay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle saveInstance) {
        // TODO with saveInstance? Perhaps save some input on losing focus
        View view = inflater.inflate(R.layout.fragment_old_account, parent, false);

        pay = (Payments) getActivity();

        s = (Spinner) view.findViewById(R.id.Payment_Old_spinner1);
        s2 = (Spinner) view.findViewById(R.id.Payment_Old_spinner2);

        final ArrayAdapter<String> a = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text_colour, Payments.accountStrings);
        final ArrayAdapter<String> a2 = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text_colour, Payments.accountStrings);

        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        a2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        s.setAdapter(a);
        s2.setAdapter(a2);


        //event handler for the spinner on the second tab
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Object item = parentView.getItemAtPosition(position);

                if(item instanceof String)
                {
                    pay.getRecentTrans(s.getItemAtPosition(position).toString());
                    ArrayAdapter<String> a3 = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text_colour, pay.recentAcc);
                    s2.setAdapter(a3);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //NOTHING IN HERE
                //Just needed it for the event handler :/
            }

        });

        return view; //
    }



}
