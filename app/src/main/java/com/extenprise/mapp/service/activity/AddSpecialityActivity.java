package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.activity.LoginActivity;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.service.data.ServProvHasServPt;
import com.extenprise.mapp.service.data.ServProvHasService;
import com.extenprise.mapp.service.data.Service;
import com.extenprise.mapp.service.data.ServicePoint;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.UIUtility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AddSpecialityActivity extends Activity {

    private Spinner mService;
    private EditText mSpeciality;
    private EditText mExperience;
    private Button mAddWorkPlace;
    private Button mAddSpeciality;
    private Button mDone;
    private View mformView;
    private View mprogressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_speciality);

        mformView = findViewById(R.id.addSpecForm);
        mprogressView = findViewById(R.id.progressView);

        mService = (Spinner) findViewById(R.id.editTextService);
        mSpeciality = (EditText) findViewById(R.id.editTextSpec);
        mExperience = (EditText) findViewById(R.id.editTextExp);
        mAddWorkPlace = (Button) findViewById(R.id.buttonAddWorkPlace);
        mAddSpeciality = (Button) findViewById(R.id.buttonAddSpec);
        mDone = (Button) findViewById(R.id.buttonDone);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialize();
    }

/*
    private void setAddWorkPlaceEnabled() {
        boolean enabled = (!mSpeciality.getText().toString().isEmpty() &&
                !mExperience.getText().toString().isEmpty());
        mAddWorkPlace.setEnabled(enabled);

    }
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_speciality, menu);
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

    public void addWorkPlace(View view) {
        if (!isValidInput()) {
            return;
        }

        ServProvHasService sps = new ServProvHasService();
        sps.setServProv(LoginHolder.servLoginRef);
        sps.setExperience(Float.parseFloat(mExperience.getText().toString()));

        Service s = new Service();
        s.setServCatagory(mService.getSelectedItem().toString());
        s.setSpeciality(mSpeciality.getText().toString());


        sps.setService(s);

        ServProvHasServPt spsspt = new ServProvHasServPt();
        LoginHolder.spsspt = spsspt;

        Intent intent = new Intent(this, AddWorkPlaceActivity.class);
        startActivity(intent);
    }

    public void addNewSpeciality(View view) {

    }

    public void saveData(View view) {
        UIUtility.showProgress(this, mformView, mprogressView, true);

        SaveServiceData task = new SaveServiceData(this);
        task.execute((Void) null);
    }

    private boolean isValidInput() {
        boolean valid = true;
        View focusView = null;

        String exp = mExperience.getText().toString();
        if (TextUtils.isEmpty(exp)) {
            mExperience.setError(getString(R.string.error_field_required));
            focusView = mExperience;
            valid = false;
        } else {
            double exp2 = Double.parseDouble(mExperience.getText().toString());
            if (exp2 < 0 || exp2 > 99) {
                mExperience.setError(getString(R.string.error_invalid_experience));
                focusView = mExperience;
                valid = false;
            }
        }

        String speciality = mSpeciality.getText().toString();
        if (TextUtils.isEmpty(speciality)) {
            mSpeciality.setError(getString(R.string.error_field_required));
            focusView = mSpeciality;
            valid = false;
        }
        if(focusView != null) {
            focusView.requestFocus();
        }
        return valid;
    }

    private void initialize() {
        boolean workPlaceAdded = false;
        ServProvHasServPt spsspt = LoginHolder.spsspt;
        ServProvHasService sps = null;
        ServiceProvider sp = LoginHolder.servLoginRef;

        ArrayList<ServProvHasServPt> spsList = sp.getServices();
        if (spsList != null) {
/*
            for (int i = spsList.size() - 1; i >= 0; i--) {
                if (spsList.get(i).getWorkPlaceCount() == 0) {
                    spsList.remove(i);
                }
            }
*/
        }

        if (spsspt != null) {
/*
            sps = spsspt.getServProvHasService();
            if (sps != null) {
                sp = sps.getServProv();
                workPlaceAdded = sps.isWorkPlaceAdded();
            }
*/
        }

        TextView specialityLbl = (TextView) findViewById(R.id.viewSpecialityLbl);
        String text = specialityLbl.getText().toString().split(":")[0];

        if (!workPlaceAdded) {
            text += ": #1";
            specialityLbl.setText(text);

            mAddSpeciality.setVisibility(View.INVISIBLE);
            mDone.setVisibility(View.INVISIBLE);
            mAddWorkPlace.setVisibility(View.VISIBLE);
        } else {
            int count = 1;
            if (sp != null) {
                count = sp.getServiceCount();
                if (count > 0) {
                    String speciality = sps.getService().getSpeciality();
                    String exp = "" + sps.getExperience();
                    mSpeciality.setText(speciality);
                    mExperience.setText(exp);
                }
            }
            text += ": #" + count;
            specialityLbl.setText(text);

            mAddWorkPlace.setVisibility(View.INVISIBLE);
            mAddSpeciality.setVisibility(View.VISIBLE);
            mDone.setVisibility(View.VISIBLE);
        }
    }

    class SaveServiceData extends AsyncTask<Void, Void, Void> {

        private Activity myActivity;

        public SaveServiceData(Activity activity) {
            myActivity = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ServiceProvider sp = LoginHolder.servLoginRef;
            ArrayList<ServProvHasServPt> spsList = sp.getServices();

            MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(MappContract.ServiceProvider.COLUMN_NAME_EMAIL_ID, sp.getEmailId());
            values.put(MappContract.ServiceProvider.COLUMN_NAME_FNAME, sp.getfName());
            values.put(MappContract.ServiceProvider.COLUMN_NAME_LNAME, sp.getlName());
            values.put(MappContract.ServiceProvider.COLUMN_NAME_PASSWD, sp.getPasswd());

            long spId = db.insert(MappContract.ServiceProvider.TABLE_NAME, null, values);

            SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getTimeInstance();

            for (ServProvHasServPt sps : spsList) {
                values = new ContentValues();
                values.put(MappContract.ServProvHasServ.COLUMN_NAME_ID_SERV_PROV, spId);
                values.put(MappContract.ServProvHasServ.COLUMN_NAME_SERVICE_NAME, sps.getService());
                values.put(MappContract.ServProvHasServ.COLUMN_NAME_SPECIALITY, sps.getService());
                values.put(MappContract.ServProvHasServ.COLUMN_NAME_EXPERIENCE, sps.getExperience());

                long spsId = db.insert(MappContract.ServProvHasServ.TABLE_NAME, null, values);

                ArrayList<ServProvHasServPt> spssptList = LoginHolder.servLoginRef.getServices();
                for (ServProvHasServPt spsspt : spssptList) {
                    ServicePoint spt = spsspt.getServicePoint();

                    values = new ContentValues();
                    values.put(MappContract.ServicePoint.COLUMN_NAME_NAME, spt.getName());
                    values.put(MappContract.ServicePoint.COLUMN_NAME_LOCATION, spt.getLocation());
                    values.put(MappContract.ServicePoint.COLUMN_NAME_PHONE, spt.getPhone());
                    values.put(MappContract.ServicePoint.COLUMN_NAME_ID_CITY, spt.getCity().getIdCity());

                    long sptId = db.insert(MappContract.ServicePoint.TABLE_NAME, null, values);

                    values = new ContentValues();
                    values.put(MappContract.ServProvHasServHasServPt.COLUMN_NAME_ID_SERV_PROV_HAS_SERV, spsId);
                    values.put(MappContract.ServProvHasServHasServPt.COLUMN_NAME_ID_SERV_PT, sptId);
                    try {
                        values.put(MappContract.ServProvHasServHasServPt.COLUMN_NAME_START_TIME, sdf.format(spsspt.getStartTime()));
                        values.put(MappContract.ServProvHasServHasServPt.COLUMN_NAME_END_TIME, sdf.format(spsspt.getEndTime()));
                    } catch (Exception x) {
                        x.printStackTrace();
                    }
                    values.put(MappContract.ServProvHasServHasServPt.COLUMN_NAME_WEEKLY_OFF, spsspt.getWorkingDays());

                    db.insert(MappContract.ServProvHasServHasServPt.TABLE_NAME, null, values);

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            UIUtility.showProgress(myActivity, mformView, mprogressView, false);
            Intent intent = new Intent(myActivity, LoginActivity.class);
            startActivity(intent);
        }

        @Override
        protected void onCancelled() {
            UIUtility.showProgress(myActivity, mformView, mprogressView, false);
        }

    }

}
