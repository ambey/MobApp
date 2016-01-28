package com.extenprise.mapp.medico.service.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.extenprise.mapp.medico.customer.data.Customer;
import com.extenprise.mapp.medico.data.Rx;

import java.util.List;

/**
 * Created by ambey on 30/10/15.
 */
public class RxInboxItem implements Parcelable {
    public static final Creator<RxInboxItem> CREATOR = new Creator<RxInboxItem>() {
        @Override
        public RxInboxItem createFromParcel(Parcel in) {
            return new RxInboxItem(in);
        }

        @Override
        public RxInboxItem[] newArray(int size) {
            return new RxInboxItem[size];
        }
    };
    private ServProvListItem servProv;
    private Customer customer;
    private Rx rx;
    private ReportService reportService;
    private List<ServProvListItem> medStoreList;

    public RxInboxItem() {
    }

    protected RxInboxItem(Parcel in) {
        servProv = in.readParcelable(ServProvListItem.class.getClassLoader());
        customer = in.readParcelable(Customer.class.getClassLoader());
        rx = in.readParcelable(Rx.class.getClassLoader());
        reportService = in.readParcelable(ReportService.class.getClassLoader());
        medStoreList = in.createTypedArrayList(ServProvListItem.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(servProv, flags);
        dest.writeParcelable(customer, flags);
        dest.writeParcelable(rx, flags);
        dest.writeParcelable(reportService, flags);
        dest.writeTypedList(medStoreList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public ServProvListItem getServProv() {
        return servProv;
    }

    public void setServProv(ServProvListItem servProv) {
        this.servProv = servProv;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Rx getRx() {
        return rx;
    }

    public void setRx(Rx rx) {
        this.rx = rx;
    }

    public ReportService getReportService() {
        return reportService;
    }

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    public List<ServProvListItem> getMedStoreList() {
        return medStoreList;
    }

    public void setMedStoreList(List<ServProvListItem> medStoreList) {
        this.medStoreList = medStoreList;
    }
}
