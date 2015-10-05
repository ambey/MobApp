package com.extenprise.mapp.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ambey on 31/8/15.
 */
public class Report implements Parcelable {
    private String date;
    private String type;
    private String id;
    private Appointment appointment;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                id, type, date
        });
        dest.writeInt(appointment.getId());
    }
}
