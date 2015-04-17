package HTTPConnect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.ncl.team5.lloydsmockup.HouseShare_Bill_Member;
import com.ncl.team5.lloydsmockup.HouseShare_Bill_Owner;
import com.ncl.team5.lloydsmockup.IntentConstants;
import com.ncl.team5.lloydsmockup.R;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import Fragments.Fragment_HS_Home;
import Utils.StringUtils;

/**
 * Created by Thanh on 25-Mar-15.
 * each object of the Notification class represents a notification
 * Server response for 1 noti: 1 param (type) + 1 param (read) + 1 list (additional info) = 1 final list
 */
public class Notification {

    //types of notifications
    public static final int JOIN_ADM = 0x00001; // new join request
    public static final int NEW_BILL = 0x00010; // new bill
    public static final int BILL_ACTIVATED = 0x00011; // bill activated
    public static final int BILL_PAYMENT_RECEIVE = 0x00020;
    public static final int BILL_PAYMENT_CONFIRMED = 0x00021;
    public static final int BILL_PAYMENT_REJECTED = 0x00022;
    public static final int BILL_PAYMENT_PAID = 0x00023;

    public static final int HSID_POS = 0;
    public static final int PARAM_POS = 1;
    public static final int TIME_POS = 2;
    public static final int PARAM2_POS = 3;


    /**
     * 3 types of noti
     * 1. NEW: Completely new
     * 2. SEEN_NOT_READ: Seen but not clicked (read)
     * 3. READ: clicked
     */
    public static final int READ = 1;
    public static final int SEEN_NOT_READ = 2;
    public static final int NEW = 0;

    private int type;
    private List<String> additional_params;
    private String source;
    private boolean read;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getSource() {
        return source;
    }

    public boolean isRead() {
        return read;
    }

    public Notification() {
        type = JOIN_ADM;
        additional_params = new ArrayList<String>();
    }

    public Notification(String id, int type, boolean read, List<String> arg_params, String source) {
        this.id = id;
        this.type = type;
        this.read = read;
        this.source = source;
        this.additional_params = arg_params;


    }

    public Notification(int type, String id) {
        this.id = id;
        read = false;
        this.type = type;
        additional_params = new ArrayList<String>();
    }

    public Notification(int type, boolean read, String id, String source) {
        this.id = id;
        this.type = type;
        this.read = read;
        this.source = source;
        additional_params = new ArrayList<String>();
    }

    public Notification addParam(String param_value) {
        additional_params.add(param_value);
        return this;
    }

    public View makeNotiRow(Activity a) {
        LayoutInflater inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableRow v = null;
        String content = "";
        String p1 = additional_params.get(PARAM_POS);
        String p2 = additional_params.get(PARAM2_POS);
        //TODO if type is normal noti (noti without button should set it clickable)
        switch (type) {
            case JOIN_ADM: {
                v = (TableRow)inflater.inflate(R.layout.hs_noti_join_req_adm_view, null);

                content = p1 + " would like to join your house.";
                // spans for style the noti (make the names bold)
                SpannableString spannableString = new SpannableString(content);
                spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0,
                        p1.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE );
                ((TextView) v.findViewById(R.id.noti_join_req_admin_name)).setText(spannableString);

                ((TextView) v.findViewById(R.id.noti_join_req_admin_name)).setMaxLines(3);
                break;
            }
            case NEW_BILL: {
                v = (TableRow) inflater.inflate(R.layout.hs_noti_new_bill, null);
                content = p1 + " has created a new bill " + p2 + ".";

                //style the spannable string
                SpannableString spannableString = new SpannableString(content);
                spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, p1.length(),
                        Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                spannableString.setSpan(new StyleSpan(Typeface.BOLD), p1.length() + 24,
                        content.length() - 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                ((TextView) v.findViewById(R.id.noti_bill_content)).setText(spannableString);
                ((TextView) v.findViewById(R.id.noti_bill_content)).setMaxLines(3);
                break;
            }

            case BILL_ACTIVATED: {
                v = (TableRow) inflater.inflate(R.layout.hs_noti_general, null);
                content = "Bill " + p1 + " has been activated.";

                //style the spannable string
                SpannableString spannableString = new SpannableString(content);
                spannableString.setSpan(new StyleSpan(Typeface.BOLD), 5, p1.length() + 5,
                        Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                ((TextView) v.findViewById(R.id.noti_content)).setText(spannableString);
                ((TextView) v.findViewById(R.id.noti_content)).setMaxLines(3);
                break;
            }

            case BILL_PAYMENT_RECEIVE: {
                v = (TableRow) inflater.inflate(R.layout.hs_noti_general, null);

                content = p1 + " has submitted a payment to " + p2 + ".";

                //style the spannable string
                SpannableString spannableString = new SpannableString(content);
                spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, p1.length(),
                        Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                spannableString.setSpan(new StyleSpan(Typeface.BOLD), p1.length() + 28,
                        content.length() - 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                ((TextView) v.findViewById(R.id.noti_content)).setText(spannableString);
                break;
            }

            case BILL_PAYMENT_CONFIRMED: {
                v = (TableRow) inflater.inflate(R.layout.hs_noti_general, null);
                String targetUser = Fragment_HS_Home.members.get(p1).getUsername();

                content = targetUser + " has paid his share for " + p2 + ".";

                //style the spannable string
                SpannableString spannableString = new SpannableString(content);
                spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, targetUser.length(),
                        Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                spannableString.setSpan(new StyleSpan(Typeface.BOLD), targetUser.length() + 23,
                        content.length() - 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                ((TextView) v.findViewById(R.id.noti_content)).setText(spannableString);
                break;
            }

            case BILL_PAYMENT_REJECTED: {
                v = (TableRow) inflater.inflate(R.layout.hs_noti_general, null);

                content = "Your latest payment to " + p1 + " has been rejected.";

                //style the spannable string
                SpannableString spannableString = new SpannableString(content);
                spannableString.setSpan(new StyleSpan(Typeface.BOLD), 23, 23 + p1.length(),
                        Spanned.SPAN_INCLUSIVE_INCLUSIVE);


                ((TextView) v.findViewById(R.id.noti_content)).setText(spannableString);
                break;
            }

            case BILL_PAYMENT_PAID: {
                v = (TableRow) inflater.inflate(R.layout.hs_noti_general, null);

                content = "Bill " + p1 + " has been paid." ;

                //style the spannable string
                SpannableString spannableString = new SpannableString(content);
                spannableString.setSpan(new StyleSpan(Typeface.BOLD), 5, 5 + p1.length(),
                        Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                ((TextView) v.findViewById(R.id.noti_content)).setText(spannableString);
                break;
            }
        }

        if (read) {
            v.setBackgroundResource(R.drawable.noti_read_bg);
            Log.d("read at", this.toString());
        }
        return v;
    }

    public Date getTimeOfNotification() throws ParseException{
        return StringUtils.getDateTimeFromServerDateResponse(additional_params.get(TIME_POS));
    }

    @Override
    public String toString() {
        return "Notification{" +
                "read=" + read +
                ", id='" + id + '\'' +
                ", type=" + type +
                ", additional_params=" + additional_params +
                ", source='" + source + '\'' +
                '}';
    }
}








