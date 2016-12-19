package com.tianyue.tv.Activity.My;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.tianyue.tv.Activity.BaseActivity;
import com.tianyue.tv.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 审核失败
 * Created by hasee on 2016/11/22.
 */
public class AuditFailure extends BaseActivity {
    @BindView(R.id.audit_failure_toolbar)
    Toolbar toolbar;
    @BindView(R.id.audit_failure_button)
    Button back;

    @Override
    protected void initView() {
        setContentView(R.layout.audit_failure_layout);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick(R.id.audit_failure_button)
    public void onClick() {
        finish();
    }
}
