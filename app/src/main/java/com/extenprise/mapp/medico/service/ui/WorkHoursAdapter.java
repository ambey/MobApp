package com.extenprise.mapp.medico.service.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;

import com.extenprise.mapp.medico.R;

/**
 * Created by ambey on 24/2/16.
 */
public class WorkHoursAdapter extends ArrayAdapter<String> {
    String[] weekDays;

    public WorkHoursAdapter(Context context, int resource) {
        super(context, resource);
        weekDays = context.getResources().getStringArray(R.array.days);
    }

    @Override
    public int getCount() {
        return weekDays.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.layout_work_hours, null);
        }
        CheckBox checkBoxWorkDay = (CheckBox) v.findViewById(R.id.checkBoxWorkDay);
        EditText editTextFrom = (EditText) v.findViewById(R.id.editTextFromTime);
        EditText editTextTo = (EditText) v.findViewById(R.id.editTextToTime);

        checkBoxWorkDay.setText(weekDays[position]);
        editTextFrom.setHint(String.format("%s *", getContext().getString(R.string.from)));
        editTextTo.setHint(String.format("%s *", getContext().getString(R.string.to)));
        return v;
    }
}
