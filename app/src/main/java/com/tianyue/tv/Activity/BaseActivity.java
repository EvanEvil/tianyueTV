package com.tianyue.tv.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tianyue.mylibrary.base.BaseFrameAty;
import com.tianyue.mylibrary.util.StatusBarUtil;
import com.tianyue.tv.Bean.User;
import com.tianyue.tv.Config.InterfaceUrl;
import com.tianyue.tv.MyApplication;
import com.tianyue.tv.R;
import com.tianyue.tv.Util.AppManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * activity 基类
 */
public abstract class BaseActivity extends BaseFrameAty {
    /**
     * 当前activity
     */
    protected Activity activity;
    /**
     * 当前上下文对象
     */
    protected Context context;
    /**
     * 当前类名称
     */
    protected String TAG = this.getClass().getSimpleName();
    /**
     * activity 是否属于显示状态
     */
    protected boolean isShow = false;
    /**
     * toast 对象
     */
    private Toast toast = null;
    /**
     * 第一次显示消息时间
     */
    private long oneTime = 0;
    /**
     * 第二次显示消息时间
     */
    private long twoTime = 0;
    /**
     * 消息内容
     */
    private String msg;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getBaseContext();
        activity = this;
        if (isKeepScreenNo()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        if (isDefaultPort()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        AppManager.getAppManager().addActivity(this);
        initView();
        init();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        unbinder = ButterKnife.bind(this);
        if (openStatus()) {
            setStatuBar();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isShow = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isShow = false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        System.gc();
    }

    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
        } catch (Exception e) {

        }
        /**
         * 按下后退键默认关闭当前activity
         */
        if (isDefaultBackFinishAct()) {
            AppManager.getAppManager().finishActivity(this);
        }
    }

    /**
     * 是否保持常亮
     *
     * @return
     */
    protected boolean isKeepScreenNo() {
        return false;
    }

    /**
     * 默认改变状态栏
     *
     * @return
     */
    protected boolean openStatus() {
        return false;
    }

    /**
     * 是否开启动画
     *
     * @return
     */
    protected boolean isHasAnimiation() {
        return true;
    }

    /**
     * 是否默认竖屏
     *
     * @return
     */
    protected boolean isDefaultPort() {
        return true;
    }
    /**
     * 是否默认后退关闭界面
     *
     * @return
     */
    protected boolean isDefaultBackFinishAct() {
        return true;
    }

    /**
     * 设置状态栏背景色
     */
    protected void setStatuBar() {
        StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.white));
    }

    /**
     * 锁定字体大小
     *
     * @return
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    /**
     * 启动页面
     *
     * @param className
     */
    protected void startActivity(Class<?> className) {
        Intent intent = new Intent(this, className);
        startActivity(intent);
        if (isHasAnimiation()) {
            overridePendingTransition(R.anim.slide_right_in,
                    R.anim.slide_left_out);
        }
    }

    /**
     * 启动页面并传递数据
     *
     * @param className
     * @param bundle    数据
     */
    protected void startActivity(Class<?> className, Bundle bundle) {
        Intent intent = new Intent(this, className);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        if (isHasAnimiation()) {
            overridePendingTransition(R.anim.slide_right_in,
                    R.anim.slide_left_out);

        }
    }

    /**
     * 第一次审核认证通过更新一次用户信息
     */
    protected void updateUser() {
        OkHttpUtils.post()
                .url(InterfaceUrl.GET_USER_INFO)
                .addParams("uId", MyApplication.instance().getUser().getId())
                .build().execute(new Callback() {
            @Override
            public Object parseNetworkResponse(Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                User user = gson.fromJson(result,User.class);
                MyApplication.instance().setUser(user);
                return null;
            }

            @Override
            public void onError(Request request, Exception e) {
                showToast("更新用户信息失败");
            }

            @Override
            public void onResponse(Object response) {

            }
        });

    }
    /**
     * 初始化对象
     */
    protected void init() {

    }

    /**
     * 关闭界面
     */
    @Override
    public void finish() {
        super.finish();
        if (isHasAnimiation()) {
            overridePendingTransition(R.anim.slide_left_in,
                    R.anim.slide_right_out);
        }
    }

    /**
     * 单例显示消息
     *
     * @param message
     */
    protected void showToast(final String message) {
        runOnUiThread(() -> {
            if (toast == null) {
                toast = new Toast(context);
                toast = toast.makeText(activity, message, Toast.LENGTH_SHORT);
                //设置Toast消息字体大小
                LinearLayout linearLayout = (LinearLayout) toast.getView();
                TextView messageView = (TextView) linearLayout.getChildAt(0);
                messageView.setTextSize(13);
                toast.show();
                //记录下第一次显示message的时间
                oneTime = System.currentTimeMillis();
                msg = message;
            } else {
                //记录下第二次显示message的时间
                twoTime = System.currentTimeMillis();
                if (message.equals(msg)) {
                    if (twoTime - oneTime >= Toast.LENGTH_SHORT) {
                        toast.show();
                    }
                } else {
                    msg = message;
                    toast.setText(message);
                    toast.show();
                }
                oneTime = twoTime;
            }
        });
    }


    /**
     * 初始化控件
     */
    protected abstract void initView();


}
