package com.extenprise.mapp.service.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.extenprise.mapp.service.data.ServProvHasServHasServPt;
import com.extenprise.mapp.util.DBUtil;

import java.util.ArrayList;

/**
 * Created by ambey on 10/9/15.
 */
public class ServProvWorkPlaceFragment extends Fragment {
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
        LoginHolder.spsspt = new ServProvHasServHasServPt();

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
                if(spec.equals("Other")) {
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

    public void showtimeFields(View view) {
        if(mStartTime.getVisibility() == View.VISIBLE) {
            mStartTime.setVisibility(View.GONE);
            mEndTime.setVisibility(View.GONE);
        } else {
            mStartTime.setVisibility(View.VISIBLE);
            mEndTime.setVisibility(View.VISIBLE);
        }
    }

    public void showFeeFields(View view) {
        TextView rupeeSign = (TextView)mRootview.findViewById(R.id.viewRsSign);
        if(mConsultFee.getVisibility() == View.VISIBLE) {
            mConsultFee.setVisibility(View.GONE);
            rupeeSign.setVisibility(View.GONE);
        } else {
            mConsultFee.setVisibility(View.VISIBLE);
            rupeeSign.setVisibility(View.VISIBLE);
        }
    }

    public void showDaysFields(View view) {
        if(mMultiSpinnerDays.getVisibility() == View.VISIBLE) {
            mMultiSpinnerDays.setVisibility(View.GONE);
        } else {
            //UIUtility.expandOrCollapse(mMultiSpinnerDays, "expand");
            mMultiSpinnerDays.setVisibility(View.VISIBLE);
        }
    }

    public void showWorkFields(View view) {
        if(mName.getVisibility() == View.VISIBLE) {
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
        for(String d : selectedDays) {
            selections[getDayIndex(d)] = true;
        }
    }

    private int getDayIndex(String day) {
        for(int i = 0; i < options.length; i++) {
            if(day.equals(options[i])) {
                return i;
            }
        }
        return 0;
    }

    private void setupAllDaysSelected() {
        selections[0] = false;
        selectedDays = options[1].toString();
        for(int i = 2; i < options.length; i++){
            selectedDays += "," + options[i];
        }
    }

    public class ButtonClickHandler implements View.OnClickListener {
        public void onClick(View view) {
            if(!mMultiSpinnerDays.getText().equals(getString(R.string.select_days))) {
                setupSelection();
            }
            getActivity().showDialog(0);
        }
    }
}
