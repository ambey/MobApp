package com.extenprise.mapp.service.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.customer.data.Customer;
import com.extenprise.mapp.data.Report;
import com.extenprise.mapp.data.ReportServiceStatus;
import com.extenprise.mapp.data.Rx;
import com.extenprise.mapp.data.RxFeedback;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.MappServiceConnection;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.data.ReportService;
import com.extenprise.mapp.service.data.RxInboxItem;
import com.extenprise.mapp.service.data.RxItemAvailability;
import com.extenprise.mapp.service.ui.RxItemListAdapter;
import com.extenprise.mapp.util.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class RxInboxItemDetailsActivity extends Activity implements ResponseHandler {
    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));
    private ArrayList<RxInboxItem> mInbox;
    private RxInboxItem mInboxItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_inbox_item_details);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        mInbox = intent.getParcelableArrayListExtra("inbox");
        int position = intent.getIntExtra("position", 0);
        mInboxItem = mInbox.get(position);
        Customer customer = intent.getParcelableExtra("customer");
        if(customer != null) {
            mInboxItem.setCustomer(customer);
        }
        int feedback = intent.getIntExtra("feedback", RxFeedback.NONE.ordinal());

        View layoutAppont = findViewById(R.id.layoutAppont);
        layoutAppont.setVisibility(View.GONE);

        TextView servProvNameView;
        TextView servPointView;
        TextView servProvPhoneView;
        TextView statusView;
        TextView dateView;
        TextView custNameView;
        TextView custPhoneView;

        if (feedback == RxFeedback.VIEW_FEEDBACK.ordinal()) {
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
        if (feedback == RxFeedback.VIEW_FEEDBACK.ordinal()) {
            sendAvailabilityButton.setVisibility(View.GONE);
            if (mInboxItem.getRx().isAllItemsAvailable()) {
                Utility.setEnabledButton(this, resendRxButton, false);
            }
        } else if (feedback == RxFeedback.GIVE_FEEDBACK.ordinal()) {
            resendRxButton.setVisibility(View.GONE);
            if(mInboxItem.getReportService().getStatus() == ReportServiceStatus.STATUS_FEEDBACK_SENT.ordinal()) {
                sendAvailabilityButton.setEnabled(false);
            }
        } else {
            sendAvailabilityButton.setVisibility(View.GONE);
            resendRxButton.setVisibility(View.GONE);
        }

        if (feedback != RxFeedback.NONE.ordinal()) {
            int status = mInboxItem.getReportService().getStatus();
            if (ReportServiceStatus.STATUS_PENDING.ordinal() == status ||
                    ReportServiceStatus.STATUS_NEW.ordinal() == status) {
                status = ReportServiceStatus.STATUS_INPROCESS.ordinal();
            }
            statusView.setText(ReportServiceStatus.getStatusString(this, status));
            if (feedback == RxFeedback.VIEW_FEEDBACK.ordinal()) {
                statusView.setVisibility(View.GONE);
            }
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
        Rx rx = mInbox.get(position).getRx();
        if (rx.getItems().size() == 0) {
            sendAvailabilityButton.setVisibility(View.GONE);
            resendRxButton.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            mConnection.setAction(MappService.DO_GET_RX_SCANNED_COPY);
            Bundle bundle = new Bundle();
            bundle.putInt("idRx", rx.getIdReport());
            mConnection.setData(bundle);
            Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
            return;
        }
        RxFeedback fb = RxFeedback.NONE;
        if (feedback == RxFeedback.VIEW_FEEDBACK.ordinal()) {
            fb = RxFeedback.VIEW_FEEDBACK;
        } else if (feedback == RxFeedback.GIVE_FEEDBACK.ordinal()) {
            fb = RxFeedback.GIVE_FEEDBACK;
        }
        RxItemListAdapter adapter = new RxItemListAdapter(this, 0, mInbox, position, fb);
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
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
    }

    public void resendRx(View view) {
        Intent intent = getParentActivityIntent();
        if (intent == null) {
            return;
        }
        startActivity(intent);
    }

    private void sentAvailabilityFeedback() {
        //Utility.showMessage(this, R.string.msg_availablity_sent);
        mInboxItem.getReportService().setStatus(ReportServiceStatus.STATUS_FEEDBACK_SENT.ordinal());
        Utility.showAlert(this, "", getString(R.string.msg_availablity_sent), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(RxInboxItemDetailsActivity.this, MedicalStoreHomeActivity.class);
                startActivity(intent);
            }
        });
        //Intent intent = getParentActivityIntent();
    }

    private void gotRxScannedCopy(Bundle data) {
        ImageView imageView = (ImageView) findViewById(R.id.rxCopyImageView);
        Report report = data.getParcelable("report");
        if (report == null) {
            return;
        }
        byte[] pix = report.getScannedCopy();
        Bitmap bitmap = BitmapFactory.decodeByteArray(pix, 0, pix.length);
        imageView.setImageBitmap(bitmap);
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        Intent intent = super.getParentActivityIntent();
        if (intent == null) {
            String parentActivityClass = getIntent().getStringExtra("parent-activity");
            if(parentActivityClass != null) {
                try {
                    intent = new Intent(this, Class.forName(parentActivityClass));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if(intent == null) {
                return null;
            }
        }
        ReportService service = mInboxItem.getReportService();
        if(service != null) {
            if (service.getStatus() == ReportServiceStatus.STATUS_NEW.ordinal()) {
                service.setStatus(ReportServiceStatus.STATUS_PENDING.ordinal());
            }
            Bundle bundle = new Bundle();
            bundle.putParcelable("report_service", service);
            mConnection.setData(bundle);
            mConnection.setAction(MappService.DO_UPDATE_REPORT_STATUS);
            Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);

        }
        intent.putParcelableArrayListExtra("inbox", mInbox);
        intent.putExtra("feedback", getIntent().getIntExtra("feedback", RxFeedback.NONE.ordinal()));
        intent.putExtra("customer", getIntent().getParcelableExtra("customer"));
        intent.putExtra("parent-activity", getIntent().getStringExtra("origin_activity"));
        return intent;
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        if (action == MappService.DO_SEND_AVAILABILITY) {
            sentAvailabilityFeedback();
            return true;
        } else if (action == MappService.DO_GET_RX_SCANNED_COPY) {
            gotRxScannedCopy(data);
            return true;
        }
        return false;
    }
}
