package com.extenprise.mapp.customer.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.customer.data.Customer;
import com.extenprise.mapp.data.RxFeedback;
import com.extenprise.mapp.service.activity.RxInboxItemDetailsActivity;
import com.extenprise.mapp.service.activity.ViewRxActivity;
import com.extenprise.mapp.service.data.RxInboxItem;
import com.extenprise.mapp.service.data.ServProvListItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by ambey on 10/10/15.
 */
public class RxListAdapter extends ArrayAdapter<RxInboxItem> implements AdapterView.OnItemClickListener {
    private ArrayList<RxInboxItem> mList;
    private Customer mCust;

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.layout_cust_view_rx, null);
        }
        TextView dateView = (TextView) v.findViewById(R.id.dateView);
        TextView fnameView = (TextView) v.findViewById(R.id.drFNameView);
        TextView lnameView = (TextView) v.findViewById(R.id.drLNameView);
        TextView clinicNameAddrView = (TextView) v.findViewById(R.id.clinicNameAddressView);
        TextView phoneView = (TextView) v.findViewById(R.id.phoneView);

        RxInboxItem item = mList.get(position);
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        dateView.setText(sdf.format(item.getRx().getDate()));
        ServProvListItem servProvItem = item.getServProv();
        fnameView.setText(servProvItem.getFirstName());
        lnameView.setText(servProvItem.getLastName());
        clinicNameAddrView.setText(String.format("%s, %s", servProvItem.getServPtName(), servProvItem.getServPtLocation()));
        phoneView.setText(servProvItem.getPhone());

        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getContext(), RxInboxItemDetailsActivity.class);
        intent.putParcelableArrayListExtra("inbox", mList);
        intent.putExtra("position", position);
        intent.putExtra("customer", mCust);
        intent.putExtra("feedback", RxFeedback.NONE.ordinal());
        intent.putExtra("parent-activity", getContext().getClass().getName());
        getContext().startActivity(intent);
    }
}
