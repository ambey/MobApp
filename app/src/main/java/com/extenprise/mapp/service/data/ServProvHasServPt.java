package com.extenprise.mapp.service.data;

//import com.extenprise.mapp.customer.data.CustomerHistoryData;
//import com.extenprise.mapp.common.Appointment;

import android.os.Parcel;
import android.os.Parcelable;

import com.extenprise.mapp.data.Appointment;

import java.util.ArrayList;

public class ServProvHasServPt implements Parcelable {

    private int idServProvHasServPt;
    private String servPointType;
    private String service;
    private float experience;
    private String workingDays;
    private float consultFee;
    private int startTime; //as minutes
    private int endTime;//as minutes
    //private ArrayList<CustomerHistoryData> historyData;
    private ArrayList<Appointment> appointment;
    private ServicePoint servicePoint;
    private String servProvPhone;

    public ServProvHasServPt() {
        appointment = new ArrayList<>();
    }

    public ServProvHasServPt(Parcel source) {
        idServProvHasServPt = source.readInt();
        startTime = source.readInt();
        endTime = source.readInt();
        experience = source.readFloat();
        consultFee = source.readFloat();

        String[] fields = new String[3];
        source.readStringArray(fields);
        servPointType = fields[0];
        service = fields[1];
        workingDays = fields[2];
    }

    public ServicePoint getServicePoint() {
        return servicePoint;
    }

    public void setServicePoint(ServicePoint servicePoint) {
        this.servicePoint = servicePoint;
    }

    public String getServProvPhone() {
        return servProvPhone;
    }

    public void setServProvPhone(String servProvPhone) {
        this.servProvPhone = servProvPhone;
    }

    public int getIdServProvHasServPt() {
        return idServProvHasServPt;
    }

    public void setIdServProvHasServPt(int idServProvHasServPt) {
        this.idServProvHasServPt = idServProvHasServPt;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public float getExperience() {
        return experience;
    }

    public void setExperience(float experience) {
        this.experience = experience;
    }

    public float getConsultFee() {
        return consultFee;
    }

    public void setConsultFee(float consultFee) {
        this.consultFee = consultFee;
    }

    public String getServPointType() {
        return servPointType;
    }

    public void setServPointType(String servPointType) {
        this.servPointType = servPointType;
    }

    public String getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(String workingDays) {
        this.workingDays = workingDays;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    /*public ArrayList<CustomerHistoryData> getHistoryData() {
        return historyData;
    }*/

 /*   public void setHistoryData(ArrayList<CustomerHistoryData> historyData) {
        this.historyData = historyData;
    }*/

   /* public ArrayList<Appointment> getAppointment() {
        return appointment;
    }*/

   /* public void setAppointment(ArrayList<Appointment> appointment) {
        this.appointment = appointment;
    }*/

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idServProvHasServPt);
        dest.writeInt(startTime);
        dest.writeInt(endTime);
        dest.writeFloat(experience);
        dest.writeFloat(consultFee);
        dest.writeStringArray(new String[]{
                servPointType, service, workingDays
        });
    }

    public static final Creator<ServProvHasServPt> CREATOR = new Creator<ServProvHasServPt>() {
        @Override
        public ServProvHasServPt createFromParcel(Parcel source) {
            return new ServProvHasServPt(source);
        }

        @Override
        public ServProvHasServPt[] newArray(int size) {
            return new ServProvHasServPt[size];
        }
    };

}
