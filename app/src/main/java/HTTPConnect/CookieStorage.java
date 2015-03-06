package HTTPConnect;

import android.content.SharedPreferences;

import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.net.CookieStore;
import java.util.Date;

/**
 * Created by Thanh on 17/02/2015.
 */
public class CookieStorage {


    private SharedPreferences file;

    public CookieStorage(SharedPreferences file) {
        this.file = file;
    }

    // write the cookie name and value to file, could store the domain and path as well.
    public String writeToFile(String key, String value) {
        String originalValue = this.file.getString(key, "no value");
        SharedPreferences.Editor editor = file.edit();
        editor.putString(key, value);
        editor.apply();
        return originalValue;
}
// 123456
    // retrieve the cookie from file.
    public BasicClientCookie pullFromFile (String key) {
        String value = file.getString(key, "no value");
        return new BasicClientCookie(key, value);
    }

}
