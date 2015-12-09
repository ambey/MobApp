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
import com.extenprise.mapp.customer.ui.RxListAdapter;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.MappServiceConnection;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.data.RxInboxItem;
import com.extenprise.mapp.util.Utility;

import java.util.ArrayList;

public class ViewRxListActivity extends Activity implements ResponseHandler {

    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));

    private ListView mRxListView;
    private ProgressBar mRxListProgress;
    private TextView mRxMsgView;

    private Customer mCust;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cust_rx_list);

        Intent intent = getIntent();
        mCust = intent.getParcelableExtra("customer");
        ArrayList<RxInboxItem> rxInboxItems = intent.getParcelableArrayListExtra("inbox");

        mRxListView = (ListView) findViewById(R.id.rxListView);
        mRxListProgress = (ProgressBar) findViewById(R.id.rxListProgress);
        mRxMsgView = (TextView) findViewById(R.id.rxMsgView);

        if(rxInboxItems == null) {
            getRxList();
        } else {
            RxListAdapter adapter = new RxListAdapter(this, 0, rxInboxItems, mCust);
            mRxListView.setAdapter(adapter);
            mRxListView.setOnItemClickListener(adapter);
        }
    }

    private void getRxList() {
        Utility.showProgress(this, mRxListView, mRxListProgress, true);
        RxInboxItem item = new RxInboxItem();
        Customer c = new Customer();
        c.setIdCustomer(mCust.getIdCustomer());
        item.setCustomer(c);
        Bundle bundle = new Bundle();
        bundle.putParcelable("rxItem", item);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_GET_CUST_RX_LIST);
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
    }

    private void gotRxList(Bundle data) {
        Utility.showProgress(this, mRxListView, mRxListProgress, false);
        ArrayList<RxInboxItem> list = data.getParcelableArrayList("inbox");
        RxListAdapter adapter = new RxListAdapter(this, 0, list, mCust);
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
}
