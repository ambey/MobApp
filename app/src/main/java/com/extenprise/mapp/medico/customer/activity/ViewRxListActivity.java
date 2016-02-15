package com.extenprise.mapp.medico.customer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.customer.data.Customer;
import com.extenprise.mapp.medico.customer.ui.RxListAdapter;
import com.extenprise.mapp.medico.data.WorkingDataStore;
import com.extenprise.mapp.medico.net.MappService;
import com.extenprise.mapp.medico.net.MappServiceConnection;
import com.extenprise.mapp.medico.net.ResponseHandler;
import com.extenprise.mapp.medico.net.ServiceResponseHandler;
import com.extenprise.mapp.medico.service.data.RxInboxItem;
import com.extenprise.mapp.medico.ui.DialogDismissListener;
import com.extenprise.mapp.medico.ui.SortActionDialog;
import com.extenprise.mapp.medico.util.Utility;

import java.util.ArrayList;

//import android.widget.ProgressBar;

public class ViewRxListActivity extends FragmentActivity implements ResponseHandler, DialogDismissListener {

    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));

    private ListView mRxListView;
    //private ProgressBar mRxListProgress;
    private TextView mRxMsgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cust_rx_list);

        Intent intent = getIntent();
        ArrayList<RxInboxItem> rxInboxItems;
        if (savedInstanceState != null) {
            rxInboxItems = savedInstanceState.getParcelableArrayList("inbox");
        } else {
            rxInboxItems = intent.getParcelableArrayListExtra("inbox");
        }
        mRxListView = (ListView) findViewById(R.id.rxListView);
        //mRxListProgress = (ProgressBar) findViewById(R.id.rxListProgress);
        mRxMsgView = (TextView) findViewById(R.id.rxMsgView);

        if (rxInboxItems == null) {
            getRxList();
        } else {
            Customer cust = WorkingDataStore.getBundle().getParcelable("customer");
            RxListAdapter adapter = new RxListAdapter(this, 0, rxInboxItems, cust);
            mRxListView.setAdapter(adapter);
            mRxListView.setOnItemClickListener(adapter);
            Bundle bundle = WorkingDataStore.getBundle();
            adapter.setAscending(bundle.getBoolean("ascending"));
            adapter.setSortField(bundle.getString("sortField"));
        }
    }

    private void getRxList() {
        //Utility.showProgress(this, mRxListView, mRxListProgress, true);
        Utility.showProgressDialog(this, true);
        RxInboxItem item = new RxInboxItem();
        Customer cust = WorkingDataStore.getBundle().getParcelable("customer");
        item.setCustomer(cust);
        Bundle bundle = new Bundle();
        bundle.putParcelable("rxItem", item);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_GET_CUST_RX_LIST);
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
    }

    private void gotRxList(Bundle data) {
        //Utility.showProgress(this, mRxListView, mRxListProgress, false);
        Utility.showProgressDialog(this, false);
        ArrayList<RxInboxItem> list = data.getParcelableArrayList("inbox");
        Customer cust = WorkingDataStore.getBundle().getParcelable("customer");
        RxListAdapter adapter = new RxListAdapter(this, 0, list, cust);
        mRxListView.setAdapter(adapter);
        mRxListView.setOnItemClickListener(adapter);

        if (list != null && list.size() > 0) {
            mRxListView.setVisibility(View.VISIBLE);
            mRxMsgView.setVisibility(View.GONE);
        } else {
            mRxListView.setVisibility(View.GONE);
            mRxMsgView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        if (action == MappService.DO_GET_CUST_RX_LIST) {
            gotRxList(data);
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rx_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_sort:
                showSortDialog();
                break;
            case R.id.action_settings:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSortDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        SortActionDialog dialog = new SortActionDialog();
        dialog.setSortFieldList(getResources().getStringArray(R.array.cust_rx_sort_field_list));
        dialog.setListener(this);
        dialog.show(fragmentManager, "RxListSort");
    }

    @Override
    public void onDialogDismissed(DialogFragment dialog) {

    }

    @Override
    public void onApplyDone(DialogFragment dialog) {
        SortActionDialog sortActionDialog = (SortActionDialog) dialog;
        RxListAdapter adapter = (RxListAdapter) mRxListView.getAdapter();
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
