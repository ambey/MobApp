package com.extenprise.mapp.service.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.service.data.ServProvListItem;

import java.util.ArrayList;

/**
 * Created by ambey on 30/10/15.
 */
public class MedStoreListAdapter extends ArrayAdapter<ServProvListItem> {
    private ArrayList<ServProvListItem> list;
    private int selectedPos;

    public MedStoreListAdapter(Context context, int resource, ArrayList<ServProvListItem> list) {
        super(context, resource);
        this.list = list;
        selectedPos = -1;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.layout_medstore_item, null);
        }
        ServProvListItem item = list.get(position);
        TextView fnameView = (TextView) v.findViewById(R.id.firstNameView);
        TextView lnameView = (TextView) v.findViewById(R.id.lastNameView);
        TextView medStoreNameView = (TextView) v.findViewById(R.id.medStoreNameView);
        TextView locationView = (TextView) v.findViewById(R.id.medStoreLocView);
        CheckBox checkBox = (CheckBox)v.findViewById(R.id.medStoreCheckBox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    selectedPos = position;
                    notifyDataSetChanged();
                } else if(selectedPos == position) {
                    selectedPos = -1;
                }
            }
        });

        System.out.println("selected pos: " + selectedPos);
        checkBox.setChecked(selectedPos == position);

        fnameView.setText(item.getFirstName());
        lnameView.setText(item.getLastName());
        medStoreNameView.setText(item.getServPtName());
        locationView.setText(item.getServPtLocation());

        return v;
    }

    @Override
    public ServProvListItem getItem(int position) {
        return list.get(position);
    }

    public int getSelectedPos() {
        return selectedPos;
    }
}
