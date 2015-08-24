package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.content.Intent;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.service.data.ServProvHasServHasServPt;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.UIUtility;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DoctorDetails extends Activity {

    private TextView mtextviewDocname;
    private TextView mtextviewDocspeciality;
    private TextView mtextviewDocQualification;
    private TextView mtextviewDocExperience;
    private TextView mtextviewReviews;
    private TextView mtextviewClinic;
    private TextView mTextviewClinictime;
    private TextView mtextviewFees;
    private ImageView mimageViewAvailable;

    private Button mbuttnBookAppointment;
    private View mFormView;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_details);

        mFormView = findViewById(R.id.bookAppointmentForm);
        mProgressView = findViewById(R.id.progressView);

        ServProvHasServHasServPt spsspt = LoginHolder.spsspt;
        ServiceProvider serviceProvider = LoginHolder.spsspt.getServProvHasService().getServProv();

        mtextviewClinic = (TextView)findViewById(R.id.textviewFirstClinic);
        mtextviewDocExperience = (TextView)findViewById(R.id.textviewDocExperience);
        mtextviewDocname = (TextView)findViewById(R.id.textviewDocname);
        mTextviewClinictime = (TextView)findViewById(R.id.textviewFirstclinictime);
        mtextviewDocspeciality = (TextView)findViewById(R.id.textviewDocspeciality);
        mtextviewFees = (TextView)findViewById(R.id.textviewFees);
        mtextviewReviews = (TextView)findViewById(R.id.textviewReviews);
        mtextviewDocQualification = (TextView)findViewById(R.id.textviewDocQualification);
        mbuttnBookAppointment = (Button)findViewById(R.id.buttonBookAppointment);


        mtextviewClinic.setText(spsspt.getServicePoint().getName());
        mtextviewDocExperience.setText("" + spsspt.getServProvHasService().getExperience());


        mTextviewClinictime.setText(UIUtility.getTimeString(spsspt.getStartTime()) + " to "
                + UIUtility.getTimeString(spsspt.getEndTime()));
        mtextviewDocname.setText(serviceProvider.getfName() + " " + serviceProvider.getlName());
        mtextviewDocspeciality.setText(spsspt.getServProvHasService().getService().getSpeciality());

        mtextviewReviews.setText("11");
        mtextviewDocQualification.setText("MD Medicine");
        mtextviewFees.setText("Rs 120");
    }

    public void bookAppointment(View view) {
        UIUtility.showProgress(this, mFormView, mProgressView, true);

        SaveAppointData task = new SaveAppointData(this);
        task.execute((Void) null);

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
            UIUtility.showAlert(myActivity, "Thanks You..!", "Your Appointment has been fixed.");
            UIUtility.showProgress(myActivity, mFormView, mProgressView, false);
            Intent intent = new Intent(myActivity, LoginActivity.class);
            startActivity(intent);
        }

        @Override
        protected void onCancelled() {
            UIUtility.showProgress(myActivity, mFormView, mProgressView, false);
        }

    }
}
