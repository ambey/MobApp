package com.extenprise.mapp.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.*;

public abstract class UploadImage extends Activity {

    private static ImageView img;
    public static void setImg(ImageView img) {
        UploadImage.img = img;
    }

    private static Context context;
    public static void setContext(Context context) {
        UploadImage.context = context;
    }

    private static int RESULT_LOAD_IMG = 1;
    private static int REQUEST_CAMERA = 1;
    String imgDecodableString;

    public static void uploadImage(final Activity activity) {

        final CharSequence[] items = { "Take Photo", "Choose from Gallery", "Cancel" };
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setTitle("Upload Image ");
        dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File f = new File(android.os.Environment
                                .getExternalStorageDirectory(), "temp.jpg");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                        activity.startActivityForResult(intent, REQUEST_CAMERA);
                        break;

                    case 1:
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        activity.startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
                        break;

                    case 2:
                        dialog.dismiss();
                        break;
                }
            }

        });
        dialogBuilder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (resultCode == RESULT_OK) {
                if (requestCode == RESULT_LOAD_IMG
                        && null != data) {
                    // Get the Image from data

                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();
                    // Set the Image in ImageView after decoding the String
                    img.setImageBitmap(BitmapFactory
                            .decodeFile(imgDecodableString));

                } else if (requestCode == REQUEST_CAMERA) {
                    File f = new File(Environment.getExternalStorageDirectory()
                            .toString());
                    for (File temp : f.listFiles()) {
                        if (temp.getName().equals("temp.jpg")) {
                            f = temp;
                            break;
                        }
                    }
                    try {
                        Bitmap bm;
                        BitmapFactory.Options btmapOptions = new BitmapFactory.Options();

                        bm = BitmapFactory.decodeFile(f.getAbsolutePath(),
                                btmapOptions);

                        // bm = Bitmap.createScaledBitmap(bm, 70, 70, true);
                        img.setImageBitmap(bm);

                        String path = android.os.Environment
                                .getExternalStorageDirectory()
                                + File.separator
                                + "Phoenix" + File.separator + "default";
                        f.delete();
                        OutputStream fOut = null;
                        File file = new File(path, String.valueOf(System
                                .currentTimeMillis()) + ".jpg");
                        try {
                            fOut = new FileOutputStream(file);
                            bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                            fOut.flush();
                            fOut.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(context, "You haven't picked Image",
                            Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    public String getPath(Uri uri) {

        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor cursor = managedQuery(uri, projection, null, null, null);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);

    }

}
