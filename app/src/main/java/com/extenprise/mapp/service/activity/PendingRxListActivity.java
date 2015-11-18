package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.util.DateChangeListener;
import com.extenprise.mapp.util.Utility;


public class PendingRxListActivity extends Activity implements DateChangeListener {

    private TextView mRxDate;
    private String mSelectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_rx_list);

        mRxDate = (TextView) findViewById(R.id.textViewRxDate);
        mSelectedDate = Utility.setCurrentDateOnView(mRxDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pending_rx_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDatePicker(View view) {
        Utility.datePicker(view, mRxDate, this);
    }

    @Override
    public void datePicked(String date) {
        mSelectedDate = date;
    }
}
