package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.extenprise.mapp.R;
import com.extenprise.mapp.data.Appointment;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.util.DBUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class ScannedRxActivity extends Activity {

    private int mAppontId;
    private String mCustId;
    private Bitmap mRxCopy;
    private ImageView mRxView;
    private Uri mRxUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_rx);

        Intent intent = getIntent();
        mAppontId = intent.getIntExtra("appont_id", -1);
        mCustId = intent.getStringExtra("cust_id");

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scanned_rx, menu);
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

    protected void displayScanCopy(Intent data) {
        try {
            if (data != null) {
                InputStream stream = getContentResolver().openInputStream(
                        data.getData());
                mRxCopy = BitmapFactory.decodeStream(stream);
                mRxView.setImageBitmap(mRxCopy);
                stream.close();
            } else {
                Uri selectedImage = mRxUri;
                getContentResolver().notifyChange(selectedImage, null);
                ContentResolver cr = getContentResolver();
                Bitmap bitmap;
                try {
                    bitmap = android.provider.MediaStore.Images.Media
                            .getBitmap(cr, selectedImage);

                    mRxView.setImageBitmap(bitmap);
                    Toast.makeText(this, selectedImage.toString(),
                            Toast.LENGTH_LONG).show();
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1 || requestCode == 2) {
                SaveBlobTask saveBlobTask = new SaveBlobTask(this, data);
                saveBlobTask.execute();
            }
        }
    }


    private class SaveBlobTask extends AsyncTask<Void, Void, Void> {

        private Activity mActivity;
        private Intent mData;

        public SaveBlobTask(Activity activity, Intent data) {
            mData = data;
            mActivity = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            MappDbHelper dbHelper = new MappDbHelper(mActivity);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            byte[] bytes = null;
            String path;
            if (mData != null) {
                path = mData.getDataString();
            } else {
                path = mRxUri.getPath();
            }
            try {
                if (path != null) {
                    RandomAccessFile raf = new RandomAccessFile(path, "r");
                    bytes = new byte[(int) raf.length()];
                    raf.readFully(bytes);
                    raf.close();
                }
            } catch (IOException x) {
                x.printStackTrace();
            }
            ContentValues values = new ContentValues();
            Appointment appointment = DBUtil.getAppointment(dbHelper, mAppontId);
            String rxId = "a" + appointment.getIdAppointment() + "r" + (appointment.getReportCount() + 1);

            values.put(MappContract.Prescription.COLUMN_NAME_ID_APPOMT, appointment.getIdAppointment());
            values.put(MappContract.Prescription.COLUMN_NAME_ID_RX, rxId);
            values.put(MappContract.Prescription.COLUMN_NAME_SCANNED_COPY, bytes);
            db.insert(MappContract.Prescription.TABLE_NAME, null, values);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            displayScanCopy(mData);
        }

        @Override
        protected void onCancelled() {
        }

    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        Intent intent = super.getParentActivityIntent();
        assert intent != null;
        intent.putExtra("cust_id", mCustId);
        intent.putExtra("appont_id", mAppontId);
        return intent;
    }
}
