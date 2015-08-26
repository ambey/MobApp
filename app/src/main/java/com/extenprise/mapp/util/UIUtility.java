package com.extenprise.mapp.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ambey on 23/7/15.
 */
public abstract class UIUtility {
    /**
     * Shows the progress UI and hides the login form.
     */
    public static void showProgress(Activity activity, final View formView, final View progressView, final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = activity.getResources().getInteger(android.R.integer.config_shortAnimTime);

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

    public static boolean showAlert(Activity mActivity, String title, String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(mActivity).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
        return true;
    }

    public static int getMinutes (String time) {
        String[] timeStr = time.split(":");
        return (Integer.parseInt(timeStr[0]) * 60 + Integer.parseInt(timeStr[1]));
    }

    public static String getTimeString (int minutes) {
        return String.format("%02d:%02d", minutes / 60, minutes % 60);
        //return minutes / 60 + ":" + minutes % 60;
    }

    public static boolean findDocAvailability(String weekOff, Calendar calendar) {
        //Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int weekOffDay = 1; // for sunday
        boolean flag = false;

        /*switch (day) {
            case Calendar.SUNDAY:
                // ...

            case Calendar.MONDAY:
                // etc ...
        }*/

        if(weekOff.equalsIgnoreCase("Monday")) {
            weekOffDay = 2;
        } else if(weekOff.equalsIgnoreCase("Tuesday")) {
            weekOffDay = 3;
        } else if(weekOff.equalsIgnoreCase("Wednesday")) {
            weekOffDay = 4;
        } else if(weekOff.equalsIgnoreCase("Thursday")) {
            weekOffDay = 5;
        } else if(weekOff.equalsIgnoreCase("Friday")) {
            weekOffDay = 6;
        } else if(weekOff.equalsIgnoreCase("Saturday")) {
            weekOffDay = 7;
        }

        if(weekOffDay != day) {
            flag = true;
        }
        return flag;
    }

    public static int getAge(String dob) {
        long ageInMillis = new Date().getTime() - getMinutes(dob);
        Date age = new Date(ageInMillis);
        return age.getYear();
    }
}
