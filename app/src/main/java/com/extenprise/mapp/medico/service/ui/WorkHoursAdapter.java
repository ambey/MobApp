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
    private boolean[] checked;
    private boolean allDaysChecked;
    private WeeklyWorkTiming weeklyWorkTiming;

    public WorkHoursAdapter(Context context, int resource, WeeklyWorkTiming weeklyWorkTiming) {
        super(context, resource);
        weekDays = context.getResources().getStringArray(R.array.days);
        checked = new boolean[weekDays.length];
        if (weeklyWorkTiming != null) {
            this.weeklyWorkTiming = weeklyWorkTiming;
        } else {
            this.weeklyWorkTiming = new WeeklyWorkTiming();
        }

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
            View layout = v.findViewById(R.id.layoutWorkTiming);
            layout.setEnabled(!allDaysChecked);
            //checkBoxWorkDay.setEnabled(!allDaysChecked);
            if (checked[position]) {
                weeklyWorkTiming.addWorkTime(weekDays[position]);
            }
            checkBoxWorkDay.setChecked(checked[position]);
        } else {
            checkBoxWorkDay.setEnabled(true);
            checkBoxWorkDay.setChecked(allDaysChecked);
        }
        checkBoxWorkDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String day = buttonView.getText().toString();

                if (isChecked) {
                    weeklyWorkTiming.addWorkTime(day);
                } else {
                    weeklyWorkTiming.removeWorkTime(day);
                    checked[position] = false;
                }
                if (day.equals(getContext().getString(R.string.all_days)) || !isChecked) {
                    if (day.equals(getContext().getString(R.string.all_days))) {
                        setAllDaysChecked(isChecked);
                    }
                    notifyDataSetChanged();
                }
            }
        });
        editTextFrom.setHint(String.format("%s *", getContext().getString(R.string.from)));
        editTextTo.setHint(String.format("%s *", getContext().getString(R.string.to)));
        String from = "", from2 = "", to = "", to2 = "";
        if (workTime != null) {
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
            checked[position] = true;
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
            if (position == 0) {
                setAllDaysChecked(true);
                setAllDaysWorkTime(text.getId(), time);
            }
            notifyDataSetChanged();
        }
    }

    private void setAllDaysWorkTime(int id, String time) {
        for (int i = 1; i < weekDays.length; i++) {
            weeklyWorkTiming.addWorkTime(weekDays[i]);
            WeeklyWorkTiming.WorkTime workTime = weeklyWorkTiming.getWorkTime(weekDays[i]);
            switch (id) {
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
        }
    }

    public WeeklyWorkTiming getWeeklyWorkTiming() {
        return weeklyWorkTiming;
    }

    private void setAllDaysChecked(boolean checked) {
        allDaysChecked = checked;
        if (checked) {
            for (int i = 1; i < this.checked.length; i++) {
                this.checked[i] = true;
            }
        }
    }
}
