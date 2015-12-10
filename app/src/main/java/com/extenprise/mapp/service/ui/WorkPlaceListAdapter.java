package com.extenprise.mapp.service.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.service.data.ServProvListItem;
import com.extenprise.mapp.service.data.WorkPlace;
import com.extenprise.mapp.util.Utility;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by avinash on 8/10/15.
 */
public class WorkPlaceListAdapter extends ArrayAdapter<WorkPlace> implements AdapterView.OnItemSelectedListener {
    private ArrayList<WorkPlace> list;
    private int selectedPosition;

    public WorkPlaceListAdapter(Context context, int resource, ArrayList<WorkPlace> list) {
        super(context, resource);
        this.list = list;
        selectedPosition = -1;
    }

    @Override
    public int getCount() {
        try {
            return list.size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.layout_workplace, null);
        }
        WorkPlace item = list.get(position);

        TextView mName = (TextView) v.findViewById(R.id.editTextName);
        TextView mLoc = (TextView) v.findViewById(R.id.editTextLoc);
        TextView  mPhone1 = (TextView) v.findViewById(R.id.editTextPhone1);
        TextView mPhone2 = (TextView) v.findViewById(R.id.editTextPhone2);
        TextView  mEmailIdwork = (TextView) v.findViewById(R.id.editTextEmail);
        TextView mConsultFee = (TextView) v.findViewById(R.id.editTextConsultationFees);
        TextView mServPtType = (TextView) v.findViewById(R.id.viewWorkPlaceType);
        TextView  mCity = (TextView) v.findViewById(R.id.editTextCity);
        TextView  mStartTime = (TextView) v.findViewById(R.id.buttonStartTime);
        TextView  mSpeciality = (TextView) v.findViewById(R.id.editTextSpeciality);
        TextView mExperience = (TextView) v.findViewById(R.id.editTextExperience);
        TextView mQualification = (TextView) v.findViewById(R.id.editTextQualification);
        TextView  mMultiSpinnerDays = (TextView) v.findViewById(R.id.editTextWeeklyOff);
        TextView  mServCatagory = (TextView) v.findViewById(R.id.spinServiceProvCategory);
        TextView mPinCode = (TextView) v.findViewById(R.id.editTextPinCode);

        mName.setText(replaceMark(mName) + item.getName());
        mLoc.setText(replaceMark(mLoc) + item.getLocation());
        mPhone1.setText(replaceMark(mPhone1) + item.getPhone());
        mPhone2.setText(mPhone2.getText().toString() + item.getAltPhone());
        mEmailIdwork.setText(mEmailIdwork.getText().toString() + " : " + item.getEmailId());
        mConsultFee.setText(mConsultFee.getText() + String.format("%.2f", item.getConsultFee()));
        mServPtType.setText(replaceMark(mServPtType) + item.getServPointType());
        mCity.setText(item.getCity().toString());
        mStartTime.setText(replaceMark(mStartTime) + Utility.getTimeString(item.getStartTime()) +
                " To " + Utility.getTimeString(item.getEndTime()));
        mQualification.setText(replaceMark(mQualification) + item.getQualification());
        mMultiSpinnerDays.setText(replaceMark(mMultiSpinnerDays) + item.getWorkingDays());
        mExperience.setText(replaceMark(mExperience) + String.format("%.01f", item.getExperience()));
        mSpeciality.setText(mSpeciality.getText().toString() + " : " + item.getSpeciality());
        mServCatagory.setText(mServCatagory.getText().toString() + " : " + item.getServCategory());
        if(item.getPincode() != null) {
            mPinCode.setText(replaceMark(mPinCode) + item.getPincode());
        }

        /*v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.v(LOG_TAG, "ROW PRESSED");
                int opt = Utility.option(getContext(), R.string.edit1, R.string.remove);
                workPlace = mWorkPlaceList.get(position);
                if (opt == R.string.edit1) {
                    getWorkPlaceView(workPlace);
                } else if (opt == R.string.remove) {
                    if (Utility.confirm(getContext(), R.string.confirm_remove_workplace)) {
                        Utility.showProgress(getContext(), mFormView, mProgressView, true);
                        Bundle bundle = new Bundle();
                        bundle.putInt("loginType", MappService.SERVICE_LOGIN);
                        bundle.putParcelable("workPlace", workPlace);
                        mConnection.setData(bundle);
                        mConnection.setAction(MappService.DO_REMOVE_WORK_PLACE);
                        Utility.doServiceAction(getApplicationContext(), mConnection, BIND_AUTO_CREATE);
                    }

                }
            }
        });*/

        if(position == selectedPosition) {
            v.setBackgroundColor(getContext().getResources().getColor(R.color.ThemeColor));
        }

        return v;
    }

    public WorkPlace getItem(int position) {
        try {
            return list.get(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        selectedPosition = -1;
        notifyDataSetChanged();
    }

    private String replaceMark(TextView tv) {
        return tv.getText().toString().replace("*", ":");
    }
}
