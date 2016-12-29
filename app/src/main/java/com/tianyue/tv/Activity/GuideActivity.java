package com.tianyue.tv.Activity;

import android.support.v4.view.ViewPager;
import android.support.v4.widget.EdgeEffectCompat;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tianyue.tv.Adapter.GuideAdapter;
import com.tianyue.tv.R;
import com.tianyue.tv.Util.DensityUtils;
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


    private EdgeEffectCompat leftEdge;
    private EdgeEffectCompat rightEdge;
    private List<View> imageViews;
    SpUtil spUtil;
    @BindView(R.id.ll_point_group)
    LinearLayout llPointGroup;// 点的根布局
    @BindView(R.id.view_red_point)
    View viewRedPoint;// 红点
    private int mPointWidth; // 两点之间间距

    private int[] pics = {R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};

    @Override
    protected void initView() {
        spUtil = new SpUtil("guide");
        isFirst = (boolean) spUtil.get("guide", true);
        setContentView(R.layout.guide_layout);
        if (isFirst) {
            viewPage.addOnPageChangeListener(this);

            imageViews = new ArrayList<>();
            for (int i = 0; i < pics.length; i++) {
                ImageView image = new ImageView(this);
                image.setScaleType(ImageView.ScaleType.FIT_XY);
                image.setImageDrawable(getResources().getDrawable(pics[i]));
                imageViews.add(image);

                //初始化小红点
                View point = new View(this);
                point.setBackgroundResource(R.drawable.shape_guide_point_default);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        DensityUtils.dp2px(this, 10), DensityUtils.dp2px(this, 10));
                if (i != 0) {
                    params.leftMargin = DensityUtils.dp2px(this, 10);
                }

                point.setLayoutParams(params);
                //添加小红点到容器中
                llPointGroup.addView(point);
            }

            if (imageViews.size() == 1) {
                start.setVisibility(View.VISIBLE);
            }


            GuideAdapter guideAdapter = new GuideAdapter(imageViews);
            viewPage.setAdapter(guideAdapter);

            viewRedPoint.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            viewRedPoint.getViewTreeObserver()
                                    .removeGlobalOnLayoutListener(this);

                            mPointWidth = llPointGroup.getChildAt(1).getLeft()
                                    - llPointGroup.getChildAt(0).getLeft();


                        }
                    });

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

    /**
     * 页面滑动监听
     *
     * @param position
     * @param positionOffset
     * @param positionOffsetPixels
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //页面滑动的时候将小红点的移动距离设置进去
        int leftMargin = (int) (mPointWidth * (positionOffset + position));
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) viewRedPoint
                .getLayoutParams();
        lp.leftMargin = leftMargin;
        viewRedPoint.setLayoutParams(lp);
    }

    @Override
    public void onPageSelected(int position) {

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
