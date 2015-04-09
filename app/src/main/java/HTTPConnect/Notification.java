package HTTPConnect;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ncl.team5.lloydsmockup.R;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Utils.StringUtils;

/**
 * Created by Thanh on 25-Mar-15.
 * each object of the Notification class represents a notification
 * Server response for 1 noti: 1 param (type) + 1 param (read) + 1 list (additional info) = 1 final list
 */
public class Notification {

    public static final int JOIN_ADM_VIEW = 1;

    public static final int HSID_POS = 0;
    public static final int PARAM_POS = 1;
    public static final int TIME_POS = 2;

    /**
     * 3 types of noti
     * 1. NEW: Completely new
     * 2. SEEN_NOT_READ: Seen but not clicked (read)
     * 3. READ: clicked
     */
    public static final int READ = 1;
    public static final int SEEN_NOT_READ = 2;
    public static final int NEW = 0;


    private boolean read;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setAdditional_params(List<String> additional_params) {
        this.additional_params = additional_params;
    }

    public boolean getRead() {
        return read;
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

    public Notification(String id, int type, boolean read, List<String> arg_params) {
        this.id = id;
        this.type = type;
        this.read = read;
        this.additional_params = arg_params;

    }

    public Notification(int type, String id) {
        this.id = id;
        read = false;
        this.type = type;
        additional_params = new ArrayList<String>();
    }

    public Notification(int type, boolean read, String id) {
        this.id = id;
        this.type = type;
        this.read = read;
        additional_params = new ArrayList<String>();
    }

    public Notification addParam(String param_value) {
        additional_params.add(param_value);
        return this;
    }

    public View makeNotiRow(Activity a) {
        LayoutInflater inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = null;

        //TODO if type is normal noti (noti without button should set it clickable)
        switch (type) {
            case JOIN_ADM_VIEW: {
                v = inflater.inflate(R.layout.hs_noti_join_req_adm_view, null);

                TextView tv = (TextView) v.findViewById(R.id.noti_join_req_admin_name);

                tv.setText(additional_params.get(PARAM_POS) + " ");

                if (read) {
                    v.setBackgroundResource(R.drawable.noti_read_bg);
                    Log.d("read at", this.toString());
                }


                return v;
            }
        }
        return v;
    }

    public Date getTimeOfNotification() throws ParseException{
        return StringUtils.getDateTimeFromServerDateResponse(additional_params.get(TIME_POS));
    }

}








