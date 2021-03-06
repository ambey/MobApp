package com.extenprise.mapp.medico.data;

//import com.extenprise.mapp.medico.customer.data.CustomerHistoryData;
//import com.extenprise.mapp.common.Appointment;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Appointment implements Comparable<Appointment>, Parcelable {
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
    private int idAppointment;
    private Date date;
    private int from; //as minutes
    private int to;//as minutes
    private boolean confirmed;
    private boolean canceled;
    private int idServProvHasServPt;
    private int idCustomer;
    private ArrayList<Report> reports;

    public Appointment() {
    }

    public Appointment(Parcel source) {
        idAppointment = source.readInt();

        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        try {
            date = sdf.parse(source.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        from = source.readInt();
        to = source.readInt();
        confirmed = source.readInt() > 0;
        canceled = source.readInt() > 0;
        idServProvHasServPt = source.readInt();
        idCustomer = source.readInt();
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
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
        dest.writeInt(idAppointment);

        String dateStr = "";
        try {
            SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
            sdf.applyPattern("dd/MM/yyyy");
            dateStr = sdf.format(date);
        }catch (Exception e) {
            e.printStackTrace();
        }
        dest.writeString(dateStr);
        dest.writeInt(from);
        dest.writeInt(to);
        dest.writeInt(confirmed ? 1 : 0);
        dest.writeInt(canceled ? 1 : 0);
        dest.writeInt(idServProvHasServPt);
        dest.writeInt(idCustomer);
    }
}
