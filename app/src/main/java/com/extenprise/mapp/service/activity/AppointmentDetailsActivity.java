package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.customer.activity.PatientHistoryActivity;
import com.extenprise.mapp.customer.data.Customer;
import com.extenprise.mapp.data.Appointment;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.util.DBUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class AppointmentDetailsActivity extends Activity {

    private Appointment mAppont;
    private ArrayList<Appointment> mOtherApponts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_details);

        View view = findViewById(R.id.appointmentLayout);
        TextView fNameView = (TextView) view.findViewById(R.id.patientFNameTextView);
        TextView lNameView = (TextView) view.findViewById(R.id.patientLNameTextView);
        TextView timeView = (TextView) view.findViewById(R.id.appointmentTimeTextView);
        TextView genderView = (TextView) view.findViewById(R.id.patientGenderTextView);
        TextView ageView = (TextView) view.findViewById(R.id.patientAgeTextView);
        TextView wtView = (TextView) view.findViewById(R.id.patientWeightTextView);
        TextView dateView = (TextView) findViewById(R.id.appointmentDateTextView);
        Button rxButton = (Button) findViewById(R.id.rXButton);
        Button uploadRxButton = (Button) findViewById(R.id.uploadScanRxButton);

        mAppont = LoginHolder.appointment;
        Customer customer = mAppont.getCustomer();

        String dateStr = mAppont.getDateOfAppointment();
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        try {
            Date date = sdf.parse(dateStr);
            date.setTime(date.getTime() + mAppont.getFromTime() * 60 * 1000);
            if (date.after(today)) {
                rxButton.setEnabled(false);
                uploadRxButton.setEnabled(false);

                rxButton.setBackgroundResource(R.drawable.disabled_button);
                uploadRxButton.setBackgroundResource(R.drawable.disabled_button);
            } else if (date.before(today)) {
                int day = cal.get(Calendar.DAY_OF_MONTH);
                cal.setTime(date);
                int apptDay = cal.get(Calendar.DAY_OF_MONTH);
                if (apptDay != day) {
                    rxButton.setEnabled(false);
                    uploadRxButton.setEnabled(true);

                    rxButton.setBackgroundResource(R.drawable.disabled_button);
                    uploadRxButton.setBackgroundResource(R.drawable.button);
                } else {
                    rxButton.setEnabled(true);
                    uploadRxButton.setEnabled(true);

                    rxButton.setBackgroundResource(R.drawable.button);
                    uploadRxButton.setBackgroundResource(R.drawable.button);
                }
            } else {
                rxButton.setEnabled(false);
                uploadRxButton.setEnabled(false);

                rxButton.setBackgroundResource(R.drawable.disabled_button);
                uploadRxButton.setBackgroundResource(R.drawable.disabled_button);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        dateView.setText(dateStr);
        fNameView.setText(customer.getfName());
        lNameView.setText(customer.getlName());
        timeView.setText(String.format("%02d:%02d",
                mAppont.getFromTime() / 60, mAppont.getFromTime() % 60));
        genderView.setText(customer.getGender());
        ageView.setText("" + customer.getAge());
        wtView.setText(String.format("%.1f", customer.getWeight()));

        mOtherApponts = DBUtil.getOtherAppointments(new MappDbHelper(getApplicationContext()),
                mAppont.getId());
        View pastAppontLayout = findViewById(R.id.pastAppointmentLayout);
        if (mOtherApponts.size() <= 1) {
            Button viewMoreButton = (Button) findViewById(R.id.viewMoreButton);
            if (viewMoreButton == null) {
                viewMoreButton = (Button) pastAppontLayout.findViewById(R.id.viewMoreButton);
            }
            viewMoreButton.setEnabled(false);
            viewMoreButton.setBackgroundResource(R.drawable.disabled_button);
        }
        if (mOtherApponts.size() > 0) {
            TextView dateOthView = (TextView) pastAppontLayout.findViewById(R.id.dateTextView);
            dateOthView.setText(mOtherApponts.get(0).getDateOfAppointment());
        } else {
            pastAppontLayout.setVisibility(View.INVISIBLE);
            TextView msgView = (TextView) findViewById(R.id.viewMsg);
            msgView.setVisibility(View.VISIBLE);
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

    public void showRxDetails(View view) {
        ViewRxActivity.appointment = mOtherApponts.get(0);
        ViewRxActivity.appointment.setCustomer(mAppont.getCustomer());
        Intent intent = new Intent(this, ViewRxActivity.class);
        startActivity(intent);
    }

    public void showPatientHistory(View view) {
        Intent intent = new Intent(this, PatientHistoryActivity.class);
        startActivity(intent);
    }
}
