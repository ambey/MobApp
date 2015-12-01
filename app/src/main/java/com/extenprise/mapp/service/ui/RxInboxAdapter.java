package com.extenprise.mapp.service.ui;

import android.app.Activity;
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
import com.extenprise.mapp.data.RxFeedback;
import com.extenprise.mapp.service.activity.RxInboxItemDetailsActivity;
import com.extenprise.mapp.service.data.RxInboxItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by ambey on 30/10/15.
 */
public class RxInboxAdapter extends ArrayAdapter<RxInboxItem> implements AdapterView.OnItemClickListener {
    private ArrayList<RxInboxItem> rxList;
    private RxFeedback feedback;

    public RxInboxAdapter(Context context, int resource, ArrayList<RxInboxItem> list, RxFeedback feedback) {
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
            if (feedback == RxFeedback.VIEW_FEEDBACK) {
                v = inflater.inflate(R.layout.layout_rx_feedback_head, null);
            } else {
                v = inflater.inflate(R.layout.layout_rx_list_detail, null);
            }
        }
        TextView statusView;
        TextView dateView;
        TextView custNameView;
        TextView custPhoneView;
        TextView servProvNameView;
        TextView servPointNameView;
        TextView servProvPhoneView;
        if (feedback == RxFeedback.VIEW_FEEDBACK) {
            statusView = (TextView) v.findViewById(R.id.feedBackStatusView);
            dateView = (TextView) v.findViewById(R.id.feedbackDateView);
            custNameView = (TextView) v.findViewById(R.id.feedbackCustNameView);
            custPhoneView = (TextView) v.findViewById(R.id.feedbackCustPhoneView);
            servProvNameView = (TextView) v.findViewById(R.id.medStoreProvView);
            servPointNameView = (TextView) v.findViewById(R.id.medStoreNameView);
            servProvPhoneView = (TextView) v.findViewById(R.id.medStoreProvPhoneView);
        } else {
            statusView = (TextView) v.findViewById(R.id.statusView);
            dateView = (TextView) v.findViewById(R.id.dateTextView);
            custNameView = (TextView) v.findViewById(R.id.custNameView);
            custPhoneView = (TextView) v.findViewById(R.id.custPhoneView);
            servProvNameView = (TextView) v.findViewById(R.id.drNameView);
            servPointNameView = (TextView) v.findViewById(R.id.drClinicView);
            servProvPhoneView = (TextView) v.findViewById(R.id.drPhoneView);
        }
        RxInboxItem item = rxList.get(position);
        int status = item.getReportService().getStatus();
        statusView.setText(ReportServiceStatus.getStatusString(getContext(), status));
        if(feedback == RxFeedback.VIEW_FEEDBACK) {
            statusView.setVisibility(View.GONE);
        }
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
        intent.putParcelableArrayListExtra("inbox", rxList);
        intent.putExtra("position", position);
        intent.putExtra("feedback", feedback.ordinal());

        Activity myActivity = (Activity)getContext();
        intent.putExtra("origin_activity", myActivity.getIntent().getStringExtra("parent-activity"));
        myActivity.startActivity(intent);
    }
}
