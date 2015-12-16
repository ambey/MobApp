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
import com.extenprise.mapp.data.WorkingDataStore;
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
    private ArrayList<AppointmentListItem> mPastList;

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
        Bundle data = WorkingDataStore.getBundle();
        mUpcomingList = data.getParcelableArrayList("upcomingList");
        if (mUpcomingList != null) {
            gotUpcomingAppontList(data);
            return;
        }
        AppointmentListItem form = new AppointmentListItem();
        setupForm(form);

        Bundle bundle = new Bundle();
        bundle.putParcelable("form", form);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_UPCOMING_APPONT_LIST);
        //mMsgView.setVisibility(View.GONE);
        if (Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE)) {
            Utility.showProgress(this, mUpcomingListView, mUpcomingProgress, true);
        }
    }

    private void getPastList() {
        Bundle data = WorkingDataStore.getBundle();
        mPastList = data.getParcelableArrayList("pastList");
        if (mPastList != null) {
            gotPastAppontList(data);
            return;
        }
        AppointmentListItem form = new AppointmentListItem();
        setupForm(form);
        Bundle bundle = new Bundle();
        bundle.putParcelable("form", form);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_PAST_APPONT_LIST);
        if (Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE)) {
            Utility.showProgress(this, mPastListView, mPastProgress, true);
        }
    }

    private void setupForm(AppointmentListItem form) {
        form.setServProvPhone(mServiceProv.getPhone());
        Calendar cal = Calendar.getInstance();
        form.setDate(cal.getTime());
        form.setTime(String.format("%d:%d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));
    }

    private void gotUpcomingAppontList(Bundle data) {
        if (mUpcomingList == null) {
            mUpcomingList = data.getParcelableArrayList("appontList");
            Bundle bundle = WorkingDataStore.getBundle();
            bundle.putParcelableArrayList("upcomingList", mUpcomingList);
        }
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
        if (mPastList == null) {
            mPastList = data.getParcelableArrayList("appontList");
            Bundle bundle = WorkingDataStore.getBundle();
            bundle.putParcelableArrayList("pastList", mPastList);
        }
        assert mPastList != null;
        adapter = new AppointmentListAdapter(this, 0, mPastList, mServiceProv);
        adapter.setShowDate(true);
        mPastListView.setAdapter(adapter);
        mPastListView.setOnItemClickListener(adapter);
        if (mPastList.size() > 0) {
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
        /* Remove the data from WorkingDataStore */
        Bundle bundle = WorkingDataStore.getBundle();
        bundle.remove("upcomingList");
        bundle.remove("pastList");
        Intent intent = super.getParentActivityIntent();
        if (intent != null) {
            intent.putExtra("service", mServiceProv);
        }
        return intent;
    }
}
