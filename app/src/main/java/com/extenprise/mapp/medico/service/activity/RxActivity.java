package com.extenprise.mapp.medico.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.activity.LoginActivity;
import com.extenprise.mapp.medico.customer.data.Customer;
import com.extenprise.mapp.medico.data.Appointment;
import com.extenprise.mapp.medico.data.Rx;
import com.extenprise.mapp.medico.data.RxFeedback;
import com.extenprise.mapp.medico.data.RxItem;
import com.extenprise.mapp.medico.data.WorkingDataStore;
import com.extenprise.mapp.medico.net.MappService;
import com.extenprise.mapp.medico.net.MappServiceConnection;
import com.extenprise.mapp.medico.net.ResponseHandler;
import com.extenprise.mapp.medico.net.ServiceResponseHandler;
import com.extenprise.mapp.medico.service.data.AppointmentListItem;
import com.extenprise.mapp.medico.service.data.RxInboxItem;
import com.extenprise.mapp.medico.util.Utility;
import com.extenprise.mapp.medico.util.Validator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RxActivity extends Activity implements ResponseHandler {
    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));

    /*private View mForm;
    private View mProgressBar;*/
    private TextView mSrNo;
    private TextView mDrugName;
    private Spinner mDrugForm;
    private TextView mCourseDur;
    private Spinner mEmptyOrFull;
    private Spinner mDoseMUnit;
    private Spinner mDoseAUnit;
    private Spinner mDoseEUnit;
    private CheckBox mMorning;
    private CheckBox mAfternnon;
    private CheckBox mEvening;
    private EditText mMDose;
    private EditText mADose;
    private EditText mEDose;
    private TextView mInTakeSteps;
    private TextView mAltDrugName;
    private Spinner mAltDrugForm;
    private Rx mRx;
    private ArrayList<RxInboxItem> mInbox;

    private String mParentActivity;
    private AppointmentListItem mAppont;
    private int mFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx);

        Intent intent = getIntent();
        if (savedInstanceState != null) {
            mParentActivity = savedInstanceState.getString("parent-activity");
            mAppont = savedInstanceState.getParcelable("appont");
            mFeedback = savedInstanceState.getInt("feedback");
        } else {
            mParentActivity = intent.getStringExtra("parent-activity");
            mAppont = intent.getParcelableExtra("appont");
            mFeedback = intent.getIntExtra("feedback", RxFeedback.NONE);
        }
        /*mForm = findViewById(R.id.rxItemForm);
        mProgressBar = findViewById(R.id.rxSave_progress);*/

        Button addButton = (Button) findViewById(R.id.addButton);
        TextView name = (TextView) findViewById(R.id.nameTextView);
        //TextView date = (TextView) findViewById(R.id.dateTextView);
        mSrNo = (TextView) findViewById(R.id.srNoTextView);
        mDrugName = (TextView) findViewById(R.id.drugEditText);
        mDoseMUnit = (Spinner) findViewById(R.id.drugUnitMSpinner);
        mDoseAUnit = (Spinner) findViewById(R.id.drugUnitASpinner);
        mDoseEUnit = (Spinner) findViewById(R.id.drugUnitESpinner);
        mDrugForm = (Spinner) findViewById(R.id.drugFormSpinner);
        mDrugForm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String drugForm = mDrugForm.getSelectedItem().toString();
                int arrayId = -1;
                if (drugForm.equalsIgnoreCase(getString(R.string.syrup))) {
                    arrayId = R.array.syrup_unit;
                } else if (drugForm.equalsIgnoreCase(getString(R.string.powder))) {
                    arrayId = R.array.powder_unit;
                }
                if (arrayId != -1) {
                    mDoseMUnit.setAdapter(new ArrayAdapter<>(RxActivity.this,
                            R.layout.layout_spinner, getResources().getStringArray(arrayId)));
                    mDoseMUnit.setVisibility(View.VISIBLE);
                    mDoseAUnit.setAdapter(new ArrayAdapter<>(RxActivity.this,
                            R.layout.layout_spinner, getResources().getStringArray(arrayId)));
                    mDoseAUnit.setVisibility(View.VISIBLE);
                    mDoseEUnit.setAdapter(new ArrayAdapter<>(RxActivity.this,
                            R.layout.layout_spinner, getResources().getStringArray(arrayId)));
                    mDoseEUnit.setVisibility(View.VISIBLE);
                } else {
                    mDoseMUnit.setAdapter(null);
                    mDoseMUnit.setVisibility(View.GONE);
                    mDoseAUnit.setAdapter(null);
                    mDoseAUnit.setVisibility(View.GONE);
                    mDoseEUnit.setAdapter(null);
                    mDoseEUnit.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mDoseMUnit.setAdapter(null);
                mDoseAUnit.setAdapter(null);
                mDoseEUnit.setAdapter(null);
            }
        });
        mCourseDur = (TextView) findViewById(R.id.courseDurEditText);
        mEmptyOrFull = (Spinner) findViewById(R.id.emptyOrFullSpinner);
        mMorning = (CheckBox) findViewById(R.id.morningCheckBox);
        mAfternnon = (CheckBox) findViewById(R.id.afternoonCheckBox);
        mEvening = (CheckBox) findViewById(R.id.eveningCheckBox);
        mMDose = (EditText) findViewById(R.id.doseMText);
        mADose = (EditText) findViewById(R.id.doseAText);
        mEDose = (EditText) findViewById(R.id.doseEText);
        mInTakeSteps = (TextView) findViewById(R.id.intakeStepsEditText);
        mAltDrugName = (TextView) findViewById(R.id.altDrugEditText);
        mAltDrugForm = (Spinner) findViewById(R.id.altDrugFormSpinner);

        RxInboxItem rxInboxItem = null;
        if (mFeedback == RxFeedback.VIEW_FEEDBACK) {
            /*if (savedInstanceState != null) {
                mInbox = savedInstanceState.getParcelableArrayList("inbox");
            } else {
                mInbox = intent.getParcelableArrayListExtra("inbox");
            }*/
            Bundle bundle = WorkingDataStore.getBundle();
            mInbox = bundle.getParcelableArrayList("inbox");
            rxInboxItem = bundle.getParcelable("rxItem");
            assert rxInboxItem != null;
            Customer c = rxInboxItem.getCustomer();
            name.setText(String.format("%s %s", c.getfName(), c.getlName()));
            addButton.setVisibility(View.GONE);
        } else {
            name.setText(String.format("%s %s", mAppont.getFirstName(), mAppont.getLastName()));
            SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
            sdf.applyPattern("dd/MM/yyyy");
            //date.setText(sdf.format(mAppont.getDate()));
        }

        mMorning.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mMDose.setEnabled(isChecked);
            }
        });

        mEvening.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mEDose.setEnabled(isChecked);
            }
        });

        mAfternnon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mADose.setEnabled(isChecked);
            }
        });

        fillRx(rxInboxItem);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Intent intent = getIntent();
        outState.putParcelableArrayList("inbox", intent.getParcelableArrayListExtra("inbox"));
        outState.putParcelable("appont", intent.getParcelableExtra("appont"));
        outState.putInt("feedback", intent.getIntExtra("feedback", RxFeedback.NONE));
        outState.putString("parent-activity", intent.getStringExtra("parent-activity"));
        outState.putInt("rxItemPos", intent.getIntExtra("rxItemPos", 0));
        outState.putParcelable("servProv", intent.getParcelableExtra("servProv"));
    }

    private void fillRx(RxInboxItem rxInboxItem) {
        if (mFeedback == RxFeedback.VIEW_FEEDBACK) {
            int position = getIntent().getIntExtra("rxItemPos", 0);
            setupRxItemUI(rxInboxItem.getRx().getItems().get(position));
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable("form", mAppont);
        mConnection.setAction(MappService.DO_GET_RX);
        mConnection.setData(bundle);
        if (Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE)) {
            //Utility.showProgress(this, mForm, mProgressBar, true);
            Utility.showProgressDialog(this, true);
        }
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

    public void addRxItem(View view) {
        addRxItem();
        clearFields();
        mSrNo.setText(String.format("%d", (mRx.getRxItemCount() + 1)));
    }

    public void doneRx(View view) {
        if (mFeedback == RxFeedback.VIEW_FEEDBACK) {
            if (updateRxItem()) {
                try {
                    WorkingDataStore.getBundle().putParcelableArrayList("inbox", mInbox);
                    Intent intent = new Intent(this, Class.forName(mParentActivity));
                    intent.putExtra("feedback", mFeedback);
                    //intent.putParcelableArrayListExtra("inbox", mInbox);
                    intent.putExtra("rxItem", getIntent().getParcelableExtra("rxItem"));
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
        Bundle bundle = new Bundle();
        bundle.putParcelable("rx", mRx);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_SAVE_RX);
        if (Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE)) {
            //Utility.showProgress(this, mForm, mProgressBar, true);
            Utility.showProgressDialog(this, true);
        }
    }

    private void setupRxItemUI(RxItem rxItem) {
        mDrugName.setText(rxItem.getDrugName());
        mDrugForm.setSelection(Utility.getDrugTypePosition(this, rxItem.getDrugForm()));
        mCourseDur.setText(String.format("%d", rxItem.getCourseDur()));
        mEmptyOrFull.setSelection(Utility.getEmptyOrFullPosition(this, rxItem.isBeforeMeal()));
        mInTakeSteps.setText(rxItem.getInTakeSteps());
        mAltDrugName.setText(rxItem.getAltDrugName());
        mAltDrugForm.setSelection(Utility.getDrugTypePosition(this, rxItem.getDrugForm()));

        mMorning.setChecked(rxItem.isMorning());
        mAfternnon.setChecked(rxItem.isAfternoon());
        mEvening.setChecked(rxItem.isEvening());
        if (rxItem.getmDose() != null) {
            String[] dose = rxItem.getmDose().split(" ");
            mMDose.setText(dose[0]);
            if (dose.length == 2) {
                mDoseMUnit.setSelection(Utility.getDoseUnitPosition(this, rxItem.getDrugForm(), dose[1].trim()));
            }
        }
        if (rxItem.getaDose() != null) {
            String[] dose = rxItem.getaDose().split(" ");
            mADose.setText(dose[0]);
            if (dose.length == 2) {
                mDoseAUnit.setSelection(Utility.getDoseUnitPosition(this, rxItem.getDrugForm(), dose[1].trim()));
            }
        }
        if (rxItem.geteDose() != null) {
            String[] dose = rxItem.geteDose().split(" ");
            mEDose.setText(dose[0]);
            if (dose.length == 2) {
                mDoseEUnit.setSelection(Utility.getDoseUnitPosition(this, rxItem.getDrugForm(), dose[1].trim()));
            }
        }
    }

    private void fillRxItem(RxItem rxItem) {
        rxItem.setDrugName(mDrugName.getText().toString());
        rxItem.setDrugForm(mDrugForm.getSelectedItem().toString());
        rxItem.setCourseDur(Integer.parseInt(mCourseDur.getText().toString()));
        boolean beforeMeal = false;
        if (mEmptyOrFull.getSelectedItem().toString().equals(getString(R.string.before_meal))) {
            beforeMeal = true;
        }
        rxItem.setBeforeMeal(beforeMeal);
        rxItem.setInTakeSteps(mInTakeSteps.getText().toString());
        rxItem.setAltDrugName(mAltDrugName.getText().toString());
        rxItem.setAltDrugForm(mAltDrugForm.getSelectedItem().toString());

        rxItem.setMorning(mMorning.isChecked());
        if (mMorning.isChecked()) {
            String unit = "";
            if (mDoseMUnit.getSelectedItem() != null) {
                unit = mDoseMUnit.getSelectedItem().toString();
            }
            rxItem.setmDose(String.format("%s %s", mMDose.getText().toString(), unit));
        }
        rxItem.setAfternoon(mAfternnon.isChecked());
        if (mAfternnon.isChecked()) {
            String unit = "";
            if (mDoseAUnit.getSelectedItem() != null) {
                unit = mDoseAUnit.getSelectedItem().toString();
            }
            rxItem.setaDose(String.format("%s %s", mADose.getText().toString(), unit));
        }
        rxItem.setEvening(mEvening.isChecked());
        if (mEvening.isChecked()) {
            String unit = "";
            if (mDoseEUnit.getSelectedItem() != null) {
                unit = mDoseEUnit.getSelectedItem().toString();
            }
            rxItem.seteDose(String.format("%s %s", mEDose.getText().toString(), unit));
        }
    }

    private boolean updateRxItem() {
        if (!isValidInput()) {
            return false;
        }
        int rxItemPos = getIntent().getIntExtra("rxItemPos", 0);
        RxInboxItem rxInboxItem = WorkingDataStore.getBundle().getParcelable("rxItem");
        //RxInboxItem rxInboxItem = getIntent().getParcelableExtra("rxItem");
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

        mCourseDur.setText("");
        mMorning.setChecked(false);
        mAfternnon.setChecked(false);
        mEvening.setChecked(false);

        mMDose.setText("");
        mADose.setText("");
        mEDose.setText("");
        mDoseMUnit.setAdapter(null);
        mDoseAUnit.setAdapter(null);
        mDoseEUnit.setAdapter(null);

        mInTakeSteps.setText("");
        mAltDrugName.setText("");
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
            if (!Validator.isValuePositive(value)) {
                mCourseDur.setError(getString(R.string.error_invalid_number));
                focusView = mCourseDur;
                valid = false;
            }
        }

        value = mDrugName.getText().toString();
        if (TextUtils.isEmpty(value)) {
            mDrugName.setError(getString(R.string.error_field_required));
            focusView = mDrugName;
            valid = false;
        }

        if (mMorning.isChecked()) {
            value = mMDose.getText().toString();
            if (TextUtils.isEmpty(value)) {
                mMDose.setError(getString(R.string.error_field_required));
                focusView = mMDose;
                valid = false;
            } else {
                if (!Validator.isValuePositive(value)) {
                    mMDose.setError(getString(R.string.error_invalid_number));
                    focusView = mMDose;
                    valid = false;
                }
            }
        }
        if (mAfternnon.isChecked()) {
            value = mADose.getText().toString();
            if (TextUtils.isEmpty(value)) {
                mADose.setError(getString(R.string.error_field_required));
                focusView = mADose;
                valid = false;
            } else {
                if (!Validator.isValuePositive(value)) {
                    mADose.setError(getString(R.string.error_invalid_number));
                    focusView = mADose;
                    valid = false;
                }
            }
        }
        if (mEvening.isChecked()) {
            value = mEDose.getText().toString();
            if (TextUtils.isEmpty(value)) {
                mEDose.setError(getString(R.string.error_field_required));
                focusView = mEDose;
                valid = false;
            } else {
                if (!Validator.isValuePositive(value)) {
                    mEDose.setError(getString(R.string.error_invalid_number));
                    focusView = mEDose;
                    valid = false;
                }
            }
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
        a.setFrom(Utility.getMinutes(mAppont.getTime()));
        mRx.setDate(new Date());
        //Utility.showProgress(this, mForm, mProgressBar, false);
        Utility.showProgressDialog(this, false);
    }

    private void saveRxDone(Bundle data) {
        //Utility.showProgress(this, mForm, mProgressBar, false);
        Utility.showProgressDialog(this, false);
        Rx rx = data.getParcelable("rx");
        if (rx != null) {
            mRx.setIdReport(rx.getIdReport());
            Log.v("RxActivity", "Saved Rx, id: " + mRx.getIdReport());
        }
        Intent intent = new Intent(this, SelectMedicalStoreActivity.class);
        intent.putExtra("parent-activity", mParentActivity);
        intent.putExtra("rx", mRx);
        intent.putExtra("appont", mAppont);
        //intent.putExtra("servProv", getIntent().getParcelableExtra("servProv"));
        startActivity(intent);
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        if (action == MappService.DO_GET_RX) {
            gotRx(data);
        } else if (action == MappService.DO_SAVE_RX) {
            saveRxDone(data);
        }
        return data.getBoolean("status");
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        Intent intent;
        try {
            intent = new Intent(this, Class.forName(mParentActivity));
            intent.putExtra("appont", mAppont);
            intent.putExtra("feedback", mFeedback);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            intent = super.getParentActivityIntent();
        }
        return intent;
    }

    @Override
    public void onBackPressed() {
        mConnection.setBound(false);
        Intent intent = getParentActivityIntent();
        if (intent != null) {
            startActivity(intent);
            return;
        }
        //startActivity(getIntent());
        super.onBackPressed();
    }
}
