package com.extenprise.mapp.medico.customer.activity;

import android.os.Bundle;
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
import com.extenprise.mapp.medico.customer.data.AppointmentListItem;
import com.extenprise.mapp.medico.customer.data.Customer;
import com.extenprise.mapp.medico.customer.ui.AppointmentListAdapter;
import com.extenprise.mapp.medico.data.WorkingDataStore;
import com.extenprise.mapp.medico.net.MappService;
import com.extenprise.mapp.medico.net.MappServiceConnection;
import com.extenprise.mapp.medico.net.ResponseHandler;
import com.extenprise.mapp.medico.net.ServiceResponseHandler;
import com.extenprise.mapp.medico.ui.DialogDismissListener;
import com.extenprise.mapp.medico.ui.SortActionDialog;
import com.extenprise.mapp.medico.util.Utility;

import java.util.ArrayList;
import java.util.Calendar;

public class ViewAppointmentListActivity extends FragmentActivity implements ResponseHandler, DialogDismissListener {

    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));

    private ListView mUpcomingListView;
    private ListView mPastListView;
    private ArrayList<AppointmentListItem> mUpcomingList;
    private TextView mPastMsgView, mUpcomMsgView;

    /*private ProgressBar mUpcomingProgress;
    private ProgressBar mPastProgress;*/
    private Button mUpcomingSortBtn;
    private Button mPastSortBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointment_list);

        mUpcomingListView = (ListView) findViewById(R.id.upcomingAppontsList);
        mPastListView = (ListView) findViewById(R.id.pastAppontsList);
        /*mUpcomingProgress = (ProgressBar) findViewById(R.id.upcomingProgress);
        mPastProgress = (ProgressBar) findViewById(R.id.pastProgress);*/
        mPastMsgView = (TextView) findViewById(R.id.pastAppontsMsgView);
        mUpcomMsgView = (TextView) findViewById(R.id.upcomingAppontsMsgView);

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

        getUpcomingList();
    }

    private void showSortDialog(boolean upcoming) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        SortActionDialog dialog = new SortActionDialog();
        dialog.setSortFieldList(getResources().getStringArray(R.array.appont_sort_field_list));
        dialog.setListener(this);
        dialog.show(fragmentManager, upcoming ? "UpAppontSort" : "PastAppontSort");
    }

    private void setupForm(AppointmentListItem form) {
        Customer customer = WorkingDataStore.getBundle().getParcelable("customer");
        form.setIdCustomer(customer.getIdCustomer());
        Calendar cal = Calendar.getInstance();
        form.setDate(cal.getTime());
        form.setTime(String.format("%d:%d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));
    }

    private void getUpcomingList() {
        AppointmentListItem form = new AppointmentListItem();
        setupForm(form);
        Bundle bundle = new Bundle();
        bundle.putParcelable("form", form);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_GET_CUST_UPCOMING_APPONTS);
        if (Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE)) {
            //Utility.showProgress(this, mUpcomingListView, mUpcomingProgress, true);
            Utility.showProgressDialog(this, true);
        }
    }

    private void getPastList() {
        AppointmentListItem form = new AppointmentListItem();
        setupForm(form);
        Bundle bundle = new Bundle();
        bundle.putParcelable("form", form);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_GET_CUST_PAST_APPONTS);
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
    }

    private void gotUpcomingApponts(Bundle data) {
        mUpcomingList = data.getParcelableArrayList("appontList");
        getPastList();
    }

    private void gotPastApponts(Bundle data) {
        //Utility.showProgress(this, mUpcomingListView, mUpcomingProgress, false);
        Utility.showProgressDialog(this, false);
        AppointmentListAdapter adapter = new AppointmentListAdapter(this, 0, mUpcomingList);
        mUpcomingListView.setAdapter(adapter);
        if (mUpcomingList != null && mUpcomingList.size() > 0) {
            mUpcomMsgView.setVisibility(View.GONE);
            mUpcomingListView.setVisibility(View.VISIBLE);
            Utility.setEnabledButton(this, mUpcomingSortBtn, true);
        } else {
            mUpcomingListView.setVisibility(View.GONE);
            mUpcomMsgView.setVisibility(View.VISIBLE);
            Utility.setEnabledButton(this, mUpcomingSortBtn, false);
        }

        //Utility.showProgress(this, mPastListView, mPastProgress, false);
        ArrayList<AppointmentListItem> pastList = data.getParcelableArrayList("appontList");
        adapter = new AppointmentListAdapter(this, 0, pastList);
        mPastListView.setAdapter(adapter);

        if (pastList != null && pastList.size() > 0) {
            mPastMsgView.setVisibility(View.GONE);
            mPastListView.setVisibility(View.VISIBLE);
            Utility.setEnabledButton(this, mPastSortBtn, true);
        } else {
            mPastListView.setVisibility(View.GONE);
            mPastMsgView.setVisibility(View.VISIBLE);
            Utility.setEnabledButton(this, mPastSortBtn, false);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_appointment_list, menu);
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        mConnection.setBound(false);
        //startActivity(getIntent());
        super.onBackPressed();
    }
}
