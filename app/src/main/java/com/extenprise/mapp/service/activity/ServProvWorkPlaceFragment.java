package com.extenprise.mapp.service.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.data.ServProvHasServPt;
import com.extenprise.mapp.service.data.ServicePoint;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.ui.TitleFragment;
import com.extenprise.mapp.util.DBUtil;
import com.extenprise.mapp.util.Utility;
import com.extenprise.mapp.util.Validator;

import java.util.ArrayList;

/**
 * Created by ambey on 10/9/15.
 */
public class ServProvWorkPlaceFragment extends Fragment implements TitleFragment, ResponseHandler {
    private ServiceResponseHandler mResponseHandler = new ServiceResponseHandler(this);

    private View mRootview;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootview = inflater.inflate(R.layout.activity_add_workplace, container, false);
        Bundle args = getArguments();
        LoginHolder.spsspt = new ServProvHasServPt();

        mFormView = mRootview.findViewById(R.id.addWorkPlaceForm);
        mProgressView = mRootview.findViewById(R.id.progressView);

        mName = (EditText) mRootview.findViewById(R.id.editTextName);
        mLoc = (EditText) mRootview.findViewById(R.id.editTextLoc);
        mCity = (Spinner) mRootview.findViewById(R.id.editTextCity);
        mPhone1 = (EditText) mRootview.findViewById(R.id.editTextPhone1);
        mPhone2 = (EditText) mRootview.findViewById(R.id.editTextPhone2);
        mEmailId = (EditText) mRootview.findViewById(R.id.editTextEmail);
        mStartTime = (Button) mRootview.findViewById(R.id.buttonStartTime);
        mEndTime = (Button) mRootview.findViewById(R.id.buttonEndTime);
        //mGender = (Spinner) rootView.findViewById(R.id.spinGender);
        mConsultFee = (EditText) mRootview.findViewById(R.id.editTextConsultationFees);
        mServPtType = (Spinner) mRootview.findViewById(R.id.viewWorkPlaceType);
        mSpeciality = (Spinner) mRootview.findViewById(R.id.editTextSpeciality);
        mExperience = (EditText) mRootview.findViewById(R.id.editTextExperience);
        mQualification = (EditText) mRootview.findViewById(R.id.editTextQualification);
        mMultiSpinnerDays = (Button) mRootview.findViewById(R.id.editTextWeeklyOff);
        mServCatagory = (Spinner) mRootview.findViewById(R.id.spinServiceProvCategory);

        mServCatagory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String servCategory = mServCatagory.getSelectedItem().toString();
                MappDbHelper dbHelper = new MappDbHelper(getActivity());
                DBUtil.setSpecOfCategory(getActivity(), dbHelper, servCategory, mSpeciality);
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
        return mRootview;
    }

    private AlertDialog openDialog() {
        final EditText txtSpec = new EditText(getActivity());
        txtSpec.setHint("Add Speciality");

        return new AlertDialog.Builder(getActivity())
                .setTitle("Add Speciality")
                .setView(txtSpec)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newSpec = txtSpec.getText().toString();
                        ArrayList<String> specs = new ArrayList<>();
                        specs.add(newSpec);
                        DBUtil.setNewSpec(getActivity(), specs, mSpeciality);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void showStartTimePicker(View view) {
        Utility.timePicker(view, mStartTime);
    }

    public void showEndTimePicker(View view) {
        Utility.timePicker(view, mEndTime);
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
        TextView rupeeSign = (TextView) mRootview.findViewById(R.id.viewRsSign);
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
    public CharSequence getPageTitle() {
        return getString(R.string.work_place_details);
    }

    @Override
    public int getPageIconResId() {
        return 0;
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        getActivity().unbindService(mConnection);
        if (action == MappService.DO_SIGNUP) {
            signUpDone(data);
            return true;
        }
        return false;
    }

    public class ButtonClickHandler implements View.OnClickListener {
        public void onClick(View view) {
            if (!mMultiSpinnerDays.getText().equals(getString(R.string.select_days))) {
                setupSelection();
            }
            getActivity().showDialog(0);
        }
    }

    public void onPrepareDialog(int id, Dialog dialog) {
        if (!mMultiSpinnerDays.getText().equals(getString(R.string.select_days))) {
            setupSelection();
        }
    }

    public Dialog onCreateDialog(int id) {
        return new AlertDialog.Builder(getActivity())
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

    public void addNewWorkPlace(View view) {
        addNewWorkPlace();
    }

    private boolean addNewWorkPlace() {
        ServProvSignUpActivity activity = (ServProvSignUpActivity) getActivity();
        if (!activity.isValidInput()) {
            return false;
        }
        if (LoginHolder.servLoginRef == null) {
            LoginHolder.servLoginRef = new ServiceProvider();
        }
        LoginHolder.servLoginRef.setQualification(mQualification.getText().toString().trim());

        ServicePoint spt = new ServicePoint();
        ServProvHasServPt spsspt = new ServProvHasServPt();

        spt.setName(mName.getText().toString().trim());
        spt.setLocation(mLoc.getText().toString().trim());
        spt.getCity().setCity(mCity.getSelectedItem().toString().trim());
        spt.setPhone(mPhone1.getText().toString().trim());
        spt.setAltPhone(mPhone2.getText().toString().trim());
        spt.setEmailId(mEmailId.getText().toString().trim());

        spsspt.getService().setSpeciality(mSpeciality.getSelectedItem().toString());
        spsspt.getService().setCategory(mServCatagory.getSelectedItem().toString());
        spsspt.setServProvPhone(LoginHolder.servLoginRef.getPhone());
        spsspt.setExperience(Float.parseFloat(mExperience.getText().toString().trim()));
        spsspt.setServPointType(mServPtType.getSelectedItem().toString());
        spsspt.setStartTime(Utility.getMinutes(mStartTime.getText().toString()));
        spsspt.setEndTime(Utility.getMinutes(mEndTime.getText().toString()));
        spsspt.setWorkingDays(mMultiSpinnerDays.getText().toString());
        spsspt.setConsultFee(Float.parseFloat(mConsultFee.getText().toString().trim()));
        spsspt.setServicePoint(spt);

        LoginHolder.servLoginRef.addServProvHasServPt(spsspt);

        clearWorkPlace();
        int count = LoginHolder.servLoginRef.getServiceCount() + 1;
        TextView countView = (TextView) mRootview.findViewById(R.id.viewWorkPlaceCount);
        countView.setText("#" + count);
        return true;
    }

    public boolean isValidInput() {
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
            if (Utility.getMinutes(mStartTime.getText().toString()) >= Utility.getMinutes(mEndTime.getText().toString())) {
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

    public void registerDone(View view) {
        if (addNewWorkPlace()) {
            ServProvSignUpActivity activity = (ServProvSignUpActivity) getActivity();
            activity.saveData();
        }
    }

    public void saveData() {
        Utility.showProgress(getActivity(), mFormView, mProgressView, true);
        Intent intent = new Intent(getActivity(), MappService.class);
        getActivity().bindService(intent, mConnection, getActivity().BIND_AUTO_CREATE);

/*
        SaveServiceData task = new SaveServiceData(this);
        task.execute((Void) null);
*/
    }

    private void signUpDone(Bundle data) {
        if (data.getBoolean("status")) {
            Utility.showRegistrationAlert(getActivity(), "Thanks You..!", "You have successfully registered.\nLogin to your account.");
        }
        Utility.showProgress(getActivity(), mFormView, mProgressView, false);
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
            Message msg = Message.obtain(null, MappService.DO_SIGNUP);
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

}
