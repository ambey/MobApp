package com.extenprise.mapp.medico.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.activity.LoginActivity;
import com.extenprise.mapp.medico.data.Rx;
import com.extenprise.mapp.medico.data.RxFeedback;
import com.extenprise.mapp.medico.data.RxItem;
import com.extenprise.mapp.medico.net.MappService;
import com.extenprise.mapp.medico.net.MappServiceConnection;
import com.extenprise.mapp.medico.net.ResponseHandler;
import com.extenprise.mapp.medico.net.ServiceResponseHandler;
import com.extenprise.mapp.medico.service.data.AppointmentListItem;
import com.extenprise.mapp.medico.service.data.RxInboxItem;
import com.extenprise.mapp.medico.service.ui.RxItemListAdapter;
import com.extenprise.mapp.medico.util.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ViewRxActivity extends Activity implements ResponseHandler {

    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));
    private String mParentActivity;
    private AppointmentListItem mAppont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_rx);

        View view = findViewById(R.id.layoutRxHead);
        view.setVisibility(View.GONE);
        view = findViewById(R.id.layoutRxFeedbackHead);
        view.setVisibility(View.GONE);

        View layout = findViewById(R.id.layoutAppont);
        layout.setBackgroundResource(R.drawable.text);

        TextView date = (TextView) layout.findViewById(R.id.dateView);
        TextView fname = (TextView) layout.findViewById(R.id.patientFNameTextView);
        TextView lname = (TextView) layout.findViewById(R.id.patientLNameTextView);
        TextView time = (TextView) layout.findViewById(R.id.appointmentTimeTextView);
        TextView gender = (TextView) layout.findViewById(R.id.patientGenderTextView);
        TextView age = (TextView) layout.findViewById(R.id.patientAgeTextView);
        TextView weight = (TextView) layout.findViewById(R.id.patientWeightTextView);

        Button feedbackButton = (Button) findViewById(R.id.buttonSendAvailability);
        feedbackButton.setVisibility(View.GONE);
        Button resendRxButton = (Button) findViewById(R.id.buttonResendRx);
        resendRxButton.setVisibility(View.GONE);

        Intent intent = getIntent();
        if (savedInstanceState != null) {
            mParentActivity = savedInstanceState.getString("parent-activity");
            mAppont = savedInstanceState.getParcelable("pastAppont");
        } else {
            mParentActivity = intent.getStringExtra("parent-activity");
            mAppont = intent.getParcelableExtra("pastAppont");
        }
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        date.setText(sdf.format(mAppont.getDate()));
        fname.setText(mAppont.getFirstName());
        lname.setText(mAppont.getLastName());
        time.setText(mAppont.getTime());
        gender.setText(mAppont.getGender());
        age.setText(String.format("%d", mAppont.getAge()));
        weight.setText(String.format("%.1f", mAppont.getWeight()));

        fillRxItems();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("appont", getIntent().getParcelableExtra("appont"));
        outState.putParcelable("pastAppont", getIntent().getParcelableExtra("pastAppont"));
        outState.putString("parent-activity", getIntent().getStringExtra("parent-activity"));
        outState.putString("super_parent_activity", getIntent().getStringExtra("super_parent_activity"));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        getIntent().putExtra("appont", savedInstanceState.getParcelable("appont"));
        getIntent().putExtra("super_parent_activity", savedInstanceState.getString("super_parent_activity"));
    }

    private void fillRxItems() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("form", mAppont);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_GET_RX);
        if (Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE)) {
            Utility.showProgressDialog(this, true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return super.onCreateOptionsMenu(menu);
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
        if (id == R.id.logout) {
            Utility.logout(getSharedPreferences("loginPrefs", MODE_PRIVATE), this, LoginActivity.class);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Intent getParentActivityIntent() {
        Intent intent = super.getParentActivityIntent();
        if (mParentActivity != null) {
            try {
                intent = new Intent(this, Class.forName(mParentActivity));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (intent != null) {
            intent.putExtra("appont", getIntent().getParcelableExtra("appont"));
            intent.putParcelableArrayListExtra("appontList", getIntent().getParcelableArrayListExtra("appontList"));
            intent.putExtra("parent-activity", getIntent().getParcelableExtra("super_parent_activity"));
        }
        return intent;
    }

    private void gotRx(Bundle data) {
        Utility.showProgressDialog(this, false);
        Rx rx = data.getParcelable("rx");
        if (rx == null || rx.getRxItemCount() == 0) {
            TextView msgView = (TextView) findViewById(R.id.viewMsgNoItems);
            msgView.setVisibility(View.VISIBLE);
            return;
        }
        ListView rxItemsList = (ListView) findViewById(R.id.listRxItems);
        ArrayList<RxInboxItem> rxList = new ArrayList<>();
        RxInboxItem inboxItem = new RxInboxItem();
        inboxItem.setRx(rx);
        rxList.add(inboxItem);
        ArrayAdapter<RxItem> adapter = new RxItemListAdapter(this, 0, rxList, inboxItem, RxFeedback.NONE);
        rxItemsList.setAdapter(adapter);
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        if (action == MappService.DO_GET_RX) {
            gotRx(data);
        }
        return data.getBoolean("status");
    }

    @Override
    public void onBackPressed() {
        mConnection.setBound(false);
        //startActivity(getIntent());
        super.onBackPressed();
    }
}
