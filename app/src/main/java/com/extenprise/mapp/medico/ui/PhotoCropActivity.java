package com.extenprise.mapp.medico.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.edmodo.cropper.CropImageView;
import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.data.WorkingDataStore;
import com.extenprise.mapp.medico.util.BitmapToByteArrayTask;
import com.extenprise.mapp.medico.util.ByteArrayToBitmapTask;

public class PhotoCropActivity extends Activity {
    private CropImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_crop);

        mImageView = (CropImageView) findViewById(R.id.cropImageView);
        Button okButton = (Button) findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapToByteArrayTask task = new BitmapToByteArrayTask(null, mImageView.getCroppedImage(), 0, null) {
                    @Override
                    protected void onPostExecute(byte[] bytes) {
                        super.onPostExecute(bytes);
                        WorkingDataStore.getBundle().putByteArray("photo", bytes);
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
                setResult(RESULT_CANCELED);
                WorkingDataStore.getBundle().remove("photo");
                finish();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            return;
        }
        final byte[] bytes = WorkingDataStore.getBundle().getByteArray("photo");
        if (bytes == null) {
            return;
        }
        ByteArrayToBitmapTask bitmapTask = new ByteArrayToBitmapTask(mImageView, bytes,
                mImageView.getMeasuredWidth(), mImageView.getMeasuredHeight());
        bitmapTask.execute();
    }
}
