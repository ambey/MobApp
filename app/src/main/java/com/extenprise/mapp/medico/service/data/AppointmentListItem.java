package com.extenprise.mapp.medico.service.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ambey on 10/10/15.
 */
public class AppointmentListItem implements Parcelable {
    public static final Creator<AppointmentListItem> CREATOR = new Creator<AppointmentListItem>() {
        @Override
        public AppointmentListItem createFromParcel(Parcel in) {
            return new AppointmentListItem(in);
        }

        @Override
        public AppointmentListItem[] newArray(int size) {
            return new AppointmentListItem[size];
        }
    };
    private String servProvPhone;
    private Date date;
    private int idServProvHasServPt;
    private int idCustomer;
    private String firstName;
    private String lastName;
    private String gender;
    private int age;
    private float weight;
    private String time;
    private int reportCount;
    private boolean confirmed;
    private boolean canceled;
    private byte[] rxCopy;
    private int rxPresent;

    public AppointmentListItem() {
    }

    protected AppointmentListItem(Parcel in) {
        servProvPhone = in.readString();
        idServProvHasServPt = in.readInt();
        idCustomer = in.readInt();
        firstName = in.readString();
        lastName = in.readString();
        gender = in.readString();
        age = in.readInt();
        weight = in.readFloat();
        time = in.readString();
        reportCount = in.readInt();
        confirmed = in.readByte() != 0;
        canceled = in.readByte() != 0;
        rxCopy = in.createByteArray();
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        String dateStr = in.readString();
        if (!dateStr.equals("")) {
            try {
                date = sdf.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        rxPresent = in.readInt();
    }

    public int getRxPresent() {
        return rxPresent;
    }

    public void setRxPresent(int rxPresent) {
        this.rxPresent = rxPresent;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public int getReportCount() {
        return reportCount;
    }

    public void setReportCount(int reportCount) {
        this.reportCount = reportCount;
    }

    public int getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(int idCustomer) {
        this.idCustomer = idCustomer;
    }

    public String getServProvPhone() {
        return servProvPhone;
    }

    public void setServProvPhone(String servProvPhone) {
        this.servProvPhone = servProvPhone;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getIdServProvHasServPt() {
        return idServProvHasServPt;
    }

    public void setIdServProvHasServPt(int idServProvHasServPt) {
        this.idServProvHasServPt = idServProvHasServPt;
    }

    public byte[] getRxCopy() {
        return rxCopy;
    }

    public void setRxCopy(byte[] rxCopy) {
        this.rxCopy = rxCopy;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(servProvPhone);
        dest.writeInt(idServProvHasServPt);
        dest.writeInt(idCustomer);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(gender);
        dest.writeInt(age);
        dest.writeFloat(weight);
        dest.writeString(time);
        dest.writeInt(reportCount);
        dest.writeByte((byte) (confirmed ? 1 : 0));
        dest.writeByte((byte) (canceled ? 1 : 0));
        dest.writeByteArray(rxCopy);
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        String dateStr = "";
        if (date != null) {
            dateStr = sdf.format(date);
        }
        dest.writeString(dateStr);
        dest.writeInt(rxPresent);
    }
}
