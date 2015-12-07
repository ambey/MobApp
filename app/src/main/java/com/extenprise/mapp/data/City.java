package com.extenprise.mapp.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ambey on 21/9/15.
 */
public class City implements Parcelable {
    private int idCity;
    private String city;
    private String state;
    private String country;

    public City() {
        country = "India";
    }

    @Override
    public String toString() {
        return "City : " + city +
                ", State : " + state +
                ", Country : " + country;
    }

    public City(Parcel source) {
        idCity = source.readInt();
        city = source.readString();
        state = source.readString();
        country = source.readString();
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getIdCity() {
        return idCity;
    }

    public void setIdCity(int idCity) {
        this.idCity = idCity;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idCity);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(country);
    }
    public static final Creator<City> CREATOR = new Creator<City>() {
        @Override
        public City createFromParcel(Parcel source) {
            return new City(source);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };
}
