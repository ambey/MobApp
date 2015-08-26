package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;

public class ServiceProviderHomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_home);

        TextView welcomeView = (TextView) findViewById(R.id.viewWelcomeLbl);
        String label = welcomeView.getText().toString() + " " +
                LoginHolder.servLoginRef.getfName() + " " +
                LoginHolder.servLoginRef.getlName();

        welcomeView.setText(label);
    }

    public void viewAppointment(View view) {
        Intent intent = new Intent(this, ViewAppointmentListActivity.class);
        startActivity(intent);
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
