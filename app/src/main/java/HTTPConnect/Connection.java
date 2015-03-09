package HTTPConnect;
import android.app.Activity;

/* This is the main class that the login uses to get the data from
 * the server. It uses the http connection stuff provided by android/java
 * but also does this in the background by extending ASyncTask.
 */

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

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
import org.json.JSONObject;
import org.apache.http.conn.ssl.SSLSocketFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;


/* The bit at the end.. the String, Void, Boolean bit, thats what
 * ASyncTask uses to do its stuff. The String is the inputs, Void is progress
 * (if progress bar is needed) and Boolean is return type. However
 * They cannot be primitives so the wrappers are needed */
public class Connection extends AsyncTask <String, Void, String>  {

    /* String to store the web address as a constant */
    private final String URL = "http://homepages.cs.ncl.ac.uk/2014-15/csc2022_team5/PHP/main.php";
    private HttpClient httpclient = new DefaultHttpClient();
    private CookieStore cookies;
    private HttpContext context = new BasicHttpContext();
    private Activity a;

    public Connection(Activity a) {

        this.a = a;
    }



    /* This is where the magic happens. This is what is run when the
     * background thread is started. It takes as parameters a list of strings of any length
     * and uses just the first 2 as username and password. This then calls the connect
     * method where more stuff happens :0 */

//    }

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

                return this.loginConnect(nameValuePairs);
            }



            return this.connect(nameValuePairs);

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
                return "false";
            }

            Log.d("cookies", cookies.toString());

            BasicClientCookie cookie = (BasicClientCookie) cookies.getCookies().get(0);
            Log.d("login cookies",cookies.getCookies().get(0).toString());
            CookieStorage cookieStorage = new CookieStorage(this.a.getSharedPreferences("cookies", Activity.MODE_PRIVATE));
            cookieStorage.writeToFile(cookies.getCookies().get(0).getName(), cookies.getCookies().get(0).getValue()); // write this cookie to file
//            BasicClientCookie storedCookie = (BasicClientCookie)cookieStorage.pullFromFile(cookie.getName());
  //          storedCookie.setDomain(cookie.getDomain());
    //        storedCookie.getPath(cookie.getPath());


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

}