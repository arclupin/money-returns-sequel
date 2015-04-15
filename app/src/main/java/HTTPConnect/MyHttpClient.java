package HTTPConnect;

/* PLEASE READ!!!
 *
 * I DID NOT WRITE THIS CLASS!
 * This class has been copied and modified from StackOverFlow
 *
 * Website: Stack Overflow
 * Web Address: http://stackoverflow.com/questions/1217141/self-signed-ssl-acceptance-android
 * Title: Self Signed Acceptance Android
 * Date Posted: March 20th 2013, 18:09
 * Author: Frederic Yesid Peña Sánc
 * Date Accessed: April 15th 2015, 01:53
 *
 *
 * The problem with using ssl was that the university uses self signed certificates.
 * the app now uses a keystore found in Res/raw/certs.bks. This file was created using
 * software downloaded from http://www.bouncycastle.org/download/bcprov-jdk15on-146.jar
 * on April 15th 2015.
 *
 * This keystore is what the app uses to authenticate with the server, as it is created
 * from the actual server certificate.
 *
 * This process would have been much simpler by using HttpConnection, but Hindsight is a
 * wonderful thing :/.
 * */

import android.content.Context;
import android.util.Log;

import com.ncl.team5.lloydsmockup.R;

import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;

public class MyHttpClient extends DefaultHttpClient {

    private static Context appContext = null;
    private static Scheme httpsScheme = null;
    private static Scheme httpScheme = null;
    private static String TAG = "MyHttpClient";

    public MyHttpClient(Context myContext) {

        appContext = myContext;

        if (httpScheme == null || httpsScheme == null) {
            httpScheme = new Scheme("http", PlainSocketFactory.getSocketFactory(), 80);
            httpsScheme = new Scheme("https", mySSLSocketFactory(), 443);
        }

        getConnectionManager().getSchemeRegistry().register(httpScheme);
        getConnectionManager().getSchemeRegistry().register(httpsScheme);

    }

    private SSLSocketFactory mySSLSocketFactory() {
        SSLSocketFactory ret = null;
        try {
            final KeyStore ks = KeyStore.getInstance("BKS");

            final InputStream inputStream = appContext.getResources().openRawResource(R.raw.certs);

            ks.load(inputStream, appContext.getString(R.string.store_pass).toCharArray());
            inputStream.close();

            ret = new SSLSocketFactory(ks);
        } catch (UnrecoverableKeyException ex) {
            Log.d(TAG, ex.getMessage());
        } catch (KeyStoreException ex) {
            Log.d(TAG, ex.getMessage());
        } catch (KeyManagementException ex) {
            Log.d(TAG, ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            Log.d(TAG, ex.getMessage());
        } catch (IOException ex) {
            Log.d(TAG, ex.getMessage());
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        } finally {
            return ret;
        }
    }

}