package com.extenprise.mapp.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.customer.activity.PatientsHomeScreenActivity;
import com.extenprise.mapp.customer.data.Customer;
import com.extenprise.mapp.data.SignInData;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.MappServiceConnection;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.activity.MedicalStoreHomeActivity;
import com.extenprise.mapp.service.activity.ServiceProviderHomeActivity;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.Utility;


public class WelcomeActivity extends Activity implements ResponseHandler {

    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));

    private int mLoginType;

    TextView textLabel;
    ImageView imgLogo;
    Animation textAnimation;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.hide();
        }
        textLabel = (TextView) findViewById(R.id.textViewlogo);
        imgLogo = (ImageView) findViewById(R.id.imageViewLogo);

        //imgAnimation = AnimationUtils.loadAnimation(this, R.anim.text_fade);
        textAnimation = AnimationUtils.loadAnimation(this, R.anim.text_fade);

        /*
        img1 = (ImageView)findViewById(R.id.img1);
       imgRotation = AnimationUtils.loadAnimation(this, R.anim.img_rotate);
        imgRotation.setRepeatCount(Animation.INFINITE);
        img1.startAnimation(imgRotation);*/
/* start Animation */

        imgLogo.startAnimation(textAnimation);
        textLabel.startAnimation(textAnimation);

        SharedPreferences loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        Boolean saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin) {
            SignInData signInData = new SignInData();
            signInData.setPhone(loginPreferences.getString("username", ""));
            signInData.setPasswd(loginPreferences.getString("passwd", ""));
            String type = loginPreferences.getString("logintype", "");
            if (type.equalsIgnoreCase(getString(R.string.patient))) {
                mLoginType = MappService.CUSTOMER_LOGIN;
            } else if (type.equalsIgnoreCase(getString(R.string.serv_prov))) {
                mLoginType = MappService.SERVICE_LOGIN;
            }

            Bundle bundle = new Bundle();
            bundle.putInt("loginType", mLoginType);
            bundle.putParcelable("signInData", signInData);
            mConnection.setAction(MappService.DO_LOGIN);
            mConnection.setData(bundle);
            Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
        } else {

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    WelcomeActivity.this.finish();
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }, 3800);
        }
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        try {
            unbindService(mConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (action == MappService.DO_LOGIN) {
            loginDone(data);
            return true;
        }
        return false;
    }

    protected void loginDone(Bundle msgData) {

        boolean success = msgData.getBoolean("status");
        if (success) {
            Intent intent;
            if (mLoginType == MappService.CUSTOMER_LOGIN) {
                Customer customer = msgData.getParcelable("customer");
                String targetActivity = getIntent().getStringExtra("target-activity");
                if (targetActivity != null) {
                    try {
                        intent = new Intent(this, Class.forName(targetActivity));
                        intent.putExtra("servProv", getIntent().getParcelableExtra("servProv"));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        return;
                    }
                } else {
                    intent = new Intent(this, PatientsHomeScreenActivity.class);
                }
                intent.putExtra("customer", customer);
                LoginHolder.custLoginRef = customer;
            } else {
                ServiceProvider serviceProvider = msgData.getParcelable("service");
                assert serviceProvider != null;
                String servPointType = serviceProvider.getServProvHasServPt(0).getServPointType();
                Log.v("LoginActivity", "service category: " + servPointType);
                LoginHolder.servLoginRef = serviceProvider;
                intent = new Intent(this, ServiceProviderHomeActivity.class);
                if (servPointType.equalsIgnoreCase(getString(R.string.medical_store))) {
                    intent = new Intent(this, MedicalStoreHomeActivity.class);
                }
            }
            startActivity(intent);
        } else {
            Utility.showMessage(this, R.string.some_error);
            /*mPasswordView.setError(getString(R.string.error_incorrect_password));
            mPasswordView.requestFocus();*/
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome_acitivity, menu);
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
        mConnection.setBound(false);
        this.finish();
    }
}
