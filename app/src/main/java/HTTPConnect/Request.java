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

    /**
     * Add a param + value pair to the request
     * @param key the param name
     * @param value the param value
     * @return this request
     */
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

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(type.name());
        for (int i = 0; i < params.size(); i++)
            b.append(params.get(i).getName()).append(": ").append(params.get(i).getValue()).append(" // ");
        return b.toString();

    }
}
