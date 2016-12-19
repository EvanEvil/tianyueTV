package com.tianyue.tv.Util;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.Button;

/**
 * 倒计时工具类
 * Created by hasee on 2016/10/25.
 */
public class TimeCountUtil extends CountDownTimer {
    private Context context;
    private Button btn;
    public TimeCountUtil(Context context, long millisInFuture, long countDownInterval, Button btn){
        super(millisInFuture,countDownInterval);
        this.context = context;
        this.btn = btn;
    }


    @Override
    public void onTick(long millisUntilFinished) {
        btn.setClickable(false);
        btn.setEnabled(false);
        btn.setText(millisUntilFinished/1000+"s重新发送");

    }

    @Override
    public void onFinish() {
        btn.setClickable(true);
        btn.setEnabled(true);
        btn.setText("重新获取验证码");

    }
}
