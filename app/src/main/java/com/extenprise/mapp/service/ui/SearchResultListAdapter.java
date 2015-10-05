package com.extenprise.mapp.service.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.data.ServProvListItem;

import java.util.ArrayList;

/**
 * Created by ambey on 4/10/15.
 */
public class SearchResultListAdapter extends ArrayAdapter<ServProvListItem> {
    private ArrayList<ServProvListItem> list;

    public SearchResultListAdapter(Context context, int resource, ArrayList<ServProvListItem> list) {
        super(context, resource);
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null){
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

        fnameView.setText(item.getFirstName());
        lnameView.setText(item.getLastName());
        clinicNameView.setText(item.getServPtName());
        locationView.setText(item.getServPtLocation());
        specialityView.setText(item.getSpeciality());
        expView.setText(String.format("%.01f", item.getExperience()));

        return v;
    }
}
