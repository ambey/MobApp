package com.extenprise.mapp.customer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.extenprise.mapp.R;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.util.UIUtility;
import com.extenprise.mapp.util.Validator;


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
    private EditText mEditTextPinCode;
    private Spinner mSpinCity;
    private Spinner mSpinState;
    private ImageView mImgView;

    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_sign_up);
        getActionBar().setDisplayHomeAsUpEnabled(true);

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
        mEditTextPinCode = (EditText)findViewById(R.id.editTextZipCode);
        mSpinGender = (Spinner)findViewById(R.id.spinGender);
        mSpinCity = (Spinner)findViewById(R.id.editTextCity);
        mSpinState = (Spinner)findViewById(R.id.editTextState);
        mImgView = (ImageView) findViewById(R.id.uploadimageview);
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

    public void showPersonalFields(View view) {
        TextView dobLbl = (TextView) findViewById(R.id.textViewDOBLbl);
        TextView heightUnit = (TextView) findViewById(R.id.viewHeightUnit);
        TextView weightUnit = (TextView) findViewById(R.id.viewWeightUnit);

        if(mEditTextCustomerFName.getVisibility() == View.VISIBLE) {
            mEditTextCustomerFName.setVisibility(View.GONE);
            mEditTextCustomerLName.setVisibility(View.GONE);
            mEditTextCustomerEmail.setVisibility(View.GONE);
            dobLbl.setVisibility(View.GONE);
            heightUnit.setVisibility(View.GONE);
            weightUnit.setVisibility(View.GONE);
            mTextViewDOB.setVisibility(View.GONE);
            mSpinGender.setVisibility(View.GONE);
            mEditTextHeight.setVisibility(View.GONE);
            mEditTextWeight.setVisibility(View.GONE);
        } else {
            mEditTextCustomerFName.setVisibility(View.VISIBLE);
            mEditTextCustomerLName.setVisibility(View.VISIBLE);
            mEditTextCustomerEmail.setVisibility(View.VISIBLE);
            dobLbl.setVisibility(View.VISIBLE);
            heightUnit.setVisibility(View.VISIBLE);
            weightUnit.setVisibility(View.VISIBLE);
            mTextViewDOB.setVisibility(View.VISIBLE);
            mSpinGender.setVisibility(View.VISIBLE);
            mEditTextHeight.setVisibility(View.VISIBLE);
            mEditTextWeight.setVisibility(View.VISIBLE);
        }
    }

    public void showAddressFields(View view) {
        if(mEditTextLoc.getVisibility() == View.VISIBLE) {
            mEditTextLoc.setVisibility(View.GONE);
            mEditTextPinCode.setVisibility(View.GONE);
            mSpinState.setVisibility(View.GONE);
            mSpinCity.setVisibility(View.GONE);
        } else {
            mEditTextLoc.setVisibility(View.VISIBLE);
            mEditTextPinCode.setVisibility(View.VISIBLE);
            mSpinState.setVisibility(View.VISIBLE);
            mSpinCity.setVisibility(View.VISIBLE);
        }
    }

    public void showDatePicker(View view) {
        UIUtility.datePicker(view, mTextViewDOB);
    }

    public void showImageUploadOptions(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("");
        alertDialog.setMessage("Please Select option to upload image.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "From Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        startActivity(intent);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "From Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
                    }
                });
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                // Set the Image in ImageView after decoding the String
                mImgView.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }



    /*public void openGallery(int req_code){
        Intent intent = new Intent();
        intent.setType("image*//*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select file to upload "), req_code);
    }*/

    /*public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            if (requestCode == SELECT_FILE1) {
                selectedPath1 = getPath(selectedImageUri);
                System.out.println("selectedPath1 : " + selectedPath1);
            }
            if (requestCode == SELECT_FILE2) {
                selectedPath2 = getPath(selectedImageUri);
                System.out.println("selectedPath2 : " + selectedPath2);
            }
            tv.setText("Selected File paths : " + selectedPath1 + "," + selectedPath2);
        }
    }*/

    /*public String getPath(Uri uri) {

        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor cursor = managedQuery(uri, projection, null, null, null);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);

    }*/



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
        String pinCode = mEditTextPinCode.getText().toString().trim();

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
        if (TextUtils.isEmpty(pinCode)) {
            mEditTextPinCode.setError(getString(R.string.error_field_required));
            focusView = mEditTextPinCode;
            valid = false;
        } else if(Validator.isPinCodeValid(pinCode)) {
            mEditTextPinCode.setError("Invalid Pin Code.");
            focusView = mEditTextPinCode;
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
            values.put(MappContract.Customer.COLUMN_NAME_PIN_CODE, mEditTextPinCode.getText().toString().trim());
            values.put(MappContract.Customer.COLUMN_NAME_ID_CITY, mSpinCity.getSelectedItem().toString().trim());
            values.put(MappContract.Customer.COLUMN_NAME_ID_STATE, mSpinState.getSelectedItem().toString().trim());

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
