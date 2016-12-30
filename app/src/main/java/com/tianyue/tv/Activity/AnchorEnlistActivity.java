package com.tianyue.tv.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tianyue.mylibrary.view.ChangeToolbar;
import com.tianyue.tv.Activity.Live.LiveBucket;
import com.tianyue.tv.Activity.Live.LiveSetting;
import com.tianyue.tv.Activity.My.CertificationOne;
import com.tianyue.tv.Bean.User;
import com.tianyue.tv.Config.AuditStateConfig;
import com.tianyue.tv.MyApplication;
import com.tianyue.tv.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 主播招募
 * Created by hasee on 2016/12/30.
 */
public class AnchorEnlistActivity extends BaseActivity {
    @BindView(R.id.anchor_enlist_toolbar)
    ChangeToolbar toolbar;
    @BindView(R.id.anchor_enlist_button)
    Button confirm;

    User user;

    @Override
    protected void initView() {
        setContentView(R.layout.anchor_enlist_layout);
        toolbar.setNavigationOnClickListener(v -> finish());
        user = MyApplication.instance().getUser();
    }

    @OnClick(R.id.anchor_enlist_button)
    public void onClick() {
        Integer bCard = user.getbCard();//审核状态
        if (bCard == null) {
            startActivity(CertificationOne.class);
            finish();
            return;
        }
        switch (bCard) {
            case AuditStateConfig.AUDIT_SUCCESS:
                Integer baudit = user.getBaudit();
                if (baudit == null) {
                    startActivity(LiveSetting.class);
                    finish();
                    return;
                }
                switch (baudit) {
                    case 1:
                        startActivity(LiveBucket.class);
                        finish();
                        break;
                }
                break;
        }
    }
}
