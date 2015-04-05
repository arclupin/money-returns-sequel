package HTTPConnect;

import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper class for a list of requests
 *
 * Created by Thanh on 04-Apr-15.
 */
public class RequestQueue {
    private List<Request> queue;

    public RequestQueue() {
        queue = new ArrayList<Request>();
    }

    public RequestQueue addRequest(Request r) {
        queue.add(r);

        return this;
    }

    public RequestQueue addRequests(Request... rs) {
        for (int i = 0; i < rs.length; i++)
            queue.add(rs[i]);

        return this;
    }

    public List<Request> toList() {
        return queue;
    }

}
