package com.extenprise.mapp.service.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.extenprise.mapp.data.Rx;
import com.extenprise.mapp.util.Utility;

import java.util.Date;

/**
 * Created by ambey on 1/11/15.
 */
public class ReportService implements Parcelable {
    private int idServProvHasServPt;
    private Rx rx;
    private int status;
    private Date receivedDate;
    private Date lastUpdateDate;

    public ReportService() {
        rx = new Rx();
    }

    protected ReportService(Parcel in) {
        idServProvHasServPt = in.readInt();
        rx = in.readParcelable(Rx.class.getClassLoader());
        status = in.readInt();
        receivedDate = Utility.getStrAsDate(in.readString(), "dd/MM/yyyy");
        lastUpdateDate = Utility.getStrAsDate(in.readString(), "dd/MM/yyyy");
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idServProvHasServPt);
        dest.writeParcelable(rx, flags);
        dest.writeInt(status);
        dest.writeString(Utility.getDateAsStr(receivedDate, "dd/MM/yyyy"));
        dest.writeString(Utility.getDateAsStr(lastUpdateDate, "dd/MM/yyyy"));
    }

    @Override
    public int describeContents() {
        return 0;
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
        return rx.getIdReport();
    }

    public void setIdReport(int idReport) {
        rx.setIdReport(idReport);
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

    public Rx getRx() {
        return rx;
    }

    public void setRx(Rx rx) {
        this.rx = rx;
    }
}
