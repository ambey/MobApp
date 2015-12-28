package com.extenprise.mapp.service.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.data.RxFeedback;
import com.extenprise.mapp.service.data.RxInboxItem;
import com.extenprise.mapp.service.ui.RxInboxAdapter;

import java.util.ArrayList;


public class RxListActivity extends Activity {

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
        int feedback = getIntent().getIntExtra("feedback", RxFeedback.NONE.ordinal());
        TextView msgView = (TextView) findViewById(R.id.noItemsMsgView);
        if(mInbox == null) {
            mInbox = new ArrayList<>();
        }
        if(mInbox.size() > 0) {
            msgView.setVisibility(View.GONE);
        }
        RxFeedback fb = RxFeedback.NONE;
        if(feedback == RxFeedback.GIVE_FEEDBACK.ordinal()) {
            fb = RxFeedback.GIVE_FEEDBACK;
        } else if(feedback == RxFeedback.VIEW_FEEDBACK.ordinal()) {
            fb = RxFeedback.VIEW_FEEDBACK;
        }
        RxInboxAdapter adapter = new RxInboxAdapter(this, 0, mInbox, fb);
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
        Intent intent = super.getParentActivityIntent();
        String parentClass = getIntent().getStringExtra("parent-activity");
        if(parentClass != null) {
            try {
                intent = new Intent(this, Class.forName(parentClass));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return intent;
    }
}
