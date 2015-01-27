package com.ncl.team5.lloydsmockup;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * Created by Ben on 27/01/2015.
 */
public class HTTPConnect {


    /* This class is supposed to connect to my server (at the moment, the server just
     * returns true) which should allow the user into the account (any account). Also this
     * code is by NO MEANS secure. However, poor network conditions are detected every time
     * the login button is pressed, it detects poor network connections (not actually sure if
     * that is the case, just a general error really) and does not log in.
     */

    public boolean getLogin() throws Exception
    {
        BufferedReader in = null;
        String data = null;

        try{
            HttpClient client = new DefaultHttpClient();
            URI website = new URI("http://testforandroid.net84.net/default.php");
            HttpGet request = new HttpGet();
            request.setURI(website);

            //respose, think this is where the problem is...
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String l = "";
            String nl = System.getProperty("line.seporator");

            while((l = in.readLine()) != null)
            {
                sb.append(l + nl);
            }
            in.close();

            data = sb.toString();

            if(data.equals("true"))
            {
                return true;
            }
            else
            {
                return false;
            }
        }finally{
            try
            {
                in.close();
                return false;
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
