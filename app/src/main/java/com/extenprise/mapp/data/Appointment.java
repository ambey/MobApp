package com.extenprise.mapp.data;

//import com.extenprise.mapp.customer.data.CustomerHistoryData;
//import com.extenprise.mapp.common.Appointment;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Appointment implements Comparable<Appointment>, Parcelable {
    private int idAppointment;
    private Date date;
    private int from; //as minutes
    private int to;//as minutes
    private int idServProvHasServPt;
    private int idCustomer;
    private ArrayList<Report> reports;

    public Appointment() {
    }

    public Appointment(Parcel source) {
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");

        idAppointment = source.readInt();
        try {
            date = sdf.parse(source.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        from = source.readInt();
        to = source.readInt();
        idServProvHasServPt = source.readInt();
        idCustomer = source.readInt();
    }

    public int getIdAppointment() {
        return idAppointment;
    }

    public void setIdAppointment(int idAppointment) {
        this.idAppointment = idAppointment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getIdServProvHasServPt() {
        return idServProvHasServPt;
    }

    public void setIdServProvHasServPt(int idServProvHasServPt) {
        this.idServProvHasServPt = idServProvHasServPt;
    }

    public int getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(int idCustomer) {
        this.idCustomer = idCustomer;
    }

    public ArrayList<Report> getReports() {
        return reports;
    }

    public void setReports(ArrayList<Report> reports) {
        this.reports = reports;
    }

    public boolean addReport(Report report) {
        return reports.add(report);
    }

    public int getReportCount() {
        return reports.size();
    }

    @Override
    public int compareTo(@NonNull Appointment another) {
        int compareValue = date.compareTo(another.getDate());
        if (compareValue == 0) {
            return (this.getFrom() - another.getFrom());
        }
        return compareValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        dest.writeInt(idAppointment);
        dest.writeString(sdf.format(date));
        dest.writeInt(from);
        dest.writeInt(to);
        dest.writeInt(idServProvHasServPt);
        dest.writeInt(idCustomer);
    }

    public static final Creator<Appointment> CREATOR = new Creator<Appointment>() {

        @Override
        public Appointment createFromParcel(Parcel source) {
            return new Appointment(source);
        }

        @Override
        public Appointment[] newArray(int size) {
            return new Appointment[size];
        }
    };
}
