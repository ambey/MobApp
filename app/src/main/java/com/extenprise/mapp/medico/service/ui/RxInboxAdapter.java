package com.extenprise.mapp.medico.service.ui;

import android.app.Activity;
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
import com.extenprise.mapp.medico.data.ReportServiceStatus;
import com.extenprise.mapp.medico.data.Rx;
import com.extenprise.mapp.medico.data.RxFeedback;
import com.extenprise.mapp.medico.data.RxItem;
import com.extenprise.mapp.medico.data.WorkingDataStore;
import com.extenprise.mapp.medico.service.activity.RxInboxItemDetailsActivity;
import com.extenprise.mapp.medico.service.data.RxInboxItem;
import com.extenprise.mapp.medico.service.data.ServProvListItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by ambey on 30/10/15.
 */
public class RxInboxAdapter extends ArrayAdapter<RxInboxItem> implements AdapterView.OnItemClickListener {

    private ArrayList<BitSet> mAvailMaps;
    private ArrayList<RxInboxItem> mRxList;
    private ArrayList<RxInboxItem> mSortedRxList;
    private String mSortField;
    private boolean mAscending;
    private RxFeedback mFeedback;

    public RxInboxAdapter(Context context, int resource, ArrayList<RxInboxItem> list, RxFeedback feedback) {
        super(context, resource);
        mRxList = list;
        this.mFeedback = feedback;
        if (feedback == RxFeedback.GIVE_FEEDBACK) {
            mAvailMaps = new ArrayList<>(mRxList.size());
            for (int i = 0; i < mRxList.size(); i++) {
                RxInboxItem inboxItem = mRxList.get(i);
                Rx rx = inboxItem.getRx();
                BitSet availMap = new BitSet(rx.getRxItemCount());
                ArrayList<RxItem> rxItems = rx.getItems();
                for (int j = 0; j < rx.getRxItemCount(); j++) {
                    availMap.set(j, (rxItems.get(j).getAvailable() == 1));
                }
                mAvailMaps.add(i, availMap);
            }
        }
    }

    @Override
    public int getCount() {
        try {
            return mRxList.size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (mFeedback == RxFeedback.VIEW_FEEDBACK) {
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
        if (mFeedback == RxFeedback.VIEW_FEEDBACK) {
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
        RxInboxItem item = getItem(position);
        int status = item.getReportService().getStatus();
        statusView.setText(ReportServiceStatus.getStatusString(getContext(), status));
        if (mFeedback == RxFeedback.VIEW_FEEDBACK) {
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
            if (mSortField == null) {
                return mRxList.get(position);
            }
            return mSortedRxList.get(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
        intent.putParcelableArrayListExtra("inbox", mRxList);
        intent.putExtra("feedback", mFeedback.ordinal());
        if (mAvailMaps != null) {
            intent.putExtra("availMap", mAvailMaps.get(position));
        }
        Activity myActivity = (Activity)getContext();
        intent.putExtra("origin_activity", myActivity.getIntent().getStringExtra("parent-activity"));
        myActivity.startActivity(intent);
    }

    public void setAscending(boolean ascending) {
        this.mAscending = ascending;
    }

    public void setSortField(String sortField) {
        this.mSortField = sortField;
        if (sortField == null) {
            return;
        }
        Comparator<RxInboxItem> comparator = null;
        Context context = getContext();
        if (sortField.equals(context.getString(R.string.by_name))) {
            comparator = new Comparator<RxInboxItem>() {
                @Override
                public int compare(RxInboxItem lhs, RxInboxItem rhs) {
                    int result = lhs.getCustomer().getfName().toUpperCase().compareTo(rhs.getCustomer().getfName().toUpperCase());
                    if (result == 0) {
                        return lhs.getCustomer().getlName().toUpperCase().compareTo(rhs.getCustomer().getlName().toUpperCase());
                    }
                    return result;
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
        } else if (sortField.equals(context.getString(R.string.by_drname))) {
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
        }
        mSortedRxList = new ArrayList<>();
        mSortedRxList.addAll(mRxList);
        if (comparator != null) {
            if (!mAscending) {
                comparator = Collections.reverseOrder(comparator);
            }
            Collections.sort(mSortedRxList, comparator);
            notifyDataSetChanged();
        }
    }

}
