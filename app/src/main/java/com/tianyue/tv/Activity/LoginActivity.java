package com.tianyue.tv.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tianyue.mylibrary.view.LoadingView;
import com.tianyue.tv.Config.InterfaceUrl;
import com.tianyue.tv.Config.ParamConfigKey;
import com.tianyue.tv.Config.RequestConfigKey;
import com.tianyue.tv.Gson.LoginGson;
import com.tianyue.tv.MyApplication;
import com.tianyue.tv.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;
import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * 登录页面
 * Created by hasee on 2016/8/2.
 */
public class LoginActivity extends BaseActivity {
    @BindView(R.id.login)
    Button login;
    @BindView(R.id.login_user_account)
    EditText userEdit;
    @BindView(R.id.login_user_password)
    EditText pwdEdit;
    @BindView(R.id.login_register)
    Button register;
    @BindView(R.id.login_forget_password)
    TextView forgetPwd;
    /**
     * 记住账号
     */

    @BindView(R.id.login_remember_account)
    CheckBox remember;
    MyApplication mApplication;
    /**
     * 初始化控件
     */
    @Override
    protected void initView() {
//        StatusBarUtil.setTransparentStatuBar(this);
        setContentView(R.layout.login_layout);
    }

    @Override
    protected boolean isHasAnimiation() {
        return false;
    }

    @OnCheckedChanged(R.id.login_remember_account)
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    @OnClick({R.id.login, R.id.login_register, R.id.login_forget_password})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                String userName = userEdit.getText().toString();
                String pwd = pwdEdit.getText().toString();
                if (!isNull(userName, pwd)) {
                    showDialogs("登录中");
                    checkLogin();
                }
                break;
            case R.id.login_register:
                startActivity(RegisterOne.class);
                break;
            case R.id.login_forget_password:
                startActivity(ForgetOne.class);
                break;
        }
    }


    /**
     * 用户名和密码是否为空
     */
    private boolean isNull(String userName, String pwd) {
        if (userName == null || "".equals(userName)) {
            showToast("用户名不能为空");
            return true;
        } else if (pwd == null || "".equals(pwd)) {
            showToast("密码不能为空");
            return true;
        }
        return false;
    }

    /**
     * 检查登录账号是否正确
     */
    private void checkLogin() {
        OkHttpUtils.post().url(InterfaceUrl.MOBILE_LOGIN)
                .addParams(ParamConfigKey.USER_NAME, userEdit.getText().toString()
                ).addParams(ParamConfigKey.PASSWORD, pwdEdit.getText().toString())
                .build().execute(new Callback() {
            @Override
            public Object parseNetworkResponse(Response response) throws IOException {
                String result = response.body().string();
                Log.i(TAG, "parseNetworkResponse: " + result);
                Gson gson = new Gson();
                LoginGson loginGson = gson.fromJson(result, LoginGson.class);
                String status = loginGson.getStatus();
                if (status.equals(RequestConfigKey.REQUEST_SUCCESS)) {
                    mApplication = MyApplication.instance();
                    mApplication.setUser(loginGson.getUser());
                    dismissDialogs();
                    startActivity(HomeActivity.class);
                    Log.i(TAG, "parseNetworkResponse: " + loginGson.getUser().getLive_streaming_address());
                    finish();
                } else {
                    showToast("用户名或密码错误");
                    dismissDialogs();
                }
                return null;
            }

            @Override
            public void onError(Request request, Exception e) {
                showToast("请求失败，检查您的网络是否可用");
                dismissDialogs();
            }

            @Override
            public void onResponse(Object response) {

            }
        });
    }

}
