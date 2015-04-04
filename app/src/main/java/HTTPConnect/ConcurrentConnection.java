package HTTPConnect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;

import com.ncl.team5.lloydsmockup.BuildConfig;

import java.util.ArrayList;
import java.util.List;

import Utils.StringUtils;

/**
 * Allow multiple requests to be sent in one async task
 *
 * Created by Thanh on 04-Apr-15.
 */
public class ConcurrentConnection extends AsyncTask<List<Request>, Void, List<Response>>{
    private Activity mContext;
    private ProgressDialog mDialog;
    private boolean showDialog;
    private String dialogMsg;

    @Override
    protected void onPreExecute() {
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


    public ConcurrentConnection(Activity a, boolean showDialog) {
        this.showDialog = showDialog;
        mContext = a;
    }

    public ConcurrentConnection setMsg(String msg) {
        this.dialogMsg = msg;
        return this;
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
        assert params.length == 1; // some assertion to make sure only 1 list added.

        List<Request> requests = params[0];
        List<Response> responses = new ArrayList<Response>();

        for(int i = 0; i < requests.size(); i++) {
            HTTPHandler handler = new HTTPHandler(mContext);
            responses.add(handler.sendRequest(requests.get(i)));
        }

        return responses;
    }


}
