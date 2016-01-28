package com.extenprise.mapp.medico.service.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.extenprise.mapp.medico.LoginHolder;
import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.data.SignInData;
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
import com.extenprise.mapp.medico.util.EncryptUtil;
import com.extenprise.mapp.medico.util.Utility;
import com.extenprise.mapp.medico.util.Validator;

import java.util.ArrayList;


public class ServProvProfileActivity extends FragmentActivity implements ResponseHandler, DialogDismissListener {

    //String []selectedDays = new String[_options.length];
    protected CharSequence[] options;
    protected boolean[] selections;
    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));
    private ArrayList<WorkPlace> mWorkPlaceList;
    private ServiceProvider mServiceProv;
    private SignInData mSignInData;
    private TextView mEmailID;
    private TextView mRegNo;
    private TextView mFname;
    private TextView mLname;
    private RadioGroup mGender;
    private RadioButton mMale, mFemale, mGenderBtn;
    private RelativeLayout mPersonalInfo, mWorkPlaceInfo;
    private ListView listView;
    private View mFormView;
    private View mProgressView;
    private ImageView mImgView;
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
    private String mCategory;

    private EditText mOldPwd;
    private boolean isPwdCorrect;

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
        mServiceProv = LoginHolder.servLoginRef;
        mSignInData.setPhone(LoginHolder.servLoginRef.getPhone());
        mWorkPlace.setSignInData(mSignInData);

        mFormView = findViewById(R.id.updateServProvform);
        mProgressView = findViewById(R.id.progressView);
        mPersonalInfo = (RelativeLayout) findViewById(R.id.personalInfo);
        mWorkPlaceInfo = (RelativeLayout) findViewById(R.id.workPlaceInfo);

        mFname = (TextView) findViewById(R.id.textViewFName);
        mLname = (TextView) findViewById(R.id.textViewLName);
        mEmailID = (TextView) findViewById(R.id.editTextEmail);
        mRegNo = (TextView) findViewById(R.id.editTextRegNum);
        mGender = (RadioGroup) findViewById(R.id.radioGroupGender);
        mMale = (RadioButton) findViewById(R.id.radioButtonMale);
        mFemale = (RadioButton) findViewById(R.id.radioButtonFemale);
        mImgView = (ImageView) findViewById(R.id.imageViewDoctor);
        listView = (ListView) findViewById(R.id.workDetailListView);

        TextView mGenderTextView = (TextView) findViewById(R.id.viewGenderLabel);
        TextView mMobNo = (TextView) findViewById(R.id.editTextMobNum);
        TextView mViewdrLbl = (TextView) findViewById(R.id.viewdrLbl);
        TextView mDocName = (TextView) findViewById(R.id.textviewDocname);

        Intent intent = getIntent();
        mCategory = intent.getStringExtra("category");
        if (mCategory.equals(getString(R.string.pharmacist))) {
            mViewdrLbl.setText(getString(R.string.welcome));
            mImgView.setImageResource(R.drawable.medstore);
            //mConsultFee.setEnabled(false);
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
        if (mServiceProv.getPhoto() != null) {
            mImgView.setImageBitmap(Utility.getBitmapFromBytes(LoginHolder.servLoginRef.getPhoto()));
        }
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

        //Get work place list from server
        sendRequest(MappService.DO_WORK_PLACE_LIST, mWorkPlace);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_serv_prove_profile, menu);
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
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isPwdCorrect) {
                        checkPwd();
                        Utility.showMessage(ServProvProfileActivity.this, R.string.msg_verify_pwd);
                        return;
                    }
                    EditText[] fields = {newPwd, confPwd};
                    if (Utility.areEditFieldsEmpty(ServProvProfileActivity.this, fields)) {
                        return;
                    }

                    boolean cancel = false;
                    View focusView = null;
                    String newpwd = newPwd.getText().toString().trim();
                    if (!Validator.isPasswordValid(newpwd)) {
                        newPwd.setError(getString(R.string.error_invalid_password));
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
                        focusView.requestFocus();
                        return;
                    }

                    mServiceProv.getSignInData().setPasswd(EncryptUtil.encrypt(newpwd));
                    Bundle bundle = new Bundle();
                    bundle.putInt("loginType", MappService.SERVICE_LOGIN);
                    bundle.putParcelable("service", mServiceProv);
                    mConnection.setData(bundle);
                    mConnection.setAction(MappService.DO_CHANGE_PWD);
                    if (Utility.doServiceAction(ServProvProfileActivity.this, mConnection, BIND_AUTO_CREATE)) {
                        Utility.showProgress(ServProvProfileActivity.this, mFormView, mProgressView, true);
                    }

                    dialog.dismiss();
                }
            });
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
                Utility.showProgress(ServProvProfileActivity.this, mFormView, mProgressView, true);
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
                    sendRequest(MappService.DO_REMOVE_WORK_PLACE, wp);
                    dialog.dismiss();
                }
            });
            return true;
        }
        return super.onContextItemSelected(item);
    }

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

    private AlertDialog getWorkPlaceView(final WorkPlace item) {
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
        TextView workhourLBL = (TextView) dialogView.findViewById(R.id.viewWorkHrsLbl);

        Utility.setNewSpinner(this, null, mServCatagory,
                new String[] { getString(R.string.select_category), mCategory });
        mServCatagory.setSelection(Utility.getSpinnerIndex(mServCatagory, mCategory));

        if (item != null) {
            action = MappService.DO_EDIT_WORK_PLACE;

            mName.setText(item.getName());
            mLoc.setText(item.getLocation());
            mPhone1.setText(item.getPhone());
            mPhone2.setText(item.getAltPhone());
            mEmailIdwork.setText(item.getEmailId());
            if (mCategory.equals(getString(R.string.pharmacist))) {
                mConsultFee.setEnabled(false);
            } else {
                mConsultFee.setText(String.format("%.2f", item.getConsultFee()));
            }
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
            if (item.getPincode() != null) {
                mPinCode.setText(item.getPincode());
            }
            ArrayList<String> specs = new ArrayList<>();
            specs.add(item.getSpeciality());
            //Utility.setNewSpec(this, specs, mSpeciality);
            Utility.setNewSpinner(this, specs, mSpeciality, new String[]{getString(R.string.other)});
            mSpeciality.setSelection(Utility.getSpinnerIndex(mSpeciality, item.getSpeciality()));
            /*specs.clear();
            specs.add(item.getServCategory());
            Utility.setNewSpec(this, specs, mServCatagory);*/
        }

        workhourLBL.setClickable(false);
        mSpeciality.setClickable(true);
        mServCatagory.setClickable(false);
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

        mServCatagory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String servCategory = mServCatagory.getSelectedItem().toString();
                /*MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
                DBUtil.setSpecOfCategory(getApplicationContext(), dbHelper, servCategory, mSpeciality);*/
                if (!TextUtils.isEmpty(servCategory) && !servCategory.equals(getString(R.string.select_category))) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("loginType", MappService.SERVICE_LOGIN);
                    SearchServProvForm mForm = new SearchServProvForm();
                    mForm.setCategory(mServCatagory.getSelectedItem().toString());
                    bundle.putParcelable("form", mForm);
                    mConnection.setData(bundle);
                    mConnection.setAction(MappService.DO_GET_SPECIALITY);
                    if (Utility.doServiceAction(ServProvProfileActivity.this, mConnection, BIND_AUTO_CREATE)) {
                        Utility.showProgress(getApplicationContext(), mFormView, mProgressView, true);
                    }
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
                if (!TextUtils.isEmpty(servCategory) && !servCategory.equals(getString(R.string.select_category))) {
                    if (spec.equals(getString(R.string.other))) {
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
                    wp.setServCategory(mServCatagory.getSelectedItem().toString());
                    wp.setSpeciality(mSpeciality.getSelectedItem().toString());
                    wp.setServPointType(mServPtType.getSelectedItem().toString());
                    wp.setConsultFee(Float.parseFloat(mConsultFee.getText().toString().trim()));
                    wp.setExperience(Float.parseFloat(mExperience.getText().toString().trim()));
                    wp.setQualification(mQualification.getText().toString().trim());
                    wp.setSignInData(mSignInData);
                    wp.setPincode(mPinCode.getText().toString().trim());
                    if(mConsultFee.isEnabled()) {
                        wp.setConsultFee(Float.parseFloat(mConsultFee.getText().toString().trim()));
                    }
                    if(finalAction == MappService.DO_EDIT_WORK_PLACE) {
                        wp.setIdServicePoint(item.getIdServicePoint());
                        wp.setIdService(item.getIdService());
                    }

                    sendRequest(finalAction, wp);
                    dialog.dismiss();
                }
            }
        });

        return dialog;
    }

    private boolean isValidWorkPlace() {
        EditText[] fields = { mExperience, mQualification,
                mName, mLoc, mPinCode, mPhone1, mConsultFee };
        /*ArrayList<EditText> list = new ArrayList<>(Arrays.asList(mExperience, mQualification,
                mName, mLoc, mPinCode, mPhone1));
        if (mCategory.equals(getString(R.string.pharmacist))) {
            list.add(mConsultFee);
        }*/
       /* if (mCategory.equals(getString(R.string.pharmacist))) {
            fields = new EditText[] { mExperience, mQualification,
                    mName, mLoc, mPinCode, mPhone1 };
        }*/
        if (Utility.areEditFieldsEmpty(this, fields)) {
            return false;
        }

        boolean valid = true;
        View focusView = null;

        String category = mServCatagory.getSelectedItem().toString();
        if (TextUtils.isEmpty(category) || category.equals(getString(R.string.select_category))) {
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

        if (mSpeciality.getSelectedItem() != null) {
            String spec = mSpeciality.getSelectedItem().toString();
            if (spec.equalsIgnoreCase(getString(R.string.select_speciality)) ||
                    spec.equals(getString(R.string.other))) {
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

        if (!Validator.isPhoneValid(mPhone1.getText().toString().trim())) {
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
        if (!TextUtils.isEmpty(email) && !Validator.isValidEmaillId(email)) {
            mEmailIdwork.setError(getString(R.string.error_invalid_email));
            focusView = mEmailIdwork;
            valid = false;
        }
        if (Validator.isPinCodeValid(mPinCode.getText().toString().trim())) {
            mPinCode.setError(getString(R.string.error_invalid_pincode));
            focusView = mPinCode;
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
        if (days.equalsIgnoreCase(getString(R.string.practice_days))) {
            mMultiSpinnerDays.setError(getString(R.string.error_field_required));
            focusView = mMultiSpinnerDays;
            valid = false;
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
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
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
        });
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
        final Activity activity = this;
        Utility.showAlert(activity, "", null, false,
                new String[]{activity.getString(R.string.take_photo),
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
                                Utility.showAlert(activity, "", getString(R.string.confirm_remove_photo), true,
                                        null,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                if (which == -1) {
                                                    Bundle bundle = new Bundle();
                                                    bundle.putInt("loginType", MappService.SERVICE_LOGIN);
                                                    bundle.putParcelable("service", mServiceProv);
                                                    mConnection.setData(bundle);
                                                    mConnection.setAction(MappService.DO_REMOVE_PHOTO);
                                                    if (Utility.doServiceAction(activity, mConnection, BIND_AUTO_CREATE)) {
                                                        Utility.showProgress(getApplicationContext(), mFormView, mProgressView, true);
                                                    }
                                                }
                                            }
                                        });
                        }

                    }
                });
    }

    public void removePhoto(View view) {
        final AlertDialog dialog = Utility.customDialogBuilder(this, null, R.string.confirm_remove_photo).create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest(MappService.DO_REMOVE_PHOTO, null);
                dialog.dismiss();
            }
        });
    }

    @Override
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
                    editIntent.setDataAndType(selectedImage, "image/*");
                    editIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(editIntent, resources.getInteger(R.integer.request_edit));
                } else {
                    mServiceProv.setPhoto(Utility.getBytesFromBitmap(((BitmapDrawable) mImgView.getDrawable()).getBitmap()));
                    sendRequest(MappService.DO_UPLOAD_PHOTO, null);

                    /*mServiceProv.setPhoto(Utility.getBytesFromBitmap(mImgView.getDrawingCache()));
                    sendRequest(MappService.DO_UPLOAD_PHOTO, null);*/
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utility.showMessage(this, R.string.some_error);
        }
    }

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
            Utility.showProgress(getApplicationContext(), mFormView, mProgressView, true);
        }
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
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

            default:
                return false;
        }
        return true;
    }

    private void changePwdDone(Bundle data) {
        Utility.showProgress(this, mFormView, mProgressView, false);
        if (data.getBoolean("status")) {
            Utility.showMessage(this, R.string.msg_change_pwd);
        } else {
            Utility.showMessage(this, R.string.some_error);
        }
    }

    private void pwdCheckDone(Bundle data) {
        Utility.showProgress(this, mFormView, mProgressView, false);
        if (data.getBoolean("status")) {
            isPwdCorrect = true;
        } else {
            isPwdCorrect = false;
            mOldPwd.setError(getString(R.string.error_wrong_pwd));
            mOldPwd.requestFocus();
        }
    }

    private void uploadPhotoDone(Bundle data) {
        Utility.showProgress(this, mFormView, mProgressView, false);
        if (data.getBoolean("status")) {
            Utility.showMessage(this, R.string.msg_upload_photo);
            LoginHolder.servLoginRef.setPhoto(Utility.getBytesFromBitmap(((BitmapDrawable) mImgView.getDrawable()).getBitmap()));
        } else {
            mImgView.setImageBitmap(mImgCopy);
            Utility.showMessage(this, R.string.some_error);
        }
    }

    private void getWorkPlaceListDone(Bundle data) {
        if (data.getBoolean("status")) {
            //Utility.showRegistrationAlert(this, "", "Problem in loading workplaces");
            mWorkPlaceList = data.getParcelableArrayList("workPlaceList");
            WorkPlaceListAdapter adapter = new WorkPlaceListAdapter(this,
                    R.layout.layout_wp_list_item, mWorkPlaceList);
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
            //listView.setSelection(adapter.getCount());
            //listView.setOverscrollFooter(getDrawable(R.drawable.up));
            listView.setAdapter(adapter);
            listView.setSelection(adapter.getCount());
            registerForContextMenu(listView);
            //listView.setOnCreateContextMenuListener(this);
            Utility.showMessage(this, R.string.work_place_details);
        }
        Utility.showProgress(this, mFormView, mProgressView, false);
    }

    public void regNoCheckDone(Bundle data) {
        if (data.getBoolean("exists")) {
            mRegNo.setError("This Registration Number is already Registered.");
            mRegNo.requestFocus();
        }
        Utility.showProgress(this, mFormView, mProgressView, false);
    }

    private void getSpecialitiesDone(Bundle data) {
        Utility.showProgress(this, mFormView, mProgressView, false);
        ArrayList<String> list = data.getStringArrayList("specialities");
        if (list == null) {
            list = new ArrayList<>();
        }
        Utility.setNewSpinner(this, list, mSpeciality, new String[]{getString(R.string.other)});
    }

    private void updateDone(int msg, Bundle data) {
        if (data.getBoolean("status")) {
            LoginHolder.servLoginRef = mServiceProv;
            Utility.showAlert(this, "", getString(msg), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    refresh();
                }
            });
        } else {
            mServiceProv = LoginHolder.servLoginRef;
            Utility.showMessage(this, R.string.some_error);
        }
        Utility.showProgress(this, mFormView, mProgressView, false);
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
