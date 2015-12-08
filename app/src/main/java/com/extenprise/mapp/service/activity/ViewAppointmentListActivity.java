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
import com.extenprise.mapp.util.Utility;

import java.util.ArrayList;
import java.util.Calendar;

public class ViewAppointmentListActivity extends Activity
        implements ResponseHandler {

    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));
    private ServiceProvider mServiceProv;

    private ArrayList<AppointmentListItem> mUpcomingList;

    private ListView mUpcomingListView;
    private ListView mPastListView;
    private ProgressBar mUpcomingProgress;
    private ProgressBar mPastProgress;
    private TextView mUpcomingMsgView;
    private TextView mPastMsgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointment_list);

        mServiceProv = LoginHolder.servLoginRef;

        mUpcomingListView = (ListView) findViewById(R.id.upcomingAppontsList);
        mPastListView = (ListView) findViewById(R.id.pastAppontsList);
        mUpcomingProgress = (ProgressBar) findViewById(R.id.upcomingProgress);
        mPastProgress = (ProgressBar) findViewById(R.id.pastProgress);
        mUpcomingMsgView = (TextView) findViewById(R.id.upcomingAppontsMsgView);
        mPastMsgView = (TextView) findViewById(R.id.pastAppontsMsgView);

        getUpcomingList();
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

    private void getUpcomingList() {
        if (!AppStatus.getInstance(this).isOnline()) {
            Utility.showMessage(this, R.string.error_not_online);
            return;
        }
        Utility.showProgress(this, mUpcomingListView, mUpcomingProgress, true);
        AppointmentListItem form = new AppointmentListItem();
        setupForm(form);

        Bundle bundle = new Bundle();
        bundle.putParcelable("form", form);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_UPCOMING_APPONT_LIST);
        //mMsgView.setVisibility(View.GONE);
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
    }

    private void getPastList() {
        Utility.showProgress(this, mPastListView, mPastProgress, true);
        AppointmentListItem form = new AppointmentListItem();
        setupForm(form);
        Bundle bundle = new Bundle();
        bundle.putParcelable("form", form);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_PAST_APPONT_LIST);
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
    }

    private void setupForm(AppointmentListItem form) {
        form.setServProvPhone(mServiceProv.getPhone());
        Calendar cal = Calendar.getInstance();
        form.setDate(cal.getTime());
        form.setTime(String.format("%d:%d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));
    }

    private void gotUpcomingAppontList(Bundle data) {
        mUpcomingList = data.getParcelableArrayList("appontList");
        getPastList();
    }

    private void gotPastAppontList(Bundle data) {
        Utility.showProgress(this, mUpcomingListView, mUpcomingProgress, false);
        AppointmentListAdapter adapter = new AppointmentListAdapter(this, 0, mUpcomingList, mServiceProv);
        adapter.setShowDate(true);
        mUpcomingListView.setAdapter(adapter);
        mUpcomingListView.setOnItemClickListener(adapter);
        if (mUpcomingList != null && mUpcomingList.size() > 0) {
            mUpcomingMsgView.setVisibility(View.GONE);
        } else {
            mUpcomingListView.setVisibility(View.GONE);
            mUpcomingMsgView.setVisibility(View.VISIBLE);
        }

        Utility.showProgress(this, mPastListView, mPastProgress, false);
        ArrayList<AppointmentListItem> list = data.getParcelableArrayList("appontList");
        assert list != null;
        adapter = new AppointmentListAdapter(this, 0, list, mServiceProv);
        adapter.setShowDate(true);
        mPastListView.setAdapter(adapter);
        mPastListView.setOnItemClickListener(adapter);
        if (list.size() > 0) {
            mPastListView.setVisibility(View.VISIBLE);
            mPastMsgView.setVisibility(View.GONE);
        } else {
            mPastListView.setVisibility(View.GONE);
            mPastMsgView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        if (action == MappService.DO_UPCOMING_APPONT_LIST) {
            gotUpcomingAppontList(data);
            return true;
        } else if (action == MappService.DO_PAST_APPONT_LIST) {
            gotPastAppontList(data);
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
