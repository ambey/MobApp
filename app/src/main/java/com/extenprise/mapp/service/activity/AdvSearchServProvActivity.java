package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.util.DBUtil;
import com.extenprise.mapp.util.UIUtility;

import java.util.Calendar;

public class AdvSearchServProvActivity extends Activity {

    private Button /*mSearchButn,*/ mButtonStartTime, mButttonEndTime;
    private EditText mDrClinicName;
    private Spinner mSpeciality;
    private Spinner mServProvCategory;
    private EditText mLocation;
    private EditText mQualification;
    private EditText mExperience;

    //private Spinner mAvaildays;
    private Spinner mGender;
    private Spinner mConsultFee;
    private View mProgressView;
    private View mSearchFormView;

    private Button mMultiSpinnerDays;
    protected CharSequence[] options = {"All Days", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    protected boolean[] selections = new boolean[options.length];
    String selectedDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adv_search_serv_prov);

        mDrClinicName = (EditText) findViewById(R.id.editSearchDr);
        mSpeciality = (Spinner) findViewById(R.id.viewSpeciality);
        mServProvCategory = (Spinner) findViewById(R.id.spinServiceProvCategory);
        mLocation = (EditText) findViewById(R.id.viewLocation);
        mQualification = (EditText) findViewById(R.id.editTextQualification);
        mButtonStartTime = (Button) findViewById(R.id.buttonStartTime);
        mButttonEndTime = (Button) findViewById(R.id.buttonEndTime);
        //mAvaildays = (Spinner) findViewById(R.id.spinAvailDays);
        mGender = (Spinner) findViewById(R.id.spinGender);
        mExperience = (EditText) findViewById(R.id.editTextExp);
        mConsultFee = (Spinner) findViewById(R.id.spinConsultationFees);

        mSearchFormView = findViewById(R.id.advSearchForm);
        mProgressView = findViewById(R.id.search_progress);

        if (LoginHolder.spsspt != null) {
            mLocation.setText(LoginHolder.spsspt.getServicePoint().getLocation());
            //mSpeciality.setSelection(LoginHolder.spsspt.getServProvHasService().getService().getSpeciality());
            //mSpeciality.setSelection();
            mSpeciality.setSelection(UIUtility.getSpinnerIndex(mSpeciality, LoginHolder.spsspt.getService().getSpeciality()));
            mServProvCategory.setSelection(UIUtility.getSpinnerIndex(mServProvCategory, LoginHolder.spsspt.getService().getServCatagory()));
            mDrClinicName.setText(LoginHolder.spsspt.getServicePoint().getName());
        }

        mMultiSpinnerDays = (Button) findViewById(R.id.spinAvailDays);
        mMultiSpinnerDays.setOnClickListener(new ButtonClickHandler());

        mServProvCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String servCategory = mServProvCategory.getSelectedItem().toString();
                MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
                DBUtil.setSpecOfCategory(getApplicationContext(), dbHelper, servCategory, mSpeciality);
                //setSpecs(specs);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    public void showtimeFields(View view) {
        if (mButtonStartTime.getVisibility() == View.VISIBLE) {
            mButtonStartTime.setVisibility(View.GONE);
            mButttonEndTime.setVisibility(View.GONE);
            view.setBackgroundResource(R.drawable.label);
            //view.setBackgroundColor(Color.parseColor("#b0171f"));
        } else {
            mButtonStartTime.setVisibility(View.VISIBLE);
            mButttonEndTime.setVisibility(View.VISIBLE);
            view.setBackgroundResource(R.drawable.spinner);
            //view.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    public void showGenderField(View view) {
        if (mGender.getVisibility() == View.VISIBLE) {
            //UIUtility.expandOrCollapse(mGender, "");
            mGender.setVisibility(View.GONE);
            view.setBackgroundResource(R.drawable.label);

        } else {
            //UIUtility.expandOrCollapse(mGender, "expand");
            mGender.setVisibility(View.VISIBLE);
            view.setBackgroundResource(R.drawable.spinner);
        }
    }

    public void showDaysField(View view) {
        if (mMultiSpinnerDays.getVisibility() == View.VISIBLE) {
            //UIUtility.expandOrCollapse(mMultiSpinnerDays, "");
            mMultiSpinnerDays.setVisibility(View.GONE);
            view.setBackgroundResource(R.drawable.label);
        } else {
            //UIUtility.expandOrCollapse(mMultiSpinnerDays, "expand");
            mMultiSpinnerDays.setVisibility(View.VISIBLE);
            view.setBackgroundResource(R.drawable.spinner);
        }
    }



    /*private void setSpecs(ArrayList<String> specs) {
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(this, R.layout.layout_spinner, specs);
        mSpeciality.setAdapter(spinnerAdapter);
    }*/

    public class ButtonClickHandler implements View.OnClickListener {
        public void onClick(View view) {
            if (!mMultiSpinnerDays.getText().equals(getString(R.string.select_days))) {
                setupSelection();
            }
            showDialog(0);
        }
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        if (!mMultiSpinnerDays.getText().equals(getString(R.string.select_days))) {
            setupSelection();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        return new AlertDialog.Builder(this)
                .setTitle("Available Days")
                .setMultiChoiceItems(options, selections, new DialogSelectionClickHandler())
                .setPositiveButton("OK", new DialogButtonClickHandler())
                .create();
    }

    public class DialogSelectionClickHandler implements DialogInterface.OnMultiChoiceClickListener {
        public void onClick(DialogInterface dialog, int clicked, boolean selected) {
            if (options[clicked].toString().equalsIgnoreCase("All Days")) {
                for (CharSequence option : options) {
                    Log.i("ME", option + " selected: " + selected);
                }
            } else {
                Log.i("ME", options[clicked] + " selected: " + selected);
            }
        }
    }

    public class DialogButtonClickHandler implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int clicked) {
            switch (clicked) {
                case DialogInterface.BUTTON_POSITIVE:
                    printSelectedDays();
                    mMultiSpinnerDays.setText(selectedDays);
                    break;
            }
        }
    }

    protected void printSelectedDays() {
        if (selections[0]) {
            setupAllDaysSelected();
            return;
        }
        int i = 1;
        selectedDays = getString(R.string.select_days);
        for (; i < options.length; i++) {
            Log.i("ME", options[i] + " selected: " + selections[i]);

            if (selections[i]) {
                selectedDays = options[i++].toString();
                break;
            }
        }
        for (; i < options.length; i++) {
            Log.i("ME", options[i] + " selected: " + selections[i]);

            if (selections[i]) {
                selectedDays += "," + options[i].toString();
            }
        }
    }

    private void setupSelection() {
        String[] selectedDays = mMultiSpinnerDays.getText().toString().split(",");
        selections[0] = false;
        for (String d : selectedDays) {
            selections[getDayIndex(d)] = true;
        }
    }

    private int getDayIndex(String day) {
        for (int i = 0; i < options.length; i++) {
            if (day.equals(options[i])) {
                return i;
            }
        }
        return 0;
    }

    private void setupAllDaysSelected() {
        selections[0] = false;
        selectedDays = options[1].toString();
        for (int i = 2; i < options.length; i++) {
            selectedDays += "," + options[i];
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

    public void searchDr(View view) {

        View focusView = null;

        String name = mDrClinicName.getText().toString().trim();
        String loc = mLocation.getText().toString().trim();
        String sp = mSpeciality.getSelectedItem().toString();
        if (sp.equals("Select Speciality") || sp.equals("Other")) {
            sp = "";
        }
        String sc = mServProvCategory.getSelectedItem().toString();
        if (sc.equals("Select Category")) {
            sc = "";
        }

        String dr = name, clinic = name;

        String qualification = mQualification.getText().toString().trim();
        String gender = mGender.getSelectedItem().toString();
        if (gender.equals("Both")) {
            gender = "";
        }
        String exp = mExperience.getText().toString().trim();
        String startTime = mButtonStartTime.getText().toString();
        String endTime = mButttonEndTime.getText().toString();
        //String availDay = UIUtility.getCommaSepparatedString(selectedDays);

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
            if (UIUtility.getMinutes(startTime) >= UIUtility.getMinutes(endTime)) {
                mButttonEndTime.setError("End Time Can't be similar or less than to Start Time.");
                focusView = mButttonEndTime;
                focusView.requestFocus();
                return;
            }
        }

        if (!name.equals("")) {
            if (name.contains(",")) {
                String[] str = name.trim().split(",");
                dr = str[0];
                if (str.length > 1) {
                    clinic = str[1];
                }
            }
        }
        UIUtility.showProgress(this, mSearchFormView, mProgressView, true);
/*
        mSearchTask = new UserSearchTask(this, dr, clinic, sp, sc, loc,
                qualification, exp, startTime, endTime, availDay, gender, consultFee);
        mSearchTask.execute((Void) null);
*/
    }

/*
    public class UserSearchTask extends AsyncTask<Void, Void, Boolean> {

        private final Activity mActivity;
        private final String mName, mClinic, mSpec, mServCategory, mLoc,
                mQualification, mExp, mStartTime, mEndTime, mAvailDay, mGender, mConsultFee;

        public UserSearchTask(Activity mActivity, String mName, String mClinic, String mSpec, String mServCategory, String mLoc,
                              String mQualification, String mExp, String mStartTime, String mEndTime,
                              String mAvailDay, String mGender, String mConsultFee) {

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
            this.mConsultFee = mConsultFee;
            this.mServCategory = mServCategory;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
            boolean presence = false;
            if(SearchServProv.searchByAll(dbHelper, mName, mClinic, mSpec, mServCategory, mLoc, mQualification,
                    mExp, mStartTime, mEndTime, mAvailDay, mGender, mConsultFee)) {
                presence = true;
            }
            return presence;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mSearchTask = null;
            UIUtility.showProgress(mActivity, mSearchFormView, mProgressView, false);

            if (success) {
                Intent intent = new Intent(mActivity, SearchServProvResultActivity.class);
                startActivity(intent);

            } else {
                    */
/*Intent intent = new Intent(mActivity, AdvSearchServProvActivity.class);
                    startActivity(intent);*//*

                UIUtility.showAlert(mActivity, "","Sorry, No result matches to your criteria!");
            }
        }

        @Override
        protected void onCancelled() {
            mSearchTask = null;
            UIUtility.showProgress(mActivity, mSearchFormView, mProgressView, false);
        }
    }
*/
}
