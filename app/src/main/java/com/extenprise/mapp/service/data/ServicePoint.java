package com.extenprise.mapp.service.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.extenprise.mapp.data.City;

import java.util.ArrayList;

public class ServicePoint implements Parcelable {

    private int idServicePoint;
    private String name;
    private String location;
    private String pincode;
    private String phone;
    private String altPhone;
    private String emailId;
    private City city;
    //private ArrayList<Integer> idServices;

    public ServicePoint() {
        //idServices = new ArrayList<>();
        city = new City();
    }

    public ServicePoint(Parcel source) {
        idServicePoint = source.readInt();
        String[] fields = new String[6];
        source.readStringArray(fields);

        int i = 0;
        name = fields[i++];
        location = fields[i++];
        phone = fields[i++];
        altPhone = fields[i++];
        emailId = fields[i++];
        pincode = fields[i];
        city = new City(source);
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public boolean addSpsspt(int idService) {
/*
        if (idServices == null) {
            idServices = new ArrayList<>();
        }
        return this.idServices.add(idService);
*/
        return true;
    }

    public boolean addService(int idService) {
        //return idServices.add(idService);
        return true;
    }

    public int getServiceId(int position) {
        try {
            //return idServices.get(position);
        } catch (IndexOutOfBoundsException x) {
            x.printStackTrace();
        }
        return -1;
    }

    public ArrayList<Integer> getServices() {
        //return idServices;
        return null;
    }

    public void setSpsspt(ArrayList<Integer> idServices) {
        //this.idServices = idServices;
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

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idServicePoint);
        dest.writeStringArray(new String[] {
                name, location, phone, altPhone, emailId, pincode
        });
        city.writeToParcel(dest, flags);
    }

    public static final Creator<ServicePoint> CREATOR = new Creator<ServicePoint>() {
        @Override
        public ServicePoint createFromParcel(Parcel source) {
            return new ServicePoint(source);
        }

        @Override
        public ServicePoint[] newArray(int size) {
            return new ServicePoint[size];
        }
    };

}
