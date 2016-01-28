package com.extenprise.mapp.medico.service.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.extenprise.mapp.medico.data.RxItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ambey on 3/11/15.
 */
public class RxItemAvailability implements Parcelable {
    public static final Creator<RxItemAvailability> CREATOR = new Creator<RxItemAvailability>() {
        @Override
        public RxItemAvailability createFromParcel(Parcel in) {
            return new RxItemAvailability(in);
        }

        @Override
        public RxItemAvailability[] newArray(int size) {
            return new RxItemAvailability[size];
        }
    };
    private int idServProvHasServPt;
    private int idRx;
    private int status;
    private Date receivedDate;
    private ArrayList<RxItem> availableList;

    public RxItemAvailability() {
        availableList = new ArrayList<>();
    }

    protected RxItemAvailability(Parcel in) {
        idServProvHasServPt = in.readInt();
        idRx = in.readInt();
        status = in.readInt();
        try {
            SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
            sdf.applyPattern("dd/MM/yyyy");
            receivedDate = sdf.parse(in.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        availableList = in.createTypedArrayList(RxItem.CREATOR);
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

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public ArrayList<RxItem> getAvailableList() {
        return availableList;
    }

    public void setAvailableList(ArrayList<RxItem> availableList) {
        this.availableList = availableList;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idServProvHasServPt);
        dest.writeInt(idRx);
        dest.writeInt(status);
        String dateStr = "";
        if (receivedDate != null) {
            SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
            sdf.applyPattern("dd/MM/yyyy");
            dateStr = sdf.format(receivedDate);
        }
        dest.writeString(dateStr);
        dest.writeTypedList(availableList);
    }
}
