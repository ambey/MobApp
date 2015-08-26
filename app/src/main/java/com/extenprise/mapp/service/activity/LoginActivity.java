package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.UIUtility;
import com.extenprise.mapp.util.Validator;

import java.util.ArrayList;
import java.util.List;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mMobileNumber;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private CheckBox mSaveLoginCheckBox;

    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

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

        mSaveLoginCheckBox = (CheckBox)findViewById(R.id.rememberMe);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            mMobileNumber.setText(loginPreferences.getString("username", ""));
            mPasswordView.setText(loginPreferences.getString("password", ""));
            mSaveLoginCheckBox.setChecked(true);
        }

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
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
                Intent intent = new Intent(this, SearchDoctorActivity.class);
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

    public void registerServProv() {
        mMobileNumber.setError(null);
        mPasswordView.setError(null);
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mMobileNumber.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String mobile = mMobileNumber.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        //boolean passwd = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        /*if (!TextUtils.isEmpty(password)) {
            passwd = true;
            if (!Validator.isPasswordValid(password)) {
            if (!Validator.isPasswordValid(password)) {
                mPasswordView.setError(getString(R.string.error_invalid_password));
                focusView = mPasswordView;
                cancel = true;
            }
        }*/

        // Check for a valid email address.
        if (TextUtils.isEmpty(mobile)) {
            mMobileNumber.setError(getString(R.string.error_field_required));
            focusView = mMobileNumber;
            cancel = true;
        } else if (!Validator.isEmailValid(mobile)) {
            mMobileNumber.setError(getString(R.string.error_invalid_phone));
            focusView = mMobileNumber;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } /*else if (!passwd) {
            *//* check if the email ID is already registered *//*

            showProgress(true);
            mAuthTask = new UserLoginTask(this, email, null);
            mAuthTask.execute((Void) null);

            //Intent intent = new Intent(this, SignUpActivity.class);
            //startActivity(intent);
        }*/ else {

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mMobileNumber.getWindowToken(), 0);

            if (mSaveLoginCheckBox.isChecked()) {
                loginPrefsEditor.putBoolean("saveLogin", true);
                loginPrefsEditor.putString("username", mobile);
                loginPrefsEditor.putString("password", password);
                loginPrefsEditor.commit();
            } else {
                loginPrefsEditor.clear();
                loginPrefsEditor.commit();
            }
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            UIUtility.showProgress(this, mLoginFormView, mProgressView, true);
            mAuthTask = new UserLoginTask(this, mobile, password);
            mAuthTask.execute((Void) null);
        }
    }
    class SetupEmailAutoCompleteTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            ArrayList<String> emailAddressCollection = new ArrayList<>();

            // Get all emails from the user's contacts and copy them to a list.
            ContentResolver cr = getContentResolver();
            Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    null, null, null);
            while (emailCur.moveToNext()) {
                String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract
                        .CommonDataKinds.Email.DATA));
                emailAddressCollection.add(email);
            }
            emailCur.close();

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

    private boolean isLoginValid(String mobile, String passwd) {
        MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                MappContract.ServiceProvider._ID,
                MappContract.ServiceProvider.COLUMN_NAME_FNAME,
                MappContract.ServiceProvider.COLUMN_NAME_LNAME,
                MappContract.ServiceProvider.COLUMN_NAME_EMAIL_ID
        };

        String selection = MappContract.ServiceProvider.COLUMN_NAME_EMAIL_ID + "=? and " +
                MappContract.ServiceProvider.COLUMN_NAME_PASSWD + "=?";

        String[] selectionArgs = {
                mobile,
                passwd
        };
        Cursor c = db.query(MappContract.ServiceProvider.TABLE_NAME,
                projection, selection, selectionArgs, null, null, null);

        int count = c.getCount();
        if(count > 0) {
            c.moveToFirst();
            ServiceProvider sp = new ServiceProvider();
            sp.setIdServiceProvider(Integer.parseInt(c.getString(0)));
            sp.setfName(c.getString(1));
            sp.setlName(c.getString(2));
            sp.setEmailId(c.getString(3));

            LoginHolder.servLoginRef = sp;
        }
        c.close();

        return (count > 0);
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final Activity mActivity;
        private final String mEmail;
        private final String mPassword;

        UserLoginTask(Activity activity, String email, String password) {
            mActivity = activity;
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            /*if (mPassword == null) {
                return isEmailIdRegistered(mEmail);
            }*/
            return isLoginValid(mEmail, mPassword);

            // attempt authentication against a network service.
/*
            HttpURLConnection connection = null;
            try {
                String urlParams = "email=" + URLEncoder.encode(mEmail, "UTF-8") +
                        "&passwd=" + URLEncoder.encode(mPassword, "UTF-8");

                URL url = new URL(getResources().getString(R.string.login_url));
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setChunkedStreamingMode(0);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length",
                        Integer.toString(urlParams.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");

                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                //Send request
                DataOutputStream wr = new DataOutputStream(
                        connection.getOutputStream());
                wr.writeBytes(urlParams);
                wr.flush();
                wr.close();

                //Get Response
                int responseCode = connection.getResponseCode();
                if (responseCode != -1) {
                    InputStream is = connection.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    String line = null;
                    StringBuffer responseBuf = new StringBuffer();
                    while ((line = rd.readLine()) != null) {
                        responseBuf.append(line);
                    }
                    rd.close();

                    Gson gson = new Gson();
                    Response response = gson.fromJson(responseBuf.toString(), Response.class);
                    if (response.getStatus() == 0) {
                        if (response.getType().equals("Customer")) {

                        } else if (response.getType().equals("ServiceProvider")) {
                            ServiceProvider sp = gson.fromJson(responseBuf.toString(), ServiceProvider.class);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }
*/
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            UIUtility.showProgress(mActivity, mLoginFormView, mProgressView, false);

            if (success) {
                /*if (mPassword == null) {
                    mPasswordView.setError(getString(R.string.error_email_exists));
                    mPasswordView.requestFocus();
                    return;
                }*/
                //finish();
                Intent intent = new Intent(mActivity, ServiceProviderHomeActivity.class);
                startActivity(intent);
            } else {
                /*if(mPassword == null) {
                    Intent intent = new Intent(mActivity, SignUpActivity.class);
                    startActivity(intent);
                    return;
                }*/
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            UIUtility.showProgress(mActivity, mLoginFormView, mProgressView, false);
        }
    }
}

