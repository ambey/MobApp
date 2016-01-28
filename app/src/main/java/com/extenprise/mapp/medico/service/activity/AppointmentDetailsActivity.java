package com.extenprise.mapp.medico.service.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.extenprise.mapp.medico.LoginHolder;
import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.customer.activity.PatientHistoryActivity;
import com.extenprise.mapp.medico.data.WorkingDataStore;
import com.extenprise.mapp.medico.net.MappService;
import com.extenprise.mapp.medico.net.MappServiceConnection;
import com.extenprise.mapp.medico.net.ResponseHandler;
import com.extenprise.mapp.medico.net.ServiceResponseHandler;
import com.extenprise.mapp.medico.service.data.AppointmentListItem;
import com.extenprise.mapp.medico.service.data.ServiceProvider;
import com.extenprise.mapp.medico.util.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class AppointmentDetailsActivity extends Activity implements ResponseHandler {

    ProgressDialog progressDialog;
    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));
    private AppointmentListItem mAppont;
    private ServiceProvider mServProv;
    private ArrayList<AppointmentListItem> mPastApponts;
    private TextView mStatusView;
    private int lastAppontIndex = 0;

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
        Button mConfirmAppontButton = (Button) findViewById(R.id.confirmButton);
        Button mCancelAppontButton = (Button) findViewById(R.id.cancelButton);
        mStatusView = (TextView) findViewById(R.id.statusTextView);

        Bundle bundle = WorkingDataStore.getBundle();
        mAppont = bundle.getParcelable("appont");

        mServProv = LoginHolder.servLoginRef;

        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        
        dateView.setText(sdf.format(mAppont.getDate()));
        fNameView.setText(mAppont.getFirstName());
        lNameView.setText(mAppont.getLastName());
        timeView.setText(mAppont.getTime());
        genderView.setText(mAppont.getGender());
        ageView.setText(String.format("%d", mAppont.getAge()));
        wtView.setText(String.format("%.1f", mAppont.getWeight()));
        
        /*Date appontDate = mAppont.getDate();
        int appontTime = Utility.getMinutes(mAppont.getTime());
        appontDate.setTime(appontDate.getTime() + appontTime * 60 * 1000);*/

        Date appontDate = new Date(
                mAppont.getDate().getTime()
                        + Utility.getMinutes(mAppont.getTime()) * 60 * 1000);

        if (appontDate.after(today)) {
            //Utility.setEnabledButton(this, rxButton, false);
            Utility.setEnabledButton(this, uploadRxButton, false);
        } else if (appontDate.before(today)) {
            int day = cal.get(Calendar.DAY_OF_MONTH);
            cal.setTime(appontDate);
            int apptDay = cal.get(Calendar.DAY_OF_MONTH);
            if (apptDay != day) { /* appointment day has passed */
                //Utility.setEnabledButton(this, rxButton, false);
                Utility.setEnabledButton(this, uploadRxButton, true);
            } else { /* appointment was today but the time has passed */
                Utility.setEnabledButton(this, rxButton, true);
                Utility.setEnabledButton(this, uploadRxButton, true);
            }
        } else { /* appointment time has just begun */
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

        //Appointment of patient is not confirmed than service provider can not create Rx of patient
        if (!mAppont.isConfirmed()) {
            Utility.setEnabledButton(this, rxButton, false);
            Utility.setEnabledButton(this, uploadRxButton, false);
        }

        //If time has past than also button should get disabled.
        if (mAppont.isConfirmed() || mAppont.isCanceled() || appontDate.before(today)) {
            Utility.setEnabledButton(this, mConfirmAppontButton, false);
            Utility.setEnabledButton(this, mCancelAppontButton, false);
        }

        fillPastAppointements();
    }

    private void statusChangeDone(int msg) {
        /*Utility.setEnabledButton(this, mConfirmAppontButton, false);
        Utility.setEnabledButton(this, mCancelAppontButton, false);*/
        //Utility.showAlert(this, "", msg);

        //refreshing page
        Utility.showAlert(this, "", getString(msg), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = getIntent();
                intent.putExtra("appont", mAppont);
                finish();
                startActivity(intent);
            }
        });
    }

    private void gotPastAppointments(Bundle data) {
        /* Hide the message view */
        TextView msgView = (TextView) findViewById(R.id.viewMsg);
        msgView.setVisibility(View.GONE);

        Bundle workingData = WorkingDataStore.getBundle();

        if (mPastApponts == null) {
            /* Get the list of appointments */
            mPastApponts = data.getParcelableArrayList("appontList");
            workingData.putParcelableArrayList("appontList", mPastApponts);
        }

        /* Setup the past appointment view layout */
        View pastAppontLayout = findViewById(R.id.pastAppointmentLayout);
        Button viewMoreButton = (Button) findViewById(R.id.viewMoreButton);
        if (viewMoreButton == null) {
            viewMoreButton = (Button) pastAppontLayout.findViewById(R.id.viewMoreButton);
        }
        Button viewRxButton = (Button) pastAppontLayout.findViewById(R.id.viewRxButton);

        /* Display the last appointment date, if past appointments are present */
        if (mPastApponts != null && mPastApponts.size() > 0) {
            AppointmentListItem lastAppont = mPastApponts.get(lastAppontIndex);
            TextView dateOthView = (TextView) pastAppontLayout.findViewById(R.id.dateTextView);
            dateOthView.setText(Utility.getDateAsStr(lastAppont.getDate(), "dd/MM/yyyy"));
            Utility.setEnabledButton(this, viewRxButton, true, R.color.LinkColor);

            /* Enable 'View More' button only if there are more than one past appointments */
            if (mPastApponts.size() > 1) {
                Utility.setEnabledButton(this, viewMoreButton, true, R.color.LinkColor);
            }
            pastAppontLayout.setVisibility(View.VISIBLE);
        } else {
            /* if no past appointment is present, then display message */
            pastAppontLayout.setVisibility(View.INVISIBLE);
            msgView.setText(R.string.no_past_appont);
            msgView.setVisibility(View.VISIBLE);
        }
    }

    private void fillPastAppointements() {
        Bundle data = WorkingDataStore.getBundle();
        mPastApponts = data.getParcelableArrayList("appontList");
        if (mPastApponts != null) {
            gotPastAppointments(data);
            return;
        }
        mPastApponts = null;
        View pastAppontLayout = findViewById(R.id.pastAppointmentLayout);
        pastAppontLayout.setVisibility(View.GONE);
        TextView msgView = (TextView) findViewById(R.id.viewMsg);
        msgView.setText(R.string.loading_page);
        msgView.setVisibility(View.VISIBLE);
        doAppontAction(MappService.DO_CUST_PAST_APPONT_LIST);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_appointment_details, menu);
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

    private void doAppontAction(int action) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("form", mAppont);
        mConnection.setData(bundle);
        mConnection.setAction(action);
        if(Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE)) {
            progressDialog = ProgressDialog.show(this, "", getString(R.string.msg_please_wait), true);
        }
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

    public void viewRxDetails(View view) {
        Intent intent = new Intent(this, ViewRxActivity.class);
        intent.putExtra("parent-activity", getClass().getName());
        intent.putExtra("appont", mAppont);
        intent.putExtra("pastAppont", mPastApponts.get(lastAppontIndex));
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
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
        if (action == MappService.DO_CUST_PAST_APPONT_LIST) {
            gotPastAppointments(data);
            return true;
        } else if (action == MappService.DO_CONFIRM_APPONT) {
            mAppont.setConfirmed(true);
            mStatusView.setText(getString(R.string.confirmed));
            statusChangeDone(R.string.msg_appont_confirmed);
            return true;
        } else if (action == MappService.DO_CANCEL_APPONT) {
            mAppont.setCanceled(true);
            mStatusView.setText(getString(R.string.canceled));
            statusChangeDone(R.string.msg_appont_canceled);
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        /* Remove the data from WorkingDataStore */
        Bundle bundle = WorkingDataStore.getBundle();
        bundle.remove("appontList");

        Intent intent = super.getParentActivityIntent();
        if (intent != null) {
            intent.putExtra("service", mServProv);
        }
        return intent;
    }

    @Override
    public void onBackPressed() {
        mConnection.setBound(false);
        //startActivity(getIntent());
        super.onBackPressed();
    }
}
