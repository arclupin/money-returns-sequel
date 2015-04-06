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


    /**
     * Add a request to the tail of the queue
     * @param r the request to be added
     * @return this queue
     */
    public RequestQueue addRequest(Request r) {
        queue.add(r);
        return this;
    }

    /**
     * Add multiple requests at once
     * @param rs an arbitrary number of requests
     * @return this queue
     */
    public RequestQueue addRequests(Request... rs) {
        for (Request r : rs)
            queue.add(r);

        return this;
    }

    /**
     * Return the object as a list of requests
     * @return the list form (the internal list indeed)
     */
    public List<Request> toList() {
        return queue;
    }

}
