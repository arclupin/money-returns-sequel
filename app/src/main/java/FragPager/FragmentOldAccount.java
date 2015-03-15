package FragPager;

/* This is the fragment that is used on the payments screen
 * that is used for the recent payee tab, so it can send to the last 3 payees */

/* Imports */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ncl.team5.lloydsmockup.Payments;
import com.ncl.team5.lloydsmockup.R;

import java.util.List;

/* Old Account Fragment Extends fragment */
public class FragmentOldAccount extends Fragment {

    /* Global variables */
    private Spinner s2;
    private Spinner s;
    private Payments pay;
    private List<String> recentAcc;

    /* Run when the fragment is created */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle saveInstance) {
        // TODO with saveInstance? Perhaps save some input on losing focus

        /* Create the view */
        View view = inflater.inflate(R.layout.fragment_old_account, parent, false);

        /* get the payment activity, and use it to populate the two Lists */
        pay = (Payments) getActivity();
        List<String> accountString =  pay.getAccountString();
        recentAcc = pay.getRecentAccString();

        /* create the two spinners (N.B. need view. or it cant find the view) */
        s = (Spinner) view.findViewById(R.id.Payment_Old_spinner1);
        s2 = (Spinner) view.findViewById(R.id.Payment_Old_spinner2);

        /* Set up array adapters to populate spinners */
        ArrayAdapter<String> a = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text_colour, accountString);
        ArrayAdapter<String> a2 = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text_colour, accountString);

        /* Set the drop down resource */
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        a2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        /* set the adapters */
        s.setAdapter(a);
        s2.setAdapter(a2);

        /* event handler for the first spinner on the first tab */
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            /* Ran when an item in the spinner is clicked, populates the second spinner */
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                /* gets the object that was clicked on */
                Object item = parentView.getItemAtPosition(position);

                /* Makes sure its a string... it will always be a string but just to check */
                if(item instanceof String)
                {
                    /* Populates the list with the recent transactions from the selected account */
                    pay.getRecentTrans(s.getItemAtPosition(position).toString());

                    /* set up a new adapter for the second spinner */
                    ArrayAdapter<String> a3 = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text_colour, recentAcc);
                    s2.setAdapter(a3);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                /* Just needed it for the event handler :/ */
            }
        });

        /* Returns the view back to the caller (payments) */
        return view;
    }

}
