package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.activity.MappService;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.service.data.ServProvHasServPt;
import com.extenprise.mapp.service.data.ServProvHasService;
import com.extenprise.mapp.service.data.ServicePoint;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.DBUtil;
import com.extenprise.mapp.util.UIUtility;
import com.extenprise.mapp.util.Validator;

import java.util.ArrayList;
import java.util.Calendar;

public class AddWorkPlaceActivity extends Activity {

    private SignUpHandler mResponseHandler = new SignUpHandler(this);

    private EditText mName;
    private EditText mLoc;
    private Spinner mCity;
    private EditText mPhone1;
    private EditText mPhone2;
    private EditText mEmailId;
    private Spinner mSpeciality;
    private EditText mExperience;
    private EditText mQualification;
    private Button mStartTime;
    private Button mEndTime;
    //private Spinner mGender;
    private EditText mConsultFee;
    //private Spinner mWeeklyOff;
    private Spinner mServPtType;
    private Spinner mServCatagory;
    private int hour, minute;

    private View mFormView;
    private View mProgressView;

    private Button mMultiSpinnerDays;
    protected CharSequence[] options = {"All Days", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    protected boolean[] selections = new boolean[options.length];
    //String []selectedDays = new String[_options.length];
    String selectedDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workplace);
        LoginHolder.spsspt = new ServProvHasServPt();

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
        //mGender = (Spinner) findViewById(R.id.spinGender);
        mConsultFee = (EditText) findViewById(R.id.editTextConsultationFees);
        mServPtType = (Spinner) findViewById(R.id.viewWorkPlaceType);
        mSpeciality = (Spinner) findViewById(R.id.editTextSpeciality);
        mExperience = (EditText) findViewById(R.id.editTextExperience);
        mQualification = (EditText) findViewById(R.id.editTextQualification);
        mMultiSpinnerDays = (Button) findViewById(R.id.editTextWeeklyOff);
        mServCatagory = (Spinner) findViewById(R.id.spinServiceProvCategory);

        mServCatagory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String servCategory = mServCatagory.getSelectedItem().toString();
                MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
                DBUtil.setSpecOfCategory(getApplicationContext(), dbHelper, servCategory, mSpeciality);
                //setSpecs(specs);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });


        mSpeciality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String spec = mSpeciality.getSelectedItem().toString();
                if (spec.equals("Other")) {
                    openDialog();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        mMultiSpinnerDays.setOnClickListener(new ButtonClickHandler());
    }

    private AlertDialog openDialog() {
        final EditText txtSpec = new EditText(this);
        txtSpec.setHint("Add Speciality");

        return new AlertDialog.Builder(this)
                .setTitle("Add Speciality")
                .setView(txtSpec)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newSpec = txtSpec.getText().toString();
                        ArrayList<String> specs = new ArrayList<String>();
                        specs.add(newSpec);
                        DBUtil.setNewSpec(getApplicationContext(), specs, mSpeciality);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void showtimeFields(View view) {
        if (mStartTime.getVisibility() == View.VISIBLE) {
            mStartTime.setVisibility(View.GONE);
            mEndTime.setVisibility(View.GONE);
        } else {
            mStartTime.setVisibility(View.VISIBLE);
            mEndTime.setVisibility(View.VISIBLE);
        }
    }

    public void showFeeFields(View view) {
        TextView rupeeSign = (TextView) findViewById(R.id.viewRsSign);
        if (mConsultFee.getVisibility() == View.VISIBLE) {
            mConsultFee.setVisibility(View.GONE);
            rupeeSign.setVisibility(View.GONE);
        } else {
            mConsultFee.setVisibility(View.VISIBLE);
            rupeeSign.setVisibility(View.VISIBLE);
        }
    }

    public void showDaysFields(View view) {
        if (mMultiSpinnerDays.getVisibility() == View.VISIBLE) {
            mMultiSpinnerDays.setVisibility(View.GONE);
        } else {
            //UIUtility.expandOrCollapse(mMultiSpinnerDays, "expand");
            mMultiSpinnerDays.setVisibility(View.VISIBLE);
        }
    }

    public void showWorkFields(View view) {
        if (mName.getVisibility() == View.VISIBLE) {
            /*UIUtility.expandOrCollapse(mName, "");
            UIUtility.expandOrCollapse(mLoc, "");
            UIUtility.expandOrCollapse(mPhone1, "");
            UIUtility.expandOrCollapse(mPhone2, "");
            UIUtility.expandOrCollapse(mEmailId, "");
            UIUtility.expandOrCollapse(mCity, "");
            UIUtility.expandOrCollapse(mServPtType, "");*/

            mName.setVisibility(View.GONE);
            mLoc.setVisibility(View.GONE);
            mPhone1.setVisibility(View.GONE);
            mPhone2.setVisibility(View.GONE);
            mEmailId.setVisibility(View.GONE);
            mCity.setVisibility(View.GONE);
            mServPtType.setVisibility(View.GONE);
        } else {
            /*UIUtility.expandOrCollapse(mName, "expand");
            UIUtility.expandOrCollapse(mLoc, "expand");
            UIUtility.expandOrCollapse(mPhone1, "expand");
            UIUtility.expandOrCollapse(mPhone2, "expand");
            UIUtility.expandOrCollapse(mEmailId, "expand");
            UIUtility.expandOrCollapse(mCity, "expand");
            UIUtility.expandOrCollapse(mServPtType, "expand");*/
            mName.setVisibility(View.VISIBLE);
            mLoc.setVisibility(View.VISIBLE);
            mPhone1.setVisibility(View.VISIBLE);
            mPhone2.setVisibility(View.VISIBLE);
            mEmailId.setVisibility(View.VISIBLE);
            mCity.setVisibility(View.VISIBLE);
            mServPtType.setVisibility(View.VISIBLE);
        }
    }


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
        addNewWorkPlace();
    }

    private boolean addNewWorkPlace() {
        if (!isValidInput()) {
            return false;
        }
        LoginHolder.servLoginRef.setQualification(mQualification.getText().toString().trim());
        //LoginHolder.servLoginRef.setGender(mGender.getSelectedItem().toString());

        ServicePoint spt = new ServicePoint();
        ServProvHasServPt spsspt = new ServProvHasServPt();

        spt.setName(mName.getText().toString().trim());
        spt.setLocation(mLoc.getText().toString().trim());
        spt.getCity().setCity(mCity.getSelectedItem().toString().trim());
        spt.setPhone(mPhone1.getText().toString().trim());
        spt.setAltPhone(mPhone2.getText().toString().trim());
        spt.setEmailId(mEmailId.getText().toString().trim());

        spsspt.getService().setSpeciality(mSpeciality.getSelectedItem().toString());
        spsspt.setExperience(Float.parseFloat(mExperience.getText().toString().trim()));
        spsspt.setServPointType(mServPtType.getSelectedItem().toString());
        spsspt.setStartTime(UIUtility.getMinutes(mStartTime.getText().toString()));
        spsspt.setEndTime(UIUtility.getMinutes(mEndTime.getText().toString()));
        spsspt.setWorkingDays(mMultiSpinnerDays.getText().toString());
        spsspt.setConsultFee(Float.parseFloat(mConsultFee.getText().toString().trim()));
        spsspt.setServicePoint(spt);

        LoginHolder.servLoginRef.addServProvHasServPt(spsspt);

        clearWorkPlace();
        int count = LoginHolder.servLoginRef.getServiceCount() + 1;
        TextView countView = (TextView) findViewById(R.id.viewWorkPlaceCount);
        countView.setText("#" + count);
        return true;
    }

    public void registerDone(View view) {
        if (addNewWorkPlace()) {
            saveData(view);
        }
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

    /*public void setSpinnerError(String errorMessagem, Spinner spnMySpinner)
    {
        View view = spnMySpinner.getSelectedView();

        // Set TextView in Secondary Unit spinner to be in error so that red (!) icon
        // appears, and then shake control if in error
        TextView tvListItem = (TextView)view;

        // Set fake TextView to be in error so that the error message appears
        TextView tvInvisibleError = (TextView)findViewById(R.id.tvInvisibleError);

        // Shake and set error if in error state, otherwise clear error
        if(errorMessage != null)
        {
            tvListItem.setError(errorMessage);
            tvListItem.requestFocus();

            // Shake the spinner to highlight that current selection
            // is invalid -- SEE COMMENT BELOW
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            spnMySpinner.startAnimation(shake);

            tvInvisibleError.requestFocus();
            tvInvisibleError.setError(errorMessage);
        }
        else
        {
            tvListItem.setSpinnerError(null, null);
            tvInvisibleError.setSpinnerError(null, null);
        }
    }*/

    private boolean isValidInput() {
        boolean valid = true;
        View focusView = null;

        String category = mServCatagory.getSelectedItem().toString();
        if (category.equalsIgnoreCase("Select Category")) {
            //UIUtility.showAlert(this, "", "Please select service category.");
            View selectedView = mServCatagory.getSelectedView();
            if (selectedView != null && selectedView instanceof TextView) {
                TextView selectedTextView = (TextView) selectedView;
                String errorString = selectedTextView.getResources().getString(R.string.error_field_required);
                selectedTextView.setError(errorString);
            }
            focusView = mServCatagory;
            valid = false;
        }

        String spec = mSpeciality.getSelectedItem().toString();
        if (spec.equalsIgnoreCase("Select Speciality") || spec.equals("Other")) {
            //UIUtility.showAlert(this, "", "Please select speciality.");
            View selectedView = mSpeciality.getSelectedView();
            if (selectedView != null && selectedView instanceof TextView) {
                TextView selectedTextView = (TextView) selectedView;
                String errorString = selectedTextView.getResources().getString(R.string.error_field_required);
                selectedTextView.setError(errorString);
            }
            focusView = mSpeciality;
            valid = false;
        }

        String exp = mExperience.getText().toString().trim();
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
        String qualification = mQualification.getText().toString().trim();
        if (TextUtils.isEmpty(qualification)) {
            mQualification.setError(getString(R.string.error_field_required));
            focusView = mQualification;
            valid = false;
        }

        String name = mName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            mName.setError(getString(R.string.error_field_required));
            focusView = mName;
            valid = false;
        }
        String location = mLoc.getText().toString().trim();
        if (TextUtils.isEmpty(location)) {
            mLoc.setError(getString(R.string.error_field_required));
            focusView = mLoc;
            valid = false;
        }
        String phone1 = mPhone1.getText().toString().trim();
        if (TextUtils.isEmpty(phone1)) {
            mPhone1.setError(getString(R.string.error_field_required));
            focusView = mPhone1;
            valid = false;
        } else if (!Validator.isPhoneValid(phone1)) {
            mPhone1.setError(getString(R.string.error_invalid_phone));
            focusView = mPhone1;
            valid = false;
        }
        String phone2 = mPhone2.getText().toString().trim();
        if (!TextUtils.isEmpty(phone2) && !Validator.isPhoneValid(phone2)) {
            mPhone2.setError(getString(R.string.error_invalid_phone));
            focusView = mPhone2;
            valid = false;
        }
        String email = mEmailId.getText().toString().trim();
        if (!TextUtils.isEmpty(email) && !Validator.isEmailValid(mEmailId.getText().toString())) {
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
                !(mStartTime.getText().toString().equals(getString(R.string.start_time)))) {
            if (UIUtility.getMinutes(mStartTime.getText().toString()) >= UIUtility.getMinutes(mEndTime.getText().toString())) {
                mEndTime.setError(getString(R.string.error_endtime));
                focusView = mEndTime;
                valid = false;
            }
        }
        String days = mMultiSpinnerDays.getText().toString();
        if (days.equalsIgnoreCase("Select Days")) {
            mMultiSpinnerDays.setError(getString(R.string.error_field_required));
            focusView = mMultiSpinnerDays;
            valid = false;
        }
        String cosultFee = mConsultFee.getText().toString().trim();
        if (TextUtils.isEmpty(cosultFee)) {
            mConsultFee.setError(getString(R.string.error_field_required));
            focusView = mConsultFee;
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
        mConsultFee.setText("");
        mMultiSpinnerDays.setText("Select Days");

    }

    private void initialize() {
        boolean workPlaceAdded = false;
        ServProvHasServPt spsspt = LoginHolder.spsspt;
        ServProvHasService sps = null;
        ServiceProvider sp = LoginHolder.servLoginRef;

        ArrayList<ServProvHasServPt> spsList = sp.getServices();
        if (spsList != null) {
            for (int i = spsList.size() - 1; i >= 0; i--) {
/*
                if (spsList.get(i).getWorkPlaceCount() == 0) {
                    spsList.remove(i);
                }
*/
            }
        }

        if (spsspt != null) {
/*
            sps = spsspt.getServProvHasService();
            if (sps != null) {
                sp = sps.getServProv();
                workPlaceAdded = sps.isWorkPlaceAdded();
            }
*/
        }

        if (!workPlaceAdded) {
            /*mSpeciality.setText("Some Error");*/
        } else {
            if (sp != null) {
                int count = sp.getServiceCount();
                if (count > 0) {
                    String speciality = sps.getService().getSpeciality();
                    String exp = "" + sps.getExperience();
                    SpinnerAdapter sa = mSpeciality.getAdapter();
                    for (int i = 0; i < sa.getCount(); i++) {
                        if (speciality.equalsIgnoreCase(sa.getItem(i).toString())) {
                            mSpeciality.setSelection(i);
                        }
                    }
                    mExperience.setText(exp);
                }
            }
        }
    }

    public void saveData(View view) {
        UIUtility.showProgress(this, mFormView, mProgressView, true);
        Intent intent = new Intent(this, MappService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);

/*
        SaveServiceData task = new SaveServiceData(this);
        task.execute((Void) null);
*/
    }

    private void signUpDone(Bundle data) {
        if (data.getBoolean("status")) {
            UIUtility.showRegistrationAlert(this, "Thanks You..!", "You have successfully registered.\nLogin to your account.");
        }
        UIUtility.showProgress(this, mFormView, mProgressView, false);
        unbindService(mConnection);
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        private Messenger mService;
        private boolean mBound;

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            mService = new Messenger(service);
            mBound = true;
            Bundle bundle = new Bundle();
            bundle.putInt("loginType", MappService.SERVICE_LOGIN);
            bundle.putParcelable("service", LoginHolder.servLoginRef);
            Message msg = Message.obtain(null, MappService.DO_LOGIN);
            msg.replyTo = new Messenger(mResponseHandler);
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
            mBound = false;
        }
    };

/*
    class SaveServiceData extends AsyncTask<Void, Void, Void> {

        private Activity myActivity;

        public SaveServiceData(Activity activity) {
            myActivity = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ServiceProvider sp = LoginHolder.servLoginRef;
            ArrayList<ServProvHasServPt> spsList = sp.getServices();

            if (spsList == null) {
                return null;
            }
            MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(MappContract.ServiceProvider.COLUMN_NAME_CELLPHONE, sp.getPhone());
            values.put(MappContract.ServiceProvider.COLUMN_NAME_FNAME, sp.getfName());
            values.put(MappContract.ServiceProvider.COLUMN_NAME_LNAME, sp.getlName());
            values.put(MappContract.ServiceProvider.COLUMN_NAME_PASSWD, sp.getPasswd());
            values.put(MappContract.ServiceProvider.COLUMN_NAME_QUALIFICATION, sp.getQualification());
            values.put(MappContract.ServiceProvider.COLUMN_NAME_GENDER, sp.getGender());
            values.put(MappContract.ServiceProvider.COLUMN_NAME_REGISTRATION_NUMBER, sp.getRegNo());

            long spId = db.insert(MappContract.ServiceProvider.TABLE_NAME, null, values);

            //SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getTimeInstance();

            for (ServProvHasServPt sps : spsList) {
                values = new ContentValues();
                values.put(MappContract.ServProvHasServ.COLUMN_NAME_ID_SERV_PROV, spId);
                values.put(MappContract.ServProvHasServ.COLUMN_NAME_SPECIALITY, sps.getService());
                values.put(MappContract.ServProvHasServ.COLUMN_NAME_SERVICE_CATAGORY, sps.getService());
                values.put(MappContract.ServProvHasServ.COLUMN_NAME_EXPERIENCE, sps.getExperience());

                long spsId = db.insert(MappContract.ServProvHasServ.TABLE_NAME, null, values);

                ArrayList<ServProvHasServPt> spssptList = sp.getServices();
                for (ServProvHasServPt spsspt : spssptList) {
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
                    values.put(MappContract.ServProvHasServHasServPt.COLUMN_NAME_SERVICE_POINT_TYPE, spsspt.getServPointType());
                    values.put(MappContract.ServProvHasServHasServPt.COLUMN_NAME_CONSULTATION_FEE, spsspt.getConsultFee());
                    values.put(MappContract.ServProvHasServHasServPt.COLUMN_NAME_WEEKLY_OFF, spsspt.getWeeklyOff());

                    db.insert(MappContract.ServProvHasServHasServPt.TABLE_NAME, null, values);

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            UIUtility.showRegistrationAlert(myActivity, "Thanks You..!", "You have successfully registered.\nLogin to your account.");
            UIUtility.showProgress(myActivity, mFormView, mProgressView, false);
            //Intent intent = new Intent(myActivity, LoginActivity.class);
            //startActivity(intent);
        }

        @Override
        protected void onCancelled() {
            UIUtility.showProgress(myActivity, mFormView, mProgressView, false);
        }
    }
*/

    private static class SignUpHandler extends Handler {
        private AddWorkPlaceActivity mActivity;

        public SignUpHandler(AddWorkPlaceActivity activity) {
            mActivity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MappService.DO_SIGNUP:
                    mActivity.signUpDone(msg.getData());
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

}
