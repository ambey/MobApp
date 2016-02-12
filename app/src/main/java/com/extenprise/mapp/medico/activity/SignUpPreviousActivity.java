package com.extenprise.mapp.medico.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.customer.activity.PatientSignUpActivity;
import com.extenprise.mapp.medico.customer.activity.SearchServProvActivity;
import com.extenprise.mapp.medico.ui.SignUpActionDialog;


public class SignUpPreviousActivity extends FragmentActivity {

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

    private void showSignUpDialog() {
        SignUpActionDialog dialog = new SignUpActionDialog();
        dialog.setContext(this);
        dialog.show(getSupportFragmentManager(), "Sign Up");
    }

    public void signUpServProv(View view) {
        showSignUpDialog();
/*
        Intent intent = new Intent(this, ServProvSignUpActivity.class);
        intent.putExtra("category", R.string.physician);
        startActivity(intent);
*/
    }

    public void signUpMedicalStore(View view) {
        showSignUpDialog();
/*
        Intent intent = new Intent(this, ServProvSignUpActivity.class);
        intent.putExtra("category", R.string.pharmacist);
        startActivity(intent);
*/
    }

    public void signUpDiagCenter(View view) {
        showSignUpDialog();
/*
        Intent intent = new Intent(this, ServProvSignUpActivity.class);
        intent.putExtra("category", R.string.diagnosticCenter);
        startActivity(intent);
*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_sign_up_previous, menu);
        //return true;
        getMenuInflater().inflate(R.menu.menu_search_doctor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_search:
                Intent intent = new Intent(this, SearchServProvActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_sign_in:
                Intent intent1 = new Intent(this, LoginActivity.class);
                startActivity(intent1);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
