package com.extenprise.mapp.medico.customer.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.extenprise.mapp.medico.LoginHolder;
import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.customer.data.Customer;
import com.extenprise.mapp.medico.util.Utility;


public class PatientsHomeScreenActivity extends Activity {

    private Customer mCustomer;
    private boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_home_screen);

        mCustomer = LoginHolder.custLoginRef;

        TextView mlastDate = (TextView) findViewById(R.id.textViewDate);
        TextView mlastTime = (TextView) findViewById(R.id.textViewTime);

        SharedPreferences prefs = getSharedPreferences("customer" + "lastVisit" +
                mCustomer.getSignInData().getPhone(), MODE_PRIVATE);
        mlastDate.setText(prefs.getString("lastVisitDate", "--"));
        mlastTime.setText(prefs.getString("lastVisitTime", "--"));
        Utility.setLastVisit(prefs);

        TextView welcomeView = (TextView) findViewById(R.id.viewWelcomeLbl);
        String label = welcomeView.getText().toString() + " " +
                mCustomer.getfName() + " " +
                mCustomer.getlName();
        welcomeView.setText(label);

        ImageView img = (ImageView) findViewById(R.id.imagePatient);
        if (mCustomer.getPhoto() != null) {
            img.setImageBitmap(Utility.getBitmapFromBytes(mCustomer.getPhoto()));
        }

        //Utility.setLastVisit(this, mCustomer.getSignInData().getPhone(), "customer");
    }

    public void viewRxList(View view) {
        Intent intent = new Intent(this, ViewRxListActivity.class);
        intent.putExtra("customer", mCustomer);
        startActivity(intent);
    }

    public void viewAppointments(View view) {
        Intent intent = new Intent(this, ViewAppointmentListActivity.class);
        intent.putExtra("customer", mCustomer);
        startActivity(intent);
    }

    public void viewProfile(View view) {
        Intent intent = new Intent(this, PatientProfileActivity.class);
        intent.putExtra("customer", mCustomer);
        startActivity(intent);
    }

    public void searchDoc(View view) {
        Intent intent = new Intent(this, SearchServProvActivity.class);
        intent.putExtra("customer", mCustomer);
        intent.putExtra("parent-activity", this.getClass().getName());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_patients_home_screen, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_settings:
                return true;
            case R.id.logout:
                Utility.logout(getSharedPreferences("loginPrefs", MODE_PRIVATE), this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (exit) {
            Log.v("onBackPressed", "PatientsHomeScreenActivity called.. calling finish.");
            finish(); // finish activity
            moveTaskToBack(true); // exist app
            //finish(); // finish activity
        } else {
            Utility.showMessage(this, R.string.msg_press_back_button);
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        /*Intent intent = super.getParentActivityIntent();
        if(intent == null) {
            return null;
        }
        intent.putParcelableArrayListExtra("inbox", getIntent().getParcelableArrayListExtra("inbox"));*/
        Log.v("getParentActivityIntent", "PatientsHomeScreenActivity called");
        return null;
    }

    private void refresh() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
