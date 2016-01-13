package com.extenprise.mapp.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import org.jsoup.Jsoup;

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
import com.google.android.gms.tagmanager.Container;
import com.google.android.gms.tagmanager.TagManager;


public class WelcomeActivity extends Activity implements ResponseHandler {

    TextView textLabel;
    ImageView imgLogo;
    Animation textAnimation;
    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));
    private int mLoginType;
    private Handler mHandler = new Handler();

    private static final String LOG_TAG = "AppUpgrade";
    private int versionCode = 0;
    String appURI = "";
    private DownloadManager downloadManager;
    private long downloadReference;

    //broadcast receiver to get notification about ongoing downloads
    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //check if the broadcast message is for our Enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if(downloadReference == referenceId){

                Log.v(LOG_TAG, "Downloading of the new app version complete");
                //start the installation of the latest version
                Intent installIntent = new Intent(Intent.ACTION_VIEW);
                installIntent.setDataAndType(downloadManager.getUriForDownloadedFile(downloadReference),
                        "application/vnd.android.package-archive");
                installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(installIntent);

            }
        }
    };

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

        initialize();

        /*
        img1 = (ImageView)findViewById(R.id.img1);
       imgRotation = AnimationUtils.loadAnimation(this, R.anim.img_rotate);
        imgRotation.setRepeatCount(Animation.INFINITE);
        img1.startAnimation(imgRotation);*/
/* start Animation */

        //TODO
        try {
            String curVersion = this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            String newVersion = curVersion;
            //NetworkOnMainThreadException
            newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + getPackageName() + "&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select("div[itemprop=softwareVersion]")
                    .first()
                    .ownText();
            Utility.showMessage(this, R.string.msg_new_version_available);
            if(Utility.strToLong(curVersion) < Utility.strToLong(newVersion)) {
                //Utility.showMessage(this, R.string.msg_new_version_available);
                Utility.showAlert(this, "", getString(R.string.msg_new_version_available), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                        Uri Download_Uri = Uri.parse(appURI);//TODO should have to get from server.
                        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                        request.setAllowedOverRoaming(false);
                        request.setTitle("My Andorid App Download");
                        request.setDestinationInExternalFilesDir(WelcomeActivity.this, Environment.DIRECTORY_DOWNLOADS,"mapp.apk");
                        downloadReference = downloadManager.enqueue(request);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private boolean web_update(){
        try {
            String curVersion = this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            String newVersion = curVersion;
            newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + getPackageName() + "&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select("div[itemprop=softwareVersion]")
                    .first()
                    .ownText();
            return (Utility.strToLong(curVersion) < Utility.strToLong(newVersion));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }



        *//*Container container = TagManager.getInstance(this).openContainer(myContainerId);
        long latestVersionCode = container.getLong("latestAppVersion");

// get currently running app version code
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        long versionCode = pInfo.versionCode;

// check if update is needed
        if(versionCode < latestVersionCode) {
            // remind user to update his version
        }*//*
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("Welcome", "onResume called...");
        initialize();
    }

    private void initialize() {
        textAnimation = AnimationUtils.loadAnimation(this, R.anim.text_fade);
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
}
