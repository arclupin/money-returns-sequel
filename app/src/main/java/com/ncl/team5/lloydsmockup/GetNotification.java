package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import HTTPConnect.ConcurrentConnection;
import HTTPConnect.Connection;
import HTTPConnect.Notification;
import HTTPConnect.Request;
import HTTPConnect.RequestQueue;
import HTTPConnect.Request_Params;
import HTTPConnect.Response;
import Utils.Utilities;


public class GetNotification {

    /* -- Variables -- */
    private Activity a;

    private String username;
    private String date;
    private String result;
    private boolean finalResult;

    private boolean returnedBool;

    private List<String> accountNums = new ArrayList<String>();

    public enum NotificationType {
        ACCOUNTS,
        TRANSACTIONS
    }

    private OnNotiFetchedListener mListener;

    interface OnNotiFetchedListener {
        void onAllTransactionDone(boolean result);
    }

    private Request accRequest;
    private RequestQueue transRequests;

    private Request getAccRequest() {
        if (accRequest != null)
            return accRequest;
        accRequest = new Request(Request.TYPE.POST);
        accRequest.addParam("TYPE", "SAA").addParam(Request_Params.PARAM_USR, username);
        return accRequest;
    }

    private RequestQueue getTransRequest() {
        if (transRequests != null)
            return transRequests;
        transRequests = new RequestQueue();
        for (int i = 0; i < accountNums.size(); i++) {
            transRequests.addRequest(new Request(Request.TYPE.POST).addParam("TYPE", "TRANSLIST")
                    .addParam(Request_Params.PARAM_USR, username).addParam("ACC_NUMBER", accountNums.get(i)));
        }
        return transRequests;
    }

    /* ===================================
         * getNotification method
         *
         * @params : Activity
         *           String
         *           String
         *
         * @return : boolean
         *
         * @use : Called from within the other
         *        classes. calls get notif
         * =================================== */
    public void getNotifications(Activity a, String username, String date) {

        this.a = a;
        mListener = (OnNotiFetchedListener) a;
        this.username = username;
        this.date = date;

//        Request accountRequest = new Request(Request.TYPE.POST);
//        accountRequest.addParam("TYPE", "SAA").addParam(Request_Params.PARAM_USR, username);
//        RequestQueue rq = new RequestQueue();
//
//        new notificationWorker(a, NotificationType.ACCOUNTS).execute(rq.addRequest(accountRequest).toList());
//
//        List<Request> transactionRequests = new ArrayList<Request>();
//        RequestQueue transQueue = new RequestQueue();
//
//        for(int i = 0; i < accountNums.size(); i++)
//        {
//            Request tempTransRequest = new Request(Request.TYPE.POST);
//            tempTransRequest.addParam("TYPE", "TRANSLIST").addParam(Request_Params.PARAM_USR, username).addParam("ACC_NUMBER", accountNums.get(i));
//            transQueue.addRequest(tempTransRequest);
//        }
//
//        new notificationWorker(a, NotificationType.TRANSACTIONS).execute(transQueue.toList());


        //return returnedBool;

        new notificationWorker(a, true, NotificationType.ACCOUNTS)
                .setMsg("Processing")
                .execute(new RequestQueue().addRequest(getAccRequest()).toList());
    }


    /* Get all of the account numbers for the account, so notifications can be taken for all of them */
    private void getAllAccounts() {
        /* Open connection */
        Connection hc = new Connection(a);

        try {
            /* Get the result */
            String result = hc.execute("TYPE", "SAA", Request_Params.PARAM_USR, username).get();

            /* Create the JSON object */
            JSONObject jo = new JSONObject(result);

            /* If expired just return */
            if (jo.getString("expired").equals("true")) {
                return;
            }
            /* Get all of the account numbers */
            else {
                JSONArray jsonArray = jo.getJSONArray("accounts");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject insideObject = jsonArray.getJSONObject(i);
                    accountNums.add(insideObject.getString("account_number"));

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /* Compare the times of transactions with the last login time, any time after last login
     * is flagged as new notification */
    private void getNotif(List<Response> responses) {

        SharedPreferences timeLogout = a.getSharedPreferences(username, 0);
        String logTime = timeLogout.getString("LOGOUT_TIME", date);
        //for()
        //transRequest.addParam("TYPE", "TRANSLIST").addParam(Request_Params.PARAM_USR, username).addParam("ACC_NUMBER", accountNum);

        for (int k = 0; k < accountNums.size(); k++) {
            String accountNum = accountNums.get(k);
            SharedPreferences settings = a.getSharedPreferences("transinsession", 0);
            boolean transInSession = settings.getBoolean("IN_SESSION", false);

            /* Start the connection */
            Connection hc = new Connection(a);

            try {
                /* This is the command needed for the transactions, takes username and account number, returns JSON String */


                /* Tries to convert to JSON Object, can throw JSON Exception */
                JSONObject jo = new JSONObject(responses.get(k).getRaw_response());

                /* Check if account has expired (very unlikely as this is called after get accounts) */
                if (jo.getString("expired").equals("true")) {
                    return;
                } else {
                    /* Array needed as transaction returned inside JSON array */
                    JSONArray jsonArray = jo.getJSONArray("transactions");

                    /* There are no transactions if the array length is zero */
                    if (jsonArray.length() == 0) {
                        mListener.onAllTransactionDone(false);
                        return;
                    }

                    /* add the last three payees to the spinner */
                    for (int i = 0; i < jsonArray.length(); i++) {

                        /* Gets an object of each element in the array */
                        JSONObject insideObject = jsonArray.getJSONObject(i);

                        /* Gets the date field and parse it into a date variable, can throw an exception but never should... */
                        String date = insideObject.getString("Time");
                        Date timeFromResponse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
                        Date logoutTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(logTime);
                        Date loginDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(this.date);

                        /* If there are any notifications at all, return true */
                        if (timeFromResponse.compareTo(logoutTime) > 0 && timeFromResponse.compareTo(loginDate) < 0 && !transInSession) {
                            mListener.onAllTransactionDone(true);
                            return;
                        }
                    }

                    mListener.onAllTransactionDone(false);
                }
            }
            /* Catch the exceptions */ catch (JSONException jse) {
            /* Exception for when the JSON cannot be parsed correctly */
                new CustomMessageBox(a, "There was an error in the server response");
                jse.printStackTrace();
            } catch (Exception e) {
            /* Failsafe if something goes utterly wrong */
                new CustomMessageBox(a, "An unknown error occurred");
                e.printStackTrace();
            }
        }
        mListener.onAllTransactionDone(false);
    }






    public class notificationWorker extends ConcurrentConnection{

        private GetNotification.NotificationType mode;

        public notificationWorker(Activity a, GetNotification.NotificationType mode) {
            super(a);
            this.mode = mode;
        }

        /**
         * Constructor #2. <br/>
         *
         * @param a          the calling activity
         * @param showDialog whether or not to show the progress dialog while doing computation
         */
        public notificationWorker(Activity a, boolean showDialog, GetNotification.NotificationType type) {
            super(a, showDialog);
            this.mode = type;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<Response> responses)
        {
            super.onPostExecute(responses);

            switch(mode) {
                case ACCOUNTS: {

                    try {
                        Log.d("acc req", responses.get(0).getRaw_response());
                        /* Create the JSON object */
                        JSONObject jo = new JSONObject(responses.get(0).getRaw_response());

                        /* If expired just return */
                        if (jo.getString("expired").equals("true")) {
                            return;
                        }
                        /* Get all of the account numbers */
                        else {
                            JSONArray jsonArray = jo.getJSONArray("accounts");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject insideObject = jsonArray.getJSONObject(i);
                                GetNotification.this.accountNums.add(insideObject.getString("account_number"));
                            }
                            new notificationWorker(a, true, NotificationType.TRANSACTIONS)
                                    .setMsg("Loading accounts")
                                    .execute(getTransRequest().toList());
                        }
                    }/* Catch the exceptions */ catch (JSONException jse) {
                        /* Exception for when the JSON cannot be parsed correctly */
                        new CustomMessageBox(a, "There was an error in the server response");
                        jse.printStackTrace();
                    } catch (Exception e) {
                        /* Failsafe if something goes utterly wrong */
                        new CustomMessageBox(a, "An unknown error occurred retrieving your accounts");
                        e.printStackTrace();
                    }
                    break;
                }

                case TRANSACTIONS:

                    SharedPreferences settings = a.getSharedPreferences("transinsession", 0);
                    boolean transInSession = settings.getBoolean("IN_SESSION", false);

                    SharedPreferences timeLogout = a.getSharedPreferences(username, 0);
                    String logTime = timeLogout.getString("LOGOUT_TIME", date);

                    for(int i = 0; i < accountNums.size(); i++) {

                        try {
                        /* This is the command needed for the transactions, takes username and account number, returns JSON String */


                        /* Tries to convert to JSON Object, can throw JSON Exception */
                            JSONObject jo = new JSONObject(responses.get(i).getRaw_response());

                        /* Check if account has expired (very unlikely as this is called after get accounts) */
                            if (jo.getString("expired").equals("true")) {
                                Utilities.showAutoLogoutDialog(a, username);
                            } else {
                            /* Array needed as transaction returned inside JSON array */
                                JSONArray jsonArray = jo.getJSONArray("transactions");

                            /* There are no transactions if the array length is zero */
                                if (jsonArray.length() == 0) {
                                    mListener.onAllTransactionDone(false);
                                    return;
                                }

                            /* add the last three payees to the spinner */
                                for (int j = 0; j < jsonArray.length(); j++) {

                                /* Gets an object of each element in the array */
                                    JSONObject insideObject = jsonArray.getJSONObject(j);

                                /* Gets the date field and parse it into a date variable, can throw an exception but never should... */
                                    String dateString = insideObject.getString("Time");
                                    Date timeFromResponse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
                                    Date logoutTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(logTime);
                                    Date loginDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);

                                /* If there are any notifications at all, return true */
                                    if (timeFromResponse.compareTo(logoutTime) > 0 &&
                                            timeFromResponse.compareTo(loginDate) < 0 && !transInSession) {
                                        mListener.onAllTransactionDone(true);
                                        return;
                                    }
                                }

                                mListener.onAllTransactionDone(false);
                                return;
                            }
                        }
                    /* Catch the exceptions */ catch (JSONException jse) {
                         /* Exception for when the JSON cannot be parsed correctly */
                            new CustomMessageBox(a, "There was an error in the server response");
                            jse.printStackTrace();
                        } catch (Exception e) {
                        /* Failsafe if something goes utterly wrong */
                            new CustomMessageBox(a, "An unknown error occurred");
                            e.printStackTrace();
                        }
                    }
                    break;
            }
//            getNotif(responses);
        }
    }

}
