package com.extenprise.mapp.service.data;

import java.util.ArrayList;

public class ServicePoint {

    private int idServicePoint;
    private String name;
    private String location;
    private String phone;
    private String altPhone;
    private String emailId;
    private String city;
    private String state;
    private String country;
    private ArrayList<ServProvHasServHasServPt> spssptList;

    public boolean addSpsspt(ServProvHasServHasServPt spsspt) {
        if (spssptList == null) {
            spssptList = new ArrayList<ServProvHasServHasServPt>();
        }
        return this.spssptList.add(spsspt);
    }

    public ArrayList<ServProvHasServHasServPt> getSpsspt() {
        return spssptList;
    }

    public void setSpsspt(ArrayList<ServProvHasServHasServPt> spssptList) {
        this.spssptList = spssptList;
    }

    public int getIdServicePoint() {
        return idServicePoint;
    }

    public void setIdServicePoint(int idServicePoint) {
        this.idServicePoint = idServicePoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
