package com.extenprise.mapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ambey on 31/8/15.
 */
public class Report implements Parcelable {
    private Date date;
    private String type;
    private int id;
    private Appointment appointment;

    public Report() {
        appointment = new Appointment();
    }

    protected Report(Parcel in) {
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        try {
            date = sdf.parse(in.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        type = in.readString();
        id = in.readInt();
        appointment = in.readParcelable(Appointment.class.getClassLoader());
    }

    public static final Creator<Report> CREATOR = new Creator<Report>() {
        @Override
        public Report createFromParcel(Parcel in) {
            return new Report(in);
        }

        @Override
        public Report[] newArray(int size) {
            return new Report[size];
        }
    };

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
        String dateStr = "";
        try {
            SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
            sdf.applyPattern("dd/MM/yyyy");
            dateStr = sdf.format(date);
        }catch (Exception e) {
            e.printStackTrace();
        }
        dest.writeString(dateStr);
        dest.writeString(type);
        dest.writeInt(id);
        dest.writeParcelable(appointment, flags);
    }
}
