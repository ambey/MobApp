package com.extenprise.mapp.medico.service.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.service.data.ServProvListItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by ambey on 30/10/15.
 */
public class MedStoreListAdapter extends ArrayAdapter<ServProvListItem> {
    private ArrayList<ServProvListItem> mList;
    private int selectedPos;
    private ArrayList<ServProvListItem> mSortedList;
    private String mSortField;
    private boolean mAscending;

    public MedStoreListAdapter(Context context, int resource, ArrayList<ServProvListItem> list) {
        super(context, resource);
        this.mList = list;
        selectedPos = -1;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.layout_medstore_item, null);
        }
        ServProvListItem item = getItem(position);
        TextView fnameView = (TextView) v.findViewById(R.id.firstNameView);
        TextView lnameView = (TextView) v.findViewById(R.id.lastNameView);
        TextView medStoreNameView = (TextView) v.findViewById(R.id.medStoreNameView);
        TextView locationView = (TextView) v.findViewById(R.id.medStoreLocView);
        CheckBox checkBox = (CheckBox) v.findViewById(R.id.medStoreCheckBox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedPos = position;
                    notifyDataSetChanged();
                } else if (selectedPos == position) {
                    selectedPos = -1;
                }
            }
        });

        System.out.println("selected pos: " + selectedPos);
        checkBox.setChecked(selectedPos == position);

        fnameView.setText(item.getFirstName());
        lnameView.setText(item.getLastName());
        medStoreNameView.setText(item.getServPtName());
        locationView.setText(item.getServPtLocation());

        return v;
    }

    @Override
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

    public int getSelectedPos() {
        return selectedPos;
    }

    public void setSortField(String sortField) {
        this.mSortField = sortField;
        if (sortField == null) {
            return;
        }
        Comparator<ServProvListItem> comparator = null;
        Context context = getContext();
        if (sortField.equals(context.getString(R.string.by_medstore_owner))) {
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
        } else if (sortField.equals(context.getString(R.string.by_medstore))) {
            comparator = new Comparator<ServProvListItem>() {
                @Override
                public int compare(ServProvListItem lhs, ServProvListItem rhs) {
                    return lhs.getServPtName().toUpperCase().compareTo(rhs.getServPtName().toUpperCase());
                }
            };
        } else if (sortField.equals(context.getString(R.string.by_location))) {
            comparator = new Comparator<ServProvListItem>() {
                @Override
                public int compare(ServProvListItem lhs, ServProvListItem rhs) {
                    return lhs.getServPtLocation().toUpperCase().compareTo(rhs.getServPtLocation().toUpperCase());
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
