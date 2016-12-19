package com.tianyue.tv.Activity.My;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tianyue.mylibrary.view.LoadingView;
import com.tianyue.tv.Activity.BaseActivity;
import com.tianyue.tv.Bean.User;
import com.tianyue.tv.Config.InterfaceUrl;
import com.tianyue.tv.Config.ParamConfigKey;
import com.tianyue.tv.Config.RequestConfigKey;
import com.tianyue.tv.Gson.LoginGson;
import com.tianyue.tv.MyApplication;
import com.tianyue.tv.R;
import com.tianyue.tv.Util.TimeCountUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 账号安全
 * Created by hasee on 2016/11/4.
 */
public class AccountSecurity extends BaseActivity {
    /**
     * 当前登录账号
     */
    @BindView(R.id.account_security_nowaccount)
    TextView nowAccount;
    /**
     * 获取验证码
     */
    @BindView(R.id.account_security_getCode)
    Button getCode;
    /**
     * 输入验证码
     */
    @BindView(R.id.account_security_code)
    EditText inputCode;
    /**
     * 密码
     */
    @BindView(R.id.account_security_password)
    EditText password;
    /**
     * 重复密码
     */
    @BindView(R.id.account_security_repassword)
    EditText rePassword;

    /**
     * 确认修改
     */
    @BindView(R.id.account_security_finish)
    Button confirm;

    String userTelephone;
    /**
     * 验证码
     */
    String code;
    @BindView(R.id.account_security_toolbar)
    Toolbar toolbar;

    LoadingView loadingDialog;

    /**
     * 初始化控件
     */
    @Override
    protected void initView() {
        setContentView(R.layout.my_account_security_layou);
        initData();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        User user = MyApplication.instance().getUser();
        if (user != null) {
            userTelephone = user.getTelephone();
            String number = userTelephone.substring(0, 3) + "****" + userTelephone.substring(7, userTelephone.length());
            nowAccount.setText(number);
            TextPaint paint = nowAccount.getPaint();
            paint.setFakeBoldText(true);
        }
    }

    @OnClick({R.id.account_security_getCode, R.id.account_security_finish})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.account_security_getCode:
                requestCode();
                break;
            case R.id.account_security_finish:
                String pwd = password.getText().toString();
                String rePwd = rePassword.getText().toString();
                String code = inputCode.getText().toString();
                if (isNull(code, pwd, rePwd)) {
                    return;
                }
                if (pwd.equals(rePwd)) {
                    loadingDialog = new LoadingView(this);
                    loadingDialog.setTouchOutside(false);
                    loadingDialog.setLoadingViewText("修改中");
                    loadingDialog.show();
                    confirmPassword(code, pwd, rePwd);
                } else {
                    showToast("两次密码不一致");
                }
                break;
        }

    }

    /**
     * 是否为空
     *
     * @return
     */
    public boolean isNull(String code, String pwd, String rePwd) {
        if (code.equals("") || code == null) {
            showToast("验证码不能为空");
            return true;
        }
        if (pwd.equals("") || pwd == null) {
            showToast("密码不能为空");
            return true;
        }
        if (rePwd.equals("") || rePwd == null) {
            showToast("密码不能为空");
            return true;
        }
        return false;
    }

    /**
     * 关闭加载界面
     */
    private void dismissLoadingView() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    /**
     * 请求获取验证码
     */
    private void requestCode() {
        OkHttpUtils.post().url(InterfaceUrl.ALTER_PWD_CODE)
                .addParams(ParamConfigKey.TELEPHONE, userTelephone)
                .build().execute(new Callback() {
            @Override
            public Object parseNetworkResponse(Response response) throws IOException {
                String result = response.body().string();
                try {
                    JSONObject object = new JSONObject(result);
                    String ret = object.optString(RequestConfigKey.RET);
                    if (ret != null) {
                        if (ret.equals(RequestConfigKey.REQUEST_SUCCESS)) {
                            code = object.optString(ParamConfigKey.P_CODE);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //验证码再次发送倒计时
                                    TimeCountUtil timeCountUtil = new TimeCountUtil(AccountSecurity.this, 60 * 1000, 1000, getCode);
                                    timeCountUtil.start();
                                }
                            });
                        } else if (ret.equals(RequestConfigKey.REQUEST_ERROR)) {
                            showToast("手机号不存在");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onError(Request request, Exception e) {
                showToast("获取验证码失败");
            }

            @Override
            public void onResponse(Object response) {

            }
        });
    }

    /**
     * 确认修改密码
     */
    private void confirmPassword(String code, String pwd, String rePwd) {
        OkHttpUtils.post().url(InterfaceUrl.ALTER_PWD)
                .addParams(ParamConfigKey.TELEPHONE, userTelephone)
                .addParams(ParamConfigKey.CODE, code)
                .addParams(ParamConfigKey.PASSWORD, pwd)
                .addParams(ParamConfigKey.RE_PASSWORD, rePwd)
                .build().execute(new Callback() {
            @Override
            public Object parseNetworkResponse(Response response) throws IOException {
                String result = response.body().string();
                try {
                    JSONObject object = new JSONObject(result);
                    String ret = object.optString(RequestConfigKey.RET);
                    if (ret != null) {
                        if (ret.equals(RequestConfigKey.REQUEST_SUCCESS)) {
                            dismissLoadingView();
                            showToast("修改密码成功");
                        } else if (ret.equals("repeat2")) {
                            dismissLoadingView();
                            showToast("验证码错误");
                        } else if (ret.equals("repeat1")) {
                            dismissLoadingView();
                            showToast("验证码不存在");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onError(Request request, Exception e) {
                dismissLoadingView();
                showToast("修改密码失败");
            }

            @Override
            public void onResponse(Object response) {

            }
        });

    }

}
