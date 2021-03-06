package com.extenprise.mapp.medico.service.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.activity.LoginActivity;
import com.extenprise.mapp.medico.customer.data.Customer;
import com.extenprise.mapp.medico.data.Report;
import com.extenprise.mapp.medico.data.ReportServiceStatus;
import com.extenprise.mapp.medico.data.Rx;
import com.extenprise.mapp.medico.data.RxFeedback;
import com.extenprise.mapp.medico.data.WorkingDataStore;
import com.extenprise.mapp.medico.net.MappService;
import com.extenprise.mapp.medico.net.MappServiceConnection;
import com.extenprise.mapp.medico.net.ResponseHandler;
import com.extenprise.mapp.medico.net.ServiceResponseHandler;
import com.extenprise.mapp.medico.service.data.RxInboxItem;
import com.extenprise.mapp.medico.service.data.RxItemAvailability;
import com.extenprise.mapp.medico.service.ui.RxItemListAdapter;
import com.extenprise.mapp.medico.util.ByteArrayToBitmapTask;
import com.extenprise.mapp.medico.util.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;

public class RxInboxItemDetailsActivity extends Activity implements ResponseHandler {
    //ProgressDialog progressDialog;
    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));
    private RxInboxItem mInboxItem;
    private Button mSendAvailButton;
    private BitSet mAvailMap;
    private String mParentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_inbox_item_details);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(R.string.title_activity_rx_feedback);
        }

        Intent intent = getIntent();
        if (savedInstanceState != null) {
            intent.putExtra("feedback", savedInstanceState.getInt("feedback"));
            intent.putExtra("availMap", savedInstanceState.getSerializable("availMap"));
        }
        int feedback = intent.getIntExtra("feedback", RxFeedback.NONE);
        mAvailMap = (BitSet) intent.getSerializableExtra("availMap");
        mParentActivity = intent.getStringExtra("parent-activity");

        Bundle workingData = WorkingDataStore.getBundle();
        ArrayList<RxInboxItem> mInbox = workingData.getParcelableArrayList("inbox");
        mInboxItem = workingData.getParcelable("rxItem");
        if (mInboxItem == null) {
            return;
        }
        Customer customer = workingData.getParcelable("customer");
        if (customer != null) {
            mInboxItem.setCustomer(customer);
        }

        View layoutAppont = findViewById(R.id.layoutAppont);
        layoutAppont.setVisibility(View.GONE);

        TextView servProvNameView;
        TextView servPointView;
        TextView servProvPhoneView;
        TextView statusView;
        TextView dateView;
        TextView custNameView;
        TextView custPhoneView;
        TextView lbl;

        if (feedback == RxFeedback.VIEW_FEEDBACK) {
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
            lbl = (TextView) findViewById(R.id.drNameLblView);

            String category = mInboxItem.getServProv().getCategory();
            if (category != null) {
                if (category.equalsIgnoreCase(getString(R.string.diagnostic_center)) ||
                        category.equalsIgnoreCase(getString(R.string.diagnosticCenter))) {
                    lbl.setText("");
                }
            }
        }

        mSendAvailButton = (Button) findViewById(R.id.buttonSendAvailability);
        Button resendRxButton = (Button) findViewById(R.id.buttonResendRx);
        if (feedback == RxFeedback.VIEW_FEEDBACK) {
            mSendAvailButton.setVisibility(View.GONE);
            if (mInboxItem.getRx() != null &&
                    mInboxItem.getRx().isAllItemsAvailable()) {
                Utility.setEnabledButton(this, resendRxButton, false);
            }
        } else if (feedback == RxFeedback.GIVE_FEEDBACK) {
            resendRxButton.setVisibility(View.GONE);
            if (mInboxItem.getReportService() != null &&
                    mInboxItem.getReportService().getStatus() == ReportServiceStatus.STATUS_FEEDBACK_SENT) {
                Utility.setEnabledButton(this, mSendAvailButton, false);
            }
        } else {
            mSendAvailButton.setVisibility(View.GONE);
            resendRxButton.setVisibility(View.GONE);
        }

        if (feedback != RxFeedback.NONE) {
            int status = RxFeedback.NONE;
            if (mInboxItem.getReportService() != null) {
                status = mInboxItem.getReportService().getStatus();
            }
            if (ReportServiceStatus.STATUS_PENDING == status ||
                    ReportServiceStatus.STATUS_NEW == status) {
                status = ReportServiceStatus.STATUS_INPROCESS;
            }
            statusView.setText(ReportServiceStatus.getStatusString(this, status));
            if (feedback == RxFeedback.VIEW_FEEDBACK) {
                statusView.setVisibility(View.GONE);
            }
        }
        if (mInboxItem.getServProv() != null) {
            servProvNameView.setText(String.format("%s %s.", mInboxItem.getServProv().getLastName().toUpperCase(),
                    mInboxItem.getServProv().getFirstName().substring(0, 1).toUpperCase()));
            servPointView.setText(String.format("%s, %s", mInboxItem.getServProv().getServPtName(),
                    mInboxItem.getServProv().getServPtLocation()));
            servProvPhoneView.setText(String.format("(%s)", mInboxItem.getServProv().getPhone()));
        }
        if (mInboxItem.getCustomer() != null) {
            custNameView.setText(String.format("%s %s.", mInboxItem.getCustomer().getlName().toUpperCase(),
                    mInboxItem.getCustomer().getfName().substring(0, 1).toUpperCase()));
            custPhoneView.setText(mInboxItem.getCustomer().getSignInData().getPhone());
        }
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        if (mInboxItem.getRx() != null) {
            dateView.setText(sdf.format(mInboxItem.getRx().getDate()));
        }

        ListView listView = (ListView) findViewById(R.id.listRxItems);
        Rx rx = mInboxItem.getRx();
        if (rx != null && rx.getItems().size() == 0) {
            mSendAvailButton.setVisibility(View.GONE);
            resendRxButton.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            mConnection.setAction(MappService.DO_GET_RX_SCANNED_COPY);
            Bundle bundle = new Bundle();
            bundle.putInt("idRx", rx.getIdReport());
            Log.d("", " ID Report ***** " + rx.getIdReport());
            mConnection.setData(bundle);
            if (Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE)) {
                Utility.showProgressDialog(this, true);
            }
            return;
        }
        int fb = RxFeedback.NONE;
        if (feedback == RxFeedback.VIEW_FEEDBACK) {
            fb = RxFeedback.VIEW_FEEDBACK;
        } else if (feedback == RxFeedback.GIVE_FEEDBACK) {
            fb = RxFeedback.GIVE_FEEDBACK;
        }
        RxItemListAdapter adapter = new RxItemListAdapter(this, 0, mInbox, mInboxItem, fb);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Intent intent = getIntent();
        outState.putInt("feedback", intent.getIntExtra("feedback", RxFeedback.NONE));
        outState.putSerializable("availMap", intent.getSerializableExtra("availMap"));
    }

    public BitSet getAvailMap() {
        return mAvailMap;
    }

    public void setAvailabilityChanged(boolean isChanged) {
        /* Change the state of the button only if the feedback was sent previously */
        if (mInboxItem.getReportService().getStatus() == ReportServiceStatus.STATUS_FEEDBACK_SENT) {
            Utility.setEnabledButton(this, mSendAvailButton, isChanged);
        }
    }

    public void sendAvailabilityFeedback(View view) {
        Log.v("RxInboxDetails", "Send Availability called");
        /* disable the button */
        mSendAvailButton = (Button) findViewById(R.id.buttonSendAvailability);
        Utility.setEnabledButton(this, mSendAvailButton, false);

        RxItemAvailability availability = new RxItemAvailability();
        availability.setIdServProvHasServPt(mInboxItem.getReportService().getIdServProvHasServPt());
        availability.setIdRx(mInboxItem.getReportService().getIdReport());
        availability.setStatus(ReportServiceStatus.STATUS_FEEDBACK_SENT);
        availability.setReceivedDate(mInboxItem.getReportService().getReceivedDate());
        availability.setAvailableList(mInboxItem.getRx().getItems());

        Bundle data = new Bundle();
        data.putParcelable("form", availability);
        mConnection.setData(data);
        mConnection.setAction(MappService.DO_SEND_AVAILABILITY);
        if (Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE)) {
            //progressDialog = ProgressDialog.show(this, "", getString(R.string.msg_please_wait), true);
            Utility.showProgressDialog(this, true);
        }
    }

    public void resendRx(View view) {
        Intent intent = new Intent(this, SelectMedicalStoreActivity.class);
        intent.putExtra("parent-activity", getClass().getName());
        intent.putExtra("origin_activity", getIntent().getStringExtra("origin_activity"));
        intent.putExtra("rx", mInboxItem.getRx());
        //intent.putExtra("feedback", intent.getIntExtra("feedback", RxFeedback.NONE));
        intent.putExtra("feedback", RxFeedback.VIEW_FEEDBACK);
        intent.putExtra("appont", "");
        intent.putExtra("servProv", "");
        startActivity(intent);
        /*Intent intent = getParentActivityIntent();
        if (intent == null) {
            return;
        }
        startActivity(intent);*/
    }

    private void sentAvailabilityFeedback() {
        mInboxItem.getReportService().setStatus(ReportServiceStatus.STATUS_FEEDBACK_SENT);
        Utility.showAlert(this, "", getString(R.string.msg_availablity_sent), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(RxInboxItemDetailsActivity.this, MedicalStoreHomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void gotRxScannedCopy(Bundle data) {
        if (!data.getBoolean("status")) {
            return;
        }
        ImageView imageView = (ImageView) findViewById(R.id.rxCopyImageView);
        TextView msgView = (TextView) findViewById(R.id.viewMsgNoItems);
        Report report = data.getParcelable("report");
        if (report == null) {
            msgView.setVisibility(View.VISIBLE);
            return;
        }
        byte[] pix = report.getScannedCopy();
        if (pix != null) {
            try {
                ByteArrayToBitmapTask task = new ByteArrayToBitmapTask(imageView, pix,
                        imageView.getMeasuredWidth(), imageView.getMeasuredHeight());
                task.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
            /*imageView.setImageBitmap(Utility.getBitmapFromBytes(pix,
                    imageView.getLayoutParams().width, imageView.getLayoutParams().height));

                    ArithmeticException: divide by zero :  at
                    Utility.getBitmapFromBytes(Utility.java:406)
                    */
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        Intent intent = super.getParentActivityIntent();
        if (mParentActivity != null) {
            try {
                intent = new Intent(this, Class.forName(mParentActivity));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                intent = super.getParentActivityIntent();
            }
        }

        if (intent != null) {
            intent.putExtra("feedback", getIntent().getIntExtra("feedback", RxFeedback.NONE));
            //intent.putExtra("origin_activity", getIntent().getStringExtra("origin_activity"));
            intent.putExtra("parent-activity", getIntent().getStringExtra("origin_activity"));
        }
        return intent;
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        Utility.showProgressDialog(this, false);
        if (action == MappService.DO_SEND_AVAILABILITY) {
            sentAvailabilityFeedback();
        } else if (action == MappService.DO_GET_RX_SCANNED_COPY) {
            gotRxScannedCopy(data);
        }
        return data.getBoolean("status");
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
            WorkingDataStore.getBundle().remove("servProv");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        mConnection.setBound(false);
        Intent intent = getParentActivityIntent();
        if (intent != null) {
            startActivity(intent);
            return;
        }
        super.onBackPressed();
    }
}
