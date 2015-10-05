package com.extenprise.mapp.activity;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.extenprise.mapp.R;
import com.extenprise.mapp.customer.data.Customer;
import com.extenprise.mapp.data.ServProvListItem;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.SearchServProv;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MappService extends Service {
    private final Messenger mMessenger = new Messenger(new LoginHandler(this));
    private Messenger mReplyTo;
    private int mLoginType;
    private int mAction;

    public static final int DO_LOGIN = 1;
    public static final int DO_SIGNUP = 2;
    public static final int DO_PHONE_EXIST_CHECK = 3;
    public static final int DO_SEARCH_SERV_PROV = 4;
    public static final int DO_SERV_PROV_DETAILS = 5;

    public static final int CUSTOMER_LOGIN = 0x10;
    public static final int SERVICE_LOGIN = 0x11;

    public int getAction() {
        return mAction;
    }

    public void setAction(int action) {
        this.mAction = action;
    }

    @Override
    public IBinder onBind(Intent intent) {
        mAction = intent.getIntExtra("action", -1);
        return mMessenger.getBinder();
    }

    public void doLogin(Message msg) {
        Bundle data = msg.getData();
        mLoginType = data.getInt("loginType");
        Object object = data.getParcelable("service");
        if (mLoginType == CUSTOMER_LOGIN) {
            object = data.getParcelable("customer");
        }
        mReplyTo = msg.replyTo;
        MappAsyncTask task;
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        try {
            task = new MappAsyncTask(getURL(DO_LOGIN), gson.toJson(object));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            onError(DO_LOGIN);
            return;
        }
        task.execute((Void) null);
    }

    public void doSignup(Message msg) {
        Bundle data = msg.getData();
        mLoginType = data.getInt("loginType");

        Object object = data.getParcelable("service");
        if (mLoginType == CUSTOMER_LOGIN) {
            object = data.getParcelable("customer");
        }
        mReplyTo = msg.replyTo;
        MappAsyncTask task;
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        try {
            task = new MappAsyncTask(getURL(DO_SIGNUP), gson.toJson(object));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            onError(DO_SIGNUP);
            return;
        }
        task.execute((Void) null);
    }

    public void doPhoneExistsCheck(Message msg) {
        Bundle data = msg.getData();
        mLoginType = data.getInt("loginType");

        Object object = data.getParcelable("service");
        if (mLoginType == CUSTOMER_LOGIN) {
            object = data.getParcelable("customer");
        }
        mReplyTo = msg.replyTo;
        Gson gson = new Gson();
        MappAsyncTask task;
        try {
            task = new MappAsyncTask(getURL(DO_PHONE_EXIST_CHECK), gson.toJson(object));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            onError(DO_PHONE_EXIST_CHECK);
            return;
        }
        task.execute((Void) null);
    }

    public void doSearchServProv(Message msg) {
        Bundle data = msg.getData();
        SearchServProv form = data.getParcelable("form");
        mReplyTo = msg.replyTo;
        Gson gson = new Gson();
        MappAsyncTask task;
        try {
            task = new MappAsyncTask(getURL(DO_SEARCH_SERV_PROV), gson.toJson(form));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            onError(DO_SEARCH_SERV_PROV);
            return;
        }
        task.execute((Void) null);
    }

    public void doGetServProvDetails(Message msg) {
        Bundle data = msg.getData();
        ServProvListItem form = data.getParcelable("form");
        mReplyTo = msg.replyTo;
        Gson gson = new Gson();
        MappAsyncTask task;
        try {
            task = new MappAsyncTask(getURL(DO_SERV_PROV_DETAILS), gson.toJson(form));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            onError(DO_SEARCH_SERV_PROV);
            return;
        }
        task.execute((Void) null);
    }

    private void onError(int action) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("status", false);
        Message msg = Message.obtain(null, action);
        msg.setData(bundle);
        try {
            mReplyTo.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private URL getURL(int action) throws MalformedURLException {
        int urlId;
        switch (action) {
            case DO_LOGIN:
                urlId = R.string.signin_serv;
                if (mLoginType == MappService.CUSTOMER_LOGIN) {
                    urlId = R.string.signin_cust;
                }
                break;
            case DO_SIGNUP:
                urlId = R.string.signup_serv;
                if (mLoginType == MappService.CUSTOMER_LOGIN) {
                    urlId = R.string.signup_cust;
                }
                break;
            case DO_PHONE_EXIST_CHECK:
                urlId = R.string.check_phone_serv;
                if (mLoginType == MappService.CUSTOMER_LOGIN) {
                    urlId = R.string.check_phone_cust;
                }
                break;
            case DO_SEARCH_SERV_PROV:
                urlId = R.string.search_serv_prov;
                break;
            case DO_SERV_PROV_DETAILS:
                urlId = R.string.serv_prov_details;
                break;
            default:
                return null;
        }
        return new URL(getResources().getString(R.string.server_name) + "/" +
                getResources().getString(urlId));
    }

    private static class LoginHandler extends Handler {
        private MappService mService;

        public LoginHandler(MappService service) {
            mService = service;
        }

        @Override
        public void handleMessage(Message msg) {
            mService.setAction(msg.what);
            switch (msg.what) {
                case DO_LOGIN:
                    mService.doLogin(msg);
                    break;
                case DO_SIGNUP:
                    mService.doSignup(msg);
                    break;
                case DO_PHONE_EXIST_CHECK:
                    mService.doPhoneExistsCheck(msg);
                    break;
                case DO_SEARCH_SERV_PROV:
                    mService.doSearchServProv(msg);
                    break;
                case DO_SERV_PROV_DETAILS:
                    mService.doGetServProvDetails(msg);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class MappAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private URL mUrl;
        private String mData;
        private Customer mCustomer;
        private ServiceProvider mServProv;
        private ArrayList<ServProvListItem> mList;

        public MappAsyncTask(URL url, String data) {
            mUrl = url;
            mData = data;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // attempt authentication against a network service.
            boolean status = false;
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) mUrl.openConnection();
                connection.setChunkedStreamingMode(0);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/json");
                connection.setRequestProperty("Content-Length",
                        Integer.toString(mData.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");

                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                //Send request
                try {
                    DataOutputStream wr = new DataOutputStream(
                            connection.getOutputStream());
                    wr.writeBytes(mData);
                    wr.flush();
                    wr.close();
                } catch (IOException x) {
                    x.printStackTrace();
                }

                //Get Response
                int responseCode = connection.getResponseCode();
                if ((responseCode / 100) == 2) {
                    InputStream is = connection.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuilder responseBuf = new StringBuilder();
                    while ((line = rd.readLine()) != null) {
                        responseBuf.append(line);
                    }
                    rd.close();

                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
                    switch (mAction) {
                        case DO_LOGIN:
                        case DO_SIGNUP:
                        case DO_PHONE_EXIST_CHECK:
                            if (mLoginType == CUSTOMER_LOGIN) {
                                mCustomer = gson.fromJson(responseBuf.toString(), Customer.class);
                            } else {
                                mServProv = gson.fromJson(responseBuf.toString(), ServiceProvider.class);
                            }
                            break;
                        case DO_SEARCH_SERV_PROV:
                            mList = gson.fromJson(responseBuf.toString(), new TypeToken<ArrayList<ServProvListItem>>(){}.getType());
                            break;
                        case DO_SERV_PROV_DETAILS:
                            mServProv = gson.fromJson(responseBuf.toString(), ServiceProvider.class);
                            break;
                    }
                    status = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return status;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("status", success);
            if(mCustomer != null) {
                bundle.putParcelable("customer", mCustomer);
            }
            if(mServProv != null) {
                bundle.putParcelable("service", mServProv);
            }
            if(mList != null) {
                bundle.putParcelableArrayList("servProvList", mList);
            }
            Message msg = Message.obtain(null, mAction);
            msg.setData(bundle);
            try {
                mReplyTo.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
/*
            mAuthTask = null;
            UIUtility.showProgress(mActivity, mLoginFormView, mProgressView, false);
*/
        }
    }
}
