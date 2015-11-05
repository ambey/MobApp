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
import com.extenprise.mapp.data.ReportServiceStatus;
import com.extenprise.mapp.service.activity.RxInboxItemDetailsActivity;
import com.extenprise.mapp.service.data.RxInboxItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by ambey on 30/10/15.
 */
public class RxInboxAdapter extends ArrayAdapter<RxInboxItem> implements AdapterView.OnItemClickListener {
    private ArrayList<RxInboxItem> rxList;
    private boolean feedback;

    public RxInboxAdapter(Context context, int resource, ArrayList<RxInboxItem> list, boolean feedback) {
        super(context, resource);
        rxList = list;
        this.feedback = feedback;
    }

    @Override
    public int getCount() {
        try {
            return rxList.size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(feedback) {
                v = inflater.inflate(R.layout.layout_rx_feedback_head, null);
            } else {
                v = inflater.inflate(R.layout.layout_rx_list_detail, null);
            }
        }
        TextView statusView = (TextView) v.findViewById(R.id.statusView);
        TextView dateView = (TextView) v.findViewById(R.id.dateTextView);
        TextView custNameView = (TextView) v.findViewById(R.id.custNameView);
        TextView custPhoneView = (TextView) v.findViewById(R.id.custPhoneView);
        TextView servProvNameView;
        TextView servPointNameView;
        TextView servProvPhoneView;
        if(feedback) {
            servProvNameView = (TextView)v.findViewById(R.id.medStoreProvView);
            servPointNameView = (TextView)v.findViewById(R.id.medStoreNameView);
            servProvPhoneView = (TextView)v.findViewById(R.id.medStoreProvPhoneView);
        } else {
            servProvNameView = (TextView) v.findViewById(R.id.drNameView);
            servPointNameView = (TextView) v.findViewById(R.id.drClinicView);
            servProvPhoneView = (TextView) v.findViewById(R.id.drPhoneView);
        }
        RxInboxItem item = rxList.get(position);
        int status = item.getReportService().getStatus();
        statusView.setText(ReportServiceStatus.getStatusString(getContext(), status));

        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        dateView.setText(sdf.format(item.getRx().getDate()));
        servProvNameView.setText(String.format("%s %s.", item.getServProv().getLastName().toUpperCase(),
                item.getServProv().getFirstName().substring(0, 1).toUpperCase()));
        custNameView.setText(String.format("%s %s.", item.getCustomer().getlName().toUpperCase(),
                item.getCustomer().getfName().substring(0, 1).toUpperCase()));
        custPhoneView.setText(item.getCustomer().getSignInData().getPhone());
        servPointNameView.setText(String.format("%s, %s", item.getServProv().getServPtName(),
                item.getServProv().getServPtLocation()));
        servProvPhoneView.setText(String.format("(%s)", item.getServProv().getPhone()));
        return v;
    }

    public RxInboxItem getItem(int position) {
        try {
            return rxList.get(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getContext(), RxInboxItemDetailsActivity.class);
        intent.putExtra("inboxItem", rxList.get(position));
        intent.putParcelableArrayListExtra("inbox", rxList);
        intent.putExtra("feedback", feedback);
        getContext().startActivity(intent);
    }
}
