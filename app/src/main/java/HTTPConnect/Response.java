package HTTPConnect;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class response represents a response from the server
 *
 * Created by Thanh on 04-Apr-15.
 */
public class Response {


    private String raw_response;
    private JSONObject jsonObject;

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public String getRaw_response() {
        return raw_response;
    }

    public Response(String r) {
        this.raw_response = r;
        try {
            jsonObject = new JSONObject(r);
        } catch (JSONException e) {
            Log.e("response construct err", e.getMessage(), e);
        }
    }

    public String getToken(String key) {
        String token = null;
        try {
           token = jsonObject.getString(key);
        } catch (JSONException e) {
            Log.e("json err", e.getMessage(), e);
        }
        return token;
    }


    public JSONArray getJSONArray(String name) {
        JSONArray j = null;
        try {
            j = jsonObject.getJSONArray(name);
        } catch (JSONException e) {
            Log.e("json err", e.getMessage(), e);
        }
        return j;
    }





}
