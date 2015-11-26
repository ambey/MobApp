package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.net.AppStatus;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.MappServiceConnection;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.data.AppointmentListItem;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.service.ui.AppointmentListAdapter;
import com.extenprise.mapp.util.DateChangeListener;
import com.extenprise.mapp.util.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ViewAppointmentListActivity extends Activity
        implements DateChangeListener, ResponseHandler {

    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));
    private ServiceProvider mServiceProv;

    private TextView mAppontsDateView;
    private ListView mUpcomingAppontsListView;
    private ListView mAppontsListView;
    private String mSelectedDate;
    private ProgressBar mUpcomingAppontsProgressBar;
    private ProgressBar mAppontsProgressBar;
    private TextView mMsgView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointment_list);

        mServiceProv = LoginHolder.servLoginRef;

        mUpcomingAppontsProgressBar = (ProgressBar) findViewById(R.id.appontsProgressBar);
        mAppontsProgressBar = (ProgressBar) findViewById(R.id.pastAppontsProgressBar);

        mAppontsDateView = (TextView) findViewById(R.id.appointmentDateTextView);

        mUpcomingAppontsListView = (ListView) findViewById(R.id.upcomingAppontsListView);
        mAppontsListView = (ListView) findViewById(R.id.appontsListView);

        //mSelectedDate = Utility.setCurrentDateOnView(mPastAppontsDateView);
        mMsgView = (TextView) findViewById(R.id.appontMsgView);

        setUpcomingAppontList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_appointment_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpcomingAppontList() {
        if (!AppStatus.getInstance(this).isOnline()) {
            Utility.showMessage(this, R.string.error_not_online);
            return;
        }
        AppointmentListItem form = new AppointmentListItem();
        form.setServProvPhone(mServiceProv.getPhone());
        form.setDate(new Date());

        Bundle bundle = new Bundle();
        bundle.putParcelable("form", form);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_UPCOMING_APPONT_LIST);
        mMsgView.setVisibility(View.GONE);
        Utility.showProgress(this, mUpcomingAppontsListView, mUpcomingAppontsProgressBar, true);
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
    }

    private void setAppontList() {
        if (!AppStatus.getInstance(this).isOnline()) {
            Utility.showMessage(this, R.string.error_not_online);
            return;
        }
        AppointmentListItem form = new AppointmentListItem();
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        Date date = new Date();
        try {
            date = sdf.parse(mSelectedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        form.setServProvPhone(mServiceProv.getPhone());
        form.setDate(date);

        Bundle bundle = new Bundle();
        bundle.putParcelable("form", form);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_APPONT_LIST);
        mMsgView.setVisibility(View.GONE);
        Utility.showProgress(this, mAppontsListView, mAppontsProgressBar, true);
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
    }

    private void gotUpcomingAppontList(Bundle data) {
        Utility.showProgress(this, mUpcomingAppontsListView, mUpcomingAppontsProgressBar, false);
        ArrayList<AppointmentListItem> list = data.getParcelableArrayList("appontList");
        assert list != null;
        AppointmentListAdapter adapter = new AppointmentListAdapter(this, 0, list, mServiceProv);
        adapter.setShowDate(true);
        mUpcomingAppontsListView.setAdapter(adapter);
        mUpcomingAppontsListView.setOnItemClickListener(adapter);
        if (list.size() > 0) {
            mMsgView.setVisibility(View.GONE);
        } else {
            mUpcomingAppontsListView.setVisibility(View.GONE);
            mMsgView.setVisibility(View.VISIBLE);
        }
    }

    private void gotAppontList(Bundle data) {
        Utility.showProgress(this, mAppontsListView, mAppontsProgressBar, false);
        ArrayList<AppointmentListItem> list = data.getParcelableArrayList("appontList");
        assert list != null;
        AppointmentListAdapter adapter = new AppointmentListAdapter(this, 0, list, mServiceProv);
        mAppontsListView.setAdapter(adapter);
        mAppontsListView.setOnItemClickListener(adapter);
        if (list.size() > 0) {
            mAppontsListView.setVisibility(View.VISIBLE);
            mMsgView.setVisibility(View.GONE);
        } else {
            mAppontsListView.setVisibility(View.GONE);
            mMsgView.setVisibility(View.VISIBLE);
        }
    }

    public void showDatePicker(View view) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
        long yesterday = cal.getTimeInMillis();
        Utility.datePicker(view, mAppontsDateView, this, yesterday, yesterday, -1);
    }

    @Override
    public void datePicked(String date) {
        mSelectedDate = date;
        setAppontList();
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        Utility.showProgress(this, mAppontsListView, mUpcomingAppontsProgressBar, false);
        if (action == MappService.DO_UPCOMING_APPONT_LIST) {
            gotUpcomingAppontList(data);
            return true;
        } else if (action == MappService.DO_APPONT_LIST) {
            gotAppontList(data);
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        Intent intent = super.getParentActivityIntent();
        if (intent != null) {
            intent.putExtra("service", mServiceProv);
        }
        return intent;
    }
}
