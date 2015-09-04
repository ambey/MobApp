package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.Validator;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ServProvSignUpActivity extends Activity {

    private EditText mFirstName;
    private EditText mLastName;
    private EditText mCellphoneview;
    private EditText mPasswdView;
    private EditText mCnfPasswdView;
    private RadioGroup mRadioGroupGender;
    private RadioButton mRadioButtonGender;
    private EditText mRegistrationNumber;
    private ImageView mImgView;
    private TextView mImgTxtView;

    // LogCat tag
    private static final String TAG = ServProvSignUpActivity.class.getSimpleName();

    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Uri fileUri; // file url to store image/video

    private Button btnCapturePicture, btnRecordVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        LoginHolder.servLoginRef = new ServiceProvider();

        mFirstName = (EditText) findViewById(R.id.editTextFName);
        mLastName = (EditText) findViewById(R.id.editTextLName);
        mCellphoneview = (EditText) findViewById(R.id.editTextCellphone);
        mPasswdView = (EditText) findViewById(R.id.editTextPasswd);
        mCnfPasswdView = (EditText) findViewById(R.id.editTextCnfPasswd);
        mImgView = (ImageView) findViewById(R.id.uploadimageview);
        mImgTxtView = (TextView) findViewById(R.id.uploadimage);
        mRadioGroupGender = (RadioGroup) findViewById(R.id.radioGroupGender);
        mRegistrationNumber = (EditText) findViewById(R.id.editTextRegistrationNumber);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
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

    public void showAddWorkPlaceScreen(View v) {
        if (!isValidInput()) {
            return;
        }
/*
        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        showProgress(true);
        mAuthTask = new UserLoginTask(email, password);
        mAuthTask.execute((Void) null);
*/
        ServiceProvider sp = new ServiceProvider();
        sp.setfName(mFirstName.getText().toString());
        sp.setlName(mLastName.getText().toString());
        sp.setPhone(mCellphoneview.getText().toString());
        sp.setGender(mRadioButtonGender.getText().toString());
        sp.setRegNo(mRegistrationNumber.getText().toString());
        sp.setPasswd(mPasswdView.getText().toString());

        LoginHolder.servLoginRef = sp;

        Intent intent = new Intent(this, AddWorkPlaceActivity.class);
        startActivity(intent);
    }

    /*public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new com.extenprise.mapp.util.DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }*/

    private boolean isValidInput() {
        boolean cancel = false;
        View focusView = null;

        String cnfPasswd = mCnfPasswdView.getText().toString();
        String passwd = mPasswdView.getText().toString();
        if(TextUtils.isEmpty(cnfPasswd)) {
            mCnfPasswdView.setError(getString(R.string.error_field_required));
            focusView = mCnfPasswdView;
            cancel = true;
        } else if (!passwd.equals(cnfPasswd)) {
            mCnfPasswdView.setError(getString(R.string.error_password_not_matching));
            focusView = mCnfPasswdView;
            cancel = true;
        }
        if (TextUtils.isEmpty(passwd)) {
            mPasswdView.setError(getString(R.string.error_field_required));
            focusView = mPasswdView;
            cancel = true;
        } else if (!Validator.isPasswordValid(passwd)) {
            mPasswdView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswdView;
            cancel = true;
        }

        String phone = mCellphoneview.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            mCellphoneview.setError(getString(R.string.error_field_required));
            focusView = mCellphoneview;
            cancel = true;
        } else if (!Validator.isPhoneValid(phone)) {
            mCellphoneview.setError(getString(R.string.error_invalid_phone));
            focusView = mCellphoneview;
            cancel = true;
        }
        if (TextUtils.isEmpty(mLastName.getText().toString())) {
            mLastName.setError(getString(R.string.error_field_required));
            focusView = mLastName;
            cancel = true;
        }
        if (TextUtils.isEmpty(mFirstName.getText().toString())) {
            mFirstName.setError(getString(R.string.error_field_required));
            focusView = mFirstName;
            cancel = true;
        }

        if(isPhoneRegistered(phone)) {
            mCellphoneview.setError(getString(R.string.error_phone_registered));
            focusView = mCellphoneview;
            cancel = true;
        }

        int genderID = mRadioGroupGender.getCheckedRadioButtonId();
        if(genderID == -1) {
            //UIUtility.showAlert(this, "", "Please Select Gender.");
            RadioButton mFemale = (RadioButton)findViewById(R.id.radioButtonFemale);
            mFemale.setError("Please select Gender.");
            focusView = mFemale;
            cancel = true;
            //return;
        } else {
            mRadioButtonGender = (RadioButton)findViewById(genderID);
        }

        String regNo = mRegistrationNumber.getText().toString();
        if(TextUtils.isEmpty(regNo)) {
            mRegistrationNumber.setError(getString(R.string.error_field_required));
            focusView = mRegistrationNumber;
            cancel = true;
        }

        if(isRegNoExist(regNo)) {
            mRegistrationNumber.setError("This Registration Number is already Registered.");
            focusView = mRegistrationNumber;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    private boolean isPhoneRegistered(String phone) {
        MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                MappContract.ServiceProvider.COLUMN_NAME_CELLPHONE
        };

        String selection = MappContract.ServiceProvider.COLUMN_NAME_CELLPHONE + "=?";

        String[] selectionArgs = {
                phone
        };
        Cursor c = db.query(MappContract.ServiceProvider.TABLE_NAME,
                projection, selection, selectionArgs, null, null, null);
        int count = c.getCount();
        c.close();

        return (count > 0);
    }

    private boolean isRegNoExist(String regNo) {
        MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                MappContract.ServiceProvider.COLUMN_NAME_REGISTRATION_NUMBER
        };

        String selection = MappContract.ServiceProvider.COLUMN_NAME_REGISTRATION_NUMBER + "=?";

        String[] selectionArgs = {
                regNo
        };
        Cursor c = db.query(MappContract.ServiceProvider.TABLE_NAME,
                projection, selection, selectionArgs, null, null, null);
        int count = c.getCount();
        c.close();

        return (count > 0);
    }

    public void captureImage(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Android File Upload");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + "Android File Upload" + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
}
