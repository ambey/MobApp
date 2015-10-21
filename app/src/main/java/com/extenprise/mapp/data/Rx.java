package com.extenprise.mapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by ambey on 31/8/15.
 */
public class Rx extends Report implements Parcelable{
    private ArrayList<RxItem> items;
    private byte[] scannedCopy;

    public Rx() {
        setType("Prescription");
        items = new ArrayList<>();
    }

    protected Rx(Parcel in) {
        super(in);
        items = in.createTypedArrayList(RxItem.CREATOR);
        scannedCopy = in.createByteArray();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(items);
        dest.writeByteArray(scannedCopy);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Rx> CREATOR = new Creator<Rx>() {
        @Override
        public Rx createFromParcel(Parcel in) {
            return new Rx(in);
        }

        @Override
        public Rx[] newArray(int size) {
            return new Rx[size];
        }
    };

    public byte[] getScannedCopy() {
        return scannedCopy;
    }

    public void setScannedCopy(byte[] scannedCopy) {
        this.scannedCopy = scannedCopy;
    }

    public ArrayList<RxItem> getItems() {
        return items;
    }

    public int getRxItemCount() {
        return items.size();
    }

    public boolean addItem(RxItem rxItem) {
        return items.add(rxItem);
    }
}
