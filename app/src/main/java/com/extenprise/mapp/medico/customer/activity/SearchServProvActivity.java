package com.extenprise.mapp.medico.customer.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.extenprise.mapp.medico.LoginHolder;
import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.activity.FirstFlipperActivity;
import com.extenprise.mapp.medico.activity.LoginActivity;
import com.extenprise.mapp.medico.net.MappService;
import com.extenprise.mapp.medico.net.MappServiceConnection;
import com.extenprise.mapp.medico.net.ResponseHandler;
import com.extenprise.mapp.medico.net.ServiceResponseHandler;
import com.extenprise.mapp.medico.service.data.SearchServProvForm;
import com.extenprise.mapp.medico.util.Utility;

import java.util.ArrayList;


public class SearchServProvActivity extends Activity implements ResponseHandler, LocationListener {

    SearchServProvForm mForm;
    ArrayList<String> specList;
    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));
    private EditText mDrClinicName;
    private Spinner mSpeciality;
    private Spinner mServProvCategory;
    private EditText mLocation;
    private View mProgressView;
    private View mSearchFormView;

    private LocationManager locationManager;
    private String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_serv_prov);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);

            if (LoginHolder.custLoginRef != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        mDrClinicName = (EditText) findViewById(R.id.editTextSearchDr);
        mSpeciality = (Spinner) findViewById(R.id.editTextSearchSp);
        mServProvCategory = (Spinner) findViewById(R.id.spinServiceProvCategory);
        mLocation = (EditText) findViewById(R.id.editTextSearchLoc);
        mSearchFormView = findViewById(R.id.search_form);
        mProgressView = findViewById(R.id.search_progress);

        mServProvCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                getSpeciality();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });


        //Prompt the user to Enabled GPS
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Utility.showAlert(this, getString(R.string.msg_use_loc), getString(R.string.msg_use_gps),
                    true, null, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (which == -1) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                }
            });
        }

        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = null;
        try {
            location = locationManager.getLastKnownLocation(provider);
        } catch(SecurityException e) {
            e.printStackTrace();
        }
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } /*else {
            mLocation.setError("Location Unavailable");
        }*/

        /*ArrayList<String> list = new ArrayList<>();
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(this, R.layout.layout_spinner, list);
        mSpeciality.setAdapter(spinnerAdapter);*/
    }

    private void getSpeciality() {
        String selectedCategory = mServProvCategory.getSelectedItem().toString();
        if (selectedCategory.equalsIgnoreCase(getString(R.string.select_category))) {
            mSpeciality.setAdapter(null);
            return;
        }
        Bundle bundle = new Bundle();
        mForm = new SearchServProvForm();
        mForm.setCategory(selectedCategory);
        bundle.putParcelable("form", mForm);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_GET_SPECIALITY);
        if (Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE)) {
            Utility.showProgress(this, mSearchFormView, mProgressView, true);
        }
    }

    private void gotSpecialities(Bundle data) {
        Utility.showProgress(this, mSearchFormView, mProgressView, false);

        specList = data.getStringArrayList("specialities");
        if (specList == null) {
            specList = new ArrayList<>();
        }
        specList.add(0, getString(R.string.select_speciality));
        SpinnerAdapter adapter = new ArrayAdapter<>(this, R.layout.layout_spinner, specList);
        mSpeciality.setAdapter(adapter);
    }

    public void viewFlipper(View view) {
        Intent i = new Intent(this, FirstFlipperActivity.class);
        startActivity(i);
    }

    public void advSearch(View view) {
/*
        String name = mDrClinicName.getText().toString().trim();
        String loc = mLocation.getText().toString().trim();
        String sp = mSpeciality.getSelectedItem().toString();
        String sc = mServProvCategory.getSelectedItem().toString();

        ServProvHasServPt spsspt = new ServProvHasServPt();

        ServicePoint spoint = new ServicePoint();
        spoint.setName(name);
        spoint.setLocation(loc);

        Service s = new Service();
        s.setSpeciality(sp);
        s.setCategory(sc);

        spsspt.setServicePoint(spoint);
        spsspt.setService(s);
*/
        fillSearchForm();
        Intent intent = new Intent(this, AdvSearchServProvActivity.class);
        intent.putExtra("form", mForm);
        //SpinnerAdapter adp = mSpeciality.getAdapter();
        /*ArrayList list = new ArrayList();
        for(int i = 0; i < mSpeciality.getCount(); i++) {
            list.add(mSpeciality.getItemAtPosition(i));
        }*/
        intent.putStringArrayListExtra("specList", specList);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_doctor, menu);
        return super.onCreateOptionsMenu(menu);
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
        Intent intent;
        if (LoginHolder.custLoginRef != null) {
            intent = new Intent(this, PatientsHomeScreenActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
    }

    private void fillSearchForm() {
        String name = mDrClinicName.getText().toString().trim();
        String loc = mLocation.getText().toString().trim();
        String sp = "";
        if (mSpeciality.getSelectedItem() != null) {
            sp = mSpeciality.getSelectedItem().toString();
        }
        if (sp.equals(getResources().getString(R.string.select_speciality)) ||
                sp.equals(getResources().getString(R.string.other))) {
            sp = "";
        }
        String sc = "";
        if (mServProvCategory.getSelectedItem() != null) {
            sc = mServProvCategory.getSelectedItem().toString();
        }
        if (sc.equals(getResources().getString(R.string.select_category))) {
            sc = "";
        }

        String dr = name, clinic = name;

        if (!name.equals("")) {
            if (name.contains(",")) {
                String[] str = name.split(",");
                dr = str[0];
                if (str.length > 1) {
                    clinic = str[1].trim();
                }
            }
        }

        mForm = new SearchServProvForm();
        mForm.setName(dr);
        mForm.setClinic(clinic);
        mForm.setCategory(sc);
        mForm.setSpeciality(sp);
        mForm.setLocation(loc);
    }

    public void searchDr(View view) {
        fillSearchForm();
        Bundle bundle = new Bundle();
        bundle.putParcelable("form", mForm);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_SEARCH_SERV_PROV);
        if (Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE)) {
            Utility.showProgress(this, mSearchFormView, mProgressView, true);
        }

        /*SearchServProv.mDbHelper = new MappDbHelper(getApplicationContext());*/
/*
        mSearchTask = new UserSearchTask(this, dr, clinic, sp, sc, loc);
        mSearchTask.execute((Void) null);
*/
    }

    protected void searchDone(Bundle msgData) {
        Utility.showProgress(this, mSearchFormView, mProgressView, false);
        boolean success = msgData.getBoolean("status");
        if (success) {
            //Send.email(this, "Test", "Test Mail From Mob App.", "jain_avinash@extenprise.com");
            Intent intent = new Intent(this, SearchServProvResultActivity.class);
            intent.putParcelableArrayListExtra("servProvList", msgData.getParcelableArrayList("servProvList"));
            startActivity(intent);
        } else {
            Utility.showMessage(this, R.string.msg_no_result);
        }
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        if (action == MappService.DO_SEARCH_SERV_PROV) {
            searchDone(data);
            return true;
        } else if (action == MappService.DO_GET_SPECIALITY) {
            gotSpecialities(data);
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        Intent intent = super.getParentActivityIntent();
        if (LoginHolder.custLoginRef != null) {
            intent = new Intent(this, PatientsHomeScreenActivity.class);
        }
        return intent;
    }

    @Override
    public void onBackPressed() {
        mConnection.setBound(false);
        //startActivity(getIntent());
        super.onBackPressed();
    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        try {
            locationManager.requestLocationUpdates(provider, 400, 1, this);
        } catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        try {
            locationManager.removeUpdates(this);
        } catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        int lat = (int) (location.getLatitude());
        int lng = (int) (location.getLongitude());
        mLocation.setText(String.format("%s%s%s", String.valueOf(lat), getString(R.string.comma), String.valueOf(lng)));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /*
    public class UserSearchTask extends AsyncTask<Void, Void, Boolean> {

        private final Activity mActivity;
        private final String mName, mClinic, mSpec, mServCategory, mLoc;

        UserSearchTask(Activity activity, String name, String clinic, String spec, String mservCategory, String loc) {
            mActivity = activity;
            mName = name;
            mClinic = clinic;
            mSpec = spec;
            mLoc = loc;
            mServCategory = mservCategory;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
            boolean presence = false;

            if(SearchServProv.searchByAll(dbHelper, mName, mClinic, mSpec, mServCategory, mLoc, "", "", "", "", "", "", "")) {
                presence = true;
            }
            return presence;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Utility.showProgress(mActivity, mSearchFormView, mProgressView, false);
            if (success) {
                Intent intent = new Intent(mActivity, SearchServProvResultActivity.class);
                startActivity(intent);
            } else {
                Utility.showAlert(mActivity,"","Sorry, No result matches your search criteria!");
            }
        }

        @Override
        protected void onCancelled() {
            Utility.showProgress(mActivity, mSearchFormView, mProgressView, false);
        }
    }
*/
}
