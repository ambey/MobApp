package com.extenprise.mapp.customer.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.MappServiceConnection;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.data.AppointmentTimeslotsForm;
import com.extenprise.mapp.service.data.ServProvHasServPt;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.DateChangeListener;
import com.extenprise.mapp.util.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class BookAppointmentActivity extends Activity
        implements DateChangeListener, ResponseHandler {

    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));

    private Spinner mSpinnerTimeSlots;
    private TextView mTextViewDate;
    private Button mBookButton;
    private ServiceProvider mServProv;
    private Customer mCust;
    private Date mSelectedDate;
    private TextView mMsgView;

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
        mMsgView = (TextView) findViewById(R.id.viewMsg);

        ServProvHasServPt spsspt = mServProv.getServProvHasServPt(0);

        textViewDocFName.setText(mServProv.getfName());
        textViewDocLName.setText(mServProv.getlName());
        textViewDocSpeciality.setText(spsspt.getService().getSpeciality());
        textViewQualification.setText(String.format("(%s)", mServProv.getQualification()));

        mSelectedDate = new Date();
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        mTextViewDate.setText(sdf.format(mSelectedDate));
        setTimeSlots();
    }

    public void bookAppointment(View view) {
        /* Send request to book appointment */
        Appointment form = new Appointment();
        form.setIdCustomer(mCust.getIdCustomer());
        form.setIdServProvHasServPt(mServProv.getServices().get(0).getIdServProvHasServPt());
        form.setDate(mSelectedDate);
        form.setFrom(Utility.getMinutes(mSpinnerTimeSlots.getSelectedItem().toString()));
        Bundle bundle = new Bundle();
        bundle.putParcelable("form", form);

        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_BOOK_APPONT);
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
        Utility.setEnabledButton(this, mBookButton, false);
        mMsgView.setVisibility(View.VISIBLE);
    }

    public void setTimeSlots() {
        Utility.setEnabledButton(this, mBookButton, false);

        Calendar cal = Calendar.getInstance();
        int minutes = cal.get(Calendar.HOUR_OF_DAY) * 60 +
                cal.get(Calendar.MINUTE);// +
        //120; // For todays appointment, available time slots would start two hours from now
        // Set the hour, minute and other components to zero, so that we can compare the date.
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date todayDate = cal.getTime();
        cal.setTime(mSelectedDate);
        if (mSelectedDate.compareTo(todayDate) < 0) {
            Utility.showAlert(this, getString(R.string.title_activity_book_appointment),
                    getString(R.string.error_past_date));
            return;
        }

        ServProvHasServPt spspt = mServProv.getServProvHasServPt(0);
        if (!(Utility.findDocAvailability(spspt.getWorkingDays(), cal))) {
            Utility.showAlert(this, "", "Doctor is not available on the given date.");
            return;
        }

        //get time slots
        Bundle bundle = new Bundle();
        AppointmentTimeslotsForm form = new AppointmentTimeslotsForm();
        form.setIdService(mServProv.getServProvHasServPt(0).getIdServProvHasServPt());
        form.setDate(mSelectedDate);
        form.setTodayDate(todayDate);
        form.setTime(minutes);
        bundle.putParcelable("form", form);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_APPONT_TIME_SLOTS);
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
    }

    private void gotTimeSlots(Bundle data) {
        ArrayList<String> list = data.getStringArrayList("timeSlots");
        if (list == null) {
            return;
        }
        for(String t : list) {
            if(Utility.isDateToday(mSelectedDate) && Utility.isTimePassed(t)) {
                list.remove(t);
            }
        }
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(this, R.layout.layout_spinner, list);
        mSpinnerTimeSlots.setAdapter(spinnerAdapter);
        if (list.size() > 0) {
            Utility.setEnabledButton(this, mBookButton, true);
        }
    }

    private void gotAppont(Bundle data) {
        mMsgView.setVisibility(View.GONE);
        if (data.getBoolean("status")) {
            Utility.showAlert(this, "", getString(R.string.msg_appont_booked), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent intent = new Intent(BookAppointmentActivity.this, PatientsHomeScreenActivity.class);
                    startActivity(intent);
                }
            });
            Appointment appointment = data.getParcelable("form");
            mCust.getAppointments().add(appointment);
            mServProv.getServProvHasServPt(0).getAppointments().add(appointment);
        } else {
            Utility.showAlert(this, "", getString(R.string.error_appont));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_appointment, menu);
        return super.onCreateOptionsMenu(menu);
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
        long currentTime = Calendar.getInstance().getTimeInMillis();
        Utility.datePicker(view, mTextViewDate, this, currentTime, -1, currentTime);
    }

    @Override
    public void datePicked(String date) {
        mSelectedDate = Utility.getStrAsDate(date, "dd/MM/yyyy");
        setTimeSlots();
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
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
        if (intent != null) {
            intent.putExtra("service", getIntent().getParcelableExtra("servProv"));
            intent.putParcelableArrayListExtra("servProvList", getIntent().getParcelableArrayListExtra("servProvList"));
        }
        return intent;
    }

    @Override
    public void onBackPressed() {
        mConnection.setBound(false);
        //startActivity(getIntent());
        super.onBackPressed();
    }
}