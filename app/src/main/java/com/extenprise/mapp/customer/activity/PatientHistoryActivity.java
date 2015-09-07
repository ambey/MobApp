package com.extenprise.mapp.customer.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.customer.data.Customer;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.service.activity.ViewRxActivity;
import com.extenprise.mapp.util.DBUtil;


public class PatientHistoryActivity extends Activity {
    private String mParentActivity;
    private int mServProvId;
    private int mCustId;
    private int mAppontId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_history);

        TextView viewFName = (TextView) findViewById(R.id.textViewFName);
        TextView viewLName = (TextView) findViewById(R.id.textViewLName);
        ListView lvMyAppont = (ListView) findViewById(R.id.listViewMyAppont);
        ListView lvOthAppont = (ListView) findViewById(R.id.listViewOtherData);

        Intent intent = getIntent();
        mParentActivity = intent.getStringExtra("parent-activity");
        mServProvId = intent.getIntExtra("sp_id", -1);
        mCustId = intent.getIntExtra("cust_id", -1);
        mAppontId = intent.getIntExtra("appont_id", -1);

        MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
        Customer customer = DBUtil.getCustomer(dbHelper, mCustId);
        viewFName.setText(customer.getfName());
        viewLName.setText(customer.getlName());

        Cursor myCursor = DBUtil.getServProvAppointmentsCursor(dbHelper, mServProvId, null);
        Cursor othCursor = DBUtil.getOtherServProvAppointmentsCursor(dbHelper, mServProvId);

        String[] columns = {
                MappContract.Appointment.COLUMN_NAME_DATE,
                MappContract.Appointment._ID
        };
        int[] viewIds = {
                R.id.dateTextView,
                R.id.appontIdTextView
        };
        SimpleCursorAdapter myAdapter = new SimpleCursorAdapter(this,
                R.layout.layout_appont_row,
                myCursor,
                columns,
                viewIds,
                0);
        lvMyAppont.setAdapter(myAdapter);

        SimpleCursorAdapter othAdapter = new SimpleCursorAdapter(this,
                R.layout.layout_appont_row,
                othCursor,
                columns,
                viewIds,
                0);
        lvOthAppont.setAdapter(othAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_patient_history, menu);
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

    public void showRxDetails(View view) {
        View parent = (View) view.getParent();
        TextView b = (TextView) parent.findViewById(R.id.appontIdTextView);
        int appontId = Integer.parseInt(b.getText().toString());
        Intent intent = new Intent(this, ViewRxActivity.class);
        intent.putExtra("parent-activity", getClass().getName());
        intent.putExtra("appont_id", mAppontId);
        intent.putExtra("last_appont_id", appontId);
        intent.putExtra("cust_id", mCustId);
        intent.putExtra("sp_id", mServProvId);
        startActivity(intent);
    }

    public Intent getParentActivityIntent() {
        Intent intent;
        if (mParentActivity != null) {
            try {
                intent = new Intent(this, Class.forName(mParentActivity));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                intent = super.getParentActivityIntent();
            }
        } else {
            intent = super.getParentActivityIntent();
        }
        if (intent != null) {
            intent.putExtra("appont_id", mAppontId);
            intent.putExtra("cust_id", mCustId);
            intent.putExtra("sp_id", mServProvId);
        }
        return intent;
    }

}
