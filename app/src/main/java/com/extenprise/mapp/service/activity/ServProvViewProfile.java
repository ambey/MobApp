package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.util.DBUtil;
import com.extenprise.mapp.util.UIUtility;


public class ServProvViewProfile extends Activity {

    TextView mDocName;
    EditText mFname, mLname, mQualification, mExp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servprov_profile);

        mDocName = (TextView) findViewById(R.id.textviewDocname);
        mFname = (EditText) findViewById(R.id.textViewFName);
        mLname = (EditText) findViewById(R.id.textViewLName);
        mQualification = (EditText) findViewById(R.id.textViewEducation);
        mExp = (EditText) findViewById(R.id.textViewWorkExp);

        mDocName.setText(LoginHolder.servLoginRef.getfName() + " " +
                LoginHolder.servLoginRef.getlName());
        mFname.setText(LoginHolder.servLoginRef.getfName());
        mLname.setText(LoginHolder.servLoginRef.getlName());
        mQualification.setText(LoginHolder.servLoginRef.getQualification());

        LoginHolder.spsspt = DBUtil.getSPSSPT(new MappDbHelper(getApplicationContext()));
        mExp.setText("" + LoginHolder.spsspt.getServProvHasService().getExperience());
    }

    public void viewBasicInfo(View view) {
        Intent intent = new Intent(this, ServProvBasicInfo.class);
        startActivity(intent);
    }

    public void viewWorkDetails(View view) {
        Intent intent = new Intent(this, ServProvViewWorkDetails.class);
        startActivity(intent);
    }

    public void editName(View view) {
        mFname.setEnabled(true);
        mLname.setEnabled(true);
    }

    public void editQualification(View view) {
        mQualification.setEnabled(true);
    }

    public void editExp(View view) {
        mExp.setEnabled(true);
    }

    public void updateProfile(View view) {


        mFname.setEnabled(false);
        mLname.setEnabled(false);
        mQualification.setEnabled(false);
        mExp.setEnabled(false);
        UIUtility.showAlert(this, "", "Updated");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_profile_main, menu);
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
