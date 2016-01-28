package com.extenprise.mapp.medico.service.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.extenprise.mapp.medico.ui.TitleFragment;

import java.util.List;

public class ServProvSignUpPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragments;

    public ServProvSignUpPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
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
