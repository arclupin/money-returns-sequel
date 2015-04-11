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

    public SubBill(String hs_id, String bill_ID, double amount, boolean isActive, boolean isPaid, Date datePaid) {
        this.hs_id = hs_id;
        this.bill_ID = bill_ID;
        this.amount = amount;
        this.isActive = isActive;
        this.isPaid = isPaid;
        if (!(isPaid ^ datePaid != null))
            throw new IllegalArgumentException("date paid must be supplied if the sub bill has been paid");
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(hs_id).append(" - ").append(bill_ID).append(" - ").append(isActive ? "active" : "inactive")
                .append(" - ").append(isPaid ? "paid on" : "not paid").append(" - ").append(datePaid);
        return b.toString();
    }
}
