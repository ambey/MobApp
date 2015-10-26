package com.extenprise.mapp.customer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
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
import android.widget.Toast;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.customer.data.Customer;
import com.extenprise.mapp.net.AppStatus;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.util.Utility;
import com.extenprise.mapp.util.Validator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class PatientProfileActivity extends Activity implements ResponseHandler {

    private ServiceResponseHandler mRespHandler = new ServiceResponseHandler(this);
    private int mServiceAction;

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

    private static int RESULT_LOAD_IMG = 1;
    private static int REQUEST_CAMERA = 2;
    private String imgDecodableString;
    private Bitmap mImgCopy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mContLay = (LinearLayout) findViewById(R.id.contLay);
        mAddrLayout = (LinearLayout) findViewById(R.id.addrLayout);

        mFormView = findViewById(R.id.scrollView);
        mProgressView = findViewById(R.id.progressView);

        mPname = (TextView) findViewById(R.id.textviewPname);
        mMobNo = (TextView) findViewById(R.id.mobnumValue);
        mImgView = (ImageView) findViewById(R.id.imageViewPatient);

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
        mSpinGender = (Spinner) findViewById(R.id.spinGender);

        viewProfile();

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

    private void viewProfile() {
        //DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String dob = "" + Calendar.getInstance();
        int d=0, m=0, y=0;
        if(LoginHolder.custLoginRef.getDob() != null) {
            //dob = df.format(LoginHolder.custLoginRef.getDob());
            Date dd = LoginHolder.custLoginRef.getDob();
            d = dd.getDay();
            m = dd.getMonth();
            y = dd.getYear();
            dob = String.format("%02d/%02d/%4d", d, m + 1, y);
        }

        mPname.setText(LoginHolder.custLoginRef.getfName() + " " + LoginHolder.custLoginRef.getlName());
        mMobNo.setText(LoginHolder.custLoginRef.getSignInData().getPhone());
        if(LoginHolder.custLoginRef.getImg() != null) {
            mImgView.setImageBitmap(Utility.getBitmapFromBytes(LoginHolder.custLoginRef.getImg()));
        }
        mEditTextCustomerFName.setText(LoginHolder.custLoginRef.getfName());
        mEditTextCustomerLName.setText(LoginHolder.custLoginRef.getlName());
        mEditTextCustomerEmail.setText(LoginHolder.custLoginRef.getEmailId());
        mTextViewDOB.setText(dob);
        mSpinGender.setSelection(Utility.getSpinnerIndex(mSpinGender, LoginHolder.custLoginRef.getGender()));
        mEditTextHeight.setText("" + LoginHolder.custLoginRef.getHeight());
        mEditTextWeight.setText("" + LoginHolder.custLoginRef.getWeight());
        mEditTextLoc.setText(LoginHolder.custLoginRef.getLocation());
        mEditTextPinCode.setText(LoginHolder.custLoginRef.getPincode());
        mSpinCity.setSelection(Utility.getSpinnerIndex(mSpinCity, LoginHolder.custLoginRef.getCity().getCity()));
        mSpinState.setSelection(Utility.getSpinnerIndex(mSpinState, LoginHolder.custLoginRef.getCity().getState()));

        setFieldsEnability(false);
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
        if(mEditTextCustomerFName.isEnabled()) {
            setFieldsEnability(false);
        } else {
            setFieldsEnability(true);
        }

        mContLay.setVisibility(View.VISIBLE);
    }

    public void updateProfile(View v) {
        if(!isValidInput()) {
            return;
        }
        if (AppStatus.getInstance(this).isOnline()) {
            Utility.showProgress(this, mFormView, mProgressView, true);
            Intent intent = new Intent(this, MappService.class);
            mServiceAction = MappService.DO_UPDATE;
            bindService(intent, mConnection, BIND_AUTO_CREATE);
        } else {
            Toast.makeText(this, "You are not online!!!!", Toast.LENGTH_LONG).show();
            Log.v("Home", "############################You are not online!!!!");
        }

    }

    private ServiceConnection mConnection = new ServiceConnection() {
        private Messenger mService;

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            mService = new Messenger(service);
            Bundle bundle = new Bundle();
            bundle.putInt("loginType", MappService.CUSTOMER_LOGIN);
            bundle.putParcelable("customer", getUpdateData());
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
        if(action == MappService.DO_UPDATE) {
            updateDone(data);
            return true;
        }
        return false;
    }

    private void updateDone(Bundle data) {
        Utility.showProgress(this, mFormView, mProgressView, false);
        if(data.getBoolean("status")) {
            Utility.showRegistrationAlert(this, "", "Profile Updated.");
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
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setAge(Utility.getAge(mTextViewDOB.getText().toString()));
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
        Utility.datePicker(view, mTextViewDOB);
    }

    public void changeImg(View view) {

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

    private boolean isValidInput() {
        boolean valid = true;
        View focusView = null;

        String fName = mEditTextCustomerFName.getText().toString().trim();
        String lName = mEditTextCustomerLName.getText().toString().trim();
        String emailId = mEditTextCustomerEmail.getText().toString().trim();
        String dob = mTextViewDOB.getText().toString();
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

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        Intent intent = super.getParentActivityIntent();
        if(intent != null) {
            intent.putExtra("customer", getIntent().getParcelableExtra("customer"));
        }
        return intent;
    }
}
