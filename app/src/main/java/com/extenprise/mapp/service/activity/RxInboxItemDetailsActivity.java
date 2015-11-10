package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.data.ReportServiceStatus;
import com.extenprise.mapp.data.RxFeedback;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.MappServiceConnection;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.data.RxInboxItem;
import com.extenprise.mapp.service.data.RxItemAvailability;
import com.extenprise.mapp.service.ui.RxItemListAdapter;
import com.extenprise.mapp.util.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class RxInboxItemDetailsActivity extends Activity implements ResponseHandler {
    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this));
    private ArrayList<RxInboxItem> mInbox;
    private RxInboxItem mInboxItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_inbox_item_details);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mInbox = intent.getParcelableArrayListExtra("inbox");
        int position = intent.getIntExtra("position", 0);
        mInboxItem = mInbox.get(position);
        boolean feedback = intent.getBooleanExtra("feedback", false);

        View layoutAppont = findViewById(R.id.layoutAppont);
        layoutAppont.setVisibility(View.GONE);

        TextView servProvNameView;
        TextView servPointView;
        TextView servProvPhoneView;
        TextView statusView;
        TextView dateView;
        TextView custNameView;
        TextView custPhoneView;

        if (feedback) {
            View layoutRxHead = findViewById(R.id.layoutRxHead);
            layoutRxHead.setVisibility(View.GONE);

            statusView = (TextView) findViewById(R.id.feedBackStatusView);
            dateView = (TextView) findViewById(R.id.feedbackDateView);
            custNameView = (TextView) findViewById(R.id.feedbackCustNameView);
            custPhoneView = (TextView) findViewById(R.id.feedbackCustPhoneView);
            servProvNameView = (TextView) findViewById(R.id.medStoreProvView);
            servPointView = (TextView) findViewById(R.id.medStoreNameView);
            servProvPhoneView = (TextView) findViewById(R.id.medStoreProvPhoneView);
        } else {
            View layoutRxFeedback = findViewById(R.id.layoutRxFeedbackHead);
            layoutRxFeedback.setVisibility(View.GONE);

            statusView = (TextView) findViewById(R.id.statusView);
            dateView = (TextView) findViewById(R.id.dateTextView);
            custNameView = (TextView) findViewById(R.id.custNameView);
            custPhoneView = (TextView) findViewById(R.id.custPhoneView);
            servProvNameView = (TextView) findViewById(R.id.drNameView);
            servPointView = (TextView) findViewById(R.id.drClinicView);
            servProvPhoneView = (TextView) findViewById(R.id.drPhoneView);
        }

        Button sendAvailabilityButton = (Button) findViewById(R.id.buttonSendAvailability);
        Button resendRxButton = (Button) findViewById(R.id.buttonResendRx);
        if (feedback) {
            sendAvailabilityButton.setVisibility(View.GONE);
            if (!mInboxItem.getRx().isAllItemsAvailable()) {
                Utility.setEnabledButton(this, resendRxButton, false);
            }
        } else {
            resendRxButton.setVisibility(View.GONE);
        }
        int status = mInboxItem.getReportService().getStatus();
        if (ReportServiceStatus.STATUS_PENDING.ordinal() == status ||
                ReportServiceStatus.STATUS_NEW.ordinal() == status) {
            status = ReportServiceStatus.STATUS_INPROCESS.ordinal();
        }
        statusView.setText(ReportServiceStatus.getStatusString(this, status));
        if (feedback) {
            statusView.setVisibility(View.GONE);
        }
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        dateView.setText(sdf.format(mInboxItem.getRx().getDate()));
        servProvNameView.setText(String.format("%s %s.", mInboxItem.getServProv().getLastName().toUpperCase(),
                mInboxItem.getServProv().getFirstName().substring(0, 1).toUpperCase()));
        custNameView.setText(String.format("%s %s.", mInboxItem.getCustomer().getlName().toUpperCase(),
                mInboxItem.getCustomer().getfName().substring(0, 1).toUpperCase()));
        custPhoneView.setText(mInboxItem.getCustomer().getSignInData().getPhone());
        servPointView.setText(String.format("%s, %s", mInboxItem.getServProv().getServPtName(),
                mInboxItem.getServProv().getServPtLocation()));
        servProvPhoneView.setText(String.format("(%s)", mInboxItem.getServProv().getPhone()));

        ListView listView = (ListView) findViewById(R.id.listRxItems);
        RxItemListAdapter adapter = new RxItemListAdapter(this, 0, mInbox, position,
                feedback ? RxFeedback.VIEW_FEEDBACK : RxFeedback.GIVE_FEEDBACK);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(adapter);
    }

    public void sendAvailabilityFeedback(View view) {
        Log.v("RxInboxDetails", "Send Availability called");

        RxItemAvailability availability = new RxItemAvailability();
        availability.setIdServProvHasServPt(mInboxItem.getReportService().getIdServProvHasServPt());
        availability.setIdRx(mInboxItem.getReportService().getIdReport());
        availability.setStatus(ReportServiceStatus.STATUS_FEEDBACK_SENT.ordinal());
        availability.setReceivedDate(mInboxItem.getReportService().getReceivedDate());
        availability.setAvailableList(mInboxItem.getRx().getItems());

        Bundle data = new Bundle();
        data.putParcelable("form", availability);
        mConnection.setData(data);
        mConnection.setAction(MappService.DO_SEND_AVAILABILITY);

        Intent intent = new Intent(this, MappService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    public void resendRx(View view) {
        Intent intent = getParentActivityIntent();
        if(intent == null) {
            return;
        }
        startActivity(intent);
    }

    private void sentAvailabilityFeedback() {
        mInboxItem.getReportService().setStatus(ReportServiceStatus.STATUS_FEEDBACK_SENT.ordinal());
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
        if (mInboxItem.getReportService().getStatus() == ReportServiceStatus.STATUS_NEW.ordinal()) {
            mInboxItem.getReportService().setStatus(ReportServiceStatus.STATUS_PENDING.ordinal());
        }
        intent.putParcelableArrayListExtra("inbox", mInbox);
        intent.putExtra("feedback", getIntent().getBooleanExtra("feedback", false));
        return intent;
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        if (action == MappService.DO_SEND_AVAILABILITY) {
            sentAvailabilityFeedback();
            return true;
        }
        return false;
    }
}
