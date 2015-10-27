package com.extenprise.mapp.customer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
        if (AppStatus.getInstance(this).isOnline()) {
            Intent intent = new Intent(this, PatientProfileActivity.class);
            intent.putExtra("customer", mCustomer);
            startActivity(intent);
        } else {
            Toast.makeText(this, "You are not online!!!!", Toast.LENGTH_LONG).show();
            Log.v("Home", "############################You are not online!!!!");
        }
    }

    public void searchDoc(View view) {
        if (AppStatus.getInstance(this).isOnline()) {
            Intent intent = new Intent(this, SearchServProvActivity.class);
            intent.putExtra("customer", mCustomer);
            startActivity(intent);
        } else {
            Toast.makeText(this, "You are not online!!!!", Toast.LENGTH_LONG).show();
            Log.v("Home", "############################You are not online!!!!");
        }
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
}
