package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.customer.data.Customer;
import com.extenprise.mapp.data.Appointment;
import com.extenprise.mapp.data.Rx;
import com.extenprise.mapp.data.RxItem;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.util.DBUtil;
import com.extenprise.mapp.util.UIUtility;

public class RxActivity extends Activity {
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
    private int mAppontId;
    private String mCustId;
    private Appointment mAppont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx);

        Intent intent = getIntent();
        mParentActivity = intent.getStringExtra("parent-activity");
        mAppontId = intent.getIntExtra("appont_id", -1);
        mCustId = intent.getStringExtra("cust_id");

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

        MappDbHelper dbHelper = new MappDbHelper(this);
        mRx = DBUtil.getRx(dbHelper, mAppontId);
        if(mRx == null) {
            mRx = new Rx();
        }
        mAppont = DBUtil.getAppointment(dbHelper, mAppontId);
        Customer customer = mAppont.getCustomer();
        mRx.setAppointment(mAppont);

        fName.setText(customer.getfName());
        lName.setText(customer.getlName());
        date.setText(mAppont.getDateOfAppointment());
        mSrNo.setText("" + (mRx.getRxItemCount() + 1));

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
            mAppont.addReport(mRx);
            addRxToDB();
        }
    }

    private void addRxToDB() {
        UIUtility.showProgress(this, mForm, mProgressBar, true);
        RxDBTask task = new RxDBTask(this);
        task.execute();
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

    private class RxDBTask extends AsyncTask<Void, Void, Void> {

        private Activity myActivity;

        public RxDBTask(Activity activity) {
            myActivity = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            Appointment appointment = mRx.getAppointment();
            String rxId = "a" + appointment.getId() + "r" + appointment.getReportCount();
            DBUtil.deleteRx(dbHelper, rxId);

            values.put(MappContract.Prescription.COLUMN_NAME_ID_APPOMT, appointment.getId());
            values.put(MappContract.Prescription.COLUMN_NAME_ID_RX, rxId);
            for (RxItem item : mRx.getItems()) {
                values.put(MappContract.Prescription.COLUMN_NAME_SR_NO, item.getSrno());
                values.put(MappContract.Prescription.COLUMN_NAME_DRUG_NAME, item.getDrugName());
                values.put(MappContract.Prescription.COLUMN_NAME_DRUG_STRENGTH, item.getDrugStrength());
                values.put(MappContract.Prescription.COLUMN_NAME_DRUG_FORM, item.getDrugForm());
                values.put(MappContract.Prescription.COLUMN_NAME_DOSE_QTY, item.getDoseQty());
                values.put(MappContract.Prescription.COLUMN_NAME_COURSE_DUR, item.getCourseDur());
                values.put(MappContract.Prescription.COLUMN_NAME_ALT_DRUG_NAME, item.getAltDrugName());
                values.put(MappContract.Prescription.COLUMN_NAME_ALT_DRUG_STRENGTH, item.getAltDrugStrength());
                values.put(MappContract.Prescription.COLUMN_NAME_ALT_DRUG_FORM, item.getAltDrugForm());
                values.put(MappContract.Prescription.COLUMN_NAME_INTAKE_STEPS, item.getInTakeSteps());
                values.put(MappContract.Prescription.COLUMN_NAME_EMPTY_OR_FULL, (item.isBeforeMeal() ? "EMPTY" : "FULL"));
                String timesPerDay = (item.isMorning() ? "1" : "0") + "-" +
                        (item.isAfternoon() ? "1" : "0") + "-" +
                        (item.isEvening() ? "1" : "0");
                values.put(MappContract.Prescription.COLUMN_NAME_TIMES_PER_DAY, timesPerDay);
                String mTime = item.getmTime();
                String aTime = item.getaTime();
                String eTime = item.geteTime();
                String timing = ((mTime != null && !mTime.equals(getString(R.string.time))) ? mTime : "") + "|" +
                        ((aTime != null && !aTime.equals(getString(R.string.time))) ? aTime : "") + "|" +
                        ((eTime != null && !eTime.equals(getString(R.string.time))) ? eTime : "");
                values.put(MappContract.Prescription.COLUMN_NAME_TIMING, timing);
                db.insert(MappContract.Prescription.TABLE_NAME, null, values);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                UIUtility.showProgress(myActivity, mForm, mProgressBar, false);
                Intent intent = new Intent(myActivity, Class.forName(mParentActivity));
                intent.putExtra("appont_id", mAppontId);
                intent.putExtra("cust_id", mCustId);
                startActivity(intent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
        }
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        try {
            Intent intent = new Intent(this, Class.forName(mParentActivity));
            intent.putExtra("appont_id", mAppontId);
            intent.putExtra("cust_id", mCustId);
            return intent;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return super.getParentActivityIntent();
    }
}
