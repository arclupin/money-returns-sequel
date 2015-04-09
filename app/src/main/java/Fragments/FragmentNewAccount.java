package Fragments;

/* This is the fragment that is used on the payments screen
 * that is used for the new payee tab */

/* Imports */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ncl.team5.lloydsmockup.Payments;
import com.ncl.team5.lloydsmockup.R;

import java.util.List;


/* New Account Fragment that extends fragment */
public class FragmentNewAccount extends Fragment {

    /* Runs when the fragment is created */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle saveInstance) {
        // TODO with saveInstance? Perhaps save some input on losing focus

        /* Create the view */
        View view = inflater.inflate(R.layout.fragment_new_account, parent, false);

        /* Set up the spinner and populate the list from the payments object */
        Spinner newS = (Spinner) view.findViewById(R.id.Payment_New_spinner1);
        Payments pay = (Payments) getActivity();
        List<String> accStr = pay.getAccountString();

        /* Set the array adapter, the view resource and add the adapter */
        ArrayAdapter<String> a = new ArrayAdapter<String>(this.getActivity(), R.layout.spinner_text_colour, accStr);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newS.setAdapter(a);

        /* Return the view to the caller (payments) */
        return view;
    }
}
