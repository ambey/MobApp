package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
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

    private Messenger mService;
    private ServiceResponseHandler mRespHandler = new ServiceResponseHandler(this);
    private int mAction;

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
        mPastApponts = data.getParcelableArrayList("appontList");
        View pastAppontLayout = findViewById(R.id.pastAppointmentLayout);
        Button viewMoreButton = (Button) findViewById(R.id.viewMoreButton);
        if (viewMoreButton == null) {
            viewMoreButton = (Button) pastAppontLayout.findViewById(R.id.viewMoreButton);
        }
        viewMoreButton.setVisibility(View.VISIBLE);
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        if (mPastApponts != null && mPastApponts.size() > 0) {
            AppointmentListItem lastAppont = mPastApponts.get(mPastApponts.size() - 1);
            TextView dateOthView = (TextView) pastAppontLayout.findViewById(R.id.dateTextView);
            dateOthView.setText(sdf.format(lastAppont.getDate()));
        } else {
            pastAppontLayout.setVisibility(View.INVISIBLE);
            TextView msgView = (TextView) findViewById(R.id.viewMsg);
            msgView.setVisibility(View.VISIBLE);
        }
        if (mPastApponts == null || mPastApponts.size() <= 1) {
            Utility.setEnabledButton(this, viewMoreButton, false);
        }
    }

    private void fillPastAppointements() {
        mAction = MappService.DO_PAST_APPONT_LIST;
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
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

    public void confirmAppointment(View view) {
        mAction = MappService.DO_CONFIRM_APPONT;
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
    }

    public void cancelAppointment(View view) {
        mAction = MappService.DO_CANCEL_APPONT;
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
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

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            mService = new Messenger(service);
            Bundle bundle = new Bundle();

            bundle.putParcelable("form", mAppont);
            Message msg = Message.obtain(null, mAction);
            msg.replyTo = new Messenger(mRespHandler);
            msg.setData(bundle);

            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
        }
    };

    @Override
    public boolean gotResponse(int action, Bundle data) {
        try {
            unbindService(mConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
