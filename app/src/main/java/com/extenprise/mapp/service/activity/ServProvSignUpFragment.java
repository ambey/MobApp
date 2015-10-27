package com.extenprise.mapp.service.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.data.SignInData;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.net.AppStatus;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.ui.TitleFragment;
import com.extenprise.mapp.util.EncryptUtil;
import com.extenprise.mapp.util.Utility;
import com.extenprise.mapp.util.Validator;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ambey on 10/9/15.
 */
public class ServProvSignUpFragment extends Fragment implements TitleFragment, ResponseHandler {
    private ServiceResponseHandler mResponseHandler = new ServiceResponseHandler(this);

    private int check;
    private View mRootView;
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

    private View mFormView;
    private View mProgressView;

    // LogCat tag
    private static final String TAG = ServProvSignUpActivity.class.getSimpleName();

    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Uri fileUri; // file url to store image/video

    private Button btnCapturePicture, btnRecordVideo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.activity_sign_up, container, false);
        LoginHolder.servLoginRef = new ServiceProvider();

        mRootView.findViewById(R.id.next).setVisibility(View.GONE);
        mFormView = mRootView.findViewById(R.id.signUpForm);
        mProgressView = mRootView.findViewById(R.id.progressView);

        mFirstName = (EditText) mRootView.findViewById(R.id.editTextFName);
        mLastName = (EditText) mRootView.findViewById(R.id.editTextLName);
        mCellphoneview = (EditText) mRootView.findViewById(R.id.editTextCellphone);
        mCellphoneview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if(!TextUtils.isEmpty(mCellphoneview.getText().toString().trim())) {
                        check = MappService.DO_PHONE_EXIST_CHECK;
                        checkExistence();
                    }
                }
            }
        });

        mPasswdView = (EditText) mRootView.findViewById(R.id.editTextPasswd);
        mCnfPasswdView = (EditText) mRootView.findViewById(R.id.editTextCnfPasswd);
        mImgView = (ImageView) mRootView.findViewById(R.id.uploadimageview);
        mImgTxtView = (TextView) mRootView.findViewById(R.id.uploadimage);
        mRadioGroupGender = (RadioGroup) mRootView.findViewById(R.id.radioGroupGender);
        mRegistrationNumber = (EditText) mRootView.findViewById(R.id.editTextRegistrationNumber);
        mRegistrationNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!TextUtils.isEmpty(mRegistrationNumber.getText().toString().trim())) {
                        check = MappService.DO_REG_NO_CHECK;
                        checkExistence();
                    }
                }
            }
        });
        return mRootView;
    }

    public boolean isValidInput(ViewPager pager) {
        boolean cancel = false;
        View focusView = null;

        String cnfPasswd = mCnfPasswdView.getText().toString();
        String passwd = mPasswdView.getText().toString();
        if (TextUtils.isEmpty(cnfPasswd)) {
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

        int genderID = mRadioGroupGender.getCheckedRadioButtonId();
        if (genderID == -1) {
            //Utility.showAlert(this, "", "Please Select Gender.");
            RadioButton mFemale = (RadioButton) mRootView.findViewById(R.id.radioButtonFemale);
            mFemale.setError("Please select Gender.");
            focusView = mFemale;
            cancel = true;
            //return;
        } else {
            mRadioButtonGender = (RadioButton) mRootView.findViewById(genderID);
        }

        String regNo = mRegistrationNumber.getText().toString();
        if (TextUtils.isEmpty(regNo)) {
            mRegistrationNumber.setError(getString(R.string.error_field_required));
            focusView = mRegistrationNumber;
            cancel = true;
        }

        /*if (isRegNoExist(regNo)) {
            mRegistrationNumber.setError("This Registration Number is already Registered.");
            focusView = mRegistrationNumber;
            cancel = true;
        }*/

        if (cancel) {
            focusView.requestFocus();
            pager.setCurrentItem(0);
            return false;
        }
        return true;
    }

    private void checkPhoneExistence() {
        Utility.showProgress(getActivity(), mFormView, mProgressView, true);
        Intent intent = new Intent(getActivity(), MappService.class);
        getActivity().bindService(intent, mConnection, FragmentActivity.BIND_AUTO_CREATE);
    }

    public void phoneCheckDone(Bundle data) {
        Utility.showProgress(getActivity(), mFormView, mProgressView, false);
        if (data.getBoolean("exists")) {
            mCellphoneview.setError(getString(R.string.error_phone_registered));
            mCellphoneview.requestFocus();
        }
    }

    private boolean isRegNoExist(String regNo) {
        MappDbHelper dbHelper = new MappDbHelper(getActivity());
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

    public void saveData() {
        ServiceProvider sp = LoginHolder.servLoginRef;
        try {
            sp.setImg(Utility.getBytesFromBitmap(mImgView.getDrawingCache()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        sp.setfName(mFirstName.getText().toString());
        sp.setlName(mLastName.getText().toString());
        sp.setPhone(mCellphoneview.getText().toString());
        sp.setGender(mRadioButtonGender.getText().toString());
        sp.setRegNo(mRegistrationNumber.getText().toString());
        sp.setPasswd(EncryptUtil.encrypt(mPasswdView.getText().toString()));
    }

    @Override
    public CharSequence getPageTitle() {
        return getString(R.string.personalDetails);
    }

    @Override
    public int getPageIconResId() {
        return 0;
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private void checkExistence() {
        if (AppStatus.getInstance(getActivity()).isOnline()) {
            Utility.showProgress(getActivity(), mFormView, mProgressView, true);
            Intent intent = new Intent(getActivity(), MappService.class);
            getActivity().bindService(intent, mConnection, FragmentActivity.BIND_AUTO_CREATE);
        } else {
            Toast.makeText(getActivity(), "You are not online!!!!", Toast.LENGTH_LONG).show();
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
            bundle.putInt("loginType", MappService.SERVICE_LOGIN);
            Message msg = null;
            if(check == MappService.DO_REG_NO_CHECK) {
                msg = Message.obtain(null, MappService.DO_REG_NO_CHECK);
                bundle.putString("regno", mRegistrationNumber.getText().toString().trim());
            } else if(check == MappService.DO_PHONE_EXIST_CHECK) {
                SignInData data = new SignInData();
                data.setPhone(mCellphoneview.getText().toString().trim());
                bundle.putParcelable("signInData", data);
                msg = Message.obtain(null, MappService.DO_PHONE_EXIST_CHECK);
            }
/*
            bundle.putString("phone", mCellphoneview.getText().toString().trim());
            bundle.putString("regno", mRegistrationNumber.getText().toString().trim());
            bundle.putParcelable("service", LoginHolder.servLoginRef);
            Message msg = Message.obtain(null, MappService.DO_SIGNUP);
*/
            msg.replyTo = new Messenger(mResponseHandler);
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
            getActivity().unbindService(mConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (action == MappService.DO_PHONE_EXIST_CHECK) {
            //phoneCheckDone(data);
            checkDone(data);
            return true;
        }
        if (action == MappService.DO_REG_NO_CHECK) {
            //regNoCheckDone(data);
            checkDone(data);
            return true;
        }
        return false;
    }

    public void checkDone(Bundle data) {
        Utility.showProgress(getActivity(), mFormView, mProgressView, false);
        if (data.getBoolean("exists")) {
            if(check == MappService.DO_PHONE_EXIST_CHECK) {
                mCellphoneview.setError(getString(R.string.error_phone_registered));
                mCellphoneview.requestFocus();
            } else if(check == MappService.DO_REG_NO_CHECK) {
                mRegistrationNumber.setError("This Registration Number is already Registered.");
                mRegistrationNumber.requestFocus();
            }
        }
    }

}
