package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.data.ReportServiceStatus;
import com.extenprise.mapp.data.Rx;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.data.MedStoreRxForm;
import com.extenprise.mapp.service.data.ServProvListItem;
import com.extenprise.mapp.service.ui.MedStoreListAdapter;
import com.extenprise.mapp.util.Utility;

import java.util.ArrayList;
import java.util.Date;

public class SelectMedicalStoreActivity extends Activity implements ResponseHandler {

    private Messenger mService;
    private ServiceResponseHandler mRespHandler = new ServiceResponseHandler(this);

    private ListView mMedStoreList;
    private Rx mRx;
    private int mAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_medical_store);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mMedStoreList = (ListView) findViewById(R.id.medStoreListView);
        Intent intent = getIntent();
        mRx = intent.getParcelableExtra("rx");

        mAction = MappService.DO_GET_MEDSTORE_LIST;
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
    }

    public void sendRxToMedStore(View view) {
        mAction = MappService.DO_SEND_RX;
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
    }

    private void gotMedStoreList(Bundle data) {
        ArrayList<ServProvListItem> list = data.getParcelableArrayList("servProvList");
        MedStoreListAdapter adapter = new MedStoreListAdapter(this, 0, list);
        mMedStoreList.setAdapter(adapter);
        if (list != null && list.size() > 0) {
            Button sendButton = (Button) findViewById(R.id.sendRxButton);
            Utility.setEnabledButton(this, sendButton, true);
        }
    }

    private void rxSentToMedStore() {
        Intent intent = new Intent(this, ServiceProviderHomeActivity.class);
        intent.putExtra("service", LoginHolder.servLoginRef);
        startActivity(intent);
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        try {
            unbindService(mConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (action == MappService.DO_GET_MEDSTORE_LIST) {
            gotMedStoreList(data);
            return true;
        } else if (action == MappService.DO_SEND_RX) {
            rxSentToMedStore();
            return true;
        }
        return false;
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            mService = new Messenger(service);
            Bundle bundle = new Bundle();

            if (mAction == MappService.DO_SEND_RX) {
                int position = ((MedStoreListAdapter)mMedStoreList.getAdapter()).getSelectedPos();
                System.out.println("selected medstore pos: " + position);
                if(position == -1) {
                    unbindService(this);
                    return;
                }
                ServProvListItem item = (ServProvListItem) mMedStoreList.getAdapter().getItem(position);
                MedStoreRxForm form = new MedStoreRxForm();
                form.setIdServProvHasServPt(item.getIdServProvHasServPt());
                form.setIdRx(mRx.getId());
                form.setDate(new Date());
                form.setStatus(ReportServiceStatus.STATUS_NEW.ordinal());
                bundle.putParcelable("form", form);
            } else if (mAction == MappService.DO_GET_MEDSTORE_LIST) {
                bundle.putInt("id", mRx.getAppointment().getIdServProvHasServPt());
            }
            Message msg = Message.obtain(null, mAction);
            msg.replyTo = new Messenger(mRespHandler);
            msg.setData(bundle);

            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
        }
    };

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        Intent intent = super.getParentActivityIntent();
        if (intent != null) {
            intent.putExtra("appont", getIntent().getParcelableExtra("appont"));
            intent.putExtra("service", getIntent().getParcelableExtra("service"));
        }
        return intent;
    }
}
