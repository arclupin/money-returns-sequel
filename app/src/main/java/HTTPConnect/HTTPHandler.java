package HTTPConnect;

import android.app.Activity;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Class handling the request post and its response retrieval
 *
 * Created by Thanh on 04-Apr-15.
 */
public class HTTPHandler {
    private HttpContext context;
    private HttpClient client;
    private BasicCookieStore cookies;
    private Activity mContext;

    public HTTPHandler(Activity a) {

        mContext = a;
        context = new BasicHttpContext();
        client  = new DefaultHttpClient();
        cookies = new BasicCookieStore();

        Log.d("mContext", mContext.toString());
        CookieStorage cookieStorage = new CookieStorage(mContext.getSharedPreferences("cookies", Activity.MODE_PRIVATE));
        BasicClientCookie cookie_sessid = cookieStorage.pullFromFile("PHPSESSID"); // pull the cookie from the file

        // set the domain and path for the cookie
        // this must be set, the cookie would not be complete otherwise
        cookie_sessid.setDomain("homepages.cs.ncl.ac.uk");
        cookie_sessid.setPath("/");

        cookies.addCookie(cookie_sessid);
        context.setAttribute(ClientContext.COOKIE_STORE, cookies);
    }

    public Response sendRequest(Request r) {
        Response rp = null;
        if (r.getType() == Request.TYPE.POST)
            rp =  post(r);
        //TODO handle other types of request
        return rp;
    }

    private Response post(Request r) {
        HttpPost httppost = new HttpPost(Connection.URL);
        String result = null;
        try {
            httppost.setEntity(new UrlEncodedFormEntity(r.getParams()));
            HttpResponse response = client.execute(httppost, context);

            Log.d("after connect cookies", cookies.toString());

            HttpEntity entity = response.getEntity();

            /* Input stream for the result */
            InputStream inputStream = entity.getContent();
            /* Reads the data */
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            /* Use a string builder to create a string of all the JSON stuff */
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            result = sb.toString();

        } catch (UnsupportedEncodingException e) {
            Log.e("err_unsupported_url_enc", e.getMessage(), e);
        } catch (ClientProtocolException e) {
            Log.e("client_protocol_exc", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("io_exc", e.getMessage(), e);
        }

        return new Response(result);


    }
}
