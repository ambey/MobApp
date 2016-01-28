package com.extenprise.mapp.medico.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Created by avinash on 13/1/16.
 */

public class LastVisited implements Parcelable {
    public static final Creator<LastVisited> CREATOR = new Creator<LastVisited>() {
        @Override
        public LastVisited createFromParcel(Parcel source) {
            return new LastVisited(source);
        }

        @Override
        public LastVisited[] newArray(int size) {
            return new LastVisited[size];
        }
    };
    private Date lastVisitedDate;
    private int lastVisitedTime;

    public LastVisited() {
    }

    public LastVisited(Parcel source) {
        try {
            SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
            sdf.applyPattern("dd/MM/yyyy");
            lastVisitedDate = sdf.parse(source.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        lastVisitedTime = source.readInt();
    }

    public String toString() {
        return "Last Visited Date: " + lastVisitedDate + ", Last Visited Time: " + lastVisitedTime;
    }

    public Date getLastVisitedDate() {
        return lastVisitedDate;
    }

    public void setLastVisitedDate(Date lastVisitedDate) {
        this.lastVisitedDate = lastVisitedDate;
    }

    public int getLastVisitedTime() {
        return lastVisitedTime;
    }

    public void setLastVisitedTime(int lastVisitedTime) {
        this.lastVisitedTime = lastVisitedTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String dateStr = "";
        if(lastVisitedDate != null) {
            SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
            sdf.applyPattern("dd/MM/yyyy");
            dateStr = sdf.format(lastVisitedDate);
        }
        dest.writeString(dateStr);
        dest.writeInt(lastVisitedTime);
    }
}
