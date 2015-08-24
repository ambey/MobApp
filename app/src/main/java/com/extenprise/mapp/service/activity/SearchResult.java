package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.service.data.ServProvHasServHasServPt;
import com.extenprise.mapp.service.data.ServProvHasService;
import com.extenprise.mapp.service.data.Service;
import com.extenprise.mapp.service.data.ServicePoint;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.SearchDoctor;
import com.extenprise.mapp.util.UIUtility;

import java.util.Calendar;

public class SearchResult extends Activity {

    private ImageView mImageViewAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        ServProvHasServHasServPt spsspt = LoginHolder.spsspt;
        mImageViewAvailable = (ImageView)findViewById(R.id.imageViewAvailability);

        if(UIUtility.findDocAvailability(spsspt.getWeeklyOff())) {
            mImageViewAvailable.setImageResource(R.drawable.g_circle);
        } else {
            mImageViewAvailable.setImageResource(R.drawable.r_circle);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_search_result, menu);
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
