package com.extenprise.mapp.data;

//import com.extenprise.mapp.customer.data.CustomerHistoryData;
//import com.extenprise.mapp.common.Appointment;

import com.extenprise.mapp.customer.data.Customer;
import com.extenprise.mapp.service.data.*;

import java.util.ArrayList;
import java.util.Date;


public class Appointment {
    private int id;
    private String servPointType;
    private String date;
    private int fromTime; //as minutes
    private int toTime;//as minutes
    private ServicePoint servicePoint;
    private ServProvHasService servProvHasService;
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

    public ServicePoint getServicePoint() {
        return servicePoint;
    }

    public void setServicePoint(ServicePoint servicePoint) {
        this.servicePoint = servicePoint;
    }

    public ServProvHasService getServProvHasService() {
        return servProvHasService;
    }

    public void setServProvHasService(ServProvHasService servProvHasService) {
        this.servProvHasService = servProvHasService;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
