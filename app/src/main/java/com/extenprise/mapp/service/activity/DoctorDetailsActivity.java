package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.customer.data.Customer;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.service.data.ServProvHasServHasServPt;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.UIUtility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DoctorDetailsActivity extends Activity {

    private TextView mTextViewDocname;
    private TextView mTextViewDocSpeciality;
    private TextView mTextViewDocQualification;
    private TextView mTextViewDocExperience;
    private TextView mTextViewReviews;
    private TextView mTextViewClinic;
    private TextView mTextViewClinicTime;
    private TextView mTextViewFees;
    private ImageView mImageViewAvailable;
    private View mFormView;
    private View mProgressView;
    private ServProvHasServHasServPt spsspt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_details);

        mFormView = findViewById(R.id.bookAppointmentForm);
        mProgressView = findViewById(R.id.progressView);

        spsspt = LoginHolder.spsspt;
        ServiceProvider serviceProvider = LoginHolder.spsspt.getServProvHasService().getServProv();

        mTextViewClinic = (TextView)findViewById(R.id.textviewFirstClinic);
        mTextViewDocExperience = (TextView)findViewById(R.id.textviewDocExperience);
        mTextViewDocname = (TextView)findViewById(R.id.textviewDocname);
        mTextViewClinicTime = (TextView)findViewById(R.id.textviewFirstclinictime);
        mTextViewDocSpeciality = (TextView)findViewById(R.id.textviewDocspeciality);
        mImageViewAvailable = (ImageView)findViewById(R.id.imageViewAvailable);

        mTextViewFees = (TextView)findViewById(R.id.textviewFees);
        mTextViewReviews = (TextView)findViewById(R.id.textviewReviews);
        mTextViewDocQualification = (TextView)findViewById(R.id.textviewDocQualification);

        mTextViewClinic.setText(spsspt.getServicePoint().getName());
        mTextViewDocExperience.setText("" + spsspt.getServProvHasService().getExperience());
        mTextViewClinicTime.setText(UIUtility.getTimeString(spsspt.getStartTime()) + " to "
                + UIUtility.getTimeString(spsspt.getEndTime()));
        mTextViewDocname.setText(serviceProvider.getfName() + " " + serviceProvider.getlName());
        mTextViewDocSpeciality.setText(spsspt.getServProvHasService().getService().getSpeciality());

        //mTextViewReviews.setText("11");
        mTextViewDocQualification.setText(serviceProvider.getQualification());
        mTextViewFees.setText("120");

        TextView availability = (TextView) findViewById(R.id.textviewAvailability);
        if(UIUtility.findDocAvailability(spsspt.getWeeklyOff(), Calendar.getInstance())) {
            mImageViewAvailable.setImageResource(R.drawable.g_circle);
            availability.setText(getString(R.string.available));
        } else {
            mImageViewAvailable.setImageResource(R.drawable.r_circle);
            availability.setText(getString(R.string.unavailable));
        }

    }

    public void bookAppointment(View view) {
        /*UIUtility.showProgress(this, mFormView, mProgressView, true);

        SaveAppointData task = new SaveAppointData(this);
        task.execute((Void) null);*/

        /*LoginHolder.servLoginRef.setfName(mTextViewDocname.getText().toString());
        LoginHolder.spsspt.setStartTime(spsspt.getStartTime());
        LoginHolder.spsspt.setEndTime(spsspt.getEndTime());*/

        if(LoginHolder.custLoginRef != null) {
            Intent intent = new Intent(this, BookAppointmentActivity.class);
            startActivity(intent);
        } else {
            Customer c = new Customer();
            c.setStatus("Appointment");
            LoginHolder.custLoginRef = c;
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_doctor_details, menu);
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