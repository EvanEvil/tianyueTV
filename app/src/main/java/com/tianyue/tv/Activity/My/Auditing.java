package com.tianyue.tv.Activity.My;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.tianyue.tv.Activity.BaseActivity;
import com.tianyue.tv.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 审核中
 * Created by hasee on 2016/11/22.
 */
public class Auditing extends BaseActivity {
    @BindView(R.id.auditing_toolbar)
    Toolbar toolbar;
    @BindView(R.id.auditing_button)
    Button back;

    @Override
    protected void initView() {
        setContentView(R.layout.auditing_layout);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @OnClick(R.id.auditing_button)
    public void onClick(View view) {
        finish();
    }
}
