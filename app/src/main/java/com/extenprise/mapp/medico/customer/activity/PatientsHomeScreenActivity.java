package com.extenprise.mapp.medico.customer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.activity.LoginActivity;
import com.extenprise.mapp.medico.customer.data.Customer;
import com.extenprise.mapp.medico.data.WorkingDataStore;
import com.extenprise.mapp.medico.ui.BackButtonHandler;
import com.extenprise.mapp.medico.util.ByteArrayToBitmapTask;
import com.extenprise.mapp.medico.util.Utility;


public class PatientsHomeScreenActivity extends Activity {

    private TextView mWelcomeView;
    private ImageView mImgView;
    private Customer mCustomer;
    private TextView mLastVisited;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_home_screen);

        mWelcomeView = (TextView) findViewById(R.id.viewWelcomeLbl);
        mImgView = (ImageView) findViewById(R.id.imagePatient);
        mLastVisited = (TextView) findViewById(R.id.lastVisitedView);
       /* try {
            SharedPreferences prefs = getSharedPreferences("customer" + "lastVisit" +
                    mCustomer.getSignInData().getPhone(), MODE_PRIVATE);
            lastVisited.setText(String.format("%s %s %s",
                    getString(R.string.last_visited),
                    prefs.getString("lastVisitDate", "--"),
                    prefs.getString("lastVisitTime", "--")));
            //Utility.setLastVisit(prefs);
        } catch (Exception e) {
            e.printStackTrace();
            //Utility.startActivity(this, LoginActivity.class);
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCustomer = WorkingDataStore.getBundle().getParcelable("customer");
        profile();
    }

    private void profile() {
        mWelcomeView.setText(String.format("%s %s %s", getString(R.string.hello),
                mCustomer.getfName(), mCustomer.getlName()));
        String lastVisit = mCustomer.getLastVisit();
        if (lastVisit == null || lastVisit.equals("")) {
            mLastVisited.setText(String.format("%s - -", getString(R.string.last_visited)));
        } else {
            mLastVisited.setText(String.format("%s %s", getString(R.string.last_visited),
                    Utility.getDateAsStr(
                            Utility.getStrAsDate(lastVisit, "yyyy-MM-dd HH:mm"),
                            "dd/MM/yyyy HH:mm")));
        }

        if (mCustomer.getPhoto() != null) {
            ByteArrayToBitmapTask task = new ByteArrayToBitmapTask(mImgView, mCustomer.getPhoto(),
                    mImgView.getLayoutParams().width, mImgView.getLayoutParams().height);
            task.execute();
        } else {
            mImgView.setImageResource(R.drawable.patient);
        }
    }

    public void enlargeImg(View view) {
        Utility.enlargeImg(this, mImgView);
        //mImgView.setSystemUiVisibility( View.SYSTEM_UI_FLAG_HIDE_NAVIGATION );
    }

    public void viewRxList(View view) {
        Utility.startActivity(this, ViewRxListActivity.class, true);
    }

    public void viewAppointments(View view) {
        Utility.startActivity(this, ViewAppointmentListActivity.class, true);
    }

    public void viewProfile(View view) {
        Utility.startActivity(this, PatientProfileActivity.class, false);
    }

    public void searchDoc(View view) {
        Intent intent = new Intent(this, SearchServProvActivity.class);
        intent.putExtra("parent-activity", this.getClass().getName());
        startActivity(intent);
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
        /*int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.action_settings:
                return true;
            case R.id.logout:
                Utility.logout(getSharedPreferences("loginPrefs", MODE_PRIVATE), this, LoginActivity.class);
                WorkingDataStore.getBundle().remove("customer");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        final BackButtonHandler buttonHandler = BackButtonHandler.getInstance();
        if (buttonHandler.isBackPressed()) {
            buttonHandler.setBackPressed(false);
            //finish();
            moveTaskToBack(true);
        } else {
            buttonHandler.setBackPressed(true);
            Utility.showMessage(this, R.string.msg_press_back_button);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    buttonHandler.setBackPressed(false);
                }
            }, 3 * 1000);
        }
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        /*Intent intent = super.getParentActivityIntent();
        if(intent == null) {
            return null;
        }
        intent.putParcelableArrayListExtra("inbox", getIntent().getParcelableArrayListExtra("inbox"));*/
        Log.v("getParentActivityIntent", "PatientsHomeScreenActivity called");
        return null;
    }

    /*private void refresh() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }*/
}
