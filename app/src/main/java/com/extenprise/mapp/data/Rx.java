package com.extenprise.mapp.data;

import java.util.ArrayList;

/**
 * Created by ambey on 31/8/15.
 */
public class Rx {
    private Appointment appointment;
    private String id;
    private ArrayList<RxItem> items;

    public Rx() {
        items = new ArrayList<>();
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public int getRxItemCount() {
        return items.size();
    }

    public boolean addItem(RxItem rxItem) {
        return items.add(rxItem);
    }
}
