package com.extenprise.mapp.service.activity;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.extenprise.mapp.R;

import java.util.ArrayList;
import java.util.List;

//import android.support.design.widget.TabLayout;

public class ServProvSignUpActivity extends FragmentActivity {

    private ServProvSignUpPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    //private ImageView mImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_pager);

        List<Fragment> fragments = new ArrayList<>();
        Bundle fragmentBundle = new Bundle();
        fragments.add(Fragment.instantiate(this, ServProvSignUpFragment.class.getName(), fragmentBundle));
        fragments.add(Fragment.instantiate(this, ServProvWorkPlaceFragment.class.getName(), fragmentBundle));

        mPagerAdapter = new ServProvSignUpPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager = (ViewPager) findViewById(R.id.signUpViewPager);
        mViewPager.setAdapter(mPagerAdapter);

        Intent intent = getIntent();
        int category = intent.getIntExtra("category", R.string.practitionar);
        Log.v(this.getClass().getName(), "category: " + getString(category));

        //mImgView = (ImageView) mRootView.findViewById(R.id.uploadimageview);
/*
        TabLayout tabLayout = (TabLayout) findViewById(R.id.slidingTabs);
        tabLayout.setupWithViewPager(mViewPager);
*/

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                ActionBar actionBar = getActionBar();
                if (actionBar != null) {
                    actionBar.setSelectedNavigationItem(position);
                    //actionBar.setTitle(position);//***** Resources$NotFoundException
                }
            }
        });

        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            //actionBar.setTitle("Personal Info");//*****

            ActionBar.TabListener tabListener = new ActionBar.TabListener() {
                @Override
                public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                    mViewPager.setCurrentItem(tab.getPosition());

                    /*if(tab.getPosition() == 1) {
                        tab.setText("Personal Info");
                        //actionBar.setTitle("Personal Info");//*****
                    } else {
                        tab.setText("Work Place Info");
                    }*/
                }

                @Override
                public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

                }

                @Override
                public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

                }
            };

            actionBar.addTab(actionBar.newTab().setText(R.string.personalDetails).setTabListener(tabListener));
            actionBar.addTab(actionBar.newTab().setText(R.string.work_place_details).setTabListener(tabListener));
        }
    }

    public boolean isValidInput() {
        List<Fragment> fragments = mPagerAdapter.getFragments();
        ServProvSignUpFragment f1 = (ServProvSignUpFragment) fragments.get(0);
        ServProvWorkPlaceFragment f2 = (ServProvWorkPlaceFragment) fragments.get(1);
        return (f1.isValidInput(mViewPager) && f2.isValidInput());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return super.onCreateOptionsMenu(menu);
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

    public void saveData() {
        ServProvSignUpFragment fragment = (ServProvSignUpFragment) mPagerAdapter.getItem(0);
        fragment.saveData();
        ServProvWorkPlaceFragment fragment2 = (ServProvWorkPlaceFragment) mPagerAdapter.getItem(1);
        fragment2.saveData();
    }

    public void captureImage(View view) {
        ServProvSignUpFragment fragment = (ServProvSignUpFragment) mPagerAdapter.getItem(0);
        fragment.captureImage(view);
    }

    public void enlargeImg(View view) {
        ServProvSignUpFragment fragment = (ServProvSignUpFragment) mPagerAdapter.getItem(0);
        fragment.enlargeImg(view);
    }


    /*public void showDaysSelectionDialog(View view) {
        ServProvWorkPlaceFragment fragment = (ServProvWorkPlaceFragment) mPagerAdapter.getItem(1);
        fragment.showDaysSelectionDialog(view);
    }*/

    /*@Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        ServProvWorkPlaceFragment fragment = (ServProvWorkPlaceFragment) mPagerAdapter.getItem(1);
        fragment.onPrepareDialog(id, dialog);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        ServProvWorkPlaceFragment fragment = (ServProvWorkPlaceFragment) mPagerAdapter.getItem(1);
        return fragment.onCreateDialog(id);
    }*/

    public void showtimeFields(View view) {
        ServProvWorkPlaceFragment fragment = (ServProvWorkPlaceFragment) mPagerAdapter.getItem(1);
        fragment.showtimeFields(view);
    }

    public void showFeeFields(View view) {
        ServProvWorkPlaceFragment fragment = (ServProvWorkPlaceFragment) mPagerAdapter.getItem(1);
        fragment.showFeeFields(view);
    }

    public void showDaysFields(View view) {
        ServProvWorkPlaceFragment fragment = (ServProvWorkPlaceFragment) mPagerAdapter.getItem(1);
        fragment.showDaysFields(view);
    }

    public void showWorkFields(View view) {
        ServProvWorkPlaceFragment fragment = (ServProvWorkPlaceFragment) mPagerAdapter.getItem(1);
        fragment.showWorkFields(view);
    }

    public void showStartTimePicker(View view) {
        ServProvWorkPlaceFragment fragment = (ServProvWorkPlaceFragment) mPagerAdapter.getItem(1);
        fragment.showStartTimePicker(view);
    }

    public void showEndTimePicker(View view) {
        ServProvWorkPlaceFragment fragment = (ServProvWorkPlaceFragment) mPagerAdapter.getItem(1);
        fragment.showEndTimePicker(view);
    }

    public void addNewWorkPlace(View view) {
        ServProvWorkPlaceFragment fragment = (ServProvWorkPlaceFragment) mPagerAdapter.getItem(1);
        fragment.addNewWorkPlace(view);
    }

    public void registerDone(View view) {
        ServProvWorkPlaceFragment fragment = (ServProvWorkPlaceFragment) mPagerAdapter.getItem(1);
        fragment.registerDone(view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        ServProvSignUpFragment fragment = (ServProvSignUpFragment) mPagerAdapter.getItem(0);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        ServProvSignUpFragment fragment = (ServProvSignUpFragment) mPagerAdapter.getItem(0);
        fragment.onSaveInstanceState(outState);
    }

    /*@Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (requestCode != -1 && (requestCode&0xffff0000) != 0) {
            //throw new IllegalArgumentException("Can only use lower 16 bits for requestCode");
            Utility.showMessage(this, "Can only use lower 16 bits for requestCode");
        }
        super.startActivityForResult(intent, requestCode);
    }*/

    @Override
    public void onBackPressed() {
        ServProvSignUpFragment fragment = (ServProvSignUpFragment) mPagerAdapter.getItem(0);
        fragment.onBackPressed();
        super.onBackPressed();
    }
}
