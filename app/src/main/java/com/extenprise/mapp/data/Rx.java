package com.extenprise.mapp.data;

import java.util.ArrayList;

/**
 * Created by ambey on 31/8/15.
 */
public class Rx extends Report {
    private ArrayList<RxItem> items;
    private byte[] scannedCopy;

    public Rx() {
        setType("Prescription");
        items = new ArrayList<>();
    }

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
