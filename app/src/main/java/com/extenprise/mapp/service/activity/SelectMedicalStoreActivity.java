package com.extenprise.mapp.service.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.data.Rx;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.data.MedStoreRxForm;
import com.extenprise.mapp.service.data.ServProvListItem;
import com.extenprise.mapp.service.ui.ServProvListAdapter;

import java.util.ArrayList;

public class SelectMedicalStoreActivity extends AppCompatActivity implements ResponseHandler {

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
        intent = new Intent(this, MappService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    public void sendRxToMedStore(View view) {
        mAction = MappService.DO_SEND_RX;
        Intent intent = new Intent(this, MappService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    private void gotMedStoreList(Bundle data) {
        ArrayList<ServProvListItem> list = data.getParcelableArrayList("medStores");
        ServProvListAdapter adapter = new ServProvListAdapter(this, 0, list);
        mMedStoreList.setAdapter(adapter);
    }

    private void rxSentToMedStore() {
        Intent intent = new Intent(this, ServiceProviderHomeActivity.class);
        intent.putExtra("service", LoginHolder.servLoginRef);
        startActivity(intent);
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
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

            ServProvListItem item = (ServProvListItem) mMedStoreList.getAdapter().getItem(mMedStoreList.getSelectedItemPosition());
            MedStoreRxForm form = new MedStoreRxForm();
            form.setIdServProvHasServPt(item.getIdServProvHasServPt());
            form.setIdRx(mRx.getId());
            if (mAction == MappService.DO_SEND_RX) {
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
        }
        return intent;
    }
}
