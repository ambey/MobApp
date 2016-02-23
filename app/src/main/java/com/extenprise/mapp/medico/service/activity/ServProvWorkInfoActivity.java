package com.extenprise.mapp.medico.service.activity;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.service.ui.ServProvSignUpPagerAdapter;
import com.extenprise.mapp.medico.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ServProvWorkInfoActivity extends FragmentActivity {

    private ServProvSignUpPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_pager);

        int listSize = 0;
        try {
            Intent intent = getIntent();
            //ArrayList<WorkPlace> mWorkPlaceList = intent.getParcelableArrayListExtra("wplist");
            listSize = intent.getIntExtra("wpListSize", 0);
            if (listSize <= 0) {
                Utility.showMessage(this, R.string.error_no_workplace_found);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            listSize = 2;
        }

        List<Fragment> fragments = new ArrayList<>();
        Bundle fragmentBundle = new Bundle();
        for (int i = 0; i < listSize; i++) {
            fragments.add(Fragment.instantiate(this, ServProvWPListFragment.class.getName(), fragmentBundle));
        }

        mPagerAdapter = new ServProvSignUpPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager = (ViewPager) findViewById(R.id.signUpViewPager);
        mViewPager.setAdapter(mPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                ActionBar actionBar = getActionBar();
                if (actionBar != null) {
                    actionBar.setSelectedNavigationItem(position);
                }
            }
        });

        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            ActionBar.TabListener tabListener = new ActionBar.TabListener() {
                @Override
                public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                    mViewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

                }

                @Override
                public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

                }
            };

            for (int i = 0; i < listSize; i++) {
                actionBar.addTab(actionBar.newTab().setText(R.string.wp + " " + i).setTabListener(tabListener));
            }
        }
    }

    public boolean isValidInput() {
        List<Fragment> fragments = mPagerAdapter.getFragments();
        ServProvWPListFragment f2 = (ServProvWPListFragment) fragments.get(0);
        return f2.isValidInput();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    public void saveData() {
        ServProvWPListFragment fragment2 = (ServProvWPListFragment) mPagerAdapter.getItem(0);
        fragment2.saveData();
    }

    public void showtimeFields(View view) {
        ServProvWPListFragment fragment = (ServProvWPListFragment) mPagerAdapter.getItem(0);
        fragment.showtimeFields(view);
    }

    public void showWorkFields(View view) {
        ServProvWPListFragment fragment = (ServProvWPListFragment) mPagerAdapter.getItem(0);
        fragment.showWorkFields(view);
    }

    public void showStartTimePicker(View view) {
        ServProvWPListFragment fragment = (ServProvWPListFragment) mPagerAdapter.getItem(0);
        fragment.showStartTimePicker(view);
    }

    public void showEndTimePicker(View view) {
        ServProvWPListFragment fragment = (ServProvWPListFragment) mPagerAdapter.getItem(0);
        fragment.showEndTimePicker(view);
    }

    public void addNewWorkPlace(View view) {
        ServProvWPListFragment fragment = (ServProvWPListFragment) mPagerAdapter.getItem(0);
        fragment.addNewWorkPlace(view);
    }

    public void registerDone(View view) {
        ServProvWPListFragment fragment = (ServProvWPListFragment) mPagerAdapter.getItem(0);
        fragment.registerDone(view);
    }

    @Override
    public void onBackPressed() {
        ServProvWPListFragment fragment = (ServProvWPListFragment) mPagerAdapter.getItem(0);
        fragment.onBackPressed();
        super.onBackPressed();
    }
}

