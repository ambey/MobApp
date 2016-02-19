package com.extenprise.mapp.medico.service.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.extenprise.mapp.medico.data.LastVisited;
import com.extenprise.mapp.medico.data.SignInData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ServiceProvider implements Parcelable {

    public static final Creator<ServiceProvider> CREATOR = new Creator<ServiceProvider>() {
        @Override
        public ServiceProvider createFromParcel(Parcel in) {
            return new ServiceProvider(in);
        }

        @Override
        public ServiceProvider[] newArray(int size) {
            return new ServiceProvider[size];
        }
    };
    private int idServiceProvider;
    private SignInData signInData;
    private String stdCode;
    private String landlineNo;
    private String fName;
    private String lName;
    private String emailId;
    private String gender;
    private String qualification;
    private String regNo;
    private int subscribed;
    private Date subsDate;
    private ArrayList<ServProvHasServPt> services;
    //private ArrayList<ServiceProvider> links;
    private byte[] photo;
    private LastVisited lastVisited;

    public ServiceProvider() {
        signInData = new SignInData();
        services = new ArrayList<>();
        lastVisited = new LastVisited();
        //links = new ArrayList<>();
    }

    protected ServiceProvider(Parcel in) {
        idServiceProvider = in.readInt();
        signInData = in.readParcelable(SignInData.class.getClassLoader());
        stdCode = in.readString();
        landlineNo = in.readString();
        fName = in.readString();
        lName = in.readString();
        emailId = in.readString();
        gender = in.readString();
        qualification = in.readString();
        regNo = in.readString();
        subscribed = in.readInt();
        services = in.createTypedArrayList(ServProvHasServPt.CREATOR);
        photo = in.createByteArray();
        lastVisited = in.readParcelable(LastVisited.class.getClassLoader());

        String date = in.readString();
        if (!date.equals("")) {
            SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
            sdf.applyPattern("dd/MM/yyyy");
            try {
                subsDate = sdf.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idServiceProvider);
        dest.writeParcelable(signInData, flags);
        dest.writeString(stdCode);
        dest.writeString(landlineNo);
        dest.writeString(fName);
        dest.writeString(lName);
        dest.writeString(emailId);
        dest.writeString(gender);
        dest.writeString(qualification);
        dest.writeString(regNo);
        dest.writeInt(subscribed);
        dest.writeTypedList(services);
        dest.writeByteArray(photo);
        dest.writeParcelable(lastVisited, flags);
        String date = "";
        if (subsDate != null) {
            SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
            date = sdf.format(subsDate);
        }
        dest.writeString(date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public LastVisited getLastVisited() {
        return lastVisited;
    }

    public void setLastVisited(LastVisited lastVisited) {
        this.lastVisited = lastVisited;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public void addServProvHasServPt(ServProvHasServPt spspt) {
        if (services == null) {
            services = new ArrayList<>();
        }
        services.add(spspt);
    }

    public int getServiceCount() {
        if (services == null) {
            return 0;
        }
        return services.size();
    }

    public ServProvHasServPt getServProvHasServPt(int position) {
        try {
            return services.get(position);
        } catch (IndexOutOfBoundsException x) {
            x.printStackTrace();
        }
        return null;
    }

    public boolean addLink(ServiceProvider sp) {
/*
        return links.add(sp);
*/
        return false;
    }

    public ServiceProvider getLink(int position) {
/*
        try {
            return links.get(position);
        } catch (IndexOutOfBoundsException x) {
            x.printStackTrace();
        }
*/
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
        return signInData.getPhone();
    }

    public void setPhone(String phone) {
        signInData.setPhone(phone);
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPasswd() {
        return signInData.getPasswd();
    }

    public void setPasswd(String passwd) {
        signInData.setPasswd(passwd);
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(int subscribed) {
        this.subscribed = subscribed;
    }

    public Date getSubsDate() {
        return subsDate;
    }

    public void setSubsDate(Date subsDate) {
        this.subsDate = subsDate;
    }

    public ArrayList<ServProvHasServPt> getServices() {
        return services;
    }

    public void setServices(ArrayList<ServProvHasServPt> services) {
        this.services = services;
    }

    public SignInData getSignInData() {
        return signInData;
    }

    public void setSignInData(SignInData signInData) {
        this.signInData = signInData;
    }

    public String getLandlineNo() {
        return landlineNo;
    }

    public void setLandlineNo(String landlineNo) {
        this.landlineNo = landlineNo;
    }

    public String getStdCode() {
        return stdCode;
    }

    public void setStdCode(String stdCode) {
        this.stdCode = stdCode;
    }
}
