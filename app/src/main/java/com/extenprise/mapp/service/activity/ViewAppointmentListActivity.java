package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.data.Appointment;
import com.extenprise.mapp.customer.data.Customer;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.util.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ViewAppointmentListActivity extends Activity
        implements DateChangeListener {

    private TextView mAppointmentDateTextView;
    private ListView mAppointmentListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointment_list);

        mAppointmentDateTextView = (TextView) findViewById(R.id.appointmentDateTextView);
        mAppointmentListView = (ListView) findViewById(R.id.appointmentListView);

        setCurrentDateOnView();
        setAppointmentList();
    }

/*
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener,
                        year, month,day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // set selected date into textview
            mAppointmentDateTextView.setText(new StringBuilder().append(day)
                    .append("-").append(month+1).append("-").append(year)
                    .append(" "));

            // set selected date into datepicker also
            mDpResult.init(year, month, day, null);

            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);

            MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
            SearchAppointment.searchAppointment(dbHelper, mAppointmentDateTextView.getText().toString(),
                    LoginHolder.servLoginRef.getIdServiceProvider());
            setAppointmentList();
        }
    };
*/

    public void setCurrentDateOnView() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
        mAppointmentDateTextView.setText(sdf.format(c.getTime()));

        MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
        if(SearchAppointment.searchAppointment(dbHelper, sdf.format(c.getTime()),
                LoginHolder.servLoginRef.getIdServiceProvider())) {
            setAppointmentList();
        } else {
            UIUtility.showAlert(this, "", "No Appointments for this date.");
            return;
        }
    }

    private void setAppointmentList() {

        final Cursor cursor = SearchAppointment.getCursor();

        String[] values = new String[]{
                MappContract.Customer.COLUMN_NAME_FNAME,
                MappContract.Customer.COLUMN_NAME_LNAME,
                MappContract.Customer.COLUMN_NAME_GENDER,
                MappContract.Customer.COLUMN_NAME_AGE,
                MappContract.Customer.COLUMN_NAME_WEIGHT,
                MappContract.Appointment.COLUMN_NAME_FROM_TIME
        };
        int[] viewIds = new int[]{
                R.id.patientFNameTextView,
                R.id.patientLNameTextView,
                R.id.patientGenderTextView,
                R.id.patientAgeTextView,
                R.id.patientWeightTextView,
                R.id.appointmentTimeTextView
        };
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.activity_view_appointment,
                cursor,
                values,
                viewIds, 0);

        mAppointmentListView.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);

        mAppointmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);

                Appointment appointment = new Appointment();
                Customer customer = new Customer();

                customer.setfName(cursor.getString(cursor.getColumnIndex(MappContract.Customer.COLUMN_NAME_FNAME)));
                customer.setlName(cursor.getString(cursor.getColumnIndex(MappContract.Customer.COLUMN_NAME_LNAME)));
                customer.setEmailId(cursor.getString(cursor.getColumnIndex(MappContract.Customer.COLUMN_NAME_EMAIL_ID)));
                customer.setGender(cursor.getString(cursor.getColumnIndex(MappContract.Customer.COLUMN_NAME_GENDER)));
                customer.setLocation(cursor.getString(cursor.getColumnIndex(MappContract.Customer.COLUMN_NAME_LOCATION)));
                customer.setPhone(cursor.getString(cursor.getColumnIndex(MappContract.Customer.COLUMN_NAME_CELLPHONE)));
                customer.setAge(Integer.parseInt(cursor.getString(cursor.getColumnIndex(MappContract.Customer.COLUMN_NAME_AGE))));

                DateFormat formatter = new SimpleDateFormat("dd-MM-yy");
                try {
                    Date dob = formatter.parse(cursor.getString(cursor.getColumnIndex(MappContract.Customer.COLUMN_NAME_DOB)));
                    if (dob != null) {
                        customer.setDob(dob);
                    }

                    Date doAppnt = formatter.parse(mAppointmentDateTextView.getText().toString());
                    if (doAppnt != null) {
                        appointment.setDateOfAppointment(doAppnt);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                appointment.setCustomer(customer);
                appointment.setServPointType(cursor.getString(cursor.getColumnIndex(MappContract.Appointment.COLUMN_NAME_SERVICE_POINT_TYPE)));
                appointment.setFromTime(Integer.parseInt(cursor.getString(cursor.getColumnIndex(MappContract.Appointment.COLUMN_NAME_FROM_TIME))));
                appointment.setToTime(Integer.parseInt(cursor.getString(cursor.getColumnIndex(MappContract.Appointment.COLUMN_NAME_TO_TIME))));

                LoginHolder.appointment = appointment;
                Intent i = new Intent(getApplicationContext(), AppointmentDetailsActivity.class);
                startActivity(i);
            }
        });
        mAppointmentListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_appointment_list, menu);
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

    public void showDatePicker(View view) {
        UIUtility.datePicker(view, mAppointmentDateTextView, this);
    }

    @Override
    public void datePicked(String date) {
        MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
        if(SearchAppointment.searchAppointment(dbHelper, date,
                LoginHolder.servLoginRef.getIdServiceProvider())) {
            setAppointmentList();
        } else {
            UIUtility.showAlert(this, "", "No Appointments for this date.");
            return;
        }
    }
}
