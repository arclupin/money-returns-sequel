package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ncl.team5.lloydsmockup.Houseshares.Payment;

import java.util.List;

import HTTPConnect.ConcurrentConnection;
import HTTPConnect.Request;
import HTTPConnect.RequestQueue;
import HTTPConnect.Request_Params;
import HTTPConnect.Response;
import HTTPConnect.Responses_Format;
import Utils.StringUtils;
import Utils.Utilities;

/**
 * Activity for approving a payment of a member
 */
public class Houseshare_Confirm_Payment extends Activity {

    private Payment targetPayment;
    private String username;
    private String payerName;

    private TextView paymentMethod_TextView;
    private TextView date_TextView;
    private TextView message_TextView;
    private TextView amount_TextView;
    private TextView confirm_Button;
    private TextView reject_Button;

    private Request paymentApprovingRequest;
    private Request paymentRejectingRequest;

    private enum MODE {APPROVE, REJECT};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_houseshare__confirm__payment);

        Intent i = getIntent();
        targetPayment =  (Payment) i.getParcelableArrayListExtra(IntentConstants.PAYMENTS).get(0);
        username = i.getStringExtra(IntentConstants.USERNAME);
        payerName = i.getStringExtra(IntentConstants.PAYER_NAME);

        Log.d("target payment", targetPayment.toString());

        if (getActionBar() != null)
            getActionBar().setTitle("Payment of " + payerName);


        paymentMethod_TextView = (TextView) findViewById(R.id.confirm_payment_method_value);
        date_TextView = (TextView) findViewById(R.id.confirm_payment_date_value);
        message_TextView = (TextView) findViewById(R.id.confirm_payment_message_value);
        amount_TextView = (TextView) findViewById(R.id.confirm_payment_amount_value);

        //set up payment information
        paymentMethod_TextView.setText(Houseshare_Payments.method_name[targetPayment.getPayMethod()]);
        date_TextView.setText(StringUtils.getGeneralDateString(targetPayment.getDatePaid()));
        message_TextView.setText(targetPayment.getMessage());
        message_TextView.setFocusable(false);
        amount_TextView.setText(String.valueOf(targetPayment.getAmount()));

        confirm_Button = (TextView) findViewById(R.id.payment_confirm);
        confirm_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Confirm_Payment_Worker(Houseshare_Confirm_Payment.this, true, MODE.APPROVE)
                        .setMsg("Processing").
                        execute(new RequestQueue().addRequest(getPaymentApprovingRequest()).toList());
            }
        });

        reject_Button = (TextView) findViewById(R.id.payment_reject);
        reject_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Confirm_Payment_Worker(Houseshare_Confirm_Payment.this, true, MODE.REJECT)
                        .setMsg("Processing").
                        execute(new RequestQueue().addRequest(getPaymentRejectingRequest()).toList());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_houseshare__payments, menu);
        return true;
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


    class Confirm_Payment_Worker extends ConcurrentConnection {
        private MODE mode;

        public Confirm_Payment_Worker(Activity a, MODE mode) {
            super(a);
            this.mode = mode;
        }

        /**
         * Constructor #2. <br/>
         *
         * @param a          the calling activity
         * @param showDialog whether or not to show the progress dialog while doing computation
         */
        public Confirm_Payment_Worker(Activity a, boolean showDialog, MODE mode) {
            super(a, showDialog);
            this.mode = mode;
        }

        @Override
        protected void onPostExecute(List<Response> responses) {
            super.onPostExecute(responses);
            Response r = responses.get(0);
            if (r.getToken(Responses_Format.RESPONSE_EXPIRED).equals("true"))
                Utilities.showAutoLogoutDialog(Houseshare_Confirm_Payment.this, username);
            else {
                if (r.getToken(Responses_Format.RESPONSE_STATUS).equals("true"))

                    new CustomMessageBox.MessageBoxBuilder
                            (Houseshare_Confirm_Payment.this,
                                    "You have " +
                                            (mode == MODE.APPROVE ? "approved" : "rejected" ) +
                                            " the payment of " + payerName + ".").setActionOnClick(
                            new CustomMessageBox.ToClick() {
                                @Override
                                public void DoOnClick() {
                                    finish();
                                }
                            }
                    ).build();
                else
                    new CustomMessageBox.MessageBoxBuilder
                            (Houseshare_Confirm_Payment.this,
                                    "Sorry, we could not process your request at the moment. " +
                                            "Please try again later.").setActionOnClick(new CustomMessageBox.ToClick() {
                        @Override
                        public void DoOnClick() {
                            finish();
                        }
                    })
                            .build();

            }
        }
    }


    /**
     * return the request used to approve a payment, or initialise it if needed to
     * @return the initialised request
     */
    private Request getPaymentApprovingRequest() {
        if (paymentApprovingRequest != null)
            return paymentApprovingRequest;
        else {
            paymentApprovingRequest = new Request(Request.TYPE.POST)
                    .addParam(Request_Params.PARAM_USR, username)
                    .addParam(Request_Params.PARAM_TYPE, Request_Params.PARAM_APPROVE_PAYMENT)
                    .addParam(Request_Params.REQUEST_HS_BILL_ID, targetPayment.getBillID())
                    .addParam(Request_Params.PARAM_HOUSESHAREID, targetPayment.getHsid());

            return paymentApprovingRequest;
        }

    }

    /**
     * return the request used to reject a payment, or initialise it if needed to
     * @return the initialised request
     */
    private Request getPaymentRejectingRequest() {
        if (paymentRejectingRequest != null)
            return paymentRejectingRequest;
        else {
            paymentRejectingRequest = new Request(Request.TYPE.POST)
                    .addParam(Request_Params.PARAM_USR, username)
                    .addParam(Request_Params.PARAM_TYPE, Request_Params.PARAM_REJECT_PAYMENT)
                    .addParam(Request_Params.REQUEST_HS_BILL_ID, targetPayment.getBillID())
                    .addParam(Request_Params.PARAM_HOUSESHAREID, targetPayment.getHsid());

            return paymentRejectingRequest;
        }

    }
}
