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
import com.extenprise.mapp.data.Appointment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AppointmentDetailsActivity extends Activity {

    TextView mDateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_details);

        mDateTextView = (TextView) findViewById(R.id.dateTextView);

        Appointment appointment = LoginHolder.appointment;
        try {
            DateFormat df = new SimpleDateFormat("dd/MM/yy");
            Date dateOfAppointment = appointment.getDateOfAppointment();
            mDateTextView.setText(df.format(dateOfAppointment));
        } catch (NullPointerException x) {
            x.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_appointment_details, menu);
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

    public void showRxActivity(View view) {
        Intent intent = new Intent(this, RxActivity.class);
        intent.putExtra("parent-activity", AppointmentDetailsActivity.class.toString());
        startActivity(intent);
    }
}
