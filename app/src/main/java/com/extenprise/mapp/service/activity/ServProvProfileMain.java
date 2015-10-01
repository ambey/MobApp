package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.service.data.ServProvHasServHasServPt;
import com.extenprise.mapp.service.data.ServProvHasService;
import com.extenprise.mapp.service.data.ServicePoint;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.SearchServProv;
import com.extenprise.mapp.util.UIUtility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;


public class ServProvProfileMain extends Activity {

    private EditText mMobNo, mEmailID, mRegNo;
    private TextView mDocName;
    private EditText mFname, mLname, mQualification, mExp;
    private RadioGroup mGender;
    private RadioButton mMale, mFemale, mGenderBtn;
    private RelativeLayout mPersonalInfo, mWorkPlaceInfo;
    private ListView listView;
    private View mFormView;
    private View mProgressView;
    private ImageView mImgView;

    private static int RESULT_LOAD_IMG = 1;
    private static int REQUEST_CAMERA = 2;
    private String imgDecodableString;
    private Bitmap mImgCopy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serv_prov_profile_main);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //LoginHolder.spsspt = SearchServProv.getSPSSPT(new MappDbHelper(getApplicationContext()));

        mFormView = findViewById(R.id.updateServProvform);
        mProgressView = findViewById(R.id.progressView);

        mPersonalInfo = (RelativeLayout) findViewById(R.id.personalInfo);
        mWorkPlaceInfo = (RelativeLayout) findViewById(R.id.workPlaceInfo);

        mMobNo = (EditText) findViewById(R.id.editTextMobNum);
        mEmailID = (EditText) findViewById(R.id.editTextEmail);
        mGender = (RadioGroup) findViewById(R.id.radioGroupGender);
        mRegNo = (EditText) findViewById(R.id.editTextRegNum);
        mDocName = (TextView) findViewById(R.id.textviewDocname);
        mFname = (EditText) findViewById(R.id.textViewFName);
        mLname = (EditText) findViewById(R.id.textViewLName);
        mQualification = (EditText) findViewById(R.id.textViewEducation);
        mExp = (EditText) findViewById(R.id.textViewWorkExp);
        mMale = (RadioButton) findViewById(R.id.radioButtonMale);
        mFemale = (RadioButton) findViewById(R.id.radioButtonFemale);
        mImgView = (ImageView) findViewById(R.id.imageViewDoctor);
        listView = (ListView) findViewById(R.id.workDetailListView);

        showPersonalInfo();
        showWorkPlaceList();

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

    private void showPersonalInfo() {
        mMobNo.setText(LoginHolder.servLoginRef.getPhone());
        mEmailID.setText(LoginHolder.servLoginRef.getEmailId());
        mRegNo.setText(LoginHolder.servLoginRef.getRegNo());
        mDocName.setText(LoginHolder.servLoginRef.getfName() + " " +
                LoginHolder.servLoginRef.getlName());
        mFname.setText(LoginHolder.servLoginRef.getfName());
        mLname.setText(LoginHolder.servLoginRef.getlName());
        mQualification.setText(LoginHolder.servLoginRef.getQualification());

        if(LoginHolder.servLoginRef.getGender().equals("Male")) {
            mMale.setSelected(true);
        } else {
            mFemale.setSelected(true);
        }
        Cursor cursor = SearchServProv.getCursor();
        mExp.setText(cursor.getString(cursor.getColumnIndex(MappContract.ServProvHasServ.COLUMN_NAME_EXPERIENCE)));
    }

    private void showWorkPlaceList() {

        final Cursor cursor = SearchServProv.getCursor();

        String[] values = new String[] {
                MappContract.ServProvHasServ.COLUMN_NAME_SERVICE_CATAGORY,
                MappContract.ServProvHasServ.COLUMN_NAME_SPECIALITY,
                MappContract.ServiceProvider.COLUMN_NAME_QUALIFICATION,
                MappContract.ServProvHasServ.COLUMN_NAME_EXPERIENCE,
                MappContract.ServicePoint.COLUMN_NAME_NAME,
                MappContract.ServProvHasServHasServPt.COLUMN_NAME_SERVICE_POINT_TYPE,
                MappContract.ServicePoint.COLUMN_NAME_LOCATION,
                MappContract.ServicePoint.COLUMN_NAME_ID_CITY,
                MappContract.ServicePoint.COLUMN_NAME_PHONE,
                MappContract.ServicePoint.COLUMN_NAME_ALT_PHONE,
                MappContract.ServicePoint.COLUMN_NAME_EMAIL_ID,
                MappContract.ServProvHasServHasServPt.COLUMN_NAME_START_TIME,
                MappContract.ServProvHasServHasServPt.COLUMN_NAME_END_TIME,
                MappContract.ServProvHasServHasServPt.COLUMN_NAME_WEEKLY_OFF,
                MappContract.ServProvHasServHasServPt.COLUMN_NAME_CONSULTATION_FEE
        };

        int[] viewIds = new int[] {
                R.id.spinServiceProvCategory,
                R.id.editTextSpeciality,
                R.id.editTextQualification,
                R.id.editTextExperience,
                R.id.editTextName,
                R.id.viewWorkPlaceType,
                R.id.editTextLoc,
                R.id.editTextCity,
                R.id.editTextPhone1,
                R.id.editTextPhone2,
                R.id.editTextEmail,
                R.id.buttonStartTime,
                R.id.buttonEndTime,
                R.id.editTextWeeklyOff,
                R.id.editTextConsultationFees
        };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.activity_servprov_wrkdetail_list,
                cursor,
                values,
                viewIds, 0){
            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {

                View view =super.getView(position, convertView, parent);
                cursor.moveToPosition(position);

                TextView mTextViewEdit = (TextView) findViewById(R.id.textViewEditWrkDetail);
                TextView mViewEdit = (TextView) findViewById(R.id.viewEditWrkDetail);

                mTextViewEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editWorkPlaceInfo();
                    }
                });
                return view;
            }
        };


        listView.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);
        listView.setAdapter(adapter);
    }

    public void editPersonalInfo(View view) {
        mFname.setEnabled(true);
        mLname.setEnabled(true);
        //mMobNo.setEnabled(true);
        mEmailID.setEnabled(true);
        mQualification.setEnabled(true);
        mExp.setEnabled(true);
        mRegNo.setEnabled(true);
        mMale.setEnabled(true);
        mFemale.setEnabled(true);

        openPersonalInfo(view);
    }

    public void editWorkPlaceInfo() {

        int[] editTxtIds = new int[] {
                R.id.editTextQualification,
                R.id.editTextExperience,
                R.id.editTextName,
                R.id.editTextLoc,
                R.id.editTextPhone1,
                R.id.editTextPhone2,
                R.id.editTextEmail,
                R.id.editTextConsultationFees
        };
        for (int editTxtId : editTxtIds) {
            EditText txt = (EditText) findViewById(editTxtId);
            txt.setEnabled(true);
        }

        int[] buttonIds = new int[] {
                R.id.buttonStartTime,
                R.id.buttonEndTime,
                R.id.editTextWeeklyOff
        };
        for (int buttonId : buttonIds) {
            Button btn = (Button) findViewById(buttonId);
            btn.setEnabled(true);
        }

        int[] spinnerIds = new int[] {
                R.id.spinServiceProvCategory,
                R.id.editTextSpeciality,
                R.id.viewWorkPlaceType,
                R.id.editTextCity
        };
        for (int spinnerId : spinnerIds) {
            Button btn = (Button) findViewById(spinnerId);
            btn.setEnabled(true);
        }
    }

    public void addNewWorkPlace(View view) {
        SimpleCursorAdapter adapter = (SimpleCursorAdapter) listView.getAdapter();
        adapter.changeCursorAndColumns( SearchServProv.getCursor(), new String[]{}, new int[]{});

        //TO DO

        //adapter.add("another row");

        //ListAdapter adp = listView.getAdapter();



        /*MatrixCursor extras = new MatrixCursor(new String[] { "_id", "title" });
        extras.addRow(new String[] { "-1", "New Template" });
        extras.addRow(new String[] { "-2", "Empty Template" });
        Cursor[] cursors = { extras, SearchServProv.getCursor() };
        Cursor extendedCursor = new MergeCursor(cursors);
*/
        adapter.notifyDataSetChanged();


    }

    public void removeWorkPlace(View view) {
        SimpleCursorAdapter adapter = (SimpleCursorAdapter) listView.getAdapter();
        adapter.changeCursorAndColumns( SearchServProv.getCursor(), new String[]{}, new int[]{});

        //TO DO

        //adapter.add("another row");

        //ListAdapter adp = listView.getAdapter();



        /*MatrixCursor extras = new MatrixCursor(new String[] { "_id", "title" });
        extras.addRow(new String[] { "-1", "New Template" });
        extras.addRow(new String[] { "-2", "Empty Template" });
        Cursor[] cursors = { extras, SearchServProv.getCursor() };
        Cursor extendedCursor = new MergeCursor(cursors);
*/
        adapter.notifyDataSetChanged();


    }

    public void updateProfile(View view) {

        SimpleCursorAdapter adapter = (SimpleCursorAdapter) listView.getAdapter();
        int count = adapter.getCount();

        //TO DO

        UIUtility.showProgress(this, mFormView, mProgressView, true);
        int uTypeID = mGender.getCheckedRadioButtonId();
        mGenderBtn = (RadioButton)findViewById(uTypeID);
        SaveServiceData task = new SaveServiceData(this);
        task.execute((Void) null);
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

    public void changeImage(View view) {

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

    public void openPersonalInfo(View view) {
        if(mPersonalInfo.getVisibility() == View.VISIBLE) {
            mPersonalInfo.setVisibility(View.GONE);
        } else {
            mPersonalInfo.setVisibility(View.VISIBLE);
            if(mWorkPlaceInfo.getVisibility() == View.VISIBLE) {
                mWorkPlaceInfo.setVisibility(View.GONE);
            }
        }
    }

    public void openWorkPlaceInfo(View view) {
        if(mWorkPlaceInfo.getVisibility() == View.VISIBLE) {
            mWorkPlaceInfo.setVisibility(View.GONE);
        } else {
            mWorkPlaceInfo.setVisibility(View.VISIBLE);
            if(mPersonalInfo.getVisibility() == View.VISIBLE) {
                mPersonalInfo.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_serv_prove_profile, menu);
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

    class SaveServiceData extends AsyncTask<Void, Void, Void> {

        private Activity myActivity;

        public SaveServiceData(Activity activity) {
            myActivity = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ServiceProvider sp = LoginHolder.servLoginRef; // TO DO
            ArrayList<ServProvHasService> spsList = sp.getServices();

            if (spsList == null) {
                return null;
            }
            String where = MappContract.ServiceProvider._ID + " = ? ";
            String[] selectionArgs = {
                    "" + LoginHolder.servLoginRef.getIdServiceProvider()
            };

            MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(MappContract.ServiceProvider.COLUMN_NAME_FNAME, mFname.getText().toString());
            values.put(MappContract.ServiceProvider.COLUMN_NAME_LNAME, mLname.getText().toString());
            values.put(MappContract.ServiceProvider.COLUMN_NAME_QUALIFICATION, mQualification.getText().toString());
            values.put(MappContract.ServiceProvider.COLUMN_NAME_GENDER, mGenderBtn.getText().toString());
            values.put(MappContract.ServiceProvider.COLUMN_NAME_REGISTRATION_NUMBER, mRegNo.getText().toString());
            values.put(MappContract.ServiceProvider.COLUMN_NAME_EMAIL_ID, mEmailID.getText().toString());

            db.update(MappContract.ServiceProvider.TABLE_NAME, values, where, selectionArgs);

            for (ServProvHasService sps : spsList) {
                values = new ContentValues();
                values.put(MappContract.ServProvHasServ.COLUMN_NAME_SPECIALITY, sps.getService().getSpeciality());
                values.put(MappContract.ServProvHasServ.COLUMN_NAME_SERVICE_CATAGORY, sps.getService().getServCatagory());
                values.put(MappContract.ServProvHasServ.COLUMN_NAME_EXPERIENCE, sps.getExperience());

                //long spsId = db.insert(MappContract.ServProvHasServ.TABLE_NAME, null, values);
                where = MappContract.ServProvHasServ.COLUMN_NAME_ID_SERV_PROV + " = ? ";
                long spsId = db.update(MappContract.ServProvHasServ.TABLE_NAME, values, where, selectionArgs);

                ArrayList<ServProvHasServHasServPt> spssptList = sps.getServProvHasServHasServPts();
                for (ServProvHasServHasServPt spsspt : spssptList) {
                    ServicePoint spt = spsspt.getServicePoint();

                    values = new ContentValues();
                    values.put(MappContract.ServicePoint.COLUMN_NAME_NAME, spt.getName());
                    values.put(MappContract.ServicePoint.COLUMN_NAME_LOCATION, spt.getLocation());
                    values.put(MappContract.ServicePoint.COLUMN_NAME_PHONE, spt.getPhone());
                    values.put(MappContract.ServicePoint.COLUMN_NAME_ID_CITY, spt.getCity());

                    long sptId = db.insert(MappContract.ServicePoint.TABLE_NAME, null, values);



                    values = new ContentValues();
                    try {
                        values.put(MappContract.ServProvHasServHasServPt.COLUMN_NAME_START_TIME, spsspt.getStartTime());
                        values.put(MappContract.ServProvHasServHasServPt.COLUMN_NAME_END_TIME, spsspt.getEndTime());
                    } catch (Exception x) {
                        x.printStackTrace();
                    }
                    values.put(MappContract.ServProvHasServHasServPt.COLUMN_NAME_SERVICE_POINT_TYPE, spsspt.getServPointType());
                    values.put(MappContract.ServProvHasServHasServPt.COLUMN_NAME_CONSULTATION_FEE, spsspt.getConsultFee());
                    values.put(MappContract.ServProvHasServHasServPt.COLUMN_NAME_WEEKLY_OFF, spsspt.getWeeklyOff());

                    where = MappContract.ServProvHasServHasServPt.COLUMN_NAME_ID_SERV_PROV_HAS_SERV + " = ? and " +
                    MappContract.ServProvHasServHasServPt.COLUMN_NAME_ID_SERV_PT + " = ? ";
                    String[] args = {
                            "" + spsId, "" + sptId
                    };
                    db.update(MappContract.ServProvHasServHasServPt.TABLE_NAME, values, where, args);
                    //db.insert(MappContract.ServProvHasServHasServPt.TABLE_NAME, null, values);

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            UIUtility.showRegistrationAlert(myActivity, "", "Profile updated.");
            UIUtility.showProgress(myActivity, mFormView, mProgressView, false);
            //Intent intent = new Intent(myActivity, LoginActivity.class);
            //startActivity(intent);
        }

        @Override
        protected void onCancelled() {
            UIUtility.showProgress(myActivity, mFormView, mProgressView, false);
        }

    }
}
