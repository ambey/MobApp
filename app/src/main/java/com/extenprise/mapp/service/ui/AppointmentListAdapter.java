package com.extenprise.mapp.service.ui;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.extenprise.mapp.R;
import com.extenprise.mapp.net.AppStatus;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.service.activity.AppointmentDetailsActivity;
import com.extenprise.mapp.service.data.AppointmentListItem;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.Utility;

import java.util.ArrayList;

/**
 * Created by ambey on 10/10/15.
 */
public class AppointmentListAdapter extends ArrayAdapter<AppointmentListItem> implements AdapterView.OnItemClickListener {
    private ArrayList<AppointmentListItem> mList;
    private ServiceProvider mServProv;

    public AppointmentListAdapter(Context context, int resource, ArrayList<AppointmentListItem> list, ServiceProvider servProv) {
        super(context, resource);
        mList = list;
        mServProv = servProv;
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
            v = inflater.inflate(R.layout.activity_view_appointment, null);
        }
        TextView fnameView = (TextView) v.findViewById(R.id.patientFNameTextView);
        TextView lnameView = (TextView) v.findViewById(R.id.patientLNameTextView);
        TextView genderView = (TextView) v.findViewById(R.id.patientGenderTextView);
        TextView ageView = (TextView) v.findViewById(R.id.patientAgeTextView);
        TextView wtView = (TextView) v.findViewById(R.id.patientWeightTextView);
        TextView timeView = (TextView) v.findViewById(R.id.appointmentTimeTextView);
        TextView statusView = (TextView)v.findViewById(R.id.statusTextView);

        AppointmentListItem item = mList.get(position);
        fnameView.setText(item.getFirstName());
        lnameView.setText(item.getLastName());
        genderView.setText(item.getGender());
        ageView.setText(String.format("%d", item.getAge()));
        wtView.setText(String.format("%.01f", item.getWeight()));
        timeView.setText(item.getTime());

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
        if (!AppStatus.getInstance(getContext()).isOnline()) {
            Utility.showMessage(getContext(), R.string.error_not_online);
            return;
        }
        Intent intent = new Intent(getContext(), AppointmentDetailsActivity.class);
        intent.putExtra("appont", mList.get(position));
        intent.putExtra("service", mServProv);
        getContext().startActivity(intent);
    }
}
