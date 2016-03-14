package com.extenprise.mapp.medico.service.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.activity.LoginActivity;
import com.extenprise.mapp.medico.data.WorkingDataStore;
import com.extenprise.mapp.medico.net.MappService;
import com.extenprise.mapp.medico.net.MappServiceConnection;
import com.extenprise.mapp.medico.net.ResponseHandler;
import com.extenprise.mapp.medico.net.ServiceResponseHandler;
import com.extenprise.mapp.medico.service.data.AppointmentListItem;
import com.extenprise.mapp.medico.service.data.ServiceProvider;
import com.extenprise.mapp.medico.service.ui.AppointmentListAdapter;
import com.extenprise.mapp.medico.ui.DialogDismissListener;
import com.extenprise.mapp.medico.ui.SortActionDialog;
import com.extenprise.mapp.medico.util.Utility;

import java.util.ArrayList;
import java.util.Calendar;

public class ViewAppointmentListActivity extends FragmentActivity
        implements ResponseHandler, DialogDismissListener {

    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));
    private ServiceProvider mServiceProv;

    private ArrayList<AppointmentListItem> mUpcomingList;
    private ArrayList<AppointmentListItem> mPastList;

    private ListView mUpcomingListView;
    private ListView mPastListView;
    private TextView mUpcomingMsgView;
    private TextView mPastMsgView;
    private Button mUpcomingSortBtn;
    private Button mPastSortBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointment_list);

        mUpcomingListView = (ListView) findViewById(R.id.upcomingAppontsList);
        mPastListView = (ListView) findViewById(R.id.pastAppontsList);
        mUpcomingMsgView = (TextView) findViewById(R.id.upcomingAppontsMsgView);
        mPastMsgView = (TextView) findViewById(R.id.pastAppontsMsgView);

        mUpcomingSortBtn = (Button) findViewById(R.id.upcomingSortButton);
        mUpcomingSortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSortDialog(true);
            }
        });
        Utility.setEnabledButton(this, mUpcomingSortBtn, false);
        mPastSortBtn = (Button) findViewById(R.id.pastSortButton);
        mPastSortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSortDialog(false);
            }
        });
        Utility.setEnabledButton(this, mPastSortBtn, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mServiceProv = (ServiceProvider) WorkingDataStore.getLoginRef();
        if (mServiceProv == null) {
            Utility.sessionExpired(this);
            return;
        }
        getUpcomingList();
    }

    private void showSortDialog(boolean upcoming) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        SortActionDialog dialog = new SortActionDialog();
        dialog.setSortFieldList(getResources().getStringArray(R.array.appont_sort_field_list));
        dialog.setListener(this);
        dialog.show(fragmentManager, upcoming ? "UpAppontSort" : "PastAppontSort");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return super.onCreateOptionsMenu(menu);
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
        if (id == R.id.logout) {
            Utility.logout(getSharedPreferences("loginPrefs", MODE_PRIVATE), this, LoginActivity.class);
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
            //Utility.showProgress(this, mUpcomingListView, mUpcomingProgress, true);
            Utility.showProgressDialog(this, true);
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
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
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
        //Utility.showProgress(this, mUpcomingListView, mUpcomingProgress, false);
        Utility.showProgressDialog(this, false);
        AppointmentListAdapter adapter = new AppointmentListAdapter(this, 0, mUpcomingList, mServiceProv);
        adapter.setShowDate(true);
        mUpcomingListView.setAdapter(adapter);
        mUpcomingListView.setOnItemClickListener(adapter);
        if (mUpcomingList != null && mUpcomingList.size() > 0) {
            mUpcomingMsgView.setVisibility(View.GONE);
            Utility.setEnabledButton(this, mUpcomingSortBtn, true);
        } else {
            mUpcomingListView.setVisibility(View.GONE);
            mUpcomingMsgView.setVisibility(View.VISIBLE);
            Utility.setEnabledButton(this, mUpcomingSortBtn, false);
        }

        //Utility.showProgress(this, mPastListView, mPastProgress, false);
        if (mPastList == null) {
            mPastList = data.getParcelableArrayList("appontList");
            Bundle bundle = WorkingDataStore.getBundle();
            bundle.putParcelableArrayList("pastList", mPastList);
        }
        adapter = new AppointmentListAdapter(this, 0, mPastList, mServiceProv);
        adapter.setShowDate(true);
        mPastListView.setAdapter(adapter);
        mPastListView.setOnItemClickListener(adapter);
        if (mPastList != null && mPastList.size() > 0) {
            mPastListView.setVisibility(View.VISIBLE);
            mPastMsgView.setVisibility(View.GONE);
            Utility.setEnabledButton(this, mPastSortBtn, true);
        } else {
            mPastListView.setVisibility(View.GONE);
            mPastMsgView.setVisibility(View.VISIBLE);
            Utility.setEnabledButton(this, mPastSortBtn, false);
        }
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        if (action == MappService.DO_UPCOMING_APPONT_LIST) {
            gotUpcomingAppontList(data);
        } else if (action == MappService.DO_PAST_APPONT_LIST) {
            gotPastAppontList(data);
        }
        return data.getBoolean("status");
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        /* Remove the data from WorkingDataStore */
        Bundle bundle = WorkingDataStore.getBundle();
        bundle.remove("upcomingList");
        bundle.remove("pastList");
        return super.getParentActivityIntent();
    }

    @Override
    public void onDialogDismissed(DialogFragment dialog) {
    }

    @Override
    public void onApplyDone(DialogFragment dialog) {
        SortActionDialog sortActionDialog = (SortActionDialog) dialog;
        AppointmentListAdapter adapter;
        if (dialog.getTag().equals("UpAppontSort")) {
            adapter = (AppointmentListAdapter) mUpcomingListView.getAdapter();
        } else {
            adapter = (AppointmentListAdapter) mPastListView.getAdapter();
        }
        adapter.setAscending(sortActionDialog.isAscending());
        adapter.setSortField(sortActionDialog.getSortField());
    }

    @Override
    public void onCancelDone(DialogFragment dialog) {

    }

    @Override
    public void onBackPressed() {
        mConnection.setBound(false);
        //startActivity(getIntent());
        super.onBackPressed();
    }
}
