package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.util.SearchDoctor;
import com.extenprise.mapp.util.UIUtility;

import java.util.Calendar;

public class AdvanceSearchDocActivity extends Activity {

    private UserSearchTask mSearchTask = null;

    private Button /*mSearchButn,*/ mButtonStartTime, mButttonEndTime;
    private EditText mDrClinicName;
    private EditText mSpeciality;
    private EditText mLocation;
    private EditText mQualification;
    private EditText mExperience;
    private Spinner mAvaildays;
    private Spinner mGender;

    private View mProgressView;
    private View mSearchFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_search_doc);

        mDrClinicName = (EditText)findViewById(R.id.editSearchDr);
        mSpeciality = (EditText)findViewById(R.id.viewSpeciality);
        mLocation = (EditText)findViewById(R.id.viewLocation);
        mQualification = (EditText)findViewById(R.id.editTextQualification);
        mButtonStartTime = (Button)findViewById(R.id.buttonStartTime);
        mButttonEndTime = (Button)findViewById(R.id.buttonEndTime);
        mAvaildays = (Spinner) findViewById(R.id.spinAvailDays);
        mGender = (Spinner) findViewById(R.id.spinGender);
        mExperience = (EditText) findViewById(R.id.editTextExp);

        mSearchFormView = findViewById(R.id.advSearchForm);
        mProgressView = findViewById(R.id.search_progress);

        if(LoginHolder.spsspt != null) {
            mLocation.setText(LoginHolder.spsspt.getServicePoint().getLocation());
            mSpeciality.setText(LoginHolder.spsspt.getServProvHasService().getService().getSpeciality());
            mDrClinicName.setText(LoginHolder.spsspt.getServicePoint().getName());
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
        /*DialogFragment timeDialog = new TimePickerFragment();
        ((TimePickerFragment) timeDialog).setParentView(view);
        timeDialog.show(getSupportFragmentManager(), "" + R.id.buttonStartTime);*/

        timePicker(mButtonStartTime);
    }

    public void showEndTimePicker(View view) {
        timePicker(mButttonEndTime);
    }

    public void timePicker(final Button button) {
        // Process to get Current Time
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        // Display Selected time in textbox
                        button.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, hour, minute, false);
        tpd.show();
    }

    public void dayPicker(View view, final Button button) {
        // Process to get Current Time
        final Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_WEEK);

        /*String[] array = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"};
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.mySpinner);
        multiSelectionSpinner.setItems(array);
        multiSelectionSpinner.setSelection(new int[]{2, 6});*/



    }

    public void searchDr(View view) {

        View focusView = null;

        String name = mDrClinicName.getText().toString().trim();
        String loc = mLocation.getText().toString().trim();
        String sp = mSpeciality.getText().toString().trim();
        String dr = name, clinic = name;

        String qualification = mQualification.getText().toString().trim();
        String gender = mGender.getSelectedItem().toString();
        String exp = mExperience.getText().toString().trim();
        String startTime = mButtonStartTime.getText().toString();
        String endTime = mButttonEndTime.getText().toString();
        String availDay = mAvaildays.getSelectedItem().toString();

        if (!(endTime.equals("Start Time")) &&
                !(startTime.equals("End Time")) ) {
            if (UIUtility.getMinutes(startTime) >= UIUtility.getMinutes(endTime)) {
                mButttonEndTime.setError("End Time Can't be similar or less than to Start Time.");
                focusView = mButttonEndTime;
                focusView.requestFocus();
                return;
            }
        }

        if(!name.equals("")) {
            if(name.contains(",")) {
                String[] str = name.trim().split(",");
                dr = str[0];
                if(str.length > 1) {
                    clinic = str[1];
                }
            }
        }
        UIUtility.showProgress(this, mSearchFormView, mProgressView, true);
        mSearchTask = new UserSearchTask(this, dr, clinic, sp, loc,
                qualification, exp, startTime, endTime, availDay, gender);
        mSearchTask.execute((Void) null);
    }

    public class UserSearchTask extends AsyncTask<Void, Void, Boolean> {

        private final Activity mActivity;
        private final String mName, mClinic, mSpec, mLoc, mQualification, mExp, mStartTime, mEndTime, mAvailDay, mGender;

        public UserSearchTask(Activity mActivity, String mName, String mClinic, String mSpec, String mLoc,
                              String mQualification, String mExp, String mStartTime, String mEndTime,
                              String mAvailDay, String mGender) {

            this.mActivity = mActivity;
            this.mName = mName;
            this.mClinic = mClinic;
            this.mSpec = mSpec;
            this.mLoc = mLoc;
            this.mQualification = mQualification;
            this.mExp = mExp;
            this.mStartTime = mStartTime;
            this.mEndTime = mEndTime;
            this.mAvailDay = mAvailDay;
            this.mGender = mGender;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
            boolean presence = false;
            if(SearchDoctor.searchByAll(dbHelper, mName, mClinic, mSpec, mLoc, mQualification,
                    mExp, mStartTime, mEndTime, mAvailDay, mGender)) {
                presence = true;
            }
            return presence;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mSearchTask = null;
            UIUtility.showProgress(mActivity, mSearchFormView, mProgressView, false);

            if (success) {
                Intent intent = new Intent(mActivity, SearchDocResultListActivity.class);
                startActivity(intent);

            } else {
                    /*Intent intent = new Intent(mActivity, AdvanceSearchDocActivity.class);
                    startActivity(intent);*/
                UIUtility.showAlert(mActivity,"No Results Found","Sorry, No result matches to your criteria!");
            }
        }

        @Override
        protected void onCancelled() {
            mSearchTask = null;
            UIUtility.showProgress(mActivity, mSearchFormView, mProgressView, false);
        }
    }
}
