package com.extenprise.mapp.service.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.extenprise.mapp.util.ByteArrayToJSONAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ambey on 10/10/15.
 */
public class AppointmentListItem implements Parcelable {
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

    public AppointmentListItem() {
    }

    public AppointmentListItem(Parcel source) {
        String[] fields = new String[5];
        source.readStringArray(fields);

        int i = 0;
        servProvPhone = fields[i++];
        firstName = fields[i++];
        lastName = fields[i++];
        gender = fields[i++];
        time = fields[i++];

        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        String dateStr = source.readString();
        if (!dateStr.equals("")) {
            try {
                date = sdf.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        idServProvHasServPt = source.readInt();
        age = source.readInt();
        weight = source.readFloat();
        confirmed = source.readInt() > 0;
        canceled = source.readInt() > 0;
        int size = source.readInt();
        if(size > 0) {
            rxCopy = new byte[size];
            source.readByteArray(rxCopy);
        }
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
        dest.writeStringArray(new String[]{servProvPhone, firstName, lastName, gender, time});
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        String dateStr = "";
        if (date != null) {
            dateStr = sdf.format(date);
        }
        dest.writeString(dateStr);
        dest.writeInt(idServProvHasServPt);
        dest.writeInt(age);
        dest.writeFloat(weight);
        dest.writeInt(confirmed ? 1 : 0);
        dest.writeInt(canceled ? 1 : 0);
        int size = 0;
        if(rxCopy != null) {
            size = rxCopy.length;
        }
        dest.writeInt(size);
        if(size > 0) {
            dest.writeByteArray(rxCopy);
        }
    }

    public static final Creator<AppointmentListItem> CREATOR = new Creator<AppointmentListItem>() {

        @Override
        public AppointmentListItem createFromParcel(Parcel source) {
            return new AppointmentListItem(source);
        }

        @Override
        public AppointmentListItem[] newArray(int size) {
            return new AppointmentListItem[size];
        }
    };
}
