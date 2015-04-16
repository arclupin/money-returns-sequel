package Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ncl.team5.lloydsmockup.CustomMessageBox;
import com.ncl.team5.lloydsmockup.HouseShare_Bill_Member;
import com.ncl.team5.lloydsmockup.HouseShare_Bill_Owner;
import com.ncl.team5.lloydsmockup.Houseshares.Bill;
import com.ncl.team5.lloydsmockup.Houseshares.Member;
import com.ncl.team5.lloydsmockup.IntentConstants;
import com.ncl.team5.lloydsmockup.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import HTTPConnect.ConcurrentConnection;
import HTTPConnect.Connection;
import HTTPConnect.Request;
import HTTPConnect.RequestQueue;
import HTTPConnect.Request_Params;
import HTTPConnect.Response;
import HTTPConnect.Responses_Format;
import Utils.StringUtils;
import Utils.Utilities;

/**
 * Fragment for displaying the home view in the home view activity <br/>
 * (another fragment that would be invoked in this activity is the notification fragment
 *
 */
public class Fragment_HS_Home extends Fragment_HS_Abstract {

    private static String username;
    private static String hs_name;
    private static String hsid;
    // the view type of the home view (Each type of view applies each type of users whose states
    //are different
    private static String view_type;
    private static boolean loaded = false;

    private String response_content;
    //backing map that stores the details of members of the group the user is in.
    public static Map<String, Member> members = new TreeMap<String, Member>();
    // backing list of bills of the group
    private static List<Bill> billList;

    //Views
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



    /**
     * Enum types of users have not joined a group (since we can't really show them any group detail
     * except the group name so they need to be treated differently from those who have fully joined
     * joined some group. This is also the purpose of the view_type variable declared above  <br/>
     * Basically there are 2 types of such users, the first type consists users that has not sent out
     * any request at all, they are mainly users who have registered for the service but have yet to
     * send out any request. <br/>
     * The other type of user here is ones that have sent out join requests to their chosen group and
     * currently are waiting for replies from their group admins. (Only the admin in a house can see
     * who have sent the request to his house and decide what to do with those requests (approve or
     * refuse). With these users, they will see a page indicating that the request has been sent (like
     * a pending page). They can also change their mind to join another group provided that they need
     * to cancel the current request first. (This is forced by the app so that one user cannot have 2
     * pending requests at any time.
     *
     *
    */
    private enum pending_mode {SENT, NOT_SENT}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaded = false;

        //get arguments from the intent
        if (getArguments().getString(IntentConstants.USERNAME) != null)
            username = getArguments().getString(IntentConstants.USERNAME);
        if (getArguments().getString(IntentConstants.HOUSE_NAME) != null)
            hs_name = getArguments().getString(IntentConstants.HOUSE_NAME);
        if (getArguments().getString(IntentConstants.HOME_VIEW_TYPE) != null)
           view_type = getArguments().getString(IntentConstants.HOME_VIEW_TYPE);
        if (getArguments().getString(IntentConstants.HOUSESHARE_ID) != null)
           hsid = getArguments().getString(IntentConstants.HOUSESHARE_ID);

        //initialise the backing bill list
        billList = new ArrayList<Bill>();
        Log.d("onCreate home_view_frag", hs_name + " - " + view_type + " - " + username);
    }


    // Event listener in this fragment. Must be implemented by the hosting activity in order to
    // allow proper communications between the hosting activity and this fragment
    private OnFragmentInteractionListener_HomeView mListener;

    /**
     * Factory method for creating a new instance of this fragment
     * @param username the username
     * @param house_name the group name
     * @param view_type the type of the home view depending on the type of user
     * @return a new instance of this fragment
     */
    public static Fragment_HS_Home newInstance(String username, String hsid, String house_name, String view_type) {
        Fragment_HS_Home fragment = new Fragment_HS_Home();
        Bundle args = new Bundle();
        //set necessary arguments
        args.putString(IntentConstants.USERNAME, username);
        args.putString(IntentConstants.HOUSESHARE_ID, hsid);
        args.putString(IntentConstants.HOUSE_NAME, house_name);
        args.putString(IntentConstants.HOME_VIEW_TYPE, view_type);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Empty constructor
     */
    public Fragment_HS_Home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        l  = (RelativeLayout) inflater.inflate(R.layout.fragment_home_view, container, false);

        // get the views of the layout
         main_view_container = (RelativeLayout) l.findViewById(R.id.home_view_main_container);
         avatar = (ImageView) l.findViewById(R.id.group_avatar);
         viewName = (TextView) l.findViewById(R.id.viewName);
         viewAddressText = (TextView) l.findViewById(R.id.viewAddressText);
         viewDescription = (TextView) l.findViewById(R.id.viewAddress);
         loadingIcon = (ProgressBar) l.findViewById(R.id.progressBar);
         loading_list_text = (TextView) l.findViewById(R.id.loading_text);
         empty_view = (RelativeLayout) l.findViewById(R.id.bill_view_empty_row);

        // initialise data including fetching data from server, updating backing data, and preparing
        // the UI
        initialiseData();

        // if user has joined some house then the list view for the bills is enabled and configured
        if (view_type.equals(Responses_Format.RESPONSE_HOUSESHARE_JOINED_HOUSE)) {
        /* Get the list view */
            billList_view = (ListView) l.findViewById(R.id.listBills);
        }

        // else if the user has sent a request then show the sent-request prompt in the home view
        else if (view_type.equals(Responses_Format.RESPONSE_HOUSESHARE_SENT_REQ)) {
            displayPendingContent("You have sent a request to this house.\n" +
                        "The admin is considering your request.\nHang in there :)", pending_mode.SENT);
        }

        // else (the user is new to the service or does not have any request sent)
        // show them a prompt for searching for a house
        else if (view_type.equals(Responses_Format.RESPONSE_HOUSESHARE_JOINED_SERVICE)) {
            displayPendingContent("You have not joined any house.\nUse the search bar below to start looking for one!",
                    pending_mode.NOT_SENT);
        }

        return l;
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * tied to {@link Activity#onResume() Activity.onResume} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();
        initialiseData();
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

    /**
     * Objects of Class HomeViewWorker are responsible for going out and fetch the data from the
     * server. Because the amount of network connections is much greater than as with the banking
     * section, it subclass ConcurrentConnection in stead of {@link Connection} to ensure that
     * multiple requests can be processed faster and more efficient.
     */
    public class HomeViewWorker extends ConcurrentConnection {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            billList = new ArrayList<Bill>();
            //show loading indicators
            Log.d("Loaded", String.valueOf(loaded));
           if (!loaded) {
               loadingIcon.setVisibility(View.VISIBLE);
               loading_list_text.setVisibility(View.VISIBLE);
               loaded = true;
           }

        }

        public HomeViewWorker(Activity a, boolean showDialog) {
            super(a, showDialog);
        }

        @Override
        protected void onPostExecute(List<Response> responses) {
            super.onPostExecute(responses);
            loadingIcon.setVisibility(View.GONE);
            loading_list_text.setVisibility(View.GONE);
            // do the processing in the home view
            response_content = processInfo(responses.get(0).getRaw_response());
            //TODO we might need a UI child thread worker for all these data
            // check for new notification and reflect it on the noti icon
            checkNewNotification(responses.get(1).getRaw_response());

            // update the backing map of members
            filterMembers(responses.get(2).getRaw_response());

            Log.d("fetch_bill_response", responses.get(3).getRaw_response());
            // update the baacking list of bills
            // TODO _FIXME this only happens when user type if full-joined
            extractBills(responses.get(3).getRaw_response());

            // prepare the UI content
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
                Utilities.showAutoLogoutDialog(getActivity(), username);
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
            viewAddressText.setText(StringUtils.implode(" ", house_Array.getString(1),
                    house_Array.getString(2), house_Array.getString(3)));
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

        // prepare the layout positions
        l.setBackgroundColor(getResources().getColor(R.color.hs_home_bg_light));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        // move the whole thing a bit up
        params.setMargins(0, 200, 0, 0);
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

        //display the prompt
        viewAddressText.setText(prompt);
        //display the description image above the prompt
        avatar.setImageDrawable(getResources().getDrawable( mode == pending_mode.SENT ?
                R.drawable.lock : R.drawable.sad));
    }

//    /**
//     * called to set the pending page, provided that user has sent a request
//     */
//    public void displayPendingContent() {
//        avatar.setImageDrawable(getResources().getDrawable(R.drawable.boy));
//    }

    /**
     * Initialise data
     * This consists of multiple stages
     * 1. Establish connection the server
     * 2. Fetch data required from the server
     * 3. Process those data, apply security filtering (such as timeout checker etc.)
     * 4. Update data backing fields.
     * 5. Preparing UI content
     */
    private void initialiseData() {

        // network connection starts to get heavy weight
        // home view requires at least 4 requests to be initialised

        // get house data
        Request home_feed_request = new Request(Request.TYPE.POST);
        home_feed_request.addParam(Request_Params.PARAM_TYPE, Request_Params.VAL_HS_2_FETCH_HOUSE_DETAIL)
                .addParam(Request_Params.PARAM_USR, username);

        // get new noti info
        // This does not fetch the new notifications in case they are available
        // because that's not the job of this fragment's worker. It's the Noti fragment worker job that
        // is to fetch the notifications and process them.
        // These two fragment workers are working at the same time on activity creation though.
        Request new_noti_check_request = new Request(Request.TYPE.POST);
        new_noti_check_request.addParam(Request_Params.PARAM_TYPE, Request_Params.VAL_REF_NOTI)
                .addParam(Request_Params.PARAM_USR, username);

        // get members data
        //TODO _FIXME Never send this request when the user has not joined any group [FIXED]
        Request fetch_members_request = new Request(Request.TYPE.POST);
        fetch_members_request.addParam(Request_Params.PARAM_TYPE, Request_Params.HS_ALL_MEMBERS)
                .addParam(Request_Params.PARAM_USR, username);

        // get bills data
        //TODO _FIXME Never send this request when the user has not joined any group [FIXED]
        Request fetch_bills_request = new Request(Request.TYPE.POST);
        fetch_bills_request.addParam(Request_Params.PARAM_TYPE, Request_Params.REQUEST_HS_GET_MY_BILLS)
                .addParam(Request_Params.PARAM_USR, username);
        if (!StringUtils.isFieldEmpty(hs_name) && view_type.equals(Responses_Format.RESPONSE_HOUSESHARE_JOINED_HOUSE)) {
            new HomeViewWorker(getActivity(), false)
                    .execute(new RequestQueue().addRequests(home_feed_request, new_noti_check_request,
                            fetch_members_request, fetch_bills_request).toList());
        }
    }

    /**
     * Method for checking if there is any new notification available to display
     *
     * @param r the response from the server regarding the new notification checking section
     */
    public void checkNewNotification(String r) {
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

    /**
     * Update the backing listof bills
     * @param r the response from the server regarding the bill section
     */
    public void extractBills(String r) {
        try {
            // make sure old data is erased
            billList = new ArrayList<Bill>();
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
                    billList.add(new Bill.BillBuilder(bill_arr.getInt(HouseShare_Bill_Member.BILL_ISACTIVE_POS) == 1,
                            !StringUtils.isFieldEmpty(bill_arr.getString(HouseShare_Bill_Member.BILL_DATE_PAID_POS)),
                            bill_arr.getBoolean(HouseShare_Bill_Member.BILL_AM_I_CREATOR))
                            .setAmount(bill_arr.getDouble(HouseShare_Bill_Member.BILL_AMOUNT_POS))
                            .setBillCreator(members.get(bill_arr.getString(HouseShare_Bill_Member.BILL_CREATOR_ID)))
                            .setBillID(bill_arr.getString(HouseShare_Bill_Member.BILL_ID_POS))
                            .setBillName(bill_arr.getString(HouseShare_Bill_Member.BILL_NAME_POS))
                            .setDateCreated(StringUtils.getDateFromServerDateResponse(
                                            bill_arr.getString(HouseShare_Bill_Member.BILL_DATE_CREATED_POS)))
                            .setDueDate(StringUtils.getDateFromServerDateResponse
                                            (bill_arr.getString(HouseShare_Bill_Member.BILL_DUE_DATE_POS)))
                            .setMessage(bill_arr.getString(HouseShare_Bill_Member.BILL_MESSAGE_POS)).build());

                    Log.d("Bill after extraction" + i, billList.get(i).toString() );
                }
            }

        }
        /* Catch the exceptions */
        catch (JSONException jse) {
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
     * update the backing member map
     * @param r the response regarding the member section
     */
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
                // add next member to the map
                members.put(arr_in.getString(0), new Member(arr_in.getString(0), arr_in.getString(1), arr_in.getString(2)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * get the houseshare id - an type of id used to uniquely identify a user of the houseshare
     * service based on his username. This is not visible to service users. They will just see their
     * username as the only form of uniqueness during their use.
     *
     * @param username the username of this person
     * @return the assigned houseshare id
     */
    private String getHouseshareID(String username) {
        String id = "";
        for (Map.Entry<String, Member> entry : members.entrySet())
            if (entry.getValue().getUsername().equals(username.trim()))
                return entry.getKey();
        return id;
    }


    /**
     * A custom adapter for backing the list of bills
     */
    class BillAdapter extends ArrayAdapter<Bill> {
        // the backing list
        private List<Bill> billList;

        public BillAdapter(Context context, int resource, List<Bill> objects) {
            super(context, resource, objects);
            billList = objects;

            /*  //TODO _FIXME This was the original implementation of the empty view. But I got some
                //TODO _FIXME problems with positioning the layout. This approach seems safer than the
                //TODO _FIXME current approach though.

                // if no bill is specified, add one empty bill
                // this would help the list view display at least 1 view
                // which will be configured to be an row indicating no bills are available
                if (billList.size() == 0) {

                    billList.add(Bill.getEmptyInstance());
                    Log.d("bill list is empty", billList.get(0).getBillID());
                }
            */
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            empty_view.setVisibility(View.GONE);

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.bill_view_row, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent billPageIntent = new Intent(getActivity(),
                            (billList.get(position).amICreator() ? HouseShare_Bill_Owner.class :
                                    HouseShare_Bill_Member.class));
                    billPageIntent.putParcelableArrayListExtra(IntentConstants.BILL_PARCEL,
                            new ArrayList<Parcelable>(Collections.singletonList(billList.get(position)))) ;
                    billPageIntent.putExtra(IntentConstants.USERNAME, username);
                    billPageIntent.putExtra(IntentConstants.HOUSE_NAME, hs_name);
                    billPageIntent.putExtra(IntentConstants.HOUSESHARE_ID, hsid);
                    // just to make sure :)
                    billPageIntent.putExtra(IntentConstants.BILL_ID, billList.get(position).getBillID());
                    startActivity(billPageIntent);
                }
            });
                TextView billName = (TextView) view.findViewById(R.id.bill_row_bill_name);
                TextView billInfo = (TextView) view.findViewById(R.id.bill_row_bill_info);

                // get the backing bill at this position
                Bill bill = billList.get(position);

                // set the name of the bill
                billName.setText(bill.getBillName());

                // set the bill info depending on the status of the bill
                if (!bill.isActive()) {
                    // check whether the user is the creator of this bill and display accordingly
                    // because we wouldn't want the user, say, alice to see something like
                    // "Created by alice on ...".
                    // Something like "You created this bill ..." looks more natural in this case
                    if (!bill.amICreator())
                        billInfo.setText("Created by " +
                                bill.getBillCreator().getUsername() + ". Status: Pending");
                    else
                        billInfo.setText("You created this bill. Status: Pending");
                }
                else {
                    // if this bill is not fully paid, the the bill info section will show its due date
                    if (!bill.isPaid()) {
                        billInfo.setText("This bill is due " +
                                StringUtils.getGeneralDateString(bill.getDueDate()) + ".");
                    }
                    // else if this bill has been paid completed and done, show its date of payment
                    else {
                        billInfo.setText("This bill has been paid on " +
                                StringUtils.getGeneralDateString(bill.getDatePaid()) + ".");
                    }
                }
            return view;
        }
    }

}
//END CLASS FRAGMENT_HS_HOME
