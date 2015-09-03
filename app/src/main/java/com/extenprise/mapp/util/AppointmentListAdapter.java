package com.extenprise.mapp.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.extenprise.mapp.R;

/**
 * Created by ambey on 2/9/15.
 */
public class AppointmentListAdapter extends ArrayAdapter {

    public AppointmentListAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            //convertView = mInflater.inflate(R.layout.activity_view_appointment, null);
        }

        TextView fNameView = (TextView) convertView.findViewById(R.id.patientFNameTextView);
        TextView lNameView = (TextView) convertView.findViewById(R.id.patientLNameTextView);
        TextView timeView = (TextView) convertView.findViewById(R.id.appointmentTimeTextView);
        TextView genderView = (TextView) convertView.findViewById(R.id.patientGenderTextView);
        TextView ageView = (TextView) convertView.findViewById(R.id.patientAgeTextView);
        TextView wtView = (TextView) convertView.findViewById(R.id.patientWeightTextView);

        return super.getView(position, convertView, parent);
    }
}
