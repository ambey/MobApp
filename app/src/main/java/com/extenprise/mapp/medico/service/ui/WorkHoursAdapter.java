package com.extenprise.mapp.medico.service.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.service.data.WeeklyWorkTiming;

/**
 * Created by ambey on 24/2/16.
 */
public class WorkHoursAdapter extends ArrayAdapter<String> implements View.OnFocusChangeListener {
    private String[] weekDays;
    private boolean isAllDaysChecked;
    private WeeklyWorkTiming weeklyWorkTiming;

    public WorkHoursAdapter(Context context, int resource, WeeklyWorkTiming weeklyWorkTiming) {
        super(context, resource);
        weekDays = context.getResources().getStringArray(R.array.days);
        this.weeklyWorkTiming = weeklyWorkTiming;
    }

    @Override
    public int getCount() {
        return weekDays.length;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.layout_work_hours, null);
        }
        CheckBox checkBoxWorkDay = (CheckBox) v.findViewById(R.id.checkBoxWorkDay);
        final EditText editTextFrom = (EditText) v.findViewById(R.id.editTextFromTime);
        editTextFrom.setOnFocusChangeListener(this);
        editTextFrom.setTag("" + position);
        final EditText editTextTo = (EditText) v.findViewById(R.id.editTextToTime);
        editTextTo.setOnFocusChangeListener(this);
        editTextTo.setTag("" + position);
        final EditText editTextFrom2 = (EditText) v.findViewById(R.id.editTextFromTime2);
        editTextFrom2.setOnFocusChangeListener(this);
        editTextFrom2.setTag("" + position);
        final EditText editTextTo2 = (EditText) v.findViewById(R.id.editTextToTime2);
        editTextTo2.setOnFocusChangeListener(this);
        editTextTo2.setTag("" + position);

        WeeklyWorkTiming.WorkTime workTime = weeklyWorkTiming.getWorkTime(weekDays[position]);
        checkBoxWorkDay.setText(weekDays[position]);
        if (!weekDays[position].equals(getContext().getString(R.string.all_days))) {
            checkBoxWorkDay.setEnabled(!isAllDaysChecked);
            boolean isChecked = isAllDaysChecked || workTime != null;
            if (isChecked) {
                weeklyWorkTiming.addWorkTime(weekDays[position]);
            }
            checkBoxWorkDay.setChecked(isChecked);
        } else {
            checkBoxWorkDay.setEnabled(true);
            checkBoxWorkDay.setChecked(isAllDaysChecked);
        }
        checkBoxWorkDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String day = buttonView.getText().toString();

                if (isChecked) {
                    weeklyWorkTiming.addWorkTime(day);
                } else {
                    weeklyWorkTiming.removeWorkTime(day);
                }
                if (day.equals(getContext().getString(R.string.all_days)) || !isChecked) {
                    if (day.equals(getContext().getString(R.string.all_days))) {
                        isAllDaysChecked = isChecked;
                    }
                    notifyDataSetChanged();
                }
            }
        });
        editTextFrom.setHint(String.format("%s *", getContext().getString(R.string.from)));
        editTextTo.setHint(String.format("%s *", getContext().getString(R.string.to)));
        String from = "", from2 = "", to = "", to2 = "";
        if (!isAllDaysChecked && workTime != null) {
            checkBoxWorkDay.setChecked(true);
            from = workTime.getFrom();
            to = workTime.getTo();
            if (workTime.getFrom2() != null) {
                from2 = workTime.getFrom2();
            }
            if (workTime.getTo2() != null) {
                to2 = workTime.getTo2();
            }
        }
        editTextFrom.setText(from);
        editTextFrom2.setText(from2);
        editTextTo.setText(to);
        editTextTo2.setText(to2);

        return v;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        EditText text = (EditText) v;
        String time = text.getText().toString().trim();
        if (!(hasFocus || time.equals(""))) {
            int position = Integer.parseInt(text.getTag().toString());
            weeklyWorkTiming.addWorkTime(weekDays[position]);
            WeeklyWorkTiming.WorkTime workTime = weeklyWorkTiming.getWorkTime(weekDays[position]);
            switch (text.getId()) {
                case R.id.editTextFromTime:
                    workTime.setFrom(time);
                    break;
                case R.id.editTextFromTime2:
                    workTime.setFrom2(time);
                    break;
                case R.id.editTextToTime:
                    workTime.setTo(time);
                    break;
                case R.id.editTextToTime2:
                    workTime.setTo2(time);
                    break;
            }
            notifyDataSetChanged();
        }
    }
}
