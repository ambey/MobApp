package com.extenprise.mapp.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.extenprise.mapp.R;
import com.extenprise.mapp.activity.LoginActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by ambey on 23/7/15.
 */
public abstract class Utility {
    /**
     * Shows the progress UI and hides the login form.
     */
    public static void showProgress(Context context, final View formView, final View progressView, final boolean show) {
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
    }

    public static void showRegistrationAlert(final Activity mActivity, String title, String msg) {
        //final boolean flag;
        //final String str = "";
        AlertDialog alertDialog = new AlertDialog.Builder(mActivity).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //Intent intent = new Intent(mActivity, cls);
                        mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                        //return true;
                    }
                });
        //alertDialog.isShowing()

        alertDialog.show();
        //return true;
    }

    public static void showAlert(Activity mActivity, String title, String msg) {
        //final boolean flag;
        //final String str = "";
        AlertDialog alertDialog = new AlertDialog.Builder(mActivity).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //Intent intent = new Intent(mActivity, cls);
                        //mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                        //return true;
                    }
                });
        //alertDialog.isShowing()

        alertDialog.setIcon(R.drawable.med_logo_final);
        alertDialog.show();
        //return true;
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


    /*public static int getAge(String dob) {

        long ageInMillis = new Date().getTime() - getMinutes(dob);
        Date age = new Date(ageInMillis);
        return age.getYear();

    }*/

    public static int getAge(String dob) {

        int year = 0, month = 0, day = 0;
        if (!dob.equals("")) {
            if (dob.contains("/")) {
                String[] dobStr = dob.split("/", 3);
                if (dobStr.length > 1) {
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

    public static void datePicker(View view, final TextView button, final DateChangeListener listener) {
        // Process to get Current Time
        final Calendar c = Calendar.getInstance();
        final int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH);
        final int day = c.get(Calendar.DAY_OF_MONTH);

        // Launch Time Picker Dialog
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
        dpd.show();
    }

    public static void datePicker(View view, final TextView button) {
        datePicker(view, button, null);
    }

    public static String getCommaSepparatedString(String[] arr) {
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; arr != null && i < arr.length; i++) {
            if (arr[i] != null && !arr[i].equals("")) {
                sb.append(arr[i]);
            }
            if (i < arr.length - 1) {
                sb.append(',');
            }
        }
        return sb.toString();
    }

    public static String getDays() {
        return "All Days,Sunday,Monday,Tuesday,Wednesday,Thursday,Friday,Saturday";
    }

    public static String getDaAsString(String sep) {
        Calendar c = Calendar.getInstance();
        return String.format("%02d/%02d/%04d", c.get(Calendar.DAY_OF_MONTH)
                , c.get(Calendar.MONTH) + 1
                , c.get(Calendar.YEAR));
    }

    public static int getSpinnerIndex(Spinner spinner, String str) {
        int index = 0;
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(str)){
                index = i;
                break;
            }
        }
        return index;
    }

    public static void expandOrCollapse(final View v, String exp_or_colpse) {
        TranslateAnimation anim;
        if(exp_or_colpse.equals("expand")) {
            anim = new TranslateAnimation(0.0f, 0.0f, -v.getHeight(), 0.0f);
            v.setVisibility(View.VISIBLE);
        } else {
            anim = new TranslateAnimation(0.0f, 0.0f, 0.0f, -v.getHeight());
            Animation.AnimationListener collapselistener= new Animation.AnimationListener() {
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

    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        if(bitmap == null) {
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
        if(enabled) {
            button.setBackgroundResource(R.drawable.button);
            button.setTextColor(context.getResources().getColor(R.color.ThemeColor));
        } else {
            button.setBackgroundResource(R.drawable.inactive_button);
            button.setTextColor(context.getResources().getColor(R.color.PassiveColor));
        }
    }

    public static void showMessage(Context context, int msgId) {
        Toast.makeText(context, context.getString(msgId), Toast.LENGTH_LONG).show();
        Log.v("Home", "############################" + context.getString(msgId));

    }

    public static void setNewSpec(Context activity, ArrayList<String> specs, Spinner speciality) {
        specs.add("Other");
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(activity, R.layout.layout_spinner, specs);
        speciality.setAdapter(spinnerAdapter);
    }

    public static AlertDialog openSpecDialog(final Activity activity, final Spinner speciality) {
        final EditText txtSpec = new EditText(activity);
        txtSpec.setHint("Add Speciality");

        return new AlertDialog.Builder(activity)
                .setTitle("Add Speciality")
                .setView(txtSpec)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newSpec = txtSpec.getText().toString();
                        ArrayList<String> specs = new ArrayList<String>();
                        specs.add(newSpec);
                        setNewSpec(activity, specs, speciality);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
