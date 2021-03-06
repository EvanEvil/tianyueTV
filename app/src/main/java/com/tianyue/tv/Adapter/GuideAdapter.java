package com.tianyue.tv.Adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by hasee on 2016/12/23.
 */
public class GuideAdapter extends PagerAdapter {

    private List<View> imageLists;

    public GuideAdapter(List<View> imageLists) {
        this.imageLists = imageLists;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView(imageLists.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager) container).addView(imageLists.get(position));
        return imageLists.get(position);
    }

    @Override
    public int getCount() {
        return imageLists.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }
}