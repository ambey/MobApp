package com.extenprise.mapp.customer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.extenprise.mapp.net.MappServiceConnection;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.activity.ServProvDetailsActivity;
import com.extenprise.mapp.service.data.ServProvListItem;
import com.extenprise.mapp.service.ui.ServProvListAdapter;
import com.extenprise.mapp.util.Utility;

import java.util.ArrayList;

public class SearchServProvResultActivity extends Activity implements ResponseHandler {

    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));

    private ArrayList<ServProvListItem> mServProvList;
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

        ServProvListItem spl = mServProvList.get(1);
        String msg = "not present";
        if(spl.getWorkingDays() != null) {
            msg = spl.getWorkingDays();
        }
        //Toast.makeText(this, "working days : " + msg, Toast.LENGTH_LONG).show();
        Log.v("Home", "############################" + "working days : " + msg);
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
        ServProvListItem mSelectedItem = mServProvList.get(position);
        Utility.showProgress(view.getContext(), mSearchResultView, mProgressView, true);
        Bundle bundle = new Bundle();
        bundle.putParcelable("form", mSelectedItem);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_SERV_PROV_DETAILS);
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
    }

    public void gotDetails(Bundle data) {
        Utility.showProgress(this, mSearchResultView, mProgressView, false);
        Intent intent = new Intent(this, ServProvDetailsActivity.class);
        intent.putParcelableArrayListExtra("servProvList", mServProvList);
        intent.putExtra("myparent-activity", getIntent().getStringExtra("parent-activity"));
        intent.putExtra("service", data.getParcelable("service"));
        startActivity(intent);
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        if (action == MappService.DO_SERV_PROV_DETAILS) {
            gotDetails(data);
            return true;
        }
        return false;
    }

    @Override
    public Intent getParentActivityIntent() {
        Intent intent = super.getParentActivityIntent();
        String parentClass = getIntent().getStringExtra("parent-activity");
        if (parentClass != null) {
            try {
                intent = new Intent(this, Class.forName(parentClass));
                intent.putExtra("form", getIntent().getParcelableExtra("form"));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return intent;
    }
}


