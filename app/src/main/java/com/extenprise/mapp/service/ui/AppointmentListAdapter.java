package com.extenprise.mapp.service.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.data.WorkingDataStore;
import com.extenprise.mapp.service.activity.AppointmentDetailsActivity;
import com.extenprise.mapp.service.data.AppointmentListItem;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by ambey on 10/10/15.
 */
public class AppointmentListAdapter extends ArrayAdapter<AppointmentListItem> implements AdapterView.OnItemClickListener {
    private ArrayList<AppointmentListItem> mList;
    private ServiceProvider mServProv;
    private boolean mShowDate;
    private ArrayList<AppointmentListItem> mSortedList;
    private String mSortField;
    private boolean mAscending;

    public AppointmentListAdapter(Context context, int resource, ArrayList<AppointmentListItem> list, ServiceProvider servProv) {
        super(context, resource);
        mList = list;
        mServProv = servProv;
        mShowDate = false;
    }

    public void setShowDate(boolean showDate) {
        this.mShowDate = showDate;
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
        TextView dateView = (TextView) v.findViewById(R.id.dateView);
        TextView fnameView = (TextView) v.findViewById(R.id.patientFNameTextView);
        TextView lnameView = (TextView) v.findViewById(R.id.patientLNameTextView);
        TextView genderView = (TextView) v.findViewById(R.id.patientGenderTextView);
        TextView ageView = (TextView) v.findViewById(R.id.patientAgeTextView);
        TextView wtView = (TextView) v.findViewById(R.id.patientWeightTextView);
        TextView timeView = (TextView) v.findViewById(R.id.appointmentTimeTextView);
        TextView statusView = (TextView) v.findViewById(R.id.statusTextView);

        AppointmentListItem item = getItem(position);
        fnameView.setText(item.getFirstName());
        lnameView.setText(item.getLastName());
        genderView.setText(item.getGender());
        ageView.setText(String.format("%d", item.getAge()));
        wtView.setText(String.format("%.01f", item.getWeight()));
        timeView.setText(item.getTime());

        if (mShowDate) {
            dateView.setText(Utility.getDateForDisplay(getContext(), item.getDate(), "dd/MM/yyyy"));
        } else {
            dateView.setVisibility(View.GONE);
        }
        int stringId = R.string.confirmed;
        if (!(item.isConfirmed() || item.isCanceled())) {
            stringId = R.string.not_confirmed;
        } else if (item.isCanceled()) {
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
        Intent intent = new Intent(getContext(), AppointmentDetailsActivity.class);
        Bundle bundle = WorkingDataStore.getBundle();
        bundle.putParcelable("appont", getItem(position));
        getContext().startActivity(intent);
    }

    public void setSortField(String sortField) {
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
