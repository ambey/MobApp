package com.extenprise.mapp.service.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.extenprise.mapp.R;
import com.extenprise.mapp.service.data.WorkPlaceListItem;
import com.extenprise.mapp.util.Utility;

import java.util.ArrayList;

/**
 * Created by avinash on 8/10/15.
 */
public class WorkPlaceListAdapter extends ArrayAdapter<WorkPlaceListItem> {
    private ArrayList<WorkPlaceListItem> list;

    public WorkPlaceListAdapter(Context context, int resource, ArrayList<WorkPlaceListItem> list) {
        super(context, resource);
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.activity_servprov_wrkdetail_list, null);
        }
        WorkPlaceListItem item = list.get(position);

        EditText mName = (EditText) v.findViewById(R.id.editTextName);
        EditText mLoc = (EditText) v.findViewById(R.id.editTextLoc);
        EditText  mPhone1 = (EditText) v.findViewById(R.id.editTextPhone1);
        EditText mPhone2 = (EditText) v.findViewById(R.id.editTextPhone2);
        EditText  mEmailIdwork = (EditText) v.findViewById(R.id.editTextEmail);
        EditText mConsultFee = (EditText) v.findViewById(R.id.editTextConsultationFees);
        Spinner mServPtType = (Spinner) v.findViewById(R.id.viewWorkPlaceType);
        Spinner  mCity = (Spinner) v.findViewById(R.id.editTextCity);
        Button  mStartTime = (Button) v.findViewById(R.id.buttonStartTime);
        Button mEndTime = (Button) v.findViewById(R.id.buttonEndTime);
        Spinner  mSpeciality = (Spinner) v.findViewById(R.id.editTextSpeciality);
        EditText mExperience = (EditText) v.findViewById(R.id.editTextExperience);
        EditText mQualification = (EditText) v.findViewById(R.id.editTextQualification);
        Button  mMultiSpinnerDays = (Button) v.findViewById(R.id.editTextWeeklyOff);
        Spinner  mServCatagory = (Spinner) v.findViewById(R.id.spinServiceProvCategory);

        mName.setText(item.getName());
        mLoc.setText(item.getLocation());
        mPhone1.setText(item.getPhone());
        mPhone2.setText(item.getAltPhone());
        mEmailIdwork.setText(item.getEmailId());
        mConsultFee.setText("" + item.getConsultFee());
        mServPtType.setSelection(Utility.getSpinnerIndex(mServPtType, item.getServPointType()));
        mCity.setSelection(Utility.getSpinnerIndex(mCity, item.getCity()));
        mStartTime.setText(item.getStartTime());
        mEndTime.setText(item.getEndTime());
        mQualification.setText(item.getQualification());
        mMultiSpinnerDays.setText(item.getWorkingDays());
        mServCatagory.setSelection(Utility.getSpinnerIndex(mServCatagory, item.getServCatagory()));
        mSpeciality.setSelection(Utility.getSpinnerIndex(mServCatagory, item.getSpeciality()));
        mExperience.setText(String.format("%.01f", item.getExperience()));

        return v;
    }
}
