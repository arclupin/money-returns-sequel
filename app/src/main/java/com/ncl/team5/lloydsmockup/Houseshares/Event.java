package com.ncl.team5.lloydsmockup.Houseshares;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ncl.team5.lloydsmockup.R;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Utils.StringUtils;

/**
 * Represents an event of this bill
 *
 * All bills have the first event being the event in which the bill creator has activated the bill
 *
 * Created by Thanh on 14-Apr-15.
 */
public class Event implements Parcelable, Comparable<Event>{


    //Events type
    private static final int ACTIVATION_TYPE = 0x00001;
    private static final int SUBBILL_PAYMENT_TYPE = 0x00010;
    private static final int BILL_PAID = 0x00011;

    private Map<Integer, String> texts;


    // the id of this event
    private String eventID;

    // type of the event
    private int eventType;

    private Bill bill;

    // date of the event
    private Date dateOfEvent;

    // who caused the event
    private Member eventSrc;

    public Event(String eventID, int eventType,Bill bill, Date dateOfEvent, Member eventSrc) {
        this.eventID = eventID;
        this.eventType = eventType;
        this.bill = bill;
        this.dateOfEvent = dateOfEvent;
        this.eventSrc = eventSrc;
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

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source.readString(),
                            source.readInt(),
                    (Bill) source.readParcelable(Bill.class.getClassLoader()),
                    (Date) source.readSerializable(),
                    (Member) source.readParcelable(Member.class.getClassLoader())
                    );
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
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
        dest.writeString(eventID);
        dest.writeInt(eventType);
        dest.writeParcelable(bill, 0);
        dest.writeSerializable(dateOfEvent);
        dest.writeParcelable(eventSrc, 0);
    }

    /**
     * Returns a string containing a concise, human-readable description of this
     * object. Subclasses are encouraged to override this method and provide an
     * implementation that takes into account the object's type and data. The
     * default implementation is equivalent to the following expression:
     * <pre>
     *   getClass().getName() + '@' + Integer.toHexString(hashCode())</pre>
     * <p>See <a href="{@docRoot}reference/java/lang/Object.html#writing_toString">Writing a useful
     * {@code toString} method</a>
     * if you intend implementing your own {@code toString} method.
     *
     * @return a printable representation of this object.
     */
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(eventID).append(" - ")
                .append(eventType).append(" - ")
                .append(eventSrc.getUsername()).append(" - ")
                .append(StringUtils.getGeneralDateString(dateOfEvent));
        return b.toString();
    }

    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param another the object to compare to this instance.
     * @return a negative integer if this instance is less than {@code another};
     * a positive integer if this instance is greater than
     * {@code another}; 0 if this instance has the same order as
     * {@code another}.
     * @throws ClassCastException if {@code another} cannot be converted into something
     *                            comparable to {@code this} instance.
     */
    @Override
    public int compareTo(Event another) {
        if (eventID.equals(another.eventID))
            return 0;
        return dateOfEvent.compareTo(another.dateOfEvent) * -1;
    }

    /**
     * Compares this instance with the specified object and indicates if they
     * are equal. In order to be equal, {@code o} must represent the same object
     * as this instance using a class-specific comparison. The general contract
     * is that this comparison should be reflexive, symmetric, and transitive.
     * Also, no object reference other than null is equal to null.
     * <p/>
     * <p>The default implementation returns {@code true} only if {@code this ==
     * o}. See <a href="{@docRoot}reference/java/lang/Object.html#writing_equals">Writing a correct
     * {@code equals} method</a>
     * if you intend implementing your own {@code equals} method.
     * <p/>
     * <p>The general contract for the {@code equals} and {@link
     * #hashCode()} methods is that if {@code equals} returns {@code true} for
     * any two objects, then {@code hashCode()} must return the same value for
     * these objects. This means that subclasses of {@code Object} usually
     * override either both methods or neither of them.
     *
     * @param o the object to compare this instance with.
     * @return {@code true} if the specified object is equal to this {@code
     * Object}; {@code false} otherwise.
     * @see #hashCode
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof  Event))
            return false;
        Event e = (Event) o;
        return (this == e) || (this.eventID.equals(e.eventID));
    }

    private Map<Integer, String> getText() {
        if (texts != null)
            return texts;
        texts = new HashMap<Integer, String>();
        texts.put(ACTIVATION_TYPE, "has activated the bill.");
        texts.put(SUBBILL_PAYMENT_TYPE, "has paid their share.");
        texts.put(BILL_PAID, " has paid this bill.");
        return texts;

    }

    public View craftView(LayoutInflater inflater) {
        View v = inflater.inflate(R.layout.hs_event_row, null);
        String text = "";
        ((TextView) v.findViewById(R.id.event_main_text)).setText(eventSrc.getUsername() + " " + getText().get(eventType));
        ((TextView) v.findViewById(R.id.event_date)).setText(StringUtils.getGeneralDateString(dateOfEvent));
        return v;
    }
}
