package com.tianyue.tv.Activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tianyue.tv.CustomView.XCFlowLayout;
import com.tianyue.tv.R;

import static android.R.attr.x;

/**
 * Created by hasee on 2016/8/8.
 */
public class SearchActivity extends BaseActivity {
    //evan
    private String mNames[] = {
            "welcome","android","TextView",
            "apple","jamy","kobe bryant",
            "jordan","layout","viewgroup",
            "margin","padding","text",
            "name","type","search","logcat"
    };

    private XCFlowLayout hot_search_flowlayout;
    private XCFlowLayout history_search_flowlayout;


    /**
     * 初始化布局
     */
    @Override
    protected void initView() {
        setContentView(R.layout.search_item_layout);
        hot_search_flowlayout = (XCFlowLayout) findViewById(R.id.flowlayout);
        history_search_flowlayout = (XCFlowLayout) findViewById(R.id.history_search_flowlayout);


    }

    /**
     * 初始化数据，根据搜索结果，进行显示
     */
    @Override
    protected void init() {
        super.init();

        initChildViews(hot_search_flowlayout);//热搜
        initChildViews(history_search_flowlayout);//历史记录

    }

    private void initChildViews(XCFlowLayout flowLayout) {
        // TODO Auto-generated method stub



        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = 5;
        lp.rightMargin = 5;
        lp.topMargin = 5;
        lp.bottomMargin = 5;
        for(int i = 0; i < mNames.length; i ++){
            TextView view = new TextView(this);
            view.setText(mNames[i]);
            view.setTextColor(Color.WHITE);
            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_hotsearch));


            flowLayout.addView(view,lp);
        }
    }

}