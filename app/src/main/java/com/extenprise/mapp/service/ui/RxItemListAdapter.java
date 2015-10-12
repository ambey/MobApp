package com.extenprise.mapp.service.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.data.Rx;
import com.extenprise.mapp.data.RxItem;

/**
 * Created by ambey on 10/10/15.
 */
public class RxItemListAdapter extends ArrayAdapter<RxItem> {
    private Rx mRx;

    public RxItemListAdapter(Context context, int resource, Rx rx) {
        super(context, resource);
        mRx = rx;
    }

    @Override
    public int getCount() {
        return mRx.getRxItemCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.layout_rx_item, null);
        }
        TextView srNoView = (TextView)v.findViewById(R.id.viewSrNo);
        TextView nameView = (TextView)v.findViewById(R.id.viewDrugName);
        TextView doseView = (TextView)v.findViewById(R.id.viewDoseQty);
        TextView courseView = (TextView)v.findViewById(R.id.viewCourseDur);

        RxItem item = mRx.getItems().get(position);
        srNoView.setText("" + (position + 1));
        nameView.setText(item.getDrugName());
        doseView.setText("" + item.getDoseQty());
        courseView.setText("" + item.getCourseDur());

        return v;
    }
}
