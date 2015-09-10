package com.extenprise.mapp.service.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by ambey on 10/9/15.
 */
public class ServProvsignUpPagerAdapter extends FragmentPagerAdapter {

    private Fragment[] fragments;

    public ServProvsignUpPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new Fragment[2];
        fragments[0] = new ServProvSignUpFragment();
        fragments[1] = new ServProvWorkPlaceFragment();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = fragments[0];
                break;
            case 1:
                fragment = fragments[1];
                break;

        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
