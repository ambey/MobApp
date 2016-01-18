package com.extenprise.mapp.service.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ambey on 4/10/15.
 */
public class ServProvListItem implements Parcelable {
    public static final Creator<ServProvListItem> CREATOR = new Creator<ServProvListItem>() {
        @Override
        public ServProvListItem createFromParcel(Parcel in) {
            return new ServProvListItem(in);
        }

        @Override
        public ServProvListItem[] newArray(int size) {
            return new ServProvListItem[size];
        }
    };
    private String phone;
    private String firstName;
    private String lastName;
    private int idServProvHasServPt;
    private String speciality;
    private float experience;
    private String servPtName;
    private String servPtLocation;
    private String workingDays;
    private String category;

    public ServProvListItem() {
    }

    protected ServProvListItem(Parcel in) {
        phone = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        idServProvHasServPt = in.readInt();
        speciality = in.readString();
        experience = in.readFloat();
        servPtName = in.readString();
        servPtLocation = in.readString();
        workingDays = in.readString();
        category = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(phone);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeInt(idServProvHasServPt);
        dest.writeString(speciality);
        dest.writeFloat(experience);
        dest.writeString(servPtName);
        dest.writeString(servPtLocation);
        dest.writeString(workingDays);
        dest.writeString(category);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
}
