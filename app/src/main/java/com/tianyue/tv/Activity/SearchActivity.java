package com.tianyue.tv.Activity;


import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.tianyue.mylibrary.util.ColorUtil;
import com.tianyue.tv.CustomView.MyFlowLayout;
import com.tianyue.tv.R;
import com.tianyue.tv.Util.DrawableUtil;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by hasee on 2016/8/8.
 */
public class SearchActivity extends BaseActivity {


    @BindView(R.id.history_search_flowlayout)
    public MyFlowLayout history_search_flowlayout;//历史搜索
    @BindView(R.id.hot_search_flowlayout)
    public MyFlowLayout hot_search_flowlayout;//热门搜索
    private ArrayList<String> nameList;


    /**
     * 初始化布局
     */
    @Override
    protected void initView() {
        setContentView(R.layout.search_item_layout);
        hot_search_flowlayout.setPadding(10, 10, 10, 10);
        hot_search_flowlayout.setHorizontalSpacing(12);
        hot_search_flowlayout.setVerticalSpacing(12);


    }

    /**
     * 初始化数据
     */
    @Override
    protected void init() {
        super.init();
        nameList = new ArrayList<>();
        for(int i=0;i<50; i++){
            nameList.add("热门标签"+i);
        }
        for (int i = 0; i < nameList.size(); i++) {
             final TextView textView = new TextView(context);
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER);

            GradientDrawable normalDrawable = DrawableUtil
                    .generateDrawable(
                            ColorUtil.generateBeautifulColor(), 6);
            GradientDrawable pressedDrawable = DrawableUtil
                    .generateDrawable(Color.parseColor("#aaaaaa"),
                            6);

            textView.setBackgroundDrawable(DrawableUtil
                    .generateSelector(normalDrawable,
                            pressedDrawable));
            textView.setPadding(12, 5, 12, 5);
            textView.setText(nameList.get(i));
            hot_search_flowlayout.addView(textView);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showToast(textView.getText().toString());
                }
            });
        }

    }

}