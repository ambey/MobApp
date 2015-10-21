package com.extenprise.mapp.service.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.extenprise.mapp.data.Rx;

/**
 * Created by ambey on 21/10/15.
 */
public class MedStoreRxForm implements Parcelable {
    private int idServProvHasServPt;
    private int idRx;

    public MedStoreRxForm() {
    }

    protected MedStoreRxForm(Parcel in) {
        idServProvHasServPt = in.readInt();
        idRx = in.readInt();
    }

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
    }
}
