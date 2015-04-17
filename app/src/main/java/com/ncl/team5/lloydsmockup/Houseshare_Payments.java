package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import Fragments.DatePickerFragment;
import Fragments.HS_Payment_Method_Dialog;
import HTTPConnect.ConcurrentConnection;
import HTTPConnect.Request;
import HTTPConnect.RequestQueue;
import HTTPConnect.Request_Params;
import HTTPConnect.Response;
import HTTPConnect.Responses_Format;
import Utils.StringUtils;
import Utils.Utilities;


public class Houseshare_Payments extends Activity implements DatePickerFragment.DatePickerListener,
        HS_Payment_Method_Dialog.PaymentMethodDialogListener{

    public static final int BANK_TRANSFER = 1;
    public static final int CASH = 2;
    public static final int OTHER_METHODS = 3;

    public static final String[] method_name = {"Not Specified", "Bank transfer", "Cash", "Other method"};

    private double amount;
    private String message;
    private String other_method;
    private Date datePaid;
    private String hsid;
    private String billID;
    private String username;
    private int paymentMethod;

    private RelativeLayout paymentMethod_Layout;
    private RelativeLayout datePicker_Layout;
    private EditText message_Layout;
    private TextView confirm_Button;
    private TextView message_title;
    private Request paymentConfirmingRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_houseshare__payments);

        Intent i = getIntent();
        amount = i.getDoubleExtra(IntentConstants.BILL_AMOUNT, 0);
        hsid = i.getStringExtra(IntentConstants.HOUSESHARE_ID);
        billID = i.getStringExtra(IntentConstants.BILL_ID);
        username = i.getStringExtra(IntentConstants.USERNAME);


        paymentMethod_Layout = (RelativeLayout) findViewById(R.id.payment_method);
        paymentMethod_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HS_Payment_Method_Dialog f = HS_Payment_Method_Dialog.initialise();
                f.show(getFragmentManager(), "PaymentMethod_Frag");
            }
        });

        datePicker_Layout = (RelativeLayout) findViewById(R.id.payment_date);
        datePicker_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment f = new DatePickerFragment();
                f.show(getFragmentManager(), "DatePicker_Frag");
            }
        });
        message_Layout = (EditText) findViewById(R.id.payment_message_value);
        message_Layout.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                message = s.toString();
            }
        });

        confirm_Button = (TextView) findViewById(R.id.payment_confirm);
        confirm_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDataValid())
                    new Payment_Worker(Houseshare_Payments.this, true).setMsg("Confirming your payment")
                    .execute(new RequestQueue().addRequest(getPaymentConfirmingRequest()).toList());
                else
                    new CustomMessageBox.MessageBoxBuilder(Houseshare_Payments.this,
                            "Something wrong with your data. Please review your data.")
                            .setTitle("Warning").build();
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

    private boolean isDataValid() {
        return amount != 0.0 && datePaid != null && paymentMethod != 0 &&
                !StringUtils.isFieldEmpty(message);
    }

    @Override
    public void onDatePicked(Date date) {
        this.datePaid = date;
        ((TextView) findViewById(R.id.payment_date_value)).setText(StringUtils.getStringFromDate(datePaid, "dd/MM/yyyy"));
    }

    @Override
    public void onMethodChosenClicked(HS_Payment_Method_Dialog f, int which, String other) {
        f.dismiss();

        ((TextView) findViewById(R.id.payment_method_value)).setText(method_name[which]);
        paymentMethod = which;
        other_method = other;
    }


    class Payment_Worker extends ConcurrentConnection {

        public Payment_Worker(Activity a) {
            super(a);
        }

        /**
         * Constructor #2. <br/>
         *
         * @param a          the calling activity
         * @param showDialog whether or not to show the progress dialog while doing computation
         */
        public Payment_Worker(Activity a, boolean showDialog) {
            super(a, showDialog);
        }

        @Override
        protected void onPostExecute(List<Response> responses) {
            super.onPostExecute(responses);
            Response r = responses.get(0);
            if (r.getToken(Responses_Format.RESPONSE_EXPIRED).equals("true"))
                Utilities.showAutoLogoutDialog(Houseshare_Payments.this, username);
            else {
                if (r.getToken(Responses_Format.RESPONSE_STATUS).equals("true"))
                    new CustomMessageBox.MessageBoxBuilder
                            (Houseshare_Payments.this,"You have submitted you payment. " +
                                    "Your bill creator will be notified soon.").setActionOnClick(
                            new CustomMessageBox.ToClick() {
                                @Override
                                public void DoOnClick() {
                                    finish();
                                }
                            }
                    )
                            .build();
                else
                    new CustomMessageBox.MessageBoxBuilder
                            (Houseshare_Payments.this,
                                    "Sorry, we could not process your request at the moment. " +
                                            "Please try again later.")
                            .build();
            }
        }
    }

    private Request getPaymentConfirmingRequest() {
        if (paymentConfirmingRequest != null)
            return paymentConfirmingRequest;
        else {
            paymentConfirmingRequest = new Request(Request.TYPE.POST)
                    .addParam(Request_Params.PARAM_USR, username)
                    .addParam(Request_Params.PARAM_TYPE, Request_Params.REQUEST_HS_CONFIRM_PAYMENT)
                    .addParam(Request_Params.REQUEST_HS_BILL_ID, billID)
                    .addParam(Request_Params.REQUEST_HS_PAY_METHOD, String.valueOf(paymentMethod))
                    .addParam(Request_Params.REQUEST_HS_AMOUNT, String.valueOf(amount))
                    .addParam(Request_Params.REQUEST_HS_PAY_MSG, message)
                    .addParam(Request_Params.REQUEST_HS_DATE_PAID,
                            StringUtils.getStringFromDate(datePaid, "yyyy-MM-dd"));
            return paymentConfirmingRequest;
        }

    }
}
