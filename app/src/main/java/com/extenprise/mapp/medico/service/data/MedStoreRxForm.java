package com.extenprise.mapp.medico.service.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ambey on 21/10/15.
 */
public class MedStoreRxForm implements Parcelable {
    public static final Creator<MedStoreRxForm> CREATOR = new Creator<MedStoreRxForm>() {
        @Override
        public MedStoreRxForm createFromParcel(Parcel in) {
            return new MedStoreRxForm(in);
        }

        @Override
        public MedStoreRxForm[] newArray(int size) {
            return new MedStoreRxForm[size];
        }
    };
    private int idServProvHasServPt;
    private int idRx;
    private Date date;
    private int status;

    public MedStoreRxForm() {
    }

    protected MedStoreRxForm(Parcel in) {
        idServProvHasServPt = in.readInt();
        idRx = in.readInt();
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        try {
            date = sdf.parse(in.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        status = in.readInt();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIdServProvHasServPt() {
        return idServProvHasServPt;
    }

    public void setIdServProvHasServPt(int idServProvHasServPt) {
        this.idServProvHasServPt = idServProvHasServPt;
    }

    public int getIdRx() {
        return idRx;
    }

    public void setIdRx(int idRx) {
        this.idRx = idRx;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idServProvHasServPt);
        dest.writeInt(idRx);
        String dateStr = "";
        if(date != null) {
            SimpleDateFormat sdf = (SimpleDateFormat)SimpleDateFormat.getDateInstance();
            sdf.applyPattern("dd/MM/yyyy");
            dateStr = sdf.format(date);
        }
        dest.writeString(dateStr);
        dest.writeInt(status);
    }
}
