package com.extenprise.mapp.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.customer.activity.PatientsHomeScreenActivity;
import com.extenprise.mapp.customer.activity.SearchServProvActivity;
import com.extenprise.mapp.customer.data.Customer;
import com.extenprise.mapp.data.SignInData;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.MappServiceConnection;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.activity.MedicalStoreHomeActivity;
import com.extenprise.mapp.service.activity.ServiceProviderHomeActivity;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.EncryptUtil;
import com.extenprise.mapp.util.Utility;
import com.extenprise.mapp.util.Validator;

import java.util.ArrayList;
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
    private EditText mMobileNumber;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private CheckBox mSaveLoginCheckBox;
    private RadioGroup mRadioGroupUType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
        }

        mLoginFormView = findViewById(R.id.login_form);
        //mLoginFormView.setEnabled(false);
        Animation rLayoutAnim = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.img_fade);
        rLayoutAnim.setDuration(3000);
        mLoginFormView.startAnimation(rLayoutAnim);

        mProgressView = findViewById(R.id.login_progress);

        mSignInData = new SignInData();
        mRadioGroupUType = (RadioGroup) findViewById(R.id.radioGroupUserType);

        // Set up the login form.
        mMobileNumber = (EditText) findViewById(R.id.mobileNumber);
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

        Button emailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        emailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mLoginFormView = emailSignInButton;

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
    }

    private void initialize() {
        SharedPreferences loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        Boolean saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin) {
            String userType = loginPreferences.getString("logintype", null);
            if (userType != null) {
                int utype = findLoginType(userType);
                if (utype == MappService.CUSTOMER_LOGIN) {
                    mRadioGroupUType.check(R.id.radioButtonPatient);
                } else {
                    mRadioGroupUType.check(R.id.radioButtonMedServiceProvider);
                }
                mMobileNumber.setText(loginPreferences.getString("username", ""));
            }
        } else {
            mPasswordView.setText("");
            mMobileNumber.setText("");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = null;
        if (LoginHolder.custLoginRef != null) {
            intent = new Intent(this, PatientsHomeScreenActivity.class);
            startActivity(intent);
        } else if (LoginHolder.servLoginRef != null) {
            ServiceProvider sp = LoginHolder.servLoginRef;
            String spType = sp.getServProvHasServPt(0).getServPointType();
            if (spType.equalsIgnoreCase(getString(R.string.medical_store))) {
                intent = new Intent(this, MedicalStoreHomeActivity.class);
            } else {
                intent = new Intent(this, ServiceProviderHomeActivity.class);
            }
        }
        if (intent != null) {
            startActivity(intent);
        } else {
            initialize();
        }
    }

    public void onBackPressed() {
        mConnection.setBound(false);
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

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
            /* hide the soft keyboard and show the message */
            Utility.hideSoftKeyboard(this);
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
        final String uType = mRadioButtonUType.getText().toString().trim();
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
            Utility.hideSoftKeyboard(this);

            mSignInData.setPhone(phone);
            mSignInData.setPasswd(EncryptUtil.encrypt(passwd));

            SharedPreferences loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            final SharedPreferences.Editor loginPrefsEditor = loginPreferences.edit();
            boolean saveLogin = loginPreferences.getBoolean("saveLogin", false);
            if (!saveLogin && mSaveLoginCheckBox.isChecked()) {
                final AlertDialog dialog = Utility.customDialogBuilder(this, null, R.string.confirm_remember).create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loginPrefsEditor.putBoolean("saveLogin", true);
                        loginPrefsEditor.putString("username", mSignInData.getPhone());
                        loginPrefsEditor.putString("passwd", mSignInData.getPasswd());
                        loginPrefsEditor.putString("logintype", uType);
                        loginPrefsEditor.apply();
                        dialog.dismiss();
                        doLogin();
                    }
                });
                return;
            } else if (!mSaveLoginCheckBox.isChecked()) {
                loginPrefsEditor.clear();
                loginPrefsEditor.apply();
            }
            doLogin();
        }
    }

    private void doLogin() {
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

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
        //mMobileNumber.setAdapter(adapter);
    }

    protected void loginDone(Bundle msgData) {
        Utility.showProgress(this, mLoginFormView, mProgressView, false);
        boolean success = msgData.getBoolean("status");
        if (success) {
            String phone, type;
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
                assert customer != null;
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
                intent.putExtra("servprov", serviceProvider);
                phone = serviceProvider.getSignInData().getPhone();
                type = "servprov";
            }
            Utility.setLastVisit(getSharedPreferences(type + "lastVisit" + phone, MODE_PRIVATE));
            Utility.showMessage(this, R.string.msg_login_done);
            startActivity(intent);
        } else {
            Utility.showMessage(this, R.string.msg_login_failed);
            mPasswordView.setError(getString(R.string.error_incorrect_password));
            mPasswordView.requestFocus();
        }
    }

    class SetupEmailAutoCompleteTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            ArrayList<String> emailAddressCollection = new ArrayList<>();

            // Get all emails from the user's contacts and copy them to a list.
            ContentResolver cr = getContentResolver();
            Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    null, null, null);
            if (emailCur != null) {
                while (emailCur.moveToNext()) {
                    String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract
                            .CommonDataKinds.Phone.DATA));
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
}