package HTTPConnect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Utils.StringUtils;
import Utils.Utilities;

/**
 * Class improved from {@link HTTPConnect.Connection} <br/>
 * Allow multiple requests to be sent in one async task <br/>
 * The order of responses in the result adheres to the order of the requests coming in. <br/>
 *
 *
 * This class will be subclassed by any activity requiring connection to the server (which most do)
 * The sub class hence would have a private field (a mode) for each type of request.
 *
 *
 *
 * The mode is used for deciding what to do before and after each request (typically related to UI update). <br/>
 * <u>Example</u>
 * For search activity, a search object in mode MAIN would show a loading spinner before the
 * search, and update the main layout after the search whereas a search in mode SEND_JOIN_REQUEST might
 * just mark a flag for the house the user sent the request to.
 *
 * @see Fragments.Fragment_HS_Home.HomeViewWorker
 * @author Thanh
 */
public class ConcurrentConnection extends AsyncTask<List<Request>, Void, List<Response>>{
    private Activity mContext;
    private ProgressDialog mDialog;
    private boolean showDialog;
    private String dialogMsg;
    private long expected_end_time;
    protected Map<String, String> additional_params;


    /**
     * Constructor #1 <br/>
     * Default option is not to show the dialog
     * @param a the the calling activity
     */;
    public ConcurrentConnection(Activity a) {
        this.mContext = a;
        showDialog = false;
    }

    /**
     * Constructor #2. <br/>
     * @param a the calling activity
     * @param showDialog whether or not to show the progress dialog while doing computation
     *
     */
    public ConcurrentConnection(Activity a, boolean showDialog) {
        this.showDialog = showDialog;
        mContext = a;
    }

    public ConcurrentConnection setMsg(String msg) {
        assert showDialog;
        this.dialogMsg = msg;
        return this;
    }

    @Override
    protected void onPreExecute() {
         expected_end_time = System.currentTimeMillis() + Connection.EXPECTED_DURATION_LONG_TASK;
        if (showDialog) {
            mDialog = new ProgressDialog(mContext);
            mDialog.setMessage(StringUtils.isFieldEmpty(dialogMsg) ? "Loading" : dialogMsg);
            mDialog.show();
        }
    }


    @Override
    protected void onPostExecute(List<Response> responses) {
        if (mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }

    /**
            * Override this method to perform a computation on a background thread. The
    * specified parameters are the parameters passed to {@link #execute}
    * by the caller of this task.
    * <p/>
            * This method can call {@link #publishProgress} to publish updates
    * on the UI thread.
            *
            * @param params The parameters of the task.
            * @return A result, defined by the subclass of this task.
    * @see #onPreExecute()
    * @see #onPostExecute
    * @see #publishProgress
    */
    @Override
    protected List<Response> doInBackground(List<Request>... params) {
        assert params.length == 1; // an assertion to make sure only 1 list is added.

        List<Request> requests = params[0];
        List<Response> responses = new ArrayList<Response>();

        for(int i = 0; i < requests.size(); i++) {
            HTTPHandler handler = new HTTPHandler(mContext);
            responses.add(handler.sendRequest(requests.get(i)));
            Log.d("Response #" + i, responses.get(i).getRaw_response());
        }
        Utilities.delayUntil(expected_end_time);
        return responses;
    }








}
