package com.extenprise.mapp.medico.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.extenprise.mapp.medico.data.ReportServiceStatus;
import com.extenprise.mapp.medico.data.RxFeedback;
import com.extenprise.mapp.medico.data.WorkingDataStore;
import com.extenprise.mapp.medico.net.MappService;
import com.extenprise.mapp.medico.net.MappServiceConnection;
import com.extenprise.mapp.medico.net.ResponseHandler;
import com.extenprise.mapp.medico.net.ServiceResponseHandler;
import com.extenprise.mapp.medico.service.data.RxInboxItem;
import com.extenprise.mapp.medico.service.data.ServiceProvider;
import com.extenprise.mapp.medico.util.Utility;

import java.util.ArrayList;

public class ServiceProviderHomeActivity extends Activity implements ResponseHandler {
    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));

    private ServiceProvider mServiceProv;
    private boolean exit = false;
    private TextView mMsgView;
    private TextView mWelcomeView;
    private ImageView mImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_home);

        mServiceProv = WorkingDataStore.getBundle().getParcelable("servProv");

        mMsgView = (TextView) findViewById(R.id.msgView);
        mWelcomeView = (TextView) findViewById(R.id.viewWelcomeLbl);
        mImgView = (ImageView) findViewById(R.id.imageDoctor);

        profile();

        TextView lastVisited = (TextView) findViewById(R.id.lastVisitedView);
        SharedPreferences prefs = getSharedPreferences("servprov" + "lastVisit" + mServiceProv.getSignInData().getPhone(), MODE_PRIVATE);
        lastVisited.setText(String.format("%s %s %s",
                getString(R.string.last_visited),
                prefs.getString("lastVisitDate", "--"),
                prefs.getString("lastVisitTime", "--")));
        Utility.setLastVisit(prefs);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mServiceProv = WorkingDataStore.getBundle().getParcelable("servProv");
        profile();
    }

    private void profile() {
        String mServPointType = mServiceProv.getServProvHasServPt(0).getServPointType();
        String label = getString(R.string.hello_dr);
        if (!mServPointType.equalsIgnoreCase(getString(R.string.clinic))) {
            label = getString(R.string.hello);
        }
        mWelcomeView.setText(String.format("%s %s %s", label,
                mServiceProv.getfName(), mServiceProv.getlName()));

        if (mServiceProv.getPhoto() != null) {
            mImgView.setImageBitmap(Utility.getBitmapFromBytes(mServiceProv.getPhoto(),
                    mImgView.getLayoutParams().width, mImgView.getLayoutParams().height));
        }
    }

    public void viewAppointment(View view) {
        Intent intent = new Intent(this, ViewAppointmentListActivity.class);
        intent.putExtra("servProv", mServiceProv);
        startActivity(intent);
    }

    public void viewProfile(View view) {
        Intent intent = new Intent(this, ServProvProfileActivity.class);
        intent.putExtra("servProv", mServiceProv);
        intent.putExtra("category", getString(R.string.physician));
        startActivity(intent);
    }

    public void viewRxFeedback(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("phone", mServiceProv.getSignInData().getPhone());
        bundle.putInt("status", ReportServiceStatus.STATUS_FEEDBACK_SENT.ordinal());
        mConnection.setAction(MappService.DO_GET_RX_FEEDBACK);
        mConnection.setData(bundle);
        mMsgView.setVisibility(View.VISIBLE);
        if (Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE)) {
            Utility.showProgressDialog(this, true);
        }
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
            case R.id.action_settings:
                break;
            case R.id.logout:
                Utility.logout(getSharedPreferences("loginPrefs", MODE_PRIVATE), this, LoginActivity.class);
                WorkingDataStore.getBundle().remove("servProv");
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
        Utility.showProgressDialog(this, false);
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
