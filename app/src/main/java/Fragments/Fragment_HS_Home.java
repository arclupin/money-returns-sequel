package Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ncl.team5.lloydsmockup.CustomMessageBox;
import com.ncl.team5.lloydsmockup.HouseShare_Bill;
import com.ncl.team5.lloydsmockup.Houseshares.Bill;
import com.ncl.team5.lloydsmockup.Houseshares.Member;
import com.ncl.team5.lloydsmockup.IntentConstants;
import com.ncl.team5.lloydsmockup.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import HTTPConnect.ConcurrentConnection;
import HTTPConnect.Connection;
import HTTPConnect.Request;
import HTTPConnect.RequestQueue;
import HTTPConnect.Request_Params;
import HTTPConnect.Response;
import HTTPConnect.Responses_Format;
import Utils.StringUtils;

/**
 * Fragment for displaying the home view in the home view activity <br/>
 * (another fragment that would be invoked in this activity is the notification fragment
 *
 */
public class Fragment_HS_Home extends Fragment_HS_Abstract {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static String username;
    private static String hs_name;
    private static String view_type;
    private static List<Bill> billList;


    private String response_content;
    private TextView viewName;
    private TextView viewAddressText;
    private TextView viewDescription;
    private RelativeLayout l;
    private RelativeLayout main_view_container;
    private ProgressBar loadingIcon;
    private ImageView avatar;
    private TextView loading_list_text;
    private RelativeLayout empty_view;

    ListView billList_view;
    private Map<String, Member> members = new TreeMap<String, Member>();

    // user has sent a request or not
    private enum pending_mode {SENT, NOT_SENT};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().getString(IntentConstants.USERNAME) != null)
            username = getArguments().getString(IntentConstants.USERNAME);
        if (getArguments().getString(IntentConstants.HOUSE_NAME) != null)
            hs_name = getArguments().getString(IntentConstants.HOUSE_NAME);
        if (getArguments().getString(IntentConstants.HOME_VIEW_TYPE) != null)
           view_type = getArguments().getString(IntentConstants.HOME_VIEW_TYPE);

        billList = new ArrayList<Bill>();
        Log.d("onCreate home_view", hs_name + " - " + view_type + " - " + username);

    }


    private OnFragmentInteractionListener_HomeView mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_HS_Home newInstance(String username, String house_name, String view_type) {
        Fragment_HS_Home fragment = new Fragment_HS_Home();
        Bundle args = new Bundle();
        args.putString(IntentConstants.USERNAME, username);
        args.putString(IntentConstants.HOUSE_NAME, house_name);
        args.putString("TYPE", view_type);
        fragment.setArguments(args);
        return fragment;
    }

    public Fragment_HS_Home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        l  = (RelativeLayout) inflater.inflate(R.layout.fragment_home_view, container, false);

         main_view_container = (RelativeLayout) l.findViewById(R.id.home_view_main_container);
         avatar = (ImageView) l.findViewById(R.id.group_avatar);
         viewName = (TextView) l.findViewById(R.id.viewName);
         viewAddressText = (TextView) l.findViewById(R.id.viewAddressText);
         viewDescription = (TextView) l.findViewById(R.id.viewAddress);
         loadingIcon = (ProgressBar) l.findViewById(R.id.progressBar);
         loading_list_text = (TextView) l.findViewById(R.id.loading_text);
         empty_view = (RelativeLayout) l.findViewById(R.id.bill_view_empty_row);
                initialiseData();
        // if user has joined some house then show it at home page
        if (view_type.equals(Responses_Format.RESPONSE_HOUSESHARE_JOINED_HOUSE)) {
            //TODO real bills come here
        /* Get the list view */
            billList_view = (ListView) l.findViewById(R.id.listBills);
            billList_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(getActivity(), HouseShare_Bill.class);
                    startActivity(i);
                }
            });

        }

        // else if user has sent a request then show the sent-request prompt in the home view
        else if (view_type.equals(Responses_Format.RESPONSE_HOUSESHARE_SENT_REQ)) {
                displayPendingContent("You have sent a request to this house.\n" +
                        "The admin is considering your request.\nHang in there :)", pending_mode.SENT);

        }

        // else show them a prompt for searching for a house
        else if (view_type.equals(Responses_Format.RESPONSE_HOUSESHARE_JOINED_SERVICE)) {
            displayPendingContent("You have not joined any house.\nUse the search bar below to start looking for one!", pending_mode.NOT_SENT);

        }

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

    @Override
    public void update() {
        // only update date when the user has joined a house
        // because the view of the other type (sent-request type) need not updating
        if (view_type.equals(Request_Params.VAL_HS_JOIN_GROUP))
             initialiseData();
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
        public void onHomeViewCreated(Fragment_HS_Home f);
    }

    public class HomeViewWorker extends ConcurrentConnection {

        //order of data in response
        private static final int BILL_ID_POS = 0;
        private static final int BILL_CREATOR_ID = 1; // houseshare id
        private static final int BILL_GROUP_NAME_POS = 2;
        private static final int BILL_NAME_POS = 3;
        private static final int BILL_DATE_CREATED_POS = 4;
        private static final int BILL_AMOUNT_POS = 5;
        private static final int BILL_DUE_DATE_POS = 6;
        private static final int BILL_DATE_PAID_POS = 7;
        private static final int BILL_MESSAGE_POS = 8;
        private static final int BILL_ISACTIVE_POS = 9;





        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIcon.setVisibility(View.VISIBLE);
            loading_list_text.setVisibility(View.VISIBLE);
        }

        public HomeViewWorker(Activity a, boolean showDialog) {
            super(a, showDialog);
        }

        @Override
        protected void onPostExecute(List<Response> responses) {
            super.onPostExecute(responses);
            loadingIcon.setVisibility(View.GONE);
            loading_list_text.setVisibility(View.GONE);
            response_content = processInfo(responses.get(0).getRaw_response()); // do the processing in the home view

            // might need a UI child thread worker for all these data
            checkNewNotification(responses.get(1).getRaw_response()); // check for new notification and reflect it on the noti icon
            filterMembers(responses.get(2).getRaw_response());
            Log.d("fetch_bill_response", responses.get(3).getRaw_response());
            extractBills(responses.get(3).getRaw_response());
            displayHomeViewContent();
           }
    }

    // method called when the fragment is called.
    protected String processInfo(String response) {
        String content = "DEFAULT";
        try {

            JSONObject jo = new JSONObject(response);

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
            } else {
//               TextView tv = (TextView) findViewById(R.id.hs_hv_response);
                content = jo.getString(Responses_Format.RESPONSE_HS_CONTENT); //TODO
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
        return content;
    }

    /**
     * called to set the home view content, provided that user has joined a house
     */
    public void displayHomeViewContent() {
        try {
            JSONArray house_Array = new JSONObject(response_content).getJSONArray("basic_info");
            viewName.setText(house_Array.getString(0));
            viewAddressText.setText(StringUtils.implode(" ", house_Array.getString(1), house_Array.getString(2), house_Array.getString(3)));
            viewDescription.setText(house_Array.getString(house_Array.length() - 1));
            avatar.setImageDrawable(getResources().getDrawable(R.drawable.boy));
            BillAdapter adapter = new BillAdapter(getActivity(),R.layout.bill_view_row, billList );
            billList_view.setAdapter(adapter);

            if (billList.size() == 0)
            {
               empty_view.setVisibility(View.VISIBLE);
            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * called when either the user has sent a request to some house but yet to receive the response
     * or the user has not joined any house as well as not sent out any request
     */
    private void displayPendingContent(String prompt, pending_mode mode) {
        l.setBackgroundColor(getResources().getColor(R.color.hs_home_bg_light));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(0, 200, 0, 0); // move the whole thing a bit up
        main_view_container.setLayoutParams(params);
        main_view_container.requestLayout();


        if (mode == pending_mode.SENT) {
            viewName.setText(hs_name);
            viewAddressText.setPadding(20, 20, 20, 20);
        }
        else {
            RelativeLayout.LayoutParams margins = (RelativeLayout.LayoutParams) viewAddressText.getLayoutParams();
            margins.setMargins(0, -40, 0, 0);
            viewAddressText.setLayoutParams(margins);

            viewAddressText.setLineSpacing(0, 1.3f);
            viewAddressText.requestLayout();
        }
        viewAddressText.setText(prompt);
        avatar.setImageDrawable(getResources().getDrawable( mode == pending_mode.SENT ? R.drawable.lock : R.drawable.sad));
    }

//    /**
//     * called to set the pending page, provided that user has sent a request
//     */
//    public void displayPendingContent() {
//        avatar.setImageDrawable(getResources().getDrawable(R.drawable.boy));
//    }

    /**
     * Initialise data
     */
    private void initialiseData() {

        // network connection starts to get heavy weight
        // home view requires at least 4 requests to be initialised

        // get house data
        Request home_feed_request = new Request(Request.TYPE.POST);
        home_feed_request.addParam(Request_Params.PARAM_TYPE, Request_Params.VAL_HS_2_FETCH_HOUSE_DETAIL)
                .addParam(Request_Params.PARAM_USR, username);

        // get noti data
        Request new_noti_check_request = new Request(Request.TYPE.POST);
        new_noti_check_request.addParam(Request_Params.PARAM_TYPE, Request_Params.VAL_REF_NOTI)
                .addParam(Request_Params.PARAM_USR, username);

        // get members data
        Request fetch_members_request = new Request(Request.TYPE.POST);
        fetch_members_request.addParam(Request_Params.PARAM_TYPE, Request_Params.HS_ALL_MEMBERS)
                .addParam(Request_Params.PARAM_USR, username);

        // get bills data
        Request fetch_bills_request = new Request(Request.TYPE.POST);
        fetch_bills_request.addParam(Request_Params.PARAM_TYPE, Request_Params.REQUEST_HS_GET_MY_BILLS)
                .addParam(Request_Params.PARAM_USR, username);

        new HomeViewWorker(getActivity(), false)
                .execute(new RequestQueue().addRequests(home_feed_request, new_noti_check_request, fetch_members_request, fetch_bills_request).toList());

    }

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
                ((Fragment_HS_Notification.OnNotificationInteraction) getActivity()).onNewNotiReceived(); // risky ~
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

    public void extractBills(String r) {
        try {

    /* Returns: JSON String */
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
            } else {
                JSONArray arr_out = jo.getJSONArray(Responses_Format.RESPONSE_HS_CONTENT);
                for (int i = 0; i < arr_out.length(); i++) {
                    //["hs_100003_b1","hs_100003","Stella house","wg","2015-04-11","154","1995-01-23","",null,"0"]
                    JSONArray bill_arr = arr_out.getJSONArray(i);

                    //initialise the bill being extracted from the response
                    billList.add(new Bill.BillBuilder(bill_arr.getInt(HomeViewWorker.BILL_ISACTIVE_POS) == 1,
                            !StringUtils.isFieldEmpty(bill_arr.getString(HomeViewWorker.BILL_DATE_PAID_POS)))
                            .setAmount(bill_arr.getDouble(HomeViewWorker.BILL_AMOUNT_POS))
                            .setBillCreator(members.get(bill_arr.getString(HomeViewWorker.BILL_CREATOR_ID)))
                            .setBillID(bill_arr.getString(HomeViewWorker.BILL_ID_POS))
                            .setBillName(bill_arr.getString(HomeViewWorker.BILL_NAME_POS))
                            .setDateCreated(StringUtils.getDateFromServerDateResponse(bill_arr.getString(HomeViewWorker.BILL_DATE_CREATED_POS)))
                            .setDueDate(StringUtils.getDateFromServerDateResponse(bill_arr.getString(HomeViewWorker.BILL_DUE_DATE_POS)))
                            .setMessage(bill_arr.getString(HomeViewWorker.BILL_MESSAGE_POS)).build());

                    Log.d("Bill after extraction" + i, billList.get(i).toString() );
                }
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

    private void filterMembers(String r) {
        try {
            JSONObject j = new JSONObject(r);
            if (j.getString("expired").equals("true")) {

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
                return;
            }
            JSONArray arr_out = j.getJSONArray(Responses_Format.RESPONSE_MEMBERS);
            for (int i = 0; i < arr_out.length(); i++) {
                JSONArray arr_in = arr_out.getJSONArray(i);
                members.put(arr_in.getString(0), new Member(arr_in.getString(0), arr_in.getString(1), arr_in.getString(2)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getHouseshareID(String username) {
        String id = null;
        for (Map.Entry<String, Member> entry : members.entrySet())
            if (entry.getValue().getUsername().equals(username.trim()))
                return entry.getKey();
        return id;
    }

    class BillAdapter extends ArrayAdapter<Bill> {
        private List<Bill> billList; // the backing list

        public BillAdapter(Context context, int resource, List<Bill> objects) {
            super(context, resource, objects);
            billList = objects;

//            // if no bill is specified, add one empty bill
//            // this would help the list view display at least 1 view
//            // which will be configured to be an row indicating no bills are available
//            if (billList.size() == 0) {
//
//                billList.add(Bill.getEmptyInstance());
//                Log.d("bill list is empty", billList.get(0).getBillID());
//            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            empty_view.setVisibility(View.GONE);
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.bill_view_row, null);
                TextView billName = (TextView) view.findViewById(R.id.bill_row_bill_name);
                TextView billInfo = (TextView) view.findViewById(R.id.bill_row_bill_info);
                // get the backing bill at this position
                Bill bill = billList.get(position);
                billName.setText(bill.getBillName());
                // set the bill info depending on the status of the bill
                if (!bill.isActive()) {
                    if (!bill.getBillCreator().getUsername().equals(username))
                        billInfo.setText("Created by " + bill.getBillCreator().getUsername() + ". Status: Pending");
                    else
                        billInfo.setText("You created this bill. Status: Pending");
                }
                else {
                    if (!bill.isPaid()) {
                        billInfo.setText("This bill is due " + StringUtils.getGeneralDateString(bill.getDueDate()) + ".");
                    }
                    else {
                        billInfo.setText("This bill has been paid on " + StringUtils.getGeneralDateString(bill.getDatePaid()) + ".");
                    }
                }
            return view;
        }
    }



}