package com.extenprise.mapp.medico.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.extenprise.mapp.medico.R;

/**
 * Created by ambey on 28/12/15.
 */
public class SortFieldListAdapter extends ArrayAdapter<String> {
    private String[] sortFields;
    private boolean[] selection;
    private int selectedPos;

    public SortFieldListAdapter(Context context, int resource, String[] sortFields) {
        super(context, resource);
        this.sortFields = sortFields;
        selectedPos = -1;
        if (sortFields != null) {
            selection = new boolean[sortFields.length];
            if (sortFields.length > 0) {
                selection[0] = true;
                selectedPos = 0;
            }
        }
    }

    @Override
    public int getCount() {
        if (sortFields == null) {
            return 0;
        }
        return sortFields.length;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.layout_sort_field, null);
        }
        CheckBox checkBox = (CheckBox) v.findViewById(R.id.sortFieldCheckBox);
        checkBox.setText(getContext().getString(R.string.sort_field, sortFields[position]));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selection[position] = isChecked;
                /* Allow only one sort field */
                if (isChecked && selectedPos != position) {
                    if (selectedPos != -1) {
                        selection[selectedPos] = false;
                    }
                    selectedPos = position;
                    notifyDataSetChanged();
                } else if (!isChecked && selectedPos == position) {
                    selection[position] = true; // Keep the option selected
                    notifyDataSetChanged();
/*
                    selectedPos = -1;
*/
                }
            }
        });
        checkBox.setChecked(selection[position]);
        return v;
    }

    public boolean[] getSelection() {
        return selection;
    }

    public String getSortField() {
        if (selectedPos == -1) {
            return null;
        }
        return sortFields[selectedPos];
    }
}
