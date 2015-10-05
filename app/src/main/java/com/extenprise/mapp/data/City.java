package com.extenprise.mapp.data;

/**
 * Created by ambey on 21/9/15.
 */
public class City {
    private int idCity;
    private String city;
    private String state;
    private String country;

    public City() {
        country = "India";
        state = "Maharashtra";
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
}
