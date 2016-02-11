package com.extenprise.mapp.medico.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.extenprise.mapp.medico.LoginHolder;
import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.data.RxFeedback;
import com.extenprise.mapp.medico.net.MappService;
import com.extenprise.mapp.medico.net.MappServiceConnection;
import com.extenprise.mapp.medico.net.ResponseHandler;
import com.extenprise.mapp.medico.net.ServiceResponseHandler;
import com.extenprise.mapp.medico.service.data.RxInboxItem;
import com.extenprise.mapp.medico.service.data.ServiceProvider;
import com.extenprise.mapp.medico.util.Utility;

import java.util.ArrayList;


public class MedicalStoreHomeActivity extends Activity implements ResponseHandler {
    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));

    private ServiceProvider mServProv;
    private Boolean exit = false;
    private TextView mMsgView;
    private TextView mWelcomeView;
    private boolean mReqSent;
    private ImageView mImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_store_home);

        mMsgView = (TextView)findViewById(R.id.msgView);
        mImg = (ImageView) findViewById(R.id.imageMedstore);
        mWelcomeView = (TextView) findViewById(R.id.viewWelcomeLbl);

        mServProv = LoginHolder.servLoginRef;
        if (mServProv == null) {
            Utility.goTOLoginPage(this);
        }

        /*extView mlastDate = (TextView) findViewById(R.id.textViewDate);
        TextView mlastTime = (TextView) findViewById(R.id.textViewTime);

        SharedPreferences prefs = getSharedPreferences("lastVisit", MODE_PRIVATE);
        Boolean saveVisit = prefs.getBoolean("saveVisit", false);
        if(saveVisit) {
            mlastDate.setText(prefs.getString("Date", ""));
            mlastTime.setText(prefs.getString("Time", ""));
        } else {
            Utility.setCurrentDateOnView(mlastDate);
            Utility.setCurrentTimeOnView(mlastTime);
        }*/
        try {
            TextView lastVisited = (TextView) findViewById(R.id.lastVisitedView);
            SharedPreferences prefs = getSharedPreferences("servprov" + "lastVisit" + mServProv.getSignInData().getPhone(), MODE_PRIVATE);
            lastVisited.setText(String.format("%s %s %s",
                    getString(R.string.last_visited),
                    prefs.getString("lastVisitDate", "--"),
                    prefs.getString("lastVisitTime", "--")));
            Utility.setLastVisit(prefs);
        } catch (Exception e) {
            Utility.goTOLoginPage(this);
        }
        profile();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mReqSent) {
            profile();
        }
    }

    private void profile() {
        mServProv = LoginHolder.servLoginRef;
        if (mServProv == null) {
            Utility.goTOLoginPage(this);
        }

        String label = mWelcomeView.getText().toString() + " " +
                mServProv.getfName() + " " +
                mServProv.getlName();
        mWelcomeView.setText(label);

        if (mServProv.getPhoto() != null) {
            mImg.setImageBitmap(Utility.getBitmapFromBytes(mServProv.getPhoto()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_medical_store_home, menu);
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
                break;
            case R.id.action_settings:
                break;
            case R.id.logout:
                Utility.logout(getSharedPreferences("loginPrefs", MODE_PRIVATE), this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void viewRxInbox(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("phone", mServProv.getSignInData().getPhone());
        mConnection.setAction(MappService.DO_GET_RX_INBOX);
        mConnection.setData(bundle);
        mMsgView.setVisibility(View.VISIBLE);
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
    }

    private void gotRxInbox(Bundle data) {
        mMsgView.setVisibility(View.GONE);
        ArrayList<RxInboxItem> list = data.getParcelableArrayList("inbox");
        Intent intent = new Intent(this, RxListActivity.class);
        intent.putParcelableArrayListExtra("inbox", list);
        intent.putExtra("feedback", RxFeedback.GIVE_FEEDBACK.ordinal());
        intent.putExtra("parent-activity", getClass().getName());
        startActivity(intent);
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        if (action == MappService.DO_GET_RX_INBOX) {
            gotRxInbox(data);
            return true;
        }
        return false;
    }

    public void viewProfile(View view) {
        mReqSent = true;
        Intent intent = new Intent(this, ServProvProfileActivity.class);
        intent.putExtra("service", mServProv);
        intent.putExtra("category", getString(R.string.pharmacist));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        mConnection.setBound(false);
        if (exit) {
            Log.v("onBackPressed", "MedicalStoreHomeActivity called.. calling finish.");
            finish(); // finish activity
            moveTaskToBack(true); // exist app
            //finish(); // finish activity
        } else {
            Utility.showMessage(this, R.string.msg_press_back_button);
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        return null;
    }
}
