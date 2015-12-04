package com.extenprise.mapp.service.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.activity.LoginActivity;
import com.extenprise.mapp.data.SignInData;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.net.AppStatus;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.MappServiceConnection;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.data.SearchServProvForm;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.service.data.WorkPlace;
import com.extenprise.mapp.service.ui.WorkPlaceListAdapter;
import com.extenprise.mapp.util.SearchServProv;
import com.extenprise.mapp.util.Utility;
import com.extenprise.mapp.util.Validator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;


public class ServProvProfileActivity extends Activity implements ResponseHandler {

    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));

    private ArrayList<WorkPlace> mWorkPlaceList;
    private WorkPlace mWorkPlace;
    private ServiceProvider mServiceProv;
    private SignInData mSignInData;

    private TextView mMobNo, mEmailID, mRegNo, mFname, mLname, mGenderTextView;
    private TextView mDocName, workhourLBL, mViewdrLbl;
    private RadioGroup mGender;
    private RadioButton mMale, mFemale, mGenderBtn;
    private RelativeLayout mPersonalInfo, mWorkPlaceInfo;
    private ListView listView;
    private View mFormView;
    private View mProgressView;
    private ImageView mImgView;

    private static int RESULT_LOAD_IMG = 1;
    private static int REQUEST_CAMERA = 2;
    private Bitmap mImgCopy;

    private EditText mName;
    private EditText mLoc;
    private Spinner mCity;
    private EditText mPhone1;
    private EditText mPhone2;
    private EditText mEmailIdwork;
    private EditText mConsultFee;
    private Spinner mServPtType;
    private Spinner mServCatagory;
    private Button mStartTime;
    private Button mEndTime;
    private Spinner mSpeciality;
    private EditText mExperience;
    private EditText mQualification;
    private EditText mPinCode;
    private Spinner mState;

    private Button mMultiSpinnerDays;
    protected CharSequence[] options;
    protected boolean[] selections;
    //String []selectedDays = new String[_options.length];
    private String selectedDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serv_prov_profile_main);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        options = Utility.getDaysOptions(this);
        selections = new boolean[options.length];

        mWorkPlace = new WorkPlace();
        mWorkPlaceList = new ArrayList<>();
        mServiceProv = new ServiceProvider();
        mSignInData = new SignInData();
        mServiceProv = LoginHolder.servLoginRef;
        mSignInData.setPhone(LoginHolder.servLoginRef.getPhone());
        mWorkPlace.setSignInData(mSignInData);

        mFormView = findViewById(R.id.updateServProvform);
        mProgressView = findViewById(R.id.progressView);
        mPersonalInfo = (RelativeLayout) findViewById(R.id.personalInfo);
        mWorkPlaceInfo = (RelativeLayout) findViewById(R.id.workPlaceInfo);

        mFname = (TextView) findViewById(R.id.textViewFName);
        mLname = (TextView) findViewById(R.id.textViewLName);
        mGenderTextView = (TextView) findViewById(R.id.viewGenderLabel);
        mMobNo = (TextView) findViewById(R.id.editTextMobNum);
        mEmailID = (TextView) findViewById(R.id.editTextEmail);
        mRegNo = (TextView) findViewById(R.id.editTextRegNum);

        mGender = (RadioGroup) findViewById(R.id.radioGroupGender);
        mViewdrLbl = (TextView) findViewById(R.id.viewdrLbl);
        mDocName = (TextView) findViewById(R.id.textviewDocname);
        mMale = (RadioButton) findViewById(R.id.radioButtonMale);
        mFemale = (RadioButton) findViewById(R.id.radioButtonFemale);
        mImgView = (ImageView) findViewById(R.id.imageViewDoctor);
        listView = (ListView) findViewById(R.id.workDetailListView);

        Intent intent = getIntent();
        String category = intent.getStringExtra("category");
        if(category.equals("Pharmacist")) {
            mViewdrLbl.setText(getString(R.string.welcome));
            mImgView.setImageResource(R.drawable.medstore);
        }
        if (savedInstanceState != null) {
            Bitmap bitmap = savedInstanceState.getParcelable("image");
            mImgView.setImageBitmap(bitmap);
        } else {
            mImgCopy = (Bitmap) getLastNonConfigurationInstance();
            if (mImgCopy != null) {
                mImgView.setImageBitmap(mImgCopy);
            }
        }
        mDocName.setText(String.format("%s %s", mServiceProv.getfName(), mServiceProv.getlName()));
        if (mServiceProv.getImg() != null) {
            mImgView.setImageBitmap(Utility.getBitmapFromBytes(LoginHolder.servLoginRef.getImg()));
        }
        mFname.setText(mFname.getText().toString() + " : " + mServiceProv.getfName());
        mLname.setText(mLname.getText().toString() + " : " + mServiceProv.getlName());
        mMobNo.setText(mMobNo.getText().toString() + " : " + mSignInData.getPhone());
        String email = getString(R.string.not_specified);
        if(mServiceProv.getEmailId() != null) {
            email = mServiceProv.getEmailId();
        }
        mEmailID.setText(mEmailID.getText().toString() + " : " + email);
        mRegNo.setText(mRegNo.getText().toString() + " : " + mServiceProv.getRegNo());
        mGenderTextView.setText(mGenderTextView.getText().toString() + " : " + mServiceProv.getGender());

        //Get work place list from server
        Utility.showProgress(this, mFormView, mProgressView, true);
        Bundle bundle = new Bundle();
        bundle.putInt("loginType", MappService.SERVICE_LOGIN);
        bundle.putParcelable("workPlace", mWorkPlace);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_WORK_PLACE_LIST);
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
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

    private void showWorkPlaceList() {
        final Cursor cursor = SearchServProv.getCursor();
        String[] values = new String[]{
                MappContract.Service.COLUMN_NAME_SERVICE_CATAGORY,
                MappContract.Service.COLUMN_NAME_SERVICE_NAME,
                MappContract.ServiceProvider.COLUMN_NAME_QUALIFICATION,
                MappContract.ServProvHasServPt.COLUMN_NAME_EXP,
                MappContract.ServicePoint.COLUMN_NAME_NAME,
                MappContract.ServProvHasServPt.COLUMN_NAME_SERVICE_POINT_TYPE,
                MappContract.ServicePoint.COLUMN_NAME_LOCATION,
                MappContract.ServicePoint.COLUMN_NAME_ID_CITY,
                MappContract.ServicePoint.COLUMN_NAME_PHONE,
                MappContract.ServicePoint.COLUMN_NAME_ALT_PHONE,
                MappContract.ServicePoint.COLUMN_NAME_EMAIL_ID,
                MappContract.ServProvHasServPt.COLUMN_NAME_START_TIME,
                MappContract.ServProvHasServPt.COLUMN_NAME_END_TIME,
                MappContract.ServProvHasServPt.COLUMN_NAME_WORKING_DAYS,
                MappContract.ServProvHasServPt.COLUMN_NAME_CONSULTATION_FEE
        };
        int[] viewIds = new int[]{
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
                viewIds, 0) {
            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {

                View view = super.getView(position, convertView, parent);
                cursor.moveToPosition(position);

                TextView mTextViewEdit = (TextView) findViewById(R.id.textViewEditWrkDetail);
                TextView mViewEdit = (TextView) findViewById(R.id.viewEditWrkDetail);

                mTextViewEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editWorkPlaceInfo(v);
                    }
                });

                return view;
            }
        };
        listView.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);
        listView.setAdapter(adapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.workDetailListView) {
            //menu.setHeaderTitle(R.string.work_place_details);
            menu.add(0, v.getId(), 0, getString(R.string.edit));
            menu.add(0, v.getId(), 0, getString(R.string.remove));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final WorkPlace wp = (WorkPlace) listView.getItemAtPosition(info.position);
        wp.setSignInData(mSignInData);
        if (item.getTitle() == getString(R.string.edit)) {
            getWorkPlaceView(wp);
            //editWorkPlaceInfo(item.getActionView());
            return true;
        } else if (item.getTitle() == getString(R.string.remove)) {
            //ArrayAdapter adapter = (ArrayAdapter) listView.getAdapter();
            //this.resultsList.remove((int) info.id);
            //adapter.removeItem(adapter.getItem(position));
            //adapter.remove(item);
            //item.getActionView().setVisibility(View.GONE);
            /*if(Utility.confirm(this, R.string.confirm_remove_workplace)) {*/
            final AlertDialog dialog = Utility.customDialogBuilder(this, null, R.string.confirm_remove_workplace).create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utility.showProgress(ServProvProfileActivity.this, mFormView, mProgressView, true);
                    //mWorkPlace = mWorkPlaceList.get(item.getItemId());
                    Bundle bundle = new Bundle();
                    bundle.putInt("loginType", MappService.SERVICE_LOGIN);
                    bundle.putParcelable("workPlace", wp);
                    mConnection.setData(bundle);
                    mConnection.setAction(MappService.DO_REMOVE_WORK_PLACE);
                    Utility.doServiceAction(ServProvProfileActivity.this, mConnection, BIND_AUTO_CREATE);
                    dialog.dismiss();
                }
            });
            return true;
        }
        return super.onContextItemSelected(item);
    }

    /*@Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        if (!AdapterView.AdapterContextMenuInfo.class.isInstance (item.getMenuInfo ())) {
            return false;
        }
        AdapterView.AdapterContextMenuInfo cmi =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        mWorkPlace = (WorkPlace) listView.getItemAtPosition(cmi.position);

        try {
            if (item.getItemId() == R.string.edit1) {
                getWorkPlaceView(mWorkPlace).show();
                return true;
            } else if (item.getItemId() == R.string.remove) {
                if(Utility.confirm(this, R.string.confirm_remove_workplace)) {
                    Utility.showProgress(this, mFormView, mProgressView, true);
                    Bundle bundle = new Bundle();
                    bundle.putInt("loginType", MappService.SERVICE_LOGIN);
                    bundle.putParcelable("mWorkPlace", mWorkPlace);
                    mConnection.setData(bundle);
                    mConnection.setAction(MappService.REMOVE_WORK_PLACE);
                    Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
                    return true;
                }
            }
        }
        catch (Exception error) {
            Utility.showMessage(this, R.string.some_error);
            return false;
        }
        return super.onMenuItemSelected(featureId, item);
    }*/

    public void showtimeFields(View view) {
        int[] buttonIds = new int[]{
                R.id.buttonStartTime,
                R.id.buttonEndTime,
        };
        for (int buttonId : buttonIds) {
            Button btn = (Button) findViewById(buttonId);
            if (btn.getVisibility() == View.GONE) {
                Utility.expand(btn, view);
            } else {
                Utility.collapse(btn, view);
            }
        }
    }

    private void enability(boolean set) {
        mFname.setEnabled(set);
        mLname.setEnabled(set);
        mEmailID.setEnabled(set);
        mRegNo.setEnabled(set);
        mMale.setEnabled(set);
        mFemale.setEnabled(set);
        mMobNo.setEnabled(false);
    }

    public void editPersonalInfo(View view) {
        /*boolean set = true;
        if(mFname.isEnabled()) {
            set = false;
        }
        enability(set);*/
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_personal_info, null);

        dialogView.findViewById(R.id.editTextCellphone).setVisibility(View.GONE);
        dialogView.findViewById(R.id.editTextPasswd).setVisibility(View.GONE);
        dialogView.findViewById(R.id.editTextCnfPasswd).setVisibility(View.GONE);

        mFname = (EditText) dialogView.findViewById(R.id.editTextFName);
        mLname = (EditText) dialogView.findViewById(R.id.editTextLName);
        mEmailID = (EditText) dialogView.findViewById(R.id.editTextEmail);
        mGender = (RadioGroup) dialogView.findViewById(R.id.radioGroupGender);
        mMale = (RadioButton) dialogView.findViewById(R.id.radioButtonMale);
        mFemale = (RadioButton) dialogView.findViewById(R.id.radioButtonFemale);
        mRegNo = (EditText) dialogView.findViewById(R.id.editTextRegistrationNumber);
        mRegNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String regNo = mRegNo.getText().toString().trim();
                    if (!TextUtils.isEmpty(regNo)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("loginType", MappService.SERVICE_LOGIN);
                        bundle.putString("regno", mRegNo.getText().toString().trim());
                        bundle.putParcelable("service", mServiceProv);
                        mConnection.setData(bundle);
                        mConnection.setAction(MappService.DO_REG_NO_CHECK);
                        Utility.doServiceAction(ServProvProfileActivity.this, mConnection, BIND_AUTO_CREATE);
                    }
                }
            }
        });

        mFname.setText(mServiceProv.getfName());
        mLname.setText(mServiceProv.getlName());
        String email = getString(R.string.not_specified);
        if(mServiceProv.getEmailId() != null) {
            email = mServiceProv.getEmailId();
        }
        mEmailID.setText(email);
        mRegNo.setText(mServiceProv.getRegNo());
        if (mServiceProv.getGender().equalsIgnoreCase("Male")) {
            mMale.setChecked(true);
        } else {
            mFemale.setChecked(true);
        }

        final AlertDialog dialog = Utility.customDialogBuilder(this, dialogView, R.string.personalDetails).create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean cancel = false;
                View focusView = null;

                String fnm = mFname.getText().toString().trim();
                String lnm = mLname.getText().toString().trim();
                String email = mEmailID.getText().toString().trim();
                String regNo = mRegNo.getText().toString();

                if (TextUtils.isEmpty(mLname.getText().toString())) {
                    mLname.setError(getString(R.string.error_field_required));
                    focusView = mLname;
                    cancel = true;
                }
                if (TextUtils.isEmpty(mFname.getText().toString())) {
                    mFname.setError(getString(R.string.error_field_required));
                    focusView = mFname;
                    cancel = true;
                }
                if (!TextUtils.isEmpty(mEmailID.getText().toString())) {
                    if (!Validator.isValidEmaillId(mEmailID.getText().toString().trim())) {
                        mEmailID.setError(getString(R.string.error_invalid_email));
                        focusView = mEmailID;
                        cancel = true;
                    }
                }
                int genderID = mGender.getCheckedRadioButtonId();
                if (genderID == -1) {
                    mFemale.setError(getString(R.string.error_select_gender));
                    focusView = mFemale;
                    cancel = true;
                } else {
                    mGenderBtn = (RadioButton) dialogView.findViewById(genderID);
                }
                if (TextUtils.isEmpty(regNo)) {
                    mRegNo.setError(getString(R.string.error_field_required));
                    focusView = mRegNo;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                    return;
                }
                mServiceProv.setfName(fnm);
                mServiceProv.setlName(lnm);
                mServiceProv.setEmailId(email);
                mServiceProv.setGender(mGenderBtn.getText().toString());
                mServiceProv.setRegNo(regNo);
                mServiceProv.setSignInData(mSignInData);

                Utility.showProgress(ServProvProfileActivity.this, mFormView, mProgressView, true);
                Bundle bundle = new Bundle();
                bundle.putInt("loginType", MappService.SERVICE_LOGIN);
                bundle.putString("regno", regNo);
                bundle.putParcelable("service", mServiceProv);
                mConnection.setData(bundle);
                mConnection.setAction(MappService.DO_UPDATE);
                Utility.doServiceAction(ServProvProfileActivity.this, mConnection, BIND_AUTO_CREATE);
                dialog.dismiss();
            }
        });
    }

    public void editWorkPlaceInfo(View v) {
        boolean set = true;
        EditText nm = (EditText) v.findViewById(R.id.editTextName);
        if (nm.isEnabled()) {
            set = false;
        }
        int[] editTxtIds = new int[]{
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
            EditText txt = (EditText) v.findViewById(editTxtId);
            txt.setEnabled(set);
        }

        int[] buttonIds = new int[]{
                R.id.buttonStartTime,
                R.id.buttonEndTime,
                R.id.editTextWeeklyOff
        };
        for (int buttonId : buttonIds) {
            Button btn = (Button) v.findViewById(buttonId);
            btn.setEnabled(set);
            btn.setClickable(true);
        }

        int[] spinnerIds = new int[]{
                R.id.spinServiceProvCategory,
                R.id.editTextSpeciality,
                R.id.viewWorkPlaceType,
                R.id.editTextCity
        };
        for (int spinnerId : spinnerIds) {
            Spinner spn = (Spinner) v.findViewById(spinnerId);
            spn.setEnabled(set);
            spn.setClickable(true);
        }
    }

    public void openPersonalInfo(View view) {
        if (mPersonalInfo.getVisibility() == View.VISIBLE) {
            //Utility.collapse(mPersonalInfo, null);
            mPersonalInfo.setVisibility(View.GONE);
        } else {
            //Utility.expand(mPersonalInfo, null);
            mPersonalInfo.setVisibility(View.VISIBLE);
            if (mWorkPlaceInfo.getVisibility() == View.VISIBLE) {
                //Utility.collapse(mWorkPlaceInfo, null);
                mWorkPlaceInfo.setVisibility(View.GONE);
            }
        }
    }

    public void openWorkPlaceInfo(View view) {
        if (mWorkPlaceInfo.getVisibility() == View.VISIBLE) {
            //Utility.collapse(mWorkPlaceInfo, null);
            mWorkPlaceInfo.setVisibility(View.GONE);
        } else {
            mWorkPlaceInfo.setVisibility(View.VISIBLE);
            //Utility.expand(mWorkPlaceInfo, null);
            if (mPersonalInfo.getVisibility() == View.VISIBLE) {
                //Utility.collapse(mPersonalInfo, null);
                mPersonalInfo.setVisibility(View.GONE);
            }
        }
    }


    ///////////////////////////Multi spinner..../////////////////////////////

    public class ButtonClickHandler implements View.OnClickListener {
        public void onClick(View view) {
            if (!mMultiSpinnerDays.getText().equals(getString(R.string.select_days))) {
                setupSelection();
            }
            showDialog(0);
        }
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        if (!mMultiSpinnerDays.getText().equals(getString(R.string.select_days))) {
            setupSelection();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        return new AlertDialog.Builder(this)
                .setTitle("Available Days")
                .setMultiChoiceItems(options, selections, new DialogSelectionClickHandler())
                .setPositiveButton("OK", new DialogButtonClickHandler())
                .create();
    }

    public class DialogSelectionClickHandler implements DialogInterface.OnMultiChoiceClickListener {
        public void onClick(DialogInterface dialog, int clicked, boolean selected) {
            if (options[clicked].toString().equalsIgnoreCase("All Days")) {
                for (CharSequence option : options) {
                    Log.i("ME", option + " selected: " + selected);
                }
            } else {
                Log.i("ME", options[clicked] + " selected: " + selected);
            }
        }
    }

    public class DialogButtonClickHandler implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int clicked) {
            switch (clicked) {
                case DialogInterface.BUTTON_POSITIVE:
                    printSelectedDays();
                    mMultiSpinnerDays.setText(selectedDays);
                    break;
            }
        }
    }

    protected void printSelectedDays() {
        if (selections[0]) {
            setupAllDaysSelected();
            return;
        }
        int i = 1;
        selectedDays = getString(R.string.select_days);
        for (; i < options.length; i++) {
            Log.i("ME", options[i] + " selected: " + selections[i]);

            if (selections[i]) {
                selectedDays = options[i++].toString();
                break;
            }
        }
        for (; i < options.length; i++) {
            Log.i("ME", options[i] + " selected: " + selections[i]);

            if (selections[i]) {
                selectedDays += "," + options[i].toString();
            }
        }
    }

    private void setupSelection() {
        String[] selectedDays = mMultiSpinnerDays.getText().toString().split(",");
        selections[0] = false;
        for (String d : selectedDays) {
            selections[getDayIndex(d)] = true;
        }
    }

    private int getDayIndex(String day) {
        for (int i = 0; i < options.length; i++) {
            if (day.equals(options[i])) {
                return i;
            }
        }
        return 0;
    }

    private void setupAllDaysSelected() {
        selections[0] = false;
        selectedDays = options[1].toString();
        for (int i = 2; i < options.length; i++) {
            selectedDays += "," + options[i];
        }
    }

    ////////////////////////////////////////////////////////////////////////////


    /////////////////////////////Image Upload/////////////////////////////

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
        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};
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
    }

    ////////////////////////////////////////////////////////////////////////


    ///////////////////////////////Add New Work Place Details/////////////////////////////////


    public void addNewWorkPlace(View view) {
        /*SimpleCursorAdapter adapter = (SimpleCursorAdapter) listView.getAdapter();
        adapter.changeCursorAndColumns( SearchServProv.getCursor(), new String[]{}, new int[]{});

        *//*MatrixCursor extras = new MatrixCursor(new String[] { "_id", "title" });
        extras.addRow(new String[] { "-1", "New Template" });
        extras.addRow(new String[] { "-2", "Empty Template" });
        Cursor[] cursors = { extras, SearchServProv.getCursor() };
        Cursor extendedCursor = new MergeCursor(cursors);
        adapter.notifyDataSetChanged();
*/
        getWorkPlaceView(null);
    }

    private AlertDialog getWorkPlaceView(WorkPlace item) {
        int action = MappService.DO_ADD_WORK_PLACE;

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_servprov_wrkdetail_list, null);

        mName = (EditText) dialogView.findViewById(R.id.editTextName);
        mLoc = (EditText) dialogView.findViewById(R.id.editTextLoc);
        mPhone1 = (EditText) dialogView.findViewById(R.id.editTextPhone1);
        mPhone2 = (EditText) dialogView.findViewById(R.id.editTextPhone2);
        mEmailIdwork = (EditText) dialogView.findViewById(R.id.editTextEmail);
        mConsultFee = (EditText) dialogView.findViewById(R.id.editTextConsultationFees);
        mServPtType = (Spinner) dialogView.findViewById(R.id.viewWorkPlaceType);
        mCity = (Spinner) dialogView.findViewById(R.id.editTextCity);
        mState = (Spinner) dialogView.findViewById(R.id.editTextState);
        mStartTime = (Button) dialogView.findViewById(R.id.buttonStartTime);
        mEndTime = (Button) dialogView.findViewById(R.id.buttonEndTime);
        mSpeciality = (Spinner) dialogView.findViewById(R.id.editTextSpeciality);
        mExperience = (EditText) dialogView.findViewById(R.id.editTextExperience);
        mPinCode = (EditText) dialogView.findViewById(R.id.editTextPinCode);
        mQualification = (EditText) dialogView.findViewById(R.id.editTextQualification);
        mMultiSpinnerDays = (Button) dialogView.findViewById(R.id.editTextWeeklyOff);
        mServCatagory = (Spinner) dialogView.findViewById(R.id.spinServiceProvCategory);
        workhourLBL = (TextView) dialogView.findViewById(R.id.viewWorkHrsLbl);

        if(item != null) {
            action = MappService.DO_EDIT_WORK_PLACE;

            mName.setText(item.getName());
            mLoc.setText(item.getLocation());
            mPhone1.setText(item.getPhone());
            mPhone2.setText(item.getAltPhone());
            mEmailIdwork.setText(item.getEmailId());
            mConsultFee.setText(String.format("%.2f", item.getConsultFee()));
            mServPtType.setSelection(Utility.getSpinnerIndex(mServPtType, item.getServPointType()));
            mCity.setSelection(Utility.getSpinnerIndex(mCity, item.getCity().getCity()));
            mState.setSelection(Utility.getSpinnerIndex(mState, item.getCity().getState()));
            mStartTime.setText(Utility.getTimeString(item.getStartTime()));
            mEndTime.setText(Utility.getTimeString(item.getEndTime()));
            mQualification.setText(item.getQualification());
            mMultiSpinnerDays.setText(item.getWorkingDays());
            mServCatagory.setSelection(Utility.getSpinnerIndex(mServCatagory, item.getServCategory()));
            //mSpeciality.setSelection(Utility.getSpinnerIndex(mServCatagory, item.getSpeciality()));
            mExperience.setText(String.format("%.01f", item.getExperience()));
            if(item.getPincode() != null) {
                mPinCode.setText(item.getPincode());
            }
            ArrayList<String> specs = new ArrayList<>();
            specs.add(item.getSpeciality());
            Utility.setNewSpec(this, specs, mSpeciality);
            mSpeciality.setSelection(Utility.getSpinnerIndex(mSpeciality, item.getSpeciality()));
            /*specs.clear();
            specs.add(item.getServCategory());
            Utility.setNewSpec(this, specs, mServCatagory);*/
        }

        workhourLBL.setClickable(false);
        mSpeciality.setClickable(true);
        mServCatagory.setClickable(true);
        mStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.timePicker(v, mStartTime);
            }
        });
        mEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.timePicker(v, mEndTime);
            }
        });
        mMultiSpinnerDays.setOnClickListener(new ButtonClickHandler());
        mServCatagory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String servCategory = mServCatagory.getSelectedItem().toString();
                /*MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
                DBUtil.setSpecOfCategory(getApplicationContext(), dbHelper, servCategory, mSpeciality);*/
                if(!TextUtils.isEmpty(servCategory) && !servCategory.equals(getString(R.string.select_category))) {
                    Utility.showProgress(getApplicationContext(), mFormView, mProgressView, true);
                    Bundle bundle = new Bundle();
                    bundle.putInt("loginType", MappService.SERVICE_LOGIN);
                    SearchServProvForm mForm = new SearchServProvForm();
                    mForm.setCategory(mServCatagory.getSelectedItem().toString());
                    bundle.putParcelable("form", mForm);
                    mConnection.setData(bundle);
                    mConnection.setAction(MappService.DO_GET_SPECIALITY);
                    Utility.doServiceAction(ServProvProfileActivity.this, mConnection, BIND_AUTO_CREATE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
        mSpeciality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String spec = mSpeciality.getSelectedItem().toString();
                String servCategory = mServCatagory.getSelectedItem().toString();
                if(!TextUtils.isEmpty(servCategory) && !servCategory.equals(getString(R.string.select_category))) {
                    if (spec.equals("Other")) {
                        Utility.openSpecDialog(ServProvProfileActivity.this, mSpeciality);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Utility.showMessage(getApplicationContext(), R.string.no_spec_added);
            }
        });

        final AlertDialog dialog = Utility.customDialogBuilder(this, dialogView, R.string.work_place_details).create();
        dialog.show();

        final int finalAction = action;
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidWorkPlace()) {
                    WorkPlace wp = new WorkPlace();
                    wp.setName(mName.getText().toString().trim());
                    wp.setLocation(mLoc.getText().toString().trim());
                    wp.getCity().setCity(mCity.getSelectedItem().toString());
                    wp.getCity().setState(mState.getSelectedItem().toString());
                    wp.setPhone(mPhone1.getText().toString().trim());
                    wp.setAltPhone(mPhone2.getText().toString().trim());
                    wp.setEmailId(mEmailIdwork.getText().toString().trim());
                    wp.setStartTime(Utility.getMinutes(mStartTime.getText().toString()));
                    wp.setEndTime(Utility.getMinutes(mEndTime.getText().toString()));
                    wp.setWorkingDays(mMultiSpinnerDays.getText().toString());
                    wp.setConsultFee(Float.parseFloat(mConsultFee.getText().toString().trim()));
                    wp.setServCategory(mServCatagory.getSelectedItem().toString());
                    wp.setSpeciality(mSpeciality.getSelectedItem().toString());
                    wp.setServPointType(mServPtType.getSelectedItem().toString());
                    wp.setConsultFee(Float.parseFloat(mConsultFee.getText().toString().trim()));
                    wp.setExperience(Float.parseFloat(mExperience.getText().toString().trim()));
                    wp.setQualification(mQualification.getText().toString().trim());
                    wp.setSignInData(mSignInData);
                    wp.setPincode(mPinCode.getText().toString().trim());

                    Utility.showProgress(getApplicationContext(), mFormView, mProgressView, true);
                    Bundle bundle = new Bundle();
                    bundle.putInt("loginType", MappService.SERVICE_LOGIN);
                    bundle.putParcelable("workPlace", wp);
                    mConnection.setData(bundle);
                    mConnection.setAction(finalAction);
                    Utility.doServiceAction(ServProvProfileActivity.this, mConnection, BIND_AUTO_CREATE);
                    dialog.dismiss();
                }
            }
        });

        return dialog;
    }

    private boolean isValidWorkPlace() {
        boolean valid = true;
        View focusView = null;

        String category = mServCatagory.getSelectedItem().toString();
        if(TextUtils.isEmpty(category) || category.equals(getString(R.string.select_category))) {
            //Utility.showAlert(this, "", "Please select service category.");
            View selectedView = mServCatagory.getSelectedView();
            if (selectedView != null && selectedView instanceof TextView) {
                TextView selectedTextView = (TextView) selectedView;
                String errorString = selectedTextView.getResources().getString(R.string.error_field_required);
                selectedTextView.setError(errorString);
            }
            focusView = mServCatagory;
            valid = false;
        }

        if(mSpeciality.getSelectedItem() != null) {
            String spec = mSpeciality.getSelectedItem().toString();
            if (spec.equalsIgnoreCase("Select Speciality") || spec.equals("Other")) {
                //Utility.showAlert(this, "", "Please select speciality.");
                View selectedView = mSpeciality.getSelectedView();
                if (selectedView != null && selectedView instanceof TextView) {
                    TextView selectedTextView = (TextView) selectedView;
                    String errorString = selectedTextView.getResources().getString(R.string.error_field_required);
                    selectedTextView.setError(errorString);
                }
                focusView = mSpeciality;
                valid = false;
            }
        }
        String exp = mExperience.getText().toString().trim();
        if (TextUtils.isEmpty(exp)) {
            mExperience.setError(getString(R.string.error_field_required));
            focusView = mExperience;
            valid = false;
        } else {
            double exp2 = Double.parseDouble(mExperience.getText().toString());
            if (exp2 < 0 || exp2 > 99) {
                mExperience.setError(getString(R.string.error_invalid_experience));
                focusView = mExperience;
                valid = false;
            }
        }

        String pincode = mPinCode.getText().toString().trim();
        if (TextUtils.isEmpty(pincode)) {
            mPinCode.setError(getString(R.string.error_field_required));
            focusView = mPinCode;
            valid = false;
        }

        String qualification = mQualification.getText().toString().trim();
        if (TextUtils.isEmpty(qualification)) {
            mQualification.setError(getString(R.string.error_field_required));
            focusView = mQualification;
            valid = false;
        }

        String name = mName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            mName.setError(getString(R.string.error_field_required));
            focusView = mName;
            valid = false;
        }
        String location = mLoc.getText().toString().trim();
        if (TextUtils.isEmpty(location)) {
            mLoc.setError(getString(R.string.error_field_required));
            focusView = mLoc;
            valid = false;
        }
        String phone1 = mPhone1.getText().toString().trim();
        if (TextUtils.isEmpty(phone1)) {
            mPhone1.setError(getString(R.string.error_field_required));
            focusView = mPhone1;
            valid = false;
        } else if (!Validator.isPhoneValid(phone1)) {
            mPhone1.setError(getString(R.string.error_invalid_phone));
            focusView = mPhone1;
            valid = false;
        }
        String phone2 = mPhone2.getText().toString().trim();
        if (!TextUtils.isEmpty(phone2) && !Validator.isPhoneValid(phone2)) {
            mPhone2.setError(getString(R.string.error_invalid_phone));
            focusView = mPhone2;
            valid = false;
        }
        String email = mEmailIdwork.getText().toString().trim();
        if (!TextUtils.isEmpty(email) && !Validator.isValidEmaillId(mEmailIdwork.getText().toString())) {
            mEmailIdwork.setError(getString(R.string.error_invalid_email));
            focusView = mEmailIdwork;
            valid = false;
        }
        if (mEndTime.getText().toString().equals(getString(R.string.end_time))) {
            mEndTime.setError(getString(R.string.error_field_required));
            focusView = mEndTime;
            valid = false;
        }
        if (mStartTime.getText().toString().equals(getString(R.string.start_time))) {
            mStartTime.setError(getString(R.string.error_field_required));
            focusView = mStartTime;
            valid = false;
        }
        if (!(mEndTime.getText().toString().equals(getString(R.string.end_time))) &&
                !(mStartTime.getText().toString().equals(getString(R.string.start_time)))) {
            if (Utility.getMinutes(mStartTime.getText().toString()) >= Utility.getMinutes(mEndTime.getText().toString())) {
                mEndTime.setError(getString(R.string.error_endtime));
                focusView = mEndTime;
                valid = false;
            }
        }
        String days = mMultiSpinnerDays.getText().toString();
        if (days.equalsIgnoreCase("Select Days")) {
            mMultiSpinnerDays.setError(getString(R.string.error_field_required));
            focusView = mMultiSpinnerDays;
            valid = false;
        }
        String cosultFee = mConsultFee.getText().toString().trim();
        if (TextUtils.isEmpty(cosultFee)) {
            mConsultFee.setError(getString(R.string.error_field_required));
            focusView = mConsultFee;
            valid = false;
        }


        if (focusView != null) {
            focusView.requestFocus();
        }
        return valid;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////


    //////////////////////////////////////////Update Profile///////////////////////////////////

    public void updateProfile(View view) {
        boolean cancel = false;
        View focusView = null;

        String fnm = mFname.getText().toString().trim();
        String lnm = mLname.getText().toString().trim();
        String email = mEmailID.getText().toString().trim();
        String regNo = mRegNo.getText().toString();

        if (TextUtils.isEmpty(mLname.getText().toString())) {
            mLname.setError(getString(R.string.error_field_required));
            focusView = mLname;
            cancel = true;
        }
        if (TextUtils.isEmpty(mFname.getText().toString())) {
            mFname.setError(getString(R.string.error_field_required));
            focusView = mFname;
            cancel = true;
        }
        if (!TextUtils.isEmpty(mEmailID.getText().toString())) {
            if (!Validator.isValidEmaillId(mEmailID.getText().toString().trim())) {
                mEmailID.setError(getString(R.string.error_invalid_email));
                focusView = mEmailID;
                cancel = true;
            }
        }
        int genderID = mGender.getCheckedRadioButtonId();
        if (genderID == -1) {
            mFemale.setError("Please select Gender.");
            focusView = mFemale;
            cancel = true;
        } else {
            mGenderBtn = (RadioButton) findViewById(genderID);
        }
        if (TextUtils.isEmpty(regNo)) {
            mRegNo.setError(getString(R.string.error_field_required));
            focusView = mRegNo;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return;
        }

        mServiceProv.setImg(Utility.getBytesFromBitmap(mImgView.getDrawingCache()));
        mServiceProv.setfName(fnm);
        mServiceProv.setlName(lnm);
        mServiceProv.setPhone(email);
        mServiceProv.setGender(mGenderBtn.getText().toString());
        mServiceProv.setRegNo(regNo);
        mServiceProv.setSignInData(mSignInData);

        Utility.showProgress(this, mFormView, mProgressView, true);
        Bundle bundle = new Bundle();
        bundle.putInt("loginType", MappService.SERVICE_LOGIN);
        bundle.putString("regno", regNo);
        bundle.putParcelable("service", mServiceProv);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_UPDATE);
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);

        /*SaveServiceData task = new SaveServiceData(this);
        task.execute((Void) null);*/
    }

/*
    class SaveServiceData extends AsyncTask<Void, Void, Void> {

        private Activity myActivity;

        public SaveServiceData(Activity activity) {
            myActivity = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ServiceProvider sp = LoginHolder.servLoginRef; // TO DO
            ArrayList<ServProvHasServPt> spsList = sp.getServices();

            if (spsList == null) {
                return null;
            }
            String where = MappContract.ServiceProvider.COLUMN_NAME_CELLPHONE + " = ? ";
            String[] selectionArgs = {
                    "" + LoginHolder.servLoginRef.getPhone()
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

            for (ServProvHasServPt sps : spsList) {
                ServicePoint spt = sps.getServicePoint();

                values = new ContentValues();
                values.put(MappContract.ServicePoint.COLUMN_NAME_NAME, spt.getName());
                values.put(MappContract.ServicePoint.COLUMN_NAME_LOCATION, spt.getLocation());
                values.put(MappContract.ServicePoint.COLUMN_NAME_PHONE, spt.getPhone());
                values.put(MappContract.ServicePoint.COLUMN_NAME_ID_CITY, spt.getCity().getIdCity());

                long sptId = db.insert(MappContract.ServicePoint.TABLE_NAME, null, values);


                values = new ContentValues();
                values.put(MappContract.Service.COLUMN_NAME_SERVICE_NAME, sps.getService().getSpeciality());
                values.put(MappContract.Service.COLUMN_NAME_SERVICE_CATAGORY, sps.getService().getCategory());
                long idService = db.insert(MappContract.Service.TABLE_NAME, null, values);

                values = new ContentValues();
                values.put(MappContract.ServProvHasServPt.COLUMN_NAME_ID_SERVICE, idService);
                values.put(MappContract.ServProvHasServPt.COLUMN_NAME_EXP, sps.getExperience());

                try {
                    values.put(MappContract.ServProvHasServPt.COLUMN_NAME_START_TIME, sps.getStartTime());
                    values.put(MappContract.ServProvHasServPt.COLUMN_NAME_END_TIME, sps.getEndTime());
                } catch (Exception x) {
                    x.printStackTrace();
                }
                values.put(MappContract.ServProvHasServPt.COLUMN_NAME_SERVICE_POINT_TYPE, sps.getServPointType());
                values.put(MappContract.ServProvHasServPt.COLUMN_NAME_CONSULTATION_FEE, sps.getConsultFee());
                values.put(MappContract.ServProvHasServPt.COLUMN_NAME_WORKING_DAYS, sps.getWorkingDays());

                where = MappContract.ServProvHasServPt.COLUMN_NAME_SERV_PROV_PHONE + " = ? and " +
                        MappContract.ServProvHasServPt.COLUMN_NAME_ID_SERV_PT + " = ? ";
                String[] args = {
                        "" + sp.getPhone(), "" + sptId
                };
                db.update(MappContract.ServProvHasServPt.TABLE_NAME, values, where, args);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Utility.showRegistrationAlert(myActivity, "", "Profile updated.");
            Utility.showProgress(myActivity, mFormView, mProgressView, false);
            //Intent intent = new Intent(myActivity, LoginActivity.class);
            //startActivity(intent);
        }

        @Override
        protected void onCancelled() {
            Utility.showProgress(myActivity, mFormView, mProgressView, false);
        }

    }
*/

    /////////////////////////////////////////////////////////////////////////////////////////////


    //////////////////////////////////////////Connection & handler////////////////////////////////////

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
/*
    private ServiceConnection mConnection = new ServiceConnection() {

        private Messenger mService;
        private boolean mBound;

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            mWorkPlace.setSignInData(LoginHolder.servLoginRef.getSignInData());
            mService = new Messenger(service);
            mBound = true;
            Message msg = null;
            Bundle bundle = new Bundle();
            bundle.putInt("loginType", MappService.SERVICE_LOGIN);
            if (mServiceAction == MappService.DO_UPDATE || mServiceAction == MappService.DO_REG_NO_CHECK) {
                bundle.putString("regno", mRegNo.getText().toString().trim());
                bundle.putParcelable("service", LoginHolder.servLoginRef);
            } else if (mServiceAction == MappService.DO_GET_SPECIALITY) {
                SearchServProvForm mForm = new SearchServProvForm();
                mForm.setCategory(mServCatagory.getSelectedItem().toString());
                bundle.putParcelable("form", mForm);
            } else {
                bundle.putParcelable("mWorkPlace", mWorkPlace);
                //for add, remove and get Workplace.
            }
            msg = Message.obtain(null, mServiceAction);
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

            mBound = false;
        }
    };
*/
    @Override
    public boolean gotResponse(int action, Bundle data) {
        switch (action) {
            case MappService.DO_ADD_WORK_PLACE:
                addWorkPlaceDone(data);
                break;
            case MappService.DO_REMOVE_WORK_PLACE:
                removeWorkPlaceDone(data);
                break;
            case MappService.DO_UPDATE:
                updateDone(data);
                break;
            case MappService.DO_REG_NO_CHECK:
                regNoCheckDone(data);
                break;
            case MappService.DO_WORK_PLACE_LIST:
                getWorkPlaceListDone(data);
                break;
            case MappService.DO_GET_SPECIALITY:
                getSpecialitiesDone(data);
                break;
            case MappService.DO_EDIT_WORK_PLACE:
                editWorkPlaceDone(data);
                break;
            default:
                return false;
        }
        return true;
    }

    public void regNoCheckDone(Bundle data) {
        if (data.getBoolean("exists")) {
            mRegNo.setError("This Registration Number is already Registered.");
            mRegNo.requestFocus();
        }
        Utility.showProgress(this, mFormView, mProgressView, false);
    }

    private void updateDone(Bundle data) {
        if (data.getBoolean("status")) {
            LoginHolder.servLoginRef = mServiceProv;
            Utility.showAlert(this, "", getString(R.string.msg_update_profile), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    refresh();
                }
            });
            /*Utility.showMessage(this, R.string.msg_update_profile);
            refresh();*/
        }
        Utility.showProgress(this, mFormView, mProgressView, false);
    }

    private void addWorkPlaceDone(Bundle data) {
        if (data.getBoolean("status")) {
            Utility.showAlert(this, "", getString(R.string.msg_add_wp), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    refresh();
                }
            });
            /*Utility.showMessage(this, R.string.msg_add_wp);
            refresh();*/
        }
        Utility.showProgress(this, mFormView, mProgressView, false);
    }

    private void editWorkPlaceDone(Bundle data) {
        if (data.getBoolean("status")) {
            Utility.showAlert(this, "", getString(R.string.msg_edit_wp), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    refresh();
                }
            });
        }
        Utility.showProgress(this, mFormView, mProgressView, false);
    }

    private void removeWorkPlaceDone(Bundle data) {
        if (data.getBoolean("status")) {
            Utility.showAlert(this, "", getString(R.string.msg_remove_wp), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    refresh();
                }
            });
        }
        Utility.showProgress(this, mFormView, mProgressView, false);
    }

    private void getWorkPlaceListDone(Bundle data) {
        if (data.getBoolean("status")) {
            //Utility.showRegistrationAlert(this, "", "Problem in loading workplaces");
            mWorkPlaceList = data.getParcelableArrayList("workPlaceList");
            WorkPlaceListAdapter adapter = new WorkPlaceListAdapter(this,
                    R.layout.layout_workplace, mWorkPlaceList);
            listView.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);
            listView.setOnTouchListener(new ListView.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;

                        case MotionEvent.ACTION_UP:
                            // Allow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                    // Handle ListView touch events.
                    v.onTouchEvent(event);
                    return true;
                }
            });
            listView.setAdapter(adapter);
            registerForContextMenu(listView);
            //listView.setOnCreateContextMenuListener(this);
            Utility.showMessage(this, R.string.work_place_details);
        }
        Utility.showProgress(this, mFormView, mProgressView, false);
    }

    private void getSpecialitiesDone(Bundle data) {
        Utility.showProgress(this, mFormView, mProgressView, false);
        ArrayList<String> list = data.getStringArrayList("specialities");
        if (list == null) {
            list = new ArrayList<>();
        }
        Utility.setNewSpec(this, list, mSpeciality);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void refresh() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

}
