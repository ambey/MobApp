package com.extenprise.mapp.customer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.extenprise.mapp.R;
import com.extenprise.mapp.customer.data.Customer;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.util.EncryptUtil;
import com.extenprise.mapp.util.Utility;
import com.extenprise.mapp.util.Validator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PatientSignUpActivity extends Activity implements ResponseHandler {

    private ServiceResponseHandler mRespHandler = new ServiceResponseHandler(this);
    private int mServiceAction;

    private LinearLayout mContLay;
    private LinearLayout mAddrLayout;

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
    private static int REQUEST_CAMERA = 2;
    private String imgDecodableString;
    private Bitmap mImgCopy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_sign_up);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mContLay = (LinearLayout) findViewById(R.id.contLay);
        mAddrLayout = (LinearLayout) findViewById(R.id.addrLayout);

        mFormView = findViewById(R.id.scrollView);
        mProgressView = findViewById(R.id.progressView);
        mTextViewDOB = (TextView) findViewById(R.id.textViewDOB);
        mEditTextCustomerFName = (EditText)findViewById(R.id.editTextCustomerFName);
        mEditTextCustomerLName = (EditText) findViewById(R.id.editTextCustomerLName);
        mEditTextCellphone = (EditText)findViewById(R.id.editTextCellphone);
        mEditTextCellphone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if(!TextUtils.isEmpty(mEditTextCellphone.getText().toString().trim())) {
                        checkPhoneExistence();
                    }
                }
            }
        });
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

        if(mContLay.getVisibility() == View.VISIBLE) {
            mContLay.setVisibility(View.GONE);
        } else {
            mContLay.setVisibility(View.VISIBLE);
            if(mAddrLayout.getVisibility() == View.VISIBLE) {
                mAddrLayout.setVisibility(View.GONE);
            }
        }
        /*
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
        }*/
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

    public void showAddressFields(View view) {
        if(mAddrLayout.getVisibility() == View.VISIBLE) {
            mAddrLayout.setVisibility(View.GONE);
        } else {
            mAddrLayout.setVisibility(View.VISIBLE);
            if(mContLay.getVisibility() == View.VISIBLE) {
                mContLay.setVisibility(View.GONE);
            }
        }
        /*if(mEditTextLoc.getVisibility() == View.VISIBLE) {
            mEditTextLoc.setVisibility(View.GONE);
            mEditTextPinCode.setVisibility(View.GONE);
            mSpinState.setVisibility(View.GONE);
            mSpinCity.setVisibility(View.GONE);
        } else {
            mEditTextLoc.setVisibility(View.VISIBLE);
            mEditTextPinCode.setVisibility(View.VISIBLE);
            mSpinState.setVisibility(View.VISIBLE);
            mSpinCity.setVisibility(View.VISIBLE);
        }*/
    }

    public void showDatePicker(View view) {
        Utility.datePicker(view, mTextViewDOB);
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

/*

        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor cursor = managedQuery(uri, projection, null, null, null);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);

    }
*/



    public void registerPatient(View view) {
        if(!isValidInput()) {
            return;
        }
        Utility.showProgress(this, mFormView, mProgressView, true);
        Intent intent = new Intent(this, MappService.class);
        mServiceAction = MappService.DO_SIGNUP;
        bindService(intent, mConnection, BIND_AUTO_CREATE);
/*
        SaveCustomerData task = new SaveCustomerData(this);
        task.execute((Void) null);
*/
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        private Messenger mService;

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            mService = new Messenger(service);
            Bundle bundle = new Bundle();
            bundle.putInt("loginType", MappService.CUSTOMER_LOGIN);
            if(mServiceAction == MappService.DO_PHONE_EXIST_CHECK) {
                bundle.putParcelable("signInData", getSignUpData().getSignInData());
            } else {
                bundle.putParcelable("customer", getSignUpData());
            }
            Message msg = Message.obtain(null, mServiceAction);
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

    @Override
    public boolean gotResponse(int action, Bundle data) {
        try {
            unbindService(mConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(action == MappService.DO_SIGNUP) {
            signUpDone(data);
            return true;
        }
        if(action == MappService.DO_PHONE_EXIST_CHECK) {
            phoneCheckComplete(data);
            return true;
        }
        return false;
    }

    private void phoneCheckComplete(Bundle data) {
        Utility.showProgress(this, mFormView, mProgressView, false);
        if(!data.getBoolean("status")) {
            mEditTextCellphone.setError(getString(R.string.error_phone_registered));
            mEditTextCellphone.requestFocus();
        }
    }

    private void signUpDone(Bundle data) {
        if(data.getBoolean("status")) {
            Utility.showRegistrationAlert(this, "Thanks You..!", "You have successfully registered.\nLogin to your account.");
        }
        Utility.showProgress(this, mFormView, mProgressView, false);
    }

    private Customer getSignUpData() {
        Customer c = new Customer();
        c.getSignInData().setPhone(mEditTextCellphone.getText().toString().trim());
        if(mServiceAction == MappService.DO_PHONE_EXIST_CHECK) {
            return c;
        }
        c.setfName(mEditTextCustomerFName.getText().toString().trim());
        c.setlName(mEditTextCustomerLName.getText().toString().trim());
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        try {
            Date dob = sdf.parse(mTextViewDOB.getText().toString().trim());
            c.setDob(dob);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setAge(Utility.getAge(mTextViewDOB.getText().toString().trim()));
        c.setEmailId(mEditTextCustomerEmail.getText().toString().trim());
        c.setGender(mSpinGender.getSelectedItem().toString().trim());
        float height = 0;
        try {
            height = Float.parseFloat(mEditTextHeight.getText().toString().trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        c.setHeight(height);
        float weight = 0;
        try {
            weight = Float.parseFloat(mEditTextWeight.getText().toString().trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        c.setWeight(weight);
        c.getSignInData().setPasswd(EncryptUtil.encrypt(mEditTextPasswd.getText().toString()));
        c.setLocation(mEditTextLoc.getText().toString().trim());
        c.setPincode(mEditTextPinCode.getText().toString().trim());
        c.getCity().setCity(mSpinCity.getSelectedItem().toString());
        c.getCity().setState(mSpinState.getSelectedItem().toString());
        c.getCity().setCountry("India");

        return c;
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
/*
        if (TextUtils.isEmpty(height)) {
            mEditTextHeight.setError(getString(R.string.error_field_required));
            focusView = mEditTextHeight;
            valid = false;
        }
*/
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

/*
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
            values.put(MappContract.Customer.COLUMN_NAME_AGE, Utility.getAge(mTextViewDOB.getText().toString().trim()));
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
            */
/*if(!imgDecodableString.equals("") || imgDecodableString != null) {
                values.put(MappContract.Customer.COLUMN_NAME_IMAGE, Utility.getBytesFromBitmap(BitmapFactory.decodeFile(imgDecodableString)));
            }*//*

            values.put(MappContract.Customer.COLUMN_NAME_IMAGE, Utility.getBytesFromBitmap(((BitmapDrawable)mImgView.getDrawable()).getBitmap()));

            //Bitmap bitmap = ((BitmapDrawable)mImgView.getDrawable()).getBitmap();


            db.insert(MappContract.Customer.TABLE_NAME, null, values);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Utility.showRegistrationAlert(myActivity, "Thanks You..!", "You have successfully registered.\nLogin to your account.");
            Utility.showProgress(myActivity, mFormView, mProgressView, false);
            */
/*Intent intent = new Intent(myActivity, LoginActivity.class);
            startActivity(intent);*//*

        }

        @Override
        protected void onCancelled() {
            Utility.showProgress(myActivity, mFormView, mProgressView, false);
        }

    }
*/

    private void checkPhoneExistence() {
        Utility.showProgress(this, mFormView, mProgressView, true);
        mServiceAction = MappService.DO_PHONE_EXIST_CHECK;
        Intent intent = new Intent(this, MappService.class);
        bindService(intent, mConnection, FragmentActivity.BIND_AUTO_CREATE);
    }
}
