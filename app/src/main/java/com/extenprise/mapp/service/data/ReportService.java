package com.extenprise.mapp.service.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ambey on 1/11/15.
 */
public class ReportService implements Parcelable{
    private int idServProvHasServPt;
    private int idReport;
    private int status;
    private Date receivedDate;
    private Date lastUpdateDate;

    protected ReportService(Parcel in) {
        idServProvHasServPt = in.readInt();
        idReport = in.readInt();
        status = in.readInt();
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        try {
            receivedDate = sdf.parse(in.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            lastUpdateDate = sdf.parse(in.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static final Creator<ReportService> CREATOR = new Creator<ReportService>() {
        @Override
        public ReportService createFromParcel(Parcel in) {
            return new ReportService(in);
        }

        @Override
        public ReportService[] newArray(int size) {
            return new ReportService[size];
        }
    };

    public int getIdServProvHasServPt() {
        return idServProvHasServPt;
    }

    public void setIdServProvHasServPt(int idServProvHasServPt) {
        this.idServProvHasServPt = idServProvHasServPt;
    }

    public int getIdReport() {
        return idReport;
    }

    public void setIdReport(int idReport) {
        this.idReport = idReport;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idServProvHasServPt);
        dest.writeInt(idReport);
        dest.writeInt(status);
        String dateStr = "";
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        if(receivedDate != null) {
            dateStr = sdf.format(receivedDate);
        }
        dest.writeString(dateStr);
        dateStr = "";
        if(lastUpdateDate != null) {
            dateStr = sdf.format(lastUpdateDate);
        }
        dest.writeString(dateStr);
    }
}
