package com.extenprise.mapp.service.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.activity.LoginActivity;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.MappServiceConnection;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.data.SearchServProvForm;
import com.extenprise.mapp.service.data.ServProvHasServPt;
import com.extenprise.mapp.service.data.ServicePoint;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.ui.TitleFragment;
import com.extenprise.mapp.util.Utility;
import com.extenprise.mapp.util.Validator;

import java.util.ArrayList;

public class ServProvWorkPlaceFragment extends Fragment implements TitleFragment, ResponseHandler {
    protected CharSequence[] options;
    protected boolean[] selections;
    //String []selectedDays = new String[_options.length];
    String selectedDays;
    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(getActivity(), this));
    private View mRootview;
    private EditText mName;
    private EditText mLoc;
    private Spinner mCity;
    private Spinner mState;
    private EditText mPhone1;
    private EditText mPhone2;
    private EditText mEmailId;
    private Spinner mSpeciality;
    private EditText mExperience;
    private EditText mQualification;
    private Button mStartTime;
    private Button mEndTime;
    private EditText mPinCode;
    //private Spinner mGender;
    private EditText mConsultFee;
    //private Spinner mWeeklyOff;
    private Spinner mServPtType;
    private Spinner mServCatagory;
    private View mFormView;
    private View mProgressView;
    private RelativeLayout mRelLayout2;
    private LinearLayout mLayoutWorkHrs;
    private Button mMultiSpinnerDays;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootview = inflater.inflate(R.layout.activity_add_workplace, container, false);
        LoginHolder.spsspt = new ServProvHasServPt();

        options = Utility.getDaysOptions(getActivity());
        selections = new boolean[options.length];

        mFormView = mRootview.findViewById(R.id.addWorkPlaceForm);
        mProgressView = mRootview.findViewById(R.id.progressView);
        mRelLayout2 = (RelativeLayout) mRootview.findViewById(R.id.relLayout2);
        mLayoutWorkHrs = (LinearLayout) mRootview.findViewById(R.id.layoutWorkHrs);

        mName = (EditText) mRootview.findViewById(R.id.editTextName);
        mLoc = (EditText) mRootview.findViewById(R.id.editTextLoc);
        mCity = (Spinner) mRootview.findViewById(R.id.editTextCity);
        mState = (Spinner) mRootview.findViewById(R.id.editTextState);
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
        mPinCode = (EditText) mRootview.findViewById(R.id.editTextPinCode);
        mQualification = (EditText) mRootview.findViewById(R.id.editTextQualification);
        mMultiSpinnerDays = (Button) mRootview.findViewById(R.id.editTextWeeklyOff);
        mServCatagory = (Spinner) mRootview.findViewById(R.id.spinServiceProvCategory);

        mServCatagory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String servCategory = mServCatagory.getSelectedItem().toString();
                if(!TextUtils.isEmpty(servCategory) && !servCategory.equals(getString(R.string.select_category))) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("loginType", MappService.SERVICE_LOGIN);
                    SearchServProvForm mForm = new SearchServProvForm();
                    mForm.setCategory(mServCatagory.getSelectedItem().toString());
                    bundle.putParcelable("form", mForm);
                    mConnection.setData(bundle);
                    mConnection.setAction(MappService.DO_GET_SPECIALITY);
                    if (Utility.doServiceAction(getActivity(), mConnection, Context.BIND_AUTO_CREATE)) {
                        Utility.showProgress(getActivity(), mFormView, mProgressView, true);
                    }
                }
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
                    openSpecDialog();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        mMultiSpinnerDays.setOnClickListener(new ButtonClickHandler());

        int category = getActivity().getIntent().getIntExtra("category", R.string.practitionar);
        if (category == R.string.medicalStore) {
            mConsultFee.setEnabled(false);
        }

        //collapse fields on create.
        Utility.collapse(mLayoutWorkHrs, null);
        Utility.collapse(mRelLayout2, null);

        return mRootview;
    }

    private void openSpecDialog() {
        Utility.openSpecDialog(getActivity(), mSpeciality);
    }

    public void showStartTimePicker(View view) {
        Utility.timePicker(view, mStartTime);
    }

    public void showEndTimePicker(View view) {
        Utility.timePicker(view, mEndTime);
    }

    public void showtimeFields(View view) {
        Log.v(this.getClass().getName(), "view: " + view.toString() + "workhrsLayout: " + mLayoutWorkHrs);
        if (mLayoutWorkHrs.getVisibility() == View.VISIBLE) {
            Utility.collapse(mLayoutWorkHrs, null);
        } else {
            Utility.expand(mLayoutWorkHrs, null);
        }
    }

    public void showFeeFields(View view) {
        Log.v(this.getClass().getName(), "view: " + view.toString() + "consult Fee: " + mConsultFee);
        TextView rupeeSign = (TextView) mRootview.findViewById(R.id.viewRsSign);
        if (mConsultFee.getVisibility() == View.VISIBLE) {
            //mConsultFee.setVisibility(View.GONE);
            rupeeSign.setVisibility(View.GONE);
            Utility.collapse(mConsultFee, null);
        } else {
            //mConsultFee.setVisibility(View.VISIBLE);
            rupeeSign.setVisibility(View.VISIBLE);
            Utility.expand(mConsultFee, null);
        }
    }

    public void showDaysFields(View view) {
        Log.v(this.getClass().getName(), "view: " + view.toString() + "Days: " + mMultiSpinnerDays);
        if (mMultiSpinnerDays.getVisibility() == View.VISIBLE) {
            //mMultiSpinnerDays.setVisibility(View.GONE);
            Utility.collapse(mMultiSpinnerDays, null);
        } else {
            //UIUtility.expandOrCollapse(mMultiSpinnerDays, "expand");
            //mMultiSpinnerDays.setVisibility(View.VISIBLE);
            Utility.expand(mMultiSpinnerDays, null);
        }
    }

    public void showWorkFields(View view) {
        if (mRelLayout2.getVisibility() == View.VISIBLE) {
            //mRelLayout2.setVisibility(View.GONE);
            Utility.collapse(mRelLayout2, null);
        } else {
            Utility.expand(mRelLayout2, null);
            //mRelLayout2.setVisibility(View.VISIBLE);
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
        spt.getCity().setCity(mCity.getSelectedItem().toString());
        spt.getCity().setState(mState.getSelectedItem().toString());
        spt.setPhone(mPhone1.getText().toString().trim());
        spt.setAltPhone(mPhone2.getText().toString().trim());
        spt.setEmailId(mEmailId.getText().toString().trim());
        spt.setPincode(mPinCode.getText().toString().trim());

        spsspt.getService().setSpeciality(mSpeciality.getSelectedItem().toString());
        spsspt.getService().setCategory(mServCatagory.getSelectedItem().toString());
        spsspt.setServProvPhone(LoginHolder.servLoginRef.getPhone());
        spsspt.setExperience(Float.parseFloat(mExperience.getText().toString().trim()));
        spsspt.setServPointType(mServPtType.getSelectedItem().toString());
        spsspt.setStartTime(Utility.getMinutes(mStartTime.getText().toString()));
        spsspt.setEndTime(Utility.getMinutes(mEndTime.getText().toString()));
        spsspt.setWorkingDays(mMultiSpinnerDays.getText().toString());
        if (mConsultFee.isEnabled()) {
            spsspt.setConsultFee(Float.parseFloat(mConsultFee.getText().toString().trim()));
        }
        spsspt.setServicePoint(spt);

        LoginHolder.servLoginRef.addServProvHasServPt(spsspt);

        clearWorkPlace();
        int count = LoginHolder.servLoginRef.getServiceCount() + 1;
        TextView countView = (TextView) mRootview.findViewById(R.id.viewWorkPlaceCount);
        countView.setText(String.format("#%d", count));
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

        String pincode = mPinCode.getText().toString().trim();
        if (TextUtils.isEmpty(pincode)) {
            mPinCode.setError(getString(R.string.error_field_required));
            focusView = mPinCode;
            valid = false;
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
        if (!TextUtils.isEmpty(email) && !Validator.isValidEmaillId(mEmailId.getText().toString())) {
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
            if (Utility.getMinutes(mStartTime.getText().toString()) + 60 >= Utility.getMinutes(mEndTime.getText().toString())) {
                mEndTime.setError(getString(R.string.error_invalid_endtime));
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

        if (mConsultFee.isEnabled()) {
            String cosultFee = mConsultFee.getText().toString().trim();
            if (TextUtils.isEmpty(cosultFee)) {
                mConsultFee.setError(getString(R.string.error_field_required));
                focusView = mConsultFee;
                valid = false;
            }
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
        mState.setSelected(false);
        mPhone1.setText("");
        mPhone2.setText("");
        mPinCode.setText("");
        mEmailId.setText("");
        mStartTime.setText(R.string.start_time);
        mEndTime.setText(R.string.end_time);
        //mWeeklyOff.setSelected(false);
        mServPtType.setSelected(false);
        mConsultFee.setText("");
        mMultiSpinnerDays.setText(getString(R.string.select_days));
    }

    public void registerDone(View view) {
        if (addNewWorkPlace()) {
            ServProvSignUpActivity activity = (ServProvSignUpActivity) getActivity();
            activity.saveData();
        }
    }

    public void saveData() {
        Bundle bundle = new Bundle();
        bundle.putInt("loginType", MappService.SERVICE_LOGIN);
        bundle.putParcelable("service", LoginHolder.servLoginRef);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_SIGNUP);
        if (Utility.doServiceAction(getActivity(), mConnection, Context.BIND_AUTO_CREATE)) {
            Utility.showProgress(getActivity(), mFormView, mProgressView, true);
        }
/*
        SaveServiceData task = new SaveServiceData(this);
        task.execute((Void) null);
*/
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        try {
            getActivity().unbindService(mConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (action == MappService.DO_SIGNUP) {
            signUpDone(data);
            return true;
        } else if (action == MappService.DO_GET_SPECIALITY) {
            getSpecialitiesDone(data);
            return true;
        }
        return false;
    }

    private void signUpDone(Bundle data) {
        if (data.getBoolean("status")) {
            Utility.showAlert(getActivity(), "", getString(R.string.msg_registration_done), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            });
            //Utility.showAlert(this, "Thanks You..!", "You have successfully registered.\nLogin to your account.");
        } else {
            Utility.showMessage(getActivity(), R.string.some_error);
        }
        Utility.showProgress(getActivity(), mFormView, mProgressView, false);
    }

    private void getSpecialitiesDone(Bundle data) {
        Utility.showProgress(getActivity(), mFormView, mProgressView, false);
        ArrayList<String> list = data.getStringArrayList("specialities");
        if (list == null) {
            list = new ArrayList<>();
        }
        Utility.setNewSpec(getActivity(), list, mSpeciality);
    }

    public class ButtonClickHandler implements View.OnClickListener {
        public void onClick(View view) {
            if (!mMultiSpinnerDays.getText().equals(getString(R.string.select_days))) {
                setupSelection();
            }
            getActivity().showDialog(0);
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

/*
    */
/**
 * Defines callbacks for service binding, passed to bindService()
 *//*

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
*/
}
