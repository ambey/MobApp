package com.extenprise.mapp.medico.customer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.net.MappService;
import com.extenprise.mapp.medico.net.MappServiceConnection;
import com.extenprise.mapp.medico.net.ResponseHandler;
import com.extenprise.mapp.medico.net.ServiceResponseHandler;
import com.extenprise.mapp.medico.service.activity.ServProvDetailsActivity;
import com.extenprise.mapp.medico.service.data.ServProvListItem;
import com.extenprise.mapp.medico.service.ui.ServProvListAdapter;
import com.extenprise.mapp.medico.ui.DialogDismissListener;
import com.extenprise.mapp.medico.ui.SortActionDialog;
import com.extenprise.mapp.medico.util.Utility;

import java.util.ArrayList;

public class SearchServProvResultActivity extends FragmentActivity implements ResponseHandler, DialogDismissListener {

    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));

    private ArrayList<ServProvListItem> mServProvList;
    /*private View mProgressView;
    private View mSearchResultView;*/
    private ArrayAdapter<ServProvListItem> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_serv_prov_result);

        /*mSearchResultView = findViewById(R.id.formServProvResult);
        mProgressView = findViewById(R.id.progressBar);*/

        Intent intent = getIntent();
        mServProvList = intent.getParcelableArrayListExtra("servProvList");

        mAdapter = new ServProvListAdapter(this, R.layout.activity_search_result, mServProvList);

        ListView listView = (ListView) findViewById(R.id.docListView);
        listView.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getDetails(position);
            }
        });
        listView.setAdapter(mAdapter);

        ServProvListItem spl = mServProvList.get(0);
        String msg = "not present";
        if (spl.getWorkingDays() != null) {
            msg = spl.getWorkingDays();
        }

        //Toast.makeText(this, "working days : " + msg, Toast.LENGTH_LONG).show();
        Log.v("Home", "############################" + "working days : " + msg);
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
        switch (id) {
            case R.id.action_sort:
                showSortDialog();
                break;
            case R.id.action_settings:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSortDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        SortActionDialog dialog = new SortActionDialog();
        dialog.setSortFieldList(getResources().getStringArray(R.array.search_sort_field_list));
        dialog.setListener(this);
        dialog.show(fragmentManager, "SearchSort");
    }

    private void getDetails(int position) {
        ServProvListItem mSelectedItem = mAdapter.getItem(position);
        Bundle bundle = new Bundle();
        bundle.putParcelable("form", mSelectedItem);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_SERV_PROV_DETAILS);
        if (Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE)) {
            //Utility.showProgress(view.getContext(), mSearchResultView, mProgressView, true);
            Utility.showProgressDialog(this, true);
        }
    }

    public void gotDetails(Bundle data) {
        //Utility.showProgress(this, mSearchResultView, mProgressView, false);
        Utility.showProgressDialog(this, false);
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

    @Override
    public void onDialogDismissed(DialogFragment dialog) {
    }

    @Override
    public void onApplyDone(DialogFragment dialog) {
        SortActionDialog sortActionDialog = (SortActionDialog) dialog;
        ListView listView = (ListView) findViewById(R.id.docListView);
        ServProvListAdapter adapter = (ServProvListAdapter) listView.getAdapter();
        adapter.setAscending(sortActionDialog.isAscending());
        adapter.setSortField(sortActionDialog.getSortField());
    }

    @Override
    public void onCancelDone(DialogFragment dialog) {

    }

    @Override
    public void onBackPressed() {
        mConnection.setBound(false);
        //startActivity(getIntent());
        super.onBackPressed();
    }
}


