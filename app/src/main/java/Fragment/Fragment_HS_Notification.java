package Fragment;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ncl.team5.lloydsmockup.CustomMessageBox;
import com.ncl.team5.lloydsmockup.Houseshare_HomeView;
import com.ncl.team5.lloydsmockup.R;
import com.squareup.otto.Bus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import HTTPConnect.Connection;
import HTTPConnect.Notification;
import HTTPConnect.Request_Params;
import HTTPConnect.Responses_Format;
import Utils.Animation;
import Utils.StringUtils;
import Utils.Utilities;

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

    private static String username;
    private static String hs_name;
    private static List<Notification> data;
    private OnFragmentInteractionListener_Notification mListener;
    private TableLayout l;
    private ViewGroup container;


    public TableLayout getL() {
        return l;
    }

    public static Fragment_HS_Notification newInstance(String username, String hs_name) {
        Fragment_HS_Notification f = new Fragment_HS_Notification();
        Bundle b = new Bundle();
        b.putString("USR", username);
        b.putString("HS_NAME", hs_name);
        f.setArguments(b);
        return f;
    }

    public static enum NOTI_WORK {
        CHECK, FETCH, SEEN, WELCOME, REFUSE
    }


    public class NotificationWorker extends Connection {
        private NOTI_WORK mode;

        public NotificationWorker setMode(NOTI_WORK mode) {
            this.mode = mode;
            return this;
        }

        @Override
        protected void onPreExecute(){
            setMode(MODE.SHORT_TASK);
        }

        public NotificationWorker(Activity a) {
            super(a);
        }

        @Override
        protected void onPostExecute(String r) {
            super.onPostExecute(r);


        }
    }

    // TODO: For now it will just display the plain response from the server
    // need updating later

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

//        username = getArguments().getString("USR");
//        hs_name = getArguments().getString("HS_NAME");
        username = Houseshare_HomeView.username;
        hs_name = Houseshare_HomeView.house_name;


        Log.d("on create noti", username);
//        new NotificationWorker(getActivity())
//               .setMode(NOTI_WORK.FETCH)
//               .execute(Request_Params.PARAM_TYPE, Request_Params.VAL_FETCH_NOTI, Request_Params.PARAM_USR, username);
        try {
            String r =  new NotificationWorker(getActivity())
                    .setMode(NOTI_WORK.FETCH)
                    .execute(Request_Params.PARAM_TYPE, Request_Params.VAL_FETCH_NOTI, Request_Params.PARAM_USR, username).get();
            fetchNotifications(r);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Bus b = new Bus();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        l = (TableLayout) getActivity().getLayoutInflater().inflate(R.layout.fragment_hs_notification, container, false);
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
//                    mListener.onWelcomeButtonClicked(Fragment_HS_Notification.this, a.getText().toString(), noti_id );
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
//                    mListener.onRefuseButtonClicked(Fragment_HS_Notification.this, a.getText().toString(), noti_id);
                    TableRow t = (TableRow) v.getParent().getParent().getParent();
                    Log.d("row on click", t.toString());
                    l.removeView(t);

                }
            });
            l.addView(v, i);



        }
        l.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                checkEmptyNotification(Fragment_HS_Notification.this);
            }
        });
        checkEmptyNotification(Fragment_HS_Notification.this);
//
//        if (newNoti) {
//
//        }

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
//        mListener.onNotificationsSeen(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener_Notification) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener_Notification");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
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

    /**
     * this method just check if there is any new notification, it does not fetch any notification
     * this is needed on activity start-up.
     * <p/>
     * <i>when is there a new notification?</i> <br/>
     * <p/>
     * there is at least 1 noti in the db server holding NEW (see the Notification class)
     * ... To be added
     */
    public void checkNewNotification(String r) {
        //TODO complete
        //Just need to send a simple request asking for the arrival of any new noti, the server will take care of the search

        try {
            /* Command required to make a payment, takes username, to account, from account, both sort codes and amount
             * Returns: JSON String */
//            result = connect.execute(Request_Params.PARAM_TYPE, Request_Params.VAL_REF_NOTI, Request_Params.PARAM_USR, this.username).get();
//            /* Turns String into JSON object, can throw JSON Exception */
            JSONObject jo = new JSONObject(r);

            /* Check if the user has timed out */
            if (jo.getString("expired").equals("true")) {

                /* Display message box and auto logout user */
                AlertDialog.Builder errorBox = new AlertDialog.Builder(getActivity());
                final Connection temp_connect = new Connection(getActivity());
                final String temp_usr = username;
                errorBox.setMessage("Your session has been timed out, please login again")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                temp_connect.autoLogout(temp_usr);
                            }
                        });
                AlertDialog alert = errorBox.create();
                alert.show();
            } else if (jo.getString("status").equals("true")) {
//                hasNewNoti = true;
            } else if (jo.getString("status").equals("false")) {
//                hasNewNoti = false;
            }

        }
        /* Catch the exceptions */ catch (JSONException jse) {
            /* Error in the JSON response */
            new CustomMessageBox(getActivity(), "There was an error in the server response");
            jse.printStackTrace();
        } catch (Exception e) {
            /* Failsafe if something goes utterly wrong */
            new CustomMessageBox(getActivity(), "An unknown error occurred");
            e.printStackTrace();
        }
    }


    /**
     * This method will be invoked when and only when the user clicks on the noti icon
     *
     * @return
     */
    public void fetchNotifications(String r) {
        //TODO complete

        Log.d("fetch here", r);

        List<Notification> l = new ArrayList<Notification>();


        try {
                JSONObject jo = new JSONObject(r);

            /* Check if the user has timed out */
            if (jo.getString("expired").equals("true")) {

                /* Display message box and auto logout user */
                AlertDialog.Builder errorBox = new AlertDialog.Builder(getActivity());
                final Connection temp_connect = new Connection(getActivity());
                final String temp_usr = username;
                errorBox.setMessage("Your session has been timed out, please login again")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                temp_connect.autoLogout(temp_usr);
                            }
                        });
                AlertDialog alert = errorBox.create();
                alert.show();
            } else if (jo.getString("status").equals("true")) {
                JSONArray Noti_js_arr = jo.getJSONArray(Responses_Format.RESPONSE_HS_CONTENT);
                for (int i = 0; i < Noti_js_arr.length(); i++) {
                    JSONArray noti_arr_in = Noti_js_arr.getJSONArray(i);

                    String id = noti_arr_in.getString(0);
                    boolean read = noti_arr_in.getString(noti_arr_in.length() - 1).equals("1");
                    int type = noti_arr_in.getInt(1);

                    Notification n = new Notification(type, read, id);
                    n.addParam(noti_arr_in.getString(Notification.HSID_POS + 2))
                            .addParam(noti_arr_in.getString(Notification.PARAM_POS + 2))
                            .addParam(noti_arr_in.getString(Notification.TIME_POS + 2));

                    l.add(n);
                }
            }
            /* There was an error indide the status return field, display appropriate error message */
            //TODO implement error messages
            else {
                /* give more info on the error here, no money taken from account */
                /* Use the status results to display certain error messages */
            }

        }
        /* Catch the exceptions */ catch (JSONException jse) {
            /* Error in the JSON response */
            new CustomMessageBox(getActivity(), "There was an error in the server response");
            Log.d("jse", jse.getMessage(), jse);
        } catch (Exception e) {
            /* Failsafe if something goes utterly wrong */
            new CustomMessageBox(getActivity(), "An unknown error occurred");
            e.printStackTrace();
        }

        data = l;


    }


    public String[] onNotificationsSeen(Fragment_HS_Notification f) {

        List<String> request = new ArrayList<String>();
        request.add(Request_Params.PARAM_TYPE);
        request.add(Request_Params.MARK_NOTI_AS_SEEN_NOT_READ);
        request.add(Request_Params.PARAM_USR);
        request.add(username);

        for (int i = 0; i < Fragment_HS_Notification.data.size(); i++) {
            request.add(Request_Params.MARK_NOTI_AS_SEEN_NOT_READ_PARAM);
            request.add(Fragment_HS_Notification.data.get(i).getId());
        }
        Log.d("Request mark as seen", Arrays.toString(request.toArray(new String[request.size()])));
        return request.toArray(new String[request.size()]);

    }


    public void afterRefuseButtonClicked(String p, String r) {
        //TODO Waiting for the server to be configured for this request

        try {
            /* Command required to make a payment, takes username, to account, from account, both sort codes and amount
             * Returns: JSON String */
//            result = connect.execute(Request_Params.PARAM_TYPE, Request_Params.VAL_REFUSE_MEMBER, Request_Params.PARAM_USR, this.username,
//                    Request_Params.VAL_REFUSE_MEMBER_PARAM, p,
//                    "NOTI_ID", noti_id).get();
            /* Turns String into JSON object, can throw JSON Exception */
            JSONObject jo = new JSONObject(r);

            /* Check if the user has timed out */
            if (jo.getString("expired").equals("true")) {

                /* Display message box and auto logout user */
                AlertDialog.Builder errorBox = new AlertDialog.Builder(getActivity());
                final Connection temp_connect = new Connection(getActivity());
                final String temp_usr = username;
                errorBox.setMessage("Your session has been timed out, please login again")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                temp_connect.autoLogout(temp_usr);
                            }
                        });
                AlertDialog alert = errorBox.create();
                alert.show();
            }

            else if (jo.getString("status").equals("true")) {
                new CustomMessageBox(getActivity(), "You have refused the request of " + p.substring(0, p.length() - 2) + "!");
            }

            /* There was an error indide the status return field, display appropriate error message */
            //TODO implement error messages
            else {
                new CustomMessageBox(getActivity(), "Sorry. We coult not process your request at the moment \nIf you are experiencing this error constantly, please contact our team.");
                /* give more info on the error here, no money taken from account */
                /* Use the status results to display certain error messages */
            } }
        /* Catch the exceptions */ catch (JSONException jse) {
            /* Error in the JSON response */
            new CustomMessageBox(getActivity(), "There was an error in the server response");
            Log.d("jse", jse.getMessage(), jse);

        } catch (Exception e) {
            /* Failsafe if something goes utterly wrong */
            new CustomMessageBox(getActivity(), "An unknown error occurred");
            e.printStackTrace();
        }
    }

    public void checkEmptyNotification(Fragment_HS_Notification f) {
        if (f.isVisible()) {
            if (l.getChildCount() == 0) {
                LayoutInflater i = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                TableRow v = (TableRow) i.inflate(R.layout.hs_noti_empty, l, false);
                l.addView(v);
            }
        }
    }

    }






