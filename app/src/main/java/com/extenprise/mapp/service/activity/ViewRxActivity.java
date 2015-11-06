package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.data.Rx;
import com.extenprise.mapp.data.RxItem;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.data.AppointmentListItem;
import com.extenprise.mapp.service.ui.RxItemListAdapter;

import java.text.SimpleDateFormat;

public class ViewRxActivity extends Activity implements ResponseHandler {

    private Messenger mService;
    private ServiceResponseHandler mRespHandler = new ServiceResponseHandler(this);

    private String mParentActivity;
    private AppointmentListItem mOrigAppont;
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
        TextView date = (TextView) layout.findViewById(R.id.dateView);
        TextView fname = (TextView) layout.findViewById(R.id.patientFNameTextView);
        TextView lname = (TextView) layout.findViewById(R.id.patientLNameTextView);
        TextView time = (TextView) layout.findViewById(R.id.appointmentTimeTextView);
        TextView gender = (TextView) layout.findViewById(R.id.patientGenderTextView);
        TextView age = (TextView) layout.findViewById(R.id.patientAgeTextView);
        TextView weight = (TextView) layout.findViewById(R.id.patientWeightTextView);

        Button feedbackButton = (Button) findViewById(R.id.buttonSendAvailability);
        feedbackButton.setVisibility(View.GONE);

        Intent intent = getIntent();

        mParentActivity = intent.getStringExtra("parent-activity");

        mOrigAppont = intent.getParcelableExtra("appont");
        mAppont = intent.getParcelableExtra("pastAppont");

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

    private void fillRxItems() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_rx, menu);
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

    public Intent getParentActivityIntent() {
        Intent intent = super.getParentActivityIntent();
        if (mParentActivity != null) {
            try {
                intent = new Intent(this, Class.forName(mParentActivity));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        if(intent != null) {
            intent.putExtra("appont", mOrigAppont);
            intent.putParcelableArrayListExtra("appontList", getIntent().getParcelableArrayListExtra("appontList"));
            intent.putExtra("parent-activity", getIntent().getParcelableExtra("super_parent_activity"));
        }
        return intent;
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

            bundle.putParcelable("form", mAppont);
            Message msg = Message.obtain(null, MappService.DO_GET_RX);
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

    private void gotRx(Bundle data) {
        Rx rx = data.getParcelable("rx");
        ListView rxItemsList = (ListView) findViewById(R.id.listRxItems);
        ArrayAdapter<RxItem> adapter = new RxItemListAdapter(this, 0, rx, false);
        rxItemsList.setAdapter(adapter);
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        try {
            unbindService(mConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (action == MappService.DO_GET_RX) {
            gotRx(data);
            return true;
        }
        return false;
    }
}
