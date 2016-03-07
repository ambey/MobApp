package com.extenprise.mapp.medico.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.customer.activity.PatientSignUpActivity;
import com.extenprise.mapp.medico.customer.activity.SearchServProvActivity;
import com.extenprise.mapp.medico.net.MappService;
import com.extenprise.mapp.medico.net.MappServiceConnection;
import com.extenprise.mapp.medico.net.ResponseHandler;
import com.extenprise.mapp.medico.net.ServiceResponseHandler;
import com.extenprise.mapp.medico.service.data.ServiceProvider;
import com.extenprise.mapp.medico.ui.SignUpActionDialog;
import com.extenprise.mapp.medico.util.Utility;


public class SignUpPreviousActivity extends FragmentActivity implements ResponseHandler {
    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_previous);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
        }
    }

    public void signUpPatient(View view) {
        Intent intent = new Intent(this, PatientSignUpActivity.class);
        startActivity(intent);
    }

    private void showSignUpDialog(int id) {
        final SignUpActionDialog dialog = new SignUpActionDialog();
        dialog.setSignupBy(id);
        dialog.setContext(this);
        dialog.setSubmitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isInputValid()) {
                    ServiceProvider sp = dialog.getInputForm();
                    mConnection.setAction(MappService.DO_SIGNUP_REQ);
                    Bundle data = new Bundle();
                    data.putParcelable("form", sp);
                    mConnection.setData(data);
                    dialog.onDismiss(dialog.getDialog());
                    if (Utility.doServiceAction(SignUpPreviousActivity.this, mConnection, BIND_AUTO_CREATE)) {
                        Utility.showProgressDialog(SignUpPreviousActivity.this, true);
                    }
                }
            }
        });
        dialog.show(getSupportFragmentManager(), "Sign Up");
    }

    private void doneSignupReq(Bundle data) {
        Utility.showProgressDialog(this, false);
        if (data.getBoolean("status")) {
            Utility.showMessage(this, R.string.msg_sign_up);
        } else {
            Utility.showMessage(this, R.string.msg_can_not_proceed);
        }
    }

    public void signUpServProv(View view) {
        showSignUpDialog(R.string.practitioner);
    }

    public void signUpMedicalStore(View view) {
        showSignUpDialog(R.string.medical_store);
    }

    public void signUpDiagCenter(View view) {
        showSignUpDialog(R.string.scan_lab);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_doctor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //menu.removeItem(R.id.logout);
        menu.findItem(R.id.logout).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.action_settings:
                return true;
            case R.id.action_search:
                Utility.startActivity(this, SearchServProvActivity.class);
                return true;
            case R.id.action_sign_in:
                Utility.startActivity(this, LoginActivity.class);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        if (action == MappService.DO_SIGNUP_REQ) {
            doneSignupReq(data);
        }
        return data.getBoolean("status");
    }
}
