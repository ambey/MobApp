package com.extenprise.mapp.medico.customer.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
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

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.activity.LoginActivity;
import com.extenprise.mapp.medico.customer.data.Customer;
import com.extenprise.mapp.medico.data.WorkingDataStore;
import com.extenprise.mapp.medico.net.MappService;
import com.extenprise.mapp.medico.net.MappServiceConnection;
import com.extenprise.mapp.medico.net.ResponseHandler;
import com.extenprise.mapp.medico.net.ServiceResponseHandler;
import com.extenprise.mapp.medico.util.BitmapToByteArrayTask;
import com.extenprise.mapp.medico.util.DateChangeListener;
import com.extenprise.mapp.medico.util.EncryptUtil;
import com.extenprise.mapp.medico.util.Utility;
import com.extenprise.mapp.medico.util.Validator;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class PatientSignUpActivity extends Activity implements ResponseHandler, DateChangeListener {

    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));

    private LinearLayout mContLay;
    private LinearLayout mAddrLayout;

    /*private View mFormView;
    private View mProgressView;*/
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
    /*private Spinner mSpinCity;*/
    private EditText mSpinCity;
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

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mContLay = (LinearLayout) findViewById(R.id.contLay);
        mAddrLayout = (LinearLayout) findViewById(R.id.addrLayout);
        findViewById(R.id.buttonEditPersonalInfo).setVisibility(View.GONE);
        findViewById(R.id.buttonEditAddr).setVisibility(View.GONE);

        /*mFormView = findViewById(R.id.scrollView);
        mProgressView = findViewById(R.id.progressView);*/
        mTextViewDOB = (TextView) findViewById(R.id.textViewDOB);
        mTextViewDOB.setHint(String.format("%s *", getString(R.string.dob)));
        mEditTextCustomerFName = (EditText) findViewById(R.id.editTextCustomerFName);
        mEditTextCustomerLName = (EditText) findViewById(R.id.editTextCustomerLName);
        mEditTextCellphone = (EditText) findViewById(R.id.editTextCellphone);
        mEditTextCellphone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String lastPhone = WorkingDataStore.getBundle().getString("lastPhone");
                if (!hasFocus) {
                    String ph = mEditTextCellphone.getText().toString().trim();
                    if (TextUtils.isEmpty(ph)) {
                        mEditTextCellphone.setError(getString(R.string.error_field_required));
                    } else if (!Validator.isPhoneValid(ph)) {
                        mEditTextCellphone.setError(getString(R.string.error_invalid_phone));
                    } else if (Validator.isPhoneValid(ph) && (lastPhone == null || !lastPhone.equals(ph))) {
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
        mSpinCity = (EditText) findViewById(R.id.editTextCity);
        mSpinState = (Spinner) findViewById(R.id.editTextState);
        mImgView = (ImageView) findViewById(R.id.uploadimageview);

        mEditTextPasswd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String pwd = mEditTextPasswd.getText().toString().trim();
                    if (!Validator.isPasswordValid(pwd) && !TextUtils.isEmpty(pwd)) {
                        mEditTextPasswd.setError(getString(R.string.error_pwd_length));
                    }
                }
            }
        });

        mEditTextConPasswd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String pwd = mEditTextConPasswd.getText().toString().trim();
                    if (!mEditTextPasswd.getText().toString().trim().equals(pwd) && !TextUtils.isEmpty(pwd)) {
                        mEditTextConPasswd.setError(getString(R.string.error_password_not_matching));
                    }
                }
            }
        });

        if (savedInstanceState != null) {
            Bitmap bitmap = savedInstanceState.getParcelable("image");
            mImgView.setImageBitmap(bitmap);
        } else {
            mImgCopy = (Bitmap) getLastNonConfigurationInstance();
            if (mImgCopy != null) {
                mImgView.setImageBitmap(mImgCopy);
            }
        }
        Utility.collapse(mContLay, true);
        Utility.collapse(mAddrLayout, true);
        WorkingDataStore.getBundle().remove("lastPhone");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
            case R.id.action_settings:
                return true;
            case R.id.action_search:
                Utility.startActivity(this, SearchServProvActivity.class);
                return true;
            case R.id.action_sign_in:
                Utility.startActivity(this, LoginActivity.class);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*public void showPersonalFields(View view) {
        if (mContLay.getVisibility() == View.VISIBLE) {
            Utility.collapse(mContLay, null);
        } else {
            if (mAddrLayout.getVisibility() == View.VISIBLE) {
                Utility.collapse(mAddrLayout, null);
            }
            Utility.expand(mContLay, null);
            mEditTextCustomerFName.requestFocus();
        }
    }*/

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

    public void showFields(View view) {
        int vID = view.getId();
        if (vID == R.id.textViewPersonalFields) {
            Utility.collapse(mAddrLayout, true);
            Utility.collapse(mContLay, (mContLay.getVisibility() == View.VISIBLE));
        } else if (vID == R.id.viewAddress) {
            Utility.collapse(mContLay, true);
            Utility.collapse(mAddrLayout, (mAddrLayout.getVisibility() == View.VISIBLE));
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
        //Utility.enlargeImage(mImgView);
    }

    public void showImageUploadOptions(View view) {
        String[] opts = new String[]{getString(R.string.take_photo),
                getString(R.string.from_gallery)};
        if (imageChanged) {
            opts = new String[]{getString(R.string.take_photo),
                    getString(R.string.from_gallery),
                    getString(R.string.remove)};
        }
        final Activity activity = this;
        final File destination = new File(Environment.getExternalStorageDirectory().getPath(), "photo.jpg");
        final Resources resources = getResources();
        Utility.showAlert(activity, activity.getString(R.string.profile_photo), null, null, false,
                opts, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Utility.startCamera(activity, resources.getInteger(R.integer.request_camera), destination);
                                break;
                            case 1:
                                Utility.pickPhotoFromGallery(activity, resources.getInteger(R.integer.request_gallery));
                                break;
                            case 2:
                                mImgView.setImageBitmap(null);
                                mImgView.setBackgroundResource(R.drawable.patient);
                                imageChanged = false;
                                break;
                        }

                    }
                });
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        try {
            Uri selectedImage = null;
            Resources resources = getResources();
            // When an Image is picked
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    Utility.showMessage(this, R.string.error_img_not_picked);
                    return;
                }
                mImgView.setBackgroundResource(0);
                if ((requestCode == resources.getInteger(R.integer.request_gallery) ||
                        requestCode == resources.getInteger(R.integer.request_edit))) {
                    // Get the Image from data
                    selectedImage = data.getData();
                    mImgView.setImageURI(selectedImage);
                    imageChanged = true;


                } else if (requestCode == resources.getInteger(R.integer.request_camera)) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    mImgView.setImageBitmap(bitmap);
                    selectedImage = Utility.getImageUri(this, bitmap);
                    imageChanged = true;
                } else {
                    Utility.showMessage(this, R.string.error_img_not_picked);
                }
            } else if (requestCode == resources.getInteger(R.integer.request_edit)) {
                imageChanged = true;
            }
            if (imageChanged) {
                if (requestCode != resources.getInteger(R.integer.request_edit)) {
                    Intent editIntent = new Intent(Intent.ACTION_EDIT);
                    editIntent.setDataAndType(selectedImage, "image*//*");
                    editIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(editIntent, resources.getInteger(R.integer.request_edit));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utility.showMessage(this, R.string.some_error);
        }
    }
*/


    /*public static Bitmap rotateImage(String path, Bitmap source) {
        float angle = -1;
        ExifInterface ei = null;
        int orientation = 0;
        try {
            ei = new ExifInterface(path);
            orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        } catch (IOException e) {
            e.printStackTrace();
        }

        switch(orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                angle = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                angle = 180;
                break;
        }

        if(angle != -1) {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            source = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        }
        return source;
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Utility.onPhotoActivityResult(this, mImgView, requestCode, resultCode, data)) {
            imageChanged = true;
        }
    }

    private void setErrorsNull() {
        mEditTextPinCode.setError(null);
        mSpinCity.setError(null);
        mEditTextLoc.setError(null);
        mEditTextWeight.setError(null);
        mEditTextCustomerLName.setError(null);
        mEditTextCustomerFName.setError(null);
        mEditTextConPasswd.setError(null);
        mEditTextPasswd.setError(null);
        mEditTextCellphone.setError(null);
        mEditTextCustomerEmail.setError(null);
        mTextViewDOB.setError(null);
        Utility.setSpinError(mSpinState, null);
        Utility.setSpinError(mSpinGender, null);
    }

/*
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            clearFocus();
            return false;
        }
    }
*/

    public void registerPatient(View view) {
        setErrorsNull();
        boolean valid = true;
        View focusView = null;
        int errMsg = R.string.error_select_state;
        int v = R.string.address;

        if (mSpinState.getSelectedItem().toString().equals(getString(R.string.state_lbl))) {
            Utility.setSpinError(mSpinState, getString(R.string.error_select_state));
            focusView = mSpinState;
            valid = false;
        }

        String valTxt = mEditTextPinCode.getText().toString().trim();
        if (TextUtils.isEmpty(valTxt)) {
            mEditTextPinCode.setError(getString(R.string.error_field_required));
            focusView = mEditTextPinCode;
            valid = false;
            errMsg = -1;
        } else if (Validator.isPinCodeValid(valTxt)) {
            mEditTextPinCode.setError(getString(R.string.error_invalid_pincode));
            focusView = mEditTextPinCode;
            valid = false;
            errMsg = -1;
        }

        int msgValid = Utility.isNameValid(mSpinCity.getText().toString().trim());
        if (msgValid != -1) {
            mSpinCity.setError(getString(msgValid));
            focusView = mSpinCity;
            valid = false;
            errMsg = -1;
        }

        if (TextUtils.isEmpty(mEditTextLoc.getText().toString().trim())) {
            mEditTextLoc.setError(getString(R.string.error_field_required));
            focusView = mEditTextLoc;
            valid = false;
            errMsg = -1;
        }

        msgValid = Validator.isWeightValid(mEditTextWeight.getText().toString().trim());
        if (msgValid != -1) {
            mEditTextWeight.setError(getString(msgValid));
            focusView = mEditTextWeight;
            valid = false;
            v = R.string.personalDetails;
            errMsg = -1;
        }

        if (mSpinGender.getSelectedItem().toString().equals(getString(R.string.gender_lbl))) {
            Utility.setSpinError(mSpinGender, getString(R.string.error_select_gender));
            errMsg = R.string.error_select_gender;
            v = R.string.personalDetails;
            focusView = mEditTextHeight;
            valid = false;
        }

        valTxt = mTextViewDOB.getText().toString().trim();
        if (TextUtils.isEmpty(valTxt)) {
            mTextViewDOB.setError(getString(R.string.error_field_required));
            focusView = mTextViewDOB;
            valid = false;
            v = R.string.personalDetails;
            errMsg = -1;
        } else if (Utility.getAge(Utility.getStrAsDate(valTxt, "dd/MM/yyyy")) < 0) {
            mTextViewDOB.setError(getString(R.string.error_future_date));
            focusView = mTextViewDOB;
            valid = false;
            v = R.string.personalDetails;
            errMsg = -1;
        }

        valTxt = mEditTextCustomerEmail.getText().toString().trim();
        if (!TextUtils.isEmpty(valTxt) && !Validator.isValidEmaillId(valTxt)) {
            mEditTextCustomerEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEditTextCustomerEmail;
            valid = false;
            v = R.string.personalDetails;
            errMsg = -1;
        }

        if (!Utility.isNameValid(this, mEditTextCustomerFName, mEditTextCustomerLName)) {
            focusView = null;
            valid = false;
            v = R.string.personalDetails;
            errMsg = -1;
        }

        valTxt = mEditTextPasswd.getText().toString().trim();
        if (!Validator.isPasswordValid(valTxt) && !TextUtils.isEmpty(valTxt)) {
            mEditTextPasswd.setError(getString(R.string.error_pwd_length));
            focusView = mEditTextPasswd;
            valid = false;
            v = -1;
            errMsg = -1;
        }
        if (!valTxt.equals(mEditTextConPasswd.getText().toString().trim())) {
            mEditTextConPasswd.setError(getString(R.string.error_password_not_matching));
            focusView = mEditTextConPasswd;
            valid = false;
            v = -1;
            errMsg = -1;
        }

        valTxt = mEditTextCellphone.getText().toString().trim();
        if (!Validator.isPhoneValid(valTxt) && !TextUtils.isEmpty(valTxt)) {
            mEditTextCellphone.setError(getString(R.string.error_invalid_phone));
            mEditTextCellphone.requestFocus();
            focusView = mEditTextCellphone;
            valid = false;
            v = -1;
            errMsg = -1;
        }

        if (Utility.areEditFieldsEmpty(this, new EditText[]{mEditTextConPasswd,
                mEditTextPasswd, mEditTextCellphone})) {
            valid = false;
            focusView = null;
            v = -1;
            errMsg = -1;
        }

        if (!valid) {
            if (v == R.string.personalDetails) {
                Utility.collapse(mAddrLayout, true);
                Utility.collapse(mContLay, false);
            } else if (v == R.string.address) {
                Utility.collapse(mContLay, true);
                Utility.collapse(mAddrLayout, false);
            }
            if (focusView != null) {
                focusView.requestFocus();
            }
            if (errMsg != -1) {
                Utility.showMessage(this, errMsg);
            }
            return;
        }

        sendRequest(MappService.DO_SIGNUP);
    }

    private void sendRequest(int action) {
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
            //Utility.showProgress(PatientSignUpActivity.this, mFormView, mProgressView, true);
            Utility.showProgressDialog(this, true);
        }
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        if (action == MappService.DO_SIGNUP) {
            signUpDone(data);
        } else if (action == MappService.DO_PHONE_EXIST_CHECK) {
            phoneCheckComplete(data);
        }
        return data.getBoolean("status");
    }

    private void phoneCheckComplete(Bundle data) {
        //Utility.showProgress(this, mFormView, mProgressView, false);
        Utility.showProgressDialog(this, false);
        if (data.getBoolean("status")) {
            mEditTextCellphone.setError(null);
            WorkingDataStore.getBundle().putString("lastPhone", mEditTextCellphone.getText().toString().trim());
        } else {
            mEditTextCellphone.requestFocus();
        }
    }

    private void signUpDone(Bundle data) {
        if (data.getBoolean("status")) {
            WorkingDataStore.getBundle().putParcelable("customer", data.getParcelable("customer"));
            Utility.showAlert(this, "", getString(R.string.msg_registration_done), null, false, null, getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Utility.startActivity(PatientSignUpActivity.this, PatientsHomeScreenActivity.class);
                        }
                    }, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            dialog.dismiss();
                            Utility.startActivity(PatientSignUpActivity.this, PatientsHomeScreenActivity.class);
                        }
                    }
            );
            //Utility.showAlert(this, "Thanks You..!", "You have successfully registered.\nLogin to your account.");
        }
        //Utility.showProgress(this, mFormView, mProgressView, false);
        Utility.showProgressDialog(this, false);
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
        c.getCity().setCity(mSpinCity.getText().toString().trim());
        c.getCity().setState(mSpinState.getSelectedItem().toString());
        c.getCity().setCountry("India");
        if (imageChanged) {
            BitmapToByteArrayTask task = new BitmapToByteArrayTask(c, ((BitmapDrawable) mImgView.getDrawable()).getBitmap());
            task.execute();
        } else {
            c.setPhoto(null);
        }

        //As customer is going to home page after sign up successful.
        Calendar calendar = Calendar.getInstance();
        c.setLastVisit(String.format("%s %s", Utility.getDateAsStr(calendar.getTime(), "yyyy-MM-dd"),
                Utility.getFormattedTime(calendar)));
        return c;
    }

    @Override
    public void datePicked(String date) {
        mTextViewDOB.setError(null);
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
        mConnection.setBound(false);
        //startActivity(getIntent());
        super.onBackPressed();
    }
}
