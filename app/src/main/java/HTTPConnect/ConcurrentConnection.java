package HTTPConnect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.ncl.team5.lloydsmockup.BuildConfig;

import java.util.ArrayList;
import java.util.List;

import Utils.StringUtils;
import Utils.Utilities;

/**
 * Class improved from {@link HTTPConnect.Connection} <br/>
 * Allow multiple requests to be sent in one async task <br/>
 * The order of responses in the result adheres to the order of the requests coming in. <br/>
 *
 * Created by Thanh on 04-Apr-15.
 */
public class ConcurrentConnection extends AsyncTask<List<Request>, Void, List<Response>>{
    private Activity mContext;
    private ProgressDialog mDialog;
    private boolean showDialog;
    private String dialogMsg;
    private long expected_end_time;



    /**
     * Constructor #1 <br/>
     * Default option is not to show the dialog
     * @param a the the calling activity
     */
    public ConcurrentConnection(Activity a) {
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
            Log.d("Response no" + i, responses.get(i).getRaw_response());
        }
        Utilities.delayUntil(expected_end_time);
        return responses;
    }








}
