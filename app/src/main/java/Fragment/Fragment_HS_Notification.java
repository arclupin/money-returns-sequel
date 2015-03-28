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
import android.widget.TableRow;
import android.widget.TextView;

import com.ncl.team5.lloydsmockup.R;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
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
    public static Date lastNoti;
    public static boolean newNoti;

    private TableLayout l;


    // TODO: For now it will just display the plain response from the server
    // need updating later
    public static List<Notification> data;


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
        try {
            data = mListener.onNotificationViewSelected(this);

        }
        catch (ParseException e){
            Log.e("date persing error", e.getMessage(), e);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        l = (TableLayout) inflater.inflate(R.layout.fragment_hs_notification, container, false);
        for (int i = 0; i < data.size(); i++) {
            Log.d("data", data.get(i).getAdditional_params().get(Notification.HSID_POS));
            View v = data.get(i).makeNotiRow(getActivity());
            Log.d("rows", v.toString());
            final TextView a = (TextView) v.findViewById(R.id.noti_join_req_admin_name);


            final String noti_id = data.get(i).getId();
            TextView welcome_button = (TextView) v.findViewById(R.id.ok);
            welcome_button.setClickable(true);
            welcome_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onWelcomeButtonClicked(Fragment_HS_Notification.this, a.getText().toString(), noti_id );
                    TableRow t = (TableRow) v.getParent().getParent().getParent(); // looks quite odd
                    Log.d("row on click", t.toString());
                    l.removeView(t);

                }
            });

            TextView cancel_button = (TextView) v.findViewById(R.id.refuse);
            cancel_button.setClickable(true);
            cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onRefuseButtonClicked(Fragment_HS_Notification.this, a.getText().toString(), noti_id);
                    TableRow t = (TableRow) v.getParent().getParent().getParent();
                    Log.d("row on click", t.toString());
                    l.removeView(t);

                }
            });
            l.addView(v);



        }
        l.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mListener.checkEmptyNotification(Fragment_HS_Notification.this);
            }
        });
//
//        if (newNoti) {
//
//        }

        Log.d("notification", Integer.toString(l.getChildCount()));
//        tv.setText(data);
        return l;
    }

    @Override
         public void onStop() {
        super.onStop();
//        Animation.fade_out(getActivity().findViewById(R.id.layout_hs_notification_container), getActivity(), Animation.SHORT, Animation.POST_EFFECT.PERMANENTLY);
    }

    public int findNotiUsingID(String id) {
        for (int i = 0; i <  l.getChildCount(); i++) {
            if (id.equals(data.get(i).getId())) {
                return i;
            }
        }
        return -1;
    }


    /**
     * onDestroyView mark the noti as seen
     * initially I put this action in the onCreateView function but this would increase the time needed for creating view which in turn would probably effect the UI
     * so it should be done this late to improve performance
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mListener.onNotificationsSeen(this);

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
        public List<Notification> onNotificationViewSelected(Fragment_HS_Notification f) throws ParseException;

        public void onNotificationsSeen(Fragment_HS_Notification f);

        public void onWelcomeButtonClicked(Fragment_HS_Notification f, String name, String noti_id);

        public void onRefuseButtonClicked(Fragment_HS_Notification f, String name, String noti_id);

        public void checkEmptyNotification(Fragment_HS_Notification f);

    }

    public static boolean isThereNewNoti(List<Notification> l) {
        for (int i = 0; i < l.size(); i++) {
            if (!l.get(i).getRead())
                return true;
        }
        return false;
    }





}
