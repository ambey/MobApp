package com.extenprise.mapp.medico.customer.data;

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
    private Date date;
    private int idServProvHasServPt;
    private int idCustomer;
    private String firstName;
    private String lastName;
    private String phone;
    private String clinicName;
    private String location;
    private String gender;
    private String time;
    private int reportCount;
    private float experience;
    private float consultFee;
    private String workingDays;
    private boolean confirmed;
    private boolean canceled;
    private byte[] rxCopy;

    public AppointmentListItem() {
    }

    protected AppointmentListItem(Parcel in) {
        idServProvHasServPt = in.readInt();
        idCustomer = in.readInt();
        firstName = in.readString();
        lastName = in.readString();
        phone = in.readString();
        gender = in.readString();
        time = in.readString();
        reportCount = in.readInt();
        experience = in.readFloat();
        consultFee = in.readFloat();
        workingDays = in.readString();
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
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public float getExperience() {
        return experience;
    }

    public void setExperience(float experience) {
        this.experience = experience;
    }

    public float getConsultFee() {
        return consultFee;
    }

    public void setConsultFee(float consultFee) {
        this.consultFee = consultFee;
    }

    public String getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(String workingDays) {
        this.workingDays = workingDays;
    }

    public int getIdServProvHasServPt() {
        return idServProvHasServPt;
    }

    public void setIdServProvHasServPt(int idServProvHasServPt) {
        this.idServProvHasServPt = idServProvHasServPt;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
        dest.writeInt(idServProvHasServPt);
        dest.writeInt(idCustomer);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(phone);
        dest.writeString(gender);
        dest.writeString(time);
        dest.writeInt(reportCount);
        dest.writeFloat(experience);
        dest.writeFloat(consultFee);
        dest.writeString(workingDays);
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
    }

}
