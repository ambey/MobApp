package com.extenprise.mapp.service.activity;

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

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.data.ReportServiceStatus;
import com.extenprise.mapp.data.RxFeedback;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.MappServiceConnection;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.data.RxInboxItem;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.Utility;

import java.util.ArrayList;

public class ServiceProviderHomeActivity extends Activity implements ResponseHandler {
    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));

    private ServiceProvider mServiceProv;
    private boolean exit = false;
    private TextView mMsgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_home);

        mServiceProv = LoginHolder.servLoginRef;

        mMsgView = (TextView) findViewById(R.id.msgView);

        TextView mlastDate = (TextView) findViewById(R.id.textViewDate);
        TextView mlastTime = (TextView) findViewById(R.id.textViewTime);

        SharedPreferences prefs = getSharedPreferences("servprov" + "lastVisit" + mServiceProv.getSignInData().getPhone(), MODE_PRIVATE);
        mlastDate.setText(prefs.getString("lastVisitDate", "--"));
        mlastTime.setText(prefs.getString("lastVisitTime", "--"));
        Utility.setLastVisit(prefs);

        TextView welcomeView = (TextView) findViewById(R.id.viewWelcomeLbl);
        String label = welcomeView.getText().toString() + " " +
                mServiceProv.getfName() + " " +
                mServiceProv.getlName();

        welcomeView.setText(label);

        ImageView img = (ImageView) findViewById(R.id.imageDoctor);
        if (mServiceProv.getPhoto() != null) {
            img.setImageBitmap(Utility.getBitmapFromBytes(mServiceProv.getPhoto()));
        }
        //Utility.setLastVisited(this);
    }

    public void viewAppointment(View view) {
        Intent intent = new Intent(this, ViewAppointmentListActivity.class);
        intent.putExtra("service", mServiceProv);
        startActivity(intent);
    }

    public void viewProfile(View view) {
        Intent intent = new Intent(this, ServProvProfileActivity.class);
        intent.putExtra("service", mServiceProv);
        intent.putExtra("category", "Practitioner");
        startActivity(intent);
    }

    public void viewRxFeedback(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", mServiceProv.getServProvHasServPt(0).getIdServProvHasServPt());
        //SPSSPT ID is not fetching from server.. so its giving null Exception.
        //TODO
        bundle.putInt("status", ReportServiceStatus.STATUS_FEEDBACK_SENT.ordinal());
        mConnection.setAction(MappService.DO_GET_RX_FEEDBACK);
        mConnection.setData(bundle);
        mMsgView.setVisibility(View.VISIBLE);
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_service_provider_home, menu);
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

    @Override
    public void onBackPressed() {
        mConnection.setBound(false);
        if (exit) {
            Log.v("onBackPressed", "ServiceProviderHomeActivity called.. calling finish.");
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
        /*Intent intent = super.getParentActivityIntent();
        if(intent == null) {
            return null;
        }
        intent.putParcelableArrayListExtra("inbox", getIntent().getParcelableArrayListExtra("inbox"));*/
        return null;
    }

    private void gotRxInbox(Bundle data) {
        mMsgView.setVisibility(View.GONE);
        ArrayList<RxInboxItem> list = data.getParcelableArrayList("inbox");
        Intent intent = new Intent(this, RxListActivity.class);
        intent.putParcelableArrayListExtra("inbox", list);
        intent.putExtra("feedback", RxFeedback.VIEW_FEEDBACK.ordinal());
        intent.putExtra("parent-activity", getClass().getName());
        startActivity(intent);
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        if (action == MappService.DO_GET_RX_FEEDBACK) {
            gotRxInbox(data);
            return true;
        }
        return false;
    }
}
