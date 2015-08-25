package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.service.data.ServProvHasServHasServPt;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.UIUtility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;


public class BookAppointment extends Activity {

    private TextView mTextViewDocname;
    private TextView mTextViewDocSpeciality;
    //private View mFormView;
    //private View mProgressView;
    private ListView mListTimeSlots;

    private TextView tvDisplayDate;
    private DatePicker dpResult;
    private Button btnChangeDate;

    private int year;
    private int month;
    private int day;

    static final int DATE_DIALOG_ID = 999;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        //mFormView = findViewById(R.id.bookAppointmentForm);
        //mProgressView = findViewById(R.id.progressView);
        mTextViewDocname = (TextView)findViewById(R.id.textViewDocName);
        mTextViewDocSpeciality = (TextView)findViewById(R.id.textViewDocSpec);
        mListTimeSlots = (ListView) findViewById(R.id.listTimeSlots);

        ServProvHasServHasServPt spsspt = LoginHolder.spsspt;
        ServiceProvider serviceProvider = LoginHolder.spsspt.getServProvHasService().getServProv();

        mTextViewDocname.setText(serviceProvider.getfName() + " " + serviceProvider.getlName());
        mTextViewDocSpeciality.setText(spsspt.getServProvHasService().getService().getSpeciality());


        ArrayList<String> liste = new ArrayList<String>();

        for(int i=LoginHolder.spsspt.getStartTime(); i<=LoginHolder.spsspt.getEndTime(); i++) {
            String from = UIUtility.getTimeString(i);
            liste.add(from);
            i=i+30;
        }

        //String[] values = new String[] { spsspt.getStartTime() + " to " + spsspt.getEndTime() };

        //Collections.addAll(liste, values);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.activity_time_slots, liste);

        mListTimeSlots.setAdapter(adapter);

        setCurrentDateOnView();
        addListenerOnButton();

    }

    public void setTimeSlots(Calendar cal) {

        if(!(UIUtility.findDocAvailability(LoginHolder.spsspt.getWeeklyOff(), cal))) {
            UIUtility.showAlert(this, "Sorry!", "Doctor is not available on the given date.");
            return;
        }


        ArrayList<String> liste = new ArrayList<String>();

        for(int i=LoginHolder.spsspt.getStartTime(); i<=LoginHolder.spsspt.getEndTime(); i++) {
            String from = UIUtility.getTimeString(i);
            liste.add(from);
            i=i+30;
        }

        //String[] values = new String[] { spsspt.getStartTime() + " to " + spsspt.getEndTime() };

        //Collections.addAll(liste, values);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.activity_time_slots, liste);

        mListTimeSlots.setAdapter(adapter);
    }

    public void setCurrentDateOnView() {

        tvDisplayDate = (TextView) findViewById(R.id.tvDate);
        dpResult = (DatePicker) findViewById(R.id.datePicker);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        // set current date into textview
        tvDisplayDate.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(month + 1).append("-").append(day).append("-")
                .append(year).append(" "));

        // set current date into datepicker
        dpResult.init(year, month, day, null);
        setTimeSlots(c);

    }

    public void addListenerOnButton() {

        btnChangeDate = (Button) findViewById(R.id.btnChangeDate);

        btnChangeDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showDialog(DATE_DIALOG_ID);


            }

        });

    }

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
            tvDisplayDate.setText(new StringBuilder().append(month + 1)
                    .append("-").append(day).append("-").append(year)
                    .append(" "));

            // set selected date into datepicker also
            dpResult.init(year, month, day, null);

            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);
            setTimeSlots(cal);


        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_appointment, menu);
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