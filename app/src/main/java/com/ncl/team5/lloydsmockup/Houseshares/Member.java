package com.ncl.team5.lloydsmockup.Houseshares;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ncl.team5.lloydsmockup.R;

import Utils.StringUtils;

/**
 * Created by Thanh on 08-Apr-15.
 */
public class Member implements Parcelable, Comparable<Member>{
    private String houseshare_id;
    private String username;
    private String joined_since;
    public static final int HASH = 2 << 5 - 1;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(houseshare_id);
        dest.writeString(username);
        dest.writeString(joined_since);
    }

    public static final Creator<Member> CREATOR = new Creator<Member>() {
        @Override
        public Member createFromParcel(Parcel source) {
            return new Member(source.readString(), source.readString(), source.readString());
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };

    public Member(String houseshare_id, String username, String joined_since) {
        this.houseshare_id = houseshare_id;
        this.username = username;
        this.joined_since = joined_since;
    }


    public String getJoined_since() {
        return joined_since;
    }

    public void setJoined_since(String joined_since) {
        this.joined_since = joined_since;
    }

    public String getHouseshare_id() {
        return houseshare_id;
    }

    public void setHouseshare_id(String houseshare_id) {
        this.houseshare_id = houseshare_id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(username).append(", ")
                .append(houseshare_id).append(", ")
                .append(joined_since).append(",  ");
        return b.toString();
    }


    /**
     * craft a view a displaying member
     * @param inflater
     * @return
     */
    public View craftViewInfo(LayoutInflater inflater) {
        View v = inflater.inflate(R.layout.hs_select_users_row, null);
        ((TextView) v.findViewById(R.id.username_select)).setText(username);
        ((TextView) v.findViewById(R.id.user_joined_since)).setText("Joined since " + StringUtils.getStringDate(joined_since, "yyyy-MM-dd", "dd-MM-yyyy")) ;
        return v;
    }

    /**
     * craft a view for entering this member sub bill
     * @param inflater
     * @return
     */
    public View craftViewSubBill(LayoutInflater inflater){
        View v = inflater.inflate(R.layout.hs_sub_bill_manual, null);
        ((TextView) v.findViewById(R.id.username_sub_bill)).setText(username);
        return v;
    }



    @Override
    public boolean equals(Object o) {
        if (! (o instanceof Member))
            return false;
        Member m = (Member) o;
        return (m == this)
            || (houseshare_id.equals(m.houseshare_id) && username.equals(m.username) &&
                    joined_since.equals(m.joined_since));

    }

    @Override
    public int hashCode() {
        int hash = HASH;
        hash = HASH * hash + houseshare_id.hashCode();
        hash = HASH * hash + username.hashCode();
        hash = HASH * hash + joined_since.hashCode();
        return hash;
    }


    @Override
    public int compareTo(Member another) {
        Log.d("compare To", StringUtils.getDateFromServerDateResponse(joined_since).toString() + StringUtils.getDateFromServerDateResponse(another.joined_since).toString() + " //");
        int chronological_order = StringUtils.getDateFromServerDateResponse(joined_since).compareTo(StringUtils.getDateFromServerDateResponse(another.joined_since));
        return chronological_order != 0 ? chronological_order : username.compareTo(another.username);
    }
}
