package com.extenprise.mapp.customer.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.extenprise.mapp.R;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.MappServiceConnection;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.data.SearchServProvForm;
import com.extenprise.mapp.ui.DaysSelectionDialog;
import com.extenprise.mapp.ui.DialogDismissListener;
import com.extenprise.mapp.util.Utility;

import java.util.ArrayList;

public class AdvSearchServProvActivity extends FragmentActivity implements ResponseHandler, DialogDismissListener {

    protected CharSequence[] options;
    protected boolean[] selections;
    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));
    private SearchServProvForm mForm;
    private Button /*mSearchButn,*/ mButtonStartTime, mButttonEndTime;
    private EditText mDrClinicName;
    private Spinner mSpeciality;
    private Spinner mServProvCategory;
    private EditText mLocation;
    private EditText mQualification;
    private EditText mExperience;
    private LinearLayout mServProLay3;
    private Spinner mGender;
    private Spinner mConsultFee;
    private View mProgressView;
    private View mSearchFormView;
    private Button mMultiSpinnerDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adv_search_serv_prov);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        options = Utility.getDaysOptions(this);
        selections = new boolean[options.length];

        mDrClinicName = (EditText) findViewById(R.id.editSearchDr);
        mSpeciality = (Spinner) findViewById(R.id.viewSpeciality);
        mServProvCategory = (Spinner) findViewById(R.id.spinServiceProvCategory);
        mLocation = (EditText) findViewById(R.id.viewLocation);
        mQualification = (EditText) findViewById(R.id.editTextQualification);
        mButtonStartTime = (Button) findViewById(R.id.buttonStartTime);
        mButttonEndTime = (Button) findViewById(R.id.buttonEndTime);
        mGender = (Spinner) findViewById(R.id.spinGender);
        mExperience = (EditText) findViewById(R.id.editTextExp);
        mConsultFee = (Spinner) findViewById(R.id.spinConsultationFees);
        mServProLay3 = (LinearLayout) findViewById(R.id.servProLay3);

        mSearchFormView = findViewById(R.id.advSearchForm);
        mProgressView = findViewById(R.id.search_progress);

        mForm = getIntent().getParcelableExtra("form");

        ArrayList<String> specList = getIntent().getStringArrayListExtra("specList");
        if (specList == null) {
            specList = new ArrayList<>();
        }
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(this, R.layout.layout_spinner, specList);
        mSpeciality.setAdapter(spinnerAdapter);

        mMultiSpinnerDays = (Button) findViewById(R.id.spinAvailDays);
        mMultiSpinnerDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDaysSelectionDialog();
            }
        });

        if (mForm != null) {
            mLocation.setText(mForm.getLocation());
            mSpeciality.setSelection(Utility.getSpinnerIndex(mSpeciality, mForm.getSpeciality()));
            mServProvCategory.setSelection(Utility.getSpinnerIndex(mServProvCategory, mForm.getCategory()));
            mDrClinicName.setText(mForm.getName());
            mQualification.setText(mForm.getQualification());
            mExperience.setText(mForm.getExperience());
            mButtonStartTime.setText(mForm.getStartTime());
            mButttonEndTime.setText(mForm.getEndTime());
            mMultiSpinnerDays.setText(mForm.getWorkDays());
        }

        mServProvCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                /*String servCategory = mServProvCategory.getSelectedItem().toString();
                MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
                DBUtil.setSpecOfCategory(getApplicationContext(), dbHelper, servCategory, mSpeciality);
                //setSpecs(specs);*/

                String selectedCategory = mServProvCategory.getSelectedItem().toString();
                if (selectedCategory.equals(getString(R.string.select_category))) {
                    return;
                }
                mForm = new SearchServProvForm();
                mForm.setCategory(selectedCategory);
                Bundle bundle = new Bundle();
                bundle.putParcelable("form", mForm);
                mConnection.setData(bundle);
                mConnection.setAction(MappService.DO_GET_SPECIALITY);
                if (Utility.doServiceAction(AdvSearchServProvActivity.this, mConnection, BIND_AUTO_CREATE)) {
                    Utility.showProgress(AdvSearchServProvActivity.this, mSearchFormView, mProgressView, true);
                }
                //getSpecialityList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    public void showtimeFields(View view) {
        if (mServProLay3.getVisibility() == View.VISIBLE) {
            Utility.collapse(mServProLay3, null);
        } else {
            Utility.expand(mServProLay3, null);
        }
    }

    public void showGenderField(View view) {
        if (mGender.getVisibility() == View.VISIBLE) {
            Utility.collapse(mGender, null);
        } else {
            Utility.expand(mGender, null);
        }
    }

    public void showDaysField(View view) {
        if (mMultiSpinnerDays.getVisibility() == View.VISIBLE) {
            Utility.collapse(mMultiSpinnerDays, null);
        } else {
            Utility.expand(mMultiSpinnerDays, null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_advance__search_doc, menu);
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

    public void showStartTimePicker(View view) {
        Utility.timePicker(view, mButtonStartTime);
        //timePicker(mButtonStartTime);
    }

    public void showEndTimePicker(View view) {
        //timePicker(mButttonEndTime);
        Utility.timePicker(view, mButttonEndTime);
    }

    public void searchDr(View view) {
        boolean valid = true;
        View focusView = null;

        String name = mDrClinicName.getText().toString().trim();
        String loc = mLocation.getText().toString().trim();
        String sp = "";
        if (mSpeciality.getSelectedItem() != null) {
            sp = mSpeciality.getSelectedItem().toString();
        }
        if (sp.equals(getResources().getString(R.string.select_speciality)) ||
                sp.equals(getResources().getString(R.string.other))) {
            sp = "";
        }
        String sc = mServProvCategory.getSelectedItem().toString();
        if (sc.equals(getResources().getString(R.string.select_category))) {
            sc = "";
        }

        String clinic = name;

        String qualification = mQualification.getText().toString().trim();
        String gender = mGender.getSelectedItem().toString();
        if (gender.equals("Both")) {
            gender = "";
        }
        String exp = mExperience.getText().toString().trim();
        if (!TextUtils.isEmpty(exp)) {
            double exp2 = Double.parseDouble(mExperience.getText().toString());
            if (exp2 < 0 || exp2 > 99) {
                mExperience.setError(getString(R.string.error_invalid_experience));
                focusView = mExperience;
                valid = false;
            }
        }

        String startTime = mButtonStartTime.getText().toString();
        String endTime = mButttonEndTime.getText().toString();
        //String availDay = Utility.getCommaSepparatedString(selectedDays);

        String availDay = mMultiSpinnerDays.getText().toString();
        if (availDay.equalsIgnoreCase("Select Days")) {
            availDay = "";
        }
        String consultFee = mConsultFee.getSelectedItem().toString();
        if (consultFee.equals("-")) {
            consultFee = "";
        }
        /*if(selectedDays != null && !(selectedDays.equals(""))) {
            availDay = selectedDays;
        }*/

        if (!(endTime.equals("")) &&
                !(startTime.equals(""))) {
            if (Utility.getMinutes(startTime) >= Utility.getMinutes(endTime)) {
                mButttonEndTime.setError("End Time Can't be similar or less than to Start Time.");
                focusView = mButttonEndTime;
                valid = false;
            }
        }

        if (!valid) {
            focusView.requestFocus();
            return;
        }

        if (!name.equals("")) {
            if (name.contains(",")) {
                String[] str = name.trim().split(",");
                if (str.length > 1) {
                    clinic = str[1];
                }
            }
        }

        mForm.setClinic(clinic);
        mForm.setName(name);
        mForm.setStartTime(startTime);
        mForm.setEndTime(endTime);
        mForm.setConsultFee(consultFee);
        mForm.setWorkDays(availDay);
        mForm.setExperience(exp);
        mForm.setGender(gender);
        mForm.setQualification(qualification);
        mForm.setCategory(sc);
        mForm.setSpeciality(sp);
        mForm.setLocation(loc);

        Bundle bundle = new Bundle();
        bundle.putParcelable("form", mForm);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_SEARCH_SERV_PROV);
        if (Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE)) {
            Utility.showProgress(this, mSearchFormView, mProgressView, true);
        }
/*
        mSearchTask = new UserSearchTask(this, dr, clinic, sp, sc, loc,
                qualification, exp, startTime, endTime, availDay, gender, consultFee);
        mSearchTask.execute((Void) null);
*/
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        if (action == MappService.DO_SEARCH_SERV_PROV) {
            searchDone(data);
            return true;
        } else if (action == MappService.DO_GET_SPECIALITY) {
            gotSpecialities(data);
            return true;
        }
        return false;
    }

    private void gotSpecialities(Bundle data) {
        Utility.showProgress(this, mSearchFormView, mProgressView, false);
        ArrayList<String> list = data.getStringArrayList("specialities");
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(0, getString(R.string.select_speciality));
        SpinnerAdapter adapter = new ArrayAdapter<>(this, R.layout.layout_spinner, list);
        mSpeciality.setAdapter(adapter);
    }

    private void searchDone(Bundle data) {
        Utility.showProgress(this, mSearchFormView, mProgressView, false);
        boolean success = data.getBoolean("status");
        if (success) {
            Intent intent = new Intent(this, SearchServProvResultActivity.class);
            intent.putParcelableArrayListExtra("servProvList", data.getParcelableArrayList("servProvList"));
            intent.putExtra("parent-activity", this.getClass().getName());
            intent.putExtra("form", mForm);
            startActivity(intent);
        } else {
            Utility.showMessage(this, R.string.msg_no_result);
        }
    }

    @Override
    public void onDialogDismissed(DialogFragment dialog) {
        DaysSelectionDialog selectionDialog = (DaysSelectionDialog) dialog;
        String selectedDays = selectionDialog.getSelectedDays();
        mMultiSpinnerDays.setText(selectedDays);
    }

    private void showDaysSelectionDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String selctedDays = "";
        if (!mMultiSpinnerDays.getText().toString().equals(getString(R.string.select_days))) {
            selctedDays = mMultiSpinnerDays.getText().toString();
        }
        DaysSelectionDialog dialog = new DaysSelectionDialog();
        dialog.setSelectedDays(selctedDays);
        dialog.show(fragmentManager, "DaysSelect");
    }

    @Override
    public void onBackPressed() {
        if(mConnection.isConnected()) {
            unbindService(mConnection);

            //finish();
            stopService(getIntent());
        }
        //startActivity(getIntent());
        super.onBackPressed();
    }
}
