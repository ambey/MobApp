package com.extenprise.mapp.customer.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.activity.LoginActivity;
import com.extenprise.mapp.customer.data.Customer;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.MappServiceConnection;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.util.DateChangeListener;
import com.extenprise.mapp.util.EncryptUtil;
import com.extenprise.mapp.util.Utility;
import com.extenprise.mapp.util.Validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class PatientSignUpActivity extends Activity implements ResponseHandler, DateChangeListener {

    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));

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

    private Bitmap mImgCopy;
    private boolean imageChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_sign_up);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mContLay = (LinearLayout) findViewById(R.id.contLay);
        mAddrLayout = (LinearLayout) findViewById(R.id.addrLayout);

        mFormView = findViewById(R.id.scrollView);
        mProgressView = findViewById(R.id.progressView);
        mTextViewDOB = (TextView) findViewById(R.id.textViewDOB);
        mEditTextCustomerFName = (EditText) findViewById(R.id.editTextCustomerFName);
        mEditTextCustomerLName = (EditText) findViewById(R.id.editTextCustomerLName);
        mEditTextCellphone = (EditText) findViewById(R.id.editTextCellphone);
        mEditTextCellphone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (Validator.isPhoneValid(mEditTextCellphone.getText().toString().trim())) {
                        sendRequest(MappService.DO_PHONE_EXIST_CHECK);
                    }
                }
            }
        });
        mEditTextCustomerEmail = (EditText) findViewById(R.id.editTextCustomerEmail);
        mEditTextPasswd = (EditText) findViewById(R.id.editTextPasswd);
        mEditTextConPasswd = (EditText) findViewById(R.id.editTextConPasswd);
        mEditTextHeight = (EditText) findViewById(R.id.editTextHeight);
        mEditTextWeight = (EditText) findViewById(R.id.editTextWeight);
        mEditTextLoc = (EditText) findViewById(R.id.editTextLoc);
        mEditTextPinCode = (EditText) findViewById(R.id.editTextZipCode);
        mSpinGender = (Spinner) findViewById(R.id.spinGender);
        mSpinCity = (Spinner) findViewById(R.id.editTextCity);
        mSpinState = (Spinner) findViewById(R.id.editTextState);
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
        if (mContLay.getVisibility() == View.VISIBLE) {
            Utility.collapse(mContLay, null);
        } else {
            Utility.expand(mContLay, null);
            if (mAddrLayout.getVisibility() == View.VISIBLE) {
                Utility.collapse(mAddrLayout, null);
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

    public void showAddressFields(View view) {
        if (mAddrLayout.getVisibility() == View.VISIBLE) {
            Utility.collapse(mAddrLayout, null);
        } else {
            Utility.expand(mAddrLayout, null);
            if (mContLay.getVisibility() == View.VISIBLE) {
                Utility.collapse(mContLay, null);
            }
        }
    }

    public void showDatePicker(View view) {
        /*DatePickerDialog dpd = Utility.datePicker(view, mTextViewDOB);
        dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
        dpd.show();*/
        long currentTime = Calendar.getInstance().getTimeInMillis();
        Utility.datePicker(view, mTextViewDOB, this, currentTime, currentTime, -1);
        //Utility.datePicker(view, mTextViewDOB);
    }

    public void enlargeImg(View view) {
        Utility.enlargeImage(mImgView);
    }

    public void showImageUploadOptions(View view) {
        final Activity activity = this;
        Utility.showAlert(activity, activity.getString(R.string.take_photo), null, false,
                Utility.imgOpts(activity), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Utility.startCamera(activity, R.integer.request_camera);
                                break;
                            case 1:
                                Utility.pickPhotoFromGallery(activity, R.integer.request_gallery);
                                break;
                            case 2:
                                mImgView.setImageBitmap(null);
                                break;
                        }

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        try {
            Uri selectedImage = null;
            // When an Image is picked
            if (resultCode == RESULT_OK) {
                if ((requestCode == R.integer.request_gallery ||
                        requestCode == R.integer.request_edit)
                        && null != data) {
                    // Get the Image from data
                    selectedImage = data.getData();
                    mImgView.setImageURI(selectedImage);
                    imageChanged = true;

                } else if (requestCode == R.integer.request_camera) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    mImgView.setImageBitmap(bitmap);
                    selectedImage = Utility.getImageUri(this, bitmap);
                    imageChanged = true;
                } else {
                    Utility.showMessage(this, R.string.error_img_not_picked);
                }
            } else if (requestCode == R.integer.request_edit) {
                imageChanged = true;
            }
            if (imageChanged) {
                if (requestCode != R.integer.request_edit) {
                    Intent editIntent = new Intent(Intent.ACTION_EDIT);
                    editIntent.setDataAndType(selectedImage, "image/*");
                    editIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(editIntent, R.integer.request_edit);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utility.showMessage(this, R.string.some_error);
        }
    }

    public void registerPatient(View view) {
        boolean valid = true;
        View focusView = null;

        EditText[] fields = {mEditTextCellphone, mEditTextPasswd, mEditTextConPasswd, mEditTextCustomerFName,
                mEditTextCustomerLName, mEditTextWeight, mEditTextLoc, mEditTextPinCode};
        if (Utility.areEditFieldsEmpty(this, fields)) {
            valid = false;
        }

        if (!Validator.isOnlyAlpha(mEditTextCustomerFName.getText().toString().trim())) {
            mEditTextCustomerFName.setError(getString(R.string.error_only_alpha));
            focusView = mEditTextCustomerFName;
            valid = false;
        }
        if (!Validator.isOnlyAlpha(mEditTextCustomerLName.getText().toString().trim())) {
            mEditTextCustomerFName.setError(getString(R.string.error_only_alpha));
            focusView = mEditTextCustomerLName;
            valid = false;
        }
        String emailId = mEditTextCustomerEmail.getText().toString().trim();
        if (!TextUtils.isEmpty(emailId) && !Validator.isValidEmaillId(emailId)) {
            mEditTextCustomerEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEditTextCustomerEmail;
            valid = false;
        }
        if (!mEditTextPasswd.getText().toString().trim().equals(mEditTextConPasswd.getText().toString().trim())) {
            mEditTextConPasswd.setError(getString(R.string.error_password_not_matching));
            focusView = mEditTextConPasswd;
            valid = false;
        }
        String dob = mTextViewDOB.getText().toString().trim();
        if (TextUtils.isEmpty(dob)) {
            mTextViewDOB.setError(getString(R.string.error_field_required));
            focusView = mTextViewDOB;
            valid = false;
        } else if (Utility.getAge(Utility.getStrAsDate(dob, "dd/MM/yyyy")) < 0) {
            mTextViewDOB.setError(getString(R.string.error_future_date));
            focusView = mTextViewDOB;
            valid = false;
        }
        double value = 0.0;
        try {
            value = Double.parseDouble(mEditTextWeight.getText().toString().trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (value <= 0.0) {
            mEditTextWeight.setError(getString(R.string.error_invalid_weight));
            focusView = mEditTextWeight;
            valid = false;
        }
        if (Validator.isPinCodeValid(mEditTextPinCode.getText().toString().trim())) {
            mEditTextPinCode.setError(getString(R.string.error_invalid_pincode));
            focusView = mEditTextPinCode;
            valid = false;
        }
        if (!valid) {
            if (focusView != null) {
                focusView.requestFocus();
            }
            return;
        }
        if (imageChanged) {
            Utility.confirm(this, R.string.msg_without_img, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_NEGATIVE) {
                        Utility.showMessage(PatientSignUpActivity.this, R.string.msg_set_img);
                        return;
                    }
                    dialog.dismiss();
                }
            });
        }

        sendRequest(MappService.DO_SIGNUP);
    }

    private void sendRequest(int action) {

        /*Object obj = null;
        if (action == MappService.DO_SIGNUP) {
            obj = getSignUpData(action);
        } else if (action == MappService.DO_PHONE_EXIST_CHECK) {
            obj = getSignUpData(action).getSignInData();
        }

        if(Utility.sendRequest(this, MappService.CUSTOMER_LOGIN, action, obj, mConnection)) {
            Utility.showProgress(PatientSignUpActivity.this, mFormView, mProgressView, true);
        }*/

        Bundle bundle = new Bundle();
        bundle.putInt("loginType", MappService.CUSTOMER_LOGIN);
        if (action == MappService.DO_SIGNUP) {
            bundle.putParcelable("customer", getSignUpData(MappService.DO_SIGNUP));
        } else if (action == MappService.DO_PHONE_EXIST_CHECK) {
            bundle.putParcelable("signInData", getSignUpData(action).getSignInData());
        }
        mConnection.setData(bundle);
        mConnection.setAction(action);
        if (Utility.doServiceAction(PatientSignUpActivity.this, mConnection, BIND_AUTO_CREATE)) {
            Utility.showProgress(PatientSignUpActivity.this, mFormView, mProgressView, true);
        }
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        if (action == MappService.DO_SIGNUP) {
            signUpDone(data);
            return true;
        }
        if (action == MappService.DO_PHONE_EXIST_CHECK) {
            phoneCheckComplete(data);
            return true;
        }
        return false;
    }

    private void phoneCheckComplete(Bundle data) {
        Utility.showProgress(this, mFormView, mProgressView, false);
        if (!data.getBoolean("status")) {
            mEditTextCellphone.setError(getString(R.string.error_phone_registered));
            mEditTextCellphone.requestFocus();
        }
    }

    private void signUpDone(Bundle data) {
        if (data.getBoolean("status")) {
            Utility.showAlert(this, "", getString(R.string.msg_registration_done), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent intent = new Intent(PatientSignUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
            //Utility.showAlert(this, "Thanks You..!", "You have successfully registered.\nLogin to your account.");
        } else {
            Utility.showMessage(this, R.string.some_error);
        }
        Utility.showProgress(this, mFormView, mProgressView, false);
    }

    private Customer getSignUpData(int action) {
        Customer c = new Customer();
        c.getSignInData().setPhone(mEditTextCellphone.getText().toString().trim());
        if (action == MappService.DO_PHONE_EXIST_CHECK) {
            return c;
        }
        c.setfName(mEditTextCustomerFName.getText().toString().trim());
        c.setlName(mEditTextCustomerLName.getText().toString().trim());
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        try {
            Date dob = sdf.parse(mTextViewDOB.getText().toString().trim());
            c.setDob(dob);
            c.setAge(Utility.getAge(dob));
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
        c.setPhoto(Utility.getBytesFromBitmap(((BitmapDrawable) mImgView.getDrawable()).getBitmap()));

        return c;
    }

    @Override
    public void datePicked(String date) {
        //int age = Utility.getAge(Utility.getStrAsDate(date, "dd/MM/yyyy"));
    }


    /**
     * Defines callbacks for service binding, passed to bindService()
     */
/*
    private ServiceConnection mConnection = new ServiceConnection() {
        private Messenger mService;

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            mService = new Messenger(service);
            Bundle bundle = new Bundle();
            bundle.putInt("loginType", MappService.CUSTOMER_LOGIN);
            if (mServiceAction == MappService.DO_PHONE_EXIST_CHECK) {
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
*/
    @Override
    public void onBackPressed() {
        if (mConnection.isConnected()) {
            unbindService(mConnection);
        }
        //startActivity(getIntent());
        super.onBackPressed();
    }
}
