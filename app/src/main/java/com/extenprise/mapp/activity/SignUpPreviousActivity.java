package com.extenprise.mapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.extenprise.mapp.R;
import com.extenprise.mapp.customer.activity.PatientSignUpActivity;
import com.extenprise.mapp.service.activity.SearchServProvActivity;
import com.extenprise.mapp.service.activity.ServProvSignUpActivity;


public class SignUpPreviousActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_previous);
    }

    public void signUpPatient(View view) {
        Intent intent = new Intent(this, PatientSignUpActivity.class);
        startActivity(intent);
    }

    public void signUpServProv(View view) {
        Intent intent = new Intent(this, ServProvSignUpActivity.class);
        intent.putExtra("category", R.string.practitionar);
        startActivity(intent);
    }

    public void signUpMedicalStore(View view) {
        Intent intent = new Intent(this, ServProvSignUpActivity.class);
        intent.putExtra("category", R.string.medicalStore);
        startActivity(intent);
    }

    public void signUpDiagCenter(View view) {
        Intent intent = new Intent(this, ServProvSignUpActivity.class);
        intent.putExtra("category", R.string.diagnosticCenter);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_sign_up_previous, menu);
        //return true;
        getMenuInflater().inflate(R.menu.menu_search_doctor, menu);
        return true;
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
