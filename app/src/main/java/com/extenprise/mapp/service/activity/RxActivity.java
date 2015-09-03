package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.customer.data.Customer;
import com.extenprise.mapp.data.Appointment;
import com.extenprise.mapp.data.Rx;
import com.extenprise.mapp.data.RxItem;
import com.extenprise.mapp.util.UIUtility;

public class RxActivity extends Activity {
    private TextView mFName;
    private TextView mLName;
    private TextView mDate;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx);

        mFName = (TextView) findViewById(R.id.fNameTextView);
        mLName = (TextView) findViewById(R.id.lNameTextView);
        mDate = (TextView) findViewById(R.id.dateTextView);
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
        mRx = new Rx();

        Appointment appointment = LoginHolder.appointment;
        Customer customer = appointment.getCustomer();

        mFName.setText(customer.getfName());
        mLName.setText(customer.getlName());
        mDate.setText(appointment.getDateOfAppointment());
        mSrNo.setText("#1");

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
        UIUtility.timePicker(view, mMTime);
    }

    public void showATimePicker(View view) {
        UIUtility.timePicker(view, mATime);
    }

    public void showETimePicker(View view) {
        UIUtility.timePicker(view, mETime);
    }

    public void addRxItem(View view) {
        addRxItem();
        clearFields();
        mSrNo.setText("" + (mRx.getRxItemCount() + 1));
    }

    public void doneRx(View view) {
        if (addRxItem()) {
            Appointment appointment = LoginHolder.appointment;
            appointment.addReport(mRx);
            try {
                Intent intent = new Intent(this, Class.forName(getIntent().getStringExtra("parent-activity")));
                startActivity(intent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean addRxItem() {
        if (!isValidInput()) {
            return false;
        }
        RxItem rxItem = new RxItem();
        rxItem.setSrno(mRx.getRxItemCount() + 1);
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
}
