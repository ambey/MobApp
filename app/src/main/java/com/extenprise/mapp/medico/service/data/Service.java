package com.extenprise.mapp.medico.service.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Service implements Parcelable {

    public static final Creator<Service> CREATOR = new Creator<Service>() {
        @Override
        public Service createFromParcel(Parcel source) {
            return new Service(source);
        }

        @Override
        public Service[] newArray(int size) {
            return new Service[size];
        }
    };
    private int idService;
    private String category;
    private String speciality;

    public Service() {}

    public Service(Parcel source) {
        idService = source.readInt();
        category = source.readString();
        speciality = source.readString();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String servCategory) {
        this.category = servCategory;
    }

    public int getIdService() {
        return idService;
    }

    public void setIdService(int idService) {
        this.idService = idService;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idService);
        dest.writeString(category);
        dest.writeString(speciality);
    }
}
