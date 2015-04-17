package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.content.SharedPreferences;

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


public class GetNotification {

    /* -- Variables -- */
    private Activity a;

    private String username;
    private String date;
    private String result;

    private boolean returnedBool;

    private List<String> accountNums = new ArrayList<String>();

    private enum NotificationType{
        ACCOUNTS,
        TRANSCATIONS
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
    public boolean getNotifications(Activity a, String username, String date)
    {

        this.a = a;
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
//        new notificationWorker(a, NotificationType.TRANSCATIONS).execute(transQueue.toList());


        //return returnedBool;

        return getNotif();
    }

    /* Get all of the account numbers for the account, so notifications can be taken for all of them */
    private void getAllAccounts()
    {
        /* Open connection */
        Connection hc = new Connection(a);

        try {
            /* Get the result */
            String result = hc.execute("TYPE","SAA", Request_Params.PARAM_USR, username ).get();

            /* Create the JSON object */
            JSONObject jo = new JSONObject(result);

            /* If expired just return */
            if(jo.getString("expired").equals("true"))
            {
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

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /* Compare the times of transactions with the last login time, any time after last login
     * is flagged as new notification */
    private boolean getNotif() {

        SharedPreferences timeLogout = a.getSharedPreferences(username, 0);
        String logTime = timeLogout.getString("LOGOUT_TIME", date);


        //for()


        //transRequest.addParam("TYPE", "TRANSLIST").addParam(Request_Params.PARAM_USR, username).addParam("ACC_NUMBER", accountNum);


        getAllAccounts();

        for (int k = 0; k < accountNums.size(); k++) {

            String accountNum = accountNums.get(k);

            SharedPreferences settings = a.getSharedPreferences("transinsession", 0);
            boolean transInSession = settings.getBoolean("IN_SESSION", false);



            /* Start the connection */
            Connection hc = new Connection(a);

            try {
                /* This is the command needed for the transactions, takes username and account number, returns JSON String */
                result = hc.execute("TYPE", "TRANSLIST", Request_Params.PARAM_USR, username, "ACC_NUMBER", accountNum).get();

                /* Tries to convert to JSON Object, can throw JSON Exception */
                JSONObject jo = new JSONObject(result);

                /* Check if account has expired (very unlikely as this is called after get accounts) */
                if (jo.getString("expired").equals("true")) {
                    return false;
                } else {
                    /* Array needed as transaction returned inside JSON array */
                    JSONArray jsonArray = jo.getJSONArray("transactions");

                    /* There are no transactions if the array length is zero */
                    if (jsonArray.length() == 0) {
                        return false;
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
                        if(timeFromResponse.compareTo(logoutTime) > 0 && timeFromResponse.compareTo(loginDate) < 0 && !transInSession)
                        {
                            return true;
                        }
                    }

                    return false;
                }
            }
            /* Catch the exceptions */
            catch (JSONException jse) {
            /* Exception for when the JSON cannot be parsed correctly */
                new CustomMessageBox(a, "There was an error in the server response");
                jse.printStackTrace();
            } catch (InterruptedException interex) {
            /* Caused when the connection is interrupted */
                new CustomMessageBox(a, "Connection has been interrupted");
                interex.printStackTrace();
            } catch (ExecutionException ee) {
            /* No idea when this is caused but it throws it... */
                new CustomMessageBox(a, "Execution Error");
                ee.printStackTrace();
            } catch (Exception e) {
            /* Failsafe if something goes utterly wrong */
                new CustomMessageBox(a, "An unknown error occurred");
                e.printStackTrace();
            }
        }
        return false;
    }



//    public class notificationWorker extends ConcurrentConnection{
//
//        private NotificationType mode;
//
//        public notificationWorker(Activity a, NotificationType mode) {
//            super(a);
//            this.mode = mode;
//        }
//
//        @Override
//        protected void onPreExecute()
//        {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected void onPostExecute(List<Response> responses)
//        {
//            super.onPostExecute(responses);
//
//            switch(mode) {
//                case ACCOUNTS:
//
//                    try {
//
//                        /* Create the JSON object */
//                        JSONObject jo = responses.get(0).getJsonObject();
//
//                        /* If expired just return */
//                        if (jo.getString("expired").equals("true")) {
//                            return;
//                        }
//                        /* Get all of the account numbers */
//                        else {
//
//                            JSONArray jsonArray = jo.getJSONArray("accounts");
//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                JSONObject insideObject = jsonArray.getJSONObject(i);
//                                accountNums.add(insideObject.getString("account_number"));
//                            }
//                        }
//                    }/* Catch the exceptions */
//                    catch (JSONException jse) {
//                        /* Exception for when the JSON cannot be parsed correctly */
//                        new CustomMessageBox(a, "There was an error in the server response");
//                        jse.printStackTrace();
//                    } catch (Exception e) {
//                        /* Failsafe if something goes utterly wrong */
//                        new CustomMessageBox(a, "An unknown error occurred retrieving your accounts");
//                        e.printStackTrace();
//                    }
//                    break;
//
//                case TRANSCATIONS:
//
//                    SharedPreferences settings = a.getSharedPreferences("transinsession", 0);
//                    boolean transInSession = settings.getBoolean("IN_SESSION", false);
//
//                    SharedPreferences timeLogout = a.getSharedPreferences(username, 0);
//                    String logTime = timeLogout.getString("LOGOUT_TIME", date);
//
//                    for(int i = 0; i < accountNums.size(); i++) {
//
//                        try {
//                        /* This is the command needed for the transactions, takes username and account number, returns JSON String */
//
//
//                        /* Tries to convert to JSON Object, can throw JSON Exception */
//                            JSONObject jo = responses.get(i).getJsonObject();
//
//                        /* Check if account has expired (very unlikely as this is called after get accounts) */
//                            if (jo.getString("expired").equals("true")) {
//                                returnedBool = false;
//                            } else {
//                            /* Array needed as transaction returned inside JSON array */
//                                JSONArray jsonArray = jo.getJSONArray("transactions");
//
//                            /* There are no transactions if the array length is zero */
//                                if (jsonArray.length() == 0) {
//                                    returnedBool = false;
//                                }
//
//                            /* add the last three payees to the spinner */
//                                for (int j = 0; j < jsonArray.length(); j++) {
//
//                                /* Gets an object of each element in the array */
//                                    JSONObject insideObject = jsonArray.getJSONObject(j);
//
//                                /* Gets the date field and parse it into a date variable, can throw an exception but never should... */
//                                    String dateString = insideObject.getString("Time");
//                                    Date timeFromResponse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
//                                    Date logoutTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(logTime);
//                                    Date loginDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
//
//                                /* If there are any notifications at all, return true */
//                                    if (timeFromResponse.compareTo(logoutTime) > 0 && timeFromResponse.compareTo(loginDate) < 0 && !transInSession) {
//                                        returnedBool = true;
//                                    }
//                                }
//
//                                returnedBool = false;
//                            }
//                        }
//                    /* Catch the exceptions */ catch (JSONException jse) {
//                         /* Exception for when the JSON cannot be parsed correctly */
//                            new CustomMessageBox(a, "There was an error in the server response");
//                            jse.printStackTrace();
//                        } catch (Exception e) {
//                        /* Failsafe if something goes utterly wrong */
//                            new CustomMessageBox(a, "An unknown error occurred");
//                            e.printStackTrace();
//                        }
//                    }
//                    break;
//            }
//
//
//
//            getNotif();
//        }
//
//
//    }

}
