package com.extenprise.mapp.service.activity;

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
import android.widget.Toast;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.data.ReportServiceStatus;
import com.extenprise.mapp.net.AppStatus;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.MappServiceConnection;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.data.RxInboxItem;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.Utility;

import java.util.ArrayList;

public class ServiceProviderHomeActivity extends Activity implements ResponseHandler {
    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this));

    private ServiceProvider mServiceProv;
    private Boolean exit = false;
    private TextView mlastDate;
    private TextView mlastTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_home);

        mlastDate = (TextView) findViewById(R.id.textViewDate);
        mlastTime = (TextView) findViewById(R.id.textViewTime);

        SharedPreferences prefs = getSharedPreferences("lastVisit", MODE_PRIVATE);
        Boolean saveVisit = prefs.getBoolean("saveVisit", false);
        if(saveVisit) {
            mlastDate.setText(prefs.getString("Date", ""));
            mlastTime.setText(prefs.getString("Time", ""));
        } else {
            Utility.setCurrentDateOnView(mlastDate);
            Utility.setCurrentTimeOnView(mlastTime);
        }

        mServiceProv = LoginHolder.servLoginRef;

        TextView welcomeView = (TextView) findViewById(R.id.viewWelcomeLbl);
        String label = welcomeView.getText().toString() + " " +
                mServiceProv.getfName() + " " +
                mServiceProv.getlName();

        welcomeView.setText(label);

        ImageView img = (ImageView) findViewById(R.id.imageDoctor);
        if (mServiceProv.getImg() != null) {
            img.setImageBitmap(Utility.getBitmapFromBytes(mServiceProv.getImg()));
        }

        Utility.setLastVisited(this);
    }

    public void viewAppointment(View view) {
        Intent intent = new Intent(this, ViewAppointmentListActivity.class);
        intent.putExtra("service", mServiceProv);
        startActivity(intent);
    }

    public void viewProfile(View view) {
        Intent intent = new Intent(this, ServProvProfileActivity.class);
        intent.putExtra("service", mServiceProv);
        startActivity(intent);
    }

    public void viewRxFeedback(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", mServiceProv.getServProvHasServPt(0).getIdServProvHasServPt());
        bundle.putInt("status", ReportServiceStatus.STATUS_FEEDBACK_SENT.ordinal());
        mConnection.setAction(MappService.DO_GET_RX_FEEDBACK);
        mConnection.setData(bundle);
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_service_provider_home, menu);
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

    @Override
    public void onBackPressed() {
        if (exit) {
            Log.v("onBackPressed", "PatientsHomeScreenActivity called.. calling finish.");
            finish(); // finish activity
            moveTaskToBack(true); // exist app
            //finish(); // finish activity
        } else {
            Utility.showMessage(this, R.string.press_back_button);
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
        ArrayList<RxInboxItem> list = data.getParcelableArrayList("inbox");
        Intent intent = new Intent(this, RxListActivity.class);
        intent.putParcelableArrayListExtra("inbox", list);
        intent.putExtra("feedback", true);
        startActivity(intent);
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        try {
            unbindService(mConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (action == MappService.DO_GET_RX_FEEDBACK) {
            gotRxInbox(data);
            return true;
        }
        return false;
    }
}
