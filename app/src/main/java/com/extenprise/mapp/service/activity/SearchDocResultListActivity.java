package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.service.data.ServProvHasService;
import com.extenprise.mapp.service.data.Service;
import com.extenprise.mapp.service.data.ServicePoint;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.SearchDoctor;

public class SearchDocResultListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_doc_result_list);

        //TO  VIEW THE LIST OF DOCTORS IN LISTVIEW FROM CURSOR THROUGH ADAPTER

        final Cursor cursor = SearchDoctor.getCursor();

        /* SimpleCursorAdapter(Context context,
                    int layout,
                    Cursor c,
                    String[] from,
                    int[] to,
                    int flags);*/

        String[] values = new String[] {
                MappContract.ServiceProvider.COLUMN_NAME_FNAME,
                MappContract.ServiceProvider.COLUMN_NAME_LNAME,
                MappContract.ServProvHasServ.COLUMN_NAME_SPECIALITY,
                MappContract.ServProvHasServ.COLUMN_NAME_EXPERIENCE,
                MappContract.ServicePoint.COLUMN_NAME_LOCATION
        };

        int[] viewIds = new int[] {
                R.id.viewDocname,
                R.id.viewDocSirName,
                R.id.viewDocSpeciality,
                R.id.viewExperience,
                R.id.viewLocation
        };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.activity_search_result,
                cursor,
                values,
                viewIds, 0);

        ListView listView = (ListView) findViewById(R.id.docListView);
        listView.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                cursor.moveToPosition(position);

                ServiceProvider sp = new ServiceProvider();
                sp.setfName(cursor.getString(cursor.getColumnIndex(MappContract.ServiceProvider.COLUMN_NAME_FNAME)));
                sp.setlName(cursor.getString(cursor.getColumnIndex(MappContract.ServiceProvider.COLUMN_NAME_LNAME)));

                Service s = new Service();
                s.setSpeciality(cursor.getString(cursor.getColumnIndex(MappContract.ServProvHasServ.COLUMN_NAME_SPECIALITY)));

                ServProvHasService sps = new ServProvHasService();
                sps.setServProv(sp);
                sps.setExperience(Float.parseFloat(cursor.getString(cursor.getColumnIndex(MappContract.ServProvHasServ.COLUMN_NAME_EXPERIENCE))));
                sps.setService(s);

                ServicePoint spt = new ServicePoint();
                spt.setName(cursor.getString(cursor.getColumnIndex(MappContract.ServicePoint.COLUMN_NAME_NAME)));
                spt.setLocation(cursor.getString(cursor.getColumnIndex(MappContract.ServicePoint.COLUMN_NAME_LOCATION)));

                LoginHolder.spsspt.setStartTime(cursor.getInt(cursor.getColumnIndex(MappContract.ServProvHasServHasServPt.COLUMN_NAME_START_TIME)));
                LoginHolder.spsspt.setEndTime(cursor.getInt(cursor.getColumnIndex(MappContract.ServProvHasServHasServPt.COLUMN_NAME_END_TIME)));
                LoginHolder.spsspt.setWeeklyOff(cursor.getString(cursor.getColumnIndex(MappContract.ServProvHasServHasServPt.COLUMN_NAME_WEEKLY_OFF)));
                LoginHolder.spsspt.setServPointType(cursor.getString(cursor.getColumnIndex(MappContract.ServProvHasServHasServPt.COLUMN_NAME_SERVICE_POINT_TYPE)));
                LoginHolder.spsspt.setServProvHasService(sps);
                LoginHolder.spsspt.setServicePoint(spt);

                Intent i = new Intent(getApplicationContext(), DoctorDetailsActivity.class);
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




}
