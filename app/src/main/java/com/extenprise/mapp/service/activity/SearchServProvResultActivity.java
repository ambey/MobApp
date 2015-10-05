package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.activity.MappService;
import com.extenprise.mapp.data.ServProvListItem;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.service.ui.SearchResultListAdapter;
import com.extenprise.mapp.util.UIUtility;

import java.util.ArrayList;

public class SearchServProvResultActivity extends Activity {

    private Messenger mService;
    private SearchResponseHandler mRespHandler = new SearchResponseHandler(this);

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

        ArrayAdapter<ServProvListItem> adapter = new SearchResultListAdapter(this,
                R.layout.activity_search_result, mServProvList);

        ListView listView = (ListView) findViewById(R.id.docListView);
        listView.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mSelectedItem = mServProvList.get(position);
                UIUtility.showProgress(view.getContext(), mSearchResultView, mProgressView, true);
                Intent intent = new Intent(view.getContext(), MappService.class);
                bindService(intent, mConnection, 0);

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

    public void gotDetails(Bundle data) {
        UIUtility.showProgress(this, mSearchResultView, mProgressView, false);
        unbindService(mConnection);
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
            Message msg = Message.obtain(null, MappService.DO_SEARCH_SERV_PROV);
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

    private static class SearchResponseHandler extends Handler {
        private SearchServProvResultActivity mActivity;

        public SearchResponseHandler(SearchServProvResultActivity activity) {
            mActivity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MappService.DO_SEARCH_SERV_PROV:
                    mActivity.gotDetails(msg.getData());
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }


    /*private class CustomAdapter extends ArrayAdapter<HashMap<String, Object>>
    {

        public CustomAdapter(Context context, int textViewResourceId,
                             ArrayList<HashMap<String, Object>> Strings) {

            //let android do the initializing :)
            super(context, textViewResourceId, Strings);
        }


        //class for caching the views in a row
        private class ViewHolder
        {
            ImageView photo;
            TextView name,team;

        }

        ViewHolder viewHolder;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView==null)
            {
                convertView=inflater.inflate(R.layout.activity_search_result, null);
                viewHolder=new ViewHolder();

                //cache the views
                *//*viewHolder.photo=(ImageView) convertView.findViewById(R.drawable.g_circle);
                viewHolder.name=(TextView) convertView.findViewById(R.id.name);
                viewHolder.team=(TextView) convertView.findViewById(R.id.team);*//*

                //link the cached views to the convertview
                convertView.setTag(viewHolder);

            }
            else
                viewHolder=(ViewHolder) convertView.getTag();


            int photoId=(Integer) searchResults.get(position).get("photo");

            //set the data to be displayed
            viewHolder.photo.setImageDrawable(getResources().getDrawable(photoId));
            viewHolder.name.setText(searchResults.get(position).get("name").toString());
            viewHolder.team.setText(searchResults.get(position).get("team").toString());

            //return the view to be displayed
            return convertView;
        }

    }*/
}


