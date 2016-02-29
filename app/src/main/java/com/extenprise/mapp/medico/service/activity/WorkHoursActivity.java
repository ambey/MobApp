package com.extenprise.mapp.medico.service.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.service.data.WeeklyWorkTiming;
import com.extenprise.mapp.medico.service.ui.WorkHoursAdapter;

public class WorkHoursActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_hours);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        ListView listViewWorkHours = (ListView) findViewById(R.id.listViewWorkHours);
        listViewWorkHours.setAdapter(new WorkHoursAdapter(this, 0, new WeeklyWorkTiming()));
    }

}
