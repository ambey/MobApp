package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.customer.activity.PatientsHomeScreenActivity;
import com.extenprise.mapp.data.SignInData;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.MappServiceConnection;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.data.Service;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.ui.TitleFragment;
import com.extenprise.mapp.util.EncryptUtil;
import com.extenprise.mapp.util.Utility;
import com.extenprise.mapp.util.Validator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ServProvSignUpFragment extends Fragment implements TitleFragment, ResponseHandler {
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    // LogCat tag
    private static final String TAG = ServProvSignUpActivity.class.getSimpleName();
    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int GALLERY_IMAGE_REQUEST_CODE = 200;
    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(getActivity(), this));
    private View mRootView;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mCellphoneview;
    private EditText mPasswdView;
    private EditText mCnfPasswdView;
    private EditText mEmailID;
    private RadioGroup mRadioGroupGender;
    private RadioButton mRadioButtonGender;
    private EditText mRegistrationNumber;
    private ImageView mImgView;
    private TextView mImgTxtView;
    private View mFormView;
    private View mProgressView;
    private Uri fileUri; // file url to store image/video
    private Bitmap defaultImgBits;

    private Button btnCapturePicture, btnRecordVideo;

    /*private static File getOutputMediaFile(int type) {

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
    }*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.activity_sign_up, container, false);
        LoginHolder.servLoginRef = new ServiceProvider();

        //mRootView.findViewById(R.id.next).setVisibility(View.GONE);
        mFormView = mRootView.findViewById(R.id.signUpForm);
        mProgressView = mRootView.findViewById(R.id.progressView);

        mFirstName = (EditText) mRootView.findViewById(R.id.editTextFName);
        mLastName = (EditText) mRootView.findViewById(R.id.editTextLName);
        mCellphoneview = (EditText) mRootView.findViewById(R.id.editTextCellphone);
        mCellphoneview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (Validator.isPhoneValid(mCellphoneview.getText().toString().trim())) {
                        checkExistence(MappService.DO_PHONE_EXIST_CHECK);
                    }
                }
            }
        });
        mEmailID = (EditText) mRootView.findViewById(R.id.editTextEmail);
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
                        checkExistence(MappService.DO_REG_NO_CHECK);
                    }
                }
            }
        });

        defaultImgBits = mImgView.getDrawingCache();

        int category = getActivity().getIntent().getIntExtra("category", R.string.practitionar);
        if (category == R.string.medicalStore) {
            mImgView.setImageResource(R.drawable.medstore);
        } else if (category == R.string.diagnosticCenter) {
            mImgView.setImageResource(R.drawable.diagcenter);
        }

        return mRootView;
    }

    public boolean isValidInput(ViewPager pager) {
        EditText[] fields = { mFirstName, mLastName, mCellphoneview,
                mPasswdView, mCnfPasswdView, mRegistrationNumber };
        if(Utility.areEditFieldsEmpty(getActivity(), fields)) {
            pager.setCurrentItem(0);
            return false;
        }

        boolean cancel = false;
        View focusView = null;

        String fnm = mFirstName.getText().toString();
        String lnm = mLastName.getText().toString();
        String cnfPasswd = mCnfPasswdView.getText().toString();
        String passwd = mPasswdView.getText().toString();
        String email = mEmailID.getText().toString().trim();

        if (!passwd.equals(cnfPasswd)) {
            mCnfPasswdView.setError(getString(R.string.error_password_not_matching));
            focusView = mCnfPasswdView;
            cancel = true;
        }
        if (!Validator.isPasswordValid(passwd)) {
            mPasswdView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswdView;
            cancel = true;
        }

        if (!Validator.isPhoneValid(mCellphoneview.getText().toString())) {
            mCellphoneview.setError(getString(R.string.error_invalid_phone));
            focusView = mCellphoneview;
            cancel = true;
        }

        if (!TextUtils.isEmpty(email) && !Validator.isValidEmaillId(email)) {
            mEmailID.setError(getString(R.string.error_invalid_email));
            focusView = mEmailID;
            cancel = true;
        }

        if (!Validator.isOnlyAlpha(lnm)) {
            mLastName.setError(getString(R.string.error_only_alpha));
            focusView = mLastName;
            cancel = true;
        }

        if (!Validator.isOnlyAlpha(fnm)) {
            mFirstName.setError(getString(R.string.error_only_alpha));
            focusView = mFirstName;
            cancel = true;
        }

        int genderID = mRadioGroupGender.getCheckedRadioButtonId();
        if (genderID == -1) {
            //Utility.showAlert(this, "", "Please Select Gender.");
            RadioButton mFemale = (RadioButton) mRootView.findViewById(R.id.radioButtonFemale);
            mFemale.setError(getString(R.string.error_select_gender));
            focusView = mFemale;
            cancel = true;
            //return;
        } else {
            mRadioButtonGender = (RadioButton) mRootView.findViewById(genderID);
        }

        if (cancel) {
            focusView.requestFocus();
            pager.setCurrentItem(0);
            return false;
        }
        return true;
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
        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);*/

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle("Upload Image ");
        dialogBuilder.setItems(Utility.optionItems(getActivity()), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File f = new File(android.os.Environment
                                .getExternalStorageDirectory(), "temp.jpg");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                        getActivity().startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                        break;
                    case 1:
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        getActivity().startActivityForResult(galleryIntent, GALLERY_IMAGE_REQUEST_CODE);
                        break;
                    case 2:
                        dialog.dismiss();
                        break;
                }
            }
        });

        dialogBuilder.create().show();
        //Utility.captureImage(getActivity()).create().show();
    }

    public void enlargeImg(View view) {
        Utility.enlargeImage(mImgView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == GALLERY_IMAGE_REQUEST_CODE
                        && null != data) {
                    // Get the Image from data

                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    // Get the cursor
                    Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();
                    // Set the Image in ImageView after decoding the String
                    mImgView.setImageBitmap(BitmapFactory
                            .decodeFile(imgDecodableString));

                } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
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
                    Utility.showMessage(getActivity(), R.string.error_img_not_picked);
                }
            }
        } catch (Exception e) {
            Utility.showMessage(getActivity(), R.string.some_error);
        }

    }

    /*public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }*/

    public void saveData() {
        if(defaultImgBits == mImgView.getDrawingCache()) {
            Utility.confirm(getActivity(), R.string.msg_without_img, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_NEGATIVE) {
                        Utility.showMessage(getActivity(), R.string.msg_set_img);
                        return;
                    }
                    dialog.dismiss();
                }
            });
        }

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
    private void checkExistence(int check) {
        Bundle bundle = new Bundle();
        bundle.putInt("loginType", MappService.SERVICE_LOGIN);
        if (check == MappService.DO_REG_NO_CHECK) {
            String regNo = mRegistrationNumber.getText().toString().trim();
            ServiceProvider sp = new ServiceProvider();
            sp.setRegNo(regNo);
            bundle.putString("regno", regNo);
            bundle.putParcelable("service", sp);
        } else if (check == MappService.DO_PHONE_EXIST_CHECK) {
            SignInData data = new SignInData();
            data.setPhone(mCellphoneview.getText().toString().trim());
            bundle.putParcelable("signInData", data);
        }
        mConnection.setData(bundle);
        mConnection.setAction(check);
        if(Utility.doServiceAction(getActivity(), mConnection, Context.BIND_AUTO_CREATE)) {
            Utility.showProgress(getActivity(), mFormView, mProgressView, true);
        }
    }

/*
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
*/
/*
            bundle.putString("phone", mCellphoneview.getText().toString().trim());
            bundle.putString("regno", mRegistrationNumber.getText().toString().trim());
            bundle.putParcelable("service", LoginHolder.servLoginRef);
            Message msg = Message.obtain(null, MappService.DO_SIGNUP);
*//*

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
*/

    @Override
    public boolean gotResponse(int action, Bundle data) {
        if (action == MappService.DO_PHONE_EXIST_CHECK ||
                action == MappService.DO_REG_NO_CHECK) {
            checkDone(action, data);
            return true;
        }
        return false;
    }

    public void checkDone(int check, Bundle data) {
        Utility.showProgress(getActivity(), mFormView, mProgressView, false);
        if (!data.getBoolean("status")) {
            if (check == MappService.DO_REG_NO_CHECK) {
                mRegistrationNumber.setError(getString(R.string.error_duplicate_reg_no));
                mRegistrationNumber.requestFocus();
            }
            if (check == MappService.DO_PHONE_EXIST_CHECK) {
                mCellphoneview.setError(getString(R.string.error_phone_registered));
                mCellphoneview.requestFocus();
            }
        }
    }

}
