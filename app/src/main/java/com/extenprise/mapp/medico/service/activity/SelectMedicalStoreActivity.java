package com.extenprise.mapp.medico.service.activity;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.activity.LoginActivity;
import com.extenprise.mapp.medico.data.ReportServiceStatus;
import com.extenprise.mapp.medico.data.Rx;
import com.extenprise.mapp.medico.net.MappService;
import com.extenprise.mapp.medico.net.MappServiceConnection;
import com.extenprise.mapp.medico.net.ResponseHandler;
import com.extenprise.mapp.medico.net.ServiceResponseHandler;
import com.extenprise.mapp.medico.service.data.MedStoreRxForm;
import com.extenprise.mapp.medico.service.data.ServProvListItem;
import com.extenprise.mapp.medico.service.ui.MedStoreListAdapter;
import com.extenprise.mapp.medico.ui.DialogDismissListener;
import com.extenprise.mapp.medico.ui.SortActionDialog;
import com.extenprise.mapp.medico.util.Utility;

import java.util.ArrayList;
import java.util.Date;

public class SelectMedicalStoreActivity extends FragmentActivity implements ResponseHandler, DialogDismissListener {

    //ProgressDialog progressDialog;
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("rx", getIntent().getParcelableExtra("rx"));
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
        form.setStatus(ReportServiceStatus.STATUS_NEW);
        bundle.putParcelable("form", form);
        mConnection.setAction(MappService.DO_SEND_RX);
        mConnection.setData(bundle);
        if(Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE)) {
            //progressDialog = ProgressDialog.show(this, "", getString(R.string.msg_please_wait), true);
            Utility.showProgressDialog(this, true);
        }
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
        /*if(progressDialog != null) {
            progressDialog.dismiss();
        }*/
        Utility.showProgressDialog(this, false);
        /*Utility.showAlert(this, "", getString(R.string.msg_rx_sent_to_medstore), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(SelectMedicalStoreActivity.this, ServiceProviderHomeActivity.class);
                startActivity(intent);
            }
        });*/
        Utility.showAlert(this, "", getString(R.string.msg_rx_sent_to_medstore), null, false, null, getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Utility.startActivity(SelectMedicalStoreActivity.this, ServiceProviderHomeActivity.class);
            }
        }, new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                Utility.startActivity(SelectMedicalStoreActivity.this, ServiceProviderHomeActivity.class);
            }
        });
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        if (action == MappService.DO_GET_MEDSTORE_LIST) {
            gotMedStoreList(data);
        } else if (action == MappService.DO_SEND_RX) {
            rxSentToMedStore();
        }
        return data.getBoolean("status");
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        Intent intent = super.getParentActivityIntent();
        if (intent != null) {
            intent.putExtra("appont", getIntent().getParcelableExtra("appont"));
            //intent.putExtra("servProv", getIntent().getParcelableExtra("servProv"));
        }
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_screen, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_sort:
                showSortDialog();
                break;
            case R.id.action_settings:
                break;
            case R.id.logout:
                Utility.logout(getSharedPreferences("loginPrefs", MODE_PRIVATE), this, LoginActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSortDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        SortActionDialog dialog = new SortActionDialog();
        dialog.setSortFieldList(getResources().getStringArray(R.array.medstore_sort_field_list));
        dialog.setListener(this);
        dialog.show(fragmentManager, "MedStoreListSort");
    }

    @Override
    public void onBackPressed() {
        mConnection.setBound(false);
        //startActivity(getIntent());
        super.onBackPressed();
    }

    @Override
    public void onDialogDismissed(DialogFragment dialog) {

    }

    @Override
    public void onApplyDone(DialogFragment dialog) {
        SortActionDialog sortActionDialog = (SortActionDialog) dialog;
        MedStoreListAdapter adapter = (MedStoreListAdapter) mMedStoreList.getAdapter();
        adapter.setAscending(sortActionDialog.isAscending());
        adapter.setSortField(sortActionDialog.getSortField());
    }

    @Override
    public void onCancelDone(DialogFragment dialog) {

    }
}
