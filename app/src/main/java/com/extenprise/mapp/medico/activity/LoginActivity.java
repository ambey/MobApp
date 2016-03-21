package com.extenprise.mapp.medico.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.customer.activity.BookAppointmentActivity;
import com.extenprise.mapp.medico.customer.activity.PatientsHomeScreenActivity;
import com.extenprise.mapp.medico.customer.activity.SearchServProvActivity;
import com.extenprise.mapp.medico.customer.data.Customer;
import com.extenprise.mapp.medico.data.WorkingDataStore;
import com.extenprise.mapp.medico.net.ErrorCode;
import com.extenprise.mapp.medico.net.MappService;
import com.extenprise.mapp.medico.net.MappServiceConnection;
import com.extenprise.mapp.medico.net.ResponseHandler;
import com.extenprise.mapp.medico.net.ServiceResponseHandler;
import com.extenprise.mapp.medico.service.activity.MedicalStoreHomeActivity;
import com.extenprise.mapp.medico.service.activity.ServProvDetailsActivity;
import com.extenprise.mapp.medico.service.activity.ServiceProviderHomeActivity;
import com.extenprise.mapp.medico.service.data.ServiceProvider;
import com.extenprise.mapp.medico.ui.BackButtonHandler;
import com.extenprise.mapp.medico.util.EncryptUtil;
import com.extenprise.mapp.medico.util.Utility;
import com.extenprise.mapp.medico.util.Validator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements ResponseHandler {

    //ProgressDialog progressDialog;
    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));
    private int mLoginType;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    // UI references.
    private AutoCompleteTextView mMobileNumber;
    private EditText mPasswordView;
    private CheckBox mSaveLoginCheckBox;
    private RadioGroup mRadioGroupUType;
    private boolean isBookAppontRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_toolbar_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

/*
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
*/

/*
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            //actionBar.hide();
        }
*/

        isBookAppontRequest = false;
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        //mProgressView = findViewById(R.id.login_progress);
        mRadioGroupUType = (RadioGroup) findViewById(R.id.radioGroupUserType);

        // Set up the login form.
        mMobileNumber = (AutoCompleteTextView) findViewById(R.id.mobileNumber);
        //populateAutoComplete();
        SharedPreferences preferences = getSharedPreferences("autoComplete", MODE_PRIVATE);
        Set<String> set = preferences.getStringSet("autoCompleteValues", new HashSet<String>());
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        new ArrayList<>(set));
        mMobileNumber.setAdapter(adapter);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.integer.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        View mLoginFormView = findViewById(R.id.login_form);
        //mLoginFormView.setEnabled(false);
        Animation rLayoutAnim = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.img_fade);
        rLayoutAnim.setDuration(3000);
        mLoginFormView.startAnimation(rLayoutAnim);

        Button emailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        emailSignInButton.setEnabled(true);
        emailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        TextView mRegisterButton = (TextView) findViewById(R.id.notRegistered);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //registerServProv();
                Intent intent = new Intent(getApplicationContext(), SignUpPreviousActivity.class);
                startActivity(intent);
            }
        });

        mSaveLoginCheckBox = (CheckBox) findViewById(R.id.rememberMe);
        initialize();

        TextView loginAs = (TextView) findViewById(R.id.viewLogin);

        String targetActivity = getIntent().getStringExtra("target-activity");
        if (targetActivity != null && targetActivity.equalsIgnoreCase(BookAppointmentActivity.class.getName())) {
            isBookAppontRequest = true;
            mLoginType = MappService.CUSTOMER_LOGIN;
            mRadioGroupUType.setVisibility(View.GONE);
            String level = loginAs.getText().toString() + " " + getString(R.string.patient);
            loginAs.setText(level);
        }
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        Log.v("Welcome", "onResume called...");
        initialize();
    }*/

    private void initialize() {
        SharedPreferences loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        Boolean saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin) {
            String userType = loginPreferences.getString("logintype", null);
            if (userType != null) {
                mLoginType = whichLoginType(userType);
                if (mLoginType == MappService.CUSTOMER_LOGIN) {
                    mRadioGroupUType.check(R.id.radioButtonPatient);
                } else {
                    mRadioGroupUType.check(R.id.radioButtonMedServiceProvider);
                }
                mMobileNumber.setText(loginPreferences.getString("username", ""));

                /*boolean logoutDone = loginPreferences.getBoolean("logout", false);
                if (!logoutDone) {
                    doLogin(loginPreferences.getString("username", ""),
                            loginPreferences.getString("passwd", ""));
                }*/
            }
        } else {
            mPasswordView.setText("");
            mMobileNumber.setText("");
        }
    }

    public void onBackPressed() {
        mConnection.setBound(false);
        if (isBookAppontRequest) {
            Intent intent = new Intent(this, ServProvDetailsActivity.class);
            /*intent.putExtra("servProv", getIntent().getParcelableExtra("servProv"));
            intent.putParcelableArrayListExtra("servProvList", getIntent().getParcelableArrayListExtra("servProvList"));*/
            startActivity(intent);
            return;
        }

        final BackButtonHandler buttonHandler = BackButtonHandler.getInstance();
        if (buttonHandler.isBackPressed()) {
            buttonHandler.setBackPressed(false);
            //finish();
            moveTaskToBack(true);
        } else {
            buttonHandler.setBackPressed(true);
            Utility.showMessage(this, R.string.msg_press_back_button);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    buttonHandler.setBackPressed(false);
                }
            }, 3 * 1000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_doctor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //menu.removeItem(R.id.logout);
        menu.findItem(R.id.logout).setVisible(false);
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
                Intent intent = new Intent(this, SearchServProvActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_sign_in:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

    private int whichLoginType(String loginType) {
        if (loginType.equalsIgnoreCase(getString(R.string.patient))) {
            return MappService.CUSTOMER_LOGIN;
        } else if (loginType.equalsIgnoreCase(getString(R.string.serv_prov))) {
            return MappService.SERVICE_LOGIN;
        }
        return -1;
    }

    private String whichLoginType(int loginType) {
        if (loginType == MappService.CUSTOMER_LOGIN) {
            return getString(R.string.patient);
        } else if (loginType == MappService.SERVICE_LOGIN) {
            return getString(R.string.serv_prov);
        }
        return "";
    }

    public void attemptLogin() {
        Button signInButton = (Button) findViewById(R.id.email_sign_in_button);
        signInButton.setEnabled(false);

        // Reset errors.
        mMobileNumber.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String phone = mMobileNumber.getText().toString().trim();
        String passwd = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(phone)) {
            mMobileNumber.setError(getString(R.string.error_field_required));
            focusView = mMobileNumber;
            cancel = true;
        } else if (!Validator.isPhoneValid(phone)) {
            mMobileNumber.setError(getString(R.string.error_invalid_phone));
            focusView = mMobileNumber;
            cancel = true;
        } else if (TextUtils.isEmpty(passwd)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }


        if (!isBookAppontRequest) {
            int uTypeID = mRadioGroupUType.getCheckedRadioButtonId();
            if (uTypeID == -1) {
            /* hide the soft keyboard and show the message */
                Utility.hideSoftKeyboard(this);
                Utility.showMessage(this, R.string.error_user_type_required);
                cancel = true;
            } else {
                RadioButton mRadioButtonUType;
                mRadioButtonUType = (RadioButton) findViewById(uTypeID);
                String uType = mRadioButtonUType.getText().toString().trim();
                mLoginType = whichLoginType(uType);
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (focusView != null) {
                focusView.requestFocus();
            }
            signInButton.setEnabled(true);
            return;
        }
        Utility.hideSoftKeyboard(this);

        String encryptedPasswd = EncryptUtil.encrypt(passwd);
        SharedPreferences loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor loginPrefsEditor = loginPreferences.edit();
        //boolean saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (mSaveLoginCheckBox.isChecked()) {
            loginPrefsEditor.putBoolean("saveLogin", true);
            loginPrefsEditor.putString("username", phone);
            loginPrefsEditor.putString("passwd", encryptedPasswd);
            loginPrefsEditor.putString("logintype", whichLoginType(mLoginType));
            loginPrefsEditor.apply();
        } else {
            loginPrefsEditor.clear();
            loginPrefsEditor.apply();
        }

        Bundle bundle = new Bundle();
        bundle.putInt("loginType", mLoginType);

        Calendar calendar = Calendar.getInstance();
        String lastVisit = String.format("%s %s", Utility.getDateAsStr(calendar.getTime(), "yyyy-MM-dd"),
                Utility.getFormattedTime(calendar));
        if (mLoginType == MappService.CUSTOMER_LOGIN) {
            Customer customer = new Customer();
            customer.getSignInData().setPhone(phone);
            customer.getSignInData().setPasswd(encryptedPasswd);
            customer.setLastVisit(lastVisit);
            bundle.putParcelable("customer", customer);
        } else if (mLoginType == MappService.SERVICE_LOGIN) {
            ServiceProvider serviceProvider = new ServiceProvider();
            serviceProvider.getSignInData().setPhone(phone);
            serviceProvider.getSignInData().setPasswd(encryptedPasswd);
            serviceProvider.setLastVisit(lastVisit);
            bundle.putParcelable("serviceProvider", serviceProvider);
        }
        mConnection.setAction(MappService.DO_LOGIN);
        mConnection.setData(bundle);
        if (Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE)) {
            Utility.showProgressDialog(this, true);
        } else {
            signInButton.setEnabled(true);
        }
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        if (action == MappService.DO_LOGIN) {
            loginDone(data);
        }
        return data.getBoolean("status");
    }

    public void forgotPwd(View view) {
        Utility.showMessage(this, R.string.forgotpwd);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("target-activity", getIntent().getStringExtra("target-activity"));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        getIntent().putExtra("target-activity", savedInstanceState.getString("target-activity"));
    }

    protected void loginDone(Bundle msgData) {
        Utility.showProgressDialog(this, false);
        boolean success = msgData.getBoolean("status");
        if (success) {
            Bundle workingData = WorkingDataStore.getBundle();
            String phone = null, userType;
            Intent intent;

            if (mLoginType == MappService.CUSTOMER_LOGIN) {
                userType = "customer";
                Customer customer = msgData.getParcelable("customer");
                if (customer != null) {
                    workingData.putParcelable("customer", customer);
                    if (customer.getSignInData() != null) {
                        phone = customer.getSignInData().getPhone();
                    }
                    WorkingDataStore.setLoginRef(customer);
                }
                String targetActivity = getIntent().getStringExtra("target-activity");
                if (targetActivity != null) {
                    try {
                        intent = new Intent(this, Class.forName(targetActivity));
                        /*intent.putExtra("servProv", getIntent().getParcelableExtra("servProv"));
                        intent.putParcelableArrayListExtra("servProvList", getIntent().getParcelableArrayListExtra("servProvList"));*/
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        return;
                    }
                } else {
                    intent = new Intent(this, PatientsHomeScreenActivity.class);
                }
            } else {
                userType = "servprov";
                ServiceProvider serviceProvider = msgData.getParcelable("service");
                assert serviceProvider != null;
                String servPointType = serviceProvider.getServProvHasServPt(0).getServPointType();
                Log.v("LoginActivity", "service category: " + servPointType);
                WorkingDataStore.setLoginRef(serviceProvider);
                workingData.putParcelable("servProv", serviceProvider);
                intent = new Intent(this, ServiceProviderHomeActivity.class);
                if (servPointType.equalsIgnoreCase(getString(R.string.medical_store)) ||
                        servPointType.equalsIgnoreCase(getString(R.string.medStoreOld))) {
                    intent = new Intent(this, MedicalStoreHomeActivity.class);
                }
                phone = serviceProvider.getSignInData().getPhone();
            }
            //Utility.setLastVisit(getSharedPreferences(type + "lastVisit" + phone, MODE_PRIVATE));

            /* Setting the auto complete list for mobile number **/
            if (phone != null) {
                SharedPreferences preferences = getSharedPreferences("autoComplete", MODE_PRIVATE);
                Set<String> list = preferences.getStringSet("autoCompleteValues", new HashSet<String>());
                Set<String> in = new HashSet<>(list);
                in.add(phone);
                preferences.edit().putStringSet("autoCompleteValues", in).apply();

                /* Setting the Last Visited of User. **/
                SharedPreferences prefs = getSharedPreferences(userType + "lastVisit" +
                        phone, MODE_PRIVATE);
                Utility.setLastVisit(prefs);
            }

            Utility.showMessage(this, R.string.msg_login_done);
            mPasswordView.setText("");
/*
            mMobileNumber.setText("");
            mPasswordView.setText("");
*/
            startActivity(intent);
        } else {
            Utility.showMessage(this, R.string.msg_login_failed);
            if (msgData.getInt("responseCode") == ErrorCode.ERROR_INVALID_USER_OR_PASSWD) {
                mMobileNumber.setError(getString(R.string.error_incorrect_password));
            }
            mMobileNumber.requestFocus();
            /* enable Sigh In button in 3 seconds */
            final Button signInButton = (Button) findViewById(R.id.email_sign_in_button);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    signInButton.setEnabled(true);
                }
            }, 3000);
        }
    }
}