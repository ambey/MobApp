package com.extenprise.mapp.medico.customer.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.activity.LoginActivity;
import com.extenprise.mapp.medico.customer.data.Customer;
import com.extenprise.mapp.medico.data.HasPhoto;
import com.extenprise.mapp.medico.data.WorkingDataStore;
import com.extenprise.mapp.medico.net.MappService;
import com.extenprise.mapp.medico.net.MappServiceConnection;
import com.extenprise.mapp.medico.net.ResponseHandler;
import com.extenprise.mapp.medico.net.ServiceResponseHandler;
import com.extenprise.mapp.medico.util.BitmapToByteArrayTask;
import com.extenprise.mapp.medico.util.ByteArrayToBitmapTask;
import com.extenprise.mapp.medico.util.DateChangeListener;
import com.extenprise.mapp.medico.util.EncryptUtil;
import com.extenprise.mapp.medico.util.PhotoTaskCompleteListener;
import com.extenprise.mapp.medico.util.Utility;
import com.extenprise.mapp.medico.util.Validator;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class PatientProfileActivity extends FragmentActivity implements ResponseHandler, DateChangeListener, PhotoTaskCompleteListener {

    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));

    private LinearLayout mContLay;
    private LinearLayout mAddrLayout;
    private LinearLayout mSelectPharmacyLayout;
    /*private View mFormView;
    private View mProgressView;*/

    private TextView mPname, mTextViewDOB, mMobNo;
    private EditText mEditTextCustomerFName;
    private EditText mEditTextCustomerLName;
    private EditText mEditTextCustomerEmail;
    private EditText mEditTextHeight;
    private EditText mEditTextWeight;
    private EditText mEditTextLoc;
    private EditText mEditTextPinCode;
    private EditText mSpinCity;
    private Spinner mSpinState;
    private Spinner mSpinGender;
    private ImageView mImgView;
    private Button mUpdateButton;

    private EditText mOldPwd;
    private boolean isPwdCorrect;
    private Customer mCust;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //mCust = WorkingDataStore.getBundle().getParcelable("customer");
        mCust = (Customer) WorkingDataStore.getLoginRef();

        mContLay = (LinearLayout) findViewById(R.id.contLay);
        mAddrLayout = (LinearLayout) findViewById(R.id.addrLayout);
        mSelectPharmacyLayout = (LinearLayout) findViewById(R.id.selectPharmacyLayout);

        mPname = (TextView) findViewById(R.id.textviewPname);
        mMobNo = (TextView) findViewById(R.id.mobnumValue);
        mImgView = (ImageView) findViewById(R.id.imageViewPatient);
        mTextViewDOB = (TextView) findViewById(R.id.textViewDOB);
        mEditTextCustomerFName = (EditText) findViewById(R.id.editTextCustomerFName);
        mEditTextCustomerLName = (EditText) findViewById(R.id.editTextCustomerLName);
        mEditTextCustomerEmail = (EditText) findViewById(R.id.editTextCustomerEmail);
        mEditTextHeight = (EditText) findViewById(R.id.editTextHeight);
        mEditTextWeight = (EditText) findViewById(R.id.editTextWeight);
        mEditTextLoc = (EditText) findViewById(R.id.editTextLoc);
        mEditTextPinCode = (EditText) findViewById(R.id.editTextZipCode);
        mSpinCity = (EditText) findViewById(R.id.editTextCity);
        mSpinState = (Spinner) findViewById(R.id.editTextState);
        mSpinGender = (Spinner) findViewById(R.id.spinGender);
        mUpdateButton = (Button) findViewById(R.id.buttonViewUpdate);

        mPname.setText(String.format("%s %s\n(%d years)", mCust.getfName(), mCust.getlName(),
                Utility.getAge(mCust.getDob())));
        mMobNo.setText(mCust.getSignInData().getPhone());
        mEditTextCustomerFName.setText(mCust.getfName());
        mEditTextCustomerLName.setText(mCust.getlName());
        mEditTextCustomerEmail.setText(mCust.getEmailId());
        mTextViewDOB.setText(Utility.getDateAsStr(mCust.getDob(), "dd/MM/yyyy"));
        mSpinGender.setSelection(Utility.getSpinnerIndex(mSpinGender, mCust.getGender()));
        mEditTextHeight.setText(String.format("%.1f", mCust.getHeight()));
        mEditTextWeight.setText(String.format("%.1f", mCust.getWeight()));
        mEditTextLoc.setText(mCust.getLocation());
        mEditTextPinCode.setText(mCust.getPincode());
        mSpinCity.setText(mCust.getCity().getCity());
        mSpinState.setSelection(Utility.getSpinnerIndex(mSpinState, mCust.getCity().getState()));

        setPersonalInfoEditable(false);
        setAddressEditable(false);
        Utility.setEnabledButton(this, mUpdateButton, false);
        Utility.collapse(mContLay, true);
        Utility.collapse(mAddrLayout, true);
        Utility.collapse(mSelectPharmacyLayout, true);
        setPhoto();

        if (savedInstanceState != null) {
            Bitmap bitmap = savedInstanceState.getParcelable("image");
            mImgView.setImageBitmap(bitmap);
        } else {
            Bitmap mImgCopy = (Bitmap) getLastNonConfigurationInstance();
            if (mImgCopy != null) {
                mImgView.setImageBitmap(mImgCopy);
            }
        }
    }

    public void showFields(View view) {
        int vID = view.getId();
        if (vID == R.id.textViewPersonalFields) {
            Utility.collapse(mAddrLayout, true);
            Utility.collapse(mSelectPharmacyLayout, true);
            Utility.collapse(mContLay, (mContLay.getVisibility() == View.VISIBLE));

        } else if (vID == R.id.viewAddress) {
            Utility.collapse(mContLay, true);
            Utility.collapse(mSelectPharmacyLayout, true);
            Utility.collapse(mAddrLayout, (mAddrLayout.getVisibility() == View.VISIBLE));

        } else if (vID == R.id.textViewPrefMedStore) {
            Utility.collapse(mContLay, true);
            Utility.collapse(mAddrLayout, true);
            Utility.collapse(mSelectPharmacyLayout, (mSelectPharmacyLayout.getVisibility() == View.VISIBLE));
        }
    }

    private void setAddressEditable(boolean editable) {
        mEditTextPinCode.setEnabled(editable);
        mEditTextLoc.setEnabled(editable);
        mSpinCity.setEnabled(editable);
        mSpinState.setEnabled(editable);
    }

    private void setPersonalInfoEditable(boolean editable) {
        mEditTextCustomerFName.setEnabled(editable);
        mEditTextCustomerLName.setEnabled(editable);
        mEditTextCustomerEmail.setEnabled(editable);
        mEditTextWeight.setEnabled(editable);
        mEditTextHeight.setEnabled(editable);
        mTextViewDOB.setEnabled(editable);
        mSpinGender.setEnabled(editable);
    }

    public void editPersonalInfo(View view) {
        setPersonalInfoEditable(true);
        Utility.setEnabledButton(this, mUpdateButton, true);
    }

    public void editAddress(View view) {
        setAddressEditable(true);
        Utility.setEnabledButton(this, mUpdateButton, true);
    }

    private void setErrorsNull() {
        mEditTextPinCode.setError(null);
        mSpinCity.setError(null);
        mEditTextLoc.setError(null);
        mEditTextWeight.setError(null);
        mEditTextCustomerLName.setError(null);
        mEditTextCustomerFName.setError(null);
        mEditTextCustomerEmail.setError(null);
        mTextViewDOB.setError(null);
        Utility.setSpinError(mSpinState, null);
        Utility.setSpinError(mSpinGender, null);
    }

    public void updateProfile(View view) {
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

        if (!valid) {
            if (v == R.string.personalDetails) {
                Utility.collapse(mAddrLayout, true);
                Utility.collapse(mContLay, false);
            } else {
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

        if (mCust.getfName().equalsIgnoreCase(mEditTextCustomerFName.getText().toString().trim()) &&
                mCust.getlName().equalsIgnoreCase(mEditTextCustomerLName.getText().toString().trim()) &&
                mCust.getEmailId().equalsIgnoreCase(mEditTextCustomerEmail.getText().toString().trim()) &&
                mCust.getGender().equalsIgnoreCase(mSpinGender.getSelectedItem().toString()) &&
                mCust.getLocation().equalsIgnoreCase(mEditTextLoc.getText().toString().trim()) &&
                mCust.getCity().getCity().equalsIgnoreCase(mSpinCity.getText().toString().trim()) &&
                mCust.getPincode().equalsIgnoreCase(mEditTextPinCode.getText().toString().trim()) &&
                mCust.getCity().getState().equalsIgnoreCase(mSpinState.getSelectedItem().toString()) &&
                mCust.getHeight() == Float.parseFloat(mEditTextHeight.getText().toString().trim()) &&
                mCust.getWeight() == Float.parseFloat(mEditTextWeight.getText().toString().trim()) &&
                Utility.getDateAsStr(mCust.getDob(), "dd/MM/yyyy").equals(mTextViewDOB.getText().toString())) {

            Utility.showMessage(this, R.string.msg_no_change_found);
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putInt("loginType", MappService.CUSTOMER_LOGIN);
        bundle.putParcelable("customer", setupUpdateData(new Customer(), false));
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_UPDATE);
        if (Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE)) {
            Utility.showProgressDialog(this, true);
        }
    }

    private void changePwdDone(Bundle data) {
        if (data.getBoolean("status")) {
            Utility.showMessage(this, R.string.msg_change_pwd);
        } else {
            Utility.hideSoftKeyboard(this);
        }
    }

    private void pwdCheckDone(Bundle data) {
        if (data.getBoolean("status")) {
            isPwdCorrect = true;
        } else {
            isPwdCorrect = false;
            Utility.hideSoftKeyboard(this);
            mOldPwd.setError(getString(R.string.error_wrong_pwd));
            mOldPwd.requestFocus();
        }
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        Utility.showProgressDialog(this, false);
        switch (action) {
            case MappService.DO_UPDATE:
                updateDone(data);
                break;
            case MappService.DO_REMOVE_PHOTO:
                removePhotoDone(data);
                break;
            case MappService.DO_PWD_CHECK:
                pwdCheckDone(data);
                break;
            case MappService.DO_CHANGE_PWD:
                changePwdDone(data);
                break;
            case MappService.DO_UPLOAD_PHOTO:
                uploadPhotoDone(data);
                break;
        }
        return data.getBoolean("status");
    }

    private void uploadPhotoDone(Bundle data) {
        if (data.getBoolean("status")) {
            Utility.showMessage(this, R.string.msg_upload_photo);
            //mCust = WorkingDataStore.getBundle().getParcelable("customer");
            mCust.setPhoto(Utility.getBytesFromBitmap(((BitmapDrawable) mImgView.getDrawable()).getBitmap()));
            BitmapToByteArrayTask task = new BitmapToByteArrayTask(mCust, ((BitmapDrawable) mImgView.getDrawable()).getBitmap());
            task.execute();
        } else {
            setPhoto();
        }
    }

    private void setPhoto() {
        if (mCust.getPhoto() != null) {
            ByteArrayToBitmapTask task = new ByteArrayToBitmapTask(mImgView, mCust.getPhoto(),
                    mImgView.getLayoutParams().width, mImgView.getLayoutParams().height);
            task.execute();
        } else {
            mImgView.setImageResource(R.drawable.patient);
        }
    }

    private void removePhotoDone(Bundle data) {
        if (data.getBoolean("status")) {
            Utility.showMessage(this, R.string.msg_photo_removed);
            //mCust = WorkingDataStore.getBundle().getParcelable("customer");
            if (mCust != null) {
                mCust.setPhoto(null);
            }
        }
        setPhoto();
    }

    private void updateDone(Bundle data) {
        if (data.getBoolean("status")) {
            Utility.showAlert(this, "", getString(R.string.msg_profile_updated), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            //Customer customer = WorkingDataStore.getBundle().getParcelable("customer");
            Customer cust = (Customer) WorkingDataStore.getLoginRef();
            setupUpdateData(cust, false);
            mPname.setText(String.format("%s %s\n(%d years)", cust.getfName(), cust.getlName(),
                    Utility.getAge(cust.getDob())));

            setPersonalInfoEditable(false);
            setAddressEditable(false);
            Utility.setEnabledButton(this, mUpdateButton, false);
        }
    }

    private Customer setupUpdateData(Customer c, boolean readPhoto) {
        c.getSignInData().setPhone(mMobNo.getText().toString());
        c.setfName(mEditTextCustomerFName.getText().toString().trim());
        c.setlName(mEditTextCustomerLName.getText().toString().trim());
        if (readPhoto && mImgView.getDrawable() != null) {
            BitmapToByteArrayTask task = new BitmapToByteArrayTask(c,
                    ((BitmapDrawable) mImgView.getDrawable()).getBitmap(), MappService.DO_UPLOAD_PHOTO, this);
            task.execute();
        }
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        try {
            Date dob = sdf.parse(mTextViewDOB.getText().toString());
            c.setDob(dob);
            c.setAge(Utility.getAge(dob));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setEmailId(mEditTextCustomerEmail.getText().toString().trim());
        c.setGender(mSpinGender.getSelectedItem().toString());
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
        c.setLocation(mEditTextLoc.getText().toString().trim());
        c.setPincode(mEditTextPinCode.getText().toString().trim());
        c.getCity().setCity(mSpinCity.getText().toString().trim());
        c.getCity().setState(mSpinState.getSelectedItem().toString());
        c.getCity().setCountry(getString(R.string.india));

        return c;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_screen, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.action_settings:
                return true;
            case R.id.action_search:
                return true;
            case R.id.action_sign_in:
                return true;
            case R.id.logout:
                Utility.logout(getSharedPreferences("loginPrefs", MODE_PRIVATE), this, LoginActivity.class);
                return true;
            case R.id.action_changepwd:
                LayoutInflater inflater = this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.layout_change_pwd, null);

                mOldPwd = (EditText) dialogView.findViewById(R.id.editTextOldPasswd);
                final EditText newPwd = (EditText) dialogView.findViewById(R.id.editTextNewPasswd);
                final EditText confPwd = (EditText) dialogView.findViewById(R.id.editTextCnfPasswd);

                mOldPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            checkPwd();
                        }
                    }
                });

                final AlertDialog dialog = Utility.customDialogBuilder(this, dialogView, R.string.changepwd).create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).
                        setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean cancel = false;
                                if (!isPwdCorrect) {
                                    checkPwd();
                                    Utility.showMessage(PatientProfileActivity.this, R.string.msg_verify_pwd);
                                    cancel = true;
                                }
                                EditText[] fields = {newPwd, confPwd};
                                if (Utility.areEditFieldsEmpty(PatientProfileActivity.this, fields)) {
                                    cancel = true;
                                }

                                View focusView = null;
                                String newpwd = newPwd.getText().toString().trim();
                                if (!Validator.isPasswordValid(newpwd)) {
                                    newPwd.setError(getString(R.string.error_pwd_length));
                                    focusView = newPwd;
                                    cancel = true;
                                }
                                String confpwd = confPwd.getText().toString().trim();
                                if (!confpwd.equals(newpwd)) {
                                    confPwd.setError(getString(R.string.error_password_not_matching));
                                    focusView = confPwd;
                                    cancel = true;
                                }

                                if (cancel) {
                                    if (focusView != null) {
                                        focusView.requestFocus();
                                    }
                                } else {
                                    Customer cust = new Customer();
                                    cust.getSignInData().setPhone(mCust.getSignInData().getPhone());
                                    cust.getSignInData().setPasswd(EncryptUtil.encrypt(newpwd));
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("loginType", MappService.CUSTOMER_LOGIN);
                                    bundle.putParcelable("customer", cust);
                                    mConnection.setData(bundle);
                                    mConnection.setAction(MappService.DO_CHANGE_PWD);
                                    if (Utility.doServiceAction(PatientProfileActivity.this, mConnection, BIND_AUTO_CREATE)) {
                                        //Utility.showProgress(PatientProfileActivity.this, mFormView, mProgressView, true);
                                        Utility.showProgressDialog(PatientProfileActivity.this, true);
                                    }
                                    dialog.dismiss();
                                }
                            }
                        });
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkPwd() {
        String oldpwd = mOldPwd.getText().toString().trim();
        if (!Validator.isPasswordValid(oldpwd)) {
            mOldPwd.setError(getString(R.string.error_wrong_pwd));
            return;
        }
        Customer cust = new Customer();
        cust.getSignInData().setPhone(mCust.getSignInData().getPhone());
        cust.getSignInData().setPasswd(EncryptUtil.encrypt(oldpwd));
        Bundle bundle = new Bundle();
        bundle.putInt("loginType", MappService.CUSTOMER_LOGIN);
        bundle.putParcelable("customer", cust);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_PWD_CHECK);
        if (Utility.doServiceAction(PatientProfileActivity.this, mConnection, BIND_AUTO_CREATE)) {
            Utility.showProgressDialog(this, true);
        }
    }

    public void showDatePicker(View view) {
        /*DatePickerDialog dpd = Utility.datePicker(view, mTextViewDOB);
        dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
        dpd.show();*/
        //Utility.datePicker(view, mTextViewDOB);
        long currentTime = Calendar.getInstance().getTimeInMillis();
        Utility.datePicker(view, mTextViewDOB, this, currentTime, currentTime, -1);
    }

    public void enlargeImg(View view) {
        Utility.enlargeImage(mImgView);
    }

    public void changeImg(View view) {
        String[] opts = new String[]{getString(R.string.take_photo),
                getString(R.string.from_gallery)};
        if (mCust.getPhoto() != null) {
            opts = new String[]{getString(R.string.take_photo),
                    getString(R.string.from_gallery),
                    getString(R.string.remove)};
        }
        final Activity activity = this;
        final File destination = new File(Environment.getExternalStorageDirectory().getPath(), "photo.jpg");
        Utility.showAlert(activity, activity.getString(R.string.profile_photo), null, null, false,
                opts, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Utility.startCamera(activity, getResources().getInteger(R.integer.request_camera), destination);
                                break;
                            case 1:
                                Utility.pickPhotoFromGallery(activity, getResources().getInteger(R.integer.request_gallery));
                                break;
                            case 2:
                                //if(mImgView.getDrawable() != null || mImgView.getDrawable() == getDrawable(R.drawable.patient))
                                Utility.showAlert(activity, activity.getString(R.string.remove),
                                        getString(R.string.confirm_remove_photo), null, true,
                                        null,
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                if (which == DialogInterface.BUTTON_POSITIVE) {
                                                    Bundle bundle = new Bundle();
                                                    bundle.putInt("loginType", MappService.CUSTOMER_LOGIN);
                                                    //Customer customer = WorkingDataStore.getBundle().getParcelable("customer");
                                                    Customer cust = new Customer();
                                                    cust.getSignInData().setPhone(mCust.getSignInData().getPhone());
                                                    bundle.putParcelable("customer", cust);
                                                    mConnection.setData(bundle);
                                                    mConnection.setAction(MappService.DO_REMOVE_PHOTO);
                                                    if (Utility.doServiceAction(activity, mConnection, BIND_AUTO_CREATE)) {
                                                        Utility.showProgressDialog(activity, true);
                                                    }
                                                }
                                            }
                                        });
                        }

                    }
                });
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        try {
            boolean imageChanged = false;
            Uri selectedImage = null;
            Resources resources = getResources();
            int requestEdit = resources.getInteger(R.integer.request_edit);

            // When an Image is picked
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    Utility.showMessage(this, R.string.error_img_not_picked);
                    return;
                }
                mImgView.setBackgroundResource(0);
                if ((requestCode == resources.getInteger(R.integer.request_gallery) ||
                        requestCode == requestEdit)) {
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
            }
            if (imageChanged && requestCode != requestEdit) {
                Intent editIntent = new Intent(Intent.ACTION_EDIT);
                editIntent.setDataAndType(selectedImage, "image*//*");
                editIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(editIntent, requestEdit);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utility.showMessage(this, R.string.some_error);
        } finally {
            Utility.setEnabledButton(this, mUpdateButton, true);
        }
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == getResources().getInteger(R.integer.request_edit) &&
                resultCode == Activity.RESULT_OK) {
            byte[] image = WorkingDataStore.getBundle().getByteArray("photo");
            if (image != null) {
                ByteArrayToBitmapTask task = new ByteArrayToBitmapTask(mImgView, image,
                        mImgView.getMeasuredWidth(), mImgView.getMeasuredHeight());
                task.execute();
                Customer c = new Customer();
                setupUpdateData(c, false);
                c.setPhoto(image);
                taskCompleted(MappService.DO_UPLOAD_PHOTO, c);
            }
        } else {
            Utility.onPhotoActivityResult(this, requestCode, resultCode, data);
        }
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        /*if (intent != null) {
            intent.putExtra("customer", WorkingDataStore.getBundle().getParcelable("customer"));
        }*/
        return super.getParentActivityIntent();
    }

    @Override
    public void datePicked(String date) {
        Date datePicked = Utility.getStrAsDate(date, "dd/MM/yyyy");
        if (!Utility.isDateAfterToday(datePicked)) {
            mPname.setText(String.format("%s %s\n(%d years)", mCust.getfName(), mCust.getlName(),
                    Utility.getAge(datePicked)));
        }
    }

    @Override
    public void onBackPressed() {
        /*Bundle bundle = WorkingDataStore.getBundle();
        bundle.putParcelable("customer", getUpdateData());*/
        mConnection.setBound(false);
        //startActivity(getIntent());
        super.onBackPressed();
    }

    @Override
    public void taskCompleted(int action, HasPhoto entity) {
        Customer customer = (Customer) entity;
        Bundle bundle = new Bundle();
        bundle.putInt("loginType", MappService.CUSTOMER_LOGIN);
        bundle.putParcelable("customer", customer);
        mConnection.setData(bundle);
        mConnection.setAction(action);
        if (Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE)) {
            //Utility.showProgresmFormViewFormView, mProgressView, true);
            Utility.showProgressDialog(this, true);
        }
    }
}
