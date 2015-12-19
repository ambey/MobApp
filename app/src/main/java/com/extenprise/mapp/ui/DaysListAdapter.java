package com.extenprise.mapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.extenprise.mapp.R;
import com.extenprise.mapp.util.Utility;

/**
 * Created by ambey on 19/12/15.
 */
public class DaysListAdapter extends ArrayAdapter<String> {
    private String[] days;
    private boolean[] selection;

    public DaysListAdapter(Context context, int resource, String selectedDays) {
        super(context, resource);
        days = Utility.getDaysOptions(context);
        selection = new boolean[days.length];
        setupSelection(selectedDays);
    }

    @Override
    public int getCount() {
        return days.length;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.layout_select_day, null);
        }
        CheckBox checkBox = (CheckBox) v.findViewById(R.id.dayCheckBox);
        checkBox.setText(days[position]);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selection[position] = isChecked;
                if (position == 0) {
                    notifyDataSetInvalidated();
                }
            }
        });
        if (position != 0) {
            checkBox.setEnabled(!selection[0]);
        }
        checkBox.setChecked(selection[0] || selection[position]);
        return v;
    }

    public boolean[] getSelection() {
        return selection;
    }

    private void setupSelection(String selectedDays) {
        if (selectedDays == null || selectedDays.equals("")) {
            return;
        }
        for (String d : selectedDays.split(",")) {
            int index = getDayIndex(d);
            if (index != -1) {
                selection[index] = true;
            }
        }
    }

    private int getDayIndex(String day) {
        for (int i = 0; i < days.length; i++) {
            if (day.equals(days[i])) {
                return i;
            }
        }
        return -1;
    }

}
