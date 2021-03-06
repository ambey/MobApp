package com.extenprise.mapp.medico.service.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.extenprise.mapp.medico.data.City;
import com.extenprise.mapp.medico.data.SignInData;

/**
 * Created by avinash on 4/10/15.
 */
public class WorkPlace implements Parcelable {

    public static final Creator<WorkPlace> CREATOR = new Creator<WorkPlace>() {
        @Override
        public WorkPlace createFromParcel(Parcel in) {
            return new WorkPlace(in);
        }

        @Override
        public WorkPlace[] newArray(int size) {
            return new WorkPlace[size];
        }
    };
    private SignInData signInData;
    private String name;
    private String location;
    private String pincode;
    private String phone;
    private String altPhone;
    private String emailId;
    private City city;
    private String servPointType;
    private String servCategory;
    private String speciality;
    private float experience;
    private String qualification;
    private String workingDays;
    private float consultFee;
    private int startTime; //as minutes
    private int endTime;//as minutes
    private int idServicePoint;
    private int idService;
    private String notes;

    public WorkPlace() {
        signInData = new SignInData();
        city = new City();
    }

    public WorkPlace(Parcel source) {
        signInData = source.readParcelable(SignInData.class.getClassLoader());

        name = source.readString();
        location = source.readString();
        pincode = source.readString();
        phone = source.readString();
        altPhone = source.readString();
        emailId = source.readString();
        servPointType = source.readString();
        servCategory = source.readString();
        speciality = source.readString();
        qualification = source.readString();
        workingDays = source.readString();

        experience = source.readFloat();
        consultFee = source.readFloat();

        startTime = source.readInt();
        endTime = source.readInt();
        idServicePoint = source.readInt();
        idService = source.readInt();

        city = new City(source);
        notes = source.readString();
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getIdServicePoint() {
        return idServicePoint;
    }

    public void setIdServicePoint(int idServicePoint) {
        this.idServicePoint = idServicePoint;
    }

    public int getIdService() {
        return idService;
    }

    public void setIdService(int idService) {
        this.idService = idService;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(signInData, flags);

        dest.writeStringArray(new String[]{
                name, location, pincode, phone, altPhone, emailId,
                servPointType, servCategory, speciality, qualification, workingDays, notes
        });

        dest.writeFloat(experience);
        dest.writeFloat(consultFee);

        dest.writeInt(startTime);
        dest.writeInt(endTime);
        dest.writeInt(idServicePoint);
        dest.writeInt(idService);

        city.writeToParcel(dest, flags);
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

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public SignInData getSignInData() {
        return signInData;
    }

    public void setSignInData(SignInData signInData) {
        this.signInData = signInData;
    }

    public String getServCategory() {
        return servCategory;
    }

    public void setServCategory(String servCategory) {
        this.servCategory = servCategory;
    }
}
