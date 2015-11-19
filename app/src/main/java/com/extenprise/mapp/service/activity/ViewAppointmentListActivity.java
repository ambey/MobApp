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
import java.util.Date;

public class ViewAppointmentListActivity extends Activity
        implements DateChangeListener, ResponseHandler {

    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));
    private ServiceProvider mServiceProv;

    private TextView mAppointmentDateTextView;
    private ListView mAppointmentListView;
    private String mSelectedDate;
    private ProgressBar mProgressBar;
    private TextView mMsgView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointment_list);

        mServiceProv = LoginHolder.servLoginRef;

        mAppointmentDateTextView = (TextView) findViewById(R.id.appointmentDateTextView);
        mAppointmentListView = (ListView) findViewById(R.id.appointmentListView);

        mSelectedDate = Utility.setCurrentDateOnView(mAppointmentDateTextView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMsgView = (TextView) findViewById(R.id.appontMsgView);

        setAppointmentList();
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


    private void setAppointmentList() {
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
        Utility.showProgress(this, mAppointmentListView, mProgressBar, true);
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
    }

    private void gotAppontList(Bundle data) {
        ArrayList<AppointmentListItem> list = data.getParcelableArrayList("appontList");
        assert list != null;
        AppointmentListAdapter adapter = new AppointmentListAdapter(this, 0, list, mServiceProv);
        mAppointmentListView.setAdapter(adapter);
        mAppointmentListView.setOnItemClickListener(adapter);
        if (list.size() > 0) {
            mMsgView.setVisibility(View.GONE);
        } else {
            mAppointmentListView.setVisibility(View.GONE);
            mMsgView.setVisibility(View.VISIBLE);
        }
    }

    public void showDatePicker(View view) {
        Utility.datePicker(view, mAppointmentDateTextView, this);
    }

    @Override
    public void datePicked(String date) {
        mSelectedDate = date;
        setAppointmentList();
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        Utility.showProgress(this, mAppointmentListView, mProgressBar, false);
        if (action == MappService.DO_APPONT_LIST) {
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
