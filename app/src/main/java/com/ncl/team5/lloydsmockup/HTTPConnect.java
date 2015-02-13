package com.ncl.team5.lloydsmockup;


/* This is the main class that the login uses to get the data from
 * the server. It uses the http connection stuff provided by android/java
 * but also does this in the background by extending ASyncTask.
 */

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

/* The bit at the end.. the String, Void, Boolean bit, thats what
 * ASyncTask uses to do its stuff. The String is the inputs, Void is progress
 * (if progress bar is needed) and Boolean is return type. However
 * They cannot be primitives so the wrappers are needed */
public class HTTPConnect extends AsyncTask <String, Void, String> {

    /* String to store the web address as a constant */
    private final String URL = "http://homepages.cs.ncl.ac.uk/2014-15/csc2022_team5/login.php";
    private String result;

    /* This is where the magic happens. This is what is run when the
     * background thread is started. It takes as parameters a list of strings of any length
     * and uses just the first 2 as username and password. This then calls the connect
     * method where more stuff happens :0 */
    protected String doInBackground(String...strings) {

        //Set up the name value pair stuff...
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

        for(int i = 0; i < strings.length; i= i+2)
        {
            //Use this line Until danh has updated server to use the accroynms
            nameValuePairs.add(new BasicNameValuePair("username", strings[1]));
            nameValuePairs.add(new BasicNameValuePair("password", strings[3]));
            break;

            //use this when danh sorts acronyms
            //nameValuePairs.add(new BasicNameValuePair(strings[i], strings[i+1]));
        }

        try
        {
            return this.connect(nameValuePairs);

//            if(result)
//            {
//                return 0;
//            }
//            else
//            {
//                return 1;
//            }
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
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(URL);

        try {
            /* Creates name value pair to be sent via post to the server */
//            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//            nameValuePairs.add(new BasicNameValuePair("username", username));
//            nameValuePairs.add(new BasicNameValuePair("password", password));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            /* Execute the post request and save the response */
            HttpResponse response = httpclient.execute(httppost);
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

            /* check the value of status */
            try {
                JSONObject jObject = new JSONObject(result);


                return jObject.toString();

                /* if true then login, else dont log in */
//                if(returnedValue.equals("true"))
//                {
//                    return true;
//                }
//                else
//                {
//                    return false;
//                }

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



}