package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.activity.LoginActivity;
import com.extenprise.mapp.net.AppStatus;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.service.data.ServProvHasServPt;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.Utility;

import java.util.Calendar;


public class ServProvDetailsActivity extends Activity {

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
    private ServiceProvider mServProv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serv_prov_details);

        mFormView = findViewById(R.id.bookAppointmentForm);
        mProgressView = findViewById(R.id.progressView);

        Intent intent = getIntent();
        mServProv = intent.getParcelableExtra("service");

        mTextViewClinic = (TextView)findViewById(R.id.textviewFirstClinic);
        mTextViewDocExperience = (TextView)findViewById(R.id.textviewDocExperience);
        mTextViewDocname = (TextView)findViewById(R.id.textviewDocname);
        mTextViewClinicTime = (TextView)findViewById(R.id.textviewFirstclinictime);
        mTextViewDocSpeciality = (TextView)findViewById(R.id.textviewDocspeciality);
        mImageViewAvailable = (ImageView)findViewById(R.id.imageViewAvailable);

        mTextViewFees = (TextView)findViewById(R.id.textviewFees);
        mTextViewReviews = (TextView)findViewById(R.id.textviewReviews);
        mTextViewDocQualification = (TextView)findViewById(R.id.textviewDocQualification);

        ServProvHasServPt spsspt = mServProv.getServProvHasServPt(0);
        mTextViewClinic.setText(spsspt.getServicePoint().getName());
        mTextViewDocExperience.setText("" + spsspt.getExperience());
        mTextViewClinicTime.setText(Utility.getTimeString(spsspt.getStartTime()) + " to "
                + Utility.getTimeString(spsspt.getEndTime()));
        mTextViewDocname.setText(mServProv.getfName() + " " + mServProv.getlName());
        mTextViewDocSpeciality.setText("(" + spsspt.getService().getSpeciality() + ")");

        //mTextViewReviews.setText("11");
        mTextViewDocQualification.setText(mServProv.getQualification());
        mTextViewFees.setText("" + spsspt.getConsultFee());

        TextView availability = (TextView) findViewById(R.id.textviewAvailability);
        if(Utility.findDocAvailability(spsspt.getWorkingDays(), Calendar.getInstance())) {
            mImageViewAvailable.setImageResource(R.drawable.g_circle);
            availability.setText(getString(R.string.available));
        } else {
            mImageViewAvailable.setImageResource(R.drawable.r_circle);
            availability.setText(getString(R.string.unavailable));
        }

    }

    public void bookAppointment(View view) {
        if (AppStatus.getInstance(this).isOnline()) {
            Intent intent = new Intent(this, BookAppointmentActivity.class);
            if(LoginHolder.custLoginRef == null) {
                intent = new Intent(this, LoginActivity.class);
                intent.putExtra("target-activity", BookAppointmentActivity.class.getName());
            }
            intent.putExtra("servProv", mServProv);
            startActivity(intent);
        } else {
            Toast.makeText(this, "You are not online!!!!", Toast.LENGTH_LONG).show();
            Log.v("Home", "############################You are not online!!!!");
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

    public Intent getParentActivityIntent() {
        Intent intent = super.getParentActivityIntent();
        intent.putParcelableArrayListExtra("servProvList", getIntent().getParcelableArrayListExtra("servProvList"));
        return intent;
    }
}