package Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.ncl.team5.lloydsmockup.HouseShare_Bill_Member;
import com.ncl.team5.lloydsmockup.HouseShare_Bill_Owner;
import com.ncl.team5.lloydsmockup.IntentConstants;
import com.ncl.team5.lloydsmockup.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import HTTPConnect.ConcurrentConnection;
import HTTPConnect.Connection;
import HTTPConnect.Notification;
import HTTPConnect.Request;
import HTTPConnect.RequestQueue;
import HTTPConnect.Request_Params;
import HTTPConnect.Response;
import HTTPConnect.Responses_Format;

/**
 * Fragment for displaying the home view in the home view activity <br/>
 * (another fragment that would be invoked in this activity is the notification fragment
 */
public class Fragment_HS_Notification extends Fragment_HS_Abstract {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DATA = "total_data";
    public static Date lastNoti;
    public static boolean newNoti;

    private static String username;
    private static String hs_name;
    private static String hsid;
    private static List<Notification> data;
    private OnNotificationInteraction mListener;
    private TableLayout mTable;
    private ProgressBar loading_icon;
    private static Boolean loaded = true;

    public RelativeLayout getmLayoutContainer() {
        return mLayoutContainer;
    }

    public ProgressBar getLoading_icon() {
        return loading_icon;
    }

    public SwipeRefreshLayout getmRefreshView() {
        return mRefreshView;
    }

    private RelativeLayout mLayoutContainer;

    private SwipeRefreshLayout mRefreshView;

    private static enum UI_WORK_TYPE {MAIN}

    ;

    private Request notisFetchingRequest;
    private Request memberApprovingRequest;
    private Request memberRefusingRequest;
    private Request notiReadMarkingRequest;


    public TableLayout getmTable() {
        return mTable;
    }

    public static Fragment_HS_Notification newInstance(String username, String hs_name, String hsid) {
        Fragment_HS_Notification f = new Fragment_HS_Notification();
        Bundle b = new Bundle();
        b.putString(IntentConstants.USERNAME, username);
        b.putString(IntentConstants.HOUSE_NAME, hs_name);
        b.putString(IntentConstants.HOUSESHARE_ID, hsid);
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
        new Notification_Worker(getActivity(), true, NOTI_WORK.FETCH)
                .execute(new RequestQueue().addRequest(getNotisFetchingRequest()).toList());
    }

    /**
     * This method is called when the user performs the swipe to refresh gesture
     * It will go fetch the data in short.
     */
    public void refresh() {
        new Notification_Worker(getActivity(), true, NOTI_WORK.FETCH_REFRESH)
                .execute(new RequestQueue().addRequest(getNotisFetchingRequest()).toList());
    }

    public static enum NOTI_WORK {
        CHECK,
        SEEN, // user has seen the notifications
        APPROVE,
        REFUSE,
        FETCH, // initial data fetch
        FETCH_REFRESH, // user swipe down to refresh
        NOTI_READ
    }


    public class Notification_Worker extends ConcurrentConnection {
        private NOTI_WORK mode;
        public static final String PARAM_USERNAME_IN_NOTI = "NOTI_USR";

        // a map storing additional params needed storing.
        private Map<String, String> additional_params;

        public Notification_Worker setMode(NOTI_WORK mode) {
            this.mode = mode;
            return this;
        }

        public Notification_Worker addNewParam(String key, String value) {
            additional_params.put(key, value);
            return this;
        }

        public String getParam(String key) {
            return additional_params.get(key);
        }

        @Override
        protected void onPreExecute() {

            if (mode == NOTI_WORK.FETCH) {
                if (!loaded) {
                    loading_icon.setVisibility(View.VISIBLE);
                    // only show the loading icon for the first time in which no notifications are
                    // occupying space yet.
                    loaded = true;
                }
                else if (!loading_icon.isShown())
                    mRefreshView.setRefreshing(true);
            }
            super.onPreExecute();
        }

        public Notification_Worker(Activity a) {
            super(a);
        }

        public Notification_Worker(Activity a, boolean dialog, NOTI_WORK mode) {
            super(a);
            this.mode = mode;
            additional_params = new HashMap<String, String>();
        }

        @Override
        protected void onPostExecute(List<Response> responses) {
            super.onPostExecute(responses);

            switch (mode) {
                case FETCH:
                case FETCH_REFRESH: {
                    fetchNotifications(responses.get(0).getRaw_response());
                    loading_icon.setVisibility(View.GONE);
                    makeUI();
                    if (mRefreshView.isRefreshing())
                        mRefreshView.setRefreshing(false); // only invoked through FETCH_REFRESH
                    break;

                }

                case REFUSE:
                case APPROVE: {
                    onSubButtonClicked(additional_params.get(PARAM_USERNAME_IN_NOTI), responses.get(0).getRaw_response(), mode);
                    break;
                }
                case NOTI_READ: {
                    // after marking the request as read, start the source
                    Intent billPageIntent = new Intent(getActivity(),
                            Boolean.valueOf(getParam(IntentConstants.IS_OWNER))
                                    ? HouseShare_Bill_Owner.class : HouseShare_Bill_Member.class);
                    billPageIntent.putExtra(IntentConstants.USERNAME, username);
                    billPageIntent.putExtra(IntentConstants.HOUSE_NAME, hs_name);
                    billPageIntent.putExtra(IntentConstants.HOUSESHARE_ID, hsid);
                    billPageIntent.putExtra(IntentConstants.BILL_ID, getParam(IntentConstants.BILL_ID));
                    startActivity(billPageIntent);
                }
            }


        }
    }

    // TODO: For now it will just display the plain response from the server
    // need updating later

    public Fragment_HS_Notification() {
        // Required empty public constructor
    }

    /**
     * interface for intereacting with the host activity
     */
    public interface OnNotificationInteraction {
        void onNewNotiReceived();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username = getArguments().getString(IntentConstants.USERNAME) != null ?
                getArguments().getString(IntentConstants.USERNAME) : username;
        hs_name = getArguments().getString(IntentConstants.HOUSE_NAME);
        hsid = getArguments().getString(IntentConstants.HOUSESHARE_ID);

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

        //set up the refresh view
        mRefreshView = (SwipeRefreshLayout) mLayoutContainer.findViewById(R.id.refresh_view_noti);
        mRefreshView.setColorSchemeColors(getResources().getColor(R.color.light_green),
                getResources().getColor(android.R.color.holo_red_light));

        mTable = (TableLayout) mLayoutContainer.findViewById(R.id.hs_notification_table);
        loading_icon = (ProgressBar) mLayoutContainer.findViewById(R.id.noti_loading_icon);
        checkEmptyNotification(this);

        return mLayoutContainer;
    }

    /**
     * Called immediately after {@link #onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     *
     * @param view               The View returned by {@link #onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("refresh", " action performed");
                refresh();
            }
        });
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
                final Connection temp_connect = new Connection(getActivity());
                // experimenting a new message box builder
                CustomMessageBox.MessageBoxBuilder builder = new CustomMessageBox.MessageBoxBuilder(getActivity(), "Your session has been timed out, please login again");
                builder.setTitle("Expired")
                        .setActionOnClick(new CustomMessageBox.ToClick() {
                            @Override
                            public void DoOnClick() {
                                temp_connect.autoLogout(username);
                            }
                        }).build();
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
                final Connection temp_connect = new Connection(getActivity());
                // experimenting a new message box builder
                CustomMessageBox.MessageBoxBuilder builder = new CustomMessageBox.MessageBoxBuilder(getActivity(), "Your session has been timed out, please login again");
                builder.setTitle("Expired")
                        .setActionOnClick(new CustomMessageBox.ToClick() {
                            @Override
                            public void DoOnClick() {
                                temp_connect.autoLogout(username);
                            }
                        }).build();
            } else if (jo.getString("status").equals("true")) {
                JSONArray Noti_js_arr = jo.getJSONArray(Responses_Format.RESPONSE_HS_CONTENT);
                for (int i = 0; i < Noti_js_arr.length(); i++) {

                    //typical response looks like this
                    // TODO _IMPORTANT! [["65","16","hs_100000","wgriffin","2015-04-11 02:38:14","0","electricity oct"]

                    JSONArray noti_arr_in = Noti_js_arr.getJSONArray(i);


                    String id = noti_arr_in.getString(0);
                    boolean read = noti_arr_in.getString(noti_arr_in.length() - 3).equals("1");
                    int type = noti_arr_in.getInt(1);
                    String source = noti_arr_in.getString(noti_arr_in.length() - 1);

                    Notification n = new Notification(type, read, id, source);
                    n.addParam(noti_arr_in.getString(Notification.HSID_POS + 2))
                            .addParam(noti_arr_in.getString(Notification.PARAM_POS + 2))
                            .addParam(noti_arr_in.getString(Notification.TIME_POS + 2))
                            .addParam(noti_arr_in.getString(Notification.PARAM2_POS + 3));

                    l.add(n);
                    Log.d("Extracted noti " + i, n.toString());
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


    /**
     * prepare the request to be sent to the server letting it know the notifications have been seen (might not yet read)
     *
     * @param f the fragment itself
     * @return the request param - use String[] instead of {@link HTTPConnect.Request} to comply with the Connection
     */
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
        Log.d("Request marked as seen", Arrays.toString(request.toArray(new String[request.size()])));
        return request.toArray(new String[request.size()]);

    }

    /**
     * The user has clicked on a notification
     *
     * @param noti_id the id of the noti read
     * @param isRead the noti has already been read?
     * @param src the target of the noti
     */
    public void onNotificationsRead(String noti_id, String src, boolean isRead, boolean isOwner) {
        // if the noti hasnt been read before, then notify the server to mark this noti as read
        // before redirecting to the target
        if (!isRead) {
            Request r = new Request(Request.TYPE.POST);
            r.addParam(Request_Params.PARAM_TYPE, Request_Params.MARK_NOTI_AS_READ)
                    .addParam(Request_Params.PARAM_USR, username)
                    .addParam(Request_Params.NOTI_ID, noti_id);
            Log.d("Request marked as read", r.toString());

            new Notification_Worker(getActivity(), true, NOTI_WORK.NOTI_READ)
                    .addNewParam(IntentConstants.BILL_ID, src)
                    .addNewParam(IntentConstants.IS_OWNER, String.valueOf(isOwner)) // god damn line took me 2 hrs
                    .setMsg("Please wait")
                    .execute(new RequestQueue().addRequest(getNotiReadMarkingRequest(noti_id)).toList());
        }
        // else if it's already read, redirect straight away to the target of the noti
        else {
            Log.d("Noti already read", noti_id);
            Intent billPageIntent = new Intent(getActivity(),
                    isOwner ? HouseShare_Bill_Owner.class : HouseShare_Bill_Member.class);
            billPageIntent.putExtra(IntentConstants.USERNAME, username);
            billPageIntent.putExtra(IntentConstants.HOUSE_NAME, hs_name);
            billPageIntent.putExtra(IntentConstants.HOUSESHARE_ID, hsid);
            billPageIntent.putExtra(IntentConstants.BILL_ID, src);
            startActivity(billPageIntent);
        }

    }


    /**
     * method performed after the user has clicked on the sub button on one of the noti_join_req row,
     * that is, either the welcome or refuse button.
     *
     * @param p    the name of the param (username who has sent the requets)
     * @param r    the response
     * @param type the type of the request (Approve or cancel)
     */
    public void onSubButtonClicked(String p, String r, NOTI_WORK type) {
        //TODO Waiting for the server to be configured for this request

        try {
            if (type != NOTI_WORK.REFUSE && type != NOTI_WORK.APPROVE)
                throw new IllegalArgumentException("type must be either APPROVE or REFUSE");
            JSONObject jo = new JSONObject(r);

            /* Check if the user has timed out */
            if (jo.getString("expired").equals("true")) {

                 /* Display message box and auto logout user */
                final Connection temp_connect = new Connection(getActivity());
                // experimenting a new message box builder
                CustomMessageBox.MessageBoxBuilder builder = new CustomMessageBox.MessageBoxBuilder(getActivity(), "Your session has been timed out, please login again");
                builder.setTitle("Expired")
                        .setActionOnClick(new CustomMessageBox.ToClick() {
                            @Override
                            public void DoOnClick() {
                                temp_connect.autoLogout(username);
                            }
                        }).build();
            } else if (jo.getString("status").equals("true")) {
                new CustomMessageBox.MessageBoxBuilder(getActivity(), "You have " + (type == NOTI_WORK.REFUSE ? "refused" : "approved")
                        + " the request of " + p.substring(0, p.length() - 1) + ".")
                        .setTitle("Confirmation").build();
            }

            /* There was an error indide the status return field, display appropriate error message */
            //TODO implement error messages
            else {
                new CustomMessageBox(getActivity(), "Sorry. We could not process your request at the moment \nIf you are experiencing this error constantly, please contact our team.");
                /* give more info on the error here, no money taken from account */
                /* Use the status results to display certain error messages */
            }
        }
        /* Catch the exceptions */ catch (JSONException jse) {
            /* Error in the JSON response */
            new CustomMessageBox(getActivity(), "There was an error in the server connection");
            Log.d("jse", jse.getMessage(), jse);

        } catch (Exception e) {
            /* Failsafe if something goes utterly wrong */
            new CustomMessageBox(getActivity(), "Something wrong happened. Try again later", "Okay", "Error");
            e.printStackTrace();
        }
    }

    /**
     * check if there is any new notification (requested by the notification fragment)
     *
     * @param f the noti fragment
     */
    public void checkEmptyNotification(Fragment_HS_Notification f) {
        Log.d("check empty noti", "abc");
        if (f.isVisible()) {
            if (mTable.getChildCount() == 0) {
                LayoutInflater i = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mTable.removeAllViews();
                TableRow v = (TableRow) i.inflate(R.layout.hs_noti_empty, null);
                v.setClickable(false);
                mTable.addView(v);
            }
        }
    }

    /**
     * Prepare the UI for the notification fragment
     */
    private void makeUI() {
        mTable.removeAllViews(); // remove all old views first
        for (int i = 0; i < data.size(); i++) {
            Log.d("data", data.get(i).getAdditional_params().get(Notification.HSID_POS));

            // initialise the row
            View v = data.get(i).makeNotiRow(getActivity());
            final Notification n = data.get(i);
            // data on row
            final String noti_id = data.get(i).getId();
            int notiType = data.get(i).getType();
            // set listeners for sub buttons in join request notis
            Log.d("Type", String.valueOf(notiType));
            switch (notiType) {
                case Notification.JOIN_ADM: {
                    //set up the welcome sub button
                    TextView welcome_button = (TextView) v.findViewById(R.id.ok);
                    welcome_button.setClickable(true);
                    welcome_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Notification_Worker(getActivity(),
                                    true, NOTI_WORK.APPROVE)
                                    .addNewParam(Notification_Worker.PARAM_USERNAME_IN_NOTI,
                                            n.getAdditional_params().get(Notification.PARAM_POS) )
                                    .setMsg("Loading")
                                    .execute(new RequestQueue()
                                            .addRequest(getMemberApprovingRequest(
                                                    n.getAdditional_params().get(Notification.PARAM_POS), noti_id))
                                            .toList());
                            TableRow t = (TableRow) v.getParent().getParent().getParent(); // looks quite odd
                            mTable.removeView(t);
                            Log.d("Approving request", getMemberApprovingRequest(n.getAdditional_params().get(Notification.PARAM_POS),
                                    noti_id).toString());

                        }
                    });

                    // set up the cancel sub button
                    TextView cancel_button = (TextView) v.findViewById(R.id.refuse);
                    cancel_button.setClickable(true);
                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Notification_Worker(getActivity(),
                                    true, NOTI_WORK.REFUSE)
                                    .addNewParam(Notification_Worker.PARAM_USERNAME_IN_NOTI,
                                            n.getAdditional_params().get(Notification.PARAM_POS) )
                                    .setMsg("Loading").execute(new RequestQueue()
                                    .addRequest(getMemberRefusingRequest
                                            (n.getAdditional_params().get(Notification.PARAM_POS),
                                                    noti_id)).toList());
                            TableRow t = (TableRow) v.getParent().getParent().getParent();
                            Log.d("Refusing request", getMemberRefusingRequest(n.getAdditional_params().get(Notification.PARAM_POS),
                                    noti_id).toString());
                            mTable.removeView(t);

                        }
                    });
                    break;
                }
                // redirect to the bill page in the following cases
                case Notification.NEW_BILL:
                case Notification.BILL_ACTIVATED: {
                    //set the src target of the notification
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onNotificationsRead(n.getId(), n.getSource(), n.isRead(), false);
                        }
                    });
                    break;
                }
                // this sort of noti is only for the bill creator, so redirect to the bill page for
                // owners
                case Notification.BILL_PAYMENT_RECEIVE:{
                    Log.d("Type", "received");
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onNotificationsRead(n.getId(), n.getSource(), n.isRead(), true);
                        }
                    });
                    break;
                }

                case Notification.BILL_PAYMENT_CONFIRMED: {
                    Log.d("Type", "confirmed");

                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onNotificationsRead(n.getId(), n.getSource(), n.isRead(), false);
                        }
                    });
                    break;
                }

                case Notification.BILL_PAYMENT_REJECTED: {
                    Log.d("Type", "rejected");

                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onNotificationsRead(n.getId(), n.getSource(), n.isRead(), false);
                        }
                    });
                    break;
                }
            }
            Log.d("data", String.valueOf(data.size()) + " i : " + i + " mTable" + mTable.getChildCount());
            if (v != null)
                mTable.addView(v, i);


        }

        // add a layout change listener to update the layout on events
        mTable.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                checkEmptyNotification(Fragment_HS_Notification.this);
            }
        });

        //initial empty check
        checkEmptyNotification(Fragment_HS_Notification.this);
    }


    /**
     * get the noti fetching request, or initialise it first if possible
     *
     * @return the initialised request
     */
    private Request getNotisFetchingRequest() {
        if (notisFetchingRequest != null)
            return notisFetchingRequest;
        else
            notisFetchingRequest = new Request(Request.TYPE.POST)
                    .addParam(Request_Params.PARAM_TYPE, Request_Params.VAL_FETCH_NOTI)
                    .addParam(Request_Params.PARAM_USR, username);
        return notisFetchingRequest;
    }


    /**
     * get the member approving request, or initialise it first if possible
     *
     * @param approvedMember the name of the member approved
     * @param noti_id        the notification id
     * @return the initialised request
     */
    private Request getMemberApprovingRequest(String approvedMember, String noti_id) {
        if (memberApprovingRequest != null)
            return memberApprovingRequest;
        else
            memberApprovingRequest = new Request(Request.TYPE.POST)
                    .addParam(Request_Params.PARAM_TYPE, Request_Params.VAL_APPROVE_MEMBER)
                    .addParam(Request_Params.PARAM_USR, username)
                    .addParam(Request_Params.VAL_APPROVE_MEMBER_PARAM, approvedMember)
                    .addParam(Request_Params.NOTI_ID, noti_id);
        return memberApprovingRequest;
    }


    /**
     * get the member refusing request, or initialise it first if possible
     *
     * @param approvedMember the name of the member approved
     * @param noti_id        the notification id
     * @return the initialised request
     */
    public Request getMemberRefusingRequest(String approvedMember, String noti_id) {
        if (memberRefusingRequest != null)
            return memberApprovingRequest;
        else
            memberRefusingRequest = new Request(Request.TYPE.POST)
                    .addParam(Request_Params.PARAM_TYPE, Request_Params.VAL_REFUSE_MEMBER)
                    .addParam(Request_Params.PARAM_USR, username)
                    .addParam(Request_Params.VAL_REFUSE_MEMBER_PARAM, approvedMember)
                    .addParam(Request_Params.NOTI_ID, noti_id);
        return memberRefusingRequest;
    }


    /**
     * get the notiReadMarkingRequest, or initialise it first if possible
     *
     * @return the initialised request
     */
    public Request getNotiReadMarkingRequest(String noti_id) {
        if (notiReadMarkingRequest != null)
            return notiReadMarkingRequest;
        else
            notiReadMarkingRequest = new Request(Request.TYPE.POST)
                    .addParam(Request_Params.PARAM_TYPE, Request_Params.MARK_NOTI_AS_READ)
                    .addParam(Request_Params.PARAM_USR, username)
                    .addParam(Request_Params.NOTI_ID, noti_id);
        return notiReadMarkingRequest;
    }
}






