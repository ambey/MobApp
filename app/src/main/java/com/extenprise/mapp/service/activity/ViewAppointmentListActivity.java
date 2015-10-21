package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.net.MappService;
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

    private Messenger mService;
    private ServiceResponseHandler mRespHandler = new ServiceResponseHandler(this);
    private ServiceProvider mServiceProv;

    private TextView mAppointmentDateTextView;
    private ListView mAppointmentListView;
    private String mSelectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointment_list);

        Intent intent = getIntent();
        mServiceProv = intent.getParcelableExtra("service");

        mAppointmentDateTextView = (TextView) findViewById(R.id.appointmentDateTextView);
        mAppointmentListView = (ListView) findViewById(R.id.appointmentListView);

        mSelectedDate = setCurrentDateOnView();
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

    public String setCurrentDateOnView() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        String date = sdf.format(c.getTime());
        mAppointmentDateTextView.setText(date);
        return date;
    }

    private void setAppointmentList() {
        Intent intent = new Intent(this, MappService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    private void gotAppontList(Bundle data) {
        ArrayList<AppointmentListItem> list = data.getParcelableArrayList("appontList");
        AppointmentListAdapter adapter = new AppointmentListAdapter(this, 0, list, mServiceProv);
        mAppointmentListView.setAdapter(adapter);
        mAppointmentListView.setOnItemClickListener(adapter);
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            mService = new Messenger(service);
            Bundle bundle = new Bundle();

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

            bundle.putParcelable("form", form);
            Message msg = Message.obtain(null, MappService.DO_APPONT_LIST);
            msg.replyTo = new Messenger(mRespHandler);
            msg.setData(bundle);

            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
        }
    };

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
        try {
            unbindService(mConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
