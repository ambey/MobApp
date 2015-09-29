package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.extenprise.mapp.R;


public class ServProvBasicInfo extends Activity {

    EditText mMobNo, mEmailID, mRegNo;
    Spinner mGender;
    TextView mDOB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servprov_basicinfo);

        mMobNo = (EditText) findViewById(R.id.editTextMobNum);
        mEmailID = (EditText) findViewById(R.id.editTextEmail);
        mDOB = (TextView) findViewById(R.id.editTextDob);
        //mGender = (Spinner) findViewById(R.id.editTextGender);
        mRegNo = (EditText) findViewById(R.id.editTextRegNum);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_profile_basicinfo, menu);
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
}
