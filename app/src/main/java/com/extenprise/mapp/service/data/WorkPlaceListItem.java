package com.extenprise.mapp.service.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.extenprise.mapp.data.SignInData;

/**
 * Created by avinash on 4/10/15.
 */
public class WorkPlaceListItem implements Parcelable {

    private SignInData signInData;
    private String name;
    private String location;
    private String pincode;
    private String phone;
    private String altPhone;
    private String emailId;
    private String city;
    private String servPointType;
    private String servCatagory;
    private String speciality;
    private float experience;
    private String qualification;
    private String workingDays;
    private float consultFee;
    private int startTime; //as minutes
    private int endTime;//as minutes

    public WorkPlaceListItem() {
        signInData = new SignInData();
    }

    public WorkPlaceListItem(Parcel source) {
        String[] fields = new String[12];
        signInData = new SignInData();

        source.readStringArray(fields);
        int count = 0;
        name = fields[count++];
        location = fields[count++];
        speciality = fields[count++];
        pincode = fields[count++];
        phone = fields[count++];
        altPhone = fields[count++];
        emailId = fields[count++];
        city = fields[count++];
        servPointType = fields[count++];
        servCatagory = fields[count++];
        qualification = fields[count++];
        signInData.setPhone(fields[count++]);
        signInData.setPasswd(fields[count++]);
        workingDays = fields[count];

        experience = source.readFloat();
        startTime = source.readInt();
        endTime = source.readInt();
        consultFee = source.readFloat();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{name, location, pincode, phone, altPhone,
                emailId, city, servPointType, servCatagory, speciality, qualification, workingDays});
        dest.writeFloat(experience);
        dest.writeFloat(consultFee);
        dest.writeInt(startTime);
        dest.writeInt(endTime);
    }

    public static final Creator<WorkPlaceListItem> CREATOR = new Creator<WorkPlaceListItem>() {

        @Override
        public WorkPlaceListItem createFromParcel(Parcel source) {
            return new WorkPlaceListItem(source);
        }

        @Override
        public WorkPlaceListItem[] newArray(int size) {
            return new WorkPlaceListItem[size];
        }
    };

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

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
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

    public String getServPointType() {
        return servPointType;
    }

    public void setServPointType(String servPointType) {
        this.servPointType = servPointType;
    }

    public String getServCatagory() {
        return servCatagory;
    }

    public void setServCatagory(String servCatagory) {
        this.servCatagory = servCatagory;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public float getExperience() {
        return experience;
    }

    public void setExperience(float experience) {
        this.experience = experience;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(String workingDays) {
        this.workingDays = workingDays;
    }

    public float getConsultFee() {
        return consultFee;
    }

    public void setConsultFee(float consultFee) {
        this.consultFee = consultFee;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public SignInData getSignInData() {
        return signInData;
    }

    public void setSignInData(SignInData signInData) {
        this.signInData = signInData;
    }
}
