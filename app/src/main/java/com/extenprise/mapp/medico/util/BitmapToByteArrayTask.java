package com.extenprise.mapp.medico.util;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.extenprise.mapp.medico.data.HasPhoto;

/**
 * Created by ambey on 4/3/16.
 */
public class BitmapToByteArrayTask extends AsyncTask<Void, Void, byte[]> {
    private HasPhoto photoRef;
    private Bitmap bitmap;
    private int action;
    private PhotoTaskCompleteListener listener;

    public BitmapToByteArrayTask(HasPhoto photoRef, Bitmap bitmap, int action, PhotoTaskCompleteListener listener) {
        this.photoRef = photoRef;
        this.bitmap = bitmap;
        this.action = action;
        this.listener = listener;
    }

    public BitmapToByteArrayTask(HasPhoto photoRef, Bitmap bitmap) {
        this(photoRef, bitmap, -1, null);
    }

    @Override
    protected byte[] doInBackground(Void... params) {
        return Utility.getBytesFromBitmap(bitmap);
    }

    @Override
    protected void onPostExecute(byte[] bytes) {
        super.onPostExecute(bytes);
        if (photoRef != null) {
            photoRef.setPhoto(bytes);
        }
        if (listener != null) {
            listener.taskCompleted(action, photoRef);
        }
    }
}
