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
import com.tianyue.tv.Config.InterfaceUrl;
import com.tianyue.tv.Config.ParamConfigKey;
import com.tianyue.tv.Config.RequestConfigKey;
import com.tianyue.tv.Gson.LoginGson;
import com.tianyue.tv.MyApplication;
import com.tianyue.tv.R;
import com.tianyue.tv.Util.DESUtil;
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
    private String userName;
    private String passWord;

    /**
     * 初始化控件
     */
    @Override
    protected void initView() {
//        StatusBarUtil.setTransparentStatuBar(this);






        jiexi();
        setContentView(R.layout.login_layout);
    }
    public void jiexi(){

        userName = getSharedPreferences("account", MODE_PRIVATE).getString("username", null);
        passWord = getSharedPreferences("account", MODE_PRIVATE).getString("password", null);
        if(userName!=null && passWord != null){
            try {
                String jiemi1 = DESUtil.decrypt(userName, "A1B2C3D4E5F60708");
                String jiemi2 = DESUtil.decrypt(passWord, "A1B2C3D4E5F60708");
                Log.e(TAG,"解密后："+jiemi1+":---："+jiemi2);

                if (!checkLoginInfo(jiemi1, jiemi2)) {
                    showDialogs("登录中");

                    checkLogin(jiemi1,jiemi2);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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
                if (!checkLoginInfo(userName, pwd)) {
                    showDialogs("登录中");
                    checkLogin(userName,pwd);
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
     *
     * @param phone_login 登录手机号
     * @param pwd 登录密码
     * @return true:不能登录 false 可以登录
     */
    private boolean checkLoginInfo(String phone_login, String pwd) {
        //手机号码校验
        String telRegex = "[1][34587]\\d{9}";
        if (phone_login == null || "".equals(phone_login)) {
            showToast("电话号码不能为空");
            return true;
        } else if (pwd == null || "".equals(pwd)) {
            showToast("密码不能为空");
            return true;
        }else if(phone_login.matches(telRegex)){
            return false;
        }else{
            showToast("手机号不符合规则");
            return true;
        }


    }

    /**
     * 检查登录账号是否正确
     */
    private void checkLogin(String key,String pwd) {

        Log.e(TAG,"登录中。。。");
        OkHttpUtils.post().url(InterfaceUrl.MOBILE_LOGIN)
                .addParams(ParamConfigKey.USER_NAME, key
                ).addParams(ParamConfigKey.PASSWORD, pwd)
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
                    Log.e(TAG,"登录成功。。。");
                    Log.i(TAG, "parseNetworkResponse: " + loginGson.getUser().getLive_streaming_address());
                    //是否保存账号密码
                    if(remember.isChecked()){
                        //keep account
                        try {
                            Log.e(TAG,"电话："+mApplication.getUser().getTelephone());
                            Log.e(TAG,"密码："+mApplication.getUser().getPassword());
                            userName = DESUtil.encrypt(mApplication.getUser().getTelephone(), "A1B2C3D4E5F60708");
                            passWord = DESUtil.encrypt(pwdEdit.getText().toString(), "A1B2C3D4E5F60708");
                            Log.e(TAG,"加密后："+userName+":---："+passWord);
                            //保存用户的信息
                           getSharedPreferences("account",MODE_PRIVATE).edit()
                                    .putString("username", userName)
                                    .putString("password", passWord)
                                    .commit();



                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{

                    }
                    startActivity(HomeActivity.class);
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
