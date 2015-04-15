package com.ncl.team5.lloydsmockup.Houseshares;

import java.util.Date;

/**
 * represent a sub bill
 *
 * Created by Thanh on 11-Apr-15.
 */
public class SubBill {
    private double amount;
    private boolean isPaid;
    private String datePaid;
    private String hs_id;
    private String bill_ID;
    private boolean isActive;
    private boolean isConfirmed;
    private Payment payment;

    public SubBill(String hs_id, String bill_ID, double amount, boolean isActive, boolean isPaid,
                   boolean isConfirmed, Date datePaid, Payment payment) {
        this.hs_id = hs_id;
        this.bill_ID = bill_ID;
        this.amount = amount;
        this.isActive = isActive;
        this.isConfirmed = isConfirmed;
        this.isPaid = isPaid;
        this.payment = payment;

        //if this bill is paid then datePaid must be supplied and vice versa
        if (isPaid ^ (datePaid != null))
            throw new IllegalArgumentException("date paid must be supplied if the sub bill has been paid");

    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(hs_id).append(" - ").append(bill_ID).append(" - ")
                .append(isActive ? "active" : "inactive")
                .append(" - ").append(isConfirmed ? "confirmed" : "not confirmed")
                .append(" - ").append(isPaid ? "paid on" : "not paid")
                .append(" - ").append(datePaid);
        return b.toString();
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setIsPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }

    public String getBill_ID() {
        return bill_ID;
    }

    public void setBill_ID(String bill_ID) {
        this.bill_ID = bill_ID;
    }

    public String getHs_id() {
        return hs_id;
    }

    public void setHs_id(String hs_id) {
        this.hs_id = hs_id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getDatePaid() {
        return datePaid;
    }

    public void setDatePaid(String datePaid) {
        this.datePaid = datePaid;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setIsConfirmed(boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    public Payment getPayment() {
        return payment;
    }
}
