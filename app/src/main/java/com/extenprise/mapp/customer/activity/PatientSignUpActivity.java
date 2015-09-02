package com.extenprise.mapp.customer.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.service.activity.LoginActivity;
import com.extenprise.mapp.service.activity.SignUpPreviousActivity;
import com.extenprise.mapp.service.data.ServProvHasServHasServPt;
import com.extenprise.mapp.service.data.ServProvHasService;
import com.extenprise.mapp.service.data.ServicePoint;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.UIUtility;
import com.extenprise.mapp.util.Validator;

import java.util.ArrayList;


public class PatientSignUpActivity extends Activity {

    private View mFormView;
    private View mProgressView;
    private TextView mTextViewDOB;
    private EditText mEditTextCustomerFName;
    private EditText mEditTextCustomerLName;
    private EditText mEditTextCellphone;
    private EditText mEditTextCustomerEmail;
    private EditText mEditTextPasswd;
    private EditText mEditTextConPasswd;
    private Spinner mSpinGender;
    private EditText mEditTextHeight;
    private EditText mEditTextWeight;
    private EditText mEditTextLoc;
    private EditText mEditTextZipCode;
    private Spinner mEditTextCity;
    private Spinner mEditTextState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_sign_up);

        mFormView = findViewById(R.id.scrollView);
        mProgressView = findViewById(R.id.progressView);
        mTextViewDOB = (TextView) findViewById(R.id.textViewDOB);
        mEditTextCustomerFName = (EditText)findViewById(R.id.editTextCustomerFName);
        mEditTextCustomerLName = (EditText) findViewById(R.id.editTextCustomerLName);
        mEditTextCellphone = (EditText)findViewById(R.id.editTextCellphone);
        mEditTextCustomerEmail = (EditText)findViewById(R.id.editTextCustomerEmail);
        mEditTextPasswd = (EditText)findViewById(R.id.editTextPasswd);
        mEditTextConPasswd = (EditText)findViewById(R.id.editTextConPasswd);
        mEditTextHeight = (EditText)findViewById(R.id.editTextHeight);
        mEditTextWeight = (EditText)findViewById(R.id.editTextWeight);
        mEditTextLoc = (EditText)findViewById(R.id.editTextLoc);
        mEditTextZipCode = (EditText)findViewById(R.id.editTextZipCode);
        mSpinGender = (Spinner)findViewById(R.id.spinGender);
        mEditTextCity = (Spinner)findViewById(R.id.editTextCity);
        mEditTextState = (Spinner)findViewById(R.id.editTextState);

        mTextViewDOB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                UIUtility.datePicker(view, mTextViewDOB);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_patient_sign_up, menu);
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

    public void showDatePicker(View view) {
        UIUtility.datePicker(view, mTextViewDOB);
    }

    public void registerPatient(View view) {
        if(!isValidInput()) {
            return;
        }
        UIUtility.showProgress(this, mFormView, mProgressView, true);
        SaveCustomerData task = new SaveCustomerData(this);
        task.execute((Void) null);
    }

    private boolean isValidInput() {
        boolean valid = true;
        View focusView = null;

        String fName = mEditTextCustomerFName.getText().toString().trim();
        String lName = mEditTextCustomerLName.getText().toString().trim();
        String cellPhone = mEditTextCellphone.getText().toString().trim();
        String emailId = mEditTextCustomerEmail.getText().toString().trim();
        String passwd1 = mEditTextPasswd.getText().toString().trim();
        String passwd2 = mEditTextConPasswd.getText().toString().trim();
        String dob = mTextViewDOB.getText().toString().trim();
        String height = mEditTextHeight.getText().toString().trim();
        String weight = mEditTextWeight.getText().toString().trim();
        String loc = mEditTextLoc.getText().toString().trim();
        String zipCode = mEditTextZipCode.getText().toString().trim();

        if (TextUtils.isEmpty(fName)) {
            mEditTextCustomerFName.setError(getString(R.string.error_field_required));
            focusView = mEditTextCustomerFName;
            valid = false;
        }
        if (TextUtils.isEmpty(lName)) {
            mEditTextCustomerLName.setError(getString(R.string.error_field_required));
            focusView = mEditTextCustomerLName;
            valid = false;
        }

        if (TextUtils.isEmpty(cellPhone)) {
            mEditTextCellphone.setError(getString(R.string.error_field_required));
            focusView = mEditTextCellphone;
            valid = false;
        } else if (!Validator.isPhoneValid(cellPhone)) {
            mEditTextCellphone.setError(getString(R.string.error_invalid_phone));
            focusView = mEditTextCellphone;
            valid = false;
        } else if(isPhoneRegistered(cellPhone)) {
            mEditTextCellphone.setError(getString(R.string.error_phone_registered));
            focusView = mEditTextCellphone;
            valid = false;
        }

        if (!TextUtils.isEmpty(emailId) && !Validator.isEmailValid(emailId)) {
            mEditTextCustomerEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEditTextCustomerEmail;
            valid = false;
        }

        if (TextUtils.isEmpty(passwd1)) {
            mEditTextPasswd.setError(getString(R.string.error_field_required));
            focusView = mEditTextPasswd;
            valid = false;
        }
        if (TextUtils.isEmpty(passwd2)) {
            mEditTextConPasswd.setError(getString(R.string.error_field_required));
            focusView = mEditTextConPasswd;
            valid = false;
        } else if (!passwd1.equals(passwd2)) {
            mEditTextConPasswd.setError(getString(R.string.error_password_not_matching));
            focusView = mEditTextConPasswd;
            valid = false;
        }

        if (TextUtils.isEmpty(dob)) {
            mTextViewDOB.setError(getString(R.string.error_field_required));
            focusView = mTextViewDOB;
            valid = false;
        }
        if (TextUtils.isEmpty(height)) {
            mEditTextHeight.setError(getString(R.string.error_field_required));
            focusView = mEditTextHeight;
            valid = false;
        }
        if (TextUtils.isEmpty(weight)) {
            mEditTextWeight.setError(getString(R.string.error_field_required));
            focusView = mEditTextWeight;
            valid = false;
        }
        if (TextUtils.isEmpty(loc)) {
            mEditTextLoc.setError(getString(R.string.error_field_required));
            focusView = mEditTextLoc;
            valid = false;
        }
        if (TextUtils.isEmpty(zipCode)) {
            mEditTextZipCode.setError(getString(R.string.error_field_required));
            focusView = mEditTextZipCode;
            valid = false;
        }


        if (focusView != null) {
            focusView.requestFocus();
        }
        return valid;
    }

    private boolean isPhoneRegistered(String phone) {
        MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                MappContract.Customer.COLUMN_NAME_CELLPHONE
        };

        String selection = MappContract.Customer.COLUMN_NAME_CELLPHONE + "=?";

        String[] selectionArgs = {
                phone
        };
        Cursor c = db.query(MappContract.Customer.TABLE_NAME,
                projection, selection, selectionArgs, null, null, null);
        int count = c.getCount();
        c.close();

        return (count > 0);
    }

    class SaveCustomerData extends AsyncTask<Void, Void, Void> {

        private Activity myActivity;

        public SaveCustomerData(Activity activity) {
            myActivity = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(MappContract.Customer.COLUMN_NAME_FNAME, mEditTextCustomerFName.getText().toString().trim());
            values.put(MappContract.Customer.COLUMN_NAME_LNAME, mEditTextCustomerLName.getText().toString().trim());
            values.put(MappContract.Customer.COLUMN_NAME_DOB, mTextViewDOB.getText().toString().trim());
            values.put(MappContract.Customer.COLUMN_NAME_AGE, UIUtility.getAge(mTextViewDOB.getText().toString().trim()));
            values.put(MappContract.Customer.COLUMN_NAME_EMAIL_ID, mEditTextCustomerEmail.getText().toString().trim());
            values.put(MappContract.Customer.COLUMN_NAME_GENDER, mSpinGender.getSelectedItem().toString().trim());
            values.put(MappContract.Customer.COLUMN_NAME_WEIGHT, mEditTextWeight.getText().toString().trim());
            values.put(MappContract.Customer.COLUMN_NAME_HEIGHT, mEditTextHeight.getText().toString().trim());
            values.put(MappContract.Customer.COLUMN_NAME_CELLPHONE, mEditTextCellphone.getText().toString().trim());
            values.put(MappContract.Customer.COLUMN_NAME_PASSWD, mEditTextPasswd.getText().toString().trim());
            values.put(MappContract.Customer.COLUMN_NAME_LOCATION, mEditTextLoc.getText().toString().trim());
            values.put(MappContract.Customer.COLUMN_NAME_ZIPCODE, mEditTextZipCode.getText().toString().trim());
            values.put(MappContract.Customer.COLUMN_NAME_ID_CITY, mEditTextCity.getSelectedItem().toString().trim());
            values.put(MappContract.Customer.COLUMN_NAME_ID_STATE, mEditTextState.getSelectedItem().toString().trim());

            db.insert(MappContract.Customer.TABLE_NAME, null, values);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            UIUtility.showRegistrationAlert(myActivity, "Thanks You..!", "You have successfully registered.\nLogin to your account.");
            UIUtility.showProgress(myActivity, mFormView, mProgressView, false);
            /*Intent intent = new Intent(myActivity, LoginActivity.class);
            startActivity(intent);*/
        }

        @Override
        protected void onCancelled() {
            UIUtility.showProgress(myActivity, mFormView, mProgressView, false);
        }

    }
}
