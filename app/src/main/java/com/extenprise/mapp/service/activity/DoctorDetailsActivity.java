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

        mTextViewReviews.setText("11");
        mTextViewDocQualification.setText(serviceProvider.getQualification());
        mTextViewFees.setText("Rs 120");

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

        Intent intent = new Intent(this, BookAppointmentActivity.class);
        startActivity(intent);
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

            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            //System.out.println(sdf.format(date));

            ContentValues values = new ContentValues();
            values.put(MappContract.Appointment.COLUMN_NAME_FROM_TIME, spsspt.getStartTime());
            values.put(MappContract.Appointment.COLUMN_NAME_TO_TIME, spsspt.getEndTime());
            values.put(MappContract.Appointment.COLUMN_NAME_DATE, sdf.format(date));
            values.put(MappContract.Appointment.COLUMN_NAME_SERVICE_POINT_TYPE, spsspt.getServPointType());
            values.put(MappContract.Appointment.COLUMN_NAME_SERVICE_NAME, spsspt.getServProvHasService().getService().getName());
            values.put(MappContract.Appointment.COLUMN_NAME_SPECIALITY, spsspt.getServProvHasService().getService().getSpeciality());
            values.put(MappContract.Appointment.COLUMN_NAME_ID_SERV_PROV, sp.getIdServiceProvider());

            db.insert(MappContract.Appointment.TABLE_NAME, null, values);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            UIUtility.showProgress(myActivity, mFormView, mProgressView, false);
            if(UIUtility.showAlert(myActivity, "Thanks You..!", "Your Appointment has been fixed.")) {
                /*Intent intent = new Intent(myActivity, SearchDoctorActivity.class);
                startActivity(intent);*/
            }
        }

        @Override
        protected void onCancelled() {
            UIUtility.showProgress(myActivity, mFormView, mProgressView, false);
        }

    }
}