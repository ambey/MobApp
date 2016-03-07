package com.extenprise.mapp.medico.net;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.customer.data.Customer;
import com.extenprise.mapp.medico.data.Appointment;
import com.extenprise.mapp.medico.data.Report;
import com.extenprise.mapp.medico.data.Rx;
import com.extenprise.mapp.medico.service.data.AppointmentListItem;
import com.extenprise.mapp.medico.service.data.RxInboxItem;
import com.extenprise.mapp.medico.service.data.ServProvListItem;
import com.extenprise.mapp.medico.service.data.ServiceProvider;
import com.extenprise.mapp.medico.service.data.WorkPlace;
import com.extenprise.mapp.medico.util.ByteArrayToJSONAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MappService extends Service {
    public static final int DO_LOGIN = 1;
    public static final int DO_SIGNUP = 2;
    public static final int DO_PHONE_EXIST_CHECK = 3;
    public static final int DO_SEARCH_SERV_PROV = 4;
    public static final int DO_SERV_PROV_DETAILS = 5;
    public static final int DO_UPDATE = 6;
    public static final int DO_ADD_WORK_PLACE = 7;
    public static final int DO_REG_NO_CHECK = 8;
    public static final int DO_REMOVE_WORK_PLACE = 9;
    public static final int DO_APPONT_TIME_SLOTS = 10;
    public static final int DO_BOOK_APPONT = 11;
    public static final int DO_APPONT_LIST = 12;
    public static final int DO_PAST_APPONT_LIST = 13;
    public static final int DO_GET_RX = 14;
    public static final int DO_SAVE_SCANNED_RX_COPY = 15;
    public static final int DO_CONFIRM_APPONT = 16;
    public static final int DO_CANCEL_APPONT = 17;
    public static final int DO_GET_SPECIALITY = 18;
    public static final int DO_SAVE_RX = 19;
    public static final int DO_SEND_RX = 20;
    public static final int DO_GET_MEDSTORE_LIST = 21;
    public static final int DO_WORK_PLACE_LIST = 22;
    public static final int DO_GET_RX_INBOX = 23;
    public static final int DO_SEND_AVAILABILITY = 24;
    public static final int DO_GET_RX_FEEDBACK = 25;
    public static final int DO_GET_RX_SCANNED_COPY = 26;
    public static final int DO_GET_CUST_UPCOMING_APPONTS = 27;
    public static final int DO_GET_CUST_PAST_APPONTS = 28;
    public static final int DO_GET_CUST_RX_LIST = 29;
    public static final int DO_UPCOMING_APPONT_LIST = 30;
    public static final int DO_EDIT_WORK_PLACE = 31;
    public static final int DO_UPDATE_REPORT_STATUS = 32;
    public static final int DO_CUST_PAST_APPONT_LIST = 33;
    public static final int DO_UPLOAD_PHOTO = 34;
    public static final int DO_REMOVE_PHOTO = 35;
    public static final int DO_GET_NEW_VERSION = 36;
    public static final int DO_PWD_CHECK = 37;
    public static final int DO_CHANGE_PWD = 38;
    public static final int DO_SIGNUP_REQ = 39;

    public static final int CUSTOMER_LOGIN = 0x10;
    public static final int SERVICE_LOGIN = 0x11;
    private final Messenger mMessenger = new Messenger(new LoginHandler(this));
    private Messenger mReplyTo;
    private int mLoginType;
    private int mAction;

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

    public void doGetNewVersion(Message msg) {
        Bundle data = msg.getData();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        //sendAsyncMsg(msg, gson.toJson(signInData));
    }

    public void doLogin(Message msg) {
        Bundle data = msg.getData();
        mLoginType = data.getInt("loginType");
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        if (mLoginType == CUSTOMER_LOGIN) {
            Customer customer = data.getParcelable("customer");
            sendAsyncMsg(msg, gson.toJson(customer));
        } else if (mLoginType == SERVICE_LOGIN) {
            ServiceProvider serviceProvider = data.getParcelable("serviceProvider");
            sendAsyncMsg(msg, gson.toJson(serviceProvider));
        }
        /*SignInData signInData = data.getParcelable("signInData");
        sendAsyncMsg(msg, gson.toJson(signInData));*/
    }

    public void doSignupOrUpdate(Message msg) {
        Bundle data = msg.getData();
        mLoginType = data.getInt("loginType");

        Object object = data.getParcelable("service");
        if (mLoginType == CUSTOMER_LOGIN) {
            object = data.getParcelable("customer");
        }

        GsonBuilder gsonBuilder = new GsonBuilder().registerTypeHierarchyAdapter(byte[].class, new ByteArrayToJSONAdapter());
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Gson gson = gsonBuilder.create();
        //Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        sendAsyncMsg(msg, gson.toJson(object));
    }

    public void doWorkPlaceStuff(Message msg) {
        Bundle data = msg.getData();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        sendAsyncMsg(msg, gson.toJson(data.getParcelable("workPlace")));
    }

    public void doPhoneExistsCheck(Message msg) {
        Bundle data = msg.getData();
        mLoginType = data.getInt("loginType");
        Gson gson = new Gson();
        sendAsyncMsg(msg, gson.toJson(data.getParcelable("signInData")));
    }

    public void doRegNoExistsCheck(Message msg) {
        Bundle data = msg.getData();
        mLoginType = data.getInt("loginType");
        Gson gson = new Gson();
        sendAsyncMsg(msg, gson.toJson(data.getParcelable("service")));
    }

    public void doSubmitForm(Message msg) {
        Bundle data = msg.getData();
        GsonBuilder gsonBuilder = new GsonBuilder().registerTypeHierarchyAdapter(byte[].class, new ByteArrayToJSONAdapter());
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Gson gson = gsonBuilder.create();
        sendAsyncMsg(msg, gson.toJson(data.getParcelable("form")));
    }

    private void doSaveRx(Message msg) {
        Bundle data = msg.getData();
        Rx rx = data.getParcelable("rx");
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        sendAsyncMsg(msg, gson.toJson(rx));
    }

    public void doGetMedStoreList(Message msg) {
        Bundle data = msg.getData();
        int idService = data.getInt("id");
        sendAsyncMsg(msg, "{\"idService\": " + idService + "}");
    }

    public void doGetRxInbox(Message msg) {
        Bundle data = msg.getData();
        String phone = data.getString("phone");
        int status = data.getInt("status");
        sendAsyncMsg(msg, "{\"phone\": " + phone + ", \"status\": " + status + "}");
    }

    public void doGetRxCopy(Message msg) {
        Bundle data = msg.getData();
        Report report = new Report();
        report.setIdReport(data.getInt("idRx"));
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        sendAsyncMsg(msg, gson.toJson(report));
    }

    public void doGetCustRxList(Message msg) {
        Bundle data = msg.getData();
        RxInboxItem inboxItem = data.getParcelable("rxItem");
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        sendAsyncMsg(msg, gson.toJson(inboxItem));
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

    private void sendAsyncMsg(Message message, String data) {
        mReplyTo = message.replyTo;
        MappAsyncTask task;
        try {
            task = new MappAsyncTask(getURL(message.what), data);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            onError(message.what);
            return;
        }
        task.execute((Void) null);
    }

    private URL getURL(int action) throws MalformedURLException {
        int urlId;
        switch (action) {
            case DO_PWD_CHECK:
                urlId = R.string.action_check_pwd_serv;
                if (mLoginType == MappService.CUSTOMER_LOGIN) {
                    urlId = R.string.action_check_pwd_cust;
                }
                break;
            case DO_CHANGE_PWD:
                urlId = R.string.action_change_pwd_serv;
                if (mLoginType == MappService.CUSTOMER_LOGIN) {
                    urlId = R.string.action_change_pwd_cust;
                }
                break;
            case DO_GET_NEW_VERSION:
                urlId = R.string.action_check_version_update;
                break;
            case DO_LOGIN:
                urlId = R.string.action_signin_serv;
                if (mLoginType == MappService.CUSTOMER_LOGIN) {
                    urlId = R.string.action_signin_cust;
                }
                break;
            case DO_SIGNUP:
                urlId = R.string.action_signup_serv;
                if (mLoginType == MappService.CUSTOMER_LOGIN) {
                    urlId = R.string.action_signup_cust;
                }
                break;
            case DO_UPDATE:
                urlId = R.string.action_update_serv;
                if (mLoginType == MappService.CUSTOMER_LOGIN) {
                    urlId = R.string.action_update_cust;
                }
                break;
            case DO_ADD_WORK_PLACE:
                urlId = R.string.action_addwork_place;
                break;
            case DO_EDIT_WORK_PLACE:
                urlId = R.string.action_editwork_place;
                break;
            case DO_WORK_PLACE_LIST:
                urlId = R.string.action_getwork_place;
                break;
            case DO_REMOVE_WORK_PLACE:
                urlId = R.string.action_remove_work_place;
                break;
            case DO_PHONE_EXIST_CHECK:
                urlId = R.string.action_check_phone_serv;
                if (mLoginType == MappService.CUSTOMER_LOGIN) {
                    urlId = R.string.action_check_phone_cust;
                }
                break;
            case DO_REG_NO_CHECK:
                urlId = R.string.action_check_reg_no_serv;
                break;
            case DO_SEARCH_SERV_PROV:
                urlId = R.string.action_search_serv_prov;
                break;
            case DO_SERV_PROV_DETAILS:
                urlId = R.string.action_serv_prov_details;
                break;
            case DO_APPONT_TIME_SLOTS:
                urlId = R.string.action_get_time_slots;
                break;
            case DO_BOOK_APPONT:
                urlId = R.string.action_book_appont;
                break;
            case DO_APPONT_LIST:
                urlId = R.string.action_appont_list;
                break;
            case DO_PAST_APPONT_LIST:
                urlId = R.string.action_past_appont_list;
                break;
            case DO_CUST_PAST_APPONT_LIST:
                urlId = R.string.action_cust_past_appont_list;
                break;
            case DO_UPCOMING_APPONT_LIST:
                urlId = R.string.action_upcoming_appont_list;
                break;
            case DO_CONFIRM_APPONT:
                urlId = R.string.action_confirm_appont;
                break;
            case DO_CANCEL_APPONT:
                urlId = R.string.action_cancel_appont;
                break;
            case DO_SAVE_SCANNED_RX_COPY:
                urlId = R.string.action_save_rx_copy;
                break;
            case DO_GET_SPECIALITY:
                urlId = R.string.action_get_speciality;
                break;
            case DO_GET_RX:
                urlId = R.string.action_get_rx;
                break;
            case DO_SAVE_RX:
                urlId = R.string.action_save_rx;
                break;
            case DO_GET_MEDSTORE_LIST:
                urlId = R.string.action_get_medstore_list;
                break;
            case DO_SEND_RX:
                urlId = R.string.action_send_rx;
                break;
            case DO_GET_RX_INBOX:
                urlId = R.string.action_get_rx_inbox;
                break;
            case DO_SEND_AVAILABILITY:
                urlId = R.string.action_send_availability_feedback;
                break;
            case DO_GET_RX_FEEDBACK:
                urlId = R.string.action_get_rx_feedback;
                break;
            case DO_GET_RX_SCANNED_COPY:
                urlId = R.string.action_get_rx_copy;
                break;
            case DO_GET_CUST_UPCOMING_APPONTS:
                urlId = R.string.action_get_cust_upcoming_apponts;
                break;
            case DO_GET_CUST_PAST_APPONTS:
                urlId = R.string.action_get_cust_past_apponts;
                break;
            case DO_GET_CUST_RX_LIST:
                urlId = R.string.action_get_cust_rx_list;
                break;
            case DO_UPLOAD_PHOTO:
                urlId = R.string.action_upload_photo_serv;
                if (mLoginType == MappService.CUSTOMER_LOGIN) {
                    urlId = R.string.action_upload_photo_cust;
                }
                break;
            case DO_REMOVE_PHOTO:
                urlId = R.string.action_remove_photo_serv;
                if (mLoginType == MappService.CUSTOMER_LOGIN) {
                    urlId = R.string.action_remove_photo_cust;
                }
                break;
            case DO_SIGNUP_REQ:
                urlId = R.string.action_signup_req;
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
                case DO_GET_NEW_VERSION:
                    mService.doGetNewVersion(msg);
                    break;
                case DO_CHANGE_PWD:
                    mService.doSignupOrUpdate(msg);
                    break;
                case DO_PWD_CHECK:
                    mService.doSignupOrUpdate(msg);
                    break;
                case DO_LOGIN:
                    mService.doLogin(msg);
                    break;
                case DO_SIGNUP:
                case DO_UPDATE:
                    mService.doSignupOrUpdate(msg);
                    break;
                case DO_ADD_WORK_PLACE:
                    mService.doWorkPlaceStuff(msg);
                    break;
                case DO_EDIT_WORK_PLACE:
                    mService.doWorkPlaceStuff(msg);
                    break;
                case DO_WORK_PLACE_LIST:
                case DO_REMOVE_WORK_PLACE:
                    mService.doWorkPlaceStuff(msg);
                    break;
                case DO_PHONE_EXIST_CHECK:
                    mService.doPhoneExistsCheck(msg);
                    break;
                case DO_REG_NO_CHECK:
                    mService.doRegNoExistsCheck(msg);
                    break;
                case DO_SEARCH_SERV_PROV:
                case DO_SERV_PROV_DETAILS:
                case DO_APPONT_TIME_SLOTS:
                case DO_BOOK_APPONT:
                case DO_APPONT_LIST:
                case DO_PAST_APPONT_LIST:
                case DO_UPCOMING_APPONT_LIST:
                case DO_CONFIRM_APPONT:
                case DO_CANCEL_APPONT:
                case DO_SAVE_SCANNED_RX_COPY:
                case DO_GET_SPECIALITY:
                case DO_GET_RX:
                case DO_SEND_RX:
                case DO_SEND_AVAILABILITY:
                case DO_GET_CUST_UPCOMING_APPONTS:
                case DO_GET_CUST_PAST_APPONTS:
                case DO_CUST_PAST_APPONT_LIST:
                case DO_SIGNUP_REQ:
                    mService.doSubmitForm(msg);
                    break;
                case DO_SAVE_RX:
                    mService.doSaveRx(msg);
                    break;
                case DO_GET_MEDSTORE_LIST:
                    mService.doGetMedStoreList(msg);
                    break;
                case DO_GET_RX_INBOX:
                case DO_GET_RX_FEEDBACK:
                    mService.doGetRxInbox(msg);
                    break;
                case DO_GET_RX_SCANNED_COPY:
                    mService.doGetRxCopy(msg);
                    break;
                case DO_GET_CUST_RX_LIST:
                    mService.doGetCustRxList(msg);
                    break;
                case DO_UPLOAD_PHOTO:
                    mService.doSignupOrUpdate(msg);
                    break;
                case DO_REMOVE_PHOTO:
                    mService.doSignupOrUpdate(msg);
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
        private WorkPlace mWorkPlace;
        private ArrayList<WorkPlace> mWorkPlaceList;
        private ArrayList<ServProvListItem> mServProvList;
        private ArrayList<String> mStringList;
        private ArrayList<AppointmentListItem> mAppontList;
        private ArrayList<com.extenprise.mapp.medico.customer.data.AppointmentListItem> mCustAppontList;
        private ArrayList<RxInboxItem> mInbox;
        private Appointment mForm;
        private Rx mRx;
        private Report mReport;
        private int mResponsecode;

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
                    mResponsecode = ErrorCode.ERROR_SERVER_UNAVAILABLE;
                    return false;
                }

                //Get Response
                mResponsecode = connection.getResponseCode();
                if ((mResponsecode / 100) == 2) {
                    InputStream is = connection.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuilder responseBuf = new StringBuilder();
                    while ((line = rd.readLine()) != null) {
                        responseBuf.append(line);
                    }
                    rd.close();

                    GsonBuilder gsonBuilder = new GsonBuilder().registerTypeHierarchyAdapter(byte[].class, new ByteArrayToJSONAdapter());
                    gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Gson gson = gsonBuilder.create();
                    switch (mAction) {
                        case DO_LOGIN:
                        case DO_CHANGE_PWD:
                        case DO_PWD_CHECK:
                        case DO_SIGNUP:
                        case DO_UPLOAD_PHOTO:
                        case DO_REMOVE_PHOTO:
                        case DO_UPDATE:
                        case DO_PHONE_EXIST_CHECK:
                            if (mLoginType == CUSTOMER_LOGIN) {
                                mCustomer = gson.fromJson(responseBuf.toString(), Customer.class);
                            } else {
                                mServProv = gson.fromJson(responseBuf.toString(), ServiceProvider.class);
                            }
                            break;
                        case DO_ADD_WORK_PLACE:
                        case DO_EDIT_WORK_PLACE:
                        case DO_REMOVE_WORK_PLACE:
                            mWorkPlace = gson.fromJson(responseBuf.toString(), WorkPlace.class);
                            break;
                        case DO_REG_NO_CHECK:
                        case DO_SERV_PROV_DETAILS:
                            mServProv = gson.fromJson(responseBuf.toString(), ServiceProvider.class);
                            break;
                        case DO_SEARCH_SERV_PROV:
                        case DO_GET_MEDSTORE_LIST:
                            mServProvList = gson.fromJson(responseBuf.toString(), new TypeToken<ArrayList<ServProvListItem>>() {
                            }.getType());
                            break;
                        case DO_APPONT_TIME_SLOTS:
                        case DO_GET_SPECIALITY:
                            mStringList = gson.fromJson(responseBuf.toString(), new TypeToken<ArrayList<String>>() {
                            }.getType());
                            break;
                        case DO_BOOK_APPONT:
                            mForm = gson.fromJson(responseBuf.toString(), Appointment.class);
                            break;
                        case DO_APPONT_LIST:
                        case DO_PAST_APPONT_LIST:
                        case DO_UPCOMING_APPONT_LIST:
                        case DO_CUST_PAST_APPONT_LIST:
                            mAppontList = gson.fromJson(responseBuf.toString(), new TypeToken<ArrayList<AppointmentListItem>>() {
                            }.getType());
                            break;
                        case DO_WORK_PLACE_LIST:
                            mWorkPlaceList = gson.fromJson(responseBuf.toString(), new TypeToken<ArrayList<WorkPlace>>() {
                            }.getType());
                            break;
                        case DO_GET_RX:
                        case DO_SAVE_RX:
                            mRx = gson.fromJson(responseBuf.toString(), Rx.class);
                            break;
                        case DO_GET_RX_INBOX:
                        case DO_GET_RX_FEEDBACK:
                        case DO_GET_CUST_RX_LIST:
                            mInbox = gson.fromJson(responseBuf.toString(), new TypeToken<ArrayList<RxInboxItem>>() {
                            }.getType());
                            break;
                        case DO_SAVE_SCANNED_RX_COPY:
                            mRx = gson.fromJson(responseBuf.toString(), Rx.class);
                            break;
                        case DO_GET_RX_SCANNED_COPY:
                            mReport = gson.fromJson(responseBuf.toString(), Report.class);
                            break;
                        case DO_GET_CUST_UPCOMING_APPONTS:
                        case DO_GET_CUST_PAST_APPONTS:
                            mCustAppontList = gson.fromJson(responseBuf.toString(),
                                    new TypeToken<ArrayList<com.extenprise.mapp.medico.customer.data.AppointmentListItem>>() {
                                    }.getType());
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
            bundle.putInt("responseCode", mResponsecode);
            if (mCustomer != null) {
                bundle.putParcelable("customer", mCustomer);
            }
            if (mServProv != null) {
                bundle.putParcelable("service", mServProv);
            }
            if (mServProvList != null) {
                bundle.putParcelableArrayList("servProvList", mServProvList);
            }
            if (mStringList != null) {
                String key = "timeSlots";
                if (mAction == DO_GET_SPECIALITY) {
                    key = "specialities";
                }
                bundle.putStringArrayList(key, mStringList);
            }
            if (mForm != null) {
                bundle.putParcelable("appontForm", mForm);
            }
            if (mWorkPlace != null) {
                bundle.putParcelable("workPlace", mWorkPlace);
            }
            if (mAppontList != null) {
                bundle.putParcelableArrayList("appontList", mAppontList);
            }
            if (mCustAppontList != null) {
                bundle.putParcelableArrayList("appontList", mCustAppontList);
            }
            if (mWorkPlaceList != null) {
                bundle.putParcelableArrayList("workPlaceList", mWorkPlaceList);
            }
            if (mRx != null) {
                bundle.putParcelable("rx", mRx);
                Log.v("MappService", "Rx id: " + mRx.getIdReport());
            }
            if (mReport != null) {
                bundle.putParcelable("report", mReport);
            }
            if (mInbox != null) {
                bundle.putParcelableArrayList("inbox", mInbox);
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
