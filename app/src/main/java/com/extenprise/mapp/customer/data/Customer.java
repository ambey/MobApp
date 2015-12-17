package com.extenprise.mapp.customer.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.extenprise.mapp.data.Appointment;
import com.extenprise.mapp.data.City;
import com.extenprise.mapp.data.SignInData;
import com.extenprise.mapp.util.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Customer implements Parcelable {

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
    private byte[] photo;
    private ArrayList<Appointment> appointments;

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public Customer() {
        signInData = new SignInData();
        city = new City();
        appointments = new ArrayList<>();
    }

    public Customer(Parcel source) {
        signInData = new SignInData();
        city = new City();
        appointments = new ArrayList<>();

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
        try {
            SimpleDateFormat sdf = (SimpleDateFormat)SimpleDateFormat.getDateInstance();
            sdf.applyPattern("dd/MM/yyyy");
            dob = sdf.parse(source.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        photo = source.createByteArray();
    }

    public ArrayList<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(ArrayList<Appointment> appointments) {
        this.appointments = appointments;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public int getAge() {
        if(age > 0) {
            return age;
        }
        return Utility.getAge(dob);
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

    public SignInData getSignInData() {
        return signInData;
    }

    public void setSignInData(SignInData signInData) {
        this.signInData = signInData;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
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

        String dateStr = "";
        if(dob != null) {
            SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
            sdf.applyPattern("dd/MM/yyyy");
            dateStr = sdf.format(dob);
        }
        dest.writeString(dateStr);
        dest.writeByteArray(photo);
    }
}
