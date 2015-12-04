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

        /*EditText mName = (EditText) v.findViewById(R.id.editTextName);
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
        Spinner  mServCatagory = (Spinner) v.findViewById(R.id.spinServiceProvCategory);*/

        TextView mName = (TextView) v.findViewById(R.id.editTextName);
        TextView mLoc = (TextView) v.findViewById(R.id.editTextLoc);
        TextView  mPhone1 = (TextView) v.findViewById(R.id.editTextPhone1);
        TextView mPhone2 = (TextView) v.findViewById(R.id.editTextPhone2);
        TextView  mEmailIdwork = (TextView) v.findViewById(R.id.editTextEmail);
        TextView mConsultFee = (TextView) v.findViewById(R.id.editTextConsultationFees);
        TextView mServPtType = (TextView) v.findViewById(R.id.viewWorkPlaceType);
        TextView  mCity = (TextView) v.findViewById(R.id.editTextCity);
        TextView  mStartTime = (TextView) v.findViewById(R.id.buttonStartTime);
        //TextView mEndTime = (TextView) v.findViewById(R.id.buttonEndTime);
        TextView  mSpeciality = (TextView) v.findViewById(R.id.editTextSpeciality);
        TextView mExperience = (TextView) v.findViewById(R.id.editTextExperience);
        TextView mQualification = (TextView) v.findViewById(R.id.editTextQualification);
        TextView  mMultiSpinnerDays = (TextView) v.findViewById(R.id.editTextWeeklyOff);
        TextView  mServCatagory = (TextView) v.findViewById(R.id.spinServiceProvCategory);
        TextView mPinCode = (TextView) v.findViewById(R.id.editTextPinCode);

        mName.setText("Name : " + item.getName());
        mLoc.setText("Location : " + item.getLocation());
        mPhone1.setText("Phone1 : " + item.getPhone());
        mPhone2.setText("Phone2 : " + item.getAltPhone());
        mEmailIdwork.setText("EmailID : " + item.getEmailId());
        mConsultFee.setText(String.format("%.2f", item.getConsultFee()));
        //mServPtType.setSelection(Utility.getSpinnerIndex(mServPtType, item.getServPointType()));
        mServPtType.setText("Service Point : " + item.getServPointType());
        //mCity.setSelection(Utility.getSpinnerIndex(mCity, item.getCity()));
        mCity.setText(item.getCity().toString());
        mStartTime.setText("Working Hours : From " + Utility.getTimeString(item.getStartTime()) +
                " To " + Utility.getTimeString(item.getEndTime()));
        //mEndTime.setText("" + Utility.getTimeString(item.getEndTime()));
        mQualification.setText("Qualification :" + item.getQualification());
        mMultiSpinnerDays.setText("Working Days : " + item.getWorkingDays());
        //mServCatagory.setSelection(Utility.getSpinnerIndex(mServCatagory, item.getServCategory()));
        //mSpeciality.setSelection(Utility.getSpinnerIndex(mServCatagory, item.getSpeciality()));
        mExperience.setText("Experience : " + String.format("%.01f", item.getExperience()));

        /*ArrayList<String> specs = new ArrayList<>();
        specs.add(item.getSpeciality());
        Utility.setNewSpec(getContext(), specs, mSpeciality);*/
        mSpeciality.setText("Speciality : " + item.getSpeciality());
        mServCatagory.setText("Category : " + item.getServCategory());
        if(item.getPincode() != null) {
            mPinCode.setText("Pin Code : " + item.getPincode());
        }

        /*mServPtType.setClickable(false);
        mCity.setClickable(false);
        mStartTime.setClickable(false);
        mEndTime.setClickable(false);
        mMultiSpinnerDays.setClickable(false);
        mServCatagory.setClickable(false);
        mSpeciality.setClickable(false);*/

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
}
