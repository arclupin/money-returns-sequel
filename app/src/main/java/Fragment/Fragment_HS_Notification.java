package Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ncl.team5.lloydsmockup.R;

import java.util.List;

import HTTPConnect.Notification;
import Utils.Animation;
import Utils.StringUtils;

/**
 * Fragment for displaying the home view in the home view activity <br/>
 * (another fragment that would be invoked in this activity is the notification fragment
 *
 */
public class Fragment_HS_Notification extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DATA = "total_data";


    // TODO: For now it will just display the plain response from the server
    // need updating later
    private static List<Notification> data;


    private OnFragmentInteractionListener_Notification mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment Fragment_HS_Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_HS_Notification newInstance(String param1) {
        Fragment_HS_Notification fragment = new Fragment_HS_Notification();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public Fragment_HS_Notification() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//        }
//        String dat =  mListener.onNotificationViewSelected(this);
//        Log.d("notification", dat);
//        if (!StringUtils.isFieldEmpty(dat)) // I guess doing this would help that even if the user loses internet connection they still can be able to see the previous state of the home view.
//            data = dat;
        data = mListener.onNotificationViewSelected(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        TableLayout l = (TableLayout) inflater.inflate(R.layout.fragment_hs_notification, container, false);
        for (int i = 0; i < data.size(); i++) {
            l.addView(data.get(i).makeNotiRow(getActivity()));
        }
//        Log.d("notification", data);
//        tv.setText(data);
        return l;
    }

    @Override
         public void onStop() {
        super.onStop();
//        Animation.fade_out(getActivity().findViewById(R.id.layout_hs_notification_container), getActivity(), Animation.SHORT, Animation.POST_EFFECT.PERMANENTLY);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener_Notification) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener_Notification");
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
    public interface OnFragmentInteractionListener_Notification {
        // TODO: Update argument type and name

        /**
         * method called when the notification fragment is created (fetch data from server) <br/>
         * NOTE: there are various solutions to this.
         *
         * @param f the fragment
         */
        public List<Notification> onNotificationViewSelected(Fragment_HS_Notification f);
    }



}
