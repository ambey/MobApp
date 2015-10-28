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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.extenprise.mapp.R;
import com.extenprise.mapp.net.AppStatus;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.service.data.ServProvListItem;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.ui.ServProvListAdapter;
import com.extenprise.mapp.util.Utility;

import java.util.ArrayList;

public class SearchServProvResultActivity extends Activity implements ResponseHandler {

    private Messenger mService;
    private ServiceResponseHandler mRespHandler = new ServiceResponseHandler(this);

    private ArrayList<ServProvListItem> mServProvList;
    private ServProvListItem mSelectedItem;
    private View mProgressView;
    private View mSearchResultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_serv_prov_result);

        mSearchResultView = findViewById(R.id.formServProvResult);
        mProgressView = findViewById(R.id.progressBar);

        Intent intent = getIntent();
        mServProvList = intent.getParcelableArrayListExtra("servProvList");

        ArrayAdapter<ServProvListItem> adapter = new ServProvListAdapter(this,
                R.layout.activity_search_result, mServProvList);

        ListView listView = (ListView) findViewById(R.id.docListView);
        listView.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                getDetails(view, position);

            }
        });
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_doc_result_list, menu);
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

    private void getDetails(View view, int position) {
        if (!AppStatus.getInstance(this).isOnline()) {
            Utility.showMessage(this, R.string.error_not_online);
            return;
        }
        mSelectedItem = mServProvList.get(position);
        Utility.showProgress(view.getContext(), mSearchResultView, mProgressView, true);
        Intent intent = new Intent(view.getContext(), MappService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    public void gotDetails(Bundle data) {
        Utility.showProgress(this, mSearchResultView, mProgressView, false);
        Intent intent = new Intent(this, ServProvDetailsActivity.class);
        intent.putParcelableArrayListExtra("servProvList", mServProvList);
        intent.putExtra("service", data.getParcelable("service"));
        startActivity(intent);
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
            bundle.putParcelable("form", mSelectedItem);
            Message msg = Message.obtain(null, MappService.DO_SERV_PROV_DETAILS);
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

    @Override
    public boolean gotResponse(int action, Bundle data) {
        try {
            unbindService(mConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(action == MappService.DO_SERV_PROV_DETAILS) {
            gotDetails(data);
            return true;
        }
        return false;
    }
    
    @Override
    public Intent getParentActivityIntent() {
        Class parentClass = getIntent().getParcelableExtra("parent-activity");
        Intent intent;
        if(parentClass != null) {
            intent = new Intent(this, parentClass);
        } else {
            intent = super.getParentActivityIntent();
        }
        return intent;
    }
}


