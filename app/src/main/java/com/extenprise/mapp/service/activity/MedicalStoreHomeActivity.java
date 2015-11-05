package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.net.MappServiceConnection;
import com.extenprise.mapp.net.ResponseHandler;
import com.extenprise.mapp.net.ServiceResponseHandler;
import com.extenprise.mapp.service.data.RxInboxItem;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.util.Utility;

import java.util.ArrayList;


public class MedicalStoreHomeActivity extends Activity implements ResponseHandler{
    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this));

    private ServiceProvider mServProv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_store_home);

        mServProv = LoginHolder.servLoginRef;

        TextView welcomeView = (TextView) findViewById(R.id.viewWelcomeLbl);
        String label = welcomeView.getText().toString() + " " +
                mServProv.getfName() + " " +
                mServProv.getlName();

        welcomeView.setText(label);

        ImageView img = (ImageView) findViewById(R.id.imageMedstore);
        if(mServProv.getImg() != null) {
            img.setImageBitmap(Utility.getBitmapFromBytes(mServProv.getImg()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_medical_store_home, menu);
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

    public void viewRxInbox(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", mServProv.getServProvHasServPt(0).getIdServProvHasServPt());
        mConnection.setAction(MappService.DO_GET_RX_INBOX);
        mConnection.setData(bundle);
        Utility.doServiceAction(this, mConnection, BIND_AUTO_CREATE);
    }

    private void gotRxInbox(Bundle data) {
        ArrayList<RxInboxItem> list = data.getParcelableArrayList("inbox");
        Intent intent = new Intent(this, RxListActivity.class);
        intent.putParcelableArrayListExtra("inbox", list);
        intent.putExtra("feedback", false);
        startActivity(intent);
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        try {
            unbindService(mConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (action == MappService.DO_GET_RX_INBOX) {
            gotRxInbox(data);
            return true;
        }
        return false;
    }

    public void viewProfile(View view) {
        Intent intent = new Intent(this, ServProvViewProfile.class);
        intent.putExtra("service", mServProv);
        startActivity(intent);
    }
}
