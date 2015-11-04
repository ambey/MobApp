package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.data.ReportServiceStatus;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.MappServiceConnection;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.data.RxInboxItem;
import com.extenprise.mapp.service.data.RxItemAvailability;
import com.extenprise.mapp.service.ui.RxItemListAdapter;

import java.text.SimpleDateFormat;

public class RxInboxItemDetailsActivity extends Activity implements ResponseHandler {
    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this));
    private RxInboxItem mInboxItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_inbox_item_details);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mInboxItem = intent.getParcelableExtra("inboxItem");

        View layoutAppont = findViewById(R.id.layoutAppont);
        layoutAppont.setVisibility(View.INVISIBLE);

        TextView statusView = (TextView) findViewById(R.id.statusView);
        TextView dateView = (TextView) findViewById(R.id.dateTextView);
        TextView custNameView = (TextView) findViewById(R.id.custNameView);
        TextView custPhoneView = (TextView) findViewById(R.id.custPhoneView);
        TextView drNameView = (TextView) findViewById(R.id.drNameView);
        TextView drClinicView = (TextView) findViewById(R.id.drClinicView);
        TextView drPhoneView = (TextView) findViewById(R.id.drPhoneView);

        int status = mInboxItem.getReportService().getStatus();
        if (ReportServiceStatus.STATUS_PENDING.ordinal() == status) {
            status = ReportServiceStatus.STATUS_INPROCESS.ordinal();
        }
        statusView.setText(ReportServiceStatus.getStatusString(this, status));

        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        dateView.setText(sdf.format(mInboxItem.getRx().getDate()));
        drNameView.setText(String.format("%s %s.", mInboxItem.getServProv().getLastName().toUpperCase(),
                mInboxItem.getServProv().getFirstName().substring(0, 1).toUpperCase()));
        custNameView.setText(String.format("%s %s.", mInboxItem.getCustomer().getlName().toUpperCase(),
                mInboxItem.getCustomer().getfName().substring(0, 1).toUpperCase()));
        custPhoneView.setText(mInboxItem.getCustomer().getSignInData().getPhone());
        drClinicView.setText(String.format("%s, %s", mInboxItem.getServProv().getServPtName(),
                mInboxItem.getServProv().getServPtLocation()));
        drPhoneView.setText(String.format("(%s)", mInboxItem.getServProv().getPhone()));

        ListView listView = (ListView) findViewById(R.id.listRxItems);
        RxItemListAdapter adapter = new RxItemListAdapter(this, 0, mInboxItem.getRx());
        listView.setAdapter(adapter);

    }

    public void sendAvailabilityFeedback(View view) {
        Log.v("RxInboxDetails", "Send Availability called");

        RxItemAvailability availability = new RxItemAvailability();
        availability.setIdServProvHasServPt(mInboxItem.getReportService().getIdServProvHasServPt());
        availability.setIdRx(mInboxItem.getReportService().getIdReport());
        availability.setReceivedDate(mInboxItem.getReportService().getReceivedDate());
        availability.setAvailableList(mInboxItem.getRx().getItems());

        Bundle data = new Bundle();
        data.putParcelable("form", availability);
        mConnection.setData(data);
        mConnection.setAction(MappService.DO_SEND_AVAILABILITY);

        Intent intent = new Intent(this, MappService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    private void sentAvailabilityFeedback() {
        Intent intent = getParentActivityIntent();
        startActivity(intent);
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        Intent intent = super.getParentActivityIntent();
        if (intent == null) {
            return null;
        }
        intent.putParcelableArrayListExtra("inbox", getIntent().getParcelableArrayListExtra("inbox"));
        return intent;
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        if(action == MappService.DO_SEND_AVAILABILITY) {
            sentAvailabilityFeedback();
            return true;
        }
        return false;
    }
}
