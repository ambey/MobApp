package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.activity.LoginActivity;
import com.extenprise.mapp.net.AppStatus;
import com.extenprise.mapp.service.data.ServProvHasServPt;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.Utility;

import java.util.Calendar;


public class ServProvDetailsActivity extends Activity {

    private ServiceProvider mServProv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serv_prov_details);

        View formView = findViewById(R.id.bookAppointmentForm);
        View progressView = findViewById(R.id.progressView);

        Intent intent = getIntent();
        mServProv = intent.getParcelableExtra("service");

        TextView textViewClinic = (TextView) findViewById(R.id.textviewFirstClinic);
        TextView textViewDocExperience = (TextView) findViewById(R.id.textviewDocExperience);
        TextView textViewDocname = (TextView) findViewById(R.id.textviewDocname);
        TextView textViewClinicTime = (TextView) findViewById(R.id.textviewFirstclinictime);
        TextView textViewDocSpeciality = (TextView) findViewById(R.id.textviewDocspeciality);
        ImageView imageViewAvailable = (ImageView) findViewById(R.id.imageViewAvailable);

        TextView textViewFees = (TextView) findViewById(R.id.textviewFees);
        TextView textViewReviews = (TextView) findViewById(R.id.textviewReviews);
        TextView textViewDocQualification = (TextView) findViewById(R.id.textviewDocQualification);

        ServProvHasServPt spsspt = mServProv.getServProvHasServPt(0);
        textViewClinic.setText(spsspt.getServicePoint().getName());
        textViewDocExperience.setText("" + spsspt.getExperience());
        textViewClinicTime.setText(Utility.getTimeString(spsspt.getStartTime()) + " to "
                + Utility.getTimeString(spsspt.getEndTime()));
        textViewDocname.setText(mServProv.getfName() + " " + mServProv.getlName());
        textViewDocSpeciality.setText("(" + spsspt.getService().getSpeciality() + ")");

        //mTextViewReviews.setText("11");
        textViewDocQualification.setText(mServProv.getQualification());
        textViewFees.setText("" + spsspt.getConsultFee());

        TextView availability = (TextView) findViewById(R.id.textviewAvailability);
        if(Utility.findDocAvailability(spsspt.getWorkingDays(), Calendar.getInstance())) {
            imageViewAvailable.setImageResource(R.drawable.g_circle);
            availability.setText(getString(R.string.available));
        } else {
            imageViewAvailable.setImageResource(R.drawable.r_circle);
            availability.setText(getString(R.string.unavailable));
        }

    }

    public void bookAppointment(View view) {
        if (!AppStatus.getInstance(this).isOnline()) {
            Utility.showMessage(this, R.string.error_not_online);
            return;
        }
        Intent intent = new Intent(this, BookAppointmentActivity.class);
        if(LoginHolder.custLoginRef == null) {
            intent = new Intent(this, LoginActivity.class);
            intent.putExtra("target-activity", BookAppointmentActivity.class.getName());
        } else {
            intent.putExtra("customer", LoginHolder.custLoginRef);
        }
        intent.putParcelableArrayListExtra("servProvList", getIntent().getParcelableArrayListExtra("servProvList"));
        intent.putExtra("servProv", mServProv);
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

    public Intent getParentActivityIntent() {
        Intent intent = super.getParentActivityIntent();
        if(intent != null) {
            intent.putParcelableArrayListExtra("servProvList", getIntent().getParcelableArrayListExtra("servProvList"));
        }
        return intent;
    }
}