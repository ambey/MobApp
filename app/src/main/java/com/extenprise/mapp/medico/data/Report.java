package com.extenprise.mapp.medico.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ambey on 31/8/15.
 */
public class Report implements Parcelable {
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
    private Date date;
    private String reportType;
    private int idReport;
    private Appointment appointment;
    private byte[] scannedCopy;

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
        reportType = in.readString();
        idReport = in.readInt();
        appointment = in.readParcelable(Appointment.class.getClassLoader());
        scannedCopy = in.createByteArray();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public int getIdReport() {
        return idReport;
    }

    public void setIdReport(int idReport) {
        this.idReport = idReport;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public byte[] getScannedCopy() {
        return scannedCopy;
    }

    public void setScannedCopy(byte[] scannedCopy) {
        this.scannedCopy = scannedCopy;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        dest.writeString(dateStr);
        dest.writeString(reportType);
        dest.writeInt(idReport);
        dest.writeParcelable(appointment, flags);
        dest.writeByteArray(scannedCopy);
    }
}
