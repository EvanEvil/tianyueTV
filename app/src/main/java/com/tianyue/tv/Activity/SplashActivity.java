package com.tianyue.tv.Activity;

import android.os.Handler;

import com.tianyue.tv.R;

/**
 * Created by hasee on 2016/12/23.
 */
public class SplashActivity extends BaseActivity {
    @Override
    protected void initView() {
        setContentView(R.layout.splash_layout);
    }

    @Override
    protected void init() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(LoginActivity.class);
                finish();
            }
        }, 2000);
    }

    @Override
    protected boolean isHasAnimiation() {
        return false;
    }
}
