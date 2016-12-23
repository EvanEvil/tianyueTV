package com.tianyue.tv.Activity;

import android.support.v4.view.ViewPager;
import android.support.v4.widget.EdgeEffectCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tianyue.mylibrary.view.IndicatorView;
import com.tianyue.tv.Adapter.GuideAdapter;
import com.tianyue.tv.R;
import com.tianyue.tv.Util.SpUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by hasee on 2016/12/23.
 */
public class GuideActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    @BindView(R.id.guide_layout_viewPage)
    ViewPager viewPage;
    @BindView(R.id.guide_layout_start)
    ImageButton start;
    @BindView(R.id.guide_layout_indicator)
    LinearLayout indicator;
    private boolean isFirst;

    private IndicatorView indicatorView;
    private EdgeEffectCompat leftEdge;
    private EdgeEffectCompat rightEdge;
    private List<View> imageViews;
    SpUtil spUtil;

    private int[] pics = {R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};

    @Override
    protected void initView() {
        spUtil = new SpUtil("guide");
        isFirst = (boolean) spUtil.get("guide", true);
        setContentView(R.layout.guide_layout);
        if (isFirst) {
            viewPage.addOnPageChangeListener(this);
            indicatorView = new IndicatorView(this);
            indicatorView.setInterval(20);
            indicatorView.setIndicatorDrawable(getResources().getDrawable(R.drawable.indicator_selector));
            indicatorView.setSelection(0);
            indicator.addView(indicatorView);
            imageViews = new ArrayList<>();
            for (int i = 0; i < pics.length; i++) {
                ImageView image = new ImageView(this);
                image.setScaleType(ImageView.ScaleType.FIT_XY);
                image.setImageDrawable(getResources().getDrawable(pics[i]));
                imageViews.add(image);
            }
            indicatorView.setCount(imageViews.size());
            if (imageViews.size() == 1) {
                start.setVisibility(View.VISIBLE);
            }

            GuideAdapter guideAdapter = new GuideAdapter(imageViews);
            viewPage.setAdapter(guideAdapter);

            /** viewpaer左滑最后一页时进入应用 */
            try {
                Field leftEdgeField = viewPage.getClass().getDeclaredField("mLeftEdge");
                Field rightEdgeField = viewPage.getClass().getDeclaredField("mRightEdge");
                if (leftEdgeField != null && rightEdgeField != null) {
                    leftEdgeField.setAccessible(true);
                    rightEdgeField.setAccessible(true);
                    leftEdge = (EdgeEffectCompat) leftEdgeField.get(viewPage);
                    rightEdge = (EdgeEffectCompat) rightEdgeField.get(viewPage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            startActivity(SplashActivity.class);
            finish();
        }
    }

    private void enterApp() {
        startActivity(LoginActivity.class);
        finish();
    }

    @Override
    protected boolean isHasAnimiation() {
        return false;
    }

    @OnClick(R.id.guide_layout_start)
    public void onClick() {
        startActivity(LoginActivity.class);
        spUtil.put("guide", false);
        finish();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        indicatorView.setSelection(position);
        /** 最后一张 */
        if (position == imageViews.size() - 1) {
            start.setVisibility(View.VISIBLE);
        } else {
            start.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        /** 到了最后一张并且还继续拖动，出现蓝色限制边条了 */
        if (rightEdge != null && !rightEdge.isFinished()) {
            /** 最后一张往右滑进入应用 */
            enterApp();
        }
    }
}
