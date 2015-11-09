package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.service.data.RxInboxItem;
import com.extenprise.mapp.service.ui.RxInboxAdapter;

import java.util.ArrayList;


public class RxListActivity extends Activity {

    private ArrayList<RxInboxItem> mInbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_list);

        mInbox = getIntent().getParcelableArrayListExtra("inbox");
        boolean feedback = getIntent().getBooleanExtra("feedback", false);
        TextView msgView = (TextView) findViewById(R.id.noItemsMsgView);
        if(mInbox.size() > 0) {
            msgView.setVisibility(View.GONE);
        }

        RxInboxAdapter adapter = new RxInboxAdapter(this, 0, mInbox, feedback);
        ListView view = (ListView) findViewById(R.id.rxListView);
        view.setAdapter(adapter);
        view.setOnItemClickListener(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rx_list, menu);
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

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        return new Intent(this, ServiceProviderHomeActivity.class);
    }
}
