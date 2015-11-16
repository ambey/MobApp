package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.util.Utility;


public class ServProvBasicInfoActivity extends Activity {

    EditText mMobNo, mEmailID, mRegNo;
    Spinner mGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servprov_basicinfo);

        mMobNo = (EditText) findViewById(R.id.editTextMobNum);
        mEmailID = (EditText) findViewById(R.id.editTextEmail);
        //mGender = (Spinner) findViewById(R.id.editTextGender);
        mRegNo = (EditText) findViewById(R.id.editTextRegNum);

        mMobNo.setText(LoginHolder.servLoginRef.getPhone());
        mEmailID.setText(LoginHolder.servLoginRef.getEmailId());
        mRegNo.setText(LoginHolder.servLoginRef.getRegNo());
    }

    public void editConInfo(View view) {
        mMobNo.setEnabled(true);
        mEmailID.setEnabled(true);
    }

    public void editBasicInfo(View view) {
        mRegNo.setEnabled(true);
    }

    public void updateInfo(View view) {

        MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String where = MappContract.ServiceProvider._ID + " = ? ";
        String[] selectionArgs = {
                "" + LoginHolder.servLoginRef.getIdServiceProvider()
        };

        ContentValues values = new ContentValues();
        values.put(MappContract.ServiceProvider.COLUMN_NAME_CELLPHONE, mMobNo.getText().toString());
        values.put(MappContract.ServiceProvider.COLUMN_NAME_EMAIL_ID, mEmailID.getText().toString());
        values.put(MappContract.ServiceProvider.COLUMN_NAME_REGISTRATION_NUMBER, mRegNo.getText().toString());

        int affectedRows = db.update(MappContract.ServiceProvider.TABLE_NAME, values, where, selectionArgs);

        if(affectedRows != 0) {
            mRegNo.setEnabled(false);
            mMobNo.setEnabled(false);
            mEmailID.setEnabled(false);

            Utility.showAlert(this, "", "Done");
        } else {
            Utility.showAlert(this, "", "Not Done");
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_profile_basicinfo, menu);
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
}
