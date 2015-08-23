package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.service.data.ServProvHasServHasServPt;
import com.extenprise.mapp.service.data.ServProvHasService;
import com.extenprise.mapp.service.data.Service;
import com.extenprise.mapp.service.data.ServicePoint;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.SearchDoctor;
import com.extenprise.mapp.util.UIUtility;

public class SearchDoctorActivity extends Activity {

    private UserSearchTask mSearchTask = null;
    private EditText mDrClinicName;
    private EditText mSpeciality;
    private EditText mLocation;
    private View mProgressView;
    private View mSearchFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_doctor);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setDisplayShowTitleEnabled(false);

        LoginHolder.spsspt = new ServProvHasServHasServPt();
        LoginHolder.servLoginRef = new ServiceProvider();

        mDrClinicName = (EditText) findViewById(R.id.editTextSearchDr);
        mSpeciality = (EditText) findViewById(R.id.editTextSearchSp);
        mLocation = (EditText) findViewById(R.id.editTextSearchLoc);
        mSearchFormView = findViewById(R.id.search_form);
        mProgressView = findViewById(R.id.search_progress);
    }

    public void advSearch(View view) {
        String name = mDrClinicName.getText().toString();
        String loc = mLocation.getText().toString();
        String sp = mSpeciality.getText().toString();

        ServProvHasServHasServPt spsspt = new ServProvHasServHasServPt();

        ServicePoint spoint = new ServicePoint();
        spoint.setName(name);
        spoint.setLocation(loc);

        Service s = new Service();
        s.setSpeciality(sp);
        ServProvHasService sps = new ServProvHasService();
        sps.setService(s);

        spsspt.setServicePoint(spoint);
        spsspt.setServProvHasService(sps);
        LoginHolder.spsspt = spsspt;

        Intent i = new Intent(this, AdvanceSearchDoc.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_doctor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_search:
                return true;
            case R.id.action_sign_in:
                showSignInScreen(null);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showSignInScreen(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void searchDr(View view) {

        String name = mDrClinicName.getText().toString().trim();
        String loc = mLocation.getText().toString().trim();
        String sp = mSpeciality.getText().toString().trim();
        String dr = name, clinic = name;

        if(!name.equals("")) {
            if(name.contains(",")) {
                String[] str = name.split(",");
                dr = str[0];
                if(str.length > 1) {
                    clinic = str[1].trim();
                }
            }
        }

        /*SearchDoctor.mDbHelper = new MappDbHelper(getApplicationContext());*/
        UIUtility.showProgress(this, mSearchFormView, mProgressView, true);
        mSearchTask = new UserSearchTask(this, dr, clinic, sp, loc);
        mSearchTask.execute((Void) null);
    }

    public class UserSearchTask extends AsyncTask<Void, Void, Boolean> {

        private final Activity mActivity;
        private final String mName, mClinic, mSpec, mLoc;

        UserSearchTask(Activity activity, String name, String clinic, String spec, String loc) {
            mActivity = activity;
            mName = name;
            mClinic = clinic;
            mSpec = spec;
            mLoc = loc;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
            boolean presence = false;

            if(SearchDoctor.searchByAll(dbHelper, mName, mClinic, mSpec, mLoc, "", "", "", "", "", "")) {
                presence = true;
            }
            return presence;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mSearchTask = null;
            UIUtility.showProgress(mActivity, mSearchFormView, mProgressView, false);
            if (success) {
                Intent intent = new Intent(mActivity, SearchDocResultList.class);
                startActivity(intent);
            } else {
                UIUtility.showAlert(mActivity,"No Results Found","Sorry, No result matches to your criteria!");
                return;
            }
        }

        @Override
        protected void onCancelled() {
            mSearchTask = null;
            UIUtility.showProgress(mActivity, mSearchFormView, mProgressView, false);
        }
    }

}
