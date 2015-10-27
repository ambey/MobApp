package com.extenprise.mapp.service.activity;

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
import com.extenprise.mapp.net.AppStatus;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.Utility;

public class ServiceProviderHomeActivity extends Activity {

    private ServiceProvider mServiceProv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_home);

        Intent intent = getIntent();
        mServiceProv = intent.getParcelableExtra("service");

        LoginHolder.servLoginRef = mServiceProv;

        TextView welcomeView = (TextView) findViewById(R.id.viewWelcomeLbl);
        String label = welcomeView.getText().toString() + " " +
                mServiceProv.getfName() + " " +
                mServiceProv.getlName();

        welcomeView.setText(label);

        ImageView img = (ImageView) findViewById(R.id.imageDoctor);
        if(mServiceProv.getImg() != null) {
            img.setImageBitmap(Utility.getBitmapFromBytes(mServiceProv.getImg()));
        }
    }

    public void viewAppointment(View view) {
        if (AppStatus.getInstance(this).isOnline()) {
            Intent intent = new Intent(this, ViewAppointmentListActivity.class);
            intent.putExtra("service", mServiceProv);
            startActivity(intent);
        } else {
            Toast.makeText(this, "You are not online!!!!", Toast.LENGTH_LONG).show();
            Log.v("Home", "############################You are not online!!!!");
        }

    }

    public void viewProfile(View view) {
        if (AppStatus.getInstance(this).isOnline()) {
            Intent intent = new Intent(this, ServProvProfileActivity.class);
            intent.putExtra("service", mServiceProv);
            startActivity(intent);
        } else {
            Toast.makeText(this, "You are not online!!!!", Toast.LENGTH_LONG).show();
            Log.v("Home", "############################You are not online!!!!");
        }
        /*MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
        if(SearchServProv.viewWorkPlaces(dbHelper)) {*/

        /*} else {
            Utility.showAlert(this, "", "Sorry, Some problem occurs in viewing profile.");
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_service_provider_home, menu);
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
