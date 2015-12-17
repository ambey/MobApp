package com.extenprise.mapp.customer.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class PatientProfileActivity extends Activity implements ResponseHandler, DateChangeListener {

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
        if (!isValidInput()) {
            return;
        }
        sendRequest(getUpdateData(), MappService.DO_UPDATE);
    }

    private void sendRequest(Customer customer, int action) {
        Bundle bundle = new Bundle();
        bundle.putInt("loginType", MappService.CUSTOMER_LOGIN);
        bundle.putParcelable("customer", customer);
        mConnection.setData(bundle);
        mConnection.setAction(action);
        if (Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE)) {
            Utility.showProgress(this, mFormView, mProgressView, true);
        }
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        if (action == MappService.DO_UPDATE) {
            updateDone(data);
            return true;
        }
        if(action == MappService.DO_UPLOAD_PHOTO) {
            uploadPhotoDone(data);
            return true;
        }
        return false;
    }

    private void uploadPhotoDone(Bundle data) {
        Utility.showProgress(this, mFormView, mProgressView, false);
        if (data.getBoolean("status")) {
            Utility.showMessage(this, R.string.msg_upload_photo);
        } else {
            mImgView.setImageBitmap(mImgCopy);
            Utility.showMessage(this, R.string.some_error);
        }
        refresh();
    }

    private void updateDone(Bundle data) {
        Utility.showProgress(this, mFormView, mProgressView, false);
        if (data.getBoolean("status")) {
            Utility.showAlert(this, "", getString(R.string.msg_profile_updated), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    LoginHolder.custLoginRef = getUpdateData();
                    refresh();
                }
            });
        }
    }

    private Customer getUpdateData() {
        Customer c = new Customer();
        c.getSignInData().setPhone(mMobNo.getText().toString());
        c.setfName(mEditTextCustomerFName.getText().toString().trim());
        c.setlName(mEditTextCustomerLName.getText().toString().trim());
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
        Utility.captureImage(this).create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        try {
            boolean isImageChanged = false;
            // When an Image is picked
            if (resultCode == RESULT_OK) {
                if (requestCode == R.integer.request_gallery
                        && null != data) {
                    // Get the Image from data

                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    if (cursor == null) {
                        return;
                    }
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();
                    // Set the Image in ImageView after decoding the String
                    mImgView.setImageBitmap(BitmapFactory
                            .decodeFile(imgDecodableString));
                    isImageChanged = true;

                } else if (requestCode == R.integer.request_camera) {
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
                        isImageChanged = true;

                        String path = android.os.Environment
                                .getExternalStorageDirectory()
                                + File.separator
                                + "Phoenix" + File.separator + "default";
                        if (f.delete()) {
                            Log.v(this.getClass().getName(), "File delete successful");
                        }
                        OutputStream fOut;
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
                    Utility.showMessage(this, R.string.error_img_not_picked);
                }
            }
            if(isImageChanged) {
                mCustomer.setPhoto(Utility.getBytesFromBitmap(mImgView.getDrawingCache()));
                sendRequest(mCustomer, MappService.DO_UPLOAD_PHOTO);
            }
        } catch (Exception e) {
            Utility.showMessage(this, R.string.some_error);
        }
    }

    private boolean isValidInput() {
        EditText[] fields = { mEditTextCustomerFName, mEditTextCustomerLName, mEditTextWeight,
                mEditTextLoc, mEditTextPinCode };
        if(Utility.areEditFieldsEmpty(this, fields)) {
            return false;
        }

        boolean valid = true;
        View focusView = null;

        String fName = mEditTextCustomerFName.getText().toString().trim();
        String lName = mEditTextCustomerLName.getText().toString().trim();
        String emailId = mEditTextCustomerEmail.getText().toString().trim();
        String dob = mTextViewDOB.getText().toString();
        //String weight = mEditTextWeight.getText().toString().trim();
        String loc = mEditTextLoc.getText().toString().trim();
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

        if (TextUtils.isEmpty(loc)) {
            mEditTextLoc.setError(getString(R.string.error_field_required));
            focusView = mEditTextLoc;
            valid = false;
        }

        if (Validator.isPinCodeValid(pinCode)) {
            mEditTextPinCode.setError(getString(R.string.error_invalid_pincode));
            focusView = mEditTextPinCode;
            valid = false;
        }

        if (focusView != null) {
            focusView.requestFocus();
        }
        return valid;
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

    /*@Override
    public void onBackPressed() {
    }*/

    private void refresh() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
