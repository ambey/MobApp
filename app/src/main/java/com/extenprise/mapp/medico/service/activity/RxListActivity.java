package com.extenprise.mapp.medico.service.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.data.RxFeedback;
import com.extenprise.mapp.medico.data.WorkingDataStore;
import com.extenprise.mapp.medico.service.data.RxInboxItem;
import com.extenprise.mapp.medico.service.ui.RxInboxAdapter;
import com.extenprise.mapp.medico.ui.DialogDismissListener;
import com.extenprise.mapp.medico.ui.SortActionDialog;

import java.util.ArrayList;


public class RxListActivity extends FragmentActivity implements DialogDismissListener {

    private int mFeedback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_list);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(R.string.title_activity_rx_list);
        }

        ArrayList<RxInboxItem> mInbox = getIntent().getParcelableArrayListExtra("inbox");
        mFeedback = getIntent().getIntExtra("feedback", RxFeedback.NONE.ordinal());
        TextView msgView = (TextView) findViewById(R.id.noItemsMsgView);
        if (mInbox == null) {
            mInbox = new ArrayList<>();
        }
        if (mInbox.size() > 0) {
            msgView.setVisibility(View.GONE);
        }
        RxFeedback fb = RxFeedback.NONE;
        if (mFeedback == RxFeedback.GIVE_FEEDBACK.ordinal()) {
            fb = RxFeedback.GIVE_FEEDBACK;
        } else if (mFeedback == RxFeedback.VIEW_FEEDBACK.ordinal()) {
            fb = RxFeedback.VIEW_FEEDBACK;
        }
        RxInboxAdapter adapter = new RxInboxAdapter(this, 0, mInbox, fb);
        Bundle bundle = WorkingDataStore.getBundle();
        adapter.setAscending(bundle.getBoolean("ascending"));
        adapter.setSortField(bundle.getString("sortField"));

        ListView view = (ListView) findViewById(R.id.rxListView);
        view.setAdapter(adapter);
        view.setOnItemClickListener(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rx_list, menu);
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
        int list = R.array.medstore_rx_inbox_sort_field_list;
        if (mFeedback == RxFeedback.VIEW_FEEDBACK.ordinal()) {
            list = R.array.dr_rx_sort_field_list;
        }
        dialog.setSortFieldList(getResources().getStringArray(list));
        dialog.setListener(this);
        dialog.show(fragmentManager, "RxListSort");
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        Intent intent = super.getParentActivityIntent();
        String parentClass = getIntent().getStringExtra("parent-activity");
        if (parentClass != null) {
            try {
                intent = new Intent(this, Class.forName(parentClass));
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
        ListView listView = (ListView) findViewById(R.id.rxListView);
        RxInboxAdapter adapter = (RxInboxAdapter) listView.getAdapter();
        adapter.setAscending(sortActionDialog.isAscending());
        adapter.setSortField(sortActionDialog.getSortField());
    }

    @Override
    public void onCancelDone(DialogFragment dialog) {

    }
}
