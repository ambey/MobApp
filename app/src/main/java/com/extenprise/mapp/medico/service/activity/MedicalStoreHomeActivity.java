package com.extenprise.mapp.medico.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.extenprise.mapp.medico.data.RxFeedback;
import com.extenprise.mapp.medico.data.WorkingDataStore;
import com.extenprise.mapp.medico.net.MappService;
import com.extenprise.mapp.medico.net.MappServiceConnection;
import com.extenprise.mapp.medico.net.ResponseHandler;
import com.extenprise.mapp.medico.net.ServiceResponseHandler;
import com.extenprise.mapp.medico.service.data.RxInboxItem;
import com.extenprise.mapp.medico.service.data.ServiceProvider;
import com.extenprise.mapp.medico.ui.BackButtonHandler;
import com.extenprise.mapp.medico.util.Utility;

import java.util.ArrayList;


public class MedicalStoreHomeActivity extends Activity implements ResponseHandler {
    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));

    private ServiceProvider mServProv;
    private TextView mMsgView;
    private ImageView mImgView;
    private TextView mWelcomeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_store_home);

        mMsgView = (TextView) findViewById(R.id.msgView);
        mImgView = (ImageView) findViewById(R.id.imageMedstore);
        mWelcomeView = (TextView) findViewById(R.id.viewWelcomeLbl);

        mServProv = WorkingDataStore.getBundle().getParcelable("servProv");
        try {
            TextView lastVisited = (TextView) findViewById(R.id.lastVisitedView);
            SharedPreferences prefs = getSharedPreferences("servprov" + "lastVisit" + mServProv.getSignInData().getPhone(), MODE_PRIVATE);
            lastVisited.setText(String.format("%s %s %s",
                    getString(R.string.last_visited),
                    prefs.getString("lastVisitDate", "--"),
                    prefs.getString("lastVisitTime", "--")));
            Utility.setLastVisit(prefs);
        } catch (Exception e) {
            Utility.goTOLoginPage(this, LoginActivity.class);
            return;
        }

        profile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mServProv = WorkingDataStore.getBundle().getParcelable("servProv");
        profile();
    }

    private void profile() {
        mWelcomeView.setText(String.format("%s %s %s", getString(R.string.hello),
                mServProv.getfName(), mServProv.getlName()));
        mImgView.setImageResource(R.drawable.medstore);
        if (mServProv.getPhoto() != null) {
            mImgView.setImageBitmap(Utility.getBitmapFromBytes(mServProv.getPhoto(),
                    mImgView.getLayoutParams().width, mImgView.getLayoutParams().height));
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
            case R.id.action_settings:
                break;
            case R.id.logout:
                Utility.logout(getSharedPreferences("loginPrefs", MODE_PRIVATE), this, LoginActivity.class);
                WorkingDataStore.getBundle().remove("servProv");
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
        if (Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE)) {
            Utility.showProgressDialog(this, true);
        }
    }

    private void gotRxInbox(Bundle data) {
        Utility.showProgressDialog(this, false);
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
        Intent intent = new Intent(this, ServProvProfileActivity.class);
        intent.putExtra("service", mServProv);
        intent.putExtra("category", getString(R.string.pharmacist));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        mConnection.setBound(false);
        final BackButtonHandler buttonHandler = BackButtonHandler.getInstance();
        if (buttonHandler.isBackPressed()) {
            buttonHandler.setBackPressed(false);
            finish();
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
}
