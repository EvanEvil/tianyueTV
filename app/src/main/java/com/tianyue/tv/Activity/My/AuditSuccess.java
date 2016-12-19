package com.tianyue.tv.Activity.My;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tianyue.tv.Activity.BaseActivity;
import com.tianyue.tv.Activity.Live.LiveBucket;
import com.tianyue.tv.Activity.Live.LiveSetting;
import com.tianyue.tv.Bean.User;
import com.tianyue.tv.MyApplication;
import com.tianyue.tv.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 审核成功
 * Created by hasee on 2016/11/22.
 */
public class AuditSuccess extends BaseActivity {
    @BindView(R.id.audit_success_toolbar)
    Toolbar toolbar;
    @BindView(R.id.audit_success_button)
    Button startLive;

    @Override
    protected void initView() {
        setContentView(R.layout.audit_success_layout);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @OnClick({R.id.audit_success_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.audit_success_button:
                User user = MyApplication.instance().getUser();
                Integer baudit = user.getBaudit();
                if (baudit == null) {
                    startActivity(LiveSetting.class);
                    return;
                }
                switch (baudit) {
                    case 1:
                        startActivity(LiveBucket.class);
                        break;
                }
                break;
        }
    }
}
