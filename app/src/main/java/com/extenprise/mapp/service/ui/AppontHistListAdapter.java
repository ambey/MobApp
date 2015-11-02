package com.extenprise.mapp.service.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.data.Appointment;
import com.extenprise.mapp.service.activity.ViewRxActivity;
import com.extenprise.mapp.service.data.AppointmentListItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by ambey on 11/10/15.
 */
public class AppontHistListAdapter extends ArrayAdapter<AppointmentListItem> {
    private AppointmentListItem mAppont;
    private ArrayList<AppointmentListItem> mList;

    public AppontHistListAdapter(Context context, int resource, AppointmentListItem appont, ArrayList<AppointmentListItem> list) {
        super(context, resource);
        mAppont = appont;
        mList = list;
    }

    @Override
    public int getCount() {
        try {
            return mList.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.layout_appont_row, null);
        }
        TextView dateView = (TextView)v.findViewById(R.id.dateTextView);
        TextView idView = (TextView)v.findViewById(R.id.appontIdTextView);

        SimpleDateFormat sdf = (SimpleDateFormat)SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        dateView.setText(sdf.format(mList.get(position).getDate()));
        idView.setText("" + position);

        return v;
    }

    @Override
    public AppointmentListItem getItem(int position) {
        try {
            return mList.get(position);
        } catch (Exception e) {
            e.printStackTrace();;
        }
        return null;
    }

}
