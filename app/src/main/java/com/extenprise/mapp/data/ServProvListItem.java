package com.extenprise.mapp.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ambey on 4/10/15.
 */
public class ServProvListItem implements Parcelable {
    private String firstName;
    private String lastName;
    private String speciality;
    private float experience;
    private String servPtName;
    private String servPtLocation;

    public ServProvListItem() {
    }

    public ServProvListItem(Parcel source) {
        String[] fields = new String[5];

        source.readStringArray(fields);
        int count = 0;
        firstName = fields[count++];
        lastName = fields[count++];
        speciality = fields[count++];
        servPtName = fields[count++];
        servPtLocation = fields[count];

        experience = source.readFloat();
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
        dest.writeStringArray(new String[]{firstName, lastName, speciality, servPtName, servPtLocation});
        dest.writeFloat(experience);
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
