package com.tianyue.tv.Activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.tianyue.tv.R;

import butterknife.BindView;

/**
 * Created by hasee on 2016/8/16.
 */
public class ClassifyActivity extends BaseActivity {
    @BindView(R.id.live_classify_toolbar)
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    protected void initView(){
        setContentView(R.layout.live_classify_layout);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
