package com.extenprise.mapp.customer.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.TimePicker;

import com.extenprise.mapp.R;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.MappServiceConnection;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.data.SearchServProvForm;
import com.extenprise.mapp.util.Utility;

import java.util.ArrayList;
import java.util.Calendar;

public class AdvSearchServProvActivity extends Activity implements ResponseHandler {

    protected CharSequence[] options;
    protected boolean[] selections;
    ArrayList<String> specList;
    String selectedDays;
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

        specList = getIntent().getStringArrayListExtra("specList");
        if (specList == null) {
            specList = new ArrayList<>();
        }
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(this, R.layout.layout_spinner, specList);
        mSpeciality.setAdapter(spinnerAdapter);

        mMultiSpinnerDays = (Button) findViewById(R.id.spinAvailDays);
        mMultiSpinnerDays.setOnClickListener(new ButtonClickHandler());

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
                getSpecialityList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    public void showtimeFields(View view) {
        if (mServProLay3.getVisibility() == View.VISIBLE) {
            Utility.collapse(mServProLay3, view);
        } else {
            Utility.expand(mServProLay3, view);
        }
    }

    public void showGenderField(View view) {
        if (mGender.getVisibility() == View.VISIBLE) {
            Utility.collapse(mGender, view);
            view.setBackgroundResource(R.drawable.label);
        } else {
            Utility.expand(mGender, view);
            view.setBackgroundResource(R.drawable.expand);
        }
    }

    public void showDaysField(View view) {
        if (mMultiSpinnerDays.getVisibility() == View.VISIBLE) {
            Utility.collapse(mMultiSpinnerDays, view);
        } else {
            Utility.expand(mMultiSpinnerDays, view);
        }
    }




    /*private void setSpecs(ArrayList<String> specs) {
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(this, R.layout.layout_spinner, specs);
        mSpeciality.setAdapter(spinnerAdapter);
    }*/

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

        View focusView;

        String name = mDrClinicName.getText().toString().trim();
        String loc = mLocation.getText().toString().trim();
        String sp = "";
        if (mSpeciality.getSelectedItem() != null) {
            sp = mSpeciality.getSelectedItem().toString();
        }
        if (sp.equals("Select Speciality") || sp.equals("Other")) {
            sp = "";
        }
        String sc = mServProvCategory.getSelectedItem().toString();
        if (sc.equals("Select Category")) {
            sc = "";
        }

        String clinic = name;

        String qualification = mQualification.getText().toString().trim();
        String gender = mGender.getSelectedItem().toString();
        if (gender.equals("Both")) {
            gender = "";
        }
        String exp = mExperience.getText().toString().trim();
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
                focusView.requestFocus();
                return;
            }
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

    private void getSpecialityList() {
        String selectedCategory = mServProvCategory.getSelectedItem().toString();
        if (selectedCategory.equalsIgnoreCase(getString(R.string.select_category))) {
            return;
        }
        mForm = new SearchServProvForm();
        mForm.setCategory(selectedCategory);
        Bundle bundle = new Bundle();
        bundle.putParcelable("form", mForm);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_GET_SPECIALITY);
        if (Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE)) {
            Utility.showProgress(this, mSearchFormView, mProgressView, true);
        }
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
/*
    private ServiceConnection mConnection = new ServiceConnection() {

        private Messenger mService;

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            mService = new Messenger(service);
            Bundle bundle = new Bundle();
            if(mAction == MappService.DO_GET_SPECIALITY) {
                mForm = new SearchServProvForm();
                mForm.setCategory(mServProvCategory.getSelectedItem().toString());
            }
            bundle.putParcelable("form", mForm);
            Message msg = Message.obtain(null, mAction);
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
        }
    };
*/
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
            Utility.showMessage(this, R.string.no_result);
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
}
