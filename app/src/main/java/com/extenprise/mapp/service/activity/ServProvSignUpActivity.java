package com.extenprise.mapp.service.activity;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.util.Utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

//import android.support.design.widget.TabLayout;

public class ServProvSignUpActivity extends FragmentActivity {

    private ServProvSignUpPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    //private ImageView mImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_pager);

        List<Fragment> fragments = new ArrayList<>();
        Bundle fragmentBundle = new Bundle();
        fragments.add(Fragment.instantiate(this, ServProvSignUpFragment.class.getName(), fragmentBundle));
        fragments.add(Fragment.instantiate(this, ServProvWorkPlaceFragment.class.getName(), fragmentBundle));

        mPagerAdapter = new ServProvSignUpPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager = (ViewPager) findViewById(R.id.signUpViewPager);
        mViewPager.setAdapter(mPagerAdapter);

        Intent intent = getIntent();
        int category = intent.getIntExtra("category", R.string.practitionar);
        Log.v(this.getClass().getName(), "category: " + getString(category));

        //mImgView = (ImageView) mRootView.findViewById(R.id.uploadimageview);
/*
        TabLayout tabLayout = (TabLayout) findViewById(R.id.slidingTabs);
        tabLayout.setupWithViewPager(mViewPager);
*/

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                ActionBar actionBar = getActionBar();
                if (actionBar != null) {
                    actionBar.setSelectedNavigationItem(position);
                }
            }
        });

        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            ActionBar.TabListener tabListener = new ActionBar.TabListener() {
                @Override
                public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                    mViewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

                }

                @Override
                public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

                }
            };

            actionBar.addTab(actionBar.newTab().setTabListener(tabListener));
            actionBar.addTab(actionBar.newTab().setTabListener(tabListener));
        }
    }

    public boolean isValidInput() {
        List<Fragment> fragments = mPagerAdapter.getFragments();
        ServProvSignUpFragment f1 = (ServProvSignUpFragment) fragments.get(0);
        ServProvWorkPlaceFragment f2 = (ServProvWorkPlaceFragment) fragments.get(1);
        return (f1.isValidInput(mViewPager) && f2.isValidInput());
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

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
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
        } catch (Exception e) {
            Utility.showMessage(this, R.string.some_error);
        }
    }*/

    /*
        public void showAddWorkPlaceScreen(View v) {
            if (!isValidInput()) {
                return;
            }
    */
/*
        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        showProgress(true);
        mAuthTask = new UserLoginTask(email, password);
        mAuthTask.execute((Void) null);
*//*

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

    */
/*public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new com.extenprise.mapp.util.DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }*//*


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
*/

    public void saveData() {
        ServProvSignUpFragment fragment = (ServProvSignUpFragment) mPagerAdapter.getItem(0);
        fragment.saveData();
        ServProvWorkPlaceFragment fragment2 = (ServProvWorkPlaceFragment) mPagerAdapter.getItem(1);
        fragment2.saveData();
    }

    public void captureImage(View view) {
        ServProvSignUpFragment fragment = (ServProvSignUpFragment) mPagerAdapter.getItem(0);
        fragment.captureImage(view);
    }

    public void enlargeImg(View view) {
        ServProvSignUpFragment fragment = (ServProvSignUpFragment) mPagerAdapter.getItem(0);
        fragment.enlargeImg(view);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        ServProvWorkPlaceFragment fragment = (ServProvWorkPlaceFragment) mPagerAdapter.getItem(1);
        fragment.onPrepareDialog(id, dialog);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        ServProvWorkPlaceFragment fragment = (ServProvWorkPlaceFragment) mPagerAdapter.getItem(1);
        return fragment.onCreateDialog(id);
    }

    public void showtimeFields(View view) {
        ServProvWorkPlaceFragment fragment = (ServProvWorkPlaceFragment) mPagerAdapter.getItem(1);
        fragment.showtimeFields(view);
    }

    public void showFeeFields(View view) {
        ServProvWorkPlaceFragment fragment = (ServProvWorkPlaceFragment) mPagerAdapter.getItem(1);
        fragment.showFeeFields(view);
    }

    public void showDaysFields(View view) {
        ServProvWorkPlaceFragment fragment = (ServProvWorkPlaceFragment) mPagerAdapter.getItem(1);
        fragment.showDaysFields(view);
    }

    public void showWorkFields(View view) {
        ServProvWorkPlaceFragment fragment = (ServProvWorkPlaceFragment) mPagerAdapter.getItem(1);
        fragment.showWorkFields(view);
    }

    public void showStartTimePicker(View view) {
        ServProvWorkPlaceFragment fragment = (ServProvWorkPlaceFragment) mPagerAdapter.getItem(1);
        fragment.showStartTimePicker(view);
    }

    public void showEndTimePicker(View view) {
        ServProvWorkPlaceFragment fragment = (ServProvWorkPlaceFragment) mPagerAdapter.getItem(1);
        fragment.showEndTimePicker(view);
    }

    public void addNewWorkPlace(View view) {
        ServProvWorkPlaceFragment fragment = (ServProvWorkPlaceFragment) mPagerAdapter.getItem(1);
        fragment.addNewWorkPlace(view);
    }

    public void registerDone(View view) {
        ServProvWorkPlaceFragment fragment = (ServProvWorkPlaceFragment) mPagerAdapter.getItem(1);
        fragment.registerDone(view);
    }
}
