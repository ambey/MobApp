package com.extenprise.mapp.service.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.data.ReportServiceStatus;
import com.extenprise.mapp.data.Rx;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.MappServiceConnection;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.data.MedStoreRxForm;
import com.extenprise.mapp.service.data.ServProvListItem;
import com.extenprise.mapp.service.ui.MedStoreListAdapter;
import com.extenprise.mapp.util.Utility;

import java.util.ArrayList;
import java.util.Date;

public class SelectMedicalStoreActivity extends Activity implements ResponseHandler {

    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));
    private ListView mMedStoreList;
    private Rx mRx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_medical_store);
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mMedStoreList = (ListView) findViewById(R.id.medStoreListView);
        Intent intent = getIntent();
        mRx = intent.getParcelableExtra("rx");

        mConnection.setAction(MappService.DO_GET_MEDSTORE_LIST);
        Bundle bundle = new Bundle();
        bundle.putInt("id", mRx.getAppointment().getIdServProvHasServPt());
        mConnection.setData(bundle);
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
    }

    public void sendRxToMedStore(View view) {
        int position = ((MedStoreListAdapter)mMedStoreList.getAdapter()).getSelectedPos();
        System.out.println("selected medstore pos: " + position);
        if(position == -1) {
            unbindService(mConnection);
            return;
        }
        Bundle bundle = new Bundle();
        ServProvListItem item = (ServProvListItem) mMedStoreList.getAdapter().getItem(position);
        MedStoreRxForm form = new MedStoreRxForm();
        form.setIdServProvHasServPt(item.getIdServProvHasServPt());
        form.setIdRx(mRx.getIdReport());
        form.setDate(new Date());
        form.setStatus(ReportServiceStatus.STATUS_NEW.ordinal());
        bundle.putParcelable("form", form);
        mConnection.setAction(MappService.DO_SEND_RX);
        mConnection.setData(bundle);
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
        Utility.showAlert(this, "", getString(R.string.msg_rx_sent_to_medstore), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(SelectMedicalStoreActivity.this, ServiceProviderHomeActivity.class);
                intent.putExtra("service", LoginHolder.servLoginRef);
                startActivity(intent);
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_doc_result_list, menu);
        return super.onCreateOptionsMenu(menu);
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
        //startActivity(getIntent());
        super.onBackPressed();
    }

}
