package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.service.data.ServProvHasServHasServPt;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.DateChangeListener;
import com.extenprise.mapp.util.UIUtility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class BookAppointmentActivity extends Activity
        implements DateChangeListener {

    private Spinner mSpinnerTimeSlots;
    private TextView mTextViewDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        TextView textViewDocFName = (TextView) findViewById(R.id.tvDocFName);
        TextView textViewDocLName = (TextView) findViewById(R.id.tvDocLName);
        TextView textViewDocSpeciality = (TextView) findViewById(R.id.tvDocSpec);
        TextView textViewQualification = (TextView) findViewById(R.id.tvQualification);
        mSpinnerTimeSlots = (Spinner) findViewById(R.id.spinnerTimeSlots);
        mTextViewDate = (TextView) findViewById(R.id.tvDate);

        ServProvHasServHasServPt spsspt = LoginHolder.spsspt;
        ServiceProvider serviceProvider = LoginHolder.spsspt.getServProvHasService().getServProv();

        textViewDocFName.setText(serviceProvider.getfName());
        textViewDocLName.setText(serviceProvider.getlName());
        textViewDocSpeciality.setText(spsspt.getServProvHasService().getService().getSpeciality());
        textViewQualification.setText("(" + serviceProvider.getQualification() + ")");

        String date = UIUtility.getDaAsString("/");
        mTextViewDate.setText(date);
        setTimeSlots(date);
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
                mTextViewDate.getText().toString()
        };
        Cursor c = db.query(MappContract.Appointment.TABLE_NAME,
                projection, selection, selectionArgs, null, null, null);
        int count = c.getCount();
        c.close();
        return (count > 0);
    }

    public void bookAppointment(View view) {
        //UIUtility.showProgress(this, mFormView, mProgressView, true);
        if(mSpinnerTimeSlots.getSelectedItem() != null && !(mSpinnerTimeSlots.getSelectedItem().toString().equals(""))) {

            if (!isTimeSlotsBooked(mSpinnerTimeSlots.getSelectedItem().toString())) {
                SaveAppointData task = new SaveAppointData(this);
                task.execute((Void) null);
            } else {
                UIUtility.showAlert(this, "Sorry!", "The time slot is already booked.");
            }
        } else {
            UIUtility.showAlert(this, "Sorry!", "Doctor is not available on the given date.");
        }
    }

    public void setTimeSlots(String dateStr) {
        Button bookButton = (Button) findViewById(R.id.buttonBook);
        bookButton.setEnabled(false);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        int minutes = cal.get(Calendar.HOUR_OF_DAY) * 60 +
                cal.get(Calendar.MINUTE) +
                120; // For todays appointment, available time slots would start two hours from now
        // Set the hour, minute and other components to zero, so that we can compare the date.
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date today = cal.getTime();
        boolean todayAppont = false;
        try {
            Date date = sdf.parse(dateStr);
            cal.setTime(date);
            if(date.compareTo(today) < 0) {
                UIUtility.showAlert(this, getString(R.string.title_activity_book_appointment),
                        getString(R.string.error_past_date));
                return;
            } else if(date.compareTo(today) == 0) {
                todayAppont = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (!(UIUtility.findDocAvailability(LoginHolder.spsspt.getWeeklyOff(), cal))) {
            UIUtility.showAlert(this, "Sorry!", "Doctor is not available on the given date.");
            return;
        }

        int startTime = LoginHolder.spsspt.getStartTime();
        if(todayAppont) {
            while (startTime < minutes) {
                startTime += 30;
            }
        }
        ArrayList<String> list = new ArrayList<>();
        for (int i = startTime; i < LoginHolder.spsspt.getEndTime(); i += 30) {
            String from = UIUtility.getTimeString(i);
            if (!isTimeSlotsBooked(from)) {
                list.add(from);
            }
        }

        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(this, R.layout.layout_spinner, list);
        mSpinnerTimeSlots.setAdapter(spinnerAdapter);
        if(list.size() > 0) {
            bookButton.setEnabled(true);
            bookButton.setBackgroundResource(R.drawable.button);
        }
    }

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

    public void showDatePicker(View view) {
        UIUtility.datePicker(view, mTextViewDate, this);
    }

    @Override
    public void datePicked(String date) {
        setTimeSlots(date);
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

            String selectedItem = mSpinnerTimeSlots.getSelectedItem().toString();
            ContentValues values = new ContentValues();
            values.put(MappContract.Appointment.COLUMN_NAME_FROM_TIME, UIUtility.getMinutes(selectedItem));
            values.put(MappContract.Appointment.COLUMN_NAME_FROM_TIME_STR, selectedItem);
            values.put(MappContract.Appointment.COLUMN_NAME_TO_TIME, UIUtility.getMinutes(selectedItem) + 30);
            values.put(MappContract.Appointment.COLUMN_NAME_DATE, mTextViewDate.getText().toString());
            values.put(MappContract.Appointment.COLUMN_NAME_SERVICE_POINT_TYPE, spsspt.getServPointType());
            values.put(MappContract.Appointment.COLUMN_NAME_SERVICE_NAME, spsspt.getServProvHasService().getService().getServCatagory());
            values.put(MappContract.Appointment.COLUMN_NAME_SPECIALITY, spsspt.getServProvHasService().getService().getSpeciality());
            values.put(MappContract.Appointment.COLUMN_NAME_ID_SERV_PROV, sp.getIdServiceProvider());
            values.put(MappContract.Appointment.COLUMN_NAME_ID_CUSTOMER, LoginHolder.custLoginRef.getIdCustomer());

            db.insert(MappContract.Appointment.TABLE_NAME, null, values);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            UIUtility.showAlert(myActivity, "Thanks You..!", "Your Appointment has been fixed.");
            /*Intent intent = new Intent(myActivity, SearchServProvActivity.class);
            startActivity(intent);*/
            //return;
        }

        @Override
        protected void onCancelled() {
        }
    }
}