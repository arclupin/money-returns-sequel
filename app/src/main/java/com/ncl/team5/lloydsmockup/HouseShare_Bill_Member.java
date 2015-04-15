package com.ncl.team5.lloydsmockup;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ncl.team5.lloydsmockup.Houseshares.Bill;
import com.ncl.team5.lloydsmockup.Houseshares.Event;
import com.ncl.team5.lloydsmockup.Houseshares.Member;
import com.ncl.team5.lloydsmockup.Houseshares.Payment;
import com.ncl.team5.lloydsmockup.Houseshares.SubBill;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Fragments.Fragment_HS_Home;
import Fragments.HS_Bill_Delete_Dialog;
import Fragments.HS_Bill_Message_Dialog;
import Fragments.HS_Bill_Participants_Dialog;
import Fragments.HS_Bill_Primary_Action_Dialog;
import HTTPConnect.ConcurrentConnection;
import HTTPConnect.Request;
import HTTPConnect.RequestQueue;
import HTTPConnect.Request_Params;
import HTTPConnect.Response;
import HTTPConnect.Responses_Format;
import Utils.StringUtils;
import Utils.Utilities;


public class HouseShare_Bill_Member extends Activity implements HS_Bill_Delete_Dialog.BillDeleteDialogListener,
        HS_Bill_Primary_Action_Dialog.BillPrimaryActionDialogListener {

    //order of data in response
    public static final int BILL_ID_POS = 0;
    public static final int BILL_CREATOR_ID = 1; // houseshare id
    public static final int BILL_GROUP_NAME_POS = 2;
    public static final int BILL_NAME_POS = 3;
    public static final int BILL_DATE_CREATED_POS = 4;
    public static final int BILL_AMOUNT_POS = 5;
    public static final int BILL_DUE_DATE_POS = 6;
    public static final int BILL_DATE_PAID_POS = 7;
    public static final int BILL_MESSAGE_POS = 8;
    public static final int BILL_ISACTIVE_POS = 9;
    public static final int BILL_AM_I_CREATOR = 10;

    private static final int SUBBILL_HSID_POS = 0;
    private static final int SUBBILL_BILL_ID_POS = 1;
    private static final int SUBBILL_AMOUNT_POS = 2;
    private static final int SUBBILL_DATE_CREATED_POS = 3;
    private static final int SUBBILL_DUE_DATE_POS = 4;
    private static final int SUBBILL_DATE_PAID_POS = 5;
    private static final int SUBBILL_IS_ACTIVE_POS = 6;
    private static final int SUBBILL_IS_CONFIRMED = 7;

    private static final int EVENT_ID_POS = 0;
    private static final int EVENT_TYPE_POS = 1;
    private static final int EVENT_BILL_ID_POS = 2;
    private static final int EVENT_DATE_POS = 4;
    private static final int EVENT_SRC_POS = 3;

    private static final int PAYMENT_HSID_POS = 0;
    private static final int PAYMENT_BILL_ID_POS = 1;
    private static final int PAYMENT_AMOUNT_POS = 2;
    private static final int PAYMENT_DATE_SUBMITTED_POS = 3;
    private static final int PAYMENT_DATE_PAID_POS = 4;
    private static final int PAYMENT_STATUS_POS = 5;
    private static final int PAYMENT_PAYMENT_METHOD_POS = 6;
    private static final int PAYMENT_PAYMENT_MESSAGE_POS = 7;


    private ActionBar actionBar;
    private TextView billName_TextView;
    private TextView billCreationDetails_TextView;
    private TextView billStatus_TextView;
    private TextView billAmount_TextView;
    private ImageView mainAction_ImageView;
    private TextView unactivatedText_TextView;
    private TextView timelineHolder_TextView;
    private RelativeLayout basicInfoContainer;
    private LinearLayout message_View;
    private LinearLayout primaryAction_View;
    private LinearLayout participants_View;
    private LinearLayout deleteBill_View;
    private TableLayout eventsTable;
    private SwipeRefreshLayout billRefresh_SwipeView;


    // the name to be dislayed (not necessarily be the full name
    // as the name might be trimmed if there is not enough space
    private String billCreator_Name_Display;
    private Bill bill;

    private Intent i;

    private String username;
    private String hs_name;
    private String hsid;

    /**
     * User confirms that he wants to delete this bill
     * Only creators have this permission
     *
     * @param f      the dialog itself (for dismission)
     * @param billID the id of the bill
     */
    @Override
    public void onButtonClickDeleteBill(HS_Bill_Delete_Dialog f, String billID) {
        //TODO server connection stuff
    }

    /**
     * User confirms this bill or activate this bill (in case of bill owners)
     *
     * @param f    the dialog itself (for dismissing the bill)
     * @param bill the bill to be confirmed
     */
    @Override
    public void onButtonClickConfirmBill(HS_Bill_Primary_Action_Dialog f, Bill bill) {
        f.dismiss();
        Request r = new Request(Request.TYPE.POST);
            r.addParam(Request_Params.PARAM_TYPE, Request_Params.REQUEST_HS_CONFIRM_SUB_BILL)
                    .addParam(Request_Params.REQUEST_HS_BILL_ID, bill.getBillID())
                    .addParam(Request_Params.PARAM_USR, username);
            new Bill_Worker(this, true, MODE.BILL_CONFIRM)
                    .setMsg("Confirming your bill").execute(new RequestQueue().addRequest(r).toList());
        }

    public static enum MODE {
        BILL_FETCH_MAIN, BILL_CONFIRM, BILL_EDIT, BILL_DELETE, BILL_FETCH_EVENTS, BILL_REFRESH,
        BILL_FETCH_PAYMENTS
    }

    private Request subBillsFetchingRequest;
    private Request eventsFetchingRequest;
    private Request billFetchingRequest;
    private Request paymentsFetchingRequest;

    /**
     * Gets data from the server and UI preparation
     */
    class Bill_Worker extends ConcurrentConnection {
        private MODE mode;

        public Bill_Worker(Activity a, MODE mode) {
            super(a);
            this.mode = mode;
        }

        /**
         * Constructor #2. <br/>
         *
         * @param a          the calling activity
         * @param showDialog whether or not to show the progress dialog while doing computation
         */
        public Bill_Worker(Activity a, boolean showDialog, MODE mode) {
            super(a, showDialog);
            this.mode = mode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mode == MODE.BILL_FETCH_MAIN || mode == MODE.BILL_REFRESH)
                bill = null;
            if (mode == MODE.BILL_REFRESH && !billRefresh_SwipeView.isRefreshing()) {
                billRefresh_SwipeView.setRefreshing(true);
            }
        }

        @Override
        protected void onPostExecute(List<Response> responses) {
            super.onPostExecute(responses);
            switch (mode) {
                case BILL_FETCH_MAIN:
                case BILL_REFRESH: {
                    Response r = responses.get(3);
                    // check for expiration of the latest response
                    if (r.getToken(Responses_Format.RESPONSE_EXPIRED).equals("true")) {
                        Utilities.showAutoLogoutDialog(HouseShare_Bill_Member.this, username);
                    } else {
                        //update the backing bill with fetched sub bills
                        filterBill(responses.get(0).getToken(Responses_Format.RESPONSE_HS_CONTENT));
                        filterSubBills(responses.get(1).getToken(Responses_Format.RESPONSE_HS_CONTENT));
                        filterPayments(responses.get(2).getToken(Responses_Format.RESPONSE_STATUS));
                        filterEvents(responses.get(3).getToken(Responses_Format.RESPONSE_STATUS));

                        requestLayout();
                        if (mode == MODE.BILL_REFRESH || billRefresh_SwipeView.isRefreshing())
                            billRefresh_SwipeView.setRefreshing(false);
                    }

                    break;
                }
                case BILL_CONFIRM: {
                    Response response = responses.get(0);
                    if (response.getToken(Responses_Format.RESPONSE_EXPIRED).equals("true")) {
                        Utilities.showAutoLogoutDialog(HouseShare_Bill_Member.this, username);
                    } else {
                        if (response.getToken(Responses_Format.RESPONSE_STATUS).equals("true")) {
                            new CustomMessageBox.MessageBoxBuilder
                                    (HouseShare_Bill_Member.this,
                                            "You have confirmed your share.\nPlease wait until " +
                                                    "other members have confirmed theirs.")
                                    .setTitle("Share confirmed").build();
                            // reset the backing data
                                getMySubBill().setIsConfirmed(true);
                            requestLayout();
                        } else
                            new CustomMessageBox.MessageBoxBuilder
                                    (HouseShare_Bill_Member.this, "Sorry. We could not process the " +
                                            "confirmation at the moment.\nPlease try again later.")
                                    .setTitle("Failed").build();
                        break;
                    }

                }
            }
            requestLayout();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_share_bill_member);
        actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setLogo(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_green)));
        }

        i = getIntent();
        bill = (Bill) i.getParcelableArrayListExtra(IntentConstants.BILL_PARCEL).get(0);
        username = i.getStringExtra(IntentConstants.USERNAME);
        hs_name = i.getStringExtra(IntentConstants.HOUSE_NAME);
        hsid = i.getStringExtra(IntentConstants.HOUSESHARE_ID);

        // initilise view objects
        billName_TextView = (TextView) findViewById(R.id.bill_basic_info);
        billCreationDetails_TextView = (TextView) findViewById(R.id.bill_creation_details);
        billStatus_TextView = (TextView) findViewById(R.id.bill_status);
        billAmount_TextView = (TextView) findViewById(R.id.bill_amount);
        unactivatedText_TextView = (TextView) findViewById(R.id.unactivated_text);
        timelineHolder_TextView = (TextView) findViewById(R.id.time_line_holder);
        basicInfoContainer = (RelativeLayout) findViewById(R.id.bill_basic_info_container);
        eventsTable = (TableLayout) findViewById(R.id.table_events);
        billRefresh_SwipeView = (SwipeRefreshLayout) findViewById(R.id.bill_update_swipe_view);
        billRefresh_SwipeView.setColorSchemeColors(getResources().getColor(R.color.light_green),
                getResources().getColor(android.R.color.holo_red_light));
        billRefresh_SwipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });


        primaryAction_View = (LinearLayout) findViewById(R.id.bill_pay_or_confirm);
        ((TextView) primaryAction_View.findViewById(R.id.bill_pay_or_confirm_text))
                .setText("Confirm");
        primaryAction_View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bill.isActive()) {
                        if (getMySubBill().isConfirmed()) {
                            new CustomMessageBox.MessageBoxBuilder(HouseShare_Bill_Member.this,
                                    "You have already confirmed this bill.\nPlease " +
                                            "wait while the other members have confirmed their shares.")
                                    .build();
                        } else {
                            HS_Bill_Primary_Action_Dialog dialog =
                                    HS_Bill_Primary_Action_Dialog.initialise(bill);
                            dialog.show(getFragmentManager(), "BillConfirm_Frag");
                        }
                } else {
                    Log.d("my sub bill", "/ " + getMySubBill().toString());
                    if (getMySubBill().getPayment() == null) {
                        Intent i = new Intent(HouseShare_Bill_Member.this, Houseshare_Payments.class);
                        i.putExtra(IntentConstants.HOUSESHARE_ID, hsid);
                        i.putExtra(IntentConstants.BILL_ID, bill.getBillID());
                        i.putExtra(IntentConstants.BILL_AMOUNT,
                                HouseShare_Bill_Member.this.getMySubBill().getAmount());
                        i.putExtra(IntentConstants.USERNAME, username);
                        startActivity(i);
                    }
                    else {
                        new CustomMessageBox.MessageBoxBuilder(HouseShare_Bill_Member.this,
                                "You have already submitted your payment.")
                                .setTitle("Payment already submitted").build();
                    }
            }
        }});
        participants_View = (LinearLayout) findViewById(R.id.bill_participants);
        participants_View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> participants = new ArrayList<String>();
                double[] shares = new double[bill.getSubBills().size()];
                boolean[] states = new boolean[bill.getSubBills().size()];

                int i = 0;
                for (String s : bill.getSubBills().keySet()) {
                    participants.add(Fragment_HS_Home.members.get(s).getUsername());
                    shares[i] = bill.getSubBills().get(s).getAmount();
                    states[i] = bill.getSubBills().get(s).isConfirmed();
                    ++i;
                }
                HS_Bill_Participants_Dialog dialog = HS_Bill_Participants_Dialog.initialise
                        (participants.toArray(new String[participants.size()]), shares, states);
                dialog.show(getFragmentManager(), "participants_dialog");
            }
        });

        message_View = (LinearLayout) findViewById(R.id.bill_announcement);
        message_View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HS_Bill_Message_Dialog dialog =
                        HS_Bill_Message_Dialog.initialise(bill.getBillCreator().getUsername());
                dialog.setCancelable(false);
                dialog.show(getFragmentManager(), "message_dialog_frag");
            }
        });

        deleteBill_View = (LinearLayout) findViewById(R.id.bill_delete);
        deleteBill_View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CustomMessageBox.MessageBoxBuilder(HouseShare_Bill_Member.this,
                        "You need to be this bill's creator in order to delete it.")
                        .setTitle("Cannot delete bill").build();

            }
        });

        //set up text views data
        billName_TextView.setText(bill.getBillName());
        billAmount_TextView.setText(StringUtils.POUND_SIGN + bill.getAmount());
        billCreationDetails_TextView.setText("Created by " +
                (StringUtils.getShortenedString(bill.getBillCreator().getUsername(), 15)
                                + " on " + StringUtils.getGeneralDateString(bill.getDateCreated())));
        if (!bill.isPaid()) {
            billStatus_TextView.setText("This bill is due in " +
                    Utilities.getDaysLeftUntilDueDate(bill.getDueDate()) + " days.");
        } else {
            billStatus_TextView.setText("This bill has been paid on " +
                    StringUtils.getGeneralDateString(bill.getDatePaid()) + ".");
        }


        new Bill_Worker(this, true, MODE.BILL_FETCH_MAIN)
                .setMsg("Loading your bill")
                .execute(new RequestQueue().addRequests(
                        getBillFetchingRequest(),
                        getSubBillFetchingRequest(),
                        getPaymentsFetchingRequest(),
                        getEventsFetchingRequest()).toList());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_house_share__bill, menu);
        return true;
    }

    /**
     * Called after {@link #onRestoreInstanceState}, {@link #onRestart}, or
     * {@link #onPause}, for your activity to start interacting with the user.
     * This is a good place to begin animations, open exclusive-access devices
     * (such as the camera), etc.
     * <p/>
     * <p>Keep in mind that onResume is not the best indicator that your activity
     * is visible to the user; a system window such as the keyguard may be in
     * front.  Use {@link #onWindowFocusChanged} to know for certain that your
     * activity is visible to the user (for example, to resume a game).
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onRestoreInstanceState
     * @see #onRestart
     * @see #onPostResume
     * @see #onPause
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("on resume", "abc");
        refresh();
    }



    private Request getSubBillFetchingRequest() {
        if (subBillsFetchingRequest != null)
            return subBillsFetchingRequest;
        subBillsFetchingRequest = new Request(Request.TYPE.POST)
                .addParam(Request_Params.PARAM_TYPE, Request_Params.REQUEST_HS_GET_SUB_BILLS)
                .addParam(Request_Params.REQUEST_HS_BILL_ID, bill.getBillID())
                .addParam(Request_Params.PARAM_USR, username);
        Log.d("subbills request: ", subBillsFetchingRequest.toString());
        return subBillsFetchingRequest;
    }


    private Request getEventsFetchingRequest() {
        if (eventsFetchingRequest != null)
            return eventsFetchingRequest;
        eventsFetchingRequest = new Request(Request.TYPE.POST)
                .addParam(Request_Params.PARAM_TYPE, Request_Params.REQUEST_HS_BILL_FETCH_EVENTS)
                .addParam(Request_Params.REQUEST_HS_BILL_ID, bill.getBillID())
                .addParam(Request_Params.PARAM_USR, username);
        Log.d("eventFetching request: ", eventsFetchingRequest.toString());
        return eventsFetchingRequest;
    }

    /**
     * fetch the info of a bill (not include the sub bill and events)
     *
     * @return the request
     */
    private Request getBillFetchingRequest() {
        if (billFetchingRequest != null)
            return billFetchingRequest;
        billFetchingRequest = new Request(Request.TYPE.POST)
                .addParam(Request_Params.PARAM_TYPE, Request_Params.REQUEST_HS_GET_A_BILL)
                .addParam(Request_Params.REQUEST_HS_BILL_ID, bill.getBillID())
                .addParam(Request_Params.PARAM_USR, username);
        Log.d("Bill fetching request: ", billFetchingRequest.toString());
        return billFetchingRequest;
    }

    private Request getPaymentsFetchingRequest() {
        if (paymentsFetchingRequest != null) {
            return paymentsFetchingRequest;
        }
        paymentsFetchingRequest = new Request(Request.TYPE.POST)
                .addParam(Request_Params.PARAM_TYPE, Request_Params.REQUEST_HS_GET_PAYMENTS)
                .addParam(Request_Params.REQUEST_HS_BILL_ID, bill.getBillID())
                .addParam(Request_Params.PARAM_USR, username);
        Log.d("payments request: ", paymentsFetchingRequest.toString());
        return paymentsFetchingRequest;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * update the events in the bill object
     *
     * @param r the response from the server
     */
    private void filterEvents(String r) {
        try {
            JSONArray events_response = new JSONArray(r);
            for (int i = 0; i < events_response.length(); i++) {
                JSONArray event_response = events_response.getJSONArray(i);
//                ["hs_100001_b1_0","1","hs_100001_b1","hs_100001","2015-04-14 20:36:38"]
                Event event = new Event(event_response.getString(EVENT_ID_POS),
                        event_response.getInt(EVENT_TYPE_POS),
                        bill,
                        StringUtils.getDateTimeFromServerDateResponse(event_response.getString(EVENT_DATE_POS)),
                        Fragment_HS_Home.members.get(event_response.getString(EVENT_SRC_POS)));

                Log.d("Extracted Event " + i, event.toString());
                bill.getEvents().add(event);

            }
            //sort the event in reverse date order (newest date first)
            Collections.sort(bill.getEvents());
            updateEventsTimeline();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateEventsTimeline() {
        eventsTable.removeAllViews();
        for (int i = 0; i < bill.getEvents().size(); i++) {
            eventsTable.addView(bill.getEvents().get(i).craftView(getLayoutInflater()));
        }
    }

    /**
     * update the sub bills in the bill object
     *
     * @param r the response from the server
     */
    private void filterSubBills(String r) {
        try {
            JSONArray subbils_response = new JSONArray(r);
            for (int i = 0; i < subbils_response.length(); i++) {
                JSONArray subbill_response = subbils_response.getJSONArray(i);
//                ["hs_100000","hs_100100_b1","8.33","2015-04-11","2015-09-23",null,"0"]
                SubBill subBill = new SubBill(subbill_response.getString(SUBBILL_HSID_POS),
                        subbill_response.getString(SUBBILL_BILL_ID_POS),
                        subbill_response.getDouble(SUBBILL_AMOUNT_POS),
                        subbill_response.getInt(SUBBILL_IS_ACTIVE_POS) == 1,
                        !subbill_response.getString(SUBBILL_DATE_PAID_POS).equals("null"),
                        subbill_response.getInt(SUBBILL_IS_CONFIRMED) == 1,
                        StringUtils.getDateFromServerDateResponse(
                                subbill_response.getString(SUBBILL_DATE_PAID_POS)),
                        null);
                Log.d("Extracted Sub Bill", subBill.toString());
                bill.getSubBills().put(subbill_response.getString(SUBBILL_HSID_POS), subBill);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param r
     */
    private void filterPayments(String r) {
        try {
            JSONArray payments_response = new JSONArray(r);
            for (int i = 0; i < payments_response.length(); i++) {
//                ["hs_100001","hs_100003_b1","7.14","2015-04-15 18:05:05","2015-04-14","S","16","abc"]

                JSONArray payment_response = payments_response.getJSONArray(i);
                String hsid = payment_response.getString(PAYMENT_HSID_POS);
                Payment p =  new Payment(hsid,
                        payment_response.getString(PAYMENT_BILL_ID_POS),
                        payment_response.getDouble(PAYMENT_AMOUNT_POS),
                        StringUtils.getDateTimeFromServerDateResponse(payment_response.getString(PAYMENT_DATE_SUBMITTED_POS)),
                        StringUtils.getDateFromServerDateResponse(payment_response.getString(PAYMENT_DATE_PAID_POS)),
                        payment_response.getString(PAYMENT_STATUS_POS).equals("C"),
                        payment_response.getInt(PAYMENT_PAYMENT_METHOD_POS),
                        payment_response.getString(PAYMENT_PAYMENT_MESSAGE_POS));


                // set the payment for this sub bill
                bill.getSubBills().get(hsid).setPayment(p);

                Log.d("Payment", p.toString());

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update the bill object
     *
     * @param r the response from the server regarding the bill
     */
    private void filterBill(String r) {
        try {
            //["hs_100003_b1","hs_100003","Stella house","wg","2015-04-11","154","1995-01-23","",null,"0"]
            JSONArray bill_arr = new JSONArray(r);

            //initialise the bill being extracted from the response
            bill = new Bill.BillBuilder(bill_arr.getInt(BILL_ISACTIVE_POS) == 1,
                    !StringUtils.isFieldEmpty(bill_arr.getString(BILL_DATE_PAID_POS)),
                    bill_arr.getBoolean(BILL_AM_I_CREATOR))
                    .setAmount(bill_arr.getDouble(BILL_AMOUNT_POS))
                    .setBillCreator(Fragment_HS_Home.members.get(bill_arr.getString(BILL_CREATOR_ID)))
                    .setBillID(bill_arr.getString(BILL_ID_POS))
                    .setBillName(bill_arr.getString(BILL_NAME_POS))
                    .setDateCreated(StringUtils.getDateFromServerDateResponse(
                            bill_arr.getString(BILL_DATE_CREATED_POS)))
                    .setDueDate(StringUtils.getDateFromServerDateResponse
                            (bill_arr.getString(BILL_DUE_DATE_POS)))
                    .setMessage(bill_arr.getString(BILL_MESSAGE_POS)).build();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * get the sub bill of the current user
     *
     * @return the sub bill object or null if not found
     */
    private SubBill getMySubBill() {
        for (SubBill s : bill.getSubBills().values()) {
            if (s.getHs_id().equals(hsid))
                return s;
        }
        return null;
    }


    /**
     * Do the layout UI stuff
     */
    private void requestLayout() {
        Log.d("Bil Active?", String.valueOf(bill.isActive()));
        if (bill.isActive()) {
            ((ImageView) primaryAction_View.findViewById(R.id.bill_pay_or_confirm_icon)).setImageResource(
                    R.drawable.pay);
            if (getMySubBill().getPayment() != null && !getMySubBill().getPayment().isConfirmed())

            {
                ((TextView) primaryAction_View.findViewById(R.id.bill_pay_or_confirm_text))
                        .setText("Submitted");

            }
            else
                ((TextView) primaryAction_View.findViewById(R.id.bill_pay_or_confirm_text))
                        .setText("Submit");

            unactivatedText_TextView.setVisibility(View.INVISIBLE);
            timelineHolder_TextView.setVisibility(View.INVISIBLE);
        } else {
            //show the activated text
            unactivatedText_TextView.setVisibility(View.VISIBLE);

            // if this user is just a member (nor creator)
            unactivatedText_TextView.setText("This bill is not activated");
            unactivatedText_TextView.setBackgroundColor
                    (getResources().getColor(android.R.color.holo_red_light));

            // if he has confirmed his share
            if (getMySubBill().isConfirmed()) {
                timelineHolder_TextView.setText("You have confirmed your share. " +
                        "Please wait until other members have confirms theirs");
            } else {
                timelineHolder_TextView.setText("Please see your share in Participants " +
                        "and confirm your share.");
            }
        }
    }

    /**
     * refresh the bill
     * called when the activity is resumed tc.
     */
    private void refresh() {
        new Bill_Worker(HouseShare_Bill_Member.this, MODE.BILL_REFRESH)
                .execute(new RequestQueue().addRequests(getBillFetchingRequest(), getSubBillFetchingRequest(),
                        getPaymentsFetchingRequest(),
                        getEventsFetchingRequest()).toList());
    }
}

