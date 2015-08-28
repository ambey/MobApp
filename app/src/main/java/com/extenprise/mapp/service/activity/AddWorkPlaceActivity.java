package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.sql.*;
import java.text.*;
import java.util.*;

import com.extenprise.mapp.*;
import com.extenprise.mapp.db.*;
import com.extenprise.mapp.service.data.*;
import com.extenprise.mapp.util.*;

public class AddWorkPlaceActivity extends Activity {

    private EditText mName;
    private EditText mLoc;
    private Spinner mCity;
    private EditText mPhone1;
    private EditText mPhone2;
    private EditText mEmailId;
    private EditText mSpeciality;
    private EditText mExperience;
    private EditText mQualification;
    private Button mStartTime;
    private Button mEndTime;
    //private Spinner mWeeklyOff;
    private Spinner mServPtType;
    private int hour, minute;

    private View mFormView;
    private View mProgressView;

    private Button mMultiSpinnerDays;
    protected CharSequence[] _options = { "All Days", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
    protected boolean[] _selections =  new boolean[ _options.length ];
    //String []selectedDays = new String[_options.length];
    StringBuilder selectedDays = new StringBuilder("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workplace);
        LoginHolder.spsspt = new ServProvHasServHasServPt();

        mFormView = findViewById(R.id.addWorkPlaceForm);
        mProgressView = findViewById(R.id.progressView);

        mName = (EditText) findViewById(R.id.editTextName);
        mLoc = (EditText) findViewById(R.id.editTextLoc);
        mCity = (Spinner) findViewById(R.id.editTextCity);
        mPhone1 = (EditText) findViewById(R.id.editTextPhone1);
        mPhone2 = (EditText) findViewById(R.id.editTextPhone2);
        mEmailId = (EditText) findViewById(R.id.editTextEmail);
        mStartTime = (Button) findViewById(R.id.buttonStartTime);
        mEndTime = (Button) findViewById(R.id.buttonEndTime);
        //mWeeklyOff = (Spinner) findViewById(R.id.editTextWeeklyOff);
        mServPtType = (Spinner) findViewById(R.id.viewWorkPlaceType);

        mSpeciality = (EditText) findViewById(R.id.editTextSpeciality);
        mExperience = (EditText) findViewById(R.id.editTextExperience);
        mQualification = (EditText) findViewById(R.id.editTextQualification);

        mMultiSpinnerDays = (Button)findViewById(R.id.editTextWeeklyOff);
        mMultiSpinnerDays.setOnClickListener( new ButtonClickHandler() );
    }

    public class ButtonClickHandler implements View.OnClickListener {
        public void onClick( View view ) {
            showDialog( 0 );
        }
    }

    @Override
    protected Dialog onCreateDialog( int id )
    {
        return  new AlertDialog.Builder( this )
                .setTitle("Available Days" )
                .setMultiChoiceItems(_options, _selections, new DialogSelectionClickHandler() )
                .setPositiveButton("OK", new DialogButtonClickHandler() )
                .create();
    }

    public class DialogSelectionClickHandler implements DialogInterface.OnMultiChoiceClickListener
    {
        public void onClick( DialogInterface dialog, int clicked, boolean selected )
        {
            Log.i("ME", _options[clicked] + " selected: " + selected);
        }
    }

    public class DialogButtonClickHandler implements DialogInterface.OnClickListener
    {
        public void onClick( DialogInterface dialog, int clicked )
        {
            switch( clicked )
            {
                case DialogInterface.BUTTON_POSITIVE:
                    printSelectedDays();
                    break;
            }
        }
    }

    protected void printSelectedDays(){
        for( int i = 0; i < _options.length; i++ ){
            Log.i( "ME", _options[ i ] + " selected: " + _selections[i] );

            if(_selections[i]) {
                //selectedDays[i] = _options[i].toString();
                selectedDays.append(_options[i].toString());
                selectedDays.append(",");
            }
        }
    }

    protected void onResume() {
        super.onResume();
        initialize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_speciality, menu);
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

    public void addNewWorkPlace(View view) {
        if (!isValidInput()) {
            return;
        }
        ServProvHasService sps = new ServProvHasService();
        sps.setServProv(LoginHolder.servLoginRef);
        sps.setExperience(Float.parseFloat(mExperience.getText().toString().trim()));

        Service s = new Service();
        s.setName("Physician");
        s.setSpeciality(mSpeciality.getText().toString().trim());
        sps.setService(s);

        ServicePoint spt = new ServicePoint();
        ServProvHasServHasServPt spsspt = new ServProvHasServHasServPt();

        spt.setName(mName.getText().toString().trim());
        spt.setLocation(mLoc.getText().toString().trim());
        spt.setCity(mCity.getSelectedItem().toString().trim());
        spt.setPhone(mPhone1.getText().toString().trim());
        spt.setAltPhone(mPhone2.getText().toString().trim());
        spt.setEmailId(mEmailId.getText().toString().trim());

        spsspt.setServPointType(mServPtType.getSelectedItem().toString());
        spsspt.setStartTime(UIUtility.getMinutes(mStartTime.getText().toString()));
        spsspt.setEndTime(UIUtility.getMinutes(mEndTime.getText().toString()));
        spsspt.setWeeklyOff(selectedDays.substring(0, selectedDays.length() - 1));
        spsspt.setServicePoint(spt);
        spsspt.setServProvHasService(sps);
        spt.addSpsspt(spsspt);
        sps.addServProvHasServHasSaervPt(spsspt);

        LoginHolder.servLoginRef.addServProvHasService(sps);

        clearWorkPlace();
        int count = sps.getWorkPlaceCount() + 1;
        TextView countView = (TextView) findViewById(R.id.viewWorkPlaceCount);
        countView.setText("#" + count);
    }

    public void registerDone(View view) {
        addNewWorkPlace(view);
        saveData(view);
    }

    public void showStartTimePicker(View view) {
        /*DialogFragment timeDialog = new TimePickerFragment();
        ((TimePickerFragment) timeDialog).setParentView(view);
        timeDialog.show(getSupportFragmentManager(), "" + R.id.buttonStartTime);*/

        timePicker(view, mStartTime);
    }

    public void showEndTimePicker(View view) {
        timePicker(view, mEndTime);
    }

    public void timePicker(View view, final Button button) {
        // Process to get Current Time
        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

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

    private boolean isValidInput() {
        boolean valid = true;
        View focusView = null;

        String spec = mSpeciality.getText().toString();
        if (TextUtils.isEmpty(spec)) {
            mSpeciality.setError(getString(R.string.error_field_required));
            focusView = mSpeciality;
            valid = false;
        }
        String exp = mExperience.getText().toString();
        if (TextUtils.isEmpty(exp)) {
            mExperience.setError(getString(R.string.error_field_required));
            focusView = mExperience;
            valid = false;
        } else {
            double exp2 = Double.parseDouble(mExperience.getText().toString());
            if (exp2 < 0 || exp2 > 99) {
                mExperience.setError(getString(R.string.error_invalid_experience));
                focusView = mExperience;
                valid = false;
            }
        }
        String qualification = mQualification.getText().toString();
        if (TextUtils.isEmpty(qualification)) {
            mQualification.setError(getString(R.string.error_field_required));
            focusView = mQualification;
            valid = false;
        }

        String name = mName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            mName.setError(getString(R.string.error_field_required));
            focusView = mName;
            valid = false;
        }
        String location = mLoc.getText().toString();
        if (TextUtils.isEmpty(location)) {
            mLoc.setError(getString(R.string.error_field_required));
            focusView = mLoc;
            valid = false;
        }
        String phone1 = mPhone1.getText().toString();
        if (TextUtils.isEmpty(phone1)) {
            mPhone1.setError(getString(R.string.error_field_required));
            focusView = mPhone1;
            valid = false;
        } else if (!Validator.isPhoneValid(phone1)) {
            mPhone1.setError(getString(R.string.error_invalid_phone));
            focusView = mPhone1;
            valid = false;
        }
        String phone2 = mPhone2.getText().toString();
        if (!TextUtils.isEmpty(phone2) && !Validator.isPhoneValid(phone2)) {
            mPhone2.setError(getString(R.string.error_invalid_phone));
            focusView = mPhone2;
            valid = false;
        }
        String email = mEmailId.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailId.setError(getString(R.string.error_field_required));
            focusView = mEmailId;
            valid = false;
        } else if (!Validator.isEmailValid(mEmailId.getText().toString())) {
            mEmailId.setError(getString(R.string.error_invalid_email));
            focusView = mEmailId;
            valid = false;
        }
        if (mEndTime.getText().toString().equals(getString(R.string.end_time))) {
            mEndTime.setError(getString(R.string.error_field_required));
            focusView = mEndTime;
            valid = false;
        }
        if (mStartTime.getText().toString().equals(getString(R.string.start_time))) {
            mStartTime.setError(getString(R.string.error_field_required));
            focusView = mStartTime;
            valid = false;
        }
        if (!(mEndTime.getText().toString().equals(getString(R.string.end_time))) &&
                !(mStartTime.getText().toString().equals(getString(R.string.start_time))) ) {
            if (UIUtility.getMinutes(mStartTime.getText().toString()) >= UIUtility.getMinutes(mEndTime.getText().toString())) {
                mEndTime.setError("End Time Can't be similar or less than to Start Time.");
                focusView = mEndTime;
                valid = false;
            }
        }
        if((selectedDays.substring(0, selectedDays.length() - 1)).equals("")) {
            mMultiSpinnerDays.setError(getString(R.string.error_field_required));
            focusView = mMultiSpinnerDays;
            valid = false;
        }

        if (focusView != null) {
            focusView.requestFocus();
        }
        return valid;
    }

    private void clearWorkPlace() {
        mName.setText("");
        mLoc.setText("");
        mCity.setSelected(false);
        mPhone1.setText("");
        mPhone2.setText("");
        mEmailId.setText("");
        mStartTime.setText(R.string.start_time);
        mEndTime.setText(R.string.end_time);
        //mWeeklyOff.setSelected(false);
        mServPtType.setSelected(false);

    }

    private void initialize() {
        boolean workPlaceAdded = false;
        ServProvHasServHasServPt spsspt = LoginHolder.spsspt;
        ServProvHasService sps = null;
        ServiceProvider sp = LoginHolder.servLoginRef;

        ArrayList<ServProvHasService> spsList = sp.getServices();
        if (spsList != null) {
            for (int i = spsList.size() - 1; i >= 0; i--) {
                if (spsList.get(i).getWorkPlaceCount() == 0) {
                    spsList.remove(i);
                }
            }
        }

        if (spsspt != null) {
            sps = spsspt.getServProvHasService();
            if (sps != null) {
                sp = sps.getServProv();
                workPlaceAdded = sps.isWorkPlaceAdded();
            }
        }

        if (!workPlaceAdded) {
            /*mSpeciality.setText("Some Error");*/
        } else {
            if (sp != null) {
                int count = sp.getServiceCount();
                if (count > 0) {
                    String speciality = sps.getService().getSpeciality();
                    String exp = "" + sps.getExperience();
                    mSpeciality.setText(speciality);
                    mExperience.setText(exp);
                }
            }
        }
    }

    public void saveData(View view) {
        UIUtility.showProgress(this, mFormView, mProgressView, true);

        SaveServiceData task = new SaveServiceData(this);
        task.execute((Void) null);
    }

    class SaveServiceData extends AsyncTask<Void, Void, Void> {

        private Activity myActivity;

        public SaveServiceData(Activity activity) {
            myActivity = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ServiceProvider sp = LoginHolder.servLoginRef;
            ArrayList<ServProvHasService> spsList = sp.getServices();

            MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(MappContract.ServiceProvider.COLUMN_NAME_CELLPHONE, sp.getPhone());
            values.put(MappContract.ServiceProvider.COLUMN_NAME_FNAME, sp.getfName());
            values.put(MappContract.ServiceProvider.COLUMN_NAME_LNAME, sp.getlName());
            values.put(MappContract.ServiceProvider.COLUMN_NAME_PASSWD, sp.getPasswd());

            long spId = db.insert(MappContract.ServiceProvider.TABLE_NAME, null, values);

            //SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getTimeInstance();

            for (ServProvHasService sps : spsList) {
                values = new ContentValues();
                values.put(MappContract.ServProvHasServ.COLUMN_NAME_ID_SERV_PROV, spId);
                values.put(MappContract.ServProvHasServ.COLUMN_NAME_SERVICE_NAME, sps.getService().getName());
                values.put(MappContract.ServProvHasServ.COLUMN_NAME_SPECIALITY, sps.getService().getSpeciality());
                values.put(MappContract.ServProvHasServ.COLUMN_NAME_EXPERIENCE, sps.getExperience());

                long spsId = db.insert(MappContract.ServProvHasServ.TABLE_NAME, null, values);

                ArrayList<ServProvHasServHasServPt> spssptList = sps.getServProvHasServHasServPts();
                for (ServProvHasServHasServPt spsspt : spssptList) {
                    ServicePoint spt = spsspt.getServicePoint();

                    values = new ContentValues();
                    values.put(MappContract.ServicePoint.COLUMN_NAME_NAME, spt.getName());
                    values.put(MappContract.ServicePoint.COLUMN_NAME_LOCATION, spt.getLocation());
                    values.put(MappContract.ServicePoint.COLUMN_NAME_PHONE, spt.getPhone());
                    values.put(MappContract.ServicePoint.COLUMN_NAME_ID_CITY, spt.getCity());

                    long sptId = db.insert(MappContract.ServicePoint.TABLE_NAME, null, values);

                    values = new ContentValues();
                    values.put(MappContract.ServProvHasServHasServPt.COLUMN_NAME_ID_SERV_PROV_HAS_SERV, spsId);
                    values.put(MappContract.ServProvHasServHasServPt.COLUMN_NAME_ID_SERV_PT, sptId);
                    try {
                        values.put(MappContract.ServProvHasServHasServPt.COLUMN_NAME_START_TIME, spsspt.getStartTime());
                        values.put(MappContract.ServProvHasServHasServPt.COLUMN_NAME_END_TIME, spsspt.getEndTime());
                    } catch (Exception x) {
                        x.printStackTrace();
                    }
                    values.put(MappContract.ServProvHasServHasServPt.COLUMN_NAME_WEEKLY_OFF, spsspt.getWeeklyOff());

                    db.insert(MappContract.ServProvHasServHasServPt.TABLE_NAME, null, values);

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            UIUtility.showAlert(myActivity, "Thanks You..!", "You have successfully registered.\nLogin to your account.");
            UIUtility.showProgress(myActivity, mFormView, mProgressView, false);
            Intent intent = new Intent(myActivity, LoginActivity.class);
            startActivity(intent);
        }

        @Override
        protected void onCancelled() {
            UIUtility.showProgress(myActivity, mFormView, mProgressView, false);
        }

    }
}
