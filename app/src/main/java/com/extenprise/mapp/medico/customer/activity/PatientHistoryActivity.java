package com.extenprise.mapp.medico.customer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.activity.LoginActivity;
import com.extenprise.mapp.medico.data.WorkingDataStore;
import com.extenprise.mapp.medico.service.activity.ViewRxActivity;
import com.extenprise.mapp.medico.service.data.AppointmentListItem;
import com.extenprise.mapp.medico.service.ui.AppontHistListAdapter;
import com.extenprise.mapp.medico.util.Utility;

import java.util.ArrayList;


public class PatientHistoryActivity extends Activity {
    private String mParentActivity;
    private ArrayList<AppointmentListItem> mApponts;
    private AppointmentListItem mAppont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_history);

        TextView viewFName = (TextView) findViewById(R.id.textViewFName);
        TextView viewLName = (TextView) findViewById(R.id.textViewLName);
        ListView lvMyAppont = (ListView) findViewById(R.id.listViewMyAppont);
        //ListView lvOthAppont = (ListView) findViewById(R.id.listViewOtherData);

        Intent intent = getIntent();
        if (savedInstanceState != null) {
            mParentActivity = savedInstanceState.getString("parent-activity");
            mAppont = savedInstanceState.getParcelable("appont");
            mApponts = savedInstanceState.getParcelableArrayList("appontList");
        }
        mParentActivity = intent.getStringExtra("parent-activity");
        mAppont = intent.getParcelableExtra("appont");
        mApponts = intent.getParcelableArrayListExtra("appontList");

        viewFName.setText(mApponts.get(0).getFirstName());
        viewLName.setText(mApponts.get(0).getLastName());

        AppontHistListAdapter adapter = new AppontHistListAdapter(this, 0, mAppont, mApponts);
        lvMyAppont.setAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("parent-activity", mParentActivity);
        outState.putParcelable("appont", mAppont);
        outState.putParcelableArrayList("appontList", mApponts);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (WorkingDataStore.getBundle().getParcelable("customer") != null) {
            getMenuInflater().inflate(R.menu.menu_patients_home_screen, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_patient_history, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.action_search:
                return true;
            case R.id.action_sign_in:
                return true;
            case R.id.logout:
                Utility.logout(getSharedPreferences("loginPrefs", MODE_PRIVATE), this, LoginActivity.class);
                WorkingDataStore.getBundle().remove("customer");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void viewRxDetails(View view) {
        View parent = (View) view.getParent();
        TextView b = (TextView) parent.findViewById(R.id.appontIdTextView);
        int position = Integer.parseInt(b.getText().toString());

        ListView lvMyAppont = (ListView) findViewById(R.id.listViewMyAppont);
        AppontHistListAdapter adapter = (AppontHistListAdapter) lvMyAppont.getAdapter();
        Intent intent = new Intent(this, ViewRxActivity.class);
        intent.putExtra("super_parent_actvity", getIntent().getParcelableExtra("parent-actvity"));
        intent.putExtra("parent-activity", this.getClass().getName());
        intent.putExtra("appont", mAppont);
        intent.putParcelableArrayListExtra("appontList", mApponts);
        intent.putExtra("pastAppont", adapter.getItem(position));
        startActivity(intent);
    }

    public Intent getParentActivityIntent() {
        Intent intent;
        if (mParentActivity != null) {
            try {
                intent = new Intent(this, Class.forName(mParentActivity));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                intent = super.getParentActivityIntent();
            }
        } else {
            intent = super.getParentActivityIntent();
        }
        if (intent != null) {
            intent.putExtra("appont", mAppont);
            intent.putExtra("appontList", mApponts);
        }
        return intent;
    }

}
