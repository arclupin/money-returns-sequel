package com.ncl.team5.lloydsmockup;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.widget.Toast;

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
import Fragments.HS_Bill_Final_Confirm_Dialog;
import Fragments.HS_Bill_Message_Dialog;
import Fragments.HS_Bill_Participants_Dialog;
import Fragments.HS_Bill_Payments_Dialog;
import Fragments.HS_Bill_Primary_Action_Dialog;
import HTTPConnect.ConcurrentConnection;
import HTTPConnect.Request;
import HTTPConnect.RequestQueue;
import HTTPConnect.Request_Params;
import HTTPConnect.Response;
import HTTPConnect.Responses_Format;
import Utils.StringUtils;
import Utils.Utilities;

/**
 * Activity for showing the bill page (owners only)

 * <br/>
 * The owner won't be able to: <br/>
 * Pay his share <br/>
 * He will be able to: <br/>
 * Activate the bill <br/>
 * Mark the bill as paid <br/>
 * See events <br/>
 * Delete the pill <br/>
 * See shares <br/>
 *
 * @see HouseShare_Bill_Member
 *
 */
public class HouseShare_Bill_Owner extends Activity implements HS_Bill_Delete_Dialog.BillDeleteDialogListener,
        HS_Bill_Primary_Action_Dialog.BillPrimaryActionDialogListener,
        HS_Bill_Payments_Dialog.BillPaymentConfirmationListener,
        HS_Bill_Final_Confirm_Dialog.FinalConfirmBillDialogListener
{

    //order of data in response
    public static final int BILL_ID_POS = 0;
    public static final int BILL_CREATOR_ID = 1; // houseshare id
    public static final int BILL_GROUP_NAME_POS = 2;
    public static final int BILL_NAME_POS = 3;
    public static final int BILL_DATE_CREATED_POS = 4;
    public static final int BILL_AMOUNT_POS = 5;
    public static final int BILL_DUE_DATE_POS = 6;
    public static final int BILL_MESSAGE_POS = 7;
    public static final int BILL_DATE_PAID_POS = 8;

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


    // the name to be displayed (not necessarily be the full name
    // as the name might be trimmed if there is not enough space
    private String billCreator_Name_Display;
    private Bill bill;

    private Intent i;

    private String username;
    private String hs_name;
    private String hsid;
    private String billID;

    public enum MODE {
        BILL_FETCH_MAIN,
        BILL_CONFIRM,
        BILL_EDIT,
        BILL_DELETE,
        BILL_FETCH_EVENTS,
        BILL_REFRESH,
        BILL_MARK_AS_PAID
    }

    //requests for workers
    private Request subBillsFetchingRequest;
    private Request eventsFetchingRequest;
    private Request billFetchingRequet;
    private Request paymentsFetchingRequest;
    private Request billConcludingRequest;


    /**
     * User confirms that he wants to delete this bill
     * Only creators have this permission
     *
     * @param f      the dialog itself (for dismission)
     * @param billID the id of the bill
     */
    @Override
    public void onButtonClickDeleteBill(HS_Bill_Delete_Dialog f, String billID) {
        f.dismiss();
        Request r = new Request(Request.TYPE.POST);
        r.addParam(Request_Params.PARAM_TYPE, Request_Params.PARAM_REMOVE_BILL)
                .addParam(Request_Params.REQUEST_HS_BILL_ID, bill.getBillID())
                .addParam(Request_Params.PARAM_USR, username);
        new Bill_Worker(this, true, MODE.BILL_DELETE)
                .setMsg("Removing your bill").execute(new RequestQueue().addRequest(r).toList());
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
        r.addParam(Request_Params.PARAM_TYPE, Request_Params.REQUEST_HS_ACTIVATE_BILL)
                .addParam(Request_Params.REQUEST_HS_BILL_ID, bill.getBillID())
                .addParam(Request_Params.PARAM_USR, username);
        new Bill_Worker(this, true, MODE.BILL_CONFIRM)
                .setMsg("Activating your bill").execute(new RequestQueue().addRequest(r).toList());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_share_bill_owner);

        actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setLogo(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_green)));
        }

        i = getIntent();
        if (i.getParcelableArrayListExtra(IntentConstants.BILL_PARCEL) != null)
            bill = (Bill) i.getParcelableArrayListExtra(IntentConstants.BILL_PARCEL).get(0);
        username = i.getStringExtra(IntentConstants.USERNAME);
        hs_name = i.getStringExtra(IntentConstants.HOUSE_NAME);
        hsid = i.getStringExtra(IntentConstants.HOUSESHARE_ID);
        billID = i.getStringExtra(IntentConstants.BILL_ID);

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
                new Bill_Worker(HouseShare_Bill_Owner.this, MODE.BILL_REFRESH)
                        .execute(new RequestQueue().addRequests(getBillFetchingRequest(),
                                getSubBillFetchingRequest(),
                                getPaymentsFetchingRequest(),
                                getEventsFetchingRequest()).toList());
            }
        });


        primaryAction_View = (LinearLayout) findViewById(R.id.bill_pay_or_confirm);
        ((TextView) primaryAction_View.findViewById(R.id.bill_pay_or_confirm_text))
                .setText("Activate");
        primaryAction_View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bill.isActive()) {
                    if (bill.canBillBeActivated()) {

                        HS_Bill_Primary_Action_Dialog dialog =
                                HS_Bill_Primary_Action_Dialog.initialise(bill);
                        dialog.show(getFragmentManager(), "BillActivation_Frag");
                    } else {
                        new CustomMessageBox.MessageBoxBuilder(HouseShare_Bill_Owner.this,
                                "Sorry, you cannot activate this bill at the moment.\nPlease " +
                                        "wait while the other members have confirmed their shares.")
                                .build();
                    }
                } else {
                    List<Payment> p = new ArrayList<Payment>();
                    List<String> u = new ArrayList<String>();
                    for (String hsid : bill.getSubBills().keySet()) {
                        if (bill.getSubBills().get(hsid).getPayment() != null) {
                            p.add(bill.getSubBills().get(hsid).getPayment());
                            u.add(Fragment_HS_Home.members.get(hsid).getUsername());
                        }
                    }
                    HS_Bill_Payments_Dialog f = HS_Bill_Payments_Dialog.initialise(
                            p.toArray(new Payment[p.size()]), u.toArray(new String[u.size()]));
                    f.show(getFragmentManager(), "Payments_Frag");
                    //TODO blur the view of primary action
                }
            }
        });
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
                        HS_Bill_Message_Dialog.initialise
                                ("You", bill.getMessage());
                dialog.setCancelable(false);
                dialog.show(getFragmentManager(), "message_dialog_frag");
            }
        });

        deleteBill_View = (LinearLayout) findViewById(R.id.bill_delete);
        deleteBill_View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HS_Bill_Delete_Dialog dialog = HS_Bill_Delete_Dialog.initialise(bill.getBillID());
                dialog.show(getFragmentManager(), "BillDel_Frag");
            }
        });

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
     * get the sub bills fetching request - or initialise it if needed to
     *
     * @return the initialised request
     */
    private Request getSubBillFetchingRequest() {
        if (subBillsFetchingRequest != null)
            return subBillsFetchingRequest;
        subBillsFetchingRequest = new Request(Request.TYPE.POST)
                .addParam(Request_Params.PARAM_TYPE, Request_Params.REQUEST_HS_GET_SUB_BILLS)
                .addParam(Request_Params.REQUEST_HS_BILL_ID, billID)
                .addParam(Request_Params.PARAM_USR, username);
        Log.d("subbills request: ", subBillsFetchingRequest.toString());
        return subBillsFetchingRequest;
    }

    /**
     * get the events fetching request - or initialise it if needed to
     *
     * @return the initialised request
     */
    private Request getEventsFetchingRequest() {
        if (eventsFetchingRequest != null)
            return eventsFetchingRequest;
        eventsFetchingRequest = new Request(Request.TYPE.POST)
                .addParam(Request_Params.PARAM_TYPE, Request_Params.REQUEST_HS_BILL_FETCH_EVENTS)
                .addParam(Request_Params.REQUEST_HS_BILL_ID, billID)
                .addParam(Request_Params.PARAM_USR, username);
        Log.d("eventFetching request: ", eventsFetchingRequest.toString());
        return eventsFetchingRequest;
    }


    /**
     * get the bill concluding request - or initialise it if needed to
     *
     * @return the initialised request
     */
    private Request getBillConcludingRequest() {
        if (billConcludingRequest != null)
            return billConcludingRequest;
        billConcludingRequest = new Request(Request.TYPE.POST)
                .addParam(Request_Params.PARAM_TYPE, Request_Params.PARAM_CONCLUDE_BILL)
                .addParam(Request_Params.REQUEST_HS_BILL_ID, billID)
                .addParam(Request_Params.PARAM_USR, username);
        Log.d("bill final request: ", billConcludingRequest.toString());
        return billConcludingRequest;
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
        Log.d("Owner resumed", "abc");
        refresh();
    }

    /**
     * fetch the info of a bill (not include the sub bill and events)
     *
     * @return the request
     */
    private Request getBillFetchingRequest() {
        if (billFetchingRequet != null)
            return billFetchingRequet;
        billFetchingRequet = new Request(Request.TYPE.POST)
                .addParam(Request_Params.PARAM_TYPE, Request_Params.REQUEST_HS_GET_A_BILL)
                .addParam(Request_Params.REQUEST_HS_BILL_ID, billID)
                .addParam(Request_Params.PARAM_USR, username);
        Log.d("Bill fetching request: ", billFetchingRequet.toString());
        return billFetchingRequet;
    }

    /**
     * get the payments fetching request - or initialise it if needed to
     *
     * @return the initialised request
     */
    private Request getPaymentsFetchingRequest() {
        if (paymentsFetchingRequest != null) {
            return paymentsFetchingRequest;
        }
        paymentsFetchingRequest = new Request(Request.TYPE.POST)
                .addParam(Request_Params.PARAM_TYPE, Request_Params.REQUEST_HS_GET_PAYMENTS)
                .addParam(Request_Params.REQUEST_HS_BILL_ID, billID)
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
//                TODO [DON'T DELETE] [FORMAT] [IMPORTANT] ["hs_100001_b1_0","1","hs_100001_b1","hs_100001","2015-04-14 20:36:38"]
                Event event = new Event(event_response.getString(EVENT_ID_POS),
                        event_response.getInt(EVENT_TYPE_POS),
                        bill,
                        StringUtils.getDateTimeFromServerDateResponse
                                (event_response.getString(EVENT_DATE_POS)),
                        Fragment_HS_Home.members.get(event_response.getString(EVENT_SRC_POS)));

                Log.d("Extracted Event " + i, event.toString());
                bill.getEvents().add(event);

            }
            //sort the event in reverse date order (newest date first)
            List<Event> eventList = new ArrayList<Event>(bill.getEvents());
            Collections.sort(eventList);
            updateEventsTimeline(eventList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * update the event timeline
     *
     */
    private void updateEventsTimeline(List<Event> eventList) {
        eventsTable.removeAllViews();
        for (int i = 0; i < eventList.size(); i++) {
            eventsTable.addView(eventList.get(i).craftView(getLayoutInflater()));
        }
        Log.d("event rows", "/ " + eventsTable.getChildCount());
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
//                TODO [IMPORTANT] [DON'T DELETE] [FORMAT]["hs_100000","hs_100100_b1","8.33","2015-04-11","2015-09-23",null,"0"]
                SubBill subBill = new SubBill(subbill_response.getString(SUBBILL_HSID_POS),
                        subbill_response.getString(SUBBILL_BILL_ID_POS),
                        subbill_response.getDouble(SUBBILL_AMOUNT_POS),
                        subbill_response.getInt(SUBBILL_IS_ACTIVE_POS) == 1,
                        !subbill_response.getString(SUBBILL_DATE_PAID_POS).equals("null"),
                        subbill_response.getInt(SUBBILL_IS_CONFIRMED) == 1,
                        StringUtils.getDateFromServerDateResponse(
                                subbill_response.getString(SUBBILL_DATE_PAID_POS)), null);
                Log.d("Extracted Sub Bill", subBill.toString());
                bill.getSubBills().put(subbill_response.getString(SUBBILL_HSID_POS), subBill);
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
            // TODO [IMPORTANT] [DON'T DELETE] [FORMAT] ["hs_100003_b1","hs_100003","Stella house","wg","2015-04-11","154","1995-01-23","",null,"0"]
            JSONArray bill_arr = new JSONArray(r);
            Log.d("extracted date paid", bill_arr.getString(BILL_DATE_PAID_POS) + !StringUtils.isFieldEmpty(bill_arr.getString(BILL_DATE_PAID_POS)));
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
                    .setDatePaid(StringUtils.getDateFromServerDateResponse(
                            bill_arr.getString(BILL_DATE_PAID_POS)))
                    .setDueDate(StringUtils.getDateFromServerDateResponse
                            (bill_arr.getString(BILL_DUE_DATE_POS)))
                    .setMessage(bill_arr.getString(BILL_MESSAGE_POS)).build();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param r the relevant response
     */
    private void filterPayments(String r) {
        try {
            JSONArray payments_response = new JSONArray(r);
            for (int i = 0; i < payments_response.length(); i++) {
//            TODO [IMPORTANT] [DON'T DELETE] [FORMAT]    ["hs_100001","hs_100003_b1","7.14","2015-04-15 18:05:05","2015-04-14","S","16","abc"]

                JSONArray payment_response = payments_response.getJSONArray(i);
                String hsid = payment_response.getString(PAYMENT_HSID_POS);
                Payment p = new Payment(hsid,
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
     * Do the layout UI stuff on update
     *
     * This will reset everything in the activaty.
     * [TODO] [IMPORTANT]!
     */
    private void requestLayout() {
        Log.d("On request layout", "can bill be paid:" + String.valueOf(bill.canBillBePaid()));

        findViewById(R.id.bill_options).setVisibility(View.VISIBLE);

        //set up text views data
        primaryAction_View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bill.isActive()) {
                    if (bill.canBillBeActivated()) {
                        HS_Bill_Primary_Action_Dialog dialog =
                                HS_Bill_Primary_Action_Dialog.initialise(bill);
                        dialog.show(getFragmentManager(), "BillActivation_Frag");
                    } else {
                        new CustomMessageBox.MessageBoxBuilder(HouseShare_Bill_Owner.this,
                                "Sorry, you cannot activate this bill at the moment.\nPlease " +
                                        "wait while the other members have confirmed their shares.")
                                .build();
                    }
                } else {
                    List<Payment> p = new ArrayList<Payment>();
                    List<String> u = new ArrayList<String>();
                    for (String hsid : bill.getSubBills().keySet()) {
                        if (bill.getSubBills().get(hsid).getPayment() != null) {
                            p.add(bill.getSubBills().get(hsid).getPayment());
                            u.add(Fragment_HS_Home.members.get(hsid).getUsername());
                        }
                    }
                    HS_Bill_Payments_Dialog f = HS_Bill_Payments_Dialog.initialise(
                            p.toArray(new Payment[p.size()]), u.toArray(new String[u.size()]));
                    f.show(getFragmentManager(), "Payments_Frag");
                    //TODO blur the view of primary action
                }
            }
        });
        billName_TextView.setText(bill.getBillName());
        billAmount_TextView.setText(StringUtils.POUND_SIGN + bill.getAmount());
        billCreationDetails_TextView.setText("Created by " +
                ("You on " + StringUtils.getGeneralDateString(bill.getDateCreated())));

        if (!bill.isPaid()) {
            long daysLeft = Utilities.getDaysLeftUntilDueDate(bill.getDueDate());
            billStatus_TextView.setText("This bill is due in " + daysLeft + " days.");
            // show a alert icon for bills ending soon
            if (daysLeft <= StringUtils.daysLeftWarning)
                findViewById(R.id.high_priority_img).setVisibility(View.VISIBLE);

        } else {
            billStatus_TextView.setText("This bill has been paid on " +
                    StringUtils.getGeneralDateString(bill.getDatePaid()) + ".");
        }

        Log.d("Bil Active?", String.valueOf(bill.isActive()));
        if (bill.isActive()) {
            if (bill.canBillBePaid()) {

                //reset the strip bar
                unactivatedText_TextView.setVisibility(View.VISIBLE);
                unactivatedText_TextView.setText("All members have paid their shares.\nLet's pay the bill.");
                unactivatedText_TextView.setBackgroundColor
                        (getResources().getColor(R.color.light_cyan));

                //reset the custom action bar
                ((ImageView) primaryAction_View.findViewById(R.id.bill_pay_or_confirm_icon))
                        .setImageResource(R.drawable.good);
                primaryAction_View.findViewById(R.id.bill_pay_or_confirm_text).setVisibility(View.GONE);
                primaryAction_View.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HS_Bill_Final_Confirm_Dialog dialog =
                                HS_Bill_Final_Confirm_Dialog.initialise(HouseShare_Bill_Owner.this.bill);
                        dialog.show(getFragmentManager(), "FInalConfirm_Frag");
                    }
                });

            }
            // else show timeline only (hide the strip)
            else {
                ((ImageView) primaryAction_View.findViewById(R.id.bill_pay_or_confirm_icon)).setImageResource(
                        R.drawable.pay);
                ((TextView) primaryAction_View.findViewById(R.id.bill_pay_or_confirm_text)).setText(
                        "Payments");
                unactivatedText_TextView.setVisibility(View.INVISIBLE);
                timelineHolder_TextView.setVisibility(View.INVISIBLE);
            }
        }

        // else if the bill is inactive (either paid or not activated)
        else {
            // if the bill has been marked paid
            if (bill.isPaid()) {
                unactivatedText_TextView.setVisibility(View.VISIBLE);
                unactivatedText_TextView.setText("This bill has been paid.");
                unactivatedText_TextView.setBackgroundColor
                        (getResources().getColor(R.color.light_blue));

                ((ImageView)primaryAction_View.findViewById(R.id.bill_pay_or_confirm_icon))
                        .setImageResource(R.drawable.paid);
                primaryAction_View.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new CustomMessageBox.MessageBoxBuilder(HouseShare_Bill_Owner.this,
                                "This bill has been paid").build();
                    }


                });
                primaryAction_View.findViewById(R.id.bill_pay_or_confirm_text).setVisibility(View.GONE);
                return;
            }

            //else if the bill is not even activated
            else {
                //show the activated text
                unactivatedText_TextView.setVisibility(View.VISIBLE);
                // if the bill can be activated now
                if (bill.canBillBeActivated()) {
                    unactivatedText_TextView.setText("This bill can be activated now");
                    unactivatedText_TextView.
                            setBackgroundColor(getResources().getColor(R.color.dark_green));
                    timelineHolder_TextView.
                            setText("You can confirm this bill by clicking the Activate button.");
                }

                // else if the bill cannot be activated
                else {
                    unactivatedText_TextView.setText("This bill is not activated");
                    unactivatedText_TextView.setBackgroundColor
                            (getResources().getColor(android.R.color.holo_red_light));
                    timelineHolder_TextView.setText("Please wait until others have confirmed " +
                            "their shares. Then you can activate this bill.");
                }
            }
        }
    }

    /**
     * refresh the bill
     * This would cause everything (from the underlying data objects and the visual components)
     * to be refreshed
     */
    private void refresh() {
        new Bill_Worker(HouseShare_Bill_Owner.this, MODE.BILL_REFRESH)
                .execute(new RequestQueue().addRequests(getBillFetchingRequest(),
                        getSubBillFetchingRequest(),
                        getPaymentsFetchingRequest(),
                        getEventsFetchingRequest()).toList());
    }


    /**
     * User clicks on one of the unconfirmed payment in the dialog
     *
     * @param f the fragement itself
     * @param p the payment object associated with this sub bill
     */
    @Override
    public void onUnconfirmedPaymentClicked(HS_Bill_Payments_Dialog f, Payment p) {
        f.dismiss();

        Intent i = new Intent(this, Houseshare_Confirm_Payment.class);
        i.putExtra(IntentConstants.USERNAME, username);
        i.putParcelableArrayListExtra(IntentConstants.PAYMENTS,
                new ArrayList<Parcelable>(Collections.singletonList(p)));
        i.putExtra(IntentConstants.PAYER_NAME, Fragment_HS_Home.members.get(p.getHsid()).getUsername());

        startActivity(i);

    }

    /**
     * The bill owner confirms that all of the shares have been paid and this bill should be concluded.
     *
     * @param f    the dialog itself (for dismissing the bill)
     * @param bill
     */
    @Override
    public void onFinalConfirmClick(HS_Bill_Final_Confirm_Dialog f, Bill bill) {
        f.dismiss();
        new Bill_Worker(this, true, MODE.BILL_MARK_AS_PAID)
                .setMsg("Processing")
                .execute(new RequestQueue().addRequest(getBillConcludingRequest()).toList());
    }


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
//            if (mode == MODE.BILL_FETCH_MAIN || mode == MODE.BILL_REFRESH)
//                bill = null;
        }

        @Override
        protected void onPostExecute(List<Response> responses) {
            super.onPostExecute(responses);

            switch (mode) {

                // the mode for doing the main work
                case BILL_FETCH_MAIN:
                case BILL_REFRESH: {
                    Response r = responses.get(3);
                    // check for expiration of the latest response
                    if (r.getToken(Responses_Format.RESPONSE_EXPIRED).equals("true")) {
                        Utilities.showAutoLogoutDialog(HouseShare_Bill_Owner.this, username);
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
                // the mode for activating a bill
                case BILL_CONFIRM: {
                    Response response = responses.get(0);
                    if (response.getToken(Responses_Format.RESPONSE_EXPIRED).equals("true")) {
                        Utilities.showAutoLogoutDialog(HouseShare_Bill_Owner.this, username);
                    }
                    else {
                        if (response.getToken(Responses_Format.RESPONSE_STATUS).equals("true")) {
                            new CustomMessageBox.MessageBoxBuilder
                                    (HouseShare_Bill_Owner.this, ("You have activated this bill." +
                                            "\nAll users show now be able to pay this bill"))
                                    .setTitle("Share confirmed").build();
                            // reset the backing data
                            refresh();
                        } else
                            new CustomMessageBox.MessageBoxBuilder
                                    (HouseShare_Bill_Owner.this, "Sorry. We could not process the " +
                                            "confirmation at the moment.\nPlease try again later.")
                                    .setTitle("Failed").build();
                            refresh();
                        break;
                    }

                }

                // the mode for marking a bill as read
                case BILL_MARK_AS_PAID: {
                    Response r = responses.get(0);
                    if (r.getToken(Responses_Format.RESPONSE_EXPIRED).equals("true")) {
                        Utilities.showAutoLogoutDialog(HouseShare_Bill_Owner.this,username);
                    }
                    else {
                        if (r.getToken(Responses_Format.RESPONSE_STATUS).equals("true"))
                            new CustomMessageBox.MessageBoxBuilder
                                    (HouseShare_Bill_Owner.this, ("You have marked this bill as paid.\n" +
                                            "All members will be notified soon."))
                                    .setTitle("Confirmation").build();
                        else
                            new CustomMessageBox.MessageBoxBuilder
                                    (HouseShare_Bill_Owner.this, "Sorry. We could not process the " +
                                            "confirmation at the moment.\nPlease try again later.")
                                    .setTitle("Failed").build();
                        refresh();
                    }
                    break;
                }

                // the mode for deleting a bill
                case BILL_DELETE: {
                    Response r = responses.get(0);
                    if (r.getToken(Responses_Format.RESPONSE_EXPIRED).equals("true")) {
                        Utilities.showAutoLogoutDialog(HouseShare_Bill_Owner.this,username);
                    }
                    else {
                        if (r.getToken(Responses_Format.RESPONSE_STATUS).equals("true")) {
                            new CustomMessageBox.MessageBoxBuilder
                                    (HouseShare_Bill_Owner.this,
                                            ("You have removed this bill successfully"))
                                    .setTitle("Confirmation")
                                    .setActionOnClick(new CustomMessageBox.ToClick() {
                                        @Override
                                        public void DoOnClick() {
                                            finish();
                                        }
                                    }).build();

                        }
                        else
                            new CustomMessageBox.MessageBoxBuilder
                                    (HouseShare_Bill_Owner.this, "Sorry. We could not process the " +
                                            "removal at the moment.\nPlease try again later.")
                                    .setTitle("Failed").build();
                        refresh();
                    }
                    break;
                }
            }
            requestLayout();
        }
    }
}

