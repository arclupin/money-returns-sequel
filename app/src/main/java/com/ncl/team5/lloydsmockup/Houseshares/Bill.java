package com.ncl.team5.lloydsmockup.Houseshares;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import Utils.StringUtils;

/**
 * Represent a bill with its data as well as view
 *
 * Created by Thanh on 11-Apr-15.
 */
public class Bill implements Comparable<Bill>, Parcelable{

    private String billID;
    private String billName;
    private String message;
    private Date dueDate;
    private Date dateCreated;
    private double amount;
    private Member billCreator;
    private TreeMap<Member, SubBill> subBills;
    //TODO Timeline
    private boolean isPaid; // order of bills needs to be taken into consideration
    private Date datePaid;
    private boolean isActive;
    private boolean amICreator;

    private static final Bill BILL_EMPTY = new Bill();

    /**
     * Constructor #1
     * @param billID the bill id
     * @param name the name of the bill
     * @param dueDate the due date
     * @param dateCreated the date this bill was created
     * @param amount the total amount of the bill
     * @param message the message attached to this bill
     * @param creator the creator of this bill
     * @param isPaid boolean value indicating whether the bill has been paid
     * @param isActive boolean value indicating whether the bill has been activated
     * @param datePaid the date this bill was paid, or null is the bill has not been paid yet
     * @param amICreator whether this user is the creator <u>important</u>
     */
    private Bill(String billID, String name, Date dueDate, Date dateCreated, double amount,
                 String message, Member creator, boolean isPaid, boolean isActive, Date datePaid,
                 boolean amICreator) {
        this.billID = billID;
        this.billName = name;
        this.dueDate = dueDate;
        this.dateCreated = dateCreated;
        this.amount = amount;
        this.message = message;
        this.billCreator = creator;
        this.isPaid = isPaid;
        this.isActive = isActive;
        this.datePaid = datePaid;
        this.subBills = new TreeMap<Member, SubBill>();
        this.amICreator = amICreator;
    }

    /**
     * Constructor #2
     * @param billID the bill id
     * @param name the name of the bill
     * @param dueDate the due date
     * @param dateCreated the date this bill was created
     * @param amount the total amount of the bill
     * @param message the message attached to this bill
     * @param creator the creator of this bill
     * @param isPaid boolean value indicating whether the bill has been paid
     * @param isActive boolean value indicating whether the bill has been activated
     * @param datePaid the date this bill was paid, or null is the bill has not been paid yet
     * @param subBills the sub bills of this bill
     * @param amICreator whether this user is the creator <u>important</u>
     */
    private Bill(String billID, String name, Date dueDate, Date dateCreated, double amount,
                 String message, Member creator, boolean isPaid, boolean isActive, Date datePaid,
                 Map<Member, SubBill> subBills, boolean amICreator) {
        this.billID = billID;
        this.billName = name;
        this.dueDate = dueDate;
        this.dateCreated = dateCreated;
        this.amount = amount;
        this.message = message;
        this.billCreator = creator;
        this.isPaid = isPaid;
        this.isActive = isActive;
        this.datePaid = datePaid;
        this.subBills = new TreeMap<Member, SubBill>(subBills);
        this.amICreator = amICreator;
    }

    /**
     * Empty constructor for constructing the empty instance
     */
    private Bill() {
        billID = "0"; // set the bill id to a invalid value
    }

    public String getBillID() {
        return billID;
    }

    public String getBillName() {
        return billName;
    }

    public String getMessage() {
        return message;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public double getAmount() {
        return amount;
    }

    public Member getBillCreator() {
        return billCreator;
    }

    public TreeMap<Member, SubBill> getSubBills() {
        return subBills;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public Date getDatePaid() {
        return datePaid;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean amICreator() {
        return amICreator;
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

    public static final Creator<Bill> CREATOR = new Creator<Bill>() {
        @Override
        public Bill createFromParcel(Parcel source) {
            return new Bill(source.readString(),
                    source.readString(),
                    (Date) source.readSerializable(),
                    (Date) source.readSerializable(),
                    source.readDouble(),
                    source.readString(),
                    (Member) source.readParcelable(Member.class.getClassLoader())
                    , source.readInt() == 1,
                    source.readInt() == 1,
                    (Date) source.readSerializable(),
                    source.readInt() == 1);
        }

        @Override
        public Bill[] newArray(int size) {
            return new Bill[size];
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
        dest.writeString(billID);
        dest.writeString(billName);
        dest.writeSerializable(dueDate);
        dest.writeSerializable(dateCreated);
        dest.writeDouble(amount);
        dest.writeString(message);
        dest.writeParcelable(billCreator, 0);
        dest.writeInt(isPaid ? 1 : 0);
        dest.writeInt(isActive ? 1 :0);
        dest.writeSerializable(datePaid);
        dest.writeInt(amICreator ? 1 : 0);
    }

    /**
     * Class BillBuilder for building a bill
     */
    public static class BillBuilder {
        private String billID = "";
        private String billName = "";
        private String message = "";
        private Date dueDate = null;
        private Date dateCreated = null;
        private double amount = 0.0;
        private Member billCreator = null;
        private TreeMap<Member, SubBill> subBills = new TreeMap<Member, SubBill>();
        private boolean isPaid = false; // order of bills needs to be taken into consideration
        private Date datePaid = null;
        private boolean isActive = false;
        private boolean amICreator;

        public BillBuilder(boolean isActive, boolean isBillPaid, boolean amICreator) {
            this.isActive = isActive;
            this.isPaid = isBillPaid;
            this.amICreator = amICreator;
        }

        public BillBuilder setBillName(String billName) {
            this.billName = billName;
            return this;
        }

        public BillBuilder setAmount(double amount) {
            this.amount = amount;
            return this;
        }

        public BillBuilder setMessage(String message) {
            this.message = message;
            return this;
        }

        public BillBuilder setDueDate(Date dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public BillBuilder setDateCreated(Date dateCreated) {
            this.dateCreated = dateCreated;
            return this;
        }

        public BillBuilder setBillCreator(Member billCreator) {
            this.billCreator = billCreator;
            return this;
        }

        public BillBuilder setSubBills(TreeMap<Member, SubBill> subBills) {
            this.subBills = subBills;
            return this;
        }

        public BillBuilder setIsPaid(boolean isPaid) {
            this.isPaid = isPaid;
            return this;
        }

        public BillBuilder setBillID(String billID) {
            this.billID = billID;
            return this;
        }

        public BillBuilder setDatePaid(Date datePaid) {
            this.datePaid = datePaid;
            return this;
        }

        public BillBuilder setIsActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public BillBuilder setAmICreator(boolean amICreator) {
            this.amICreator = amICreator;
            return this;
        }

        private boolean isAllDataSet() {
            return  !StringUtils.isFieldEmpty(billID) &&
                    !StringUtils.isFieldEmpty(billName) &&
                    dueDate != null && dateCreated != null &&
                    !(isPaid ^ datePaid != null ) &&
                    amount != 0 &&
                    billCreator != null;
        }

        public Bill build() {
            if (isAllDataSet())
                return new Bill(billID, billName, dueDate, dateCreated, amount, message, billCreator,
                        isPaid, isActive, datePaid, subBills, amICreator);
            throw (new IllegalStateException("Bill has illegal state"));
        }
    }


    @Override
    public String toString() {

        StringBuilder b = new StringBuilder();
        b.append(billID).append(" - ").append(billName).append(" - ").append(amount).append(" - ")
                .append("Created by ").append(billCreator.getUsername())
                .append(" on ").append(dateCreated).append(" - ")
                .append("Due ").append(dueDate.toString()).append(" - ")
                .append("Users: ");

        for (Member m: subBills.keySet())
            b.append(m.getUsername()).append("*");

        b.append(" - ").append(isActive ? "active" : "inactive").append(" - ")
                .append(isPaid ? "paid on" : "not paid")
                .append(" - ").append(datePaid)
                .append(" - ").append(amICreator ? "Creator" : "Not creator");

        return b.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Bill))
            return false;
        if (this == o)
            return true;
        return billID.equals(((Bill) o).billID);
    }


    @Override
    public int compareTo(Bill another) {
        if(billID.equals(another.billID))
            return 0;
        if(isPaid && !another.isPaid)
            return -1; // bills that have not been paid > bills that have been paid
        else if (!isPaid && another.isPaid)
            return 1;
        else if (isPaid && another.isPaid) // if bills are both paid
            return (datePaid.compareTo(another.datePaid)); // newly paid bills > older paid bills
        else // else if bills have both not been paid
        {
            if(isActive && !another.isActive)
                return 1; // bills that have been activated > bills that have not been activated
            else if (!isActive && another.isActive)
                return -1;
            else if (isActive && another.isActive) // if bills are both active
            {
                int i  = dueDate.compareTo(another.dueDate) * -1;
                // bills due earlier > bills due later
                if (i != 0)
                    return i;
                else {
                    int m = dateCreated.compareTo(another.dateCreated);
                    // bills that are created later > bills that are created earlier
                    if (m != 0)
                        return m;
                    else {
                        int k = billName.compareTo(another.billName) * -1; // alphabetical order
                        if (k != 0)
                            return k;
                        else
                            return billID.compareTo(another.billID);
                            // if all above comparisons yield equality, then compare the bill id
                    }
                }

            }
        }
        return billID.compareTo(another.billID);
    }

    @Override
    public int hashCode() {
        int hash = Member.HASH;
        hash = (hash + billID.hashCode()) * Member.HASH;
        hash = (hash + billName.hashCode()) * Member.HASH;
        hash = (hash + (isPaid ? 1 : 0)) * Member.HASH;
        hash = (hash + (isActive ? 1 : 0)) * Member.HASH;
        hash = (hash + dueDate.hashCode()) * Member.HASH;
        hash = (hash + dateCreated.hashCode()) * Member.HASH;
        hash = (hash + billName.hashCode()) * Member.HASH;
        return hash;
    }

    /**
     * Get the empty instance
     * @return the empty instance
     */
    public static Bill getEmptyInstance() {
        return BILL_EMPTY;
    }

    /**
     * Check if this bill is the static final empty one
     * @return the result
     */
    public boolean isBillEmptyConstant(Bill bill) {
            return bill == BILL_EMPTY;
    }
}
