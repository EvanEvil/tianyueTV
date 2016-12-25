package com.tianyue.tv.Fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianyue.tv.Activity.Discovery.TotalStationSearchActivity;
import com.tianyue.tv.R;
import com.tianyue.tv.Util.ConstantUtil;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by hasee on 2016/8/12.
 *
 */
public class DiscoveryFragment extends BaseFragment {

    @BindView(R.id.tags_layout)
    TagFlowLayout mTagFlowLayout;   //热门标签

    @BindView(R.id.hide_scroll_view)
    NestedScrollView mScrollView;

    @BindView(R.id.hide_tags_layout)
    TagFlowLayout mHideTagLayout;

    @BindView(R.id.more_layout) //查看更多根布局
    LinearLayout mMoreLayout;

    @BindView(R.id.tv_more)
    TextView mMoreText; //查看更多
    private boolean isShowMore = true;

    private List<String> hotSearchTags = new ArrayList<>();
    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.discovery_home, container,false);
        return view;
    }
    /**
     * 初始化 发现 页面控件
     *
     */
    @Override
    protected void init() {

        getDiscoveryData();
    }

    @Override
    public void finishCreateView(Bundle state)
    {

        mScrollView.setNestedScrollingEnabled(true);
        getTags();
    }


    /**
     *  获取热门标签
     */
    private void getTags() {
        //getTagFromServer(); 后期实现
        hotSearchTags.add("苍井空");
        hotSearchTags.add("匠人直播");
        hotSearchTags.add("天越网");
        hotSearchTags.add("lol");
        hotSearchTags.add("特朗普");
        hotSearchTags.add("小帝帝的男主播");
        hotSearchTags.add("小帝帝开车");
        hotSearchTags.add("小帝帝城会玩");
        hotSearchTags.add("杨幂");
        hotSearchTags.add("赵丽颖");
        hotSearchTags.add("科比");
        hotSearchTags.add("哈登");
        hotSearchTags.add("林俊杰");
        initTagLayout();
    }

    private void initTagLayout() {
        //获取热搜标签集合前9个默认显示
        List<String> frontTags = hotSearchTags.subList(0, 8);
        mTagFlowLayout.setAdapter(new TagAdapter<String>(frontTags) {

            @Override
            public View getView(FlowLayout parent, int position, String tag) {
                final TextView mTags = (TextView) LayoutInflater.from(getActivity())
                        .inflate(R.layout.layout_tags_item, parent, false);
                mTags.setText(tag);

                        mTags.setOnClickListener(v -> lunchActivity(tag));


                return mTags;
            }
        });
        mHideTagLayout.setAdapter(new TagAdapter<String>(hotSearchTags) {

            @Override
            public View getView(FlowLayout parent, int position, String tag) {
                final TextView mTag = (TextView) LayoutInflater.from(getActivity())
                        .inflate(R.layout.layout_tags_item, parent, false);
                mTag.setText(tag);
                mTag.setOnClickListener(v -> lunchActivity(tag));
                return mTag;
            }
        });

    }

    /**
     * 热门标签搜索的点击跳转
     * @param tag   搜索的标签
     */
    private void lunchActivity(String tag) {
        Intent intent = new Intent(getActivity(),TotalStationSearchActivity.class);
        intent.putExtra(ConstantUtil.TAG,tag);
        startActivity(intent);
    }

    @OnClick(R.id.more_layout)
    void showAndHideMoreLayout(){
        if (isShowMore)
        {
            isShowMore = false;
            mScrollView.setVisibility(View.VISIBLE);
            mMoreText.setText("收起");
            mTagFlowLayout.setVisibility(View.GONE);
            Drawable upDrawable = getResources().getDrawable(R.mipmap.ic_arrow_up_gray_round);
            upDrawable.setBounds(0, 0, upDrawable.getMinimumWidth(), upDrawable.getMinimumHeight());
            mMoreText.setCompoundDrawables(upDrawable, null, null, null);
        } else
        {
            isShowMore = true;
            mScrollView.setVisibility(View.GONE);
            mMoreText.setText("查看更多");
            mTagFlowLayout.setVisibility(View.VISIBLE);
            Drawable downDrawable = getResources().getDrawable(R.mipmap.ic_arrow_down_gray_round);
            downDrawable.setBounds(0, 0, downDrawable.getMinimumWidth(), downDrawable.getMinimumHeight());
            mMoreText.setCompoundDrawables(downDrawable, null, null, null);
        }
    }
    /**
     * 前往搜索界面
     */
    @OnClick(R.id.card_view)
    void startSearchActivity()
    {
        //showToast("尚未开通此功能");
        startActivity(new Intent(getActivity(), TotalStationSearchActivity.class));
    }
    private void getDiscoveryData(){




    }

}
