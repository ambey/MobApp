package com.extenprise.mapp.service.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.extenprise.mapp.customer.data.Customer;
import com.extenprise.mapp.data.Rx;

/**
 * Created by ambey on 30/10/15.
 */
public class RxInboxItem implements Parcelable{
    private ServProvListItem servProv;
    private Customer customer;
    private Rx rx;
    private ReportService reportService;

    protected RxInboxItem(Parcel in) {
        servProv = in.readParcelable(ServProvListItem.class.getClassLoader());
        customer = in.readParcelable(Customer.class.getClassLoader());
        rx = in.readParcelable(Rx.class.getClassLoader());
        reportService = in.readParcelable(ReportService.class.getClassLoader());
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(servProv, flags);
        dest.writeParcelable(customer, flags);
        dest.writeParcelable(rx, flags);
        dest.writeParcelable(reportService, flags);
    }
}
