package com.extenprise.mapp.service.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.data.Rx;
import com.extenprise.mapp.data.RxItem;

/**
 * Created by ambey on 10/10/15.
 */
public class RxItemListAdapter extends ArrayAdapter<RxItem> {
    private Rx mRx;
    private boolean mFeedback;

    public RxItemListAdapter(Context context, int resource, Rx rx, boolean feedback) {
        super(context, resource);
        mRx = rx;
        mFeedback = feedback;
    }

    @Override
    public int getCount() {
        return mRx.getRxItemCount();
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
        availableCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mRx.getItems().get(position).setAvailable(isChecked);
            }
        });
        if (mFeedback) {
            availableCB.setChecked(mRx.getItems().get(position).isAvailable());
            availableCB.setEnabled(false);
        }
        RxItem item = mRx.getItems().get(position);
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
}
