package com.extenprise.mapp.medico.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.customer.activity.PatientsHomeScreenActivity;
import com.extenprise.mapp.medico.customer.data.Customer;
import com.extenprise.mapp.medico.data.WorkingDataStore;
import com.extenprise.mapp.medico.net.MappService;
import com.extenprise.mapp.medico.net.MappServiceConnection;
import com.extenprise.mapp.medico.net.ResponseHandler;
import com.extenprise.mapp.medico.net.ServiceResponseHandler;
import com.extenprise.mapp.medico.service.activity.MedicalStoreHomeActivity;
import com.extenprise.mapp.medico.service.activity.ServiceProviderHomeActivity;
import com.extenprise.mapp.medico.service.data.ServiceProvider;
import com.extenprise.mapp.medico.util.Utility;

import java.util.Calendar;

/*import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;*/


public class WelcomeActivity extends Activity implements ResponseHandler {
    TextView mTextLabel;

    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));
    private int mLoginType;

    /*
    private static final String LOG_TAG = "AppUpgrade";
    private int versionCode = 0;
    private DownloadManager downloadManager;
    private long downloadReference;

    //String appURI = "";
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
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.hide();
        }
        mTextLabel = (TextView) findViewById(R.id.textViewLogo);

        //Checking for updates in version of app.
/*
        MarketService ms = new MarketService(this);
        ms.level(MarketService.MINOR).checkVersion();
*/

//        initialize();
        /*
        img1 = (ImageView)findViewById(R.id.img1);
        imgRotation = AnimationUtils.loadAnimation(this, R.anim.img_rotate);
        imgRotation.setRepeatCount(Animation.INFINITE);
        img1.startAnimation(imgRotation);*/
        //imgAnimation = AnimationUtils.loadAnimation(this, R.anim.text_fade);
/* start Animation */
    }

    /*private boolean web_update(){
         try {
            String curVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            Log.v("Current Version : - ", curVersion);
            String newVersion = "";
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
                        Uri Download_Uri = Uri.parse(appURI);
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
        Animation textAnimation = AnimationUtils.loadAnimation(this, R.anim.text_fade);
        textAnimation.setDuration(4000);
        textAnimation.setFillEnabled(true);
        textAnimation.setFillAfter(true);
        mTextLabel.startAnimation(textAnimation);

        SharedPreferences loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        Boolean saveLogin = loginPreferences.getBoolean("saveLogin", false);
        //boolean logoutDone = loginPreferences.getBoolean("logout", false);
        if (saveLogin) {
            String type = loginPreferences.getString("logintype", "");
            if (type.equalsIgnoreCase(getString(R.string.patient))) {
                mLoginType = MappService.CUSTOMER_LOGIN;
            } else if (type.equalsIgnoreCase(getString(R.string.serv_prov))) {
                mLoginType = MappService.SERVICE_LOGIN;
            }
            String phone = loginPreferences.getString("username", "");
            String pwd = loginPreferences.getString("passwd", "");
            Calendar calendar = Calendar.getInstance();
            String lastVisit = String.format("%s %s", Utility.getDateAsStr(calendar.getTime(), "yyyy-MM-dd"),
                    Utility.getFormattedTime(calendar));

            Bundle bundle = new Bundle();
            bundle.putInt("loginType", mLoginType);
            if (mLoginType == MappService.CUSTOMER_LOGIN) {
                Customer customer = new Customer();
                customer.getSignInData().setPhone(phone);
                customer.getSignInData().setPasswd(pwd);
                customer.setLastVisit(lastVisit);
                bundle.putParcelable("customer", customer);
            } else if (mLoginType == MappService.SERVICE_LOGIN) {
                ServiceProvider serviceProvider = new ServiceProvider();
                serviceProvider.getSignInData().setPhone(phone);
                serviceProvider.getSignInData().setPasswd(pwd);
                serviceProvider.setLastVisit(lastVisit);
                bundle.putParcelable("serviceProvider", serviceProvider);
            }
            mConnection.setAction(MappService.DO_LOGIN);
            mConnection.setData(bundle);
            if (Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE)) {
                final View viewWaitMsg = findViewById(R.id.textViewWaitMsg);
                final Animation textBlink = AnimationUtils.loadAnimation(this, R.anim.text_fade);
                textBlink.setDuration(500);
                textBlink.setRepeatCount(Animation.INFINITE);
                textBlink.setRepeatMode(Animation.RESTART);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewWaitMsg.setVisibility(View.VISIBLE);
                        viewWaitMsg.startAnimation(textBlink);
                    }
                }, 5000);
            }
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    mTextLabel.setVisibility(View.GONE);
                    //WelcomeActivity.this.finish();
                }
            }, 3800);
        }
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        Utility.showProgressDialog(this, false);
        if (action == MappService.DO_LOGIN) {
            loginDone(data);
        }
        return data.getBoolean("status");
    }

    protected void loginDone(Bundle msgData) {
        Utility.showProgressDialog(this, false);
        boolean success = msgData.getBoolean("status");
        if (success) {
            Bundle workingData = WorkingDataStore.getBundle();
            Intent intent;
            if (mLoginType == MappService.CUSTOMER_LOGIN) {
                Customer customer = msgData.getParcelable("customer");
                if (customer != null) {
                    workingData.putParcelable("customer", customer);
                }
                intent = new Intent(this, PatientsHomeScreenActivity.class);
            } else {
                String servPointType = "";
                ServiceProvider serviceProvider = msgData.getParcelable("service");
                if (serviceProvider != null) {
                    workingData.putParcelable("servProv", serviceProvider);
                    servPointType = serviceProvider.getServProvHasServPt(0).getServPointType();
                }
                Log.v("LoginActivity", "service category: " + servPointType);
                intent = new Intent(this, ServiceProviderHomeActivity.class);
                if (servPointType.equalsIgnoreCase(getString(R.string.medical_store))) {
                    intent = new Intent(this, MedicalStoreHomeActivity.class);
                }
            }
            startActivity(intent);
        } else {
            Utility.startActivity(this, LoginActivity.class);
        }
    }
}
