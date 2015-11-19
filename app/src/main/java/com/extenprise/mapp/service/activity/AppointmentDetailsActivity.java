package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.customer.activity.PatientHistoryActivity;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.MappServiceConnection;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.data.AppointmentListItem;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class AppointmentDetailsActivity extends Activity implements ResponseHandler {

    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));

    private AppointmentListItem mAppont;
    private ServiceProvider mServProv;
    private ArrayList<AppointmentListItem> mPastApponts;
    private Button mConfirmAppontButton;
    private Button mCancelAppontButton;
    private TextView mStatusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_details);

        View view = findViewById(R.id.appointmentLayout);
        TextView fNameView = (TextView) view.findViewById(R.id.patientFNameTextView);
        TextView lNameView = (TextView) view.findViewById(R.id.patientLNameTextView);
        TextView timeView = (TextView) view.findViewById(R.id.appointmentTimeTextView);
        TextView genderView = (TextView) view.findViewById(R.id.patientGenderTextView);
        TextView ageView = (TextView) view.findViewById(R.id.patientAgeTextView);
        TextView wtView = (TextView) view.findViewById(R.id.patientWeightTextView);
        TextView dateView = (TextView) findViewById(R.id.appointmentDateTextView);
        Button rxButton = (Button) findViewById(R.id.rXButton);
        Button uploadRxButton = (Button) findViewById(R.id.uploadScanRxButton);
        mConfirmAppontButton = (Button) findViewById(R.id.confirmButton);
        mCancelAppontButton = (Button) findViewById(R.id.cancelButton);
        mStatusView = (TextView) findViewById(R.id.statusTextView);

        Intent intent = getIntent();
        mAppont = intent.getParcelableExtra("appont");

        mServProv = LoginHolder.servLoginRef;

        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        Date date = mAppont.getDate();
        int fromTime = Utility.getMinutes(mAppont.getTime());
        date.setTime(date.getTime() + fromTime * 60 * 1000);
        if (date.after(today)) {
            //Utility.setEnabledButton(this, rxButton, false);
            Utility.setEnabledButton(this, uploadRxButton, false);
        } else if (date.before(today)) {
            int day = cal.get(Calendar.DAY_OF_MONTH);
            cal.setTime(date);
            int apptDay = cal.get(Calendar.DAY_OF_MONTH);
            if (apptDay != day) {
                //Utility.setEnabledButton(this, rxButton, false);
                Utility.setEnabledButton(this, uploadRxButton, true);
            } else {
                Utility.setEnabledButton(this, rxButton, true);
                Utility.setEnabledButton(this, uploadRxButton, true);
            }
        } else {
            Utility.setEnabledButton(this, rxButton, false);
            Utility.setEnabledButton(this, uploadRxButton, false);
        }

        if (mAppont.isConfirmed()) {
            mStatusView.setText(getString(R.string.confirmed));
        } else if (mAppont.isCanceled()) {
            mStatusView.setText(getString(R.string.canceled));
        } else {
            mStatusView.setText(getString(R.string.not_confirmed));
        }

        if (mAppont.isConfirmed() || mAppont.isCanceled()) {
            Utility.setEnabledButton(this, mConfirmAppontButton, false);
            Utility.setEnabledButton(this, mCancelAppontButton, false);
        }

        dateView.setText(sdf.format(date));
        fNameView.setText(mAppont.getFirstName());
        lNameView.setText(mAppont.getLastName());
        timeView.setText(mAppont.getTime());
        genderView.setText(mAppont.getGender());
        ageView.setText(String.format("%d", mAppont.getAge()));
        wtView.setText(String.format("%.1f", mAppont.getWeight()));

        fillPastAppointements();
    }

    private void statusChangeDone(String msg) {
        Utility.setEnabledButton(this, mConfirmAppontButton, false);
        Utility.setEnabledButton(this, mCancelAppontButton, false);
        Utility.showAlert(this, "", msg);
    }

    private void gotPastAppointments(Bundle data) {
        TextView msgView = (TextView) findViewById(R.id.viewMsg);
        msgView.setVisibility(View.GONE);
        mPastApponts = data.getParcelableArrayList("appontList");
        View pastAppontLayout = findViewById(R.id.pastAppointmentLayout);
        Button viewMoreButton = (Button) findViewById(R.id.viewMoreButton);
        if (viewMoreButton == null) {
            viewMoreButton = (Button) pastAppontLayout.findViewById(R.id.viewMoreButton);
        }
        Button viewRxButton = (Button) pastAppontLayout.findViewById(R.id.viewRxButton);
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        if (mPastApponts != null && mPastApponts.size() > 0) {
            AppointmentListItem lastAppont = mPastApponts.get(mPastApponts.size() - 1);
            TextView dateOthView = (TextView) pastAppontLayout.findViewById(R.id.dateTextView);
            dateOthView.setText(sdf.format(lastAppont.getDate()));
            Utility.setEnabledButton(this, viewRxButton, true, R.color.LinkColor);
            if (mPastApponts.size() > 1) {
                Utility.setEnabledButton(this, viewMoreButton, true, R.color.LinkColor);
            }
            pastAppontLayout.setVisibility(View.VISIBLE);
        } else {
            pastAppontLayout.setVisibility(View.INVISIBLE);
            msgView.setText(R.string.no_past_appont);
            msgView.setVisibility(View.VISIBLE);
        }
    }

    private void fillPastAppointements() {
        View pastAppontLayout = findViewById(R.id.pastAppointmentLayout);
        pastAppontLayout.setVisibility(View.GONE);
        TextView msgView = (TextView) findViewById(R.id.viewMsg);
        msgView.setText(R.string.loading_page);
        msgView.setVisibility(View.VISIBLE);
        doAppontAction(MappService.DO_PAST_APPONT_LIST);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_appointment_details, menu);
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

    private void doAppontAction(int action) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("form", mAppont);
        mConnection.setData(bundle);
        mConnection.setAction(action);
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
    }

    public void confirmAppointment(View view) {
        doAppontAction(MappService.DO_CONFIRM_APPONT);
    }

    public void cancelAppointment(View view) {
        doAppontAction(MappService.DO_CANCEL_APPONT);
    }

    public void showRxActivity(View view) {
        Intent intent = new Intent(this, RxActivity.class);
        intent.putExtra("parent-activity", getClass().getName());
        intent.putExtra("appont", mAppont);
        intent.putExtra("service", mServProv);
        startActivity(intent);
    }

    public void showRxDetails(View view) {
        Intent intent = new Intent(this, ViewRxActivity.class);
        intent.putExtra("parent-activity", getClass().getName());
        intent.putExtra("appont", mAppont);
        intent.putExtra("pastAppont", mPastApponts.get(mPastApponts.size() - 1));
        startActivity(intent);
    }

    public void showPatientHistory(View view) {
        Intent intent = new Intent(this, PatientHistoryActivity.class);
        intent.putExtra("parent-activity", getClass().getName());
        intent.putExtra("appont", mAppont);
        intent.putParcelableArrayListExtra("appontList", mPastApponts);
        startActivity(intent);
    }

    public void showScannedRxScreen(View view) {
        Intent intent = new Intent(this, ScannedRxActivity.class);
        intent.putExtra("appont", mAppont);
        startActivity(intent);
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        if (action == MappService.DO_PAST_APPONT_LIST) {
            gotPastAppointments(data);
            return true;
        } else if (action == MappService.DO_CONFIRM_APPONT) {
            mAppont.setConfirmed(true);
            mStatusView.setText(getString(R.string.confirmed));
            statusChangeDone(getString(R.string.msg_appont_confirmed));
            return true;
        } else if (action == MappService.DO_CANCEL_APPONT) {
            mAppont.setCanceled(true);
            mStatusView.setText(getString(R.string.canceled));
            statusChangeDone(getString(R.string.msg_appont_canceled));
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        Intent intent = super.getParentActivityIntent();
        if (intent != null) {
            intent.putExtra("service", mServProv);
        }
        return intent;
    }
}
