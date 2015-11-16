package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.customer.data.Customer;
import com.extenprise.mapp.data.Appointment;
import com.extenprise.mapp.data.Rx;
import com.extenprise.mapp.data.RxItem;
import com.extenprise.mapp.net.AppStatus;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.MappServiceConnection;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.data.AppointmentListItem;
import com.extenprise.mapp.service.data.RxInboxItem;
import com.extenprise.mapp.util.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RxActivity extends Activity implements ResponseHandler {
    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));

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
    private ArrayList<RxInboxItem> mInbox;

    private String mParentActivity;
    private AppointmentListItem mAppont;
    private boolean mFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx);

        Intent intent = getIntent();
        mParentActivity = intent.getStringExtra("parent-activity");
        mAppont = intent.getParcelableExtra("appont");
        mFeedback = intent.getBooleanExtra("feedback", false);

        mForm = findViewById(R.id.rxItemForm);
        mProgressBar = findViewById(R.id.rxSave_progress);

        Button addButton = (Button) findViewById(R.id.addButton);
        TextView name = (TextView) findViewById(R.id.nameTextView);
        //TextView date = (TextView) findViewById(R.id.dateTextView);
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

        RxInboxItem rxInboxItem = null;
        if (mFeedback) {
            mInbox = intent.getParcelableArrayListExtra("inbox");
            int position = intent.getIntExtra("position", 0);
            rxInboxItem = mInbox.get(position);
            Customer c = rxInboxItem.getCustomer();
            name.setText(String.format("%s %s", c.getfName(), c.getlName()));
            addButton.setVisibility(View.GONE);
        } else {
            name.setText(String.format("%s %s", mAppont.getFirstName(), mAppont.getLastName()));
            SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
            sdf.applyPattern("dd/MM/yyyy");
            //date.setText(sdf.format(mAppont.getDate()));
        }

        fillRx(rxInboxItem);
    }

    private void fillRx(RxInboxItem rxInboxItem) {
        if (mFeedback) {
            int position = getIntent().getIntExtra("rxItemPos", 0);
            setupRxItemUI(rxInboxItem.getRx().getItems().get(position));
            return;
        }
        Utility.showProgress(this, mForm, mProgressBar, true);
        Bundle bundle = new Bundle();
        bundle.putParcelable("form", mAppont);
        mConnection.setAction(MappService.DO_GET_RX);
        mConnection.setData(bundle);
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
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
        mSrNo.setText(String.format("%d", (mRx.getRxItemCount() + 1)));
    }

    public void doneRx(View view) {
        if (mFeedback) {
            if (updateRxItem()) {
                try {
                    Intent intent = new Intent(this, Class.forName(mParentActivity));
                    intent.putExtra("feedback", mFeedback);
                    intent.putParcelableArrayListExtra("inbox", mInbox);
                    intent.putExtra("position", getIntent().getIntExtra("position", 0));
                    startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return;
        }
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
        Bundle bundle = new Bundle();
        bundle.putParcelable("rx", mRx);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_SAVE_RX);
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
    }

    private void setupRxItemUI(RxItem rxItem) {
        mDrugName.setText(rxItem.getDrugName());
        mDrugStrength.setText(rxItem.getDrugStrength());
        mDrugForm.setSelection(Utility.getDrugTypePosition(this, rxItem.getDrugForm()));
        mDoseQty.setText(rxItem.getDoseQty());
        mCourseDur.setText(String.format("%d", rxItem.getCourseDur()));
        mEmptyOrFull.setSelection(Utility.getEmptyOrFullPosition(this, rxItem.isBeforeMeal()));
        mInTakeSteps.setText(rxItem.getInTakeSteps());
        mAltDrugName.setText(rxItem.getAltDrugName());
        mAltDrugStrength.setText(rxItem.getAltDrugStrength());
        mAltDrugForm.setSelection(Utility.getDrugTypePosition(this, rxItem.getDrugForm()));

        mMorning.setChecked(rxItem.isMorning());
        mAfternnon.setChecked(rxItem.isAfternoon());
        mEvening.setChecked(rxItem.isEvening());
        if (rxItem.getmTime() != null) {
            mMTime.setText(rxItem.getmTime());
        }
        if (rxItem.getaTime() != null) {
            mATime.setText(rxItem.getaTime());
        }
        if (rxItem.geteTime() != null) {
            mETime.setText(rxItem.geteTime());
        }
    }

    private void fillRxItem(RxItem rxItem) {
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
    }

    private boolean updateRxItem() {
        if (!isValidInput()) {
            return false;
        }
        int position = getIntent().getIntExtra("position", 0);
        int rxItemPos = getIntent().getIntExtra("rxItemPos", 0);
        RxInboxItem rxInboxItem = mInbox.get(position);
        RxItem item = rxInboxItem.getRx().getItems().get(rxItemPos);
        fillRxItem(item);
        return true;
    }

    private boolean addRxItem() {
        if (!isValidInput()) {
            return false;
        }
        RxItem rxItem = new RxItem();
        fillRxItem(rxItem);
        int srNo = mRx.getNextSrNo();
        rxItem.setSrNo(srNo);
        mRx.setNextSrNo(srNo + 1);

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
        if (mRx == null) {
            mRx = new Rx();
        }
        mSrNo.setText(String.format("%d", mRx.getNextSrNo()));

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
        if (rx != null) {
            mRx.setIdReport(rx.getIdReport());
            Log.v("RxActivity", "Saved Rx, id: " + mRx.getIdReport());
        }
        Intent intent = new Intent(this, SelectMedicalStoreActivity.class);
        intent.putExtra("rx", mRx);
        intent.putExtra("appont", mAppont);
        intent.putExtra("service", getIntent().getParcelableExtra("service"));
        startActivity(intent);
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        if (action == MappService.DO_GET_RX) {
            gotRx(data);
            return true;
        } else if (action == MappService.DO_SAVE_RX) {
            saveRxDone(data);
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        try {
            Intent intent = new Intent(this, Class.forName(mParentActivity));
            intent.putExtra("appont", mAppont);
            intent.putExtra("feedback", mFeedback);
            intent.putParcelableArrayListExtra("inbox", mInbox);
            intent.putExtra("position", getIntent().getIntExtra("position", 0));
            return intent;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return super.getParentActivityIntent();
    }
}
