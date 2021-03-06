package com.extenprise.mapp.medico.service.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.activity.LoginActivity;
import com.extenprise.mapp.medico.data.Rx;
import com.extenprise.mapp.medico.net.MappService;
import com.extenprise.mapp.medico.net.MappServiceConnection;
import com.extenprise.mapp.medico.net.ResponseHandler;
import com.extenprise.mapp.medico.net.ServiceResponseHandler;
import com.extenprise.mapp.medico.service.data.AppointmentListItem;
import com.extenprise.mapp.medico.util.BitmapToByteArrayTask;
import com.extenprise.mapp.medico.util.ByteArrayToBitmapTask;
import com.extenprise.mapp.medico.util.Utility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ScannedRxActivity extends Activity implements ResponseHandler {

    private MappServiceConnection mConnection = new MappServiceConnection(new ServiceResponseHandler(this, this));
    private AppointmentListItem mAppont;
    private Bitmap mRxCopy;
    private ImageView mRxView;
    private Uri mRxUri;
    private Intent mData;
    private Button mResendRx;
    private String mParentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_rx);

        Intent intent = getIntent();
        if (savedInstanceState != null) {
            mAppont = savedInstanceState.getParcelable("appont");
            mParentActivity = savedInstanceState.getString("parent-activity");
        } else {
            mAppont = intent.getParcelableExtra("appont");
            mParentActivity = intent.getStringExtra("parent-activity");
        }

        mRxView = (ImageView) findViewById(R.id.rxCopyImageView);
        if (savedInstanceState != null) {
            Bitmap bitmap = savedInstanceState.getParcelable("image");
            mRxView.setImageBitmap(bitmap);
        } else {
            mRxCopy = (Bitmap) getLastNonConfigurationInstance();
            if (mRxCopy != null) {
                mRxView.setImageBitmap(mRxCopy);
            }
        }
        mResendRx = (Button) findViewById(R.id.btnResend);
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
        switch (item.getItemId()) {
            case R.id.action_settings:
                break;
            case R.id.logout:
                Utility.logout(getSharedPreferences("loginPrefs", MODE_PRIVATE), this, LoginActivity.class);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startImageCapture(View view) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo = new File(Environment.getExternalStorageDirectory(), "rxCopy.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        mRxUri = Uri.fromFile(photo);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 2);
        }
    }

    public void startFileChooser(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    protected void displayScanCopy() {
        try {
            if (mData != null) {
                final InputStream stream = getContentResolver().openInputStream(
                        mData.getData());
                if (stream != null) {
                    AsyncTask<Void, Void, byte[]> task = new AsyncTask<Void, Void, byte[]>() {
                        @Override
                        protected byte[] doInBackground(Void... params) {
                            try {
                                return Utility.readBytes(stream);
                            } catch (IOException e) {
                                e.printStackTrace();
                                return null;
                            }
                        }

                        @Override
                        protected void onPostExecute(byte[] bytes) {
                            ByteArrayToBitmapTask bitmapTask = new ByteArrayToBitmapTask(mRxView, bytes,
                                    mRxView.getMeasuredWidth(), mRxView.getMeasuredHeight());
                            bitmapTask.execute();
                        }
                    };
                    task.execute();
                }
            } else {
                Uri selectedImage = mRxUri;
                getContentResolver().notifyChange(selectedImage, null);
                ContentResolver cr = getContentResolver();
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media
                            .getBitmap(cr, selectedImage);

                    BitmapToByteArrayTask task = new BitmapToByteArrayTask(null, bitmap) {
                        @Override
                        protected void onPostExecute(byte[] bytes) {
                            super.onPostExecute(bytes);
                            ByteArrayToBitmapTask bitmapTask = new ByteArrayToBitmapTask(mRxView, bytes,
                                    mRxView.getMeasuredWidth(), mRxView.getMeasuredHeight());
                            bitmapTask.execute();
                        }
                    };
                    task.execute();
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                            .show();
                    Log.e("Camera", e.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Intent intent = getIntent();
        outState.putParcelable("appont", intent.getParcelableExtra("appont"));
        outState.putString("parent-activity", mParentActivity);
        BitmapDrawable drawable = (BitmapDrawable) mRxView.getDrawable();
        if (drawable != null) {
            Bitmap bitmap = drawable.getBitmap();
            outState.putParcelable("image", bitmap);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return mRxCopy;
    }

/*
    protected byte[] getData(Intent imgData) throws IOException {
        InputStream stream = getContentResolver().openInputStream(
                imgData.getData());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead, offset = 0;
        while ((bytesRead = stream.read(buffer)) != -1) {
            baos.write(buffer, offset, bytesRead);
            offset += bytesRead;
        }
        baos.flush();
        byte[] bytes = baos.toByteArray();
        stream.close();
        baos.close();
        return bytes;
    }
*/

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1 || requestCode == 2) {
                SaveBlobTask saveBlobTask = new SaveBlobTask(data);
                saveBlobTask.execute();
                displayScanCopy();
            }
        }
    }

    private void sendRxToMedStore(Bundle data) {
        Rx rx = data.getParcelable("rx");
        Intent intent = new Intent(this, SelectMedicalStoreActivity.class);
        intent.putExtra("parent-activity", mParentActivity);
        intent.putExtra("rx", rx);
        startActivity(intent);
    }

    @Override
    public boolean gotResponse(int action, Bundle data) {
        Utility.showProgressDialog(ScannedRxActivity.this, false);
        if (action == MappService.DO_SAVE_SCANNED_RX_COPY) {
            //displayScanCopy();
            if (data.getBoolean("status")) {
                sendRxToMedStore(data);
            } else {
                mResendRx.setVisibility(View.VISIBLE);
            }
        }
        return data.getBoolean("status");
    }

    /*
        @Nullable
        @Override
        public Intent getParentActivityIntent() {
            Intent intent = super.getParentActivityIntent();
            assert intent != null;
            intent.putExtra("appont", mAppont);
            return intent;
        }

    */

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        Intent intent;
        try {
            intent = new Intent(this, Class.forName(mParentActivity));
            intent.putExtra("appont", mAppont);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            intent = super.getParentActivityIntent();
        }
        return intent;
    }

    @Override
    public void onBackPressed() {
        mConnection.setBound(false);
        Intent intent = getParentActivityIntent();
        if (intent != null) {
            startActivity(intent);
            return;
        }
        //startActivity(getIntent());
        super.onBackPressed();
    }

/*
    private class SaveBlobTask extends AsyncTask<Void, Void, Void> {

        public SaveBlobTask(Intent data) {
            mData = data;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String path;
            if (mData != null) {
                path = mData.getDataString();
            } else {
                path = mRxUri.getPath();
            }
            try {
                if (path != null) {
                    RandomAccessFile raf = new RandomAccessFile(path, "r");
                    byte[] mBytes = new byte[(int) raf.length()];
                    raf.readFully(mBytes);
                    raf.close();
                    mAppont.setRxCopy(mBytes);
                }
            } catch (IOException x) {
                x.printStackTrace();
            }
*/
/*
            ContentValues values = new ContentValues();
            Appointment appointment = DBUtil.getAppointment(dbHelper, mAppontId);
            String rxId = "a" + appointment.getIdAppointment() + "r" + (appointment.getReportCount() + 1);

            values.put(MappContract.Prescription.COLUMN_NAME_ID_APPOMT, appointment.getIdAppointment());
            values.put(MappContract.Prescription.COLUMN_NAME_ID_RX, rxId);
            values.put(MappContract.Prescription.COLUMN_NAME_SCANNED_COPY, bytes);
            db.insert(MappContract.Prescription.TABLE_NAME, null, values);
*//*

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("form", mAppont);
            mConnection.setData(bundle);
            mConnection.setAction(MappService.DO_SAVE_SCANNED_RX_COPY);
            Utility.doServiceAction(ScannedRxActivity.this, mConnection, BIND_AUTO_CREATE);
        }

        @Override
        protected void onCancelled() {
        }
    }
*/

    public void sendRx(View view) {
        //sendRx();
        Bundle bundle = new Bundle();
        bundle.putParcelable("form", mAppont);
        mConnection.setData(bundle);
        mConnection.setAction(MappService.DO_SAVE_SCANNED_RX_COPY);
        if (Utility.doServiceAction(ScannedRxActivity.this, mConnection, BIND_AUTO_CREATE)) {
            Utility.showProgressDialog(ScannedRxActivity.this, true);
        } else {
            setSendButton(R.string.resend_rx);
        }
    }

    private void setSendButton(int txt) {
        mResendRx.setVisibility(View.VISIBLE);
        mResendRx.setText(getString(txt));
    }

    private class SaveBlobTask extends AsyncTask<Void, Void, byte[]> {

        public SaveBlobTask(Intent data) {
            mData = data;
        }

        @Override
        protected byte[] doInBackground(Void... voids) {
            String path;
            if (mData != null) {
                mRxUri = mData.getData();
            }
            try {
                return Utility.readBytes(getContentResolver().openInputStream(mRxUri));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            mAppont.setRxCopy(bytes);
            setSendButton(R.string.send);
        }
    }
}
