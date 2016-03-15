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
import com.extenprise.mapp.medico.util.ByteArrayToBitmapTask;
import com.extenprise.mapp.medico.util.Utility;

import java.util.Calendar;


public class ServProvDetailsActivity extends Activity {

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
        TextView notes = (TextView) findViewById(R.id.textViewNotes);
/*
        TextView textViewReviews = (TextView) findViewById(R.id.textviewReviews);
*/
        TextView textViewDocQualification = (TextView) findViewById(R.id.textviewDocQualification);
        Button bookAppontButton = (Button) findViewById(R.id.buttonBookAppointment);

        ServiceProvider servProv = WorkingDataStore.getBundle().getParcelable("servProv");
        if (!(servProv != null && servProv.getServiceCount() > 0)) {
            Utility.sessionExpired(this);
            return;
        }
        ServProvHasServPt spspt = servProv.getServProvHasServPt(0);
        Service service = spspt.getService();
        String category = service.getCategory();

        if (category.equalsIgnoreCase(getString(R.string.pharmacist))) {
            Utility.setEnabledButton(this, bookAppontButton, false);
            imageView.setImageResource(R.drawable.medstore);
            lbl.setText("");
        }
        if (category.equalsIgnoreCase(getString(R.string.diagnosticCenter)) ||
                category.equals(getString(R.string.diagnostic_center))) {
            imageView.setImageResource(R.drawable.diagcenter);
            lbl.setText("");
        }

        if (servProv.getPhoto() != null) {
            ByteArrayToBitmapTask task = new ByteArrayToBitmapTask(imageView, servProv.getPhoto(),
                    imageView.getLayoutParams().width, imageView.getLayoutParams().height);
            task.execute();
        }
        textViewClinic.setText(spspt.getServicePoint().getName());
        textViewDocExperience.setText(String.format("%.1f", spspt.getExperience()));
        textViewClinicTime.setText(String.format("%s to %s",
                Utility.getTimeInTwelveFormat(spspt.getStartTime()),
                Utility.getTimeInTwelveFormat(spspt.getEndTime())));
        textViewDocname.setText(String.format("%s %s", servProv.getfName(), servProv.getlName()));
        textViewDocSpeciality.setText(String.format("(%s)", spspt.getService().getSpeciality()));
        textViewDocQualification.setText(servProv.getQualification());
        textViewFees.setText(String.format("%.2f", spspt.getConsultFee()));
        sptType.setText(String.format("%s %s", spspt.getServPointType(), getString(R.string.nm)));
        notes.setText(spspt.getNotes());

        TextView availability = (TextView) findViewById(R.id.textviewAvailability);
        if (Utility.findDocAvailability(spspt.getWorkingDays(), Calendar.getInstance())) {
            imageViewAvailable.setImageResource(R.drawable.g_circle);
            availability.setText(getString(R.string.available));
        } else {
            imageViewAvailable.setImageResource(R.drawable.r_circle);
            availability.setText(getString(R.string.unavailable));
        }
    }

    /*
    @Override
    protected void onResume() {
        super.onResume();
        if (mServProv == null) {
            Utility.startActivity(this, LoginActivity.class);
        }
    }
*/

    public void bookAppointment(View view) {
        Intent intent = new Intent(this, BookAppointmentActivity.class);
        if (WorkingDataStore.getLoginRef() == null) {
            intent = new Intent(this, LoginActivity.class);
            intent.putExtra("target-activity", BookAppointmentActivity.class.getName());
        }
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        /*if (WorkingDataStore.getBundle().getParcelable("customer") == null) {
            //menu.removeItem(R.id.logout); Not Removing Item
            //menu.findItem(R.id.logout).setVisible(false); // Null Pointer Exception
        }*/
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (WorkingDataStore.getLoginRef() == null) {
            menu.removeItem(R.id.logout);
        }
        return true;
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
            case R.id.logout:
                Utility.logout(getSharedPreferences("loginPrefs", MODE_PRIVATE), this, LoginActivity.class);
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

    @Override
    public void onBackPressed() {
        Intent intent = getParentActivityIntent();
        if (intent != null) {
            startActivity(intent);
            return;
        }
        super.onBackPressed();
    }
}