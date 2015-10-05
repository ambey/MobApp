package com.extenprise.mapp.customer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.extenprise.mapp.R;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.util.UIUtility;
import com.extenprise.mapp.util.Validator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class PatientProfile extends Activity {

    private RadioGroup mGender;
    private RadioButton mBtnMale, mBtnFemale;

    private View mFormView;
    private View mProgressView;

    private TextView mPname, mTextViewDOB;
    private EditText mEditTextCustomerFName;
    private EditText mEditTextCustomerLName;
    private EditText mEditTextCustomerEmail;
    private EditText mEditTextHeight;
    private EditText mEditTextWeight;
    private EditText mEditTextLoc;
    private EditText mEditTextPinCode;
    private Spinner mSpinCity;
    private Spinner mSpinState;
    private ImageView mImgView;

    private static int RESULT_LOAD_IMG = 1;
    private static int REQUEST_CAMERA = 2;
    private String imgDecodableString;
    private Bitmap mImgCopy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mFormView = findViewById(R.id.scrollView);
        mProgressView = findViewById(R.id.progressView);
        mTextViewDOB = (TextView) findViewById(R.id.textViewDOB);
        mEditTextCustomerFName = (EditText)findViewById(R.id.editTextCustomerFName);
        mEditTextCustomerLName = (EditText) findViewById(R.id.editTextCustomerLName);

        mEditTextCustomerEmail = (EditText)findViewById(R.id.editTextCustomerEmail);

        mEditTextHeight = (EditText)findViewById(R.id.editTextHeight);
        mEditTextWeight = (EditText)findViewById(R.id.editTextWeight);
        mEditTextLoc = (EditText)findViewById(R.id.editTextLoc);
        mEditTextPinCode = (EditText)findViewById(R.id.editTextZipCode);

        mSpinCity = (Spinner)findViewById(R.id.editTextCity);
        mSpinState = (Spinner)findViewById(R.id.editTextState);
        mImgView = (ImageView) findViewById(R.id.uploadimageview);

        if (savedInstanceState != null) {
            Bitmap bitmap = savedInstanceState.getParcelable("image");
            mImgView.setImageBitmap(bitmap);
        } else {
            mImgCopy = (Bitmap) getLastNonConfigurationInstance();
            if (mImgCopy != null) {
                mImgView.setImageBitmap(mImgCopy);
            }
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        BitmapDrawable drawable = (BitmapDrawable) mImgView.getDrawable();
        if (drawable != null) {
            Bitmap bitmap = drawable.getBitmap();
            outState.putParcelable("image", bitmap);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return mImgCopy;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_patient_profile, menu);
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

    public void showImageUploadOptions(View view) {

        final CharSequence[] items = { "Take Photo", "Choose from Gallery", "Cancel" };
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Upload Image ");
        dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File f = new File(android.os.Environment
                                .getExternalStorageDirectory(), "temp.jpg");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                        startActivityForResult(intent, REQUEST_CAMERA);
                        //startImageCapture();
                        break;

                    case 1:
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
                        break;

                    case 2:
                        dialog.dismiss();
                        break;
                }
            }

        });
        dialogBuilder.create().show();
    }

    /*public void startImageCapture() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo = new File(Environment.getExternalStorageDirectory(), "rxCopy.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        mRxUri = Uri.fromFile(photo);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 2);
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (resultCode == RESULT_OK) {
                if (requestCode == RESULT_LOAD_IMG
                        && null != data) {
                    // Get the Image from data

                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

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

                } else if (requestCode == REQUEST_CAMERA) {
                    File f = new File(Environment.getExternalStorageDirectory()
                            .toString());
                    for (File temp : f.listFiles()) {
                        if (temp.getName().equals("temp.jpg")) {
                            f = temp;
                            break;
                        }
                    }
                    try {
                        Bitmap bm;
                        BitmapFactory.Options btmapOptions = new BitmapFactory.Options();

                        bm = BitmapFactory.decodeFile(f.getAbsolutePath(),
                                btmapOptions);

                        // bm = Bitmap.createScaledBitmap(bm, 70, 70, true);
                        mImgView.setImageBitmap(bm);

                        String path = android.os.Environment
                                .getExternalStorageDirectory()
                                + File.separator
                                + "Phoenix" + File.separator + "default";
                        f.delete();
                        OutputStream fOut = null;
                        File file = new File(path, String.valueOf(System
                                .currentTimeMillis()) + ".jpg");
                        try {
                            fOut = new FileOutputStream(file);
                            bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                            fOut.flush();
                            fOut.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "You haven't picked Image",
                            Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    public String getPath(Uri uri) {

        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor cursor = managedQuery(uri, projection, null, null, null);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);

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

        String emailId = mEditTextCustomerEmail.getText().toString().trim();

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


        if (!TextUtils.isEmpty(emailId) && !Validator.isEmailValid(emailId)) {
            mEditTextCustomerEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEditTextCustomerEmail;
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

            values.put(MappContract.Customer.COLUMN_NAME_WEIGHT, mEditTextWeight.getText().toString().trim());
            values.put(MappContract.Customer.COLUMN_NAME_HEIGHT, mEditTextHeight.getText().toString().trim());

            values.put(MappContract.Customer.COLUMN_NAME_LOCATION, mEditTextLoc.getText().toString().trim());
            values.put(MappContract.Customer.COLUMN_NAME_PIN_CODE, mEditTextPinCode.getText().toString().trim());
            values.put(MappContract.Customer.COLUMN_NAME_ID_CITY, mSpinCity.getSelectedItem().toString().trim());
            values.put(MappContract.Customer.COLUMN_NAME_ID_STATE, mSpinState.getSelectedItem().toString().trim());
            /*if(!imgDecodableString.equals("") || imgDecodableString != null) {
                values.put(MappContract.Customer.COLUMN_NAME_IMAGE, UIUtility.getBytesFromBitmap(BitmapFactory.decodeFile(imgDecodableString)));
            }*/
            values.put(MappContract.Customer.COLUMN_NAME_IMAGE, UIUtility.getBytesFromBitmap(((BitmapDrawable)mImgView.getDrawable()).getBitmap()));

            //Bitmap bitmap = ((BitmapDrawable)mImgView.getDrawable()).getBitmap();


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