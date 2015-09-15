package com.extenprise.mapp.customer.activity;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.util.UIUtility;


public class PatientsHomeScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_home_screen);

        TextView welcomeView = (TextView) findViewById(R.id.viewWelcomeLbl);
        String label = welcomeView.getText().toString() + " " +
                LoginHolder.custLoginRef.getfName() + " " +
                LoginHolder.custLoginRef.getlName();

        welcomeView.setText(label);

        ImageView img = (ImageView) findViewById(R.id.imagePatient);
        if(LoginHolder.custLoginRef.getImg() != null) {
            img.setImageBitmap(UIUtility.getBitmapFromBytes(LoginHolder.custLoginRef.getImg()));
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
