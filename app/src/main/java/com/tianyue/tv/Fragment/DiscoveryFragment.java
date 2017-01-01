package com.tianyue.tv.Fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianyue.tv.Activity.Discovery.TotalStationSearchActivity;
import com.tianyue.tv.R;
import com.tianyue.tv.Util.ConstantUtil;
import com.tianyue.tv.Util.KeyBoardUtil;
import com.tianyue.tv.Util.LogUtil;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by hasee on 2016/8/12.
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
    @BindView(R.id.search_edit)
    EditText search_edit;   //搜索编辑框

    @BindView(R.id.btn_search_cancle)
    Button btn_search_cancle;   //搜索取消按钮
    @BindView(R.id.ll_hotSearch_tag)
    LinearLayout ll_hotSearch_tag;
    /**
     * 是否显示更多
     */
    private boolean isShowMore = true;

    /**
     * 热门标签
     */
    private List<String> hotSearchTags = new ArrayList<>();
    private List<String> frontTags;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.discovery_home, container, false);
        return view;
    }

    /**
     * 初始化 发现 页面控件
     */
    @Override
    protected void init() {
        //监听editText是否为空,动态修改button文字
        search_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {

                    btn_search_cancle.setText("取消");
                } else {
                    btn_search_cancle.setVisibility(View.VISIBLE);
                    btn_search_cancle.setText("搜索");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                LogUtil.e("输入结束");
            }
        });
        //监听edittext是否获取焦点
        search_edit.setOnFocusChangeListener((v, hasFocus) -> {
            if (search_edit != null) {
                if (hasFocus) {
                    LogUtil.e("获取到了焦点");
                    btn_search_cancle.setVisibility(View.VISIBLE);
                    KeyBoardUtil.openKeybord(search_edit, context);
                } else {
                    btn_search_cancle.setVisibility(View.GONE);
                    KeyBoardUtil.closeKeybord(search_edit, context);
                }
            }

        });
        //软键盘搜索监听
        search_edit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String tag = search_edit.getText().toString().trim();
                if (TextUtils.isEmpty(tag)) {
                    showToast("搜索内容不能为空");
                    return true;
                }
                search_edit.setText("");
                lunchActivity(tag);
            }
            return false;
        });
        //button点击监听  取消：隐藏键盘  搜索：开启搜索的Activity
        btn_search_cancle.setOnClickListener(v -> {
            if ("取消".equals(btn_search_cancle.getText())) {
                KeyBoardUtil.closeKeybord(search_edit, context);
                btn_search_cancle.setVisibility(View.GONE);

            } else {
                search_edit.setText("");
                lunchActivity(search_edit.getText().toString().trim());
            }
        });
        getDiscoveryData();
    }

    @Override
    public void finishCreateView(Bundle state) {

        mScrollView.setNestedScrollingEnabled(true);
        getTags();
    }


    /**
     * 获取热门标签
     */
    private void getTags() {
        //getTagFromServer(); 后期实现
//        hotSearchTags.add("百合");
//        hotSearchTags.add("匠人直播");
//        hotSearchTags.add("天越");
//        hotSearchTags.add("lol");
//        hotSearchTags.add("小啊");
//        hotSearchTags.add("小帝帝的男主播");
//        hotSearchTags.add("小帝帝开车");
//        hotSearchTags.add("小帝帝城会玩");
//        hotSearchTags.add("杨幂");
//        hotSearchTags.add("赵丽颖");
//        hotSearchTags.add("科比");
//        hotSearchTags.add("哈登");
//        hotSearchTags.add("林俊杰");
        initTagLayout();
    }

    /**
     * 初始化流式布局
     */
    private void initTagLayout() {
        //如果集合数据为空，则隐藏流式布局
        if (hotSearchTags.size() == 0) {
            ll_hotSearch_tag.setVisibility(View.GONE);
            return;
        }
        //获取热搜标签集合前8个默认显示
        int defaultShow = 8;
        if (hotSearchTags.size() < 8) {
            defaultShow = hotSearchTags.size();
            frontTags = hotSearchTags.subList(0, defaultShow);
            mMoreLayout.setVisibility(View.GONE);
        } else {

            frontTags = hotSearchTags.subList(0, defaultShow);
            mMoreLayout.setVisibility(View.VISIBLE);
        }
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
     *
     * @param tag 搜索的标签
     */
    private void lunchActivity(String tag) {
        Intent intent = new Intent(getActivity(), TotalStationSearchActivity.class);
        intent.putExtra(ConstantUtil.TAG, tag);
        startActivity(intent);
    }

    @OnClick(R.id.more_layout)
    void showAndHideMoreLayout() {
        if (isShowMore) {
            isShowMore = false;
            mScrollView.setVisibility(View.VISIBLE);
            mMoreText.setText("收起");
            mTagFlowLayout.setVisibility(View.GONE);
            Drawable upDrawable = getResources().getDrawable(R.mipmap.ic_arrow_up_gray_round);
            upDrawable.setBounds(0, 0, upDrawable.getMinimumWidth(), upDrawable.getMinimumHeight());
            mMoreText.setCompoundDrawables(upDrawable, null, null, null);
        } else {
            isShowMore = true;
            mScrollView.setVisibility(View.GONE);
            mMoreText.setText("查看更多");
            mTagFlowLayout.setVisibility(View.VISIBLE);
            Drawable downDrawable = getResources().getDrawable(R.mipmap.ic_arrow_down_gray_round);
            downDrawable.setBounds(0, 0, downDrawable.getMinimumWidth(), downDrawable.getMinimumHeight());
            mMoreText.setCompoundDrawables(downDrawable, null, null, null);
        }
    }

    private void getDiscoveryData() {


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtil.e("onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.e("onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtil.e("onDetach");
    }
}
