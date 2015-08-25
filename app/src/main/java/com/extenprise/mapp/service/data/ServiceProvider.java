package com.extenprise.mapp.service.data;

import java.sql.Date;
import java.util.ArrayList;


public class ServiceProvider {

    private int idServiceProvider;
    private String fName;
    private String lName;
    private String phone;
    private String altPhone;
    private String emailId;
    private String passwd;
    private String gender;
    private boolean subscribed;
    private Date subsDate;
    private ArrayList<ServProvHasService> services;

    public void addServProvHasService(ServProvHasService sps) {
        if (services == null) {
            services = new ArrayList<ServProvHasService>();
        }
        services.add(sps);
    }

    public int getServiceCount() {
        if (services == null) {
            return 0;
        }
        return services.size();
    }

    public ServProvHasService getServProvHasService(String name, String speciality) {
        for (ServProvHasService sps: services) {
            Service s = sps.getService();
            if(s.getName().equals(name) && s.getSpeciality().equals(speciality)) {
                return sps;
            }
        }
        return null;
    }

    public int getIdServiceProvider() {
        return idServiceProvider;
    }

    public void setIdServiceProvider(int idServiceProvider) {
        this.idServiceProvider = idServiceProvider;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAltPhone() {
        return altPhone;
    }

    public void setAltPhone(String altPhone) {
        this.altPhone = altPhone;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }

    public Date getSubsDate() {
        return subsDate;
    }

    public void setSubsDate(Date subsDate) {
        this.subsDate = subsDate;
    }

    public ArrayList<ServProvHasService> getServices() {
        return services;
    }

    public void setServices(ArrayList<ServProvHasService> services) {
        this.services = services;
    }
}