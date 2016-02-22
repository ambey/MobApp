package com.extenprise.mapp.medico.service.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.service.data.WorkPlace;
import com.extenprise.mapp.medico.util.Utility;

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
            v = inflater.inflate(R.layout.layout_wp_list_item, null);
        }
        WorkPlace item = list.get(position);

        TextView  mServCatagory = (TextView) v.findViewById(R.id.spinServiceProvCategory);
        TextView  mSpeciality = (TextView) v.findViewById(R.id.editTextSpeciality);
        TextView mQualification = (TextView) v.findViewById(R.id.editTextQualification);
        TextView mExperience = (TextView) v.findViewById(R.id.editTextExperience);
        TextView mName = (TextView) v.findViewById(R.id.editTextName);
        TextView mServPtType = (TextView) v.findViewById(R.id.viewWorkPlaceType);
        TextView mLoc = (TextView) v.findViewById(R.id.editTextLoc);
        TextView  mPhone1 = (TextView) v.findViewById(R.id.editTextPhone1);
        TextView  mEmailIdwork = (TextView) v.findViewById(R.id.editTextEmail);
        TextView  mMultiSpinnerDays = (TextView) v.findViewById(R.id.editTextWeeklyOff);
        TextView  mStartTime = (TextView) v.findViewById(R.id.buttonStartTime);
        TextView mConsultFee = (TextView) v.findViewById(R.id.editTextConsultationFees);
        /*TextView mPhone2 = (TextView) v.findViewById(R.id.editTextPhone2);
        TextView  mCity = (TextView) v.findViewById(R.id.editTextCity);
        TextView mPinCode = (TextView) v.findViewById(R.id.editTextPinCode);*/

        mServCatagory.setText(item.getServCategory());
        mSpeciality.setText(item.getSpeciality());
        mQualification.setText(item.getQualification());
        mExperience.setText(String.format("%s%s years", getContext().getString(R.string.wp_exp),
                String.format("%.01f", item.getExperience())));
        mName.setText(item.getName());
        mServPtType.setText(String.format(" (%s) ", item.getServPointType()));
        mLoc.setText(String.format("%s, %s -%s", item.getLocation(), item.getCity().toWPstr(), item.getPincode()));

        String phone = item.getPhone();
        if(item.getAltPhone() != null && !item.getAltPhone().equals("")) {
            phone += phone + ", " + item.getAltPhone();
        }
        mPhone1.setText(phone);
        mEmailIdwork.setText(item.getEmailId());
        mMultiSpinnerDays.setText(String.format("%s : %s", getContext().getString(R.string.wp_days),
                item.getWorkingDays()));
        mStartTime.setText(String.format("%s%s To %s", getContext().getString(R.string.wp_work_hrs),
                Utility.getTimeString(item.getStartTime()), Utility.getTimeString(item.getEndTime())));
        mConsultFee.setText(String.format("%s%s", getContext().getString(R.string.consult_fees),
                String.format("%.2f", item.getConsultFee())));

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

    public int getSelectedPosition() {
        return selectedPosition;
    }

    /*private String replaceMark(TextView tv) {
        return tv.getText().toString().replace("*", ":");
    }*/
}
