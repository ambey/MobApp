package com.extenprise.mapp.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.customer.activity.PatientsHomeScreenActivity;
import com.extenprise.mapp.customer.data.Customer;
import com.extenprise.mapp.data.SignInData;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.MappServiceConnection;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.activity.MedicalStoreHomeActivity;
import com.extenprise.mapp.service.activity.SearchServProvActivity;
import com.extenprise.mapp.service.activity.ServiceProviderHomeActivity;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.EncryptUtil;
import com.extenprise.mapp.util.Utility;
import com.extenprise.mapp.util.Validator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements ResponseHandler {

    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));
    private int mLoginType;
    private SignInData mSignInData;


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    // UI references.
    private AutoCompleteTextView mMobileNumber;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private CheckBox mSaveLoginCheckBox;
    private RadioGroup mRadioGroupUType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //final View welcomeView = findViewById(R.id.welcome);
        mLoginFormView = findViewById(R.id.login_form);
        //mLoginFormView.setEnabled(false);
        Animation rLayoutAnim = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.img_fade);
        rLayoutAnim.setDuration(3000);
        mLoginFormView.startAnimation(rLayoutAnim);


       /* welcomeView.post(new Runnable() {
            @Override
            public void run() {
                Animation rWelcomeAnim = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.text_fade);
                rWelcomeAnim.setDuration(4500);
                rWelcomeAnim.setFillEnabled(true);
                welcomeView.startAnimation(rWelcomeAnim);
            }
        });
        mLoginFormView.post(new Runnable() {
            @Override
            public void run() {
                Animation rLayoutAnim = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.img_fade);
                rLayoutAnim.setDuration(3500);
                mLoginFormView.startAnimation(rLayoutAnim);
            }
        });
        welcomeView.postDelayed(new Runnable() {
            @Override
            public void run() {
                welcomeView.setVisibility(View.GONE);
                mLoginFormView.setEnabled(true);
            }
        }, 3500);*/

        mProgressView = findViewById(R.id.login_progress);

        mSignInData = new SignInData();
        mRadioGroupUType = (RadioGroup) findViewById(R.id.radioGroupUserType);

        // Set up the login form.
        mMobileNumber = (AutoCompleteTextView) findViewById(R.id.mobileNumber);
        populateAutoComplete();

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

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
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
        /*SharedPreferences loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);

        Boolean saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin) {
            if (loginPreferences.getString("passwd", null) != null) {
                mMobileNumber.setText(loginPreferences.getString("username", ""));
                mPasswordView.setText(loginPreferences.getString("passwd", ""));
                mSaveLoginCheckBox.setChecked(true);
                mSignInData.setPhone(loginPreferences.getString("username", ""));
                mSignInData.setPasswd(loginPreferences.getString("passwd", ""));
                mLoginType = findLoginType(loginPreferences.getString("logintype", ""));

                processLogin();
            }

            *//*mMobileNumber.setText(loginPreferences.getString("username", ""));
            if(loginPreferences.getString("passwd", "") != null) {
                mPasswordView.setText(loginPreferences.getString("passwd", ""));
                mLoginType = loginPreferences.getInt("logintype", 0);
            }
            mSaveLoginCheckBox.setChecked(true);*//*
        }*/


    }

    public void onBackPressed()
    {
        this.finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

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
                Intent intent = new Intent(this, SearchServProvActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_sign_in:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateAutoComplete() {
        if (VERSION.SDK_INT >= 8) {
            // Use AccountManager (API 8+)
            new SetupEmailAutoCompleteTask().execute(null, null);
        }
    }

/*
    public void registerServProv() {
        mMobileNumber.setError(null);
        mPasswordView.setError(null);
        Intent intent = new Intent(this, ServProvSignUpActivity.class);
        startActivity(intent);
    }
*/


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

    private int findLoginType(String loginType) {
        if (loginType.equalsIgnoreCase(getString(R.string.patient))) {
            return MappService.CUSTOMER_LOGIN;
        } else if (loginType.equalsIgnoreCase(getString(R.string.serv_prov))) {
            return MappService.SERVICE_LOGIN;
        }
        return -1;
    }

    public void attemptLogin() {
        int uTypeID = mRadioGroupUType.getCheckedRadioButtonId();
        RadioButton mRadioButtonUType;
        if (uTypeID == -1) {
            Utility.showMessage(this, R.string.error_user_type_required);
            //Utility.showAlert(this, "", "Please Select user type.");
            return;
        } else {
            mRadioButtonUType = (RadioButton) findViewById(uTypeID);
        }

        // Reset errors.
        mMobileNumber.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String uType = mRadioButtonUType.getText().toString().trim();
        mLoginType = findLoginType(uType);
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

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mMobileNumber.getWindowToken(), 0);

            mSignInData.setPhone(phone);
            mSignInData.setPasswd(EncryptUtil.encrypt(passwd));

            SharedPreferences loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            SharedPreferences.Editor loginPrefsEditor = loginPreferences.edit();

            loginPrefsEditor.putString("passwd", String.valueOf(Calendar.getInstance().getTime()));
            if (mSaveLoginCheckBox.isChecked()) {
                loginPrefsEditor.putBoolean("saveLogin", true);
                loginPrefsEditor.putString("username", mSignInData.getPhone());
                loginPrefsEditor.putString("passwd", mSignInData.getPasswd());
                loginPrefsEditor.putString("logintype", uType);
                loginPrefsEditor.apply();
            } else {
                loginPrefsEditor.clear();
                loginPrefsEditor.apply();
            }
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            processLogin();
/*
            mAuthTask = new UserLoginTask(this, mobile, password);
            mAuthTask.execute((Void) null);
*/
        }
    }

    private void processLogin() {
        Bundle bundle = new Bundle();
        bundle.putInt("loginType", mLoginType);
        bundle.putParcelable("signInData", mSignInData);
        mConnection.setAction(MappService.DO_LOGIN);
        mConnection.setData(bundle);
        if (Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE)) {
            Utility.showProgress(this, mLoginFormView, mProgressView, true);
        }
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        if (action == MappService.DO_LOGIN) {
            loginDone(data);
            return true;
        }
        return false;
    }

    class SetupEmailAutoCompleteTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            ArrayList<String> emailAddressCollection = new ArrayList<>();

            // Get all emails from the user's contacts and copy them to a list.
            ContentResolver cr = getContentResolver();
            Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    null, null, null);
            if (emailCur != null) {
                while (emailCur.moveToNext()) {
                    String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract
                            .CommonDataKinds.Email.DATA));
                    emailAddressCollection.add(email);
                }
                emailCur.close();
            }
            return emailAddressCollection;
        }

        @Override
        protected void onPostExecute(List<String> emailAddressCollection) {
            addEmailsToAutoComplete(emailAddressCollection);
        }
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
        mMobileNumber.setAdapter(adapter);
    }

    protected void loginDone(Bundle msgData) {
        Utility.showProgress(this, mLoginFormView, mProgressView, false);
        boolean success = msgData.getBoolean("status");
        if (success) {
            String phone = "", type = "";
            Intent intent;
            if (mLoginType == MappService.CUSTOMER_LOGIN) {
                Customer customer = msgData.getParcelable("customer");
                String targetActivity = getIntent().getStringExtra("target-activity");
                if (targetActivity != null) {
                    try {
                        intent = new Intent(this, Class.forName(targetActivity));
                        intent.putExtra("servProv", getIntent().getParcelableExtra("servProv"));
                        intent.putParcelableArrayListExtra("servProvList", getIntent().getParcelableArrayListExtra("servProvList"));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        return;
                    }
                } else {
                    intent = new Intent(this, PatientsHomeScreenActivity.class);
                }
                intent.putExtra("customer", customer);
                LoginHolder.custLoginRef = customer;
                phone = customer.getSignInData().getPhone();
                type = "customer";
            } else {
                ServiceProvider serviceProvider = msgData.getParcelable("service");
                assert serviceProvider != null;
                String servPointType = serviceProvider.getServProvHasServPt(0).getServPointType();
                Log.v("LoginActivity", "service category: " + servPointType);
                LoginHolder.servLoginRef = serviceProvider;
                intent = new Intent(this, ServiceProviderHomeActivity.class);
                if (servPointType.equalsIgnoreCase(getString(R.string.medical_store))) {
                    intent = new Intent(this, MedicalStoreHomeActivity.class);
                }
                phone = serviceProvider.getSignInData().getPhone();
                type = "servprov";
            }
            Utility.setLastVisit(this, phone, type);
            startActivity(intent);
        } else {
            mPasswordView.setError(getString(R.string.error_incorrect_password));
            mPasswordView.requestFocus();
        }
    }
}

