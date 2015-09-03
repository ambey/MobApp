package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.customer.data.Customer;
import com.extenprise.mapp.data.Appointment;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.util.DBUtil;

public class ViewRxActivity extends Activity {

    public static Appointment appointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_rx);

        TextView date = (TextView) findViewById(R.id.viewDate);
        View layout = findViewById(R.id.layoutAppont);
        TextView fname = (TextView) layout.findViewById(R.id.patientFNameTextView);
        TextView lname = (TextView) layout.findViewById(R.id.patientLNameTextView);
        TextView time = (TextView) layout.findViewById(R.id.appointmentTimeTextView);
        TextView gender = (TextView) layout.findViewById(R.id.patientGenderTextView);
        TextView age = (TextView) layout.findViewById(R.id.patientAgeTextView);
        TextView weight = (TextView) layout.findViewById(R.id.patientWeightTextView);

        Customer customer = appointment.getCustomer();
        date.setText(appointment.getDateOfAppointment());
        fname.setText(customer.getfName());
        lname.setText(customer.getlName());
        time.setText(String.format("%02d:%02d", appointment.getFromTime() / 60,
                appointment.getFromTime() % 60));
        gender.setText(customer.getGender());
        //age.setText(customer.getAge());
        //weight.setText("" + customer.getWeight());

        String[] values = {
                MappContract.Prescription.COLUMN_NAME_ID_APPOMT,
                MappContract.Prescription.COLUMN_NAME_DRUG_NAME,
                MappContract.Prescription.COLUMN_NAME_DOSE_QTY,
                MappContract.Prescription.COLUMN_NAME_COURSE_DUR
        };
        int[] viewIds = {
                R.id.viewSrNo,
                R.id.viewDrugName,
                R.id.viewDoseQty,
                R.id.viewCourseDur
        };

        ListView rxItemsList = (ListView) findViewById(R.id.listRxItems);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.layout_rx_item,
                DBUtil.getRxCursor(new MappDbHelper(getApplicationContext()), appointment.getId()),
                values,
                viewIds, 0);
        rxItemsList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_rx, menu);
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
