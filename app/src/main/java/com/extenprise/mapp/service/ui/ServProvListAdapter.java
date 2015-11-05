package com.extenprise.mapp.service.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.service.data.ServProvListItem;
import com.extenprise.mapp.util.Utility;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ambey on 4/10/15.
 */
public class ServProvListAdapter extends ArrayAdapter<ServProvListItem> implements AdapterView.OnItemSelectedListener {
    private ArrayList<ServProvListItem> list;
    private int selectedPosition;

    public ServProvListAdapter(Context context, int resource, ArrayList<ServProvListItem> list) {
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
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.activity_search_result, null);
        }
        ServProvListItem item = list.get(position);
        TextView fnameView = (TextView) v.findViewById(R.id.viewFirstName);
        TextView lnameView = (TextView) v.findViewById(R.id.viewLastName);
        TextView clinicNameView = (TextView) v.findViewById(R.id.viewClinicName);
        TextView locationView = (TextView) v.findViewById(R.id.viewLocation);
        TextView specialityView = (TextView) v.findViewById(R.id.viewDocSpeciality);
        TextView expView = (TextView) v.findViewById(R.id.viewExpValue);
        ImageView imgAvail = (ImageView) v.findViewById(R.id.imageViewAvailability);

        if(item.getAvailDays() != null) {
          if(Utility.findDocAvailability(item.getAvailDays(), Calendar.getInstance())) {
              imgAvail.setImageResource(R.drawable.g_circle);
          } else {
              imgAvail.setImageResource(R.drawable.r_circle);
          }
        }

        fnameView.setText(item.getFirstName());
        lnameView.setText(item.getLastName());
        clinicNameView.setText(item.getServPtName());
        locationView.setText(item.getServPtLocation());
        specialityView.setText(item.getSpeciality());
        expView.setText(String.format("%.01f", item.getExperience()));

        if(position == selectedPosition) {
            int textColor = Color.WHITE;
            v.setBackgroundColor(getContext().getResources().getColor(R.color.ThemeColor));
            fnameView.setTextColor(textColor);
            lnameView.setTextColor(textColor);
            clinicNameView.setTextColor(textColor);
            locationView.setTextColor(textColor);
            specialityView.setTextColor(textColor);
            expView.setTextColor(textColor);
        }

        return v;
    }

    public ServProvListItem getItem(int position) {
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
