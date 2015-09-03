package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.customer.data.Customer;
import com.extenprise.mapp.data.Appointment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class AppointmentDetailsActivity extends Activity {

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

        Appointment appointment = LoginHolder.appointment;
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
                appointment.getFromTime() / 60, appointment.getFromTime() % 60));
        genderView.setText(customer.getGender());
        ageView.setText("" + customer.getAge());
        wtView.setText(String.format("%.1f", customer.getWeight()));
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
