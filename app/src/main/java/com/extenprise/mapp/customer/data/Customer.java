package com.extenprise.mapp.customer.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.extenprise.mapp.data.City;
import com.extenprise.mapp.data.SignInData;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.*;


public class Customer implements Parcelable {

    private int idCustomer;
    private SignInData signInData;
    private String fName;
    private String lName;
    private String emailId;
    private String gender;
    private int age;
    private float weight;
    private float height;
    private String location;
    private Date dob;
    private City city;
    private String pincode;
    private byte[] img;

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public Customer() {
        signInData = new SignInData();
        city = new City();
    }

    public Customer(Parcel source) {
        signInData = new SignInData();
        city = new City();
        idCustomer = source.readInt();

        String[] fields = new String[11];
        source.readStringArray(fields);
        int i = 0;
        fName = fields[i++];
        lName = fields[i++];
        signInData.setPhone(fields[i++]);
        signInData.setPasswd(fields[i++]);
        emailId = fields[i++];
        gender = fields[i++];
        location = fields[i++];
        pincode = fields[i++];
        city.setCity(fields[i++]);
        city.setState(fields[i++]);
        city.setCountry(fields[i]);

        age = source.readInt();
        weight = source.readFloat();
        height = source.readFloat();
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public int getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(int idCustomer) {
        this.idCustomer = idCustomer;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPhone() {
        return signInData.getPhone();
    }

    public void setPhone(String phone) {
        signInData.setPhone(phone);
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public int getIdCity() {
        return city.getIdCity();
    }

    public void setIdCity(int id) {
        city.setIdCity(id);
    }

    public String getCity() {
        return city.getCity();
    }

    public void setCity(String city) {
        this.city.setCity(city);
    }

    public String getState() {
        return city.getState();
    }

    public void setState(String state) {
        city.setState(state);
    }

    public String getCountry() {
        return city.getCountry();
    }

    public void setCountry(String country) {
        city.setCountry(country);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idCustomer);
        dest.writeStringArray(new String[]{
                fName, lName, signInData.getPhone(), signInData.getPasswd(), emailId, gender,
                location, pincode, city.getCity(), city.getState(), city.getCountry()
        });
        dest.writeInt(age);
        dest.writeFloat(weight);
        dest.writeFloat(height);
    }

    public static final Creator<Customer> CREATOR = new Creator<Customer>() {

        @Override
        public Customer createFromParcel(Parcel source) {
            return new Customer(source);
        }

        @Override
        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };
}
