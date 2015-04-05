package Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ncl.team5.lloydsmockup.CustomMessageBox;
import com.ncl.team5.lloydsmockup.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import HTTPConnect.Connection;
import HTTPConnect.Notification;
import HTTPConnect.Request_Params;
import HTTPConnect.Responses_Format;
import Utils.Utilities;

/**
 * Fragment for displaying the home view in the home view activity <br/>
 * (another fragment that would be invoked in this activity is the notification fragment
 *
 */
public class Fragment_HS_Notification extends Fragment_HS_Abstract {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DATA = "total_data";
    public static Date lastNoti;
    public static boolean newNoti;

    private static String username;
    private static String hs_name;
    private static List<Notification> data;
    private OnNotificationInteraction mListener;
    private TableLayout mTable;
    private ProgressBar loading_icon;
    private RelativeLayout mLayoutContainer;

    private static enum UI_WORK_TYPE {MAIN};


    public TableLayout getmTable() {
        return mTable;
    }

    public static Fragment_HS_Notification newInstance(String username, String hs_name) {
        Fragment_HS_Notification f = new Fragment_HS_Notification();
        Bundle b = new Bundle();
        b.putString("USR", username);
        b.putString("HS_NAME", hs_name);
        f.setArguments(b);
        return f;
    }

//    class UIWorker extends AsyncTask<UI_WORK_TYPE, Void, Boolean> {
//
//
//        @Override
//        protected void onPreExecute() {
//            loading_icon.setVisibility(View.VISIBLE);
//        }
//
//        /**
//         * Override this method to perform a computation on a background thread. The
//         * specified parameters are the parameters passed to {@link #execute}
//         * by the caller of this task.
//         * <p/>
//         * This method can call {@link #publishProgress} to publish updates
//         * on the UI thread.
//         *
//         * @param params The parameters of the task.
//         * @return A result, defined by the subclass of this task.
//         * @see #onPreExecute()
//         * @see #onPostExecute
//         * @see #publishProgress
//         */
//        @Override
//        protected Boolean doInBackground(UI_WORK_TYPE... params) {
//            switch (params[0]) {
//                case MAIN:{
//
//                    // must wrap the work involving the ui inside runOnUiThread otherwise it would throw an exception that
//                    // the current thread can't touch view it hasnt created.
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            makeUI();
//                        }
//                    });
//
//                    break;
//                }
//            }
//            return true;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean result) {
//            loading_icon.setVisibility(View.GONE);
//        }
//    }

    /**
     * By default the fragments in pager are not updated when switched to.
     * So use this method to achieve this
     */
    @Override
    public void update() {
            new NotificationWorker(getActivity())
                    .setMode(NOTI_WORK.FETCH_UPDATE)
                    .execute(Request_Params.PARAM_TYPE, Request_Params.VAL_FETCH_NOTI, Request_Params.PARAM_USR, username);
    }

    public static enum NOTI_WORK {
        CHECK, FETCH, SEEN, WELCOME, REFUSE, FETCH_UPDATE
    }


    public class NotificationWorker extends Connection {
        private NOTI_WORK mode;

        public NotificationWorker setMode(NOTI_WORK mode) {
            this.mode = mode;
            return this;
        }

        @Override
        protected String doInBackground(String... params) {
            return super.doInBackground(params);
        }

        @Override
        protected void onPreExecute(){
            setMode(MODE.LONG_NO_DIALOG_TASK);
            if (mode == NOTI_WORK.FETCH_UPDATE) {
                loading_icon.setVisibility(View.VISIBLE);
            }
        }

        public NotificationWorker(Activity a) {
            super(a);
        }

        @Override
        protected void onPostExecute(String r) {
            super.onPostExecute(r);

            if (mode == NOTI_WORK.FETCH_UPDATE) {
                fetchNotifications(r);
                loading_icon.setVisibility(View.GONE);
                makeUI();
            }



        }
    }

    // TODO: For now it will just display the plain response from the server
    // need updating later

    public Fragment_HS_Notification() {
        // Required empty public constructor
    }

public interface OnNotificationInteraction {
    public void onNewNotiReceived();
}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username = getArguments().getString("USR");
        hs_name = getArguments().getString("HS_NAME");

//        Log.d("on create noti", username);
//        try {
//            String n =  new NotificationWorker(getActivity())
//                    .setMode(NOTI_WORK.FETCH)
//                    .execute(Request_Params.PARAM_TYPE, Request_Params.VAL_REF_NOTI, Request_Params.PARAM_USR, username).get();
//            checkNewNotification(n);
//
//            String r =  new NotificationWorker(getActivity())
//                    .setMode(NOTI_WORK.FETCH)
//                    .execute(Request_Params.PARAM_TYPE, Request_Params.VAL_FETCH_NOTI, Request_Params.PARAM_USR, username).get();
//            fetchNotifications(r);
//        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//        Bus b = new Bus();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mLayoutContainer = (RelativeLayout) inflater.inflate(R.layout.fragment_hs_notification, container, false);
        mTable = (TableLayout) mLayoutContainer.findViewById(R.id.hs_notification_table);
        loading_icon = (ProgressBar) mLayoutContainer.findViewById(R.id.noti_loading_icon);
        checkEmptyNotification(this);

        return mLayoutContainer;
    }


    public int findNotiUsingID(String id) {
        for (int i = 0; i <  mTable.getChildCount(); i++) {
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
        try {
            mListener = (OnNotificationInteraction) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnNotificationInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
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
        //Just need to send a simple request asking for the arrival of any new noti, the server will take care of the display_search

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
                mListener.onNewNotiReceived();
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
            if (mTable.getChildCount() == 0) {
                LayoutInflater i = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                TableRow v = (TableRow) i.inflate(R.layout.hs_noti_empty, null);
                mTable.addView(v);
            }
        }
    }

    private void makeUI() {
        mTable.removeAllViews();
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
                    mTable.removeView(t);

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
                    mTable.removeView(t);

                }
            });
            mTable.addView(v, i);



        }
        mTable.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                checkEmptyNotification(Fragment_HS_Notification.this);
            }
        });
        checkEmptyNotification(Fragment_HS_Notification.this);
    }

    }






