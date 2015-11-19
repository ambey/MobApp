package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.activity.FirstFlipperActivity;
import com.extenprise.mapp.activity.LoginActivity;
import com.extenprise.mapp.net.AppStatus;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.MappServiceConnection;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.data.SearchServProvForm;
import com.extenprise.mapp.service.data.ServProvHasServPt;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.Utility;

import java.util.ArrayList;


public class SearchServProvActivity extends Activity implements ResponseHandler {

    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));

    private EditText mDrClinicName;
    private Spinner mSpeciality;
    private Spinner mServProvCategory;
    private EditText mLocation;
    private View mProgressView;
    private View mSearchFormView;
    SearchServProvForm mForm;

    ArrayList<String> specList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_serv_prov);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setDisplayShowTitleEnabled(false);

        LoginHolder.spsspt = new ServProvHasServPt();
        LoginHolder.servLoginRef = new ServiceProvider();//spinServiceProvCategory

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

        /*ArrayList<String> list = new ArrayList<>();
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(this, R.layout.layout_spinner, list);
        mSpeciality.setAdapter(spinnerAdapter);*/
    }

    private void getSpeciality() {
        if (!AppStatus.getInstance(this).isOnline()) {
            Utility.showMessage(this, R.string.error_not_online);
            return;
        }
        String selectedCategory = mServProvCategory.getSelectedItem().toString();
        if(selectedCategory.equalsIgnoreCase(getString(R.string.select_category))) {
            return;
        }
        Utility.showProgress(this, mSearchFormView, mProgressView, true);
        Bundle bundle = new Bundle();
        mForm = new SearchServProvForm();
        mForm.setCategory(selectedCategory);
        bundle.putParcelable("form", mForm);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_GET_SPECIALITY);
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
    }

    private void gotSpecialities(Bundle data) {
        Utility.showProgress(this, mSearchFormView, mProgressView, false);

        specList = data.getStringArrayList("specialities");
        if (specList == null) {
            specList = new ArrayList<>();
        }
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
        if (AppStatus.getInstance(this).isOnline()) {
            //Toast.makeText(this, "You are online!!!!", Toast.LENGTH_LONG).show();
            Utility.showProgress(this, mSearchFormView, mProgressView, true);
            Bundle bundle = new Bundle();
            bundle.putParcelable("form", mForm);
            mConnection.setData(bundle);
            mConnection.setAction(MappService.DO_SEARCH_SERV_PROV);
            Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
        } else {
            Toast.makeText(this, "You are not online!!!!", Toast.LENGTH_LONG).show();
            Log.v("Home", "############################You are not online!!!!");
        }

        /*SearchServProv.mDbHelper = new MappDbHelper(getApplicationContext());*/

/*
        mSearchTask = new UserSearchTask(this, dr, clinic, sp, sc, loc);
        mSearchTask.execute((Void) null);
*/
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

    protected void searchDone(Bundle msgData) {
        Utility.showProgress(this, mSearchFormView, mProgressView, false);
        boolean success = msgData.getBoolean("status");
        if (success) {
            Intent intent = new Intent(this, SearchServProvResultActivity.class);
            intent.putParcelableArrayListExtra("servProvList", msgData.getParcelableArrayList("servProvList"));
            startActivity(intent);
        } else {
            Utility.showMessage(this, R.string.no_result);
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

}
