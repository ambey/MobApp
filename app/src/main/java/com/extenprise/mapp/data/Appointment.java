package com.extenprise.mapp.data;

//import com.extenprise.mapp.customer.data.CustomerHistoryData;
//import com.extenprise.mapp.common.Appointment;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.extenprise.mapp.customer.data.Customer;
import com.extenprise.mapp.service.data.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Appointment implements Comparable<Appointment>, Parcelable {
    private int id;
    private String servPointType;
    private String date;
    private int fromTime; //as minutes
    private int toTime;//as minutes
    private ServProvHasServPt service;
    private Customer customer;
    private ArrayList<Report> reports;

    public Appointment() {
        reports = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean addReport(Report report) {
        return reports.add(report);
    }

    public int getReportCount() {
        return reports.size();
    }

    public String getServPointType() {
        return servPointType;
    }

    public void setServPointType(String servPointType) {
        this.servPointType = servPointType;
    }

    public String getDateOfAppointment() {
        return date;
    }

    public void setDateOfAppointment(String dateOfAppointment) {
        this.date = dateOfAppointment;
    }

    public int getFromTime() {
        return fromTime;
    }

    public void setFromTime(int fromTime) {
        this.fromTime = fromTime;
    }

    public int getToTime() {
        return toTime;
    }

    public void setToTime(int toTime) {
        this.toTime = toTime;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public int compareTo(@NonNull Appointment another) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date thisDate = sdf.parse(this.getDateOfAppointment());
            Date otherDate = sdf.parse(another.getDateOfAppointment());
            int compareValue = thisDate.compareTo(otherDate);
            if (compareValue == 0) {
                return (this.getFromTime() - another.getFromTime());
            }
            return compareValue;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(fromTime);
        dest.writeInt(toTime);
        dest.writeStringArray(new String[] {
                servPointType, date
        });
        if(customer != null) {
            dest.writeInt(customer.getIdCustomer());
        } else {
            dest.writeInt(-1);
        }
        if(service != null) {
            dest.writeInt(service.getIdServProvHasServPt());
        } else {
            dest.writeInt(-1);
        }
    }
}
