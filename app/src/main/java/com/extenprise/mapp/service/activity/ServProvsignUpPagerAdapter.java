package com.extenprise.mapp.service.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by ambey on 10/9/15.
 */
public class ServProvsignUpPagerAdapter extends FragmentPagerAdapter {
    public ServProvsignUpPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new ServProvSignUpFragment();
                break;
            case 1:
                fragment = new ServProvWorkPlaceFragment();
                break;

        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
