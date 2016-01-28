package com.extenprise.mapp.medico.customer.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.customer.data.AppointmentListItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by ambey on 10/10/15.
 */
public class AppointmentListAdapter extends ArrayAdapter<AppointmentListItem> implements AdapterView.OnItemClickListener {
    private ArrayList<AppointmentListItem> mList;
    private ArrayList<AppointmentListItem> mSortedList;
    private String mSortField;
    private boolean mAscending;

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

        AppointmentListItem item = getItem(position);
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

    public AppointmentListItem getItem(int position) {
        try {
            if (mSortField == null) {
                return mList.get(position);
            }
            return mSortedList.get(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
/*
        Intent intent = new Intent(getContext(), AppointmentDetailsActivity.class);
        intent.putExtra("appont", mList.get(position));
        getContext().startActivity(intent);
*/
    }

    public void setSortField(String sortField) {
        this.mSortField = sortField;
        this.mSortField = sortField;
        if (sortField == null) {
            return;
        }
        Comparator<AppointmentListItem> comparator = null;
        Context context = getContext();
        if (sortField.equals(context.getString(R.string.by_name))) {
            comparator = new Comparator<AppointmentListItem>() {
                @Override
                public int compare(AppointmentListItem lhs, AppointmentListItem rhs) {
                    int result = lhs.getFirstName().toUpperCase().compareTo(rhs.getFirstName().toUpperCase());
                    if (result == 0) {
                        return lhs.getLastName().toUpperCase().compareTo(rhs.getLastName().toUpperCase());
                    }
                    return result;
                }
            };
        } else if (sortField.equals(context.getString(R.string.by_date))) {
            comparator = new Comparator<AppointmentListItem>() {
                @Override
                public int compare(AppointmentListItem lhs, AppointmentListItem rhs) {
                    return lhs.getDate().compareTo(rhs.getDate());
                }
            };
        }
        mSortedList = new ArrayList<>();
        mSortedList.addAll(mList);
        if (comparator != null) {
            if (!mAscending) {
                comparator = Collections.reverseOrder(comparator);
            }
            Collections.sort(mSortedList, comparator);
            notifyDataSetChanged();
        }
    }

    public void setAscending(boolean ascending) {
        this.mAscending = ascending;
    }
}
