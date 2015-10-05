package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.activity.FirstFlipperActivity;
import com.extenprise.mapp.activity.LoginActivity;
import com.extenprise.mapp.activity.MappService;
import com.extenprise.mapp.data.SearchServProvForm;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.service.data.ServProvHasServPt;
import com.extenprise.mapp.service.data.ServProvHasService;
import com.extenprise.mapp.service.data.Service;
import com.extenprise.mapp.service.data.ServicePoint;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.DBUtil;
import com.extenprise.mapp.util.UIUtility;


public class SearchServProvActivity extends Activity {

    private Messenger mService;
    private SearchResponseHandler mRespHandler = new SearchResponseHandler(this);

    private EditText mDrClinicName;
    private Spinner mSpeciality;
    private Spinner mServProvCategory;
    private EditText mLocation;
    private View mProgressView;
    private View mSearchFormView;
    SearchServProvForm mForm;

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
        mServProvCategory = (Spinner)findViewById(R.id.spinServiceProvCategory);
        mLocation = (EditText) findViewById(R.id.editTextSearchLoc);
        mSearchFormView = findViewById(R.id.search_form);
        mProgressView = findViewById(R.id.search_progress);

        mServProvCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String servCategory = mServProvCategory.getSelectedItem().toString();
                MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
                DBUtil.setSpecOfCategory(getApplicationContext(), dbHelper, servCategory, mSpeciality);
                //setSpecs(specs);
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

    public void viewFlipper(View view) {
        Intent i = new Intent(this, FirstFlipperActivity.class);
        startActivity(i);
    }

    public void advSearch(View view) {
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
        s.setServCatagory(sc);
        ServProvHasService sps = new ServProvHasService();
        sps.setService(s);

        spsspt.setServicePoint(spoint);
        //spsspt.setServiceProvider(PHasService(sps));
        LoginHolder.spsspt = spsspt;

        Intent i = new Intent(this, AdvSearchServProvActivity.class);
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
        String sp = mSpeciality.getSelectedItem().toString();
        if(sp.equals("Select Speciality") || sp.equals("Other")) {
            sp = "";
        }
/*
        String sc = mServProvCategory.getSelectedItem().toString();
        if(sc.equals("Select Category")) {
            sc = "";
        }
*/

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

        mForm = new SearchServProvForm();
        mForm.setName(dr);
        mForm.setClinic(clinic);
        mForm.setSpeciality(sp);
        mForm.setLocation(loc);

        /*SearchServProv.mDbHelper = new MappDbHelper(getApplicationContext());*/
        UIUtility.showProgress(this, mSearchFormView, mProgressView, true);
        Intent intent = new Intent(this, MappService.class);
        bindService(intent, mConnection, 0);
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
            UIUtility.showProgress(mActivity, mSearchFormView, mProgressView, false);
            if (success) {
                Intent intent = new Intent(mActivity, SearchServProvResultActivity.class);
                startActivity(intent);
            } else {
                UIUtility.showAlert(mActivity,"","Sorry, No result matches your search criteria!");
            }
        }

        @Override
        protected void onCancelled() {
            UIUtility.showProgress(mActivity, mSearchFormView, mProgressView, false);
        }
    }
*/

    protected void searchDone(Bundle msgData) {
        UIUtility.showProgress(this, mSearchFormView, mProgressView, false);
        unbindService(mConnection);
        boolean success = msgData.getBoolean("status");
        if (success) {
            Intent intent = new Intent(this, SearchServProvResultActivity.class);
            intent.putParcelableArrayListExtra("servProvList", msgData.getParcelableArrayList("servProvList"));
            startActivity(intent);
        }
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            mService = new Messenger(service);
            Bundle bundle = new Bundle();
            bundle.putParcelable("form", mForm);
            Message msg = Message.obtain(null, MappService.DO_SEARCH_SERV_PROV);
            msg.replyTo = new Messenger(mRespHandler);
            msg.setData(bundle);

            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
        }
    };

    private static class SearchResponseHandler extends Handler {
        private SearchServProvActivity mActivity;

        public SearchResponseHandler(SearchServProvActivity activity) {
            mActivity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MappService.DO_SEARCH_SERV_PROV:
                    mActivity.searchDone(msg.getData());
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

}
