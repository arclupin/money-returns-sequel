package Fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ncl.team5.lloydsmockup.R;

import Utils.StringUtils;

/**
 * Fragment for displaying the home view in the home view activity <br/>
 * (another fragment that would be invoked in this activity is the notification fragment
 *
 */
public class Fragment_HS_Home extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DATA = "total_data";


    // TODO: For now it will just display the plain response from the server
    // need updating later
    private static String data;


    private OnFragmentInteractionListener_HomeView mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment Fragment_HS_Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_HS_Home newInstance(String param1) {
        Fragment_HS_Home fragment = new Fragment_HS_Home();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public Fragment_HS_Home() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//        }
        String dat =  mListener.onHomeViewCreated(this);
        Log.d("date", dat);
        if (!StringUtils.isFieldEmpty(dat)) // I guess doing this would help that even if the user loses internet connection they still can be able to see the previous state of the home view.
            data = dat;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout l = (FrameLayout) inflater.inflate(R.layout.fragment_home_view, container, false);
        TextView tv = (TextView) l.findViewById(R.id.home_view_text_view);
        Log.d("date", data);
        tv.setText(data);
        return l;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener_HomeView) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener_HomeView");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener_HomeView {
        // TODO: Update argument type and name

        /**
         * method called when the home view fragment is created (fetch data from server) <br/>
         * NOTE: there are various solutions to this.
         *
         * @param f the fragment
         */
        public String onHomeViewCreated(Fragment_HS_Home f);
    }



}
