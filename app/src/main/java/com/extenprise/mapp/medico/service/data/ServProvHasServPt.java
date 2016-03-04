package com.extenprise.mapp.medico.service.data;

//import com.extenprise.mapp.medico.customer.data.CustomerHistoryData;
//import com.extenprise.mapp.common.Appointment;

import android.os.Parcel;
import android.os.Parcelable;

import com.extenprise.mapp.medico.data.Appointment;

import java.util.ArrayList;

public class ServProvHasServPt implements Parcelable {

    public static final Creator<ServProvHasServPt> CREATOR = new Creator<ServProvHasServPt>() {
        @Override
        public ServProvHasServPt createFromParcel(Parcel in) {
            return new ServProvHasServPt(in);
        }

        @Override
        public ServProvHasServPt[] newArray(int size) {
            return new ServProvHasServPt[size];
        }
    };
    private int idServProvHasServPt;
    private String servPointType;
    private Service service;
    private float experience;
    private String workingDays;
    private float consultFee;
    private int startTime; //as minutes
    private int endTime;//as minutes
    private WeeklyWorkTiming weeklyWorkTiming;
    private ArrayList<Appointment> appointments;
    private ServicePoint servicePoint;
    private String servProvPhone;
    private String notes;

    public ServProvHasServPt() {
        service = new Service();
        appointments = new ArrayList<>();
    }

    protected ServProvHasServPt(Parcel in) {
        idServProvHasServPt = in.readInt();
        servPointType = in.readString();
        service = in.readParcelable(Service.class.getClassLoader());
        experience = in.readFloat();
        workingDays = in.readString();
        consultFee = in.readFloat();
        startTime = in.readInt();
        endTime = in.readInt();
        weeklyWorkTiming = in.readParcelable(WeeklyWorkTiming.class.getClassLoader());
        appointments = in.createTypedArrayList(Appointment.CREATOR);
        servicePoint = in.readParcelable(ServicePoint.class.getClassLoader());
        servProvPhone = in.readString();
        notes = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idServProvHasServPt);
        dest.writeString(servPointType);
        dest.writeParcelable(service, flags);
        dest.writeFloat(experience);
        dest.writeString(workingDays);
        dest.writeFloat(consultFee);
        dest.writeInt(startTime);
        dest.writeInt(endTime);
        dest.writeParcelable(weeklyWorkTiming, flags);
        dest.writeTypedList(appointments);
        dest.writeParcelable(servicePoint, flags);
        dest.writeString(servProvPhone);
        dest.writeString(notes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
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

    public WeeklyWorkTiming getWeeklyWorkTiming() {
        return weeklyWorkTiming;
    }

    public void setWeeklyWorkTiming(WeeklyWorkTiming weeklyWorkTiming) {
        this.weeklyWorkTiming = weeklyWorkTiming;
    }

    public ArrayList<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointment(ArrayList<Appointment> appointments) {
        this.appointments = appointments;
    }

}
