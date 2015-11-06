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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.data.Appointment;
import com.extenprise.mapp.data.Rx;
import com.extenprise.mapp.data.RxItem;
import com.extenprise.mapp.net.AppStatus;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.data.AppointmentListItem;
import com.extenprise.mapp.util.Utility;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RxActivity extends Activity implements ResponseHandler {
    private Messenger mService;
    private ServiceResponseHandler mRespHandler = new ServiceResponseHandler(this);

    private View mForm;
    private View mProgressBar;
    private TextView mSrNo;
    private TextView mDrugName;
    private TextView mDrugStrength;
    private Spinner mDrugForm;
    private TextView mDoseQty;
    private TextView mCourseDur;
    private Spinner mEmptyOrFull;
    private CheckBox mMorning;
    private CheckBox mAfternnon;
    private CheckBox mEvening;
    private TextView mMTime;
    private TextView mATime;
    private TextView mETime;
    private TextView mInTakeSteps;
    private TextView mAltDrugName;
    private TextView mAltDrugStrength;
    private Spinner mAltDrugForm;
    private Rx mRx;

    private String mParentActivity;
    private AppointmentListItem mAppont;
    private int mAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx);

        Intent intent = getIntent();
        mParentActivity = intent.getStringExtra("parent-activity");
        mAppont = intent.getParcelableExtra("appont");

        mForm = findViewById(R.id.rxItemForm);
        mProgressBar = findViewById(R.id.rxSave_progress);

        TextView fName = (TextView) findViewById(R.id.fNameTextView);
        TextView lName = (TextView) findViewById(R.id.lNameTextView);
        TextView date = (TextView) findViewById(R.id.dateTextView);
        mSrNo = (TextView) findViewById(R.id.srNoTextView);
        mDrugName = (TextView) findViewById(R.id.drugEditText);
        mDrugStrength = (TextView) findViewById(R.id.drugStrengthEditText);
        mDrugForm = (Spinner) findViewById(R.id.drugFormSpinner);
        mDoseQty = (TextView) findViewById(R.id.doseQtyEditText);
        mCourseDur = (TextView) findViewById(R.id.courseDurEditText);
        mEmptyOrFull = (Spinner) findViewById(R.id.emptyOrFullSpinner);
        mMorning = (CheckBox) findViewById(R.id.morningCheckBox);
        mAfternnon = (CheckBox) findViewById(R.id.afternoonCheckBox);
        mEvening = (CheckBox) findViewById(R.id.eveningCheckBox);
        mMTime = (TextView) findViewById(R.id.timeMTextView);
        mATime = (TextView) findViewById(R.id.timeATextView);
        mETime = (TextView) findViewById(R.id.timeETextView);
        mInTakeSteps = (TextView) findViewById(R.id.intakeStepsEditText);
        mAltDrugName = (TextView) findViewById(R.id.altDrugEditText);
        mAltDrugStrength = (TextView) findViewById(R.id.altDrugStrengthEditText);
        mAltDrugForm = (Spinner) findViewById(R.id.altDrugFormSpinner);

        fName.setText(mAppont.getFirstName());
        lName.setText(mAppont.getLastName());
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        date.setText(sdf.format(mAppont.getDate()));

        fillRx();
/*
        MappDbHelper dbHelper = new MappDbHelper(this);
        mRx = DBUtil.getRx(dbHelper, mAppontId);
        if(mRx == null) {
            mRx = new Rx();
        }
        mSrNo.setText("" + (mRx.getRxItemCount() + 1));
*/

    }

    private void fillRx() {
        Utility.showProgress(this, mForm, mProgressBar, true);
        mAction = MappService.DO_GET_RX;
        Intent intent = new Intent(this, MappService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rx, menu);
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

    public void showMTimePicker(View view) {
        Utility.timePicker(view, mMTime);
    }

    public void showATimePicker(View view) {
        Utility.timePicker(view, mATime);
    }

    public void showETimePicker(View view) {
        Utility.timePicker(view, mETime);
    }

    public void addRxItem(View view) {
        addRxItem();
        clearFields();
        mSrNo.setText(String.format("%d",(mRx.getRxItemCount() + 1)));
    }

    public void doneRx(View view) {
        if (addRxItem()) {
            addRxToDB();
        }
    }

    private void addRxToDB() {
        if (!AppStatus.getInstance(this).isOnline()) {
            Utility.showMessage(this, R.string.error_not_online);
            return;
        }
        Utility.showProgress(this, mForm, mProgressBar, true);
        mAction = MappService.DO_SAVE_RX;
        Intent intent = new Intent(this, MappService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);

/*
        RxDBTask task = new RxDBTask(this);
        task.execute();
*/
    }

    private boolean addRxItem() {
        if (!isValidInput()) {
            return false;
        }
        RxItem rxItem = new RxItem();
        rxItem.setDrugName(mDrugName.getText().toString());
        rxItem.setDrugStrength(mDrugStrength.getText().toString());
        rxItem.setDrugForm(mDrugForm.getSelectedItem().toString());
        rxItem.setDoseQty(mDoseQty.getText().toString());
        rxItem.setCourseDur(Integer.parseInt(mCourseDur.getText().toString()));
        boolean beforeMeal = false;
        if (mEmptyOrFull.getSelectedItem().toString().equals(getString(R.string.before_meal))) {
            beforeMeal = true;
        }
        rxItem.setBeforeMeal(beforeMeal);
        rxItem.setInTakeSteps(mInTakeSteps.getText().toString());
        rxItem.setAltDrugName(mAltDrugName.getText().toString());
        rxItem.setAltDrugStrength(mAltDrugStrength.getText().toString());
        rxItem.setAltDrugForm(mAltDrugForm.getSelectedItem().toString());

        rxItem.setMorning(mMorning.isChecked());
        rxItem.setAfternoon(mAfternnon.isChecked());
        rxItem.setEvening(mEvening.isChecked());

        String time = getString(R.string.time);
        if (!mMTime.getText().toString().equals(time)) {
            rxItem.setmTime(mMTime.getText().toString());
        }
        if (!mATime.getText().toString().equals(time)) {
            rxItem.setaTime(mATime.getText().toString());
        }
        if (!mETime.getText().toString().equals(time)) {
            rxItem.seteTime(mETime.getText().toString());
        }

        mRx.addItem(rxItem);
        return true;
    }

    private void clearFields() {
        mDrugName.setText("");
        mDrugStrength.setText("");

        mDoseQty.setText("");
        mCourseDur.setText("");
        mMorning.setChecked(false);
        mAfternnon.setChecked(false);
        mEvening.setChecked(false);

        String time = getString(R.string.time);
        mMTime.setText(time);
        mATime.setText(time);
        mETime.setText(time);

        mInTakeSteps.setText("");
        mAltDrugName.setText("");
        mAltDrugStrength.setText("");
    }

    private boolean isValidInput() {
        boolean valid = true;
        View focusView = null;

        String value = mCourseDur.getText().toString();
        if (TextUtils.isEmpty(value)) {
            mCourseDur.setError(getString(R.string.error_field_required));
            focusView = mCourseDur;
            valid = false;
        } else {
            try {
                int dur = Integer.parseInt(value);
                if (dur < 0) {
                    mCourseDur.setError(getString(R.string.error_num_should_be_positive));
                    focusView = mCourseDur;
                    valid = false;
                }
            } catch (NumberFormatException x) {
                mCourseDur.setError(getString(R.string.error_invalid_number));
            }
        }

        value = mDoseQty.getText().toString();
        if (TextUtils.isEmpty(value)) {
            mDoseQty.setError(getString(R.string.error_field_required));
            focusView = mDoseQty;
            valid = false;
        }

        value = mDrugName.getText().toString();
        if (TextUtils.isEmpty(value)) {
            mDrugName.setError(getString(R.string.error_field_required));
            focusView = mDrugName;
            valid = false;
        }
        if (!valid) {
            focusView.requestFocus();
        }
        return valid;
    }

    private void gotRx(Bundle data) {
        mRx = data.getParcelable("rx");
        if(mRx != null) {
            mSrNo.setText(String.format("%d", mRx.getRxItemCount() + 1));
        } else {
            mSrNo.setText("1");
            mRx = new Rx();
        }
        Appointment a = mRx.getAppointment();
        a.setDate(mAppont.getDate());
        a.setIdServProvHasServPt(mAppont.getIdServProvHasServPt());
        a.setIdCustomer(mAppont.getIdCustomer());
        mRx.setDate(new Date());
        Utility.showProgress(this, mForm, mProgressBar, false);
    }

    private void saveRxDone(Bundle data) {
        Utility.showProgress(this, mForm, mProgressBar, false);
        Rx rx = data.getParcelable("rx");
        if(rx != null) {
            mRx.setId(rx.getId());
            Log.v("RxActivity","Saved Rx, id: " + mRx.getId());
        }
        Intent intent = new Intent(this, SelectMedicalStoreActivity.class);
        intent.putExtra("rx", mRx);
        intent.putExtra("appont", mAppont);
        intent.putExtra("service", getIntent().getParcelableExtra("service"));
        startActivity(intent);
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        try {
            unbindService(mConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(action == MappService.DO_GET_RX) {
            gotRx(data);
            return true;
        } else if(action == MappService.DO_SAVE_RX) {
            saveRxDone(data);
            return true;
        }
        return false;
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

            if(mAction == MappService.DO_GET_RX) {
                bundle.putParcelable("form", mAppont);
            } else if(mAction == MappService.DO_SAVE_RX) {
                bundle.putParcelable("rx", mRx);
            }
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

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        try {
            Intent intent = new Intent(this, Class.forName(mParentActivity));
            intent.putExtra("appont", mAppont);
            return intent;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return super.getParentActivityIntent();
    }
}
