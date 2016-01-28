package com.extenprise.mapp.medico.net;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.util.Utility;

/**
 * Created by avinash on 17/11/15.
 */

public class Send {

    public static void message(Activity activity, String phoneNo, String message) {

        //SMS is getting send but its charging sms charges from the user.

        Log.i("Send SMS", "");
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Utility.showMessage(activity, R.string.msg_sms_sent);
        }
        catch (Exception e) {
            Utility.showMessage(activity, R.string.msg_sms_failed);
            e.printStackTrace();
        }
    }

    /*protected void sendSMS() {
        Log.i("Send SMS", "");
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);

        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address"  , new String ("01234"));
        smsIntent.putExtra("sms_body"  , "Test ");

        try {
            startActivity(smsIntent);
            finish();
            Log.i("Finished sending SMS...", "");
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this,
                    "SMS faild, please try again later.", Toast.LENGTH_SHORT).show();
        }
    } */


    public static void email(Activity activity, String subject, String body, String to) {
        Log.i("Send email", "");
        //Intent emailIntent = new Intent(Intent.ACTION_SEND);
        /*if(cc != null) {
            emailIntent.putExtra(Intent.EXTRA_CC, cc);
        }*/

        Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, to);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.setData(Uri.parse("mailto:")); // or just "mailto:" for blank
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        try {
            activity.startActivity(intent);
            Log.i("Finished sending email.", "");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("email Can not be sent.", "");
        }
   }



   public static void call(Activity activity) {
       Log.i("Send call", "");
      Intent in=new Intent(Intent.ACTION_CALL,Uri.parse("0000000000"));
      try{
          activity.startActivity(in);
      }

      catch (android.content.ActivityNotFoundException ex){
         //Toast.makeText(activity,"yourActivity is not founded",Toast.LENGTH_SHORT).show();
          Utility.showMessage(activity, R.string.some_error);
      }
   }
}
