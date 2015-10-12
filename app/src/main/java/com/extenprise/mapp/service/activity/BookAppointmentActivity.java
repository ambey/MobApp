package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.customer.data.Customer;
import com.extenprise.mapp.data.Appointment;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.data.ServProvHasServPt;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.DateChangeListener;
import com.extenprise.mapp.util.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class BookAppointmentActivity extends Activity
        implements DateChangeListener, ResponseHandler {

    private Messenger mService;
    private ServiceResponseHandler mRespHandler = new ServiceResponseHandler(this);
    private int mAction;

    private Spinner mSpinnerTimeSlots;
    private TextView mTextViewDate;
    private Button mBookButton;
    private ServiceProvider mServProv;
    private Customer mCust;
    private Date mSelectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        Intent intent = getIntent();
        mServProv = intent.getParcelableExtra("servProv");
        mCust = intent.getParcelableExtra("customer");

        TextView textViewDocFName = (TextView) findViewById(R.id.tvDocFName);
        TextView textViewDocLName = (TextView) findViewById(R.id.tvDocLName);
        TextView textViewDocSpeciality = (TextView) findViewById(R.id.tvDocSpec);
        TextView textViewQualification = (TextView) findViewById(R.id.tvQualification);
        mSpinnerTimeSlots = (Spinner) findViewById(R.id.spinnerTimeSlots);
        mTextViewDate = (TextView) findViewById(R.id.tvDate);
        mBookButton = (Button) findViewById(R.id.buttonBook);

        ServProvHasServPt spsspt = mServProv.getServProvHasServPt(0);

        textViewDocFName.setText(mServProv.getfName());
        textViewDocLName.setText(mServProv.getlName());
        textViewDocSpeciality.setText(spsspt.getService().getSpeciality());
        textViewQualification.setText("(" + mServProv.getQualification() + ")");

        mSelectedDate = new Date();
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        mTextViewDate.setText(sdf.format(mSelectedDate));
        setTimeSlots();
    }

/*
    private boolean isTimeSlotsBooked(String selectedItem) {
        MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                MappContract.Appointment.COLUMN_NAME_ID_SERV_PROV_SERV_PT
        };
        String selection = MappContract.Appointment.COLUMN_NAME_FROM_TIME + "=? and " +
                MappContract.Appointment.COLUMN_NAME_DATE + "=?";
        String[] selectionArgs = {
                "" + Utility.getMinutes(selectedItem),
                mTextViewDate.getText().toString()
        };
        Cursor c = db.query(MappContract.Appointment.TABLE_NAME,
                projection, selection, selectionArgs, null, null, null);
        int count = c.getCount();
        c.close();
        return (count > 0);
    }
*/

    public void bookAppointment(View view) {
        //Utility.showProgress(this, mFormView, mProgressView, true);
/*
        if(mSpinnerTimeSlots.getSelectedItem() != null && !(mSpinnerTimeSlots.getSelectedItem().toString().equals(""))) {

            if (!isTimeSlotsBooked(mSpinnerTimeSlots.getSelectedItem().toString())) {
                SaveAppointData task = new SaveAppointData(this);
                task.execute((Void) null);
            } else {
                Utility.showAlert(this, "", "The time slot is already booked.");
            }
        } else {
            Utility.showAlert(this, "", "Doctor is not available on the given date.");
        }
*/
        /* Send request to book appointment */
        mAction = MappService.DO_BOOK_APPONT;
        Intent intent = new Intent(this, MappService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    public void setTimeSlots() {
        mBookButton.setEnabled(false);
        mBookButton.setBackgroundResource(R.drawable.inactive_button);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        int minutes = cal.get(Calendar.HOUR_OF_DAY) * 60 +
                cal.get(Calendar.MINUTE);// +
        //120; // For todays appointment, available time slots would start two hours from now
        // Set the hour, minute and other components to zero, so that we can compare the date.
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date today = cal.getTime();
        cal.setTime(mSelectedDate);
        if (mSelectedDate.compareTo(today) < 0) {
            Utility.showAlert(this, getString(R.string.title_activity_book_appointment),
                    getString(R.string.error_past_date));
            return;
        }

        ServProvHasServPt spspt = mServProv.getServProvHasServPt(0);
        if (!(Utility.findDocAvailability(spspt.getWorkingDays(), cal))) {
            Utility.showAlert(this, "", "Doctor is not available on the given date.");
            return;
        }

        /* get Appointment time slots */
        mAction = MappService.DO_APPONT_TIME_SLOTS;
        Intent intent = new Intent(this, MappService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    private void gotTimeSlots(Bundle data) {
        ArrayList<String> list = data.getStringArrayList("timeSlots");
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(this, R.layout.layout_spinner, list);
        mSpinnerTimeSlots.setAdapter(spinnerAdapter);
        if (list.size() > 0) {
            mBookButton.setEnabled(true);
            mBookButton.setBackgroundResource(R.drawable.button);
        }
    }

    private void gotAppont(Bundle data) {
        Utility.showAlert(this, "", "Your Appointment has been booked.");
        Appointment appointment = data.getParcelable("form");
        mCust.getAppointments().add(appointment);
        mServProv.getServProvHasServPt(0).getAppointments().add(appointment);

        mBookButton.setEnabled(false);
        mBookButton.setBackgroundResource(R.drawable.inactive_button);
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
        Utility.datePicker(view, mTextViewDate, this);
    }

    @Override
    public void datePicked(String date) {
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        try {
            mSelectedDate = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setTimeSlots();
    }

    class SaveAppointData extends AsyncTask<Void, Void, Void> {

        private Activity myActivity;

        public SaveAppointData(Activity activity) {
            myActivity = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ServProvHasServPt spsspt = mServProv.getServProvHasServPt(0);

            MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String selectedItem = mSpinnerTimeSlots.getSelectedItem().toString();
            ContentValues values = new ContentValues();
            values.put(MappContract.Appointment.COLUMN_NAME_FROM_TIME, Utility.getMinutes(selectedItem));
            values.put(MappContract.Appointment.COLUMN_NAME_FROM_TIME_STR, selectedItem);
            values.put(MappContract.Appointment.COLUMN_NAME_TO_TIME, Utility.getMinutes(selectedItem) + 30);
            values.put(MappContract.Appointment.COLUMN_NAME_DATE, mTextViewDate.getText().toString());
            values.put(MappContract.Appointment.COLUMN_NAME_ID_SERV_PROV_SERV_PT, spsspt.getIdServProvHasServPt());
            values.put(MappContract.Appointment.COLUMN_NAME_ID_CUSTOMER, mCust.getIdCustomer());

            db.insert(MappContract.Appointment.TABLE_NAME, null, values);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Utility.showAlert(myActivity, "", "Your Appointment has been fixed.");
            mBookButton.setEnabled(false);
            mBookButton.setBackgroundResource(R.drawable.inactive_button);
            /*Intent intent = new Intent(myActivity, SearchServProvActivity.class);
            startActivity(intent);*/
            //return;
        }

        @Override
        protected void onCancelled() {
        }
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            mService = new Messenger(service);
            Bundle bundle = new Bundle();
            if (mAction == MappService.DO_APPONT_TIME_SLOTS) {
                SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
                sdf.applyPattern("dd/MM/yyyy");
                bundle.putInt("id", mServProv.getServProvHasServPt(0).getIdServProvHasServPt());
                bundle.putString("date", sdf.format(mSelectedDate));
            } else if (mAction == MappService.DO_BOOK_APPONT) {
                Appointment form = new Appointment();
                form.setIdCustomer(mCust.getIdCustomer());
                form.setIdServProvHasServPt(mServProv.getServices().get(0).getIdServProvHasServPt());
                form.setDate(mSelectedDate);
                form.setFrom(Utility.getMinutes(mSpinnerTimeSlots.getSelectedItem().toString()));
                bundle.putParcelable("form", form);
            }
            Message msg = Message.obtain(null, mAction);
            msg.replyTo = new Messenger(mRespHandler);
            msg.setData(bundle);

            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
        }
    };

    @Override
    public boolean gotResponse(int action, Bundle data) {
        unbindService(mConnection);
        if (action == MappService.DO_APPONT_TIME_SLOTS) {
            gotTimeSlots(data);
            return true;
        } else if (action == MappService.DO_BOOK_APPONT) {
            gotAppont(data);
            return true;
        }
        return false;
    }

    @Override
    public Intent getParentActivityIntent() {
        Intent intent = super.getParentActivityIntent();
        intent.putExtra("service", getIntent().getParcelableExtra("servProv"));
        return intent;
    }
}