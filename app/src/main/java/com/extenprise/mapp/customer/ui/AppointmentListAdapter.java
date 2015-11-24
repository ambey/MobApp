package com.extenprise.mapp.customer.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.customer.data.AppointmentListItem;
import com.extenprise.mapp.service.data.ServicePoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by ambey on 10/10/15.
 */
public class AppointmentListAdapter extends ArrayAdapter<AppointmentListItem> implements AdapterView.OnItemClickListener {
    private ArrayList<AppointmentListItem> mList;

    public AppointmentListAdapter(Context context, int resource, ArrayList<AppointmentListItem> list) {
        super(context, resource);
        mList = list;
    }

    @Override
    public int getCount() {
        try {
            return mList.size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.layout_cust_view_appointment, null);
        }
        TextView dateView = (TextView) v.findViewById(R.id.dateView);
        TextView fnameView = (TextView) v.findViewById(R.id.drFNameView);
        TextView lnameView = (TextView) v.findViewById(R.id.drLNameView);
        TextView clinicNameAddrView = (TextView) v.findViewById(R.id.clinicNameAddressView);
        TextView timeView = (TextView) v.findViewById(R.id.appontTimeView);
        TextView phoneView = (TextView) v.findViewById(R.id.phoneView);
        TextView statusView = (TextView)v.findViewById(R.id.statusTextView);

        AppointmentListItem item = mList.get(position);
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        dateView.setText(sdf.format(item.getDate()));
        fnameView.setText(item.getFirstName());
        lnameView.setText(item.getLastName());
        clinicNameAddrView.setText(String.format("%s, %s", item.getClinicName(), item.getLocation()));
        timeView.setText(item.getTime());
        phoneView.setText(item.getPhone());

        int stringId = R.string.confirmed;
        if(! (item.isConfirmed() || item.isCanceled()) ) {
            stringId = R.string.not_confirmed;
        } else if(item.isCanceled()) {
            stringId = R.string.canceled;
        }
        statusView.setText(getContext().getString(stringId));

        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
/*
        Intent intent = new Intent(getContext(), AppointmentDetailsActivity.class);
        intent.putExtra("appont", mList.get(position));
        getContext().startActivity(intent);
*/
    }
}
