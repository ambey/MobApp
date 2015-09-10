package com.extenprise.mapp.activity;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.customer.data.Customer;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.service.data.ServiceProvider;

public class LoginService extends Service {
    private final Messenger mMessenger = new Messenger(new LoginHandler());
    private Messenger mReplyTo;
    private int mLoginType;
    private String mPhoneNo;
    private String mPasswd;

    public static final int DO_LOGIN = 1;
    public static final int CUSTOMER_LOGIN = 0x10;
    public static final int SERVICE_LOGIN = 0x11;

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    class LoginHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DO_LOGIN:
                    Bundle data = msg.getData();
                    mLoginType = data.getInt("loginType");
                    mPhoneNo = data.getString("phoneNo");
                    mPasswd = data.getString("passwd");
                    mReplyTo = msg.replyTo;
                    UserLoginTask authTask = new UserLoginTask();
                    authTask.execute((Void) null);
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private boolean isLoginValid() {
        MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int count = 0;


        if (mLoginType == LoginService.CUSTOMER_LOGIN) {
            String[] projection = {
                    MappContract.Customer._ID,
                    MappContract.Customer.COLUMN_NAME_FNAME,
                    MappContract.Customer.COLUMN_NAME_LNAME,
                    MappContract.Customer.COLUMN_NAME_EMAIL_ID,
                    MappContract.Customer.COLUMN_NAME_CELLPHONE
            };

            String selection = MappContract.Customer.COLUMN_NAME_CELLPHONE + "=? and " +
                    MappContract.Customer.COLUMN_NAME_PASSWD + "=?";

            String[] selectionArgs = {
                    mPhoneNo,
                    mPasswd
            };
            Cursor c = db.query(MappContract.Customer.TABLE_NAME,
                    projection, selection, selectionArgs, null, null, null);

            count = c.getCount();
            if (count > 0) {
                c.moveToFirst();
                Customer p = new Customer();
                p.setIdCustomer(Integer.parseInt(c.getString(0)));
                p.setfName(c.getString(1));
                p.setlName(c.getString(2));
                p.setEmailId(c.getString(3));
                p.setPhone(c.getString(4));

                if (LoginHolder.custLoginRef != null) {
                    if (LoginHolder.custLoginRef.getStatus() != null) {
                        p.setStatus(LoginHolder.custLoginRef.getStatus());
                    }
                }
                LoginHolder.custLoginRef = p;
            }
            c.close();

        } else if (mLoginType == SERVICE_LOGIN) {
            String[] projection = {
                    MappContract.ServiceProvider._ID,
                    MappContract.ServiceProvider.COLUMN_NAME_FNAME,
                    MappContract.ServiceProvider.COLUMN_NAME_LNAME,
                    MappContract.ServiceProvider.COLUMN_NAME_EMAIL_ID
            };

            String selection = MappContract.ServiceProvider.COLUMN_NAME_CELLPHONE + "=? and " +
                    MappContract.ServiceProvider.COLUMN_NAME_PASSWD + "=?";

            String[] selectionArgs = {
                    mPhoneNo,
                    mPasswd
            };
            Cursor c = db.query(MappContract.ServiceProvider.TABLE_NAME,
                    projection, selection, selectionArgs, null, null, null);

            count = c.getCount();
            if (count > 0) {
                c.moveToFirst();
                ServiceProvider sp = new ServiceProvider();
                sp.setIdServiceProvider(Integer.parseInt(c.getString(0)));
                sp.setfName(c.getString(1));
                sp.setlName(c.getString(2));
                sp.setEmailId(c.getString(3));

                LoginHolder.servLoginRef = sp;
            }
            c.close();
        }

        return (count > 0);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            /*if (mPassword == null) {
                return isEmailIdRegistered(mEmail);
            }*/
            return isLoginValid();

            // attempt authentication against a network service.
/*
            HttpURLConnection connection = null;
            try {
                String urlParams = "email=" + URLEncoder.encode(mEmail, "UTF-8") +
                        "&passwd=" + URLEncoder.encode(mPassword, "UTF-8");

                URL url = new URL(getResources().getString(R.string.login_url));
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setChunkedStreamingMode(0);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length",
                        Integer.toString(urlParams.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");

                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                //Send request
                DataOutputStream wr = new DataOutputStream(
                        connection.getOutputStream());
                wr.writeBytes(urlParams);
                wr.flush();
                wr.close();

                //Get Response
                int responseCode = connection.getResponseCode();
                if (responseCode != -1) {
                    InputStream is = connection.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    String line = null;
                    StringBuffer responseBuf = new StringBuffer();
                    while ((line = rd.readLine()) != null) {
                        responseBuf.append(line);
                    }
                    rd.close();

                    Gson gson = new Gson();
                    Response response = gson.fromJson(responseBuf.toString(), Response.class);
                    if (response.getStatus() == 0) {
                        if (response.getType().equals("Customer")) {

                        } else if (response.getType().equals("ServiceProvider")) {
                            ServiceProvider sp = gson.fromJson(responseBuf.toString(), ServiceProvider.class);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }
*/
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("status", success);
            Message msg = Message.obtain(null, LoginService.DO_LOGIN);
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
