package com.ncl.team5.lloydsmockup;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ncl.team5.lloydsmockup.Houseshares.Bill;
import com.ncl.team5.lloydsmockup.Houseshares.Member;
import com.ncl.team5.lloydsmockup.Houseshares.SubBill;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
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


public class HouseShare_Bill extends Activity implements HS_Bill_Delete_Dialog.BillDeleteDialogListener,
HS_Bill_Primary_Action_Dialog.BillPrimaryActionDialogListener{

    private static final int SUBBILL_HSID_POS = 0;
    private static final int SUBBILL_BILL_ID_POS = 1;
    private static final int SUBBILL_AMOUNT_POS = 2;
    private static final int SUBBILL_DATE_CREATED_POS = 3;
    private static final int SUBBILL_DUE_DATE_POS = 4;
    private static final int SUBBILL_DATE_PAID_POS = 5;
    private static final int SUBBILL_IS_ACTIVE_POS = 6;

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
        //TODO
    }

    public static enum MODE {BILL_FETCH_MAIN, BILL_CONFIRM, BILL_EDIT, BILL_DELETE};
    public Request subBillsFetchingRequest;

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
        protected void onPostExecute(List<Response> responses) {
            super.onPostExecute(responses);
            switch (mode) {
                case BILL_FETCH_MAIN: {
                    Response r = responses.get(0);
                    // check for expiration
                    if (r.getToken(Responses_Format.RESPONSE_EXPIRED).equals("true")) {
                        Utilities.showAutoLogoutDialog(HouseShare_Bill.this, username);
                    }
                    else {
                        //update the backing bill with fetched sub bills
                        filterSubBills(responses.get(0).getToken(Responses_Format.RESPONSE_HS_CONTENT));

                        //update the time line [holder]
                        if (!getMySubBill().isActive() && !bill.amICreator()) {
                            timelineHolder_TextView.setText("Please see your share in Participants " +
                                    "and confirm your share.");
                        }
                        else if (bill.amICreator()) {
                            timelineHolder_TextView.setText("Please wait until others have confirmed " +
                                    "their shares. Then you can activate this bill.");
                        }
                    }
                    break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_share__bill);

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
        primaryAction_View = (LinearLayout) findViewById(R.id.bill_pay_or_confirm);
            ((TextView) primaryAction_View.findViewById(R.id.bill_pay_or_confirm_text))
                    .setText(bill.amICreator() ? "Activate" : "Confirm");
        primaryAction_View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HS_Bill_Primary_Action_Dialog dialog = HS_Bill_Primary_Action_Dialog.initialise(bill);
                dialog.show(getFragmentManager(), "BillCOnfirm_Frag");
            }
        });
        participants_View = (LinearLayout) findViewById(R.id.bill_participants);
        participants_View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> participants = new ArrayList<String>();
                boolean[] states = new boolean[bill.getSubBills().size()];
                int i = 0;
                for (Member s : bill.getSubBills().keySet()) {
                    participants.add(s.getUsername());
                    states[i++] = bill.getSubBills().get(s).isActive();
                }
                HS_Bill_Participants_Dialog dialog = HS_Bill_Participants_Dialog.initialise
                        (participants.toArray(new String[participants.size()]), states);
                dialog.show(getFragmentManager(), "participants_dialog");
            }
        });

        message_View = (LinearLayout) findViewById(R.id.bill_announcement);
        message_View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HS_Bill_Message_Dialog dialog =
                        HS_Bill_Message_Dialog.initialise
                                (bill.amICreator() ? "You" : bill.getBillCreator().getUsername());
                dialog.setCancelable(false);
                dialog.show(getFragmentManager(), "message_dialog_frag");
            }
        });

        deleteBill_View = (LinearLayout) findViewById(R.id.bill_delete);
        deleteBill_View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bill.amICreator()) {
                    HS_Bill_Delete_Dialog dialog = HS_Bill_Delete_Dialog.initialise(bill.getBillID());
                    dialog.show(getFragmentManager(), "BillDel_Frag");
                }
                else new CustomMessageBox.MessageBoxBuilder(HouseShare_Bill.this,
                        "You need to be this bill's creator in order to delete it.")
                        .setTitle("Cannot delete bill").build();

            }
        });

        //set up text views data
        billName_TextView.setText(bill.getBillName());
        billAmount_TextView.setText(StringUtils.POUND_SIGN + bill.getAmount());
        billCreationDetails_TextView.setText("Created by " +
                (bill.amICreator() ? "you" :
                        StringUtils.getShortenedString(bill.getBillCreator().getUsername(), 15)
                        + " on " + StringUtils.getGeneralDateString(bill.getDateCreated())));
        if (!bill.isPaid()) {
            billStatus_TextView.setText("This bill is due in " +
                    Utilities.getDaysLeftUntilDueDate(bill.getDueDate()) +  " days.");
        }
        else {
            billStatus_TextView.setText("This bill has been paid on " +
                    StringUtils.getGeneralDateString(bill.getDatePaid()) + ".");
        }

        if (bill.isActive()) {
            basicInfoContainer.setBackgroundColor(getResources().getColor(R.color.dark_green));

        }
        else {
            unactivatedText_TextView.setVisibility(View.VISIBLE);


        }

        new Bill_Worker(this, true, MODE.BILL_FETCH_MAIN)
                .setMsg("Loading your bill")
                .execute(new RequestQueue().addRequest(getSubBillFetchingRequest()).toList());


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_house_share__bill, menu);
        return true;
    }

    public Request getSubBillFetchingRequest() {
        if (subBillsFetchingRequest != null)
            return subBillsFetchingRequest;
        subBillsFetchingRequest = new Request(Request.TYPE.POST)
                .addParam(Request_Params.PARAM_TYPE, Request_Params.REQUEST_HS_GET_SUB_BILLS)
                .addParam(Request_Params.REQUEST_HS_BILL_ID, bill.getBillID())
                .addParam(Request_Params.PARAM_USR, username);
        Log.d("bill fetching request: ", subBillsFetchingRequest.toString());
        return subBillsFetchingRequest;
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
     * update the sub bills in the bill object
     * @param r the response from the server
     */
    private void filterSubBills(String r) {
        try {
            JSONArray subbils_response = new JSONArray(r);
            for (int i = 0; i < subbils_response.length(); i++) {
                JSONArray subbill_response = subbils_response.getJSONArray(i);
//                ["hs_100000","hs_100100_b1","8.33","2015-04-11","2015-09-23",null,"0"]
                Log.d("Date paid", subbill_response.getString(SUBBILL_DATE_PAID_POS) + !subbill_response.getString(SUBBILL_DATE_PAID_POS).equals("null"));
                SubBill subBill = new SubBill(subbill_response.getString(SUBBILL_HSID_POS),
                        subbill_response.getString(SUBBILL_BILL_ID_POS),
                        subbill_response.getDouble(SUBBILL_AMOUNT_POS),
                        subbill_response.getInt(SUBBILL_IS_ACTIVE_POS) == 1,
                        !subbill_response.getString(SUBBILL_DATE_PAID_POS).equals("null"),
                        StringUtils.getDateFromServerDateResponse(
                                subbill_response.getString(SUBBILL_DATE_PAID_POS)));
                Log.d("Extracted Sub Bill", subBill.toString());
                bill.getSubBills().put(Fragment_HS_Home.members.get
                                (subbill_response.getString(SUBBILL_HSID_POS)), subBill);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * get the sub bill of the current user
     * @return the sub bill object or null if not found
     */
    private SubBill getMySubBill() {
        for (SubBill s : bill.getSubBills().values()) {
            if (s.getHs_id().equals(hsid))
                return s;
        }
        return null;
    }
}
