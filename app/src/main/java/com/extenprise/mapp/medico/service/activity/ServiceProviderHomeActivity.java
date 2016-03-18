package com.extenprise.mapp.medico.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import com.extenprise.mapp.medico.service.data.ServiceProvider;
import com.extenprise.mapp.medico.ui.BackButtonHandler;
import com.extenprise.mapp.medico.util.ByteArrayToBitmapTask;
import com.extenprise.mapp.medico.util.Utility;

public class ServiceProviderHomeActivity extends Activity implements ResponseHandler {
    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));

    private TextView mMsgView;
    private TextView mWelcomeView;
    private ImageView mImgView;
    private ServiceProvider mServiceProvider;
    private TextView mLastVisited;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_home);

        mMsgView = (TextView) findViewById(R.id.msgView);
        mWelcomeView = (TextView) findViewById(R.id.viewWelcomeLbl);
        mImgView = (ImageView) findViewById(R.id.imageDoctor);
        mLastVisited = (TextView) findViewById(R.id.lastVisitedView);

        /*SharedPreferences prefs = getSharedPreferences("servprov" + "lastVisit" + mServiceProvider.getSignInData().getPhone(), MODE_PRIVATE);
        lastVisited.setText(String.format("%s %s %s",
                getString(R.string.last_visited),
                prefs.getString("lastVisitDate", "--"),
                prefs.getString("lastVisitTime", "--")));*/
        //Utility.setLastVisit(prefs);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mServiceProvider = (ServiceProvider) WorkingDataStore.getLoginRef();
        if (!(mServiceProvider != null && mServiceProvider.getServiceCount() > 0)) {
            Utility.sessionExpired(this);
            return;
        }
        profile();
    }

    private void profile() {
        String lastVisit = mServiceProvider.getLastVisit();
        if (lastVisit == null || lastVisit.equals("")) {
            mLastVisited.setText(String.format("%s - -", getString(R.string.last_visited)));
        } else {
            mLastVisited.setText(String.format("%s %s", getString(R.string.last_visited),
                    Utility.getDateAsStr(
                            Utility.getStrAsDate(lastVisit, "yyyy-MM-dd HH:mm"),
                            "dd/MM/yyyy HH:mm")));
        }

        String label = getString(R.string.hello_dr);
        int defaultImg = R.drawable.dr_avatar;
        if (getCategory() == R.string.diagnosticCenter) {
            label = getString(R.string.hello);
            defaultImg = R.drawable.diagcenter;
        }
        mWelcomeView.setText(String.format("%s %s %s", label,
                mServiceProvider.getfName(), mServiceProvider.getlName()));

        if (mServiceProvider.getPhoto() != null) {
            ByteArrayToBitmapTask task = new ByteArrayToBitmapTask(mImgView,
                    mServiceProvider.getPhoto(),
                    mImgView.getLayoutParams().width, mImgView.getLayoutParams().height);
            task.execute();
        } else {
            mImgView.setImageResource(defaultImg);
        }
    }

    private int getCategory() {
        int category = R.string.physician;
        if (!mServiceProvider.getServProvHasServPt(0).getServPointType().
                equalsIgnoreCase(getString(R.string.clinic))) {
            category = R.string.diagnosticCenter;
        }
        return category;
    }

    public void viewAppointment(View view) {
        Utility.startActivity(this, ViewAppointmentListActivity.class, true);
    }

    public void viewProfile(View view) {
        Intent intent = new Intent(this, ServProvProfileActivity.class);
        intent.putExtra("category", getString(getCategory()));
        startActivity(intent);
    }

    public void viewRxFeedback(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("phone", mServiceProvider.getSignInData().getPhone());
        bundle.putInt("status", ReportServiceStatus.STATUS_FEEDBACK_SENT);
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

    @Override
    public void onBackPressed() {
        mConnection.setBound(false);
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
        return null;
    }

    private void gotRxInbox(Bundle data) {
        Utility.showProgressDialog(this, false);
        mMsgView.setVisibility(View.GONE);
        Bundle bundle = WorkingDataStore.getBundle();
        bundle.putParcelableArrayList("inbox", data.getParcelableArrayList("inbox"));
        Intent intent = new Intent(this, RxListActivity.class);
        intent.putExtra("feedback", RxFeedback.VIEW_FEEDBACK);
        intent.putExtra("parent-activity", getClass().getName());
        intent.putExtra("category", getCategory());
        startActivity(intent);
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        if (action == MappService.DO_GET_RX_FEEDBACK) {
            gotRxInbox(data);
        }
        return data.getBoolean("status");
    }
}
