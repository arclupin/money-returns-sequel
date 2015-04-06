package HTTPConnect;
import android.app.Activity;

/* This is the main class that the login uses to get the data from
 * the server. It uses the http connection stuff provided by android/java
 * but also does this in the background by extending ASyncTask.
 */

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.ncl.team5.lloydsmockup.CustomMessageBox;
import com.ncl.team5.lloydsmockup.KillApp;
import com.ncl.team5.lloydsmockup.Login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import Utils.Utilities;


/**
 * The bit at the end.. the String, Void, Boolean bit, thats what
 * ASyncTask uses to do its stuff. The String is the inputs, Void is progress
 * (if progress bar is needed) and Boolean is return type. However
 * They cannot be primitives so the wrappers are needed <br/>
 *
 * Consider using {@link HTTPConnect.ConcurrentConnection} (based on this class) instead for a neater and quicker solution
 * @see HTTPConnect.ConcurrentConnection
 */
public class Connection extends AsyncTask <String, Void, String>  {

    /* String to store the web address as a constant */
    public static final String URL = "http://homepages.cs.ncl.ac.uk/2014-15/csc2022_team5/PHP/main.php";
    private HttpClient httpclient = new DefaultHttpClient();
    private CookieStore cookies;
    private HttpContext context = new BasicHttpContext();
    private Activity a;

    private MODE mode = MODE.SHORT_TASK;
    public static enum MODE {SHORT_TASK, LONG_TASK, LONG_NO_DIALOG_TASK};

    public ProgressDialog getD() {
        return d;
    }

    private ProgressDialog d;
    private String text_dialog;

    private long expected_end_time;
    public static long EXPECTED_DURATION_LONG_TASK = 1000; // 1 seconds is the appropriate choice for long task I guess.
    public Connection(Activity a) {
        this.a = a;
    }



    /* This is where the magic happens. This is what is run when the
     * background thread is started. It takes as parameters a list of strings of any length
     * and uses just the first 2 as username and password. This then calls the connect
     * method where more stuff happens :0 */

//    }

    @Override
    protected void onPreExecute() {
        if (mode == MODE.LONG_NO_DIALOG_TASK || mode == MODE.LONG_TASK)
            expected_end_time = System.currentTimeMillis() + EXPECTED_DURATION_LONG_TASK;
        else
            expected_end_time = System.currentTimeMillis(); // unachievable :P
        if (mode == MODE.LONG_TASK) {
            d = new ProgressDialog(a);
            d.setMessage( text_dialog != null ? text_dialog : "Loading");
            d.show();
        }

    }

    @Override
    protected  void onPostExecute(String result) {
       if (d != null && d.isShowing())
        d.dismiss();

    }

    public Connection setMode(MODE mode) {
        this.mode = mode;
        return this;
    }

    public Connection setDialogMessage(String m)  {
        if (this.mode == MODE.LONG_TASK)
            this.text_dialog = m;
        return this;
    }

    /**
     * set the expected time for this task to finish
     * @param howLong
     * @return
     */
    public Connection setTimeExpected(long howLong) {
        EXPECTED_DURATION_LONG_TASK = howLong;
        return this;
    }

     protected String doInBackground(String...strings) {
        //Set up the name value pair stuff...
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

        for(int i = 0; i < strings.length; i= i+2)
        {

            //use this when danh sorts acronyms
            nameValuePairs.add(new BasicNameValuePair(strings[i], strings[i+1]));
        }

        try
        {

            if(strings[1].equals("LOGIN"))
            {

                String r = this.loginConnect(nameValuePairs);
                // this bit guarantees that the task will execute for at least 2 seconds (not too long, not too short)
                Utilities.delayUntil(expected_end_time);
                return r;
            }
            String r = this.connect(nameValuePairs);
            // this bit guarantees that the task will execute for at least 2 seconds (not too long, not too short)
            Utilities.delayUntil(expected_end_time);
            return r;

        }
        catch(IOException e)
        {
            return "error";
        }
    }

    /* This is the method that actually does the work, it connects to the
     * HTTP server, uses post to give it the username and password, gets the
     * input from the server, turns it into a JSON object, and then sees if it can log in */

     public String connect(List<NameValuePair> nameValuePairs) throws IOException {
         httpclient = new DefaultHttpClient();
         cookies = new BasicCookieStore();

         // initialise the cookieStorage (read cookies from file)
         CookieStorage cookieStorage = new CookieStorage(this.a.getSharedPreferences("cookies", Activity.MODE_PRIVATE));
         BasicClientCookie stored_cookie = cookieStorage.pullFromFile("PHPSESSID");

         // cookies to be sent to the server MUST have the domain and path, otherwise the server will just ignore it.
         // the params could just be stored in the file as well, could be done later.
         stored_cookie.setDomain("homepages.cs.ncl.ac.uk");
         stored_cookie.setPath("/");

         cookies.addCookie(stored_cookie );// add this cookie (session id) to the post request
         Log.d("before connect cookies",cookies.getCookies().get(0).toString());

         context = new BasicHttpContext();
         context.setAttribute(ClientContext.COOKIE_STORE,cookies);
         HttpPost httppost = new HttpPost(URL);

        try {
            /* Creates name value pair to be sent via post to the server */
//            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//            nameValuePairs.add(new BasicNameValuePair("username", username));
//            nameValuePairs.add(new BasicNameValuePair("password", password));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            /* Execute the post request and save the response */
            HttpResponse response = httpclient.execute(httppost, context);

            Log.d("after connect cookies",cookies.toString());

            HttpEntity entity = response.getEntity();

            /* Input stream for the result */
            InputStream inputStream = entity.getContent();
            /* Reads the data */
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            /* Use a string builder to create a string of all the JSON stuff */
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }

            String result = sb.toString();

            Log.d("server", result);

            /* check the value of status */
            try {
                JSONObject jObject = new JSONObject(result);


                return jObject.toString();



            }catch(Exception e)
            {
                throw new IOException("Error parsing JSON");
            }

        } catch (ClientProtocolException e) {
            throw new IOException("Connection could not be established");

        } catch (IOException e) {
            throw new IOException("Connection could not be established");

        }


    }



    public String loginConnect(List<NameValuePair> nameValuePairs) throws IOException {
        // Create a new HttpClient and Post Header
        httpclient = new DefaultHttpClient();
        cookies = new BasicCookieStore();
        context = new BasicHttpContext();
        context.setAttribute(ClientContext.COOKIE_STORE,cookies);
        HttpPost httppost = new HttpPost(URL);
        try {

            Log.d("test", "test");

            /* Creates name value pair to be sent via post to the server */
//            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//            nameValuePairs.add(new BasicNameValuePair("username", username));
//            nameValuePairs.add(new BasicNameValuePair("password", password));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            /* Execute the post request and save the response */
            HttpResponse response = httpclient.execute(httppost, context);
            if(cookies.getCookies().size() == 0)
            {
                Log.d("cookies", "no cookies found");
                //TODO DELETE THE OLD COOKIE
                //return "false";
            }
            else {

                Log.d("cookies", cookies.toString());

                BasicClientCookie cookie = (BasicClientCookie) cookies.getCookies().get(0);
                Log.d("login cookies", cookies.getCookies().get(0).toString());
                CookieStorage cookieStorage = new CookieStorage(this.a.getSharedPreferences("cookies", Activity.MODE_PRIVATE));
                cookieStorage.writeToFile(cookies.getCookies().get(0).getName(), cookies.getCookies().get(0).getValue()); // write this cookie to file
//            BasicClientCookie storedCookie = (BasicClientCookie)cookieStorage.pullFromFile(cookie.getName());
                //          storedCookie.setDomain(cookie.getDomain());
                //        storedCookie.getPath(cookie.getPath());
            }

            HttpEntity entity = response.getEntity();

            /* Input stream for the result */
            InputStream inputStream = entity.getContent();
            /* Reads the data */
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            /* Use a string builder to create a string of all the JSON stuff */
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }

            String result = sb.toString();

            Log.d("server", result);

            /* check the value of status */
            try {
                JSONObject jObject = new JSONObject(result);
                return jObject.toString();
            }catch(Exception e)
            {
                Log.d("error", "JSON PARSE ERROR");
                throw new IOException("Error parsing JSON");
            }

        } catch (ClientProtocolException e) {
            Log.d("error", "connection could not be established");
            throw new IOException("Connection could not be established");

        } catch (IOException e) {
            Log.d("error", "connection could not be established");
            throw new IOException("Connection could not be established");
        }


    }
    public void autoLogout(String username) {

        /* Start a new connection */
        try {
            /* try to execute a logout on the server */
            this.execute("TYPE", "LOGOUT", Request_Params.PARAM_USR, username);
        }
        catch (Exception e) {
            /* Doesnt really need a detailed error as user is logged out anyway, just print stack trace */
            e.printStackTrace();
        }

        /* Has to kill the app whether it has managed to send a logout or not */
        ((KillApp) this.a.getApplication()).setStatus(false);
        this.a.finish();
        Intent intent1 = new Intent(this.a.getApplicationContext(), Login.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.a.startActivity(intent1);
    }

    public JSONObject connect_js(final String username, String... strings) {
        String result;
        JSONObject jo = null;
        try {
            /* Command required to make a payment, takes username, to account, from account, both sort codes and amount
             * Returns: JSON String */
            result = this.execute(strings).get();
            /* Turns String into JSON object, can throw JSON Exception */
            jo = new JSONObject(result);

            /* Check if the user has timed out */
            if (jo.getString("expired").equals("true")) {

                /* Display message box and auto logout user */
                AlertDialog.Builder errorBox = new AlertDialog.Builder(a);
                errorBox.setMessage("Your session has been timed out, please login again")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                autoLogout(username);
                            }
                        });
                AlertDialog alert = errorBox.create();
                alert.show();
                return null;
            } else return jo;
        }
        /* Catch the exceptions */
        catch (JSONException jse) {
            /* Error in the JSON response */
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

        return jo;
    } }