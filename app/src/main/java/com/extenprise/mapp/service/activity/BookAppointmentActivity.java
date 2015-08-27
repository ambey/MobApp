package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.service.data.ServProvHasServHasServPt;
import com.extenprise.mapp.service.data.ServProvHasService;
import com.extenprise.mapp.service.data.Service;
import com.extenprise.mapp.service.data.ServicePoint;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.UIUtility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;


public class BookAppointmentActivity extends Activity {

    private TextView mTextViewDocname;
    private TextView mTextViewDocSpeciality;
    //private View mFormView;
    //private View mProgressView;
    private ListView mListTimeSlots;

    private TextView mTvDisplayDate;
    private DatePicker mDpResult;
    private Button mBtnChangeDate, mbuttonBook;

    private int year;
    private int month;
    private int day;
    private String selectedItem;

    static final int DATE_DIALOG_ID = 999;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        //mFormView = findViewById(R.id.bookAppointmentForm);
        //mProgressView = findViewById(R.id.progressView);
        mTextViewDocname = (TextView) findViewById(R.id.textViewDocName);
        mTextViewDocSpeciality = (TextView) findViewById(R.id.textViewDocSpec);
        mListTimeSlots = (ListView) findViewById(R.id.listTimeSlots);

        ServProvHasServHasServPt spsspt = LoginHolder.spsspt;
        ServiceProvider serviceProvider = LoginHolder.spsspt.getServProvHasService().getServProv();

        mTextViewDocname.setText(serviceProvider.getfName() + " " + serviceProvider.getlName());
        mTextViewDocSpeciality.setText(spsspt.getServProvHasService().getService().getSpeciality());

        setCurrentDateOnView();
        addListenerOnButton();

    }

    private boolean isTimeSlotsBooked(String selectedItem) {
        MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                MappContract.Appointment.COLUMN_NAME_ID_SERV_PROV
        };
        String selection = MappContract.Appointment.COLUMN_NAME_FROM_TIME + "=? and " +
                MappContract.Appointment.COLUMN_NAME_DATE + "=?";
        String[] selectionArgs = {
                "" + UIUtility.getMinutes(selectedItem),
                mTvDisplayDate.getText().toString()
        };
        Cursor c = db.query(MappContract.Appointment.TABLE_NAME,
                projection, selection, selectionArgs, null, null, null);
        int count = c.getCount();
        c.close();
        return (count > 0);
    }

    public void bookAppointment(View view) {
        //UIUtility.showProgress(this, mFormView, mProgressView, true);
        if (!isTimeSlotsBooked(selectedItem)) {
            SaveAppointData task = new SaveAppointData(this);
            task.execute((Void) null);
        } else {
            UIUtility.showAlert(this, "Sorry!", "The time slot is already booked.");
        }
    }

    public void setTimeSlots(Calendar cal) {

        if (!(UIUtility.findDocAvailability(LoginHolder.spsspt.getWeeklyOff(), cal))) {
            UIUtility.showAlert(this, "Sorry!", "Doctor is not available on the given date.");
            // listView is your instance of your ListView
            ArrayAdapter sampleAdapter = (ArrayAdapter) mListTimeSlots.getAdapter();
            sampleAdapter.clear();
            sampleAdapter.notifyDataSetChanged();
            return;
        }
        ArrayList<String> liste = new ArrayList<String>();
        for (int i = LoginHolder.spsspt.getStartTime(); i <= LoginHolder.spsspt.getEndTime(); i += 30) {
            String from = UIUtility.getTimeString(i);
            if (!isTimeSlotsBooked(from)) {
                liste.add(from);
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.activity_time_slots, liste);

        mListTimeSlots.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);
        mListTimeSlots.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for (int a = 0; a < parent.getChildCount(); a++) {
                    parent.getChildAt(a).setBackgroundColor(Color.TRANSPARENT);
                }
                selectedItem = mListTimeSlots.getItemAtPosition(position).toString().trim();
                view.setBackgroundColor(Color.GREEN);
            }
        });
        mListTimeSlots.setAdapter(adapter);
    }

    public void setCurrentDateOnView() {

        mTvDisplayDate = (TextView) findViewById(R.id.tvDate);
        mDpResult = (DatePicker) findViewById(R.id.datePicker);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        // set current date into textview
        mTvDisplayDate.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(month + 1).append("-").append(day).append("-")
                .append(year).append(" "));

        // set current date into datepicker
        mDpResult.init(year, month, day, null);
        setTimeSlots(c);

    }

    public void addListenerOnButton() {
        mBtnChangeDate = (Button) findViewById(R.id.btnChangeDate);
        mBtnChangeDate.setOnClickListener(new View.OnClickListener() {
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
                        year, month, day);
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
            mTvDisplayDate.setText(new StringBuilder().append(day)
                    .append("-").append(month + 1).append("-").append(year)
                    .append(" "));

            // set selected date into datepicker also
            mDpResult.init(year, month, day, null);

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

    class SaveAppointData extends AsyncTask<Void, Void, Void> {

        private Activity myActivity;

        public SaveAppointData(Activity activity) {
            myActivity = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ServProvHasServHasServPt spsspt = LoginHolder.spsspt;
            ServiceProvider sp = LoginHolder.spsspt.getServProvHasService().getServProv();

            MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(MappContract.Appointment.COLUMN_NAME_FROM_TIME, UIUtility.getMinutes(selectedItem));
            values.put(MappContract.Appointment.COLUMN_NAME_TO_TIME, UIUtility.getMinutes(selectedItem) + 30);
            values.put(MappContract.Appointment.COLUMN_NAME_DATE, mTvDisplayDate.getText().toString());
            values.put(MappContract.Appointment.COLUMN_NAME_SERVICE_POINT_TYPE, spsspt.getServPointType());
            values.put(MappContract.Appointment.COLUMN_NAME_SERVICE_NAME, spsspt.getServProvHasService().getService().getName());
            values.put(MappContract.Appointment.COLUMN_NAME_SPECIALITY, spsspt.getServProvHasService().getService().getSpeciality());
            values.put(MappContract.Appointment.COLUMN_NAME_ID_SERV_PROV, sp.getIdServiceProvider());

            db.insert(MappContract.Appointment.TABLE_NAME, null, values);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(UIUtility.showAlert(myActivity, "Thanks You..!", "Your Appointment has been fixed.")) {
                Intent intent = new Intent(myActivity, SearchDoctorActivity.class);
                startActivity(intent);
                //return;
            }
        }

        @Override
        protected void onCancelled() {
        }
    }
}