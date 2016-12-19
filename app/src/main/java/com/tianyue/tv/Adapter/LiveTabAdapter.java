package com.tianyue.tv.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * TabLayoutAdapter
 * Created by hasee on 2016/11/30.
 */
public class LiveTabAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;
    private List<String> tabTitleList;

    public LiveTabAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> tabTitleList) {
        super(fm);
        this.fragmentList = fragmentList;
        this.tabTitleList = tabTitleList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return tabTitleList == null ? 0 : tabTitleList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitleList.get(position % tabTitleList.size());
    }
}
