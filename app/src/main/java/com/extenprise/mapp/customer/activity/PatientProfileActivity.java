package com.extenprise.mapp.customer.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.customer.data.Customer;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.MappServiceConnection;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.util.DateChangeListener;
import com.extenprise.mapp.util.Utility;
import com.extenprise.mapp.util.Validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class PatientProfileActivity extends FragmentActivity implements ResponseHandler, DateChangeListener {

    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));
    private Customer mCustomer;

    private LinearLayout mContLay;
    private LinearLayout mAddrLayout;
    private View mFormView;
    private View mProgressView;

    private TextView mPname, mTextViewDOB, mMobNo;
    private EditText mEditTextCustomerFName;
    private EditText mEditTextCustomerLName;
    private EditText mEditTextCustomerEmail;
    private EditText mEditTextHeight;
    private EditText mEditTextWeight;
    private EditText mEditTextLoc;
    private EditText mEditTextPinCode;
    private Spinner mSpinCity;
    private Spinner mSpinState;
    private Spinner mSpinGender;
    private ImageView mImgView;

    private Bitmap mImgCopy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mCustomer = LoginHolder.custLoginRef;

        mContLay = (LinearLayout) findViewById(R.id.contLay);
        mAddrLayout = (LinearLayout) findViewById(R.id.addrLayout);

        mFormView = findViewById(R.id.scrollView);
        mProgressView = findViewById(R.id.progressView);

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
        mSpinCity = (Spinner) findViewById(R.id.editTextCity);
        mSpinState = (Spinner) findViewById(R.id.editTextState);
        mSpinGender = (Spinner) findViewById(R.id.spinGender);

        mPname.setText(String.format("%s %s\n(%d years)", mCustomer.getfName(), mCustomer.getlName(),
                Utility.getAge(mCustomer.getDob())));
        mMobNo.setText(mCustomer.getSignInData().getPhone());
        if (mCustomer.getPhoto() != null) {
            mImgView.setImageBitmap(Utility.getBitmapFromBytes(mCustomer.getPhoto()));
        }
        mEditTextCustomerFName.setText(mCustomer.getfName());
        mEditTextCustomerLName.setText(mCustomer.getlName());
        mEditTextCustomerEmail.setText(mCustomer.getEmailId());

        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        Date dt = mCustomer.getDob();
        if (dt != null) {
            String dob = sdf.format(mCustomer.getDob());
            mTextViewDOB.setText(dob);
        }
        mSpinGender.setSelection(Utility.getSpinnerIndex(mSpinGender, mCustomer.getGender()));
        mEditTextHeight.setText(String.format("%.1f", mCustomer.getHeight()));
        mEditTextWeight.setText(String.format("%.1f", mCustomer.getWeight()));
        mEditTextLoc.setText(mCustomer.getLocation());
        mEditTextPinCode.setText(mCustomer.getPincode());
        mSpinCity.setSelection(Utility.getSpinnerIndex(mSpinCity, mCustomer.getCity().getCity()));
        mSpinState.setSelection(Utility.getSpinnerIndex(mSpinState, mCustomer.getCity().getState()));

        setFieldsEnability(false);

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

    private void setFieldsEnability(boolean set) {
        mEditTextCustomerFName.setEnabled(set);
        mEditTextCustomerLName.setEnabled(set);
        mEditTextCustomerEmail.setEnabled(set);
        mEditTextPinCode.setEnabled(set);
        mEditTextLoc.setEnabled(set);
        mEditTextWeight.setEnabled(set);
        mEditTextHeight.setEnabled(set);
        mTextViewDOB.setEnabled(set);
        mSpinCity.setEnabled(set);
        mSpinState.setEnabled(set);
        mSpinGender.setEnabled(set);
    }

    public void editPatientProf(View v) {
        if (mEditTextCustomerFName.isEnabled()) {
            setFieldsEnability(false);
        } else {
            setFieldsEnability(true);
        }
        //showPersonalFields(v);
        mContLay.setVisibility(View.VISIBLE);
    }

    public void updateProfile(View v) {
        EditText[] fields = {mEditTextCustomerFName, mEditTextCustomerLName, mEditTextWeight,
                mEditTextLoc, mEditTextPinCode};
        if (Utility.areEditFieldsEmpty(this, fields)) {
            return;
        }

        boolean valid = true;
        View focusView = null;

        String fName = mEditTextCustomerFName.getText().toString().trim();
        String lName = mEditTextCustomerLName.getText().toString().trim();
        String emailId = mEditTextCustomerEmail.getText().toString().trim();
        String dob = mTextViewDOB.getText().toString();
        //String weight = mEditTextWeight.getText().toString().trim();
        //String loc = mEditTextLoc.getText().toString().trim();
        String pinCode = mEditTextPinCode.getText().toString().trim();

        if (!Validator.isOnlyAlpha(fName)) {
            mEditTextCustomerFName.setError(getString(R.string.error_only_alpha));
            focusView = mEditTextCustomerFName;
            valid = false;
        }
        if (!Validator.isOnlyAlpha(lName)) {
            mEditTextCustomerFName.setError(getString(R.string.error_only_alpha));
            focusView = mEditTextCustomerLName;
            valid = false;
        }
        if (!TextUtils.isEmpty(emailId) && !Validator.isValidEmaillId(emailId)) {
            mEditTextCustomerEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEditTextCustomerEmail;
            valid = false;
        }
        if (TextUtils.isEmpty(dob)) {
            mTextViewDOB.setError(getString(R.string.error_field_required));
            focusView = mTextViewDOB;
            valid = false;
        } else if (Utility.getAge(Utility.getStrAsDate(dob, "dd/MM/yyyy")) <= 0) {
            mTextViewDOB.setError(getString(R.string.error_future_date));
            focusView = mTextViewDOB;
            valid = false;
        }
        if (Validator.isPinCodeValid(pinCode)) {
            mEditTextPinCode.setError(getString(R.string.error_invalid_pincode));
            focusView = mEditTextPinCode;
            valid = false;
        }

        if (!valid && focusView != null) {
            focusView.requestFocus();
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putInt("loginType", MappService.CUSTOMER_LOGIN);
        bundle.putParcelable("customer", getUpdateData());
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_UPDATE);
        if (Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE)) {
            Utility.showProgress(this, mFormView, mProgressView, true);
        }
        //sendRequest(getUpdateData(), MappService.DO_UPDATE);
    }

    private void removePhotoDone() {
        Utility.showProgress(this, mFormView, mProgressView, false);
        mImgView.setBackgroundResource(R.drawable.patient);
        mImgView.setImageBitmap(null);
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        if (action == MappService.DO_UPDATE) {
            updateDone(data);
            return true;
        } else if (action == MappService.DO_REMOVE_PHOTO) {
            removePhotoDone();
            return true;
        }
        /*if (action == MappService.DO_UPLOAD_PHOTO) {
            uploadPhotoDone(data);
            return true;
        }*/
        return false;
    }

    /*private void uploadPhotoDone(Bundle data) {
        Utility.showProgress(this, mFormView, mProgressView, false);
        if (data.getBoolean("status")) {
            LoginHolder.custLoginRef.setPhoto(Utility.getBytesFromBitmap(mImgView.getDrawingCache()));
            Utility.showMessage(this, R.string.msg_upload_photo);
        } else {
            mImgView.setImageBitmap(mImgCopy);
            Utility.showMessage(this, R.string.some_error);
        }
        refresh();
    }*/

    private void updateDone(Bundle data) {
        Utility.showProgress(this, mFormView, mProgressView, false);
        if (data.getBoolean("status")) {
            Utility.showAlert(this, "", getString(R.string.msg_profile_updated), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    LoginHolder.custLoginRef = getUpdateData();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            });
        } else {
            Utility.showMessage(this, R.string.some_error);
        }
    }

    private Customer getUpdateData() {
        Customer c = new Customer();
        c.getSignInData().setPhone(mMobNo.getText().toString());
        c.setfName(mEditTextCustomerFName.getText().toString().trim());
        c.setlName(mEditTextCustomerLName.getText().toString().trim());
        if (mImgView.getDrawable() != null) {
            c.setPhoto(Utility.getBytesFromBitmap(((BitmapDrawable) mImgView.getDrawable()).getBitmap()));
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
        c.getCity().setCity(mSpinCity.getSelectedItem().toString());
        c.getCity().setState(mSpinState.getSelectedItem().toString());
        c.getCity().setCountry("India");

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
        final Activity activity = this;
        Utility.showAlert(activity, activity.getString(R.string.take_photo), null, false,
                /*
                The array is put here instead of a method call to get the array because
                OnClickHandler is using array index to take actions, and it helps to understand
                if the array is infront of us
                 */
                new String[]{activity.getString(R.string.take_photo),
                        activity.getString(R.string.from_gallery),
                        activity.getString(R.string.remove)}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Utility.startCamera(activity, getResources().getInteger(R.integer.request_camera));
                                break;
                            case 1:
                                Utility.pickPhotoFromGallery(activity, getResources().getInteger(R.integer.request_gallery));
                                break;
                            case 2:
                                Utility.showAlert(activity, activity.getString(R.string.remove), getString(R.string.confirm_remove_photo), true,
                                        null,
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                if (which == DialogInterface.BUTTON_POSITIVE) {
                                                    Bundle bundle = new Bundle();
                                                    bundle.putInt("loginType", MappService.CUSTOMER_LOGIN);
                                                    bundle.putParcelable("customer", mCustomer);
                                                    mConnection.setData(bundle);
                                                    mConnection.setAction(MappService.DO_REMOVE_PHOTO);
                                                    if (Utility.doServiceAction(activity, mConnection, BIND_AUTO_CREATE)) {
                                                        Utility.showProgress(getApplicationContext(), mFormView, mProgressView, true);
                                                    }
                                                }
                                            }
                                        });
                        }

                    }
                });
    }

    @Override
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
                editIntent.setDataAndType(selectedImage, "image/*");
                editIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(editIntent, requestEdit);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utility.showMessage(this, R.string.some_error);
        }
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        Intent intent = super.getParentActivityIntent();
        if (intent != null) {
            intent.putExtra("customer", getIntent().getParcelableExtra("customer"));
        }
        return intent;
    }

    @Override
    public void datePicked(String date) {
        Date datePicked = Utility.getStrAsDate(date, "dd/MM/yyyy");
        if (!Utility.isDateAfterToday(datePicked)) {
            mPname.setText(String.format("%s %s\n(%d years)", mCustomer.getfName(), mCustomer.getlName(),
                    Utility.getAge(mCustomer.getDob())));
        }
    }
}
