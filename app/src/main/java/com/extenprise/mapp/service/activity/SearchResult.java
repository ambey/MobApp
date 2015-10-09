package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.service.data.ServProvHasServPt;
import com.extenprise.mapp.util.Utility;

import java.util.Calendar;

public class SearchResult extends Activity {

    private ImageView mImageViewAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        ServProvHasServPt spsspt = LoginHolder.spsspt;
        mImageViewAvailable = (ImageView)findViewById(R.id.imageViewAvailability);

        if(Utility.findDocAvailability(spsspt.getWorkingDays(), Calendar.getInstance())) {
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
