package com.extenprise.mapp.medico.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.extenprise.mapp.medico.LoginHolder;
import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.activity.LoginActivity;
import com.extenprise.mapp.medico.net.AppStatus;
import com.extenprise.mapp.medico.net.MappService;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public abstract class Utility {
    private static ProgressDialog _progressDialog;

    /**
     * Shows the progress UI and hides the login form.
     */
    /*public static void showProgress(Context context, ProgressDialog progressDialog, boolean show) {
        if(show) {
            progressDialog = ProgressDialog.show(context, "", "Please Wait...", true);
        } else {
            progressDialog.dismiss();
        }
    }*/
    public static void showProgressDialog(Context context, boolean show) {
        if (show) {
            _progressDialog = ProgressDialog.show(context, "", context.getString(R.string.msg_please_wait), true);
        } else {
            if (_progressDialog != null) {
                _progressDialog.dismiss();
            }
        }
    }

    public static void showProgress(final Context context, final View formView, final View progressView, final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

            formView.setVisibility(show ? View.GONE : View.VISIBLE);
            formView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    formView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            formView.setVisibility(show ? View.GONE : View.VISIBLE);
        }

        /*Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showProgress(context, formView, progressView, false);
                showMessage(context, R.string.msg_cant_upload);
            }
        }, 5000);*/
    }

    public static void showAlert(Activity activity, String title, String msg) {
        showAlert(activity, title, msg, false, null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public static void showAlert(Activity activity, String title, String msg, DialogInterface.OnClickListener listener) {
        showAlert(activity, title, msg, false, null, listener);
    }

    public static void showAlert(Activity activity, String title, String msg, boolean cancelOpt, String[] menuOptions, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        if (msg != null) {
            builder.setMessage(msg);
        }
        if (menuOptions != null) {
            builder.setItems(menuOptions, listener);
        }

        AlertDialog dialog = builder.create();
        if (menuOptions == null) {
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, activity.getString(R.string.ok), listener);
            if (cancelOpt) {
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, activity.getString(R.string.cancel), listener);
            }
        }
        dialog.setCanceledOnTouchOutside(false);
        //dialog.setIcon(R.drawable.med_logo_final);
        dialog.show();
    }

    public static int getMinutes(String time) {
        String[] timeStr = time.split(":");
        return (Integer.parseInt(timeStr[0]) * 60 + Integer.parseInt(timeStr[1]));
    }

    public static String getTimeString(int minutes) {
        return String.format("%02d:%02d", minutes / 60, minutes % 60);
        //return minutes / 60 + ":" + minutes % 60;
    }

    public static boolean findDocAvailability(String availDays, Calendar calendar) {
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String searchDay = "Sunday";
        if (day == 2) {
            searchDay = "Monday";
        } else if (day == 3) {
            searchDay = "Tuesday";
        } else if (day == 4) {
            searchDay = "Wednesday";
        } else if (day == 5) {
            searchDay = "Thursday";
        } else if (day == 6) {
            searchDay = "Friday";
        } else if (day == 7) {
            searchDay = "Saturday";
        }

        ArrayList<String> availDaysList = new ArrayList<>();
        availDaysList.add(availDays);
        if (availDays.contains(",")) {
            availDaysList = new ArrayList<>(Arrays.asList(availDays.trim().split(",")));
        }

        return availDaysList.contains(searchDay);
    }

    public static int getAge(Date dob) {
        Calendar birthCal = Calendar.getInstance();
        birthCal.setTime(dob);

        Calendar nowCal = Calendar.getInstance();

        int age = nowCal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);
        if (nowCal.get(Calendar.MONTH) < birthCal.get(Calendar.MONTH)) {
            age--;
        } else if (nowCal.get(Calendar.MONTH) == birthCal.get(Calendar.MONTH)) {
            if (nowCal.get(Calendar.DAY_OF_MONTH) < birthCal.get(Calendar.DAY_OF_MONTH)) {
                age--;
            }
        }
        return age;
    }

    public static void timePicker(View view, final TextView button) {
        // Process to get Current Time
        final Calendar c = Calendar.getInstance();
        final int hour = c.get(Calendar.HOUR_OF_DAY);
        final int minute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog tpd = new TimePickerDialog(view.getContext(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        // Display Selected time in textbox
                        button.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, hour, minute, false);
        tpd.show();
    }

    public static void datePicker(View view, final TextView button, final DateChangeListener listener,
                                  long defaultTime, long maxTime, long minTime) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(defaultTime);
        final int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH);
        final int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(view.getContext(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = String.format("%02d/%02d/%4d", dayOfMonth, monthOfYear + 1, year);
                        button.setText(date);
                        if (listener != null) {
                            listener.datePicked(date);
                        }
                    }
                }, year, month, day);
        if (maxTime != -1) {
            dpd.getDatePicker().setMaxDate(maxTime);
        }
        if (minTime != -1) {
            dpd.getDatePicker().setMinDate(minTime);
        }
        dpd.show();
    }

    public static Date getStrAsDate(String date, String pattern) {
        Date d = null;
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern(pattern);
        try {
            d = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    public static boolean isTimePassed(String t) {
        Date todayDate, date;
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("HH:mm");
        Calendar now = Calendar.getInstance();
        try {
            todayDate = sdf.parse(now.get(Calendar.HOUR) + ":" + now.get(Calendar.MINUTE));
            date = sdf.parse(t);
        } catch (java.text.ParseException e) {
            todayDate = new Date(0);
            date = new Date(0);
        }
        return date.before(todayDate);
    }

    public static boolean isDateToday(Date date) {
        Calendar cal = Calendar.getInstance();
        //120; // For todays appointment, available time slots would start two hours from now
        // Set the hour, minute and other components to zero, so that we can compare the date.
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date todayDate = cal.getTime();
        return date.compareTo(todayDate) == 0;
    }

    public static int getSpinnerIndex(Spinner spinner, String str) {
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(str)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getBitmapFromBytes(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static void setEnabledButton(Context context, Button button, boolean enabled) {
        button.setEnabled(enabled);
        if (enabled) {
            button.setBackgroundResource(R.drawable.button);
            button.setTextColor(ContextCompat.getColor(context, R.color.ThemeColor));
        } else {
            button.setBackgroundResource(R.drawable.inactive_button);
            button.setTextColor(ContextCompat.getColor(context, R.color.PassiveColor));
        }
    }

    public static void setEnabledButton(Context context, Button button, boolean enabled, int color) {
        button.setEnabled(enabled);
        button.setTextColor(ContextCompat.getColor(context, color));
    }

    public static void showMessage(Context context, int msgId) {
        Toast.makeText(context, context.getString(msgId), Toast.LENGTH_LONG).show();
        Log.v("Home", "############################" + context.getString(msgId));
    }

    /*public static void setNewSpec(Context activity, ArrayList<String> specs, Spinner speciality) {
        specs.add("Other");
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(activity, R.layout.layout_spinner, specs);
        speciality.setAdapter(spinnerAdapter);
    }*/

    public static void setNewSpinner(Context activity, ArrayList<String> list, Spinner spinner, String[] str) {
        if (list == null) {
            list = new ArrayList<>();
        }
        if (str != null) {
            Collections.addAll(list, str);
        }
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(activity, R.layout.layout_spinner, list);
        spinner.setAdapter(spinnerAdapter);
    }

    /* There is already a set of methods named showAlert in this class. */
    /* Please use showAlert to show alert dialog */
    public static AlertDialog openSpecDialog(final Activity activity, final Spinner speciality) {
        final EditText txtSpec = new EditText(activity);
        txtSpec.setHint("Add Speciality");

        return new AlertDialog.Builder(activity)
                .setTitle("Add Speciality")
                .setView(txtSpec)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newSpec = txtSpec.getText().toString();
                        ArrayList<String> specs = new ArrayList<>();
                        specs.add(newSpec);
                        //setNewSpec(activity, specs, speciality);
                        setNewSpinner(activity, specs, speciality, new String[]{activity.getString(R.string.other)});
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static boolean doServiceAction(Context context, ServiceConnection connection, int flag) {
        if (!AppStatus.getInstance(context).isOnline()) {
            showMessage(context, R.string.error_not_online);
            return false;
        }
        Intent intent = new Intent(context, MappService.class);
        return context.bindService(intent, connection, flag);
    }

    public static void enlargeImage(ImageView imageView) {
        if (imageView.getLayoutParams().height == LinearLayout.LayoutParams.FILL_PARENT) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(140, 140);
            params.gravity = Gravity.CENTER;
            imageView.setLayoutParams(params);
            imageView.setAdjustViewBounds(true);
        } else {
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        /*Bitmap bitmapToScale = imageView.getDrawingCache();

        if(bitmapToScale == null)
            return null;
//get the original width and height
        int width = bitmapToScale.getWidth();
        int height = bitmapToScale.getHeight();
// create a matrix for the manipulation
        Matrix matrix = new Matrix();

// resize the bit map
        matrix.postScale(500 / width, 500 / height);

// recreate the new Bitmap and set it back
        return Bitmap.createBitmap(bitmapToScale, 0, 0, bitmapToScale.getWidth(), bitmapToScale.getHeight(), matrix, true);*/
    }

    public static void expand(final View v, View view) {
        if (view != null) {
            view.setBackgroundResource(R.drawable.expand);
        }
        v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int targtetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LayoutParams.WRAP_CONTENT
                        : (int) (targtetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int) (targtetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v, View view) {
        if (view != null) {
            view.setBackgroundResource(R.drawable.label);
        }
        final int initialHeight = v.getMeasuredHeight();
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void setLastVisit(SharedPreferences prefer) {
        //SharedPreferences prefer = activity.getSharedPreferences(type + "lastVisit" + phone, 0);
        SharedPreferences.Editor preferencesEditor = prefer.edit();
        preferencesEditor.putBoolean("saveVisit", true);
        Calendar calendar = Calendar.getInstance();
        /*preferencesEditor.putString("Date", getDateAsStr(calendar.getTime(), "dd/MM/yyyy"));
        preferencesEditor.putString("Time", getFormattedTime(calendar));
        preferencesEditor.apply();*/

        SharedPreferences.Editor prefEditor = prefer.edit();
        prefEditor.putString("lastVisitDate", Utility.getDateAsStr(calendar.getTime(), "dd/MM/yyyy"));
        prefEditor.putString("lastVisitTime", Utility.getFormattedTime(calendar));
        prefEditor.apply();
    }


    public static void setCurrentDateOnView(TextView v) {
        Calendar c = Calendar.getInstance();
        if (v != null) {
            v.setText(getDateAsStr(c.getTime(), "dd/MM/yyyy"));
        }
    }

    public static String getFormattedTime(Calendar calendar) {
        return String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }

    public static void setCurrentTimeOnView(TextView v) {
        Calendar c = Calendar.getInstance();
        if (v != null) {
            v.setText(getFormattedTime(c));
        }
    }

    public static int getStringPosInArray(String[] array, String value) {
        try {
            for (int i = 0; i < array.length; i++) {
                if (array[i].equalsIgnoreCase(value)) {
                    return i;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int getDrugTypePosition(Context context, String drugType) {
        String[] drugTypes = context.getResources().getStringArray(R.array.drug_type);
        return getStringPosInArray(drugTypes, drugType);
    }

    public static int getDoseUnitPosition(Context context, String drugType, String doseUnit) {
        int arrayResId = -1;
        if (drugType.equalsIgnoreCase(context.getString(R.string.syrup))) {
            arrayResId = R.array.syrup_unit;
        } else if (drugType.equalsIgnoreCase(context.getString(R.string.powder))) {
            arrayResId = R.array.powder_unit;
        }
        if (arrayResId == -1) {
            return -1;
        }
        return getStringPosInArray(context.getResources().getStringArray(arrayResId), doseUnit);
    }

    public static int getEmptyOrFullPosition(Context context, boolean empty) {
        String[] emptyOrFull = context.getResources().getStringArray(R.array.when);
        String when = context.getResources().getString(R.string.before_meal);
        if (!empty) {
            when = context.getResources().getString(R.string.after_meal);
        }
        if (emptyOrFull[0].equalsIgnoreCase(when)) {
            return 0;
        }
        return 1;
    }

    public static String[] getDaysOptions(Context context) {
        return context.getResources().getStringArray(R.array.days);
    }

    /* There is already a set of methods named showAlert in this class. */
    /* Please use showAlert to show alert dialog */
    public static void confirm(Context activity, int msg, DialogInterface.OnClickListener listener) throws Resources.NotFoundException {
        //final boolean[] confirm = new boolean[1];
        new AlertDialog.Builder(activity)
                .setMessage(msg)
                .setPositiveButton(R.string.yes, listener)
                .setNegativeButton(R.string.no, listener);
    }

    /* There is already a set of methods named showAlert in this class. */
    /* Please use showAlert to show alert dialog */
    public static AlertDialog.Builder customDialogBuilder(final Activity activity, View dialogView, int title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (dialogView != null) {
            builder.setTitle(title);
            builder.setView(dialogView);
        } else {
            builder.setMessage(title);
        }
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder;
    }

    public static boolean isDateAfterToday(Date date) {
        Date today = new Date();
        if (today.compareTo(date) < 0) {
            return true;
        } else if (today.compareTo(date) > 0) {
            return false;
        } else {
            return false; //Both Dates are equal
        }
    }

    public static String getDateAsStr(Date date, String pattern) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern(pattern);
        return sdf.format(date);
    }

    public static String getDateForDisplay(Context context, Date date, String pattern) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        Calendar todayCal = Calendar.getInstance();
        int currentYear = todayCal.get(Calendar.YEAR);
        int currentMonth = todayCal.get(Calendar.MONTH);
        int today = todayCal.get(Calendar.DAY_OF_MONTH);
        if (year == currentYear && month == currentMonth) {
            if (day == today) {
                return context.getString(R.string.today);
            }
        }
        todayCal.set(Calendar.DAY_OF_MONTH, today + 1);
        currentYear = todayCal.get(Calendar.YEAR);
        currentMonth = todayCal.get(Calendar.MONTH);
        if (year == currentYear && month == currentMonth) {
            if (day == todayCal.get(Calendar.DAY_OF_MONTH)) {
                return context.getString(R.string.tomorrow);
            }
        }
        Calendar yesterCal = Calendar.getInstance();
        yesterCal.set(Calendar.DAY_OF_MONTH, today - 1);
        currentYear = yesterCal.get(Calendar.YEAR);
        currentMonth = yesterCal.get(Calendar.MONTH);
        if (year == currentYear && month == currentMonth) {
            if (day == yesterCal.get(Calendar.DAY_OF_MONTH)) {
                return context.getString(R.string.yesterday);
            }
        }

        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern(pattern);
        return sdf.format(date);
    }

    public static void logout(SharedPreferences loginPreferences, Activity activity) {
        if (loginPreferences.getBoolean("saveLogin", false)) {
            loginPreferences.edit().clear().apply();
        }
        LoginHolder.custLoginRef = null;
        LoginHolder.servLoginRef = null;
        activity.finish();
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }

    public static void startCamera(Activity activity, int request) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(intent, request);
    }

    public static void pickPhotoFromGallery(Activity activity, int request) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(galleryIntent, request);
    }

    public static boolean areEditFieldsEmpty(Activity activity, EditText[] fields) {
        for (EditText field : fields) {
            if (field.isEnabled()) {
                if (TextUtils.isEmpty(field.getText().toString().trim())) {
                    field.setError(activity.getString(R.string.error_field_required));
                    field.requestFocus();
                    return true;
                }
            }
        }
        return false;
    }

    public static Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, bos);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Photo", null);
        return Uri.parse(path);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusView = activity.getCurrentFocus();
        if (focusView != null) {
            manager.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
        }
    }

    public static String getCommaSepparatedString(String[] arr) {
        try {
            String value = arr[0];
            for (int i = 1; i < arr.length; i++) {
                value += "," + arr[i];
            }
            return value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    ///////////////////////////////////////////////////Un Used Method /////////////////////


    public static String getDays(Context context) {
        return getCommaSepparatedString(context.getResources().getStringArray(R.array.days));
    }

    public static String getDateAsString(String sep) {
        Calendar c = Calendar.getInstance();
        return String.format("%02d/%02d/%04d", c.get(Calendar.DAY_OF_MONTH)
                , c.get(Calendar.MONTH) + 1
                , c.get(Calendar.YEAR));
    }

    public static void datePicker(View view, final TextView button, final DateChangeListener listener) {
        datePicker(view, button, listener, Calendar.getInstance().getTimeInMillis(), -1, -1);
    }

    public static void datePicker(View view, final TextView button) {
        datePicker(view, button, null, Calendar.getInstance().getTimeInMillis(), -1, -1);
    }

    /*public static int getAge(String dob, String sep) {

        *//*long ageInMillis = new Date().getTime() - getMinutes(dob);
        Date age = new Date(ageInMillis);
        return age.getYear();*//*

        int year = 0, month = 0, day = 0;
        if (!dob.equals("")) {
            if (dob.contains(sep)) {
                String[] dobStr = dob.split(sep, 3);
                if (dobStr.length == 3) {
                    day = Integer.parseInt(dobStr[0]);
                    month = Integer.parseInt(dobStr[1]);
                    year = Integer.parseInt(dobStr[2]);
                }
            }
        }
        GregorianCalendar cal = new GregorianCalendar();
        int y, m, d, a;

        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(year, month, day);

        a = y - cal.get(Calendar.YEAR);
        if ((m < cal.get(Calendar.MONTH))
                || ((m == cal.get(Calendar.MONTH)) && (d < cal
                .get(Calendar.DAY_OF_MONTH)))) {
            --a;
        }
        return a;
    }

    *//*public boolean onDoubleTap(MotionEvent e) {
        ImageView imageView = (ImageView) findViewById(imageViewId);
        int width = imageView.getWidth();
        int height = imageView.getWidth();
        float maxScale;
        if ( width < height ) {
            maxScale = (float) (width * Math.pow(1.5, 6));
        } else {
            maxScale = (float) (height * Math.pow(1.5, 6));
        }

        Drawable d = imageView.getDrawable();
        int imageWidth = d.getIntrinsicWidth();
        int imageHeight = d.getIntrinsicHeight();
        float[] value = new float[9];
        matrix.getValues(value);
        scaleWidth = (int)(imageWidth * value[Matrix.MSCALE_X]);
        scaleHeight = (int)(imageHeight * value[Matrix.MSCALE_Y]);

        if ( (scaleWidth * 2) < maxScale ) {
            matrix.postScale(2, 2, e.getRawX(), e.getRawY());
        } else {
            matrix.postScale(0, 0, e.getRawX(), e.getRawY());
        }
        isDoubleTab = true;
        tuneMatrix(matrix);
        savedMatrix.set(matrix);
        return false;
    }*//*

    public static void expandOrCollapse(final View v, String exp_or_colpse) {
        TranslateAnimation anim;
        if (exp_or_colpse.equals("expand")) {
            anim = new TranslateAnimation(0.0f, 0.0f, -v.getHeight(), 0.0f);
            v.setVisibility(View.VISIBLE);
        } else {
            anim = new TranslateAnimation(0.0f, 0.0f, 0.0f, -v.getHeight());
            Animation.AnimationListener collapselistener = new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    v.setVisibility(View.GONE);
                }
            };
            anim.setAnimationListener(collapselistener);
        }
        anim.setDuration(300);
        anim.setInterpolator(new AccelerateInterpolator(0.5f));
        v.startAnimation(anim);
    }

    public static Animation imgAnim() {
        RotateAnimation anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

//Setup anim with desired properties
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE); //Repeat animation indefinitely
        anim.setDuration(700); //Put desired duration per anim cycle here, in milliseconds

//Start animation
        return anim;
    }*/
}
