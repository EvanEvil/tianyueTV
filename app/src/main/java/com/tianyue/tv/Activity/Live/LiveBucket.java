package com.tianyue.tv.Activity.Live;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tianyue.tv.Activity.BaseActivity;
import com.tianyue.tv.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 直播空间
 * Created by hasee on 2016/11/28.
 */
public class LiveBucket extends BaseActivity {
    @BindView(R.id.my_live_bucket_help)
    TextView help;
    @BindView(R.id.my_live_bucket_toolbar)
    Toolbar toolbar;
    @BindView(R.id.my_live_bucket_fans)
    TextView fans;
    @BindView(R.id.my_live_bucket_store)
    TextView store;
    @BindView(R.id.my_live_bucket_money)
    TextView money;
    @BindView(R.id.my_live_bucket_setting)
    TextView setting;
    @BindView(R.id.my_live_bucket_start)
    Button startLive;

    @Override
    protected void initView() {
        setContentView(R.layout.my_live_bucket);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick({R.id.my_live_bucket_help, R.id.my_live_bucket_fans, R.id.my_live_bucket_store, R.id.my_live_bucket_money, R.id.my_live_bucket_setting, R.id.my_live_bucket_start})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_live_bucket_help:
                break;
            case R.id.my_live_bucket_fans:
                break;
            case R.id.my_live_bucket_store:
                break;
            case R.id.my_live_bucket_money:
                break;
            case R.id.my_live_bucket_setting:
                startActivity(LiveSetting.class);
                break;
            case R.id.my_live_bucket_start:
                startActivity(StartLivePort.class);
                break;
        }
    }
}
