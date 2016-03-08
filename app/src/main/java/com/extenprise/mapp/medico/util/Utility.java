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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.activity.LoginActivity;
import com.extenprise.mapp.medico.data.WorkingDataStore;
import com.extenprise.mapp.medico.net.AppStatus;
import com.extenprise.mapp.medico.net.ErrorCode;
import com.extenprise.mapp.medico.net.MappService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public abstract class Utility {
    public static final String photoFileName = Environment.getExternalStorageDirectory().getPath() + File.separator + "photo.jpg";
    public static final String photoEditFileName = Environment.getExternalStorageDirectory().getPath() + File.separator + "photoEdit.jpg";

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
        if (_progressDialog != null) {
            _progressDialog.dismiss();
            _progressDialog = null;
        }
        if (show) {
            _progressDialog = ProgressDialog.show(context, "", context.getString(R.string.msg_please_wait), true);
            /*_progressDialog.setProgressDrawable();*/
            //_progressDialog.getIndeterminateDrawable().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.MULTIPLY);
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
        showAlert(activity, title, msg, null, false, null, activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public static void showAlert(Activity activity, String title, String msg, DialogInterface.OnClickListener listener) {
        showAlert(activity, title, msg, null, false, null, activity.getString(R.string.ok), listener);
    }

    /*public static void showAlert(Activity activity, String title, String msg,
                                 DialogInterface.OnClickListener listener, DialogInterface.OnCancelListener cancelListener) {
        showAlert(activity, title, msg, null, false, null, activity.getString(R.string.ok), listener);
    }*/

    public static void showAlert(Activity activity, String title, String msg,
                                 boolean cancelOpt, String[] menuOptions, DialogInterface.OnClickListener listener) {
        showAlert(activity, title, msg, null, cancelOpt, menuOptions, activity.getString(R.string.ok), listener);
    }

    public static void showAlert(Activity activity, String title, String msg, View view,
                                 boolean cancelOpt, String[] menuOptions, DialogInterface.OnClickListener listener) {
        showAlert(activity, title, msg, view, cancelOpt, menuOptions, activity.getString(R.string.ok), listener);
    }

    public static void showAlert(Activity activity, String title, String msg, View view,
                                 boolean cancelOpt, String[] menuOptions, String okLabel, DialogInterface.OnClickListener listener) {
        showAlert(activity, title, msg, view, cancelOpt, menuOptions, okLabel, listener, null);
    }

    public static void showAlert(Activity activity, String title, String msg, View view,
                                 boolean cancelOpt, String[] menuOptions, String okLabel,
                                 DialogInterface.OnClickListener listener,
                                 DialogInterface.OnCancelListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        if (view != null) {
            builder.setView(view);
        }
        if (msg != null) {
            builder.setMessage(msg);
        }
        if (menuOptions != null) {
            builder.setItems(menuOptions, listener);
        }

        AlertDialog dialog = builder.create();
        if (menuOptions == null) {
            //builder.setPositiveButton(R.string.ok, listener);
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, okLabel, listener);
            if (cancelOpt) {
                //builder.setNegativeButton(R.string.cancel, listener);
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, activity.getString(R.string.cancel), listener);
            }
        }

        if (cancelListener != null) {
            dialog.setOnCancelListener(cancelListener);
        }

        dialog.setCanceledOnTouchOutside(false);
        //dialog.setCancelable(false);
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

    public static String displayTime(int st, int en) {
        String start = getTimeString(st);
        String end = getTimeString(en);
        //new SimpleDateFormat("h:mm a");
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("kk:mm");
        try {
            Date d = sdf.parse(start);
            Date d1 = sdf.parse(end);
            sdf.applyPattern("hh:mm aa");
            return String.format("%s to %s", sdf.format(d), sdf.format(d1));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return String.format("%s to %s", start, end);
    }

    public static String getTimeInTwelveFormat(int min) {
        String start = getTimeString(min);
        //new SimpleDateFormat("h:mm a");
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("kk:mm");
        try {
            Date d = sdf.parse(start);
            sdf.applyPattern("hh:mm aa");
            return sdf.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
            return start;
        }
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
                        button.setError(null);
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

    public static Bitmap getBitmapFromBytes(byte[] image, int width, int height) {
        if (width < 0 || height < 0) {
            Log.v("getBitmapFromBytes", "Invalid width or height");
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(image, 0, image.length, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;

        int inSampleSize = 1;
        if (imageHeight > height || imageWidth > width) {
            final int halfHeight = imageHeight / 2;
            final int halfWidth = imageWidth / 2;
            while ((halfHeight / inSampleSize) > height
                    && (halfWidth / inSampleSize) > width) {
                inSampleSize *= 2;
            }
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        return BitmapFactory.decodeByteArray(image, 0, image.length, options);
    }

/*
    // convert from byte array to bitmap
    public static Bitmap getBitmapFromBytes(byte[] image) throws Exception {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
*/

    public static void setEnabledButton(Context context, Button button, boolean enabled) {
        button.setEnabled(enabled);
/*
        if (enabled) {
            button.setBackgroundResource(R.drawable.raised_button_bg);
            button.setTextColor(ContextCompat.getColor(context, R.color.ThemeColor));
        } else {
            button.setBackgroundResource(R.drawable.inactive_button);
            button.setTextColor(ContextCompat.getColor(context, R.color.PassiveColor));
        }
*/
    }

    public static void setEnabledButton(Context context, Button button, boolean enabled, int color) {
        button.setEnabled(enabled);
        button.setTextColor(ContextCompat.getColor(context, color));
    }

    public static void showMessage(Context context, int msgId) {
        if (msgId == -1) {
            return;
        }
        Toast.makeText(context, context.getString(msgId), Toast.LENGTH_LONG).show();
        Log.v("Home", "############################" + context.getString(msgId));
    }

    public static void showErrorMessage(Context context, int errorCode) {
        switch (errorCode) {
            case ErrorCode.ERROR_SERVER_UNAVAILABLE:
                showMessage(context, R.string.error_server_connect);
                break;
            case ErrorCode.ERROR_NETWORK_PROBLEM:
                showMessage(context, R.string.error_not_online);
                break;
            case ErrorCode.ERROR_APPONT_CONFLICT:
                showMessage(context, R.string.error_appointment_clash);
                break;
            /*case ErrorCode.ERROR_INVALID_USER_OR_PASSWD:
                showMessage(context, R.string.error_invalid_user_or_passwd);
                break;*/
            case ErrorCode.ERROR_PHONE_EXISTS:
                showMessage(context, R.string.error_request_already_made);
                break;
            case ErrorCode.ERROR_WRONG_PASSWD:
                showMessage(context, R.string.error_wrong_passwd);
                break;
            case ErrorCode.ERROR_NO_RX_FOUND:
                showMessage(context, R.string.error_no_rx_found);
                break;
            case ErrorCode.ERROR_NO_MATCHING_RESULT:
                showMessage(context, R.string.error_no_matching_result);
                break;
            case ErrorCode.ERROR_NO_AVAILABLE_TIMESLOT:
                showMessage(context, R.string.error_no_available_timeslot);
                break;
            case ErrorCode.ERROR_NO_WORKPLACE_FOUND:
                showMessage(context, R.string.error_no_workplace_found);
                break;
            case ErrorCode.ERROR_USER_EXISTS:
                showMessage(context, R.string.error_phone_registered);
                break;
            case ErrorCode.ERROR_REQUEST_FAILED:
                showMessage(context, R.string.error_request_failed);
                break;
        }
    }

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

    public static boolean doServiceAction(Context context, ServiceConnection connection, int flag) {
        if (!AppStatus.getInstance(context).isOnline()) {
            showMessage(context, R.string.error_not_online);
            return false;
        }
        Intent intent = new Intent(context, MappService.class);
        return context.bindService(intent, connection, flag);
    }

    public static void enlargeImg(Activity activity, ImageView imgV) {
        imgV.setScaleType(ImageView.ScaleType.FIT_XY);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        //activity.getSupportActionBar().hide();
    }

    public static void enlargeImage(ImageView imageView) {
        if (imageView.getLayoutParams().height == LinearLayout.LayoutParams.MATCH_PARENT) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(140, 140);
            params.gravity = Gravity.CENTER;
            imageView.setLayoutParams(params);
            imageView.setAdjustViewBounds(true);
        } else {
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
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

    public static void collapseExpand(final View v) {
        collapse(v, (v.getVisibility() == View.VISIBLE));
    }

    public static void collapse(final View v, final boolean collapse) {
        if (!collapse) {
            v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
        final int height = v.getMeasuredHeight();
        if (!collapse) {
            v.getLayoutParams().height = 0;
            v.setVisibility(View.VISIBLE);
        }

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (collapse) {
                    if (interpolatedTime == 1) {
                        v.setVisibility(View.GONE);
                    } else {
                        v.getLayoutParams().height = height - (int) (height * interpolatedTime);
                        v.requestLayout();
                    }
                } else {
                    v.getLayoutParams().height = interpolatedTime == 1
                            ? LayoutParams.WRAP_CONTENT
                            : (int) (height * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int) (height / v.getContext().getResources().getDisplayMetrics().density));
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
        //prefEditor.clear();// Clearing editor before using it.... Should we do so?
        prefEditor.putString("lastVisitDate", Utility.getDateAsStr(calendar.getTime(), "dd/MM/yyyy"));
        prefEditor.putString("lastVisitTime", Utility.getFormattedTime(calendar));
        prefEditor.apply();
    }

    /*public static void fieldsEnability(View[] views, boolean set) {
        for (View view : views) {
            view.setEnabled(set);
        }
    }*/

    public static void sessionExpired(Activity activity) {
        Utility.showMessage(activity, R.string.error_session_expired);
        Utility.startActivity(activity, LoginActivity.class);
    }

    public static void startActivity(Activity activity, Class targetClass) {
        Log.v("Home", "############################" + activity.getString(R.string.msg_exception));
        Intent intent = new Intent(activity, targetClass);
        activity.startActivity(intent);
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

    public static void logout(SharedPreferences loginPreferences, Activity activity, Class targetClass) {
        if (loginPreferences.getBoolean("saveLogin", false)) {
            loginPreferences.edit().clear().apply();
        }
        WorkingDataStore.getBundle().clear();
        activity.finish();
        Intent intent = new Intent(activity, targetClass);
        activity.startActivity(intent);
    }

    public static void startCamera(Activity activity, int request, File destination) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
        activity.startActivityForResult(intent, request);
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
        boolean empty = false;
        for (EditText field : fields) {
            if (field.isEnabled()) {
                if (TextUtils.isEmpty(field.getText().toString().trim())) {
                    field.setError(activity.getString(R.string.error_field_required));
                    field.requestFocus();
                    empty = true;
                }
            }
        }
        return empty;
    }

    public static void setSpinError(Spinner spin, String msg) {
        View tV = spin.getSelectedView();
        if (tV != null && tV instanceof TextView) {
            TextView selectedtV = (TextView) tV;
            selectedtV.setError(msg);
        }
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

    //While using show Alert method for dialogs having views... its not displaying the buttons.
    //while there is no any difference, so for now am using this method only for showing dialogs..
    //and buttons onclick is not returning thr for validation... its closing the dialog...
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

/*
    public static String[] imageOpts(Activity activity, boolean isRemove) {
        String[] opts = new String[]{activity.getString(R.string.take_photo),
                activity.getString(R.string.from_gallery)};
        if (isRemove) {
            opts = new String[]{activity.getString(R.string.take_photo),
                    activity.getString(R.string.from_gallery),
                    activity.getString(R.string.remove)};
        }
        return opts;
    }
*/

    public static boolean onPhotoActivityResult(Context context, final ImageView imageView, int requestCode, int resultCode, Intent data) {
        boolean imageChanged = false;
        try {
            Uri selectedImage;
            Bitmap bitmap;
            Resources resources = context.getResources();
            // When an Image is picked
            if (resultCode == Activity.RESULT_OK) {
                imageView.setBackgroundResource(0);
                if (data == null) {
                    String photoFileName = Utility.photoFileName;
                    //File photo = new File(photoFileName);
                    selectedImage = Uri.fromFile(new File(photoFileName));
                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedImage);
                    setUpImage(context, imageView, bitmap, null);
                    //imageView.setImageURI(selectedImage);
                    imageChanged = true;
                } else {
                    if (requestCode == resources.getInteger(R.integer.request_gallery)) {
                        // Get the Image from data
                        selectedImage = data.getData();
                        bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedImage);
                        setUpImage(context, imageView, bitmap, data);
                        //imageView.setImageURI(selectedImage);
                        imageChanged = true;
                    } else if (requestCode == resources.getInteger(R.integer.request_camera)) {
                        bitmap = (Bitmap) data.getExtras().get("data");
                        setUpImage(context, imageView, bitmap, data);
                        imageChanged = true;
                    } else {
                        Utility.showMessage(context, R.string.error_img_not_picked);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utility.showMessage(context, R.string.some_error);
        }
        return imageChanged;
    }

    private static void setUpImage(Context context, final ImageView imageView, Bitmap bitmap, Intent data) {
        Uri selectedImage = null;
        if (bitmap != null) {
            BitmapToByteArrayTask task = new BitmapToByteArrayTask(null, bitmap) {
                @Override
                protected byte[] doInBackground(Void... params) {
                    return super.doInBackground(params);
                }

                @Override
                protected void onPostExecute(byte[] bytes) {
                    super.onPostExecute(bytes);
                    ByteArrayToBitmapTask bitmapTask = new ByteArrayToBitmapTask(imageView, bytes,
                            imageView.getLayoutParams().width, imageView.getLayoutParams().height);
                    bitmapTask.execute();
                }
            };
            task.execute();
            selectedImage = Utility.getImageUri(context, bitmap);
        } else if (data != null) {
            selectedImage = data.getData();
        }
        imageView.setImageURI(selectedImage);
    }

    public static boolean isNameValid(Activity activity, EditText first, EditText last) {
        String fnm = first.getText().toString().trim();
        String lnm = last.getText().toString().trim();
        boolean valid = true;
        View focusView = null;

        if (fnm.length() < 2 && lnm.length() < 2) {
            first.setError(activity.getString(R.string.error_proper_name));
            focusView = first;
            valid = false;
        }

        int msg = isNameValid(lnm);
        if (msg != -1 && msg != R.string.error_name_min_length) {
            last.setError(activity.getString(msg));
            focusView = last;
            valid = false;
        }
        msg = isNameValid(fnm);
        if (msg != -1 && msg != R.string.error_name_min_length) {
            first.setError(activity.getString(msg));
            focusView = first;
            valid = false;
        }

        if (focusView != null) {
            focusView.requestFocus();
        }
        return valid;
    }

    public static int isNameValid(String name) {
        if (TextUtils.isEmpty(name)) {
            return R.string.error_field_required;
        }
        char[] carray = name.toCharArray();
        for (char c : carray) {
            if (!Character.isLetter(c) && !Character.isSpaceChar(c)) {
                return R.string.error_only_alpha;
            }
        }
        if (name.length() < 2) {
            return R.string.error_name_min_length;
        }
        return -1;
    }

    private Drawable resize(Activity activity, Drawable image) {
        Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap,
                (int) (bitmap.getWidth() * 0.5), (int) (bitmap.getHeight() * 0.5), false);
        return new BitmapDrawable(activity.getResources(), bitmapResized);
    }

    ///////////////////////////////////////////////////Un Used Methods /////////////////////

/*

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




    public static AlertDialog openSpecDialog(final Activity activity, final Spinner speciality) {
        final EditText txtSpec = new EditText(activity);
        txtSpec.setHint(activity.getString(R.string.speciality));

        final ArrayList<String> specs = new ArrayList<>();
        SpinnerAdapter adapter = speciality.getAdapter();

        if(adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                specs.add(adapter.getItem(i).toString());
            }
        }

    return new AlertDialog.Builder(activity)
            .setTitle(activity.getString(R.string.add_new_spec))
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




    public static int getAge(String dob, String sep) {

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

    */
}
