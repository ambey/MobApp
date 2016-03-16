package com.extenprise.mapp.medico.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.IOException;

/**
 * Created by ambey on 4/3/16.
 */
public class UriToBitmapTask extends AsyncTask<Void, Void, Bitmap> {
    private Context context;
    private Uri photoUri;

    public UriToBitmapTask(Context context, Uri photoUri) {
        this.context = context;
        this.photoUri = photoUri;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            return Utility.getBitmapFromUri(context, photoUri);
        } catch (IOException e) {
            return null;
        }
    }
}
