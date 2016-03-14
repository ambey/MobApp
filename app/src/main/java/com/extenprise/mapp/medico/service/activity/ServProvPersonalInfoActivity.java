package com.extenprise.mapp.medico.service.activity;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.activity.LoginActivity;
import com.extenprise.mapp.medico.data.WorkingDataStore;
import com.extenprise.mapp.medico.net.MappService;
import com.extenprise.mapp.medico.net.MappServiceConnection;
import com.extenprise.mapp.medico.net.ResponseHandler;
import com.extenprise.mapp.medico.net.ServiceResponseHandler;
import com.extenprise.mapp.medico.service.data.ServiceProvider;
import com.extenprise.mapp.medico.util.Utility;
import com.extenprise.mapp.medico.util.Validator;

public class ServProvPersonalInfoActivity extends FragmentActivity implements ResponseHandler {
    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));

    private EditText mEmailID;
    private EditText mRegNo;
    private EditText mFname;
    private EditText mLname;
    private RadioGroup mGender;
    private RadioButton mFemale;
    private RadioButton mGenderBtn;

    private ServiceProvider mServiceProv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serv_prov_personal_info);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mServiceProv = (ServiceProvider) WorkingDataStore.getLoginRef();

        findViewById(R.id.editTextCellphone).setVisibility(View.GONE);
        findViewById(R.id.editTextPasswd).setVisibility(View.GONE);
        findViewById(R.id.editTextCnfPasswd).setVisibility(View.GONE);
        findViewById(R.id.phonePrefix).setVisibility(View.GONE);

        mFname = (EditText) findViewById(R.id.editTextFName);
        mLname = (EditText) findViewById(R.id.editTextLName);
        mLname.setNextFocusForwardId(R.id.editTextEmail);
        mEmailID = (EditText) findViewById(R.id.editTextEmail);
        mEmailID.setNextFocusForwardId(R.id.editTextRegistrationNumber);
        mGender = (RadioGroup) findViewById(R.id.radioGroupGender);
        mFemale = (RadioButton) findViewById(R.id.radioButtonFemale);
        RadioButton mMale = (RadioButton) findViewById(R.id.radioButtonMale);

        mRegNo = (EditText) findViewById(R.id.editTextRegistrationNumber);
        mRegNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!TextUtils.isEmpty(mRegNo.getText().toString().trim())) {
                        sendRequest(MappService.DO_REG_NO_CHECK);
                    }
                }
            }
        });

        TextView mPhone = (TextView) findViewById(R.id.mobnumValue);
        mPhone.setText(mServiceProv.getSignInData().getPhone());
        mFname.setText(mServiceProv.getfName());
        mLname.setText(mServiceProv.getlName());
        String email = mServiceProv.getEmailId();
        if (email != null) {
            mEmailID.setText(email);
        }
        mRegNo.setText(mServiceProv.getRegNo());
        if (mServiceProv.getGender().equalsIgnoreCase("Male")) {
            mMale.setChecked(true);
        } else {
            mFemale.setChecked(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.action_settings:
                break;
            case R.id.logout:
                Utility.logout(getSharedPreferences("loginPrefs", MODE_PRIVATE), this, LoginActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateProfile(View view) {
        boolean cancel = false;
        View focusView = null;

        if (Utility.areEditFieldsEmpty(this, new EditText[]{mRegNo})) {
            cancel = true;
        }
        int val = mGender.getCheckedRadioButtonId();
        if (val == -1) {
            mFemale.setError(getString(R.string.error_select_gender));
            focusView = mFemale;
            cancel = true;
        } else {
            mGenderBtn = (RadioButton) findViewById(val);
        }
        String email = mEmailID.getText().toString().trim();
        if (!TextUtils.isEmpty(email) && !Validator.isValidEmaillId(email)) {
            mEmailID.setError(getString(R.string.error_invalid_email));
            focusView = mEmailID;
            cancel = true;
        }

        if (!Utility.isNameValid(this, mFname, mLname)) {
            focusView = null;
            cancel = true;
        }

        if (cancel) {
            if (focusView != null) {
                focusView.requestFocus();
            }
            return;
        }

        sendRequest(MappService.DO_UPDATE);
    }

    private ServiceProvider getProfileData(ServiceProvider sp) {
        if (sp == null) {
            sp = (ServiceProvider) WorkingDataStore.getLoginRef();
        }
        sp.setfName(mFname.getText().toString().trim());
        sp.setlName(mLname.getText().toString().trim());
        sp.setEmailId(mEmailID.getText().toString().trim());
        sp.setGender(mGenderBtn.getText().toString());
        sp.setRegNo(mRegNo.getText().toString());

        return sp;
    }

    private void sendRequest(int action) {
        Bundle bundle = new Bundle();
        bundle.putInt("loginType", MappService.SERVICE_LOGIN);
        bundle.putParcelable("service", getProfileData(mServiceProv));
        mConnection.setData(bundle);
        mConnection.setAction(action);
        if (Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE)) {
            Utility.showProgressDialog(this, true);
        }
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        Utility.showProgressDialog(this, false);
        switch (action) {
            case MappService.DO_UPDATE:
                updateDone(R.string.msg_update_profile_done, data);
                break;
            case MappService.DO_REG_NO_CHECK:
                regNoCheckDone(data);
                break;
        }
        return data.getBoolean("status");
    }

    private void updateDone(int msg, Bundle data) {
        if (data.getBoolean("status")) {
            Utility.showAlert(this, "", getString(msg), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            ServiceProvider serviceProvider = (ServiceProvider) WorkingDataStore.getLoginRef();
            getProfileData(serviceProvider);
        }
    }

    public void regNoCheckDone(Bundle data) {
        if (data.getBoolean("exists")) {
            mRegNo.setError(getString(R.string.error_duplicate_reg_no));
            mRegNo.requestFocus();
        }
    }
}
