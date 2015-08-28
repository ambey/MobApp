package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.util.UIUtility;

public class RxActivity extends Activity {
    private TextView mMTime;
    private TextView mATime;
    private TextView mETime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx);

        mMTime = (TextView) findViewById(R.id.timeMTextView);
        mATime = (TextView) findViewById(R.id.timeATextView);
        mETime = (TextView) findViewById(R.id.timeETextView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rx, menu);
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

    public void showMTimePicker(View view) {
        UIUtility.timePicker(view, mMTime);
    }

    public void showATimePicker(View view) {
        UIUtility.timePicker(view, mATime);
    }

    public void showETimePicker(View view) {
        UIUtility.timePicker(view, mETime);
    }
}
