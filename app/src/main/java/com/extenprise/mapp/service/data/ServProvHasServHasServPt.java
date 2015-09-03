package com.extenprise.mapp.service.data;

//import com.extenprise.mapp.customer.data.CustomerHistoryData;
//import com.extenprise.mapp.common.Appointment;

public class ServProvHasServHasServPt {

    private String servPointType;
    private String weeklyOff;
    private float consultFee;
    private int startTime; //as minutes
    private int endTime;//as minutes
    //private ArrayList<CustomerHistoryData> historyData;
    //private ArrayList<Appointment> appointment;
    private ServicePoint servicePoint;
    private ServProvHasService servProvHasService;

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

    public String getWeeklyOff() {
        return weeklyOff;
    }

    public void setWeeklyOff(String weeklyOff) {
        this.weeklyOff = weeklyOff;
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
}
