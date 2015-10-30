package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.extenprise.mapp.R;
import com.extenprise.mapp.activity.LoginActivity;


public class WelcomeActivity extends Activity {


    TextView textLabel;
    ImageView imgLogo;
    Animation imgAnimation,textAnimation;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getActionBar().setDisplayHomeAsUpEnabled(false);
       getActionBar().hide();
        textLabel = (TextView)findViewById(R.id.textViewlogo);
        imgLogo = (ImageView)findViewById(R.id.imageViewLogo);

        imgAnimation = AnimationUtils.loadAnimation(this, R.anim.text_fade);
        textAnimation = AnimationUtils.loadAnimation(this,R.anim.text_fade);

        /*
        img1 = (ImageView)findViewById(R.id.img1);
       imgRotation = AnimationUtils.loadAnimation(this, R.anim.img_rotate);
        imgRotation.setRepeatCount(Animation.INFINITE);
        img1.startAnimation(imgRotation);*/
/* start Animation */

        imgLogo.startAnimation(imgAnimation);
        textLabel.startAnimation(textAnimation);

      mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        }, 3800);






    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome_acitivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
