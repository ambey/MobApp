package com.extenprise.mapp.service.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ambey on 4/11/15.
 */
public class AppointmentTimeslotsForm implements Parcelable {
    private int idService;
    private Date date;
    private Date todayDate;
    private int time;

    public AppointmentTimeslotsForm() {

    }

    protected AppointmentTimeslotsForm(Parcel in) {
        idService = in.readInt();
        time = in.readInt();
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        try {
            date = sdf.parse(in.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            todayDate = sdf.parse(in.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static final Creator<AppointmentTimeslotsForm> CREATOR = new Creator<AppointmentTimeslotsForm>() {
        @Override
        public AppointmentTimeslotsForm createFromParcel(Parcel in) {
            return new AppointmentTimeslotsForm(in);
        }

        @Override
        public AppointmentTimeslotsForm[] newArray(int size) {
            return new AppointmentTimeslotsForm[size];
        }
    };

    public int getIdService() {
        return idService;
    }

    public void setIdService(int idService) {
        this.idService = idService;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getTodayDate() {
        return todayDate;
    }

    public void setTodayDate(Date todayDate) {
        this.todayDate = todayDate;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idService);
        dest.writeInt(time);
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        String dateStr = "";
        if (date != null) {
            dateStr = sdf.format(date);
        }
        dest.writeString(dateStr);
        if (todayDate != null) {
            dateStr = sdf.format(todayDate);
        }
        dest.writeString(dateStr);
    }
}
