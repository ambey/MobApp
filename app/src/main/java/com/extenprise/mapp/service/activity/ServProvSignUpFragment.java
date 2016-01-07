package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.data.SignInData;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.MappServiceConnection;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.ui.TitleFragment;
import com.extenprise.mapp.util.EncryptUtil;
import com.extenprise.mapp.util.Utility;
import com.extenprise.mapp.util.Validator;

public class ServProvSignUpFragment extends Fragment implements TitleFragment, ResponseHandler {
    // LogCat tag
/*
    private static final String TAG = ServProvSignUpActivity.class.getSimpleName();
*/
    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(getActivity(), this));

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

    private View mRootView;
    private View mFormView;
    private View mProgressView;
    private boolean imageChanged = false;
/*
    private int requestGallery = 100;
    private int requestCamera = 200;
    private int requestEdit = 300;
*/

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
        //mImgTxtView = (TextView) mRootView.findViewById(R.id.uploadimage);
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
        int category = getActivity().getIntent().getIntExtra("category", R.string.practitionar);
        if (category == R.string.medicalStore) {
            mImgView.setImageResource(R.drawable.medstore);
        } else if (category == R.string.diagnosticCenter) {
            mImgView.setImageResource(R.drawable.diagcenter);
        }
        //defaultImgBits = ((BitmapDrawable) mImgView.getDrawable()).getBitmap();

        /*if (savedInstanceState != null) {
            Bitmap bitmap = savedInstanceState.getParcelable("image");
            mImgView.setImageBitmap(bitmap);
        } else {
            mImgCopy = (Bitmap) getActivity().getLastNonConfigurationInstance();
            if (mImgCopy != null) {
                mImgView.setImageBitmap(mImgCopy);
            }
        }*/

        return mRootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        BitmapDrawable drawable = (BitmapDrawable) mImgView.getDrawable();
        if (drawable != null) {
            Bitmap bitmap = drawable.getBitmap();
            outState.putParcelable("image", bitmap);
        }
        super.onSaveInstanceState(outState);
    }

    /*@Override
    public Object onRetainNonConfigurationInstance() {
        return mImgCopy;
    }*/

    public boolean isValidInput(ViewPager pager) {
        boolean cancel = false;
        View focusView = null;

        EditText[] fields = {mFirstName, mLastName, mCellphoneview,
                mPasswdView, mCnfPasswdView, mRegistrationNumber};
        if (Utility.areEditFieldsEmpty(getActivity(), fields)) {
            pager.setCurrentItem(0);
            cancel = true;
        }

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
            if (focusView != null) {
                focusView.requestFocus();
            }
            pager.setCurrentItem(0);
            return false;
        }

        if (!imageChanged) {
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

        return true;
    }

    public void captureImage(View v) {
        final Activity activity = getActivity();
        Utility.showAlert(activity, getString(R.string.uploadImg), null, false, new String[]{activity.getString(R.string.take_photo),
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
                        mImgView.setImageBitmap(null);
                        mImgView.setBackgroundResource(R.drawable.dr_avatar);
                        break;
                }
            }
        });
    }

    public void enlargeImg(View view) {
        Utility.enlargeImage(mImgView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        try {
            Uri selectedImage = null;
            Resources resources = getResources();
            int requestEdit = resources.getInteger(R.integer.request_edit);
            // When an Image is picked
            if (resultCode == Activity.RESULT_OK) {
                if ((requestCode == resources.getInteger(R.integer.request_gallery) ||
                        requestCode == requestEdit)
                        && data != null) {
                    // Get the Image from data
                    selectedImage = data.getData();
                    mImgView.setImageURI(selectedImage);
                    imageChanged = true;

                } else if (requestCode == resources.getInteger(R.integer.request_camera) && data != null) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    mImgView.setImageBitmap(bitmap);
                    selectedImage = Utility.getImageUri(getActivity(), bitmap);
                    imageChanged = true;
                } else {
                    Utility.showMessage(getActivity(), R.string.error_img_not_picked);
                }
            } else if (requestCode == requestEdit) {
                imageChanged = true;
            }
            if (imageChanged) {
                if (requestCode != requestEdit) {
                    Intent editIntent = new Intent(Intent.ACTION_EDIT);
                    editIntent.setDataAndType(selectedImage, "image/*");
                    editIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    getActivity().startActivityForResult(editIntent, requestEdit);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utility.showMessage(getActivity(), R.string.some_error);
        }
    }

    public void saveData() {
        ServiceProvider sp = LoginHolder.servLoginRef;
        try {
            sp.setPhoto(Utility.getBytesFromBitmap(((BitmapDrawable) mImgView.getDrawable()).getBitmap()));
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
        if (Utility.doServiceAction(getActivity(), mConnection, Context.BIND_AUTO_CREATE)) {
            Utility.showProgress(getActivity(), mFormView, mProgressView, true);
        }
    }

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


    /*private boolean isRegNoExist(String regNo) {
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
    }*/

    public void onBackPressed() {
        mConnection.setBound(false);
        //startActivity(getIntent());
        getActivity().onBackPressed();
    }

}
