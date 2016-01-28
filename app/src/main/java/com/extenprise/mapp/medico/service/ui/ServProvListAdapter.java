package com.extenprise.mapp.medico.service.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.service.data.ServProvListItem;
import com.extenprise.mapp.medico.util.Utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by ambey on 4/10/15.
 */
public class ServProvListAdapter extends ArrayAdapter<ServProvListItem> implements AdapterView.OnItemSelectedListener {
    private ArrayList<ServProvListItem> mList;
    private ArrayList<ServProvListItem> mSortedList;
    private int mSelectedPosition;
    private String mSortField;
    private boolean mAscending;

    public ServProvListAdapter(Context context, int resource, ArrayList<ServProvListItem> list) {
        super(context, resource);
        this.mList = list;
        mSelectedPosition = -1;
    }

    @Override
    public int getCount() {
        try {
            return mList.size();
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
        ServProvListItem item = getItem(position);
        TextView fnameView = (TextView) v.findViewById(R.id.viewFirstName);
        TextView lnameView = (TextView) v.findViewById(R.id.viewLastName);
        TextView clinicNameView = (TextView) v.findViewById(R.id.viewClinicName);
        TextView locationView = (TextView) v.findViewById(R.id.viewLocation);
        TextView specialityView = (TextView) v.findViewById(R.id.viewDocSpeciality);
        TextView expView = (TextView) v.findViewById(R.id.viewExpValue);
        ImageView imgAvail = (ImageView) v.findViewById(R.id.imageViewAvailability);
        TextView lbl = (TextView) v.findViewById(R.id.viewDr);

        if(item.getCategory() != null) {
            if(item.getCategory().equalsIgnoreCase(getContext().getString(R.string.physician))) {
                lbl.setText(getContext().getString(R.string.drLbl));
            } else {
                lbl.setText("");
            }
        }

        if (item.getWorkingDays() != null) {
            if (Utility.findDocAvailability(item.getWorkingDays(), Calendar.getInstance())) {
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

        if (position == mSelectedPosition) {
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSelectedPosition = position;
        notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        mSelectedPosition = -1;
        notifyDataSetChanged();
    }

    public void setSortField(String sortField) {
        this.mSortField = sortField;
        if (sortField == null) {
            return;
        }
        Comparator<ServProvListItem> comparator = null;
        Context context = getContext();
        if (sortField.equals(context.getString(R.string.by_drname))) {
            comparator = new Comparator<ServProvListItem>() {
                @Override
                public int compare(ServProvListItem lhs, ServProvListItem rhs) {
                    int result = lhs.getFirstName().toUpperCase().compareTo(rhs.getFirstName().toUpperCase());
                    if (result == 0) {
                        return lhs.getLastName().toUpperCase().compareTo(rhs.getLastName().toUpperCase());
                    }
                    return result;
                }
            };
        } else if (sortField.equals(context.getString(R.string.by_clinic_name))) {
            comparator = new Comparator<ServProvListItem>() {
                @Override
                public int compare(ServProvListItem lhs, ServProvListItem rhs) {
                    return lhs.getServPtName().toUpperCase().compareTo(rhs.getServPtName().toUpperCase());
                }
            };
        } else if (sortField.equals(context.getString(R.string.by_experience))) {
            comparator = new Comparator<ServProvListItem>() {
                @Override
                public int compare(ServProvListItem lhs, ServProvListItem rhs) {
                    return ((int) (lhs.getExperience() * 10) - (int) (rhs.getExperience() * 10));
                }
            };
        } else if (sortField.equals(context.getString(R.string.by_location))) {
            comparator = new Comparator<ServProvListItem>() {
                @Override
                public int compare(ServProvListItem lhs, ServProvListItem rhs) {
                    return lhs.getServPtLocation().toUpperCase().compareTo(rhs.getServPtLocation().toUpperCase());
                }
            };
        } else if (sortField.equals(context.getString(R.string.by_category))) {
            comparator = new Comparator<ServProvListItem>() {
                @Override
                public int compare(ServProvListItem lhs, ServProvListItem rhs) {
                    return lhs.getSpeciality().toUpperCase().compareTo(rhs.getSpeciality().toUpperCase());
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
