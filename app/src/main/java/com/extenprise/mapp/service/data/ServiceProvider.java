package com.extenprise.mapp.service.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.extenprise.mapp.data.SignInData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ServiceProvider implements Parcelable {

    private int idServiceProvider;
    private SignInData signInData;
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
    //private byte[] img;

    public ServiceProvider() {
        signInData = new SignInData();
        services = new ArrayList<>();
        //links = new ArrayList<>();
    }
/*
    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }
*/

    public ServiceProvider(Parcel source) {
        signInData = new SignInData();
        services = new ArrayList<>();
        //links = new ArrayList<>();

        idServiceProvider = source.readInt();
        subscribed = source.readInt();

        String[] fields = new String[9];
        int i = 0;
        source.readStringArray(fields);
        fName = fields[i++];
        lName = fields[i++];
        signInData.setPhone(fields[i++]);
        signInData.setPasswd(fields[i++]);
        emailId = fields[i++];
        gender = fields[i++];
        qualification = fields[i++];
        regNo = fields[i++];

        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        try {
            subsDate = sdf.parse(fields[i]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idServiceProvider);
        dest.writeInt(subscribed);

        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        dest.writeStringArray(new String[]{
                fName, lName, getPhone(), getPasswd(), emailId,
                gender, qualification, regNo, sdf.format(subsDate)
        });
    }

    public static final Creator<ServiceProvider> CREATOR = new Creator<ServiceProvider>() {
        @Override
        public ServiceProvider createFromParcel(Parcel source) {
            return new ServiceProvider(source);
        }

        @Override
        public ServiceProvider[] newArray(int size) {
            return new ServiceProvider[size];
        }
    };

    public SignInData getSignInData() {
        return signInData;
    }

    public void setSignInData(SignInData signInData) {
        this.signInData = signInData;
    }
}
