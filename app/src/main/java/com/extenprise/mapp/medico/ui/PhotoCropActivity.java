package com.extenprise.mapp.medico.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.edmodo.cropper.CropImageView;
import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.data.WorkingDataStore;
import com.extenprise.mapp.medico.util.BitmapToByteArrayTask;
import com.extenprise.mapp.medico.util.ByteArrayToBitmapTask;
import com.extenprise.mapp.medico.util.Utility;

import java.io.IOException;

public class PhotoCropActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_crop);

        final CropImageView imageView = (CropImageView) findViewById(R.id.cropImageView);
        final Uri imageUri = WorkingDataStore.getBundle().getParcelable("uri");
        if (imageUri == null) {
            return;
        }
        AsyncTask<Void, Void, byte[]> task = new AsyncTask<Void, Void, byte[]>() {
            @Override
            protected byte[] doInBackground(Void... params) {
                try {
                    return Utility.readBytes(getContentResolver().openInputStream(imageUri));
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(byte[] bytes) {
                ByteArrayToBitmapTask bitmapTask = new ByteArrayToBitmapTask(imageView, bytes,
                        imageView.getMeasuredWidth(), imageView.getMeasuredHeight());
                bitmapTask.execute();
            }
        };
        task.execute();
        Button okButton = (Button) findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapToByteArrayTask task = new BitmapToByteArrayTask(null, imageView.getCroppedImage(), 0, null) {
                    @Override
                    protected void onPostExecute(byte[] bytes) {
                        super.onPostExecute(bytes);
                        WorkingDataStore.getBundle().putByteArray("image", bytes);
                        setResult(RESULT_OK);
                        finish();
                    }
                };
                task.execute();
            }
        });
        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
