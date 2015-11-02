package com.extenprise.mapp.service.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.data.ReportServiceStatus;
import com.extenprise.mapp.service.data.RxInboxItem;
import com.extenprise.mapp.service.ui.RxItemListAdapter;

import java.text.SimpleDateFormat;

public class RxInboxItemDetailsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_inbox_item_details);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        RxInboxItem item = intent.getParcelableExtra("inboxItem");

        View layoutAppont = findViewById(R.id.layoutAppont);
        layoutAppont.setVisibility(View.INVISIBLE);

        TextView statusView = (TextView) findViewById(R.id.statusView);
        TextView dateView = (TextView) findViewById(R.id.dateTextView);
        TextView custNameView = (TextView) findViewById(R.id.custNameView);
        TextView custPhoneView = (TextView) findViewById(R.id.custPhoneView);
        TextView drNameView = (TextView) findViewById(R.id.drNameView);
        TextView drClinicView = (TextView) findViewById(R.id.drClinicView);
        TextView drPhoneView = (TextView) findViewById(R.id.drPhoneView);

        int status = item.getReportService().getStatus();
        if(ReportServiceStatus.STATUS_PENDING.ordinal() == status) {
            status = ReportServiceStatus.STATUS_INPROCESS.ordinal();
        }
        statusView.setText(ReportServiceStatus.getStatusString(this, status));

        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        dateView.setText(sdf.format(item.getRx().getDate()));
        drNameView.setText(String.format("%s %s.", item.getServProv().getLastName().toUpperCase(),
                item.getServProv().getFirstName().substring(0, 1).toUpperCase()));
        custNameView.setText(String.format("%s %s.", item.getCustomer().getlName().toUpperCase(),
                item.getCustomer().getfName().substring(0, 1).toUpperCase()));
        custPhoneView.setText(item.getCustomer().getSignInData().getPhone());
        drClinicView.setText(String.format("%s, %s", item.getServProv().getServPtName(),
                item.getServProv().getServPtLocation()));
        drPhoneView.setText(String.format("(%s)", item.getServProv().getPhone()));

        ListView listView = (ListView)findViewById(R.id.listRxItems);
        RxItemListAdapter adapter = new RxItemListAdapter(this, 0, item.getRx());
        listView.setAdapter(adapter);

    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        Intent intent = super.getParentActivityIntent();
        if(intent == null) {
            return null;
        }
        intent.putParcelableArrayListExtra("inbox", getIntent().getParcelableArrayListExtra("inbox"));
        return intent;
    }
}
