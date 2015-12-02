package com.extenprise.mapp.service.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ambey on 4/10/15.
 */
public class ServProvListItem implements Parcelable {
    private String phone;
    private String firstName;
    private String lastName;
    private int idServProvHasServPt;
    private String speciality;
    private float experience;
    private String servPtName;
    private String servPtLocation;
    private String workingDays;


    public ServProvListItem() {
    }

    public ServProvListItem(Parcel source) {
        String[] fields = new String[7];

        source.readStringArray(fields);
        int count = 0;
        phone = fields[count++];
        firstName = fields[count++];
        lastName = fields[count++];
        speciality = fields[count++];
        servPtName = fields[count++];
        servPtLocation = fields[count++];
        workingDays = fields[count];

        experience = source.readFloat();
        idServProvHasServPt = source.readInt();
    }

    public String getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(String workingDays) {
        this.workingDays = workingDays;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getIdServProvHasServPt() {
        return idServProvHasServPt;
    }

    public void setIdServProvHasServPt(int idServProvHasServPt) {
        this.idServProvHasServPt = idServProvHasServPt;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getServPtName() {
        return servPtName;
    }

    public void setServPtName(String servPtName) {
        this.servPtName = servPtName;
    }

    public String getServPtLocation() {
        return servPtLocation;
    }

    public void setServPtLocation(String servPtLocation) {
        this.servPtLocation = servPtLocation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{phone, firstName, lastName, speciality, servPtName, servPtLocation, workingDays});
        dest.writeFloat(experience);
        dest.writeInt(idServProvHasServPt);
    }

    public static final Creator<ServProvListItem> CREATOR = new Creator<ServProvListItem>() {

        @Override
        public ServProvListItem createFromParcel(Parcel source) {
            return new ServProvListItem(source);
        }

        @Override
        public ServProvListItem[] newArray(int size) {
            return new ServProvListItem[size];
        }
    };
}
