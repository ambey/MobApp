package com.extenprise.mapp.customer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.customer.data.Customer;
import com.extenprise.mapp.net.AppStatus;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.service.activity.SearchServProvActivity;
import com.extenprise.mapp.util.Utility;


public class PatientsHomeScreenActivity extends Activity {

    private Customer mCustomer;
    private Boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_home_screen);

        Intent intent = getIntent();
        mCustomer = intent.getParcelableExtra("customer");

        LoginHolder.custLoginRef = mCustomer;

        TextView welcomeView = (TextView) findViewById(R.id.viewWelcomeLbl);
        String label = welcomeView.getText().toString() + " " +
                mCustomer.getfName() + " " +
                mCustomer.getlName();

        welcomeView.setText(label);

        ImageView img = (ImageView) findViewById(R.id.imagePatient);
        if(mCustomer.getImg() != null) {
            img.setImageBitmap(Utility.getBitmapFromBytes(mCustomer.getImg()));
        }
    }

    public void viewProfile(View view) {
        Intent intent = new Intent(this, PatientProfileActivity.class);
        intent.putExtra("customer", mCustomer);
        startActivity(intent);
    }

    public void searchDoc(View view) {
        Intent intent = new Intent(this, SearchServProvActivity.class);
        intent.putExtra("customer", mCustomer);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_patients_home_screen, menu);
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


    @Override
    public void onBackPressed() {
        if (exit) {
            Log.v("onBackPressed", "PatientsHomeScreenActivity called.. calling finish.");
            finish(); // finish activity
            moveTaskToBack(true); // exist app
            //finish(); // finish activity
        } else {
            Utility.showMessage(this, R.string.press_back_button);
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
}
