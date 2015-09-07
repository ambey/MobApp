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

    private int mAppontId;
    private int mCustId;

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

        Intent intent = getIntent();
        mAppontId = intent.getIntExtra("appont_id", -1);
        mCustId = intent.getIntExtra("cust_id", -1);

        MappDbHelper dbHelper = new MappDbHelper(this);
        Appointment appointment = DBUtil.getAppointment(dbHelper, mAppontId);
        Customer customer = appointment.getCustomer();

        String dateStr = appointment.getDateOfAppointment();
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        try {
            Date date = sdf.parse(dateStr);
            date.setTime(date.getTime() + appointment.getFromTime() * 60 * 1000);
            if (date.after(today)) {
                rxButton.setEnabled(false);
                uploadRxButton.setEnabled(false);

                rxButton.setBackgroundResource(R.drawable.inactive_button);
                uploadRxButton.setBackgroundResource(R.drawable.inactive_button);
            } else if (date.before(today)) {
                int day = cal.get(Calendar.DAY_OF_MONTH);
                cal.setTime(date);
                int apptDay = cal.get(Calendar.DAY_OF_MONTH);
                if (apptDay != day) {
                    rxButton.setEnabled(false);
                    uploadRxButton.setEnabled(true);

                    rxButton.setBackgroundResource(R.drawable.inactive_button);
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

                rxButton.setBackgroundResource(R.drawable.inactive_button);
                uploadRxButton.setBackgroundResource(R.drawable.inactive_button);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        dateView.setText(dateStr);
        fNameView.setText(customer.getfName());
        lNameView.setText(customer.getlName());
        timeView.setText(String.format("%02d:%02d",
                appointment.getFromTime() / 60, appointment.getFromTime() % 60));
        genderView.setText(customer.getGender());
        ageView.setText("" + customer.getAge());
        wtView.setText(String.format("%.1f", customer.getWeight()));

        ArrayList<Appointment> othApponts = DBUtil.getOtherAppointments(new MappDbHelper(this),
                appointment.getId());
        View pastAppontLayout = findViewById(R.id.pastAppointmentLayout);
        Button viewMoreButton = (Button) findViewById(R.id.viewMoreButton);
        if (viewMoreButton == null) {
            viewMoreButton = (Button) pastAppontLayout.findViewById(R.id.viewMoreButton);
        }
        viewMoreButton.setVisibility(View.VISIBLE);
        if (othApponts.size() <= 1) {
            viewMoreButton.setEnabled(false);
            viewMoreButton.setBackgroundResource(R.drawable.inactive_button);
        }
        if (othApponts.size() > 0) {
            TextView dateOthView = (TextView) pastAppontLayout.findViewById(R.id.dateTextView);
            TextView idOthView = (TextView) pastAppontLayout.findViewById(R.id.appontIdTextView);
            dateOthView.setText(othApponts.get(0).getDateOfAppointment());
            idOthView.setText("" + othApponts.get(0).getId());
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
        intent.putExtra("parent-activity", getClass().getName());
        intent.putExtra("appont_id", mAppontId);
        intent.putExtra("cust_id", mCustId);
        startActivity(intent);
    }

    public void showRxDetails(View view) {
        Intent intent = new Intent(this, ViewRxActivity.class);
        intent.putExtra("appont_id", mAppontId);
        intent.putExtra("cust_id", mCustId);
        startActivity(intent);
    }

    public void showPatientHistory(View view) {
        Intent intent = new Intent(this, PatientHistoryActivity.class);
        intent.putExtra("sp_id", LoginHolder.servLoginRef.getIdServiceProvider());
        intent.putExtra("cust_id", mCustId);
        intent.putExtra("appont_id", mAppontId);
        startActivity(intent);
    }
}
