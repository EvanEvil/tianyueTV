package com.tianyue.tv.Fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tianyue.mylibrary.view.ChangeToolbar;
import com.tianyue.tv.Adapter.LiveTabAdapter;
import com.tianyue.tv.Bean.LiveHomeColumn;
import com.tianyue.tv.R;
import com.tianyue.tv.Util.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

/**
 * Created by hasee on 2016/12/6.
 */
public class LiveHomeFragment extends BaseFragment {
    @BindView(R.id.live_home_tab)
    TabLayout tabLayout;
    @BindView(R.id.live_home_viewPage)
    ViewPager viewPage;

    List<Fragment> listFragment;
    @BindView(R.id.home_toolbar)
    ChangeToolbar toolbar;

    LiveFragment liveFragment;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.live_home, null);
        return view;
    }

    @Override
    protected void init() {
        toolbar.setNavigationOnClickListener(v -> {
        });
        String[] tabTitles = getResources().getStringArray(R.array.live_home_tab);
        List<String> lists = Arrays.asList(tabTitles);

        liveFragment = new LiveFragment();

        listFragment = new ArrayList<>();
        listFragment.add(liveFragment);


        int type = 100;
        for (int i = 0; i < 6; i++) {
            LiveOtherColumnFragment fragment = new LiveOtherColumnFragment();
            type = type + 100;
            Bundle bundle = new Bundle();
            bundle.putString("type",type+"");
            fragment.setArguments(bundle);
            listFragment.add(fragment);
            LogUtil.i(type+"");
        }
//        listFragment.add(new LiveOtherColumnFragment());
//        listFragment.add(new LiveOtherColumnFragment());
//        listFragment.add(new LiveOtherColumnFragment());
//        listFragment.add(new LiveOtherColumnFragment());
//        listFragment.add(new LiveOtherColumnFragment());
//        listFragment.add(new LiveOtherColumnFragment());

        liveFragment.setOnColumnMoreListener((position, liveHomeColumns) -> {
            Log.i(TAG, "init: " + liveHomeColumns.get(position).getClassify() + "" + position);
            switch (liveHomeColumns.get(position).getClassify()) {
                case "匠人":
                    viewPage.setCurrentItem(1);
                    break;
                case "衣":
                    viewPage.setCurrentItem(2);
                    break;
                case "食":
                    viewPage.setCurrentItem(3);
                    break;
                case "住":
                    viewPage.setCurrentItem(4);
                    break;
                case "行":
                    viewPage.setCurrentItem(5);
                    break;
                case "知":
                    viewPage.setCurrentItem(6);
                    break;
            }
        });

        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        for (int i = 0; i < lists.size(); i++) {
            tabLayout.addTab(tabLayout.newTab().setText(lists.get(i)));
        }

        LiveTabAdapter tabAdapter = new LiveTabAdapter(getChildFragmentManager(), listFragment, lists);
        viewPage.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPage);
    }

    @Override
    public void finishCreateView(Bundle state) {

    }
}
