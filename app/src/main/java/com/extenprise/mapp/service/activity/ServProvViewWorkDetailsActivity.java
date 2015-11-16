package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
import com.extenprise.mapp.service.data.ServicePoint;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.SearchServProv;
import com.extenprise.mapp.util.Utility;

import java.util.Calendar;


public class ServProvViewWorkDetailsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servprov_workdetail);

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
                MappContract.Service.COLUMN_NAME_SERVICE_NAME,
                MappContract.ServProvHasServPt.COLUMN_NAME_EXP,
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
                String docAvailDays = cursor.getString(cursor.getColumnIndex(MappContract.ServProvHasServPt.COLUMN_NAME_WORKING_DAYS));

                ImageView mImageViewAvailable = (ImageView) view.findViewById(R.id.imageViewAvailability);

                if(Utility.findDocAvailability(docAvailDays, Calendar.getInstance())) {
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

                ServicePoint spt = new ServicePoint();
                spt.setName(cursor.getString(cursor.getColumnIndex(MappContract.ServicePoint.COLUMN_NAME_NAME)));
                spt.setLocation(cursor.getString(cursor.getColumnIndex(MappContract.ServicePoint.COLUMN_NAME_LOCATION)));

                LoginHolder.spsspt.getService().setSpeciality(cursor.getString(cursor.getColumnIndex(MappContract.Service.COLUMN_NAME_SERVICE_NAME)));
                LoginHolder.spsspt.setExperience(Float.parseFloat(cursor.getString(cursor.getColumnIndex(MappContract.ServProvHasServPt.COLUMN_NAME_EXP))));
                LoginHolder.spsspt.setConsultFee(Float.parseFloat(cursor.getString(cursor.getColumnIndex(MappContract.ServProvHasServPt.COLUMN_NAME_CONSULTATION_FEE))));
                LoginHolder.spsspt.setStartTime(cursor.getInt(cursor.getColumnIndex(MappContract.ServProvHasServPt.COLUMN_NAME_START_TIME)));
                LoginHolder.spsspt.setEndTime(cursor.getInt(cursor.getColumnIndex(MappContract.ServProvHasServPt.COLUMN_NAME_END_TIME)));
                LoginHolder.spsspt.setWorkingDays(cursor.getString(cursor.getColumnIndex(MappContract.ServProvHasServPt.COLUMN_NAME_WORKING_DAYS)));
                LoginHolder.spsspt.setServPointType(cursor.getString(cursor.getColumnIndex(MappContract.ServProvHasServPt.COLUMN_NAME_SERVICE_POINT_TYPE)));
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
        getMenuInflater().inflate(R.menu.menu_view_profile_work_detail, menu);
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
}
