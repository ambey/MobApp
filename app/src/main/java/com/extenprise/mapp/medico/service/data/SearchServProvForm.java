package com.extenprise.mapp.medico.service.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ambey on 4/10/15.
 */
public class SearchServProvForm implements Parcelable {
    public static final Creator<SearchServProvForm> CREATOR = new Creator<SearchServProvForm>() {

        @Override
        public SearchServProvForm createFromParcel(Parcel source) {
            return new SearchServProvForm(source);
        }

        @Override
        public SearchServProvForm[] newArray(int size) {
            return new SearchServProvForm[size];
        }
    };
    private String name;
    private String clinic;
    private String location;
    private String category;
    private String speciality;
    private String qualification;
    private String experience;
    private String startTime;
    private String endTime;
    private String workDays;
    private String consultFee;
    private String gender;

    public SearchServProvForm() {
    }

    public SearchServProvForm(Parcel source) {
        String[] fields = new String[parcelStringArray().length];

        source.readStringArray(fields);
        int count = 0;
        name = fields[count++];
        clinic = fields[count++];
        location = fields[count++];
        category = fields[count++];
        speciality = fields[count++];
        qualification = fields[count++];
        experience = fields[count++];
        startTime = fields[count++];
        endTime = fields[count++];
        workDays = fields[count++];
        consultFee = fields[count++];
        gender = fields[count];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClinic() {
        return clinic;
    }

    public void setClinic(String clinic) {
        this.clinic = clinic;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getWorkDays() {
        return workDays;
    }

    public void setWorkDays(String workDays) {
        this.workDays = workDays;
    }

    public String getConsultFee() {
        return consultFee;
    }

    public void setConsultFee(String consultFee) {
        this.consultFee = consultFee;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(parcelStringArray());
    }

    private String[] parcelStringArray() {
        return new String[] {name, clinic, location, category, speciality, qualification, experience,
                startTime, endTime, workDays, consultFee, gender};
    }
}
