package com.tianyue.tv.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tianyue.tv.Fragment.BaseFragment;

import java.util.List;

/**
 * Created by Evan on 2016/12/18.
 */

public class HomePagerAdapter extends FragmentPagerAdapter {
    List<BaseFragment> fragments;
    public HomePagerAdapter(FragmentManager fm, List<BaseFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments==null? null:fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments==null? 0:fragments.size();
    }





}
