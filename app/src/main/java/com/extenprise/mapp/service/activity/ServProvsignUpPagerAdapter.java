package com.extenprise.mapp.service.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.extenprise.mapp.ui.TitleFragment;

import java.util.List;

/**
 * Created by ambey on 10/9/15.
 */
public class ServProvsignUpPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragments;

    public ServProvsignUpPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        mFragments = fragments;
    }

    public List<Fragment> getFragments() {
        return mFragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        TitleFragment f = (TitleFragment)mFragments.get(position);
        return f.getPageTitle();
    }
}
