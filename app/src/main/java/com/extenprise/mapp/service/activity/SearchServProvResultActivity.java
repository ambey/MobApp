package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.service.data.ServProvHasService;
import com.extenprise.mapp.service.data.Service;
import com.extenprise.mapp.service.data.ServicePoint;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.SearchServProv;
import com.extenprise.mapp.util.UIUtility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class SearchServProvResultActivity extends Activity {

    ArrayList<HashMap<String, Object>> searchResults;
    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_serv_prov_result);

        //TO  VIEW THE LIST OF DOCTORS IN LISTVIEW FROM CURSOR THROUGH ADAPTER

        final Cursor cursor = SearchServProv.getCursor();

        /* SimpleCursorAdapter(Context context,
                    int layout,
                    Cursor c,
                    String[] from,
                    int[] to,
                    int flags);*/


        //ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.activity_search_result);


        String[] values = new String[] {
                MappContract.ServiceProvider.COLUMN_NAME_FNAME,
                MappContract.ServiceProvider.COLUMN_NAME_LNAME,
                MappContract.ServProvHasServ.COLUMN_NAME_SPECIALITY,
                MappContract.ServProvHasServ.COLUMN_NAME_EXPERIENCE,
                MappContract.ServicePoint.COLUMN_NAME_NAME,
                MappContract.ServicePoint.COLUMN_NAME_LOCATION
        };

        int[] viewIds = new int[] {
                R.id.viewFirstName,
                R.id.viewLastName,
                R.id.viewDocSpeciality,
                R.id.viewExpValue,
                R.id.viewClinicName,
                R.id.viewLocation
        };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.activity_search_result,
                cursor,
                values,
                viewIds, 0){
            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {

                View view =super.getView(position, convertView, parent);
                cursor.moveToPosition(position);
                String docAvailDays = cursor.getString(cursor.getColumnIndex(MappContract.ServProvHasServHasServPt.COLUMN_NAME_WEEKLY_OFF));

                ImageView mImageViewAvailable = (ImageView) view.findViewById(R.id.imageViewAvailability);

                if(UIUtility.findDocAvailability(docAvailDays, Calendar.getInstance())) {
                    mImageViewAvailable.setImageResource(R.drawable.gcircle);
                } else {
                    mImageViewAvailable.setImageResource(R.drawable.rcircle);
                }

            /*YOUR CHOICE OF COLOR*/
                //textView.setTextColor(Color.BLUE);

                return view;
            }
        };

        ListView listView = (ListView) findViewById(R.id.docListView);
        listView.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                cursor.moveToPosition(position);

                ServiceProvider sp = new ServiceProvider();
                sp.setIdServiceProvider(cursor.getInt(cursor.getColumnIndex(MappContract.ServiceProvider._ID)));
                sp.setfName(cursor.getString(cursor.getColumnIndex(MappContract.ServiceProvider.COLUMN_NAME_FNAME)));
                sp.setlName(cursor.getString(cursor.getColumnIndex(MappContract.ServiceProvider.COLUMN_NAME_LNAME)));
                sp.setQualification(cursor.getString(cursor.getColumnIndex(MappContract.ServiceProvider.COLUMN_NAME_QUALIFICATION)));

                Service s = new Service();
                s.setSpeciality(cursor.getString(cursor.getColumnIndex(MappContract.ServProvHasServ.COLUMN_NAME_SPECIALITY)));

                ServProvHasService sps = new ServProvHasService();
                sps.setServProv(sp);
                sps.setExperience(Float.parseFloat(cursor.getString(cursor.getColumnIndex(MappContract.ServProvHasServ.COLUMN_NAME_EXPERIENCE))));
                sps.setService(s);

                ServicePoint spt = new ServicePoint();
                spt.setName(cursor.getString(cursor.getColumnIndex(MappContract.ServicePoint.COLUMN_NAME_NAME)));
                spt.setLocation(cursor.getString(cursor.getColumnIndex(MappContract.ServicePoint.COLUMN_NAME_LOCATION)));

                LoginHolder.spsspt.setConsultFee(Float.parseFloat(cursor.getString(cursor.getColumnIndex(MappContract.ServProvHasServHasServPt.COLUMN_NAME_CONSULTATION_FEE))));
                LoginHolder.spsspt.setStartTime(cursor.getInt(cursor.getColumnIndex(MappContract.ServProvHasServHasServPt.COLUMN_NAME_START_TIME)));
                LoginHolder.spsspt.setEndTime(cursor.getInt(cursor.getColumnIndex(MappContract.ServProvHasServHasServPt.COLUMN_NAME_END_TIME)));
                LoginHolder.spsspt.setWeeklyOff(cursor.getString(cursor.getColumnIndex(MappContract.ServProvHasServHasServPt.COLUMN_NAME_WEEKLY_OFF)));
                LoginHolder.spsspt.setServPointType(cursor.getString(cursor.getColumnIndex(MappContract.ServProvHasServHasServPt.COLUMN_NAME_SERVICE_POINT_TYPE)));
                LoginHolder.spsspt.setServProvHasService(sps);
                LoginHolder.spsspt.setServicePoint(spt);

                Intent i = new Intent(getApplicationContext(), ServProvDetailsActivity.class);
                startActivity(i);
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


