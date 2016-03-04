package com.extenprise.mapp.medico.service.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.activity.LoginActivity;
import com.extenprise.mapp.medico.data.WorkingDataStore;
import com.extenprise.mapp.medico.net.MappService;
import com.extenprise.mapp.medico.net.MappServiceConnection;
import com.extenprise.mapp.medico.net.ResponseHandler;
import com.extenprise.mapp.medico.net.ServiceResponseHandler;
import com.extenprise.mapp.medico.service.data.SearchServProvForm;
import com.extenprise.mapp.medico.service.data.ServProvHasServPt;
import com.extenprise.mapp.medico.service.data.ServicePoint;
import com.extenprise.mapp.medico.service.data.ServiceProvider;
import com.extenprise.mapp.medico.ui.DaysSelectionDialog;
import com.extenprise.mapp.medico.ui.DialogDismissListener;
import com.extenprise.mapp.medico.ui.TitleFragment;
import com.extenprise.mapp.medico.util.Utility;
import com.extenprise.mapp.medico.util.Validator;

import java.util.ArrayList;

public class ServProvWorkPlaceFragment extends Fragment implements TitleFragment, ResponseHandler, DialogDismissListener {
    protected CharSequence[] options;
    protected boolean[] selections;
    //String []selectedDays = new String[_options.length];
    //String selectedDays;
    private MappServiceConnection mConnection;
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
    private EditText mNotes;
    //private Spinner mWeeklyOff;
    private Spinner mServPtType;
    private Spinner mServCatagory;
    //private View mFormView;
    //private View mProgressView;
    private RelativeLayout mRelLayout2;
    private LinearLayout mLayoutWorkHrs;
    private Button mMultiSpinnerDays;

    private ArrayList<String> mSpecialityList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootview = inflater.inflate(R.layout.activity_add_workplace, container, false);
        mConnection = new MappServiceConnection(new ServiceResponseHandler(getActivity(), this));
        //LoginHolder.spsspt = new ServProvHasServPt();

        options = Utility.getDaysOptions(getActivity());
        selections = new boolean[options.length];
        mSpecialityList = new ArrayList<>();

        /*mFormView = mRootview.findViewById(R.id.addWorkPlaceForm);
        mProgressView = mRootview.findViewById(R.id.progressView);*/
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
        mNotes = (EditText) mRootview.findViewById(R.id.editTextNotes);
        mConsultFee = (EditText) mRootview.findViewById(R.id.editTextConsultationFees);
        mConsultFee.setText("0");
        mServPtType = (Spinner) mRootview.findViewById(R.id.viewWorkPlaceType);
        mSpeciality = (Spinner) mRootview.findViewById(R.id.editTextSpeciality);
        mExperience = (EditText) mRootview.findViewById(R.id.editTextExperience);
        mPinCode = (EditText) mRootview.findViewById(R.id.editTextPinCode);
        mQualification = (EditText) mRootview.findViewById(R.id.editTextQualification);
        mMultiSpinnerDays = (Button) mRootview.findViewById(R.id.editTextWeeklyOff);
        mServCatagory = (Spinner) mRootview.findViewById(R.id.spinServiceProvCategory);

        int category = getActivity().getIntent().getIntExtra("category", R.string.physician);
        /*mServCatagory.setSelection(Utility.getSpinnerIndex(mServCatagory, getString(category)));
        mServCatagory.setClickable(false);*/

        ArrayList<String> listWPType = new ArrayList<>();
        if (category == R.string.physician) {
            listWPType.add(getString(R.string.clinic));
        } else if (category == R.string.pharmacist) {
            mConsultFee.setEnabled(false);
            listWPType.add(getString(R.string.medical_store));
        } else {
            listWPType.add(getString(R.string.path_lab));
            listWPType.add(getString(R.string.scan_lab));
        }

        Utility.setNewSpinner(getActivity(), listWPType, mServPtType, null);
        Utility.setNewSpinner(getActivity(), null, mServCatagory,
                new String[]{getString(R.string.select_category), getString(category)});
        Utility.setNewSpinner(getActivity(), null, mSpeciality,
                new String[]{getString(R.string.select_speciality)});

        mServCatagory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String servCategory = mServCatagory.getSelectedItem().toString();
                if (!TextUtils.isEmpty(servCategory) && !servCategory.equals(getString(R.string.select_category))) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("loginType", MappService.SERVICE_LOGIN);
                    SearchServProvForm mForm = new SearchServProvForm();
                    mForm.setCategory(mServCatagory.getSelectedItem().toString());
                    bundle.putParcelable("form", mForm);
                    mConnection.setData(bundle);
                    mConnection.setAction(MappService.DO_GET_SPECIALITY);
                    if (Utility.doServiceAction(getActivity(), mConnection, Context.BIND_AUTO_CREATE)) {
                        //Utility.showProgress(getActivity(), mFormView, mProgressView, true);
                        Utility.showProgressDialog(getActivity(), true);
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
                if (spec.equals(getString(R.string.other))) {
                    final EditText txtSpec = new EditText(getActivity());
                    txtSpec.setHint(getString(R.string.speciality));
                    final AlertDialog dialog = Utility.customDialogBuilder(getActivity(), txtSpec, R.string.add_new_spec).create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).
                            setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String txt = txtSpec.getText().toString().trim();
                                    boolean cancel = false;
                                    if (TextUtils.isEmpty(txt)) {
                                        txtSpec.setError(getString(R.string.error_field_required));
                                        txtSpec.requestFocus();
                                        cancel = true;
                                    }
                                    if (!Validator.isOnlyAlpha(txt)) {
                                        txtSpec.setError(getString(R.string.error_only_alpha));
                                        txtSpec.requestFocus();
                                        cancel = true;
                                    }
                                    if (!cancel) {
                                        mSpecialityList.add(0, txt);
                                        if (!mSpecialityList.contains(getString(R.string.other))) {
                                            mSpecialityList.add(getString(R.string.other));
                                        }
                                        Utility.setNewSpinner(getActivity(), mSpecialityList, mSpeciality, null);
                                        dialog.dismiss();
                                    }
                                }
                            });
                    //Utility.openSpecDialog(getActivity(), mSpeciality);

                   /*
                   This is not allowing the validation to be done.. as its closing the dialog even when returning.

                   Utility.showAlert(getActivity(), getString(R.string.add_new_spec), "", txtSpec, true, null,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which == DialogInterface.BUTTON_POSITIVE) {

                                    }
                                }
                            });*/
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
        mMultiSpinnerDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDaysSelectionDialog(v);
            }
        });
        //mMultiSpinnerDays.setOnClickListener(new ButtonClickHandler());

        //collapse fields on create.
        /*Utility.collapse(mLayoutWorkHrs, null);
        Utility.collapse(mRelLayout2, null);*/

        Utility.collapseExpand(mLayoutWorkHrs);
        Utility.collapseExpand(mRelLayout2);

        return mRootview;
    }

    public void showStartTimePicker(View view) {
        Utility.timePicker(view, mStartTime);
    }

    public void showEndTimePicker(View view) {
        Utility.timePicker(view, mEndTime);
    }

    public void showtimeFields(View view) {
        Log.v(this.getClass().getName(), "view: " + view.toString() + "workhrsLayout: " + mLayoutWorkHrs);
        /*if (mLayoutWorkHrs.getVisibility() == View.VISIBLE) {
            Utility.collapse(mLayoutWorkHrs, null);
        } else {
            Utility.expand(mLayoutWorkHrs, null);
        }*/

        Utility.collapseExpand(mLayoutWorkHrs);
    }

   /* public void showFeeFields(View view) {
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
    }*/

    public void showWorkFields(View view) {
        /*if (mRelLayout2.getVisibility() == View.VISIBLE) {
            //mRelLayout2.setVisibility(View.GONE);
            Utility.collapse(mRelLayout2, null);
        } else {
            Utility.expand(mRelLayout2, null);
            //mRelLayout2.setVisibility(View.VISIBLE);
        }*/
        Utility.collapseExpand(mRelLayout2);
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
    public void onDialogDismissed(DialogFragment dialog) {
        DaysSelectionDialog selectionDialog = (DaysSelectionDialog) dialog;
        String selectedDays = selectionDialog.getSelectedDays();
        /*String[] days = selectionDialog.getSelectedDays().split(getString(R.string.comma));
        if(days.length > 2) {
            selectedDays = days[0] + getString(R.string.comma) + " " + days[1] + " ...";
        }*/
        mMultiSpinnerDays.setText(selectedDays);
    }

    @Override
    public void onApplyDone(DialogFragment dialog) {

    }

    @Override
    public void onCancelDone(DialogFragment dialog) {

    }

    public void showDaysSelectionDialog(View view) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        //fragmentManager.saveFragmentInstanceState(this);
        String selctedDays = "";
        if (!mMultiSpinnerDays.getText().toString().equals(getString(R.string.select_days))) {
            selctedDays = mMultiSpinnerDays.getText().toString();
        }
        DaysSelectionDialog dialog = new DaysSelectionDialog();
        dialog.setSelectedDays(selctedDays);
        dialog.setListener(this);
        dialog.show(fragmentManager, "DaysSelect");
    }

    public void addNewWorkPlace(View view) {
        addNewWorkPlace();
    }

    private boolean addNewWorkPlace() {

        ServProvSignUpActivity activity = (ServProvSignUpActivity) getActivity();
        if (!activity.isValidInput()) {
            return false;
        }
        ServiceProvider serviceProvider = WorkingDataStore.getBundle().getParcelable("servProv");
        if (serviceProvider == null) {
            serviceProvider = new ServiceProvider();
        }
        serviceProvider.setQualification(mQualification.getText().toString().trim());

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
        spsspt.setServProvPhone(serviceProvider.getPhone());
        spsspt.setExperience(Float.parseFloat(mExperience.getText().toString().trim()));
        spsspt.setServPointType(mServPtType.getSelectedItem().toString());
        spsspt.setStartTime(Utility.getMinutes(mStartTime.getText().toString()));
        spsspt.setEndTime(Utility.getMinutes(mEndTime.getText().toString()));
        spsspt.setWorkingDays(mMultiSpinnerDays.getText().toString());
        if (mConsultFee.isEnabled()) {
            String fees = mConsultFee.getText().toString().trim();
            float fee = 0;
            if (!TextUtils.isEmpty(fees)) {
                fee = Float.parseFloat(fees);
            }
            spsspt.setConsultFee(fee);
        }
        spsspt.setNotes(mNotes.getText().toString().trim());
        spsspt.setServicePoint(spt);

        serviceProvider.addServProvHasServPt(spsspt);

        clearWorkPlace();
        int count = serviceProvider.getServiceCount() + 1;
        TextView countView = (TextView) mRootview.findViewById(R.id.viewWorkPlaceCount);
        countView.setText(String.format("#%d", count));
        WorkingDataStore.getBundle().putParcelable("servProv", serviceProvider);
        return true;
    }

    public boolean isValidInput() {
        EditText[] fields = {mExperience, mQualification,
                mName, mLoc, mPinCode, mPhone1};
/*        if(mServCatagory.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.pharmacist))) {
            fields = new EditText[] { mExperience, mQualification,
                    mName, mLoc, mPinCode, mPhone1 };
        }*/
        if (Utility.areEditFieldsEmpty(getActivity(), fields)) {
            return false;
        }

        boolean valid = true;
        View focusView = null;

        if (mConsultFee.isEnabled()) {
            String fees = mConsultFee.getText().toString().trim();
            if (!TextUtils.isEmpty(fees)) {
                try {
                    float v = Float.parseFloat(fees);
                } catch (NumberFormatException n) {
                    mConsultFee.setError(getString(R.string.error_only_digit));
                    valid = false;
                    focusView = mConsultFee;
                }
            }
        }

        String category = mServCatagory.getSelectedItem().toString();
        if (category.equalsIgnoreCase(getString(R.string.select_category))) {
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
        if (spec.equalsIgnoreCase(getString(R.string.select_speciality)) ||
                spec.equals(getString(R.string.other))) {
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

        String phone1 = mPhone1.getText().toString().trim();
        if (!Validator.isPhoneValid(phone1)) {
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

        if (Validator.isPinCodeValid(mPinCode.getText().toString().trim())) {
            mPinCode.setError(getString(R.string.error_invalid_pincode));
            focusView = mPinCode;
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
        if (days.equalsIgnoreCase(getString(R.string.practice_days))) {
            mMultiSpinnerDays.setError(getString(R.string.error_field_required));
            focusView = mMultiSpinnerDays;
            valid = false;
        }

        ServiceProvider serviceProvider = WorkingDataStore.getBundle().getParcelable("servProv");
        if (serviceProvider != null) {
            for (int i = 0; i < serviceProvider.getServiceCount(); i++) {
                ServProvHasServPt spspt = serviceProvider.getServProvHasServPt(i);
                if (spspt == null) {
                    continue;
                }
                String[] workdays = spspt.getWorkingDays().split(getString(R.string.comma));
                for (String workday : workdays) {
                    String[] workdays2 = days.split(getString(R.string.comma));
                    for (String aWorkdays2 : workdays2) {
                        if (workday.equals(aWorkdays2)) {
                            int st1 = spspt.getStartTime();
                            int en1 = spspt.getEndTime();
                            int st2 = Utility.getMinutes(mStartTime.getText().toString());
                            int en2 = Utility.getMinutes(mEndTime.getText().toString());

                            if (st2 == st1 || en1 == en2 ||
                                    (st2 > st1 && st2 < en1) ||
                                    (en2 > st1 && en2 < en1) ||
                                    (st2 < st1 && en2 > en1)) {
                                //Utility.showAlert(getActivity(), "", getString(R.string.msg_time_collapse));
                                mEndTime.setError(getString(R.string.msg_time_reserved));
                                mEndTime.requestFocus();
                                return false;
                            }
                        }
                    }
                }
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
        mNotes.setText("");
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
        bundle.putParcelable("service", WorkingDataStore.getBundle().getParcelable("servProv"));
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_SIGNUP);
        if (Utility.doServiceAction(getActivity(), mConnection, Context.BIND_AUTO_CREATE)) {
            //Utility.showProgress(getActivity(), mFormView, mProgressView, true);
            Utility.showProgressDialog(getActivity(), true);
        }
/*
        SaveServiceData task = new SaveServiceData(this);
        task.execute((Void) null);
*/
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        if (action == MappService.DO_SIGNUP) {
            signUpDone(data);
        } else if (action == MappService.DO_GET_SPECIALITY) {
            getSpecialitiesDone(data);
        }
        return data.getBoolean("status");
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
        }
        //Utility.showProgress(getActivity(), mFormView, mProgressView, false);
        Utility.showProgressDialog(getActivity(), false);
    }

    private void getSpecialitiesDone(Bundle data) {
        //Utility.showProgress(getActivity(), mFormView, mProgressView, false);
        Utility.showProgressDialog(getActivity(), false);
        mSpecialityList = data.getStringArrayList("specialities");
        if (mSpecialityList == null) {
            mSpecialityList = new ArrayList<>();
        }
        mSpecialityList.add(0, getString(R.string.select_speciality));
        if (!mSpecialityList.contains(getString(R.string.other))) {
            mSpecialityList.add(getString(R.string.other));
        }
        //Utility.setNewSpec(getActivity(), list, mSpeciality);
        Utility.setNewSpinner(getActivity(), mSpecialityList, mSpeciality, null);
    }

    public void onBackPressed() {
        mConnection.setBound(false);
        //startActivity(getIntent());
        //getActivity().onBackPressed();
    }

    /*
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
    }*/

    /*public void onPrepareDialog(int id, Dialog dialog) {
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
    }*/

    /*public class ButtonClickHandler implements View.OnClickListener {
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
    }*/

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
