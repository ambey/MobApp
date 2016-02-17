package com.extenprise.mapp.medico.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.activity.LoginActivity;
import com.extenprise.mapp.medico.customer.activity.BookAppointmentActivity;
import com.extenprise.mapp.medico.data.WorkingDataStore;
import com.extenprise.mapp.medico.service.data.ServProvHasServPt;
import com.extenprise.mapp.medico.service.data.Service;
import com.extenprise.mapp.medico.service.data.ServiceProvider;
import com.extenprise.mapp.medico.util.Utility;

import java.util.Calendar;


public class ServProvDetailsActivity extends Activity {

    private ServiceProvider mServProv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serv_prov_details);
/*
        View formView = findViewById(R.id.bookAppointmentForm);
        View progressView = findViewById(R.id.progressView);
*/
        TextView textViewClinic = (TextView) findViewById(R.id.textviewFirstClinic);
        TextView textViewDocExperience = (TextView) findViewById(R.id.textviewDocExperience);
        TextView textViewDocname = (TextView) findViewById(R.id.textviewDocname);
        TextView textViewClinicTime = (TextView) findViewById(R.id.textviewFirstclinictime);
        TextView textViewDocSpeciality = (TextView) findViewById(R.id.textviewDocspeciality);
        ImageView imageViewAvailable = (ImageView) findViewById(R.id.imageViewAvailable);
        ImageView imageView = (ImageView) findViewById(R.id.imageViewUser);
        TextView lbl = (TextView) findViewById(R.id.textview);
        TextView textViewFees = (TextView) findViewById(R.id.textviewFees);
        TextView sptType = (TextView) findViewById(R.id.textviewClinicName);
/*
        TextView textViewReviews = (TextView) findViewById(R.id.textviewReviews);
*/
        TextView textViewDocQualification = (TextView) findViewById(R.id.textviewDocQualification);
        Button bookAppontButton = (Button) findViewById(R.id.buttonBookAppointment);

        Intent intent = getIntent();
        if (savedInstanceState != null) {
            mServProv = savedInstanceState.getParcelable("servProv");
        } else {
            mServProv = intent.getParcelableExtra("servProv");
        }
        assert mServProv != null;
        ServProvHasServPt spsspt = mServProv.getServProvHasServPt(0);
        Service service = spsspt.getService();

        String category = service.getCategory();
        if (category.equalsIgnoreCase(getString(R.string.pharmacist))) {
            Utility.setEnabledButton(this, bookAppontButton, false);
            imageView.setImageResource(R.drawable.medstore);
            lbl.setText("");
        }
        if (category.equalsIgnoreCase(getString(R.string.diagnosticCenter))) {
            imageView.setImageResource(R.drawable.diagcenter);
            lbl.setText("");
        }

        if (mServProv.getPhoto() != null) {
            imageView.setImageBitmap(Utility.getBitmapFromBytes(mServProv.getPhoto(),
                    imageView.getLayoutParams().width, imageView.getLayoutParams().height));
        }
        textViewClinic.setText(spsspt.getServicePoint().getName());
        textViewDocExperience.setText(String.format("%.1f", spsspt.getExperience()));
        textViewClinicTime.setText(String.format("%s to %s",
                Utility.getTimeString(spsspt.getStartTime()), Utility.getTimeString(spsspt.getEndTime())));
        textViewDocname.setText(String.format("%s %s", mServProv.getfName(), mServProv.getlName()));
        textViewDocSpeciality.setText(String.format("(%s)", spsspt.getService().getSpeciality()));
        textViewDocQualification.setText(mServProv.getQualification());
        textViewFees.setText(String.format("%.2f", spsspt.getConsultFee()));
        sptType.setText(String.format("%s %s", spsspt.getServPointType(), getString(R.string.nm)));

        TextView availability = (TextView) findViewById(R.id.textviewAvailability);
        if (Utility.findDocAvailability(spsspt.getWorkingDays(), Calendar.getInstance())) {
            imageViewAvailable.setImageResource(R.drawable.g_circle);
            availability.setText(getString(R.string.available));
        } else {
            imageViewAvailable.setImageResource(R.drawable.r_circle);
            availability.setText(getString(R.string.unavailable));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("servProv", getIntent().getParcelableExtra("servProv"));
        outState.putParcelableArrayList("servProvList", getIntent().getParcelableArrayListExtra("servProvList"));
        outState.putString("myparent-activity", getIntent().getStringExtra("myparent-activity"));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        getIntent().putParcelableArrayListExtra("servProvList", savedInstanceState.getParcelableArrayList("servProvList"));
        getIntent().putExtra("myparent-activity", savedInstanceState.getString("myparent-activity"));
    }

    /*
    @Override
    protected void onResume() {
        super.onResume();
        if (mServProv == null) {
            Utility.goTOLoginPage(this, LoginActivity.class);
        }
    }
*/

    public void bookAppointment(View view) {
        Intent intent = new Intent(this, BookAppointmentActivity.class);
        if (WorkingDataStore.getBundle().getParcelable("customer") == null) {
            intent = new Intent(this, LoginActivity.class);
            intent.putExtra("target-activity", BookAppointmentActivity.class.getName());
        } else {
            intent.putExtra("customer", WorkingDataStore.getBundle().getParcelable("customer"));
        }
        intent.putParcelableArrayListExtra("servProvList", getIntent().getParcelableArrayListExtra("servProvList"));
        intent.putExtra("servProv", mServProv);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_doctor_details, menu);
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

    public Intent getParentActivityIntent() {
        Intent intent = super.getParentActivityIntent();
        if (intent != null) {
            intent.putParcelableArrayListExtra("servProvList", getIntent().getParcelableArrayListExtra("servProvList"));
            intent.putExtra("parent-activity", getIntent().getStringExtra("myparent-activity"));
        }
        return intent;
    }
}