package com.extenprise.mapp.medico.customer.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.activity.FirstFlipperActivity;
import com.extenprise.mapp.medico.activity.LoginActivity;
import com.extenprise.mapp.medico.customer.data.Customer;
import com.extenprise.mapp.medico.data.WorkingDataStore;
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

    /*private LocationManager locationManager;
    private String provider;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_serv_prov);

        Customer customer = (Customer) WorkingDataStore.getLoginRef();
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            if (customer != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        /*if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }*/


        mDrClinicName = (EditText) findViewById(R.id.editTextSearchDr);
        mSpeciality = (Spinner) findViewById(R.id.editTextSearchSp);
        mServProvCategory = (Spinner) findViewById(R.id.spinServiceProvCategory);
        mLocation = (EditText) findViewById(R.id.editTextSearchLoc);

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
        /*locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
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
        }*/ /*else {
            mLocation.setError("Location Unavailable");
        }*/

        /*ArrayList<String> list = new ArrayList<>();
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(this, R.layout.layout_spinner, list);
        mSpeciality.setAdapter(spinnerAdapter);*/
    }

    private void getSpeciality() {
        String selectedCategory = mServProvCategory.getSelectedItem().toString();
        if (selectedCategory.equalsIgnoreCase(getString(R.string.select_category))) {
            ArrayList<String> specList = new ArrayList<>();
            specList.add(getString(R.string.select_speciality));
            mSpeciality.setAdapter(new ArrayAdapter<>(this, R.layout.layout_spinner, specList));
            return;
        }
        Bundle bundle = new Bundle();
        mForm = new SearchServProvForm();
        mForm.setCategory(selectedCategory);
        bundle.putParcelable("form", mForm);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_GET_SPECIALITY);
        if (Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE)) {
            //Utility.showProgress(this, mSearchFormView, mProgressView, true);
            Utility.showProgressDialog(this, true);
        }
    }

    private void gotSpecialities(Bundle data) {
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
        Bundle bundle = WorkingDataStore.getBundle();
        bundle.putParcelable("searchForm", mForm);
        bundle.putStringArrayList("specList", specList);
        Intent intent = new Intent(this, AdvSearchServProvActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        /*if (WorkingDataStore.getBundle().getParcelable("customer") == null) {
            //menu.removeItem(R.id.logout); Not Removing Item
            //menu.findItem(R.id.logout).setVisible(false); // Null Pointer Exception
        }*/
        getMenuInflater().inflate(R.menu.menu_search_doctor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (WorkingDataStore.getLoginRef() == null) {
            //menu.removeItem(R.id.logout);
            menu.findItem(R.id.logout).setVisible(false);
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
            case R.id.action_sign_in:
                Intent intent;
                if (WorkingDataStore.getLoginRef() != null) {
                    intent = new Intent(this, PatientsHomeScreenActivity.class);
                } else {
                    intent = new Intent(this, LoginActivity.class);
                }
                startActivity(intent);
                return true;
            case R.id.logout:
                Utility.logout(getSharedPreferences("loginPrefs", MODE_PRIVATE), this, LoginActivity.class);
                return true;
        }

        return super.onOptionsItemSelected(item);
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
            //Utility.showProgress(this, mSearchFormView, mProgressView, true);
            Utility.showProgressDialog(this, true);
        }

        /*SearchServProv.mDbHelper = new MappDbHelper(getApplicationContext());*/
/*
        mSearchTask = new UserSearchTask(this, dr, clinic, sp, sc, loc);
        mSearchTask.execute((Void) null);
*/
    }

    protected void searchDone(Bundle msgData) {
        ArrayList<Parcelable> list = msgData.getParcelableArrayList("servProvList");
        if (list != null && list.size() > 0) {
            Bundle bundle = WorkingDataStore.getBundle();
            bundle.putParcelableArrayList("servProvList", msgData.getParcelableArrayList("servProvList"));
            SearchServProvResultActivity.setParentActivity(this.getClass().getName());
            Intent intent = new Intent(this, SearchServProvResultActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        Utility.showProgressDialog(this, false);
        boolean success = data.getBoolean("status");
        if (action == MappService.DO_SEARCH_SERV_PROV) {
            searchDone(data);
        } else if (action == MappService.DO_GET_SPECIALITY && success) {
            gotSpecialities(data);
        }
        return success;
    }

    @Override
    public Intent getParentActivityIntent() {
        if (WorkingDataStore.getLoginRef() != null) {
            return new Intent(this, PatientsHomeScreenActivity.class);
        }
        return super.getParentActivityIntent();
    }

    @Override
    public void onBackPressed() {
        mConnection.setBound(false);
        Intent intent = getParentActivityIntent();
        if (intent != null) {
            startActivity(intent);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (WorkingDataStore.getLoginRef() != null) {
                Utility.startActivity(this, PatientsHomeScreenActivity.class);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onLocationChanged(Location location) {

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
