package com.extenprise.mapp.medico.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.ViewFlipper;

import com.extenprise.mapp.medico.R;

public class FirstFlipperActivity extends Activity {

    private ViewFlipper viewFlipper;
/*
    private float lastX;
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_flipper);

        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        /*viewFlipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.setInAnimation(this, android.R.anim.slide_in_left);
                viewFlipper.setOutAnimation(this, android.R.anim.slide_out_right);

            }
        });*/
        final GestureDetector gestureDetector;
        gestureDetector = new GestureDetector(new MyGestureDetector());

        viewFlipper.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                return !gestureDetector.onTouchEvent(event);
            }
        });

        /*
         * Find the views declared in main.xml.
         */
        RadioButton RB0 = (RadioButton) findViewById(R.id.radio0);
        RadioButton RB1 = (RadioButton) findViewById(R.id.radio1);


        /*
         * Set a listener that will listen for clicks on the radio buttons and
         * perform suitable actions.
         */
        RB0.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                touch(v);
            }
        });
        RB1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                touch(v);
            }
        });

    }

    private void touch(View v) {
        switch (v.getId()) {
            case R.id.radio0:
                viewFlipper.setDisplayedChild(0);
                viewFlipper.setInAnimation(this, android.R.anim.slide_in_left);
                break;
            case R.id.radio1:
                viewFlipper.setDisplayedChild(1);
                viewFlipper.setOutAnimation(this, android.R.anim.slide_out_right);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
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

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 250;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            System.out.println(" in onFling() :: ");
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                viewFlipper.setInAnimation(getApplicationContext(), R.anim.in_from_right);
                viewFlipper.setInAnimation(getApplicationContext(), R.anim.out_to_left);
                viewFlipper.showNext();
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                viewFlipper.setInAnimation(getApplicationContext(), R.anim.in_from_left);
                viewFlipper.setInAnimation(getApplicationContext(), R.anim.out_to_right);
                viewFlipper.showPrevious();
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }


/*
        public boolean onTouchEvent(MotionEvent touchevent)
        {
            switch (touchevent.getAction())
            {
                // when user first touches the screen to swap
                case MotionEvent.ACTION_DOWN:
                {
                    lastX = touchevent.getX();
                    break;
                }
                case MotionEvent.ACTION_UP:
                {
                    float currentX = touchevent.getX();

                    // if left to right swipe on screen
                    if (lastX < currentX)
                    {
                        // If no more View/Child to flip
                        if (viewFlipper.getDisplayedChild() == 0)
                            break;

                        // set the required Animation type to ViewFlipper
                        // The Next screen will come in form Left and current Screen will go OUT from Right
                        viewFlipper.setInAnimation(getApplicationContext(), R.anim.in_from_left);
                        viewFlipper.setOutAnimation(getApplicationContext(), R.anim.out_to_right);
                        // Show the next Screen
                        viewFlipper.showNext();
                    }

                    // if right to left swipe on screen
                    if (lastX > currentX)
                    {
                        if (viewFlipper.getDisplayedChild() == 1)
                            break;
                        // set the required Animation type to ViewFlipper
                        // The Next screen will come in form Right and current Screen will go OUT from Left
                        viewFlipper.setInAnimation(getApplicationContext(), R.anim.in_from_right);
                        viewFlipper.setOutAnimation(getApplicationContext(), R.anim.out_to_left);
                        // Show The Previous Screen
                        viewFlipper.showPrevious();
                    }
                    break;
                }
            }
            return false;
        }
*/
    }
}
