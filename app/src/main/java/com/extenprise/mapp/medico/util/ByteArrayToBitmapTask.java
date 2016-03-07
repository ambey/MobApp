package com.extenprise.mapp.medico.util;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by ambey on 4/3/16.
 */
public class ByteArrayToBitmapTask extends AsyncTask<Void, Void, Bitmap> {
    private WeakReference<ImageView> viewWeakReference;
    private byte[] image;
    private int width;
    private int height;

    public ByteArrayToBitmapTask(ImageView view, byte[] image, int width, int height) {
        viewWeakReference = new WeakReference<>(view);
        this.image = image;
        this.width = width;
        this.height = height;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        return Utility.getBitmapFromBytes(image, width, height);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (viewWeakReference != null) {
            ImageView view = viewWeakReference.get();
            if (view != null && bitmap != null) {
                view.setImageBitmap(bitmap);
            }
        }
    }
}
