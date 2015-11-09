package com.extenprise.mapp.service.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.data.RxItem;
import com.extenprise.mapp.service.activity.RxActivity;
import com.extenprise.mapp.service.activity.RxInboxItemDetailsActivity;
import com.extenprise.mapp.service.data.RxInboxItem;

/**
 * Created by ambey on 10/10/15.
 */
public class RxItemListAdapter extends ArrayAdapter<RxItem> implements AdapterView.OnItemClickListener {
    private RxInboxItem mRxInboxItem;
    private boolean mFeedback;

    public RxItemListAdapter(Context context, int resource, RxInboxItem rxInboxItem, boolean feedback) {
        super(context, resource);
        mRxInboxItem = rxInboxItem;
        mFeedback = feedback;
    }

    @Override
    public int getCount() {
        return mRxInboxItem.getRx().getRxItemCount();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.layout_rx_item, null);
        }
        TextView nameView = (TextView) v.findViewById(R.id.viewDrugName);
        TextView strengthView = (TextView) v.findViewById(R.id.viewDrugStrength);
        TextView kindView = (TextView) v.findViewById(R.id.viewDrugKind);

        TextView mView = (TextView) v.findViewById(R.id.mView);
        TextView aView = (TextView) v.findViewById(R.id.aView);
        TextView eView = (TextView) v.findViewById(R.id.eView);

        TextView doseFreqView = (TextView) v.findViewById(R.id.viewDoseFreq);
        TextView courseView = (TextView) v.findViewById(R.id.viewCourseDur);

        CheckBox availableCB = (CheckBox) v.findViewById(R.id.checkboxAvailable);
        TextView availableView = (TextView) v.findViewById(R.id.viewAvailable);

        final RxItem item = mRxInboxItem.getRx().getItems().get(position);

        if (mFeedback) {
            availableCB.setVisibility(View.GONE);
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
                }
            });
        }
        nameView.setText(item.getDrugName().toUpperCase());
        strengthView.setText(item.getDrugStrength());
        kindView.setText(item.getDrugForm());

        mView.setText(item.isMorning() ? "1" : "0");
        aView.setText(item.isAfternoon() ? "1" : "0");
        eView.setText(item.isEvening() ? "1" : "0");

        doseFreqView.setText(String.format("%s: %s", getContext().getString(R.string.dose), item.getDoseQty()));
        courseView.setText(String.format("%d %s", item.getCourseDur(), getContext().getString(R.string.days)));

        return v;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(!mFeedback) {
            return;
        }
        Intent intent = new Intent(getContext(), RxActivity.class);
        intent.putExtra("feedback", true);
        intent.putExtra("parent-activity", RxInboxItemDetailsActivity.class.getName());
        intent.putExtra("position", position);
        intent.putExtra("inboxItem", mRxInboxItem);
        getContext().startActivity(intent);
    }
}
