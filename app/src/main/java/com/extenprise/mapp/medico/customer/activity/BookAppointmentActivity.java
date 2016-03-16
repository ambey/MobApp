package com.extenprise.mapp.medico.customer.activity;

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

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.activity.LoginActivity;
import com.extenprise.mapp.medico.customer.data.Customer;
import com.extenprise.mapp.medico.data.Appointment;
import com.extenprise.mapp.medico.data.WorkingDataStore;
import com.extenprise.mapp.medico.net.MappService;
import com.extenprise.mapp.medico.net.MappServiceConnection;
import com.extenprise.mapp.medico.net.ResponseHandler;
import com.extenprise.mapp.medico.net.ServiceResponseHandler;
import com.extenprise.mapp.medico.service.data.AppointmentTimeslotsForm;
import com.extenprise.mapp.medico.service.data.ServProvHasServPt;
import com.extenprise.mapp.medico.service.data.ServiceProvider;
import com.extenprise.mapp.medico.util.DateChangeListener;
import com.extenprise.mapp.medico.util.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class BookAppointmentActivity extends Activity
        implements DateChangeListener, ResponseHandler {

    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));

    private Spinner mSpinnerTimeSlots;
    private TextView mTextViewDate;
    private Button mBookButton;
    private Date mSelectedDate;
    private ServiceProvider mServiceProvider;
    //private TextView mMsgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        mServiceProvider = WorkingDataStore.getBundle().getParcelable("servProv");
        if (!(mServiceProvider != null && mServiceProvider.getServiceCount() > 0)) {
            Utility.sessionExpired(this);
            return;
        }
        TextView lbl = (TextView) findViewById(R.id.tvDrLbl);
        if (!mServiceProvider.getServProvHasServPt(0).getService().getCategory().
                equalsIgnoreCase(getString(R.string.physician))) {
            lbl.setText("");
        }

        TextView textViewDocFName = (TextView) findViewById(R.id.tvDocFName);
        TextView textViewDocLName = (TextView) findViewById(R.id.tvDocLName);
        TextView textViewDocSpeciality = (TextView) findViewById(R.id.tvDocSpec);
        TextView textViewQualification = (TextView) findViewById(R.id.tvQualification);
        mSpinnerTimeSlots = (Spinner) findViewById(R.id.spinnerTimeSlots);
        mTextViewDate = (TextView) findViewById(R.id.tvDate);
        mBookButton = (Button) findViewById(R.id.buttonBook);
        //mMsgView = (TextView) findViewById(R.id.viewMsg);

        //ServProvHasServPt spsspt = mServProv.getServProvHasServPt(0);
        textViewDocFName.setText(mServiceProvider.getfName());
        textViewDocLName.setText(mServiceProvider.getlName());
        textViewDocSpeciality.setText(mServiceProvider.getServProvHasServPt(0).getService().getSpeciality());
        textViewQualification.setText(String.format("(%s)", mServiceProvider.getQualification()));

        mSelectedDate = new Date();
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        mTextViewDate.setText(sdf.format(mSelectedDate));
        setTimeSlots();
    }

    public void bookAppointment(View view) {
        /* Send request to book appointment */
        Appointment form = new Appointment();
        Customer customer = (Customer) WorkingDataStore.getLoginRef();
        if (customer == null) {
            Utility.sessionExpired(this);
            return;
        }
        form.setIdCustomer(customer.getIdCustomer());
        form.setIdServProvHasServPt(mServiceProvider.getServices().get(0).getIdServProvHasServPt());
        form.setDate(mSelectedDate);
        form.setFrom(Utility.getMinutes(mSpinnerTimeSlots.getSelectedItem().toString()));
        Bundle bundle = new Bundle();
        bundle.putParcelable("form", form);

        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_BOOK_APPONT);
        if (Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE)) {
            //mMsgView.setVisibility(View.VISIBLE);
            Utility.showProgressDialog(this, true);
        }
    }

    private void setTimeSlots() {
        Utility.setEnabledButton(this, mBookButton, false);

        Calendar cal = Calendar.getInstance();
        int minutes = cal.get(Calendar.HOUR_OF_DAY) * 60 +
                cal.get(Calendar.MINUTE);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date todayDate = cal.getTime();
        //120; // For todays appointment, available time slots would start two hours from now
        // Set the hour, minute and other components to zero, so that we can compare the date.
        /*if (mSelectedDate.compareTo(todayDate) < 0) {
            Utility.showAlert(this, getString(R.string.title_activity_book_appointment),
                    getString(R.string.error_past_date));
            return;
        }*/
        cal.setTime(mSelectedDate);
        ServProvHasServPt spspt = mServiceProvider.getServProvHasServPt(0);
        if (!(Utility.findDocAvailability(spspt.getWorkingDays(), cal))) {
            SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(this, R.layout.layout_spinner, new ArrayList<String>());
            mSpinnerTimeSlots.setAdapter(spinnerAdapter);
            //Utility.showAlert(this, "", "Doctor is not available on the given date.");
            Utility.showMessage(this, R.string.msg_doc_unavailable);
            return;
        }

        //get time slots
        Bundle bundle = new Bundle();
        AppointmentTimeslotsForm form = new AppointmentTimeslotsForm();
        form.setIdService(mServiceProvider.getServProvHasServPt(0).getIdServProvHasServPt());
        form.setDate(mSelectedDate);
        form.setTodayDate(todayDate);
        form.setTime(minutes);
        bundle.putParcelable("form", form);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_APPONT_TIME_SLOTS);
        if (Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE)) {
            Utility.showProgressDialog(this, true);
        }
    }

    private void gotTimeSlots(Bundle data) {
        ArrayList<String> list = data.getStringArrayList("timeSlots");
        if (list == null) {
            list = new ArrayList<>();
            //return;
        }
        for (String t : list) {
            if (Utility.isDateToday(mSelectedDate) && Utility.isTimePassed(t)) {
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
        //mMsgView.setVisibility(View.GONE);
        if (data.getBoolean("status")) {
            Utility.showAlert(this, "", getString(R.string.msg_appont_booked), null, false, null, getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Utility.startActivity(BookAppointmentActivity.this, PatientsHomeScreenActivity.class);
                }
            }, new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                    Utility.startActivity(BookAppointmentActivity.this, PatientsHomeScreenActivity.class);

                }
            });
            Utility.setEnabledButton(this, mBookButton, false);
            Appointment appointment = data.getParcelable("form");
            Customer customer = (Customer) WorkingDataStore.getLoginRef();
            if (customer == null) {
                Utility.sessionExpired(this);
                return;
            }
            customer.getAppointments().add(appointment);
            mServiceProvider.getServProvHasServPt(0).getAppointments().add(appointment);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.action_search:
                return true;
            case R.id.action_sign_in:
                return true;
            case R.id.logout:
                Utility.logout(getSharedPreferences("loginPrefs", MODE_PRIVATE), this, LoginActivity.class);
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
        Utility.showProgressDialog(this, false);
        if (action == MappService.DO_APPONT_TIME_SLOTS) {
            gotTimeSlots(data);
        } else if (action == MappService.DO_BOOK_APPONT) {
            gotAppont(data);
        }
        return data.getBoolean("status");
    }


    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent();
    }

    @Override
    public void onBackPressed() {
        mConnection.setBound(false);
        Intent intent = getParentActivityIntent();
        if (intent != null) {
            startActivity(intent);
            return;
        }
        super.onBackPressed();
    }
}