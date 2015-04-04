package HTTPConnect;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class request representing an request to be sent to the server.
 *
 * Created by Thanh on 04-Apr-15.
 */
public class Request {
   private List<BasicNameValuePair> params;

   public static enum TYPE {POST, GET};
    private TYPE type;

    public Request(TYPE t) {
        params = new ArrayList<BasicNameValuePair>();
        this.type = t;
    }

    public Request addParam(String key, String value) {
        params.add(new BasicNameValuePair(key, value));
        return this;
    }

    public List<BasicNameValuePair> getParams() {
        return params;
    }

    public TYPE getType() {
        return type;
    }

}
