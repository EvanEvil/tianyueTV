package com.tianyue.tv.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.tianyue.mylibrary.util.StatusBarUtil;
import com.tianyue.tv.Adapter.HomePagerAdapter;
import com.tianyue.tv.Fragment.BaseFragment;
import com.tianyue.tv.Fragment.DiscoveryFragment;
import com.tianyue.tv.Fragment.LiveHomeFragment;
import com.tianyue.tv.Fragment.MyFragment;
import com.tianyue.tv.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class HomeActivity extends BaseActivity  {
    @BindView(R.id.home_navigation_bar)
    RadioGroup radioGroup;//
    FragmentManager fragmentManager;
    LiveHomeFragment liveFragment;
    DiscoveryFragment discoveryFragment;
    MyFragment myFragment;
    @BindView(R.id.viewpager_home)
    ViewPager viewpager_home;//首页viewpage
    private List<BaseFragment> fragments = new ArrayList<>();
    private Fragment currentFragment = new Fragment();

    private int currentIndex = 0;

    private final String CURRENT = "fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.white));
        radioGroup.setOnCheckedChangeListener(checkedChangeListener);
        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState != null) {
            currentIndex = savedInstanceState.getInt(CURRENT);

            fragments.clear();
            fragments.add((BaseFragment) fragmentManager.findFragmentByTag(0 + ""));
            fragments.add((BaseFragment) fragmentManager.findFragmentByTag(1 + ""));
            fragments.add((BaseFragment) fragmentManager.findFragmentByTag(2 + ""));

           // restoreFragment();

        } else {
            fragments.add(liveFragment = new LiveHomeFragment());
            fragments.add(discoveryFragment = new DiscoveryFragment());
            fragments.add(myFragment = new MyFragment());
            //showFragment();
        }
        HomePagerAdapter homePagerAdapter = new HomePagerAdapter(fragmentManager,fragments);
        viewpager_home.setAdapter(homePagerAdapter);
        viewpager_home.setCurrentItem(0);
        viewpager_home.addOnPageChangeListener(new MyOnPageChangeListener());

    }
    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            RadioButton rb = (RadioButton) radioGroup.getChildAt(position);
            rb.setChecked(true);
//            BaseFragment baseFragment = fragments.get(position);
//            baseFragment.init();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT, currentIndex);
    }

    @Override
    protected boolean isHasAnimiation() {
        return false;
    }

    @Override
    protected boolean isDefaultBackFinishAct() {
        return false;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        if (myFragment != null) {
            if (myFragment.isAdded()) {
                myFragment.upDateUser();
            }
        }
    }

//    /**
//     * 显示Fragment
//     */
//    private void showFragment() {
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        if (!fragments.get(currentIndex).isAdded()) {
//            transaction
//                    .hide(currentFragment)
//                    .add(R.id.home_fill, fragments.get(currentIndex), currentIndex + "");
//        } else {
//            transaction
//                    .hide(currentFragment)
//                    .show(fragments.get(currentIndex));
//        }
//        currentFragment = fragments.get(currentIndex);
//        transaction.commit();
//    }

//    /**
//     * 恢复Fragment
//     */
//    private void restoreFragment() {
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        for (int i = 0; i < fragments.size(); i++) {
//            if (i == currentIndex) {
//                transaction.show(fragments.get(i));
//            } else {
//                transaction.hide(fragments.get(i));
//            }
//        }
//        transaction.commit();
//        currentFragment = fragments.get(currentIndex);
//    }


    //导航栏监听
    RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (group.getId() == R.id.home_navigation_bar) {
                switch (checkedId) {
                    case R.id.home_rb:
                        currentIndex = 0;
                        break;
                    case R.id.discovery_rb:
                        currentIndex = 1;
                        break;
                    case R.id.my_rb:
                        currentIndex = 2;
                        break;
                    default:
                        break;
                }
            }
            //showFragment();
            viewpager_home.setCurrentItem(currentIndex,true);
        }
    };


}
