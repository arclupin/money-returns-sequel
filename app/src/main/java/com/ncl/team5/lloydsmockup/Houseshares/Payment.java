package com.ncl.team5.lloydsmockup.Houseshares;

import android.os.Parcel;
import android.os.Parcelable;

import com.ncl.team5.lloydsmockup.IntentConstants;

import java.util.Date;

/**
 * Created by Thanh on 15-Apr-15.
 */
public class Payment implements Parcelable {
        private String hsid;
        private String billID;
        private double amount;
        private Date dateCreated;
        private Date datePaid;
        private boolean isConfirmed;
        private int payMethod;
        private String message;

    public Payment(String hsid, String billID, double amount, Date dateCreated, Date datePaid,
                   boolean isConfirmed, int payMethod, String message) {
        this.hsid = hsid;
        this.billID = billID;
        this.amount = amount;
        this.dateCreated = dateCreated;
        this.datePaid = datePaid;
        this.isConfirmed = isConfirmed;
        this.payMethod = payMethod;
        this.message = message;
    }


    public String getHsid() {
        return hsid;
    }

    public void setHsid(String hsid) {
        this.hsid = hsid;
    }

    public String getBillID() {
        return billID;
    }

    public void setBillID(String billID) {
        this.billID = billID;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDatePaid() {
        return datePaid;
    }

    public void setDatePaid(Date datePaid) {
        this.datePaid = datePaid;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setIsConfirmed(boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    public int getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(int payMethod) {
        this.payMethod = payMethod;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "hsid='" + hsid + '\'' +
                ", billID='" + billID + '\'' +
                ", amount=" + amount +
                ", dateCreated=" + dateCreated +
                ", datePaid=" + datePaid +
                ", isConfirmed=" + isConfirmed +
                ", payMethod=" + payMethod +
                ", message='" + message + '\'' +
                '}';
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }


    public static final Creator<Payment> CREATOR = new Creator<Payment>() {
        @Override
        public Payment createFromParcel(Parcel source) {
            return new Payment(source.readString(),
                    source.readString(),
                    source.readDouble(),
                    (Date) source.readSerializable(),
                    (Date) source.readSerializable(),
                    source.readInt() == 1,
                    source.readInt(),
                    source.readString());
        }

        @Override
        public Payment[] newArray(int size) {
            return new Payment[size];
        }
    };

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hsid);
        dest.writeString(billID);
        dest.writeDouble(amount);
        dest.writeSerializable(dateCreated);
        dest.writeSerializable(datePaid);
        dest.writeInt(isConfirmed ? 1 : 0);
        dest.writeInt(payMethod);
        dest.writeString(message);
    }
}
