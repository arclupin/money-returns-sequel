package HTTPConnect;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ncl.team5.lloydsmockup.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

/**
 * Created by Thanh on 25-Mar-15.
 * each object of the Notification class represents a notification
 *
 */
public class Notification {

    public static final int JOIN_ADM_VIEW = 1;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setAdditional_params(List<String> additional_params) {
        this.additional_params = additional_params;
    }

    public List<String> getAdditional_params() {
        return additional_params;
    }

    private int type;
    private List<String> additional_params;

    public Notification() {
        type = JOIN_ADM_VIEW;
        additional_params = new ArrayList<String>();
    }

    public Notification(int type, List<String> arg_params) {
        this.type = type;
        this.additional_params = arg_params;

    }

    public Notification(int type) {
        this.type = type;
        additional_params = new ArrayList<String>();
    }

    public void addParam(String param_value) {
        additional_params.add(param_value);
    }

    public View makeNotiRow(Activity a) {
        LayoutInflater inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = null;

        switch (type) {
            case JOIN_ADM_VIEW: {
                v = inflater.inflate(R.layout.hs_noti_join_req_adm_view, null);
                TextView tv = (TextView) v.findViewById(R.id.noti_join_req_admin_name);
                tv.setText(additional_params.get(0) + " ");
                return v;
            }
        }
        return v;
    }
}








