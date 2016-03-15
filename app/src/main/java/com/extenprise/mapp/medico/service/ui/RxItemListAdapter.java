package com.extenprise.mapp.medico.service.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.data.Rx;
import com.extenprise.mapp.medico.data.RxFeedback;
import com.extenprise.mapp.medico.data.RxItem;
import com.extenprise.mapp.medico.data.WorkingDataStore;
import com.extenprise.mapp.medico.service.activity.RxActivity;
import com.extenprise.mapp.medico.service.activity.RxInboxItemDetailsActivity;
import com.extenprise.mapp.medico.service.data.RxInboxItem;

import java.util.ArrayList;
import java.util.BitSet;

/**
 * Created by ambey on 10/10/15.
 */
public class RxItemListAdapter extends ArrayAdapter<RxItem> implements AdapterView.OnItemClickListener {
    private ArrayList<RxInboxItem> mInbox;
    private RxInboxItem mRxInboxItem;
    private int mFeedback;
    private BitSet mAvailMap;
    private ArrayList<RxItem> mRxItems;

    public RxItemListAdapter(Context context, int resource, ArrayList<RxInboxItem> rxInbox, RxInboxItem rxItem, int feedback) {
        super(context, resource);
        mInbox = rxInbox;
        mRxInboxItem = rxItem;
        mFeedback = feedback;
        if (mFeedback == RxFeedback.GIVE_FEEDBACK) {
            Rx rx = mRxInboxItem.getRx();
            mAvailMap = new BitSet(rx.getRxItemCount());
            ArrayList<RxItem> rxItems = rx.getItems();
            for (int j = 0; j < rx.getRxItemCount(); j++) {
                mAvailMap.set(j, (rxItems.get(j).getAvailable() == 1));
            }
        } else {
            mRxItems = new ArrayList<>();
            int idMedStore = WorkingDataStore.getBundle().getInt("idMedStore");
            for (int i = 0; i < mRxInboxItem.getRx().getRxItemCount(); i++) {
                RxItem rItem = mRxInboxItem.getRx().getItems().get(i);
                if (rItem.getIdServProvHasServPt() == idMedStore) {
                    mRxItems.add(rItem);
                }
            }
        }
    }

    @Override
    public RxItem getItem(int position) {
        if (mFeedback == RxFeedback.GIVE_FEEDBACK) {
            return mRxInboxItem.getRx().getItems().get(position);
        }
        return mRxItems.get(position);
    }

    @Override
    public int getCount() {
        int count = 0;
        try {
            if (mFeedback == RxFeedback.GIVE_FEEDBACK) {
                count = mRxInboxItem.getRx().getRxItemCount();
            } else {
                count = mRxItems.size();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.layout_rx_item, null);
        }
        TextView nameView = (TextView) v.findViewById(R.id.viewDrugName);
        TextView kindView = (TextView) v.findViewById(R.id.viewDrugKind);

        TextView mView = (TextView) v.findViewById(R.id.mView);
        TextView aView = (TextView) v.findViewById(R.id.aView);
        TextView eView = (TextView) v.findViewById(R.id.eView);

        TextView courseView = (TextView) v.findViewById(R.id.viewCourseDur);

        RadioButton availableCB = (RadioButton) v.findViewById(R.id.rbAvailable);
        RadioButton notAvailableCB = (RadioButton) v.findViewById(R.id.rbNotAvailable);
        TextView availableView = (TextView) v.findViewById(R.id.viewAvailable);

        final RxItem item = getItem(position);

        if (mFeedback != RxFeedback.GIVE_FEEDBACK) {
            availableCB.setVisibility(View.GONE);
            notAvailableCB.setVisibility(View.GONE);
            int resId = R.string.available;
            int color = Color.GREEN;
            if (item.getAvailable() == 0) {
                resId = R.string.not_available;
                color = Color.RED;
            }
            availableView.setText(resId);
            availableView.setTextColor(color);
        } else {
            availableView.setVisibility(View.GONE);
            availableCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    item.setAvailable(isChecked ? 1 : 0);
                    mAvailMap.set(position, isChecked);
                    RxInboxItemDetailsActivity activity = (RxInboxItemDetailsActivity) getContext();
                    BitSet origAvailMap = activity.getAvailMap();
                    activity.setAvailabilityChanged(!origAvailMap.equals(mAvailMap));
                }
            });
            if (item.getAvailable() == 1) {
                availableCB.setChecked(true);
            } else {
                notAvailableCB.setChecked(true);
            }
        }/* else {
            availableCB.setVisibility(View.GONE);
            availableView.setVisibility(View.GONE);
        }*/
        nameView.setText(item.getDrugName().toUpperCase());
        kindView.setText(item.getDrugForm());

        mView.setText(item.isMorning() ? item.getmDose() : "0");
        aView.setText(item.isAfternoon() ? item.getaDose() : "0");
        eView.setText(item.isEvening() ? item.geteDose() : "0");

        courseView.setText(String.format("%d %s", item.getCourseDur(), getContext().getString(R.string.days)));

        return v;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mFeedback != RxFeedback.VIEW_FEEDBACK) {
            return;
        }
        Bundle bundle = WorkingDataStore.getBundle();
        bundle.putParcelable("rxItem", mRxInboxItem);
        bundle.putParcelableArrayList("inbox", mInbox);

        Intent intent = new Intent(getContext(), RxActivity.class);
        intent.putExtra("feedback", RxFeedback.VIEW_FEEDBACK);
        intent.putExtra("parent-activity", RxInboxItemDetailsActivity.class.getName());
        intent.putExtra("rxItemPos", position);
        //intent.putParcelableArrayListExtra("inbox", mInbox);
        getContext().startActivity(intent);
    }
}
