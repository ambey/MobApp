package com.extenprise.mapp.medico.customer.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.customer.data.Customer;
import com.extenprise.mapp.medico.data.RxFeedback;
import com.extenprise.mapp.medico.data.WorkingDataStore;
import com.extenprise.mapp.medico.service.activity.RxInboxItemDetailsActivity;
import com.extenprise.mapp.medico.service.data.RxInboxItem;
import com.extenprise.mapp.medico.service.data.ServProvListItem;

//import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by ambey on 10/10/15.
 */
public class RxListAdapter extends ArrayAdapter<RxInboxItem> implements AdapterView.OnItemClickListener {
    private ArrayList<RxInboxItem> mList;
    private Customer mCust;
    private ArrayList<RxInboxItem> mSortedList;
    //private int mSelectedPosition;
    private String mSortField;
    private boolean mAscending;

    public RxListAdapter(Context context, int resource, ArrayList<RxInboxItem> list, Customer c) {
        super(context, resource);
        mList = list;
        mCust = c;
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

    public RxInboxItem getItem(int position) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.layout_cust_view_rx, null);
        }
        TextView dateView = (TextView) v.findViewById(R.id.dateView);
        TextView drFnameView = (TextView) v.findViewById(R.id.drFNameView);
        TextView drLnameView = (TextView) v.findViewById(R.id.drLNameView);
        TextView clinicNameAddrView = (TextView) v.findViewById(R.id.clinicNameAddressView);
        TextView phoneView = (TextView) v.findViewById(R.id.phoneView);
        TextView lbl = (TextView) v.findViewById(R.id.lbl);

        RxInboxItem item = getItem(position);
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        dateView.setText(sdf.format(item.getRx().getDate()));
        ServProvListItem servProvItem = item.getServProv();
        drFnameView.setText(servProvItem.getFirstName());
        drLnameView.setText(servProvItem.getLastName());
        clinicNameAddrView.setText(String.format("%s, %s", servProvItem.getServPtName(), servProvItem.getServPtLocation()));
        phoneView.setText(servProvItem.getPhone());

        String category = servProvItem.getCategory();
        if(category != null) {
            if (category.equalsIgnoreCase(getContext().getString(R.string.diagnostic_center))) {
                lbl.setText("");
            }
        }

        View medStoreLayout = v.findViewById(R.id.medStoreLayout);
        if (item.getMedStoreList() != null && item.getMedStoreList().size() > 0) {
            medStoreLayout.setVisibility(View.VISIBLE);
            TextView medStoreNameView = (TextView) v.findViewById(R.id.medStoreNameView);
            TextView fNameView = (TextView) v.findViewById(R.id.firstNameView);
            TextView lNameView = (TextView) v.findViewById(R.id.lastNameView);
            TextView medStoreAddrView = (TextView) v.findViewById(R.id.medStoreAddressView);
            TextView medStorePhView = (TextView) v.findViewById(R.id.medStorePhoneView);

            ServProvListItem medStore = item.getMedStoreList().get(0);
            medStoreNameView.setText(medStore.getServPtName());
            fNameView.setText(medStore.getFirstName());
            lNameView.setText(medStore.getLastName());
            medStoreAddrView.setText(String.format("%s,", medStore.getServPtLocation()));
            medStorePhView.setText(medStore.getPhone());
        } else {
            medStoreLayout.setVisibility(View.GONE);
        }
        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = WorkingDataStore.getBundle();
        bundle.putParcelable("rxItem", getItem(position));
        if (mSortField != null) {
            bundle.putString("sortField", mSortField);
            bundle.putBoolean("ascending", mAscending);
        }
        Intent intent = new Intent(getContext(), RxInboxItemDetailsActivity.class);
        intent.putParcelableArrayListExtra("inbox", mList);
        intent.putExtra("customer", mCust);
        intent.putExtra("feedback", RxFeedback.NONE.ordinal());
        intent.putExtra("parent-activity", getContext().getClass().getName());
        getContext().startActivity(intent);
    }

    public void setSortField(String sortField) {
        this.mSortField = sortField;
        if (sortField == null) {
            return;
        }
        Comparator<RxInboxItem> comparator = null;
        Context context = getContext();
        if (sortField.equals(context.getString(R.string.by_drname))) {
            comparator = new Comparator<RxInboxItem>() {
                @Override
                public int compare(RxInboxItem lhs, RxInboxItem rhs) {
                    int result = lhs.getServProv().getFirstName().toUpperCase().compareTo(rhs.getServProv().getFirstName().toUpperCase());
                    if (result == 0) {
                        return lhs.getServProv().getLastName().toUpperCase().compareTo(rhs.getServProv().getLastName().toUpperCase());
                    }
                    return result;
                }
            };
        } else if (sortField.equals(context.getString(R.string.by_clinic_name))) {
            comparator = new Comparator<RxInboxItem>() {
                @Override
                public int compare(RxInboxItem lhs, RxInboxItem rhs) {
                    return lhs.getServProv().getServPtName().toUpperCase().compareTo(rhs.getServProv().getServPtName().toUpperCase());
                }
            };
        } else if (sortField.equals(context.getString(R.string.by_medstore))) {
            comparator = new Comparator<RxInboxItem>() {
                @Override
                public int compare(RxInboxItem lhs, RxInboxItem rhs) {
                    ServProvListItem lhsItem = null;
                    if (lhs.getMedStoreList() != null && lhs.getMedStoreList().size() > 0) {
                        lhsItem = lhs.getMedStoreList().get(0);
                    }
                    ServProvListItem rhsItem = null;
                    if (rhs.getMedStoreList() != null && rhs.getMedStoreList().size() > 0) {
                        rhsItem = rhs.getMedStoreList().get(0);
                    }
                    if (lhsItem == null && rhsItem == null) {
                        return 0;
                    }
                    if (lhsItem == null) {
                        return 100;
                    }
                    if (rhsItem == null) {
                        return -100;
                    }
                    return lhsItem.getServPtName().toUpperCase().compareTo(rhsItem.getServPtName().toUpperCase());
                }
            };
        } else if (sortField.equals(context.getString(R.string.by_date))) {
            comparator = new Comparator<RxInboxItem>() {
                @Override
                public int compare(RxInboxItem lhs, RxInboxItem rhs) {
                    return lhs.getRx().getDate().compareTo(rhs.getRx().getDate());
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
