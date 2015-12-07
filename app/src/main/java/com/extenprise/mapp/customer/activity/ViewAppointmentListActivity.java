package com.extenprise.mapp.customer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.customer.data.Customer;
import com.extenprise.mapp.customer.ui.AppointmentListAdapter;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.MappServiceConnection;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.customer.data.AppointmentListItem;
import com.extenprise.mapp.util.Utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ViewAppointmentListActivity extends Activity implements ResponseHandler {

    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));

    private ListView mUpcomingListView;
    private ListView mPastListView;
    private ArrayList<AppointmentListItem> mUpcomingList;
    private ArrayList<AppointmentListItem> mPastList;
    private TextView mMsgView, mUpcomMsgView;

    private ProgressBar mUpcomingProgress;
    private ProgressBar mPastProgress;

    private Customer mCust;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cust_appointment_list);

        Intent intent = getIntent();
        mCust = intent.getParcelableExtra("customer");

        mUpcomingListView = (ListView) findViewById(R.id.upcomingAppontsList);
        mPastListView = (ListView) findViewById(R.id.pastAppontsList);
        mUpcomingProgress = (ProgressBar) findViewById(R.id.upcomingProgress);
        mPastProgress = (ProgressBar) findViewById(R.id.pastProgress);
        mMsgView = (TextView) findViewById(R.id.pastAppontMsgView);
        mUpcomMsgView = (TextView) findViewById(R.id.appontMsgView);

        getUpcomingList();
    }

    private void setupForm(AppointmentListItem form) {
        form.setIdCustomer(mCust.getIdCustomer());
        Calendar cal = Calendar.getInstance();
        form.setDate(cal.getTime());
        form.setTime(String.format("%d:%d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));
    }

    private void getUpcomingList() {
        Utility.showProgress(this, mUpcomingListView, mUpcomingProgress, true);
        AppointmentListItem item = new AppointmentListItem();
        setupForm(item);
        Bundle bundle = new Bundle();
        bundle.putParcelable("appont", item);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_GET_CUST_UPCOMING_APPONTS);
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
    }

    private void getPastList() {
        Utility.showProgress(this, mPastListView, mPastProgress, true);
        AppointmentListItem item = new AppointmentListItem();
        setupForm(item);
        Bundle bundle = new Bundle();
        bundle.putParcelable("appont", item);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_GET_CUST_PAST_APPONTS);
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
    }

    private void gotUpcomingApponts(Bundle data) {
        mUpcomingList = data.getParcelableArrayList("appontList");
        if (mUpcomingList.size() > 0) {
            mUpcomMsgView.setVisibility(View.GONE);
            mUpcomingListView.setVisibility(View.VISIBLE);
        } else {
            mUpcomingListView.setVisibility(View.GONE);
            mUpcomMsgView.setVisibility(View.VISIBLE);
        }
        getPastList();
    }

    private void gotPastApponts(Bundle data) {
        Utility.showProgress(this, mUpcomingListView, mUpcomingProgress, false);
        AppointmentListAdapter adapter = new AppointmentListAdapter(this, 0, mUpcomingList);
        mUpcomingListView.setAdapter(adapter);
        Utility.showProgress(this, mPastListView, mPastProgress, false);
        mPastList = data.getParcelableArrayList("appontList");
        adapter = new AppointmentListAdapter(this, 0, mPastList);
        mPastListView.setAdapter(adapter);

        if (mPastList.size() > 0) {
            mMsgView.setVisibility(View.GONE);
            mPastListView.setVisibility(View.VISIBLE);
        } else {
            mPastListView.setVisibility(View.GONE);
            mMsgView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        if (action == MappService.DO_GET_CUST_UPCOMING_APPONTS) {
            gotUpcomingApponts(data);
            return true;
        } else if (action == MappService.DO_GET_CUST_PAST_APPONTS) {
            gotPastApponts(data);
            return true;
        }

        return false;
    }
}
