package com.extenprise.mapp.medico.service.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.activity.LoginActivity;
import com.extenprise.mapp.medico.data.SignInData;
import com.extenprise.mapp.medico.data.WorkingDataStore;
import com.extenprise.mapp.medico.net.MappService;
import com.extenprise.mapp.medico.net.MappServiceConnection;
import com.extenprise.mapp.medico.net.ResponseHandler;
import com.extenprise.mapp.medico.net.ServiceResponseHandler;
import com.extenprise.mapp.medico.service.data.SearchServProvForm;
import com.extenprise.mapp.medico.service.data.ServiceProvider;
import com.extenprise.mapp.medico.service.data.WorkPlace;
import com.extenprise.mapp.medico.service.ui.WorkPlaceListAdapter;
import com.extenprise.mapp.medico.ui.DaysSelectionDialog;
import com.extenprise.mapp.medico.ui.DialogDismissListener;
import com.extenprise.mapp.medico.util.ByteArrayToBitmapTask;
import com.extenprise.mapp.medico.util.EncryptUtil;
import com.extenprise.mapp.medico.util.Utility;
import com.extenprise.mapp.medico.util.Validator;

import java.io.File;
import java.util.ArrayList;


public class ServProvProfileActivity extends FragmentActivity implements ResponseHandler, DialogDismissListener {

    //String []selectedDays = new String[_options.length];
    protected CharSequence[] options;
    protected boolean[] selections;
    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));
    private ArrayList<WorkPlace> mWorkPlaceList;
    private ServiceProvider mServiceProv;
    private SignInData mSignInData;
    private RelativeLayout mPersonalInfo, mWorkPlaceInfo;
    /*
        private LinearLayout mInfo, mPInfo;
    */
    private ListView mListViewWP;
    private String mCategory; /*mSpecStr*/
    private ArrayList<String> mSpecialityList;

    private ImageView mImgView;
    private TextView mEmailID;
    private TextView mRegNo;
    private TextView mFname;
    private TextView mLname;
    private RadioGroup mGender;
    private RadioButton mMale, mFemale, mGenderBtn;
    private EditText mQualification;

    private EditText mName;
    private EditText mLoc;
    private EditText mPhone1;
    private EditText mPhone2;
    private EditText mEmailIdwork;
    private Spinner mCity;
    private Spinner mState;
    private EditText mPinCode;

    private EditText mNotes;
    private EditText mConsultFee;
    private EditText mExperience;
    private Spinner mServPtType;
    private Spinner mServCatagory;
    private Spinner mSpeciality;
    private Button mStartTime;
    private Button mEndTime;
    private Button mMultiSpinnerDays;

    private EditText mOldPwd;
    private boolean isPwdCorrect;
    private ImageButton mEditWpPencil;
    private int defaultImg;
    private TextView mDocName;

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

        WorkPlace mWorkPlace = new WorkPlace();
        mWorkPlaceList = new ArrayList<>();
        mServiceProv = new ServiceProvider();
        mSignInData = new SignInData();

        mServiceProv = WorkingDataStore.getBundle().getParcelable("servProv");
        //assert mServiceProv != null;
        mSignInData.setPhone(mServiceProv.getPhone());
        mWorkPlace.setSignInData(mSignInData);

        /*mFormView = findViewById(R.id.updateServProvform);
        mProgressView = findViewById(R.id.progressView);*/
        mPersonalInfo = (RelativeLayout) findViewById(R.id.personalInfo);
        mWorkPlaceInfo = (RelativeLayout) findViewById(R.id.workPlaceInfo);
/*
        mInfo = (LinearLayout) findViewById(R.id.info);
        mPInfo = (LinearLayout) findViewById(R.id.pInfo);
*/
        TextView mobNo = (TextView) findViewById(R.id.mobnumValue);
        mobNo.setText(mServiceProv.getSignInData().getPhone());

        mFname = (TextView) findViewById(R.id.textViewFName);
        mLname = (TextView) findViewById(R.id.textViewLName);
        mEmailID = (TextView) findViewById(R.id.editTextEmail);
        mRegNo = (TextView) findViewById(R.id.editTextRegNum);
        mGender = (RadioGroup) findViewById(R.id.radioGroupGender);
        mMale = (RadioButton) findViewById(R.id.radioButtonMale);
        mFemale = (RadioButton) findViewById(R.id.radioButtonFemale);
        mImgView = (ImageView) findViewById(R.id.imageViewDoctor);
        mListViewWP = (ListView) findViewById(R.id.workDetailListView);
        mEditWpPencil = (ImageButton) findViewById(R.id.textViewAddNewWork);

        TextView mGenderTextView = (TextView) findViewById(R.id.viewGenderLabel);
        TextView mMobNo = (TextView) findViewById(R.id.editTextMobNum);
        TextView mViewdrLbl = (TextView) findViewById(R.id.viewdrLbl);
        mDocName = (TextView) findViewById(R.id.textviewDocname);

 /*       Intent intent = getIntent();
        mCategory = intent.getStringExtra("category");
        if (mCategory.equals(getString(R.string.pharmacist))) {
            mViewdrLbl.setText(getString(R.string.welcome));
            mImgView.setImageResource(R.drawable.medstore);
        } else if(mCategory.equals(getString(R.string.diagnostic_center))) {
            mViewdrLbl.setText(getString(R.string.welcome));
            mImgView.setImageResource(R.drawable.diagcenter);
        }*/

        mCategory = getString(R.string.physician);
        defaultImg = R.drawable.dr_avatar;
        String servPointType = mServiceProv.getServProvHasServPt(0).getServPointType();
        if(!servPointType.equalsIgnoreCase(getString(R.string.clinic))) {
            if(servPointType.equalsIgnoreCase(getString(R.string.medical_store))) {
                mCategory = getString(R.string.pharmacist);
                //mImgView.setImageResource(R.drawable.medstore);
                defaultImg = R.drawable.medstore;
            } else {
                mCategory = getString(R.string.diagnostic_center);
                //mImgView.setImageResource(R.drawable.diagcenter);
                defaultImg = R.drawable.diagcenter;
            }
            mViewdrLbl.setText(getString(R.string.welcome));
        }

        if (savedInstanceState != null) {
            Bitmap bitmap = savedInstanceState.getParcelable("image");
            mImgView.setImageBitmap(bitmap);
        } else {
            Bitmap mImgCopy = (Bitmap) getLastNonConfigurationInstance();
            if (mImgCopy != null) {
                mImgView.setImageBitmap(mImgCopy);
            }
        }
        mDocName.setText(String.format("%s %s", mServiceProv.getfName(), mServiceProv.getlName()));
        setPhoto();
        mFname.setText(getString(R.string.first_name_with_lbl, mServiceProv.getfName()));
        mLname.setText(getString(R.string.last_name_with_lbl, mServiceProv.getlName()));
        mMobNo.setText(getString(R.string.mobile_no_with_lbl, mSignInData.getPhone()));
        String email = getString(R.string.not_specified);
        if (mServiceProv.getEmailId() != null) {
            email = mServiceProv.getEmailId();
        }
        mEmailID.setText(getString(R.string.email_id_with_lbl, email));
        mRegNo.setText(getString(R.string.reg_no_with_lbl, mServiceProv.getRegNo()));
        mGenderTextView.setText(getString(R.string.gender_with_lbl, mServiceProv.getGender()));

        //Get work place mSpecialityList from server
        sendRequest(MappService.DO_WORK_PLACE_LIST, mWorkPlace);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mServiceProv = WorkingDataStore.getBundle().getParcelable("servProv");
        mDocName.setText(String.format("%s %s", mServiceProv.getfName(), mServiceProv.getlName()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_screen, menu);
        return super.onCreateOptionsMenu(menu);
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
        if(id == R.id.action_changepwd) {
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.layout_change_pwd, null);

            mOldPwd = (EditText) dialogView.findViewById(R.id.editTextOldPasswd);
            final EditText newPwd = (EditText) dialogView.findViewById(R.id.editTextNewPasswd);
            final EditText confPwd = (EditText) dialogView.findViewById(R.id.editTextCnfPasswd);

            mOldPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        checkPwd();
                    }
                }
            });

            final AlertDialog dialog = Utility.customDialogBuilder(this, dialogView, R.string.changepwd).create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).
                    setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean cancel = false;
                            if (!isPwdCorrect) {
                                checkPwd();
                                Utility.showMessage(ServProvProfileActivity.this, R.string.msg_verify_pwd);
                                cancel = true;
                            }
                            EditText[] fields = {newPwd, confPwd};
                            if (Utility.areEditFieldsEmpty(ServProvProfileActivity.this, fields)) {
                                cancel = true;
                            }

                            View focusView = null;
                            String newpwd = newPwd.getText().toString().trim();
                            if (!Validator.isPasswordValid(newpwd)) {
                                newPwd.setError(getString(R.string.error_pwd_length));
                                focusView = newPwd;
                                cancel = true;
                            }
                            String confpwd = confPwd.getText().toString().trim();
                            if (!confpwd.equals(newpwd)) {
                                confPwd.setError(getString(R.string.error_password_not_matching));
                                focusView = confPwd;
                                cancel = true;
                            }

                            if (cancel) {
                                if (focusView != null) {
                                    focusView.requestFocus();
                                }
                            } else {
                                mServiceProv.getSignInData().setPasswd(EncryptUtil.encrypt(newpwd));
                                Bundle bundle = new Bundle();
                                bundle.putInt("loginType", MappService.SERVICE_LOGIN);
                                bundle.putParcelable("service", mServiceProv);
                                mConnection.setData(bundle);
                                mConnection.setAction(MappService.DO_CHANGE_PWD);
                                if (Utility.doServiceAction(ServProvProfileActivity.this, mConnection, BIND_AUTO_CREATE)) {
                                    //Utility.showProgress(ServProvProfileActivity.this, mFormView, mProgressView, true);
                                    Utility.showProgressDialog(ServProvProfileActivity.this, true);
                                }
                                dialog.dismiss();
                            }
                        }
                    });
        }
        if (id == R.id.logout) {
            Utility.logout(getSharedPreferences("loginPrefs", MODE_PRIVATE), this, LoginActivity.class);
            WorkingDataStore.getBundle().remove("servProv");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkPwd() {
        String oldpwd = mOldPwd.getText().toString().trim();
        if (Validator.isPasswordValid(oldpwd)) {
            mServiceProv.getSignInData().setPasswd(EncryptUtil.encrypt(oldpwd));
            Bundle bundle = new Bundle();
            bundle.putInt("loginType", MappService.SERVICE_LOGIN);
            bundle.putParcelable("service", mServiceProv);
            mConnection.setData(bundle);
            mConnection.setAction(MappService.DO_PWD_CHECK);
            if (Utility.doServiceAction(ServProvProfileActivity.this, mConnection, BIND_AUTO_CREATE)) {
                //Utility.showProgress(ServProvProfileActivity.this, mFormView, mProgressView, true);
                Utility.showProgressDialog(this, true);
            }
        } else {
            mOldPwd.setError(getString(R.string.error_wrong_pwd));
        }
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
        final WorkPlace wp = (WorkPlace) mListViewWP.getItemAtPosition(info.position);
        wp.setSignInData(mSignInData);
        if (item.getTitle() == getString(R.string.edit)) {
            getWorkPlaceView(wp, info.position);
            //editWorkPlaceInfo(item.getActionView());
            return true;
        } else if (item.getTitle() == getString(R.string.remove)) {
            //ArrayAdapter adapter = (ArrayAdapter) mListViewWP.getAdapter();
            //this.resultsList.remove((int) info.id);
            //adapter.removeItem(adapter.getItem(position));
            //adapter.remove(item);
            //item.getActionView().setVisibility(View.GONE);
            /*if(Utility.confirm(this, R.string.confirm_remove_workplace)) {*/
            Utility.showAlert(this, "", getString(R.string.confirm_remove_workplace), null, true, null,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == AlertDialog.BUTTON_POSITIVE) {
                                sendRequest(MappService.DO_REMOVE_WORK_PLACE, wp);
                            }
                            dialog.dismiss();
                        }
                    });
            return true;
        }
        return super.onContextItemSelected(item);
    }

    public void editWorkPlace(View view) {
        WorkPlaceListAdapter adapter = (WorkPlaceListAdapter) mListViewWP.getAdapter();
        if (adapter == null) {
            return;
        }
        int selectedPos = adapter.getSelectedPosition();
        if (selectedPos == -1 && mWorkPlaceList.size() == 1) {
            selectedPos = 0;
        } else if (selectedPos == -1) {
            Utility.showMessage(this, R.string.msg_select_edit_work_place);
            return;
        }
        WorkPlace workPlace = mWorkPlaceList.get(selectedPos);
        getWorkPlaceView(workPlace, selectedPos);
    }

    public void addNewWorkPlace(View view) {
        /*SimpleCursorAdapter adapter = (SimpleCursorAdapter) mListViewWP.getAdapter();
        adapter.changeCursorAndColumns( SearchServProv.getCursor(), new String[]{}, new int[]{});

        *//*MatrixCursor extras = new MatrixCursor(new String[] { "_id", "title" });
        extras.addRow(new String[] { "-1", "New Template" });
        extras.addRow(new String[] { "-2", "Empty Template" });
        Cursor[] cursors = { extras, SearchServProv.getCursor() };
        Cursor extendedCursor = new MergeCursor(cursors);
        adapter.notifyDataSetChanged();
*/
        getWorkPlaceView(null, -1);
    }

    private void getWorkPlaceView(final WorkPlace item, final int position) {
        int action = MappService.DO_ADD_WORK_PLACE;

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_servprov_wrkdetail_list, null);

        mName = (EditText) dialogView.findViewById(R.id.editTextName);
        mLoc = (EditText) dialogView.findViewById(R.id.editTextLoc);
        mPhone1 = (EditText) dialogView.findViewById(R.id.editTextPhone1);
        mPhone2 = (EditText) dialogView.findViewById(R.id.editTextPhone2);
        mEmailIdwork = (EditText) dialogView.findViewById(R.id.editTextEmail);
        mConsultFee = (EditText) dialogView.findViewById(R.id.editTextConsultationFees);
        mNotes = (EditText) dialogView.findViewById(R.id.editTextNotes);
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

        Utility.setNewSpinner(this, null, mServCatagory,
                new String[]{String.format("%s *", getString(R.string.select_category)), mCategory});
        Utility.setNewSpinner(this, mSpecialityList, mSpeciality, null);

        ArrayList<String> listWPType = new ArrayList<>();
        if(mCategory.equalsIgnoreCase(getString(R.string.physician))) {
            listWPType.add(getString(R.string.clinic));
        }
        else if(mCategory.equalsIgnoreCase(getString(R.string.pharmacist))) {
            mConsultFee.setEnabled(false);
            listWPType.add(getString(R.string.medical_store));
        } else {
            listWPType.add(getString(R.string.path_lab));
            listWPType.add(getString(R.string.scan_lab));
        }
        Utility.setNewSpinner(this, listWPType, mServPtType, null);

        if (item != null) {
            action = MappService.DO_EDIT_WORK_PLACE;

            mName.setText(item.getName());
            mLoc.setText(item.getLocation());
            mPhone1.setText(item.getPhone());
            mPhone2.setText(item.getAltPhone());
            mEmailIdwork.setText(item.getEmailId());
            mConsultFee.setText(String.format("%.2f", item.getConsultFee()));
            mNotes.setText(item.getNotes());
            mServPtType.setSelection(Utility.getSpinnerIndex(mServPtType, item.getServPointType()));
            mCity.setSelection(Utility.getSpinnerIndex(mCity, item.getCity().getCity()));
            mState.setSelection(Utility.getSpinnerIndex(mState, item.getCity().getState()));
            mStartTime.setText(Utility.getTimeString(item.getStartTime()));
            mEndTime.setText(Utility.getTimeString(item.getEndTime()));
            mQualification.setText(item.getQualification());
            mMultiSpinnerDays.setText(item.getWorkingDays());
            mServCatagory.setSelection(Utility.getSpinnerIndex(mServCatagory, item.getServCategory()));
            mSpeciality.setSelection(Utility.getSpinnerIndex(mSpeciality, item.getSpeciality()));
            //mSpecStr = item.getSpeciality();
            mExperience.setText(String.format("%.01f", item.getExperience()));
            if (item.getPincode() != null) {
                mPinCode.setText(item.getPincode());
            }
            /*ArrayList<String> specs = new ArrayList<>();
            specs.add(item.getSpeciality());
            //Utility.setNewSpec(this, specs, mSpeciality);
            Utility.setNewSpinner(this, specs, mSpeciality, new String[]{getString(R.string.other)});*/
            /*specs.clear();
            specs.add(item.getServCategory());
            Utility.setNewSpec(this, specs, mServCatagory);*/
        }

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
        mMultiSpinnerDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDaysSelectionDialog();
            }
        });

        /*mServCatagory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String servCategory = mServCatagory.getSelectedItem().toString();
                *//*MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
                DBUtil.setSpecOfCategory(getApplicationContext(), dbHelper, servCategory, mSpeciality);*//*
                if (!TextUtils.isEmpty(servCategory) && !servCategory.equals(getString(R.string.select_category))) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("loginType", MappService.SERVICE_LOGIN);
                    SearchServProvForm mForm = new SearchServProvForm();
                    mForm.setCategory(mServCatagory.getSelectedItem().toString());
                    bundle.putParcelable("form", mForm);
                    mConnection.setData(bundle);
                    mConnection.setAction(MappService.DO_GET_SPECIALITY);
                    if (Utility.doServiceAction(ServProvProfileActivity.this, mConnection, BIND_AUTO_CREATE)) {
                        //Utility.showProgress(getApplicationContext(), mFormView, mProgressView, true);
                        Utility.showProgressDialog(ServProvProfileActivity.this, true);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });*/
        mSpeciality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String spec = mSpeciality.getSelectedItem().toString();
                String servCategory = mServCatagory.getSelectedItem().toString();
                if (!TextUtils.isEmpty(servCategory) && !servCategory.equals(getString(R.string.select_category))) {
                    if (spec.equals(getString(R.string.other))) {
                        final EditText txtSpec = new EditText(ServProvProfileActivity.this);
                        txtSpec.setHint(getString(R.string.speciality));
                        final AlertDialog dialog = Utility.customDialogBuilder(ServProvProfileActivity.this, txtSpec, R.string.add_new_spec).create();
                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).
                                setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String txt = txtSpec.getText().toString().trim();
                                        boolean cancel = false;
                                        if(TextUtils.isEmpty(txt)) {
                                            txtSpec.setError(getString(R.string.error_field_required));
                                            txtSpec.requestFocus();
                                            cancel = true;
                                        }
                                        if(!Validator.isOnlyAlpha(txt)) {
                                            txtSpec.setError(getString(R.string.error_only_alpha));
                                            txtSpec.requestFocus();
                                            cancel = true;
                                        }
                                        if(!cancel) {
                                            mSpecialityList.add(0, txt);
                                            if (!mSpecialityList.contains(getString(R.string.other))) {
                                                mSpecialityList.add(getString(R.string.other));
                                            }
                                            Utility.setNewSpinner(ServProvProfileActivity.this, mSpecialityList, mSpeciality, null);
                                            dialog.dismiss();
                                        }
                                    }
                                });
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
                    for ( int i = 0; i < mWorkPlaceList.size(); i++ ) {
                        if(item != null && i == position) {
                            continue;
                        }
                        WorkPlace w = mWorkPlaceList.get(i);

                        String[] workdays = w.getWorkingDays().split(getString(R.string.comma));
                        for (String workday : workdays) {
                            String[] workdays2 = mMultiSpinnerDays.getText().toString().split(getString(R.string.comma));
                            for (String aWorkdays2 : workdays2) {
                                if (workday.equals(aWorkdays2)) {
                                    int st1 = w.getStartTime();
                                    int en1 = w.getEndTime();
                                    int st2 = Utility.getMinutes(mStartTime.getText().toString());
                                    int en2 = Utility.getMinutes(mEndTime.getText().toString());

                                    if (st2 == st1 || en1 == en2 ||
                                            (st2 > st1 && st2 < en1) ||
                                            (en2 > st1 && en2 < en1) ||
                                            (st2 < st1 && en2 > en1)) {
                                        //Utility.showAlert(getActivity(), "", getString(R.string.msg_time_collapse));
                                        mEndTime.setError(getString(R.string.msg_time_reserved));
                                        mEndTime.requestFocus();
                                        return;
                                    }
                                }
                            }
                        }
                    }

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
                    wp.setServCategory(mServCatagory.getSelectedItem().toString());
                    wp.setSpeciality(mSpeciality.getSelectedItem().toString());
                    wp.setServPointType(mServPtType.getSelectedItem().toString());
                    wp.setNotes(mNotes.getText().toString().trim());
                    wp.setExperience(Float.parseFloat(mExperience.getText().toString().trim()));
                    wp.setQualification(mQualification.getText().toString().trim());
                    wp.setSignInData(mSignInData);
                    wp.setPincode(mPinCode.getText().toString().trim());
                    if (mConsultFee.isEnabled()) {
                        String fees = mConsultFee.getText().toString().trim();
                        float fee = 0;
                        if (!TextUtils.isEmpty(fees)) {
                            fee = Float.parseFloat(fees);
                        }
                        wp.setConsultFee(fee);
                    }
                    if (finalAction == MappService.DO_EDIT_WORK_PLACE) {
                        wp.setIdServicePoint(item.getIdServicePoint());
                        wp.setIdService(item.getIdService());
                    }

                    sendRequest(finalAction, wp);
                    dialog.dismiss();
                }
            }
        });

        /*
        This code is not displaying buttons properly.
        Utility.showAlert(this, getString(R.string.work_place_details), "", dialogView, true, null,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == AlertDialog.BUTTON_NEGATIVE) {
                            dialog.dismiss();
                            return;
                        }

                    }
                });*/
        /*final AlertDialog dialog = Utility.customDialogBuilder(this, dialogView, R.string.work_place_details).create();
        dialog.show();*/
    }

    private boolean isValidWorkPlace() {
        boolean valid = true;
        View focusView = null;
        String value = "";

        if (Utility.areEditFieldsEmpty(this, new EditText[]{mPhone1, mPinCode,
                mLoc, mName, mQualification, mExperience})) {
            valid = false;
        }

        if (mConsultFee.isEnabled()) {
            value = mConsultFee.getText().toString().trim();
            if (!TextUtils.isEmpty(value)) {
                try {
                    float v = Float.parseFloat(value);
                } catch (NumberFormatException n) {
                    mConsultFee.setError(getString(R.string.error_only_digit));
                    valid = false;
                    focusView = mConsultFee;
                }
            }
        }

        value = mMultiSpinnerDays.getText().toString();
        if (value.equalsIgnoreCase(getString(R.string.practice_days))) {
            mMultiSpinnerDays.setError(getString(R.string.error_field_required));
            focusView = mMultiSpinnerDays;
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

        value = mEmailIdwork.getText().toString().trim();
        if (!TextUtils.isEmpty(value) && !Validator.isValidEmaillId(value)) {
            mEmailIdwork.setError(getString(R.string.error_invalid_email));
            focusView = mEmailIdwork;
            valid = false;
        }

        value = mPhone2.getText().toString().trim();
        if (!TextUtils.isEmpty(value) && !Validator.isPhoneValid(value)) {
            mPhone2.setError(getString(R.string.error_invalid_phone));
            focusView = mPhone2;
            valid = false;
        }

        int errMsg = -1;
        if (mState.getSelectedItem().toString().equals(getString(R.string.state_lbl))) {
            Utility.setSpinError(mState, getString(R.string.error_select_state));
            focusView = mPhone2;
            errMsg = R.string.error_select_state;
        }

        if (Utility.areEditFieldsEmpty(this, new EditText[]{mPhone1, mPinCode,
                mLoc, mName, mExperience, mQualification})) {
            valid = false;
            focusView = null;
        }

        value = mPhone1.getText().toString().trim();
        if (!Validator.isPhoneValid(value) && !TextUtils.isEmpty(value)) {
            mPhone1.setError(getString(R.string.error_invalid_phone));
            focusView = mPhone1;
            valid = false;
        }

        value = mPinCode.getText().toString().trim();
        if (!TextUtils.isEmpty(value) && Validator.isPinCodeValid(value)) {
            mPinCode.setError(getString(R.string.error_invalid_pincode));
            focusView = mPinCode;
            valid = false;
        }

        if (mSpeciality.getSelectedItem() != null) {
            value = mSpeciality.getSelectedItem().toString();
            if (value.equalsIgnoreCase(getString(R.string.select_speciality)) ||
                    value.equals(getString(R.string.other))) {
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

        value = mServCatagory.getSelectedItem().toString();
        if (TextUtils.isEmpty(value) || value.equals(getString(R.string.select_category))) {
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

        if (valid && errMsg != -1) {
            valid = false;
            Utility.showMessage(this, errMsg);
        }
        if (focusView != null) {
            focusView.requestFocus();
        }
        return valid;
    }

    public void editPersonalInfo(View view) {
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_personal_info, null);

        dialogView.findViewById(R.id.editTextCellphone).setVisibility(View.GONE);
        dialogView.findViewById(R.id.editTextPasswd).setVisibility(View.GONE);
        dialogView.findViewById(R.id.editTextCnfPasswd).setVisibility(View.GONE);
        dialogView.findViewById(R.id.phonePrefix).setVisibility(View.GONE);

        mFname = (EditText) dialogView.findViewById(R.id.editTextFName);
        mLname = (EditText) dialogView.findViewById(R.id.editTextLName);
        mLname.setNextFocusForwardId(R.id.editTextEmail);
        mEmailID = (EditText) dialogView.findViewById(R.id.editTextEmail);
        mEmailID.setNextFocusForwardId(R.id.editTextRegistrationNumber);
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
                        mServiceProv.setRegNo(regNo);
                        //bundle.putString("regno", regNo);
                        sendRequest(MappService.DO_REG_NO_CHECK, null);
                    }
                }
            }
        });
        mFname.setText(mServiceProv.getfName());
        mLname.setText(mServiceProv.getlName());
        String email = getString(R.string.not_specified);
        if (mServiceProv.getEmailId() != null) {
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
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).
                setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           EditText[] fields = {(EditText) mFname, (EditText) mLname, (EditText) mRegNo};
                                           if (Utility.areEditFieldsEmpty(ServProvProfileActivity.this, fields)) {
                                               return;
                                           }
                                           boolean cancel = false;
                                           View focusView = null;
                                           String email = mEmailID.getText().toString().trim();
                                           if (!TextUtils.isEmpty(email) && !Validator.isValidEmaillId(email)) {
                                               mEmailID.setError(getString(R.string.error_invalid_email));
                                               focusView = mEmailID;
                                               cancel = true;
                                           }
                                           if (!Validator.isOnlyAlpha(mFname.getText().toString().trim())) {
                                               mFname.setError(getString(R.string.error_only_alpha));
                                               focusView = mFname;
                                               cancel = true;
                                           }
                                           if (!Validator.isOnlyAlpha(mLname.getText().toString().trim())) {
                                               mLname.setError(getString(R.string.error_only_alpha));
                                               focusView = mLname;
                                               cancel = true;
                                           }
                                           int genderID = mGender.getCheckedRadioButtonId();
                                           if (genderID == -1) {
                                               mFemale.setError(getString(R.string.error_select_gender));
                                               focusView = mFemale;
                                               cancel = true;
                                           } else {
                                               mGenderBtn = (RadioButton) dialogView.findViewById(genderID);
                                           }

                                           if (cancel && focusView != null) {
                                               focusView.requestFocus();
                                               return;
                                           }
                                           mServiceProv.setfName(mFname.getText().toString().trim());
                                           mServiceProv.setlName(mLname.getText().toString().trim());
                                           mServiceProv.setEmailId(email);
                                           mServiceProv.setGender(mGenderBtn.getText().toString());
                                           mServiceProv.setRegNo(mRegNo.getText().toString());
                                           mServiceProv.setSignInData(mSignInData);

                                           sendRequest(MappService.DO_UPDATE, null);
                                           dialog.dismiss();
                                       }
                                   }
                );

                /*
                This code is not showing the buttons properly.

                Utility.showAlert(this, getString(R.string.personalDetails), "", dialogView, true, null,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == AlertDialog.BUTTON_NEGATIVE) {
                                    dialog.dismiss();
                                    return;
                                }
                            }
                        });*/

        /*final AlertDialog dialog = Utility.customDialogBuilder(this, dialogView, R.string.personalDetails).create();
        dialog.show();*/
    }

    public void openPersonalInfo(View view) {
        Utility.startActivity(this, ServProvPersonalInfoActivity.class);
        /*if (mPersonalInfo.getVisibility() == View.VISIBLE) {
            mPersonalInfo.setVisibility(View.GONE);
//            mInfo.setVisibility(View.VISIBLE);
        } else {
            mPersonalInfo.setVisibility(View.VISIBLE);
            //          mInfo.setVisibility(View.GONE);
            if (mWorkPlaceInfo.getVisibility() == View.VISIBLE) {
                mWorkPlaceInfo.setVisibility(View.GONE);
            }
        }*/
        /*Utility.collapseExpand(mInfo);
        Utility.collapseExpand(mPersonalInfo);
        Utility.collapseExpand(mWorkPlaceInfo);*/
    }

    public void openWorkPlaceInfo(View view) {
        /*Intent intent = new Intent(this, ServProvWorkInfoActivity.class);
        //intent.putStringArrayListExtra("wplist", mWorkPlaceList);
        intent.putParcelableArrayListExtra("wplist", mWorkPlaceList);
        intent.putExtra("category", mWorkPlaceList.get(0).getServCategory());
        intent.putExtra("wpListSize", mWorkPlaceList.size());
        startActivity(intent);*/

        if (mWorkPlaceInfo.getVisibility() == View.VISIBLE) {
            mWorkPlaceInfo.setVisibility(View.GONE);
        } else {
            mWorkPlaceInfo.setVisibility(View.VISIBLE);
            if (mPersonalInfo.getVisibility() == View.VISIBLE) {
                mPersonalInfo.setVisibility(View.GONE);
            }
        }
    }

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

/*
    @Override
    public Object onRetainNonConfigurationInstance() {
        return mImgCopy;
    }
*/

    public void changeImage(View view) {
        String[] opts = new String[]{getString(R.string.take_photo),
                getString(R.string.from_gallery)};
        if (mServiceProv.getPhoto() != null) {
            opts = new String[]{getString(R.string.take_photo),
                    getString(R.string.from_gallery),
                    getString(R.string.remove)};
        }
        final Activity activity = this;
        final File destination = new File(Environment.getExternalStorageDirectory().getPath(), "photo.jpg");
        Utility.showAlert(activity, getString(R.string.profile_photo), null, null, false,
                opts, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                //Utility.startCamera(activity, getResources().getInteger(R.integer.request_camera));
                                Utility.startCamera(activity, getResources().getInteger(R.integer.request_camera), destination);
                                break;
                            case 1:
                                Utility.pickPhotoFromGallery(activity, getResources().getInteger(R.integer.request_gallery));
                                break;
                            case 2:
                                Utility.showAlert(activity, "", getString(R.string.confirm_remove_photo), null, true,
                                        null,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                if (which == DialogInterface.BUTTON_POSITIVE) {
                                                    Bundle bundle = new Bundle();
                                                    bundle.putInt("loginType", MappService.SERVICE_LOGIN);
                                                    ServiceProvider sp = new ServiceProvider();
                                                    sp.getSignInData().setPhone(mServiceProv.getSignInData().getPhone());
                                                    bundle.putParcelable("service", sp);
                                                    mConnection.setData(bundle);
                                                    mConnection.setAction(MappService.DO_REMOVE_PHOTO);
                                                    if (Utility.doServiceAction(activity, mConnection, BIND_AUTO_CREATE)) {
                                                        Utility.showProgressDialog(ServProvProfileActivity.this, true);
                                                    }
                                                }
                                            }
                                        });
                        }

                    }
                });
    }

/*
    public void removePhoto(View view) {
        final Activity activity = this;
        Utility.showAlert(activity, getString(R.string.confirm_remove_photo), "", null, true, null,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == AlertDialog.BUTTON_POSITIVE) {
                            Bundle bundle = new Bundle();
                            bundle.putInt("loginType", MappService.SERVICE_LOGIN);
                            ServiceProvider sp = new ServiceProvider();
                            sp.getSignInData().setPhone(mServiceProv.getSignInData().getPhone());
                            bundle.putParcelable("service", sp);
                            mConnection.setData(bundle);
                            mConnection.setAction(MappService.DO_REMOVE_PHOTO);
                            if (Utility.doServiceAction(activity, mConnection, BIND_AUTO_CREATE)) {
                                Utility.showProgressDialog(activity, true);
                            }
                        }
                        dialog.dismiss();
                    }
                });
    }
*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Utility.onPhotoActivityResult(this, mImgView, requestCode, resultCode, data)) {
            mServiceProv.setPhoto(Utility.getBytesFromBitmap(((BitmapDrawable) mImgView.getDrawable()).getBitmap()));
            sendRequest(MappService.DO_UPLOAD_PHOTO, null);
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        try {
            boolean isImageChanged = false;
            Uri selectedImage = null;
            Resources resources = getResources();
            // When an Image is picked
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    Utility.showMessage(this, R.string.error_img_not_picked);
                    return;
                }
                mImgView.setBackgroundResource(0);
                if ((requestCode == resources.getInteger(R.integer.request_gallery) ||
                        requestCode == resources.getInteger(R.integer.request_edit))) {
                    // Get the Image from data
                    selectedImage = data.getData();
                    mImgView.setImageURI(selectedImage);
                    isImageChanged = true;

                } else if (requestCode == resources.getInteger(R.integer.request_camera)) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    mImgView.setImageBitmap(bitmap);
                    selectedImage = Utility.getImageUri(this, bitmap);
                    isImageChanged = true;
                } else {
                    Utility.showMessage(this, R.string.error_img_not_picked);
                }
            } else if (requestCode == resources.getInteger(R.integer.request_edit)) {
                isImageChanged = true;
            }

            if (isImageChanged) {
                if (requestCode != resources.getInteger(R.integer.request_edit)) {
                    Intent editIntent = new Intent(Intent.ACTION_EDIT);
                    editIntent.setDataAndType(selectedImage, "image*//*");
                    editIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(editIntent, resources.getInteger(R.integer.request_edit));
                } else {
                    mServiceProv.setPhoto(Utility.getBytesFromBitmap(((BitmapDrawable) mImgView.getDrawable()).getBitmap()));
                    sendRequest(MappService.DO_UPLOAD_PHOTO, null);

                    *//*mServiceProv.setPhoto(Utility.getBytesFromBitmap(mImgView.getDrawingCache()));
                    sendRequest(MappService.DO_UPLOAD_PHOTO, null);*//*
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utility.showMessage(this, R.string.some_error);
        }
    }*/

//////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////Connection & handler////////////////////////////////////

    private void sendRequest(int action, WorkPlace wp) {
        Bundle bundle = new Bundle();
        bundle.putInt("loginType", MappService.SERVICE_LOGIN);
        if (wp != null) {
            bundle.putParcelable("workPlace", wp);
        } else {
            bundle.putParcelable("service", mServiceProv);
        }
        mConnection.setData(bundle);
        mConnection.setAction(action);
        if (Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE)) {
            //Utility.showProgress(getApplicationContext(), mFormView, mProgressView, true);
            Utility.showProgressDialog(this, true);
        }
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        Utility.showProgressDialog(this, false);
        switch (action) {
            case MappService.DO_ADD_WORK_PLACE:
                updateDone(R.string.msg_add_wp_done, data);
                break;
            case MappService.DO_REMOVE_WORK_PLACE:
                updateDone(R.string.msg_remove_wp_done, data);
                break;
            case MappService.DO_UPDATE:
                updateDone(R.string.msg_update_profile_done, data);
                break;
            case MappService.DO_EDIT_WORK_PLACE:
                updateDone(R.string.msg_edit_wp_done, data);
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
            case MappService.DO_UPLOAD_PHOTO:
                uploadPhotoDone(data);
                break;
            case MappService.DO_PWD_CHECK:
                pwdCheckDone(data);
                break;
            case MappService.DO_CHANGE_PWD:
                changePwdDone(data);
                break;
            case MappService.DO_REMOVE_PHOTO:
                removePhotoDone(data);
                break;
        }
        return data.getBoolean("status");
    }

    private void removePhotoDone(Bundle data) {
        if (data.getBoolean("status")) {
            Utility.showMessage(this, R.string.msg_photo_removed);
            mServiceProv = WorkingDataStore.getBundle().getParcelable("servProv");
            mServiceProv.setPhoto(null);
        }
        setPhoto();
    }

    private void setPhoto() {
        mImgView.setImageResource(defaultImg);
        if (mServiceProv.getPhoto() != null) {
            ByteArrayToBitmapTask task = new ByteArrayToBitmapTask(mImgView, mServiceProv.getPhoto(),
                    mImgView.getLayoutParams().width, mImgView.getLayoutParams().height);
            task.execute();
            /*mImgView.setBackgroundResource(0);
            mImgView.setImageBitmap(Utility.getBitmapFromBytes(mServiceProv.getPhoto(),
                    mImgView.getLayoutParams().width, mImgView.getLayoutParams().height));*/
        }
    }

    private void changePwdDone(Bundle data) {
        //Utility.showProgress(this, mFormView, mProgressView, false);
        if (data.getBoolean("status")) {
            Utility.showMessage(this, R.string.msg_change_pwd);
        }
    }

    private void pwdCheckDone(Bundle data) {
        if (data.getBoolean("status")) {
            isPwdCorrect = true;
        } else {
            isPwdCorrect = false;
            mOldPwd.setError(getString(R.string.error_wrong_pwd));
            mOldPwd.requestFocus();
        }
    }

    private void uploadPhotoDone(Bundle data) {
        if (data.getBoolean("status")) {
            Utility.showMessage(this, R.string.msg_upload_photo);
            mServiceProv = WorkingDataStore.getBundle().getParcelable("servProv");
            mServiceProv.setPhoto(Utility.getBytesFromBitmap(((BitmapDrawable) mImgView.getDrawable()).getBitmap()));
        }
        setPhoto();
    }

    private void getWorkPlaceListDone(Bundle data) {
        if (data.getBoolean("status")) {
            //Utility.showRegistrationAlert(this, "", "Problem in loading workplaces");
            mEditWpPencil.setVisibility(View.VISIBLE);
            mWorkPlaceList = data.getParcelableArrayList("workPlaceList");
            WorkPlaceListAdapter adapter = new WorkPlaceListAdapter(this,
                    R.layout.layout_wp_list_item, mWorkPlaceList);
            mListViewWP.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);
            /*mListViewWP.setOnTouchListener(new ListView.OnTouchListener() {
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
            });*/
            //mListViewWP.setSelection(adapter.getCount());
            //mListViewWP.setOverscrollFooter(getDrawable(R.drawable.up));
            mListViewWP.setAdapter(adapter);
            mListViewWP.setSelection(adapter.getCount());
            registerForContextMenu(mListViewWP);
            Utility.showMessage(this, R.string.work_place_details);

            //mListViewWP.setOnCreateContextMenuListener(this);
        } else {
            mEditWpPencil.setVisibility(View.GONE);
            /*else {
            Utility.showMessage(this, R.string.some_error);
        }*/
        }
        //Getting speciality list...
        Bundle bundle = new Bundle();
        bundle.putInt("loginType", MappService.SERVICE_LOGIN);
        SearchServProvForm mForm = new SearchServProvForm();
        mForm.setCategory(mCategory);
        bundle.putParcelable("form", mForm);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_GET_SPECIALITY);
        Utility.doServiceAction(ServProvProfileActivity.this, mConnection, BIND_AUTO_CREATE);
        //Utility.showProgress(this, mFormView, mProgressView, false);
    }

    public void regNoCheckDone(Bundle data) {
        if (data.getBoolean("exists")) {
            mRegNo.setError("This Registration Number is already Registered.");
            mRegNo.requestFocus();
        }
        //Utility.showProgress(this, mFormView, mProgressView, false);
    }

    private void getSpecialitiesDone(Bundle data) {
        //Utility.showProgress(this, mFormView, mProgressView, false);
        mSpecialityList = data.getStringArrayList("specialities");
        if (mSpecialityList == null) {
            mSpecialityList = new ArrayList<>();
        }
        mSpecialityList.add(0, String.format("%s *", getString(R.string.select_speciality)));
        if(!mSpecialityList.contains(getString(R.string.other))) {
            mSpecialityList.add(getString(R.string.other));
        }
        /*Utility.setNewSpinner(this, mSpecialityList, mSpeciality, null);
        if(mSpecStr != null && !mSpecStr.equals("")) {
            mSpeciality.setSelection(Utility.getSpinnerIndex(mSpeciality, mSpecStr));
        }*/
    }

    private void updateDone(int msg, Bundle data) {
        if (data.getBoolean("status")) {
            //LoginHolder.servLoginRef = mServiceProv;
            Utility.showAlert(this, "", getString(msg), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    refresh();
                }
            });
        }
        //Utility.showProgress(this, mFormView, mProgressView, false);

    }

    private void refresh() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    /*@Override
    public void onBackPressed() {
        unbindService(mConnection);
        startActivity(super.getParentActivityIntent());
    }*/

///////////////////////////Multi spinner..../////////////////////////////

    @Override
    public void onDialogDismissed(DialogFragment dialog) {
        DaysSelectionDialog selectionDialog = (DaysSelectionDialog) dialog;
        String selectedDays = selectionDialog.getSelectedDays();
        mMultiSpinnerDays.setText(selectedDays);
        mMultiSpinnerDays.setError(null);
    }

    @Override
    public void onApplyDone(DialogFragment dialog) {

    }

    @Override
    public void onCancelDone(DialogFragment dialog) {

    }

    private void showDaysSelectionDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String selctedDays = "";
        if (!mMultiSpinnerDays.getText().toString().equals(getString(R.string.select_days))) {
            selctedDays = mMultiSpinnerDays.getText().toString();
        }
        DaysSelectionDialog dialog = new DaysSelectionDialog();
        dialog.setSelectedDays(selctedDays);
        dialog.setListener(this);
        dialog.show(fragmentManager, "DaysSelect");
    }

    @Override
    public void onBackPressed() {
        mConnection.setBound(false);
        //startActivity(getIntent());
        super.onBackPressed();
    }

}
