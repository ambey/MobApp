package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.customer.activity.PatientHistoryActivity;
import com.extenprise.mapp.customer.data.Customer;
import com.extenprise.mapp.data.Appointment;
import com.extenprise.mapp.data.RxItem;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.util.DBUtil;
import com.extenprise.mapp.util.UIUtility;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class AppointmentDetailsActivity extends Activity {

    private int mAppontId;
    private int mCustId;
    private int mLastAppontId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_details);

        View view = findViewById(R.id.appointmentLayout);
        TextView fNameView = (TextView) view.findViewById(R.id.patientFNameTextView);
        TextView lNameView = (TextView) view.findViewById(R.id.patientLNameTextView);
        TextView timeView = (TextView) view.findViewById(R.id.appointmentTimeTextView);
        TextView genderView = (TextView) view.findViewById(R.id.patientGenderTextView);
        TextView ageView = (TextView) view.findViewById(R.id.patientAgeTextView);
        TextView wtView = (TextView) view.findViewById(R.id.patientWeightTextView);
        TextView dateView = (TextView) findViewById(R.id.appointmentDateTextView);
        Button rxButton = (Button) findViewById(R.id.rXButton);
        Button uploadRxButton = (Button) findViewById(R.id.uploadScanRxButton);

        Intent intent = getIntent();
        mAppontId = intent.getIntExtra("appont_id", -1);
        mCustId = intent.getIntExtra("cust_id", -1);

        MappDbHelper dbHelper = new MappDbHelper(this);
        Appointment appointment = DBUtil.getAppointment(dbHelper, mAppontId);
        Customer customer = appointment.getCustomer();

        String dateStr = appointment.getDateOfAppointment();
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        try {
            Date date = sdf.parse(dateStr);
            date.setTime(date.getTime() + appointment.getFromTime() * 60 * 1000);
            if (date.after(today)) {
                rxButton.setEnabled(false);
                uploadRxButton.setEnabled(false);

                rxButton.setBackgroundResource(R.drawable.inactive_button);
                uploadRxButton.setBackgroundResource(R.drawable.inactive_button);
            } else if (date.before(today)) {
                int day = cal.get(Calendar.DAY_OF_MONTH);
                cal.setTime(date);
                int apptDay = cal.get(Calendar.DAY_OF_MONTH);
                if (apptDay != day) {
                    rxButton.setEnabled(false);
                    uploadRxButton.setEnabled(true);

                    rxButton.setBackgroundResource(R.drawable.inactive_button);
                    uploadRxButton.setBackgroundResource(R.drawable.button);
                } else {
                    rxButton.setEnabled(true);
                    uploadRxButton.setEnabled(true);

                    rxButton.setBackgroundResource(R.drawable.button);
                    uploadRxButton.setBackgroundResource(R.drawable.button);
                }
            } else {
                rxButton.setEnabled(false);
                uploadRxButton.setEnabled(false);

                rxButton.setBackgroundResource(R.drawable.inactive_button);
                uploadRxButton.setBackgroundResource(R.drawable.inactive_button);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        dateView.setText(dateStr);
        fNameView.setText(customer.getfName());
        lNameView.setText(customer.getlName());
        timeView.setText(String.format("%02d:%02d",
                appointment.getFromTime() / 60, appointment.getFromTime() % 60));
        genderView.setText(customer.getGender());
        ageView.setText("" + customer.getAge());
        wtView.setText(String.format("%.1f", customer.getWeight()));

        View pastAppontLayout = findViewById(R.id.pastAppointmentLayout);
        Button viewMoreButton = (Button) findViewById(R.id.viewMoreButton);
        if (viewMoreButton == null) {
            viewMoreButton = (Button) pastAppontLayout.findViewById(R.id.viewMoreButton);
        }
        viewMoreButton.setVisibility(View.VISIBLE);
        List<Appointment> pastApponts = getPastAppointments(appointment);
        mLastAppontId = -1;
        if(pastApponts != null) {
            Appointment lastAppont = pastApponts.get(pastApponts.size() - 1);
            mLastAppontId = lastAppont.getId();
            TextView dateOthView = (TextView) pastAppontLayout.findViewById(R.id.dateTextView);
            TextView idOthView = (TextView) pastAppontLayout.findViewById(R.id.appontIdTextView);
            dateOthView.setText(lastAppont.getDateOfAppointment());
            idOthView.setText("" + lastAppont.getId());
        } else {
            pastAppontLayout.setVisibility(View.INVISIBLE);
            TextView msgView = (TextView) findViewById(R.id.viewMsg);
            msgView.setVisibility(View.VISIBLE);
        }
        if (pastApponts == null || pastApponts.size() <= 1) {
            viewMoreButton.setEnabled(false);
            viewMoreButton.setBackgroundResource(R.drawable.inactive_button);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_appointment_details, menu);
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

    public void showRxActivity(View view) {
        Intent intent = new Intent(this, RxActivity.class);
        intent.putExtra("parent-activity", getClass().getName());
        intent.putExtra("appont_id", mAppontId);
        intent.putExtra("cust_id", mCustId);
        startActivity(intent);
    }

    public void showRxDetails(View view) {
        Intent intent = new Intent(this, ViewRxActivity.class);
        intent.putExtra("parent-activity", getClass().getName());
        intent.putExtra("appont_id", mAppontId);
        intent.putExtra("last_appont_id", mLastAppontId);
        intent.putExtra("cust_id", mCustId);
        startActivity(intent);
    }

    public void showPatientHistory(View view) {
        Intent intent = new Intent(this, PatientHistoryActivity.class);
        intent.putExtra("parent-activity", getClass().getName());
        intent.putExtra("sp_id", LoginHolder.servLoginRef.getIdServiceProvider());
        intent.putExtra("cust_id", mCustId);
        intent.putExtra("appont_id", mAppontId);
        startActivity(intent);
    }

    public void startFileChooser(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            SaveBlobTask saveBlobTask = new SaveBlobTask(this, data);
            saveBlobTask.execute();
        }
    }

    private class SaveBlobTask extends AsyncTask<Void, Void, Void> {

        private Activity myActivity;
        private Intent mData;

        public SaveBlobTask(Activity activity, Intent data) {
            myActivity = activity;
            mData = data;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
/*

                try {
                    InputStream stream = getContentResolver().openInputStream(
                            mData.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(stream);
                    stream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
*/
            String path = mData.getDataString();
            try {
                RandomAccessFile raf = new RandomAccessFile(path, "r");
                byte[] bytes = new byte[(int)raf.length()];
                raf.readFully(bytes);
                raf.close();
                ContentValues values = new ContentValues();
                Appointment appointment = DBUtil.getAppointment(dbHelper, mAppontId);
                String rxId = "a" + appointment.getId() + "r" + (appointment.getReportCount() + 1);

                values.put(MappContract.Prescription.COLUMN_NAME_ID_APPOMT, appointment.getId());
                values.put(MappContract.Prescription.COLUMN_NAME_ID_RX, rxId);
                values.put(MappContract.Prescription.COLUMN_NAME_SCANNED_COPY, bytes);
                db.insert(MappContract.Prescription.TABLE_NAME,null,values);

            } catch (FileNotFoundException x) {
                x.printStackTrace();
            } catch (IOException x) {
                x.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
/*
            try {
                UIUtility.showProgress(myActivity, mForm, mProgressBar, false);
                Intent intent = new Intent(myActivity, Class.forName(mParentActivity));
                intent.putExtra("appont_id", mAppontId);
                intent.putExtra("cust_id", mCustId);
                startActivity(intent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
*/
        }

        @Override
        protected void onCancelled() {
        }
    }

    private List<Appointment> getPastAppointments(Appointment appointment) {
        ArrayList<Appointment> othApponts = DBUtil.getOtherAppointments(new MappDbHelper(this),
                appointment.getId());
        Collections.sort(othApponts);
        int i = 0;
        boolean found = false;
        for (; i < othApponts.size(); i++) {
            if (appointment.compareTo(othApponts.get(i)) >= 0) {
                found = true;
            } else {
                break;
            }
        }
        if (found) {
            return othApponts.subList(0, i);
        }
        return null;
    }
}
