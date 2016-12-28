package com.tianyue.tv.Activity;

import android.os.Handler;
import android.util.Log;

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

import java.io.File;
import java.io.IOException;

/**
 * Created by hasee on 2016/12/23.
 */
public class SplashActivity extends BaseActivity {
    private String userName;
    private String passWord;
    MyApplication mApplication;
    private Handler mHandler = new Handler();
    @Override
    protected void initView() {
        setContentView(R.layout.splash_layout);
    }

    @Override
    protected void init() {
        checkAutoLogin();

    }

    @Override
    protected boolean isHasAnimiation() {
        return false;
    }

    /**
     * 判断用户是否登录过,从sp缓存中获取,并登录,如果没有缓存,则延时2秒后跳转到登录界面
     */
    public void checkAutoLogin(){
        File file = new File("/data/data/" + getPackageName().toString()
                + "/shared_prefs", "account.xml");
        if (file.exists()) {
            boolean exists = file.exists();

            Log.e(TAG, "校验SP,SP是否存在:" +exists);
        }
        userName = getSharedPreferences("account", MODE_PRIVATE).getString("username", null);
        passWord = getSharedPreferences("account", MODE_PRIVATE).getString("password", null);
        if(userName!=null && passWord != null){
            try {
                String jiemi1 = DESUtil.decrypt(userName, "A1B2C3D4E5F60708");
                String jiemi2 = DESUtil.decrypt(passWord, "A1B2C3D4E5F60708");
                Log.e(TAG,"解密后："+jiemi1+":---："+jiemi2);

                if (!checkLoginInfo(jiemi1, jiemi2)) {
//                    showDialogs("登录中");

                    checkLogin(jiemi1,jiemi2);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Log.e(TAG,"账号和密码为空,将进入登录界面");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(LoginActivity.class);
                    finish();
                }
            }, 2000);
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
//                    dismissDialogs();
                    Log.e(TAG,"登录成功。。。");
                    Log.i(TAG, "parseNetworkResponse: " + loginGson.getUser().getLive_streaming_address());




                    startActivity(HomeActivity.class);
                    mHandler.removeCallbacksAndMessages(null);
                    finish();
                } else {
                    showToast("用户名或密码错误");
//                    dismissDialogs();
                }
                return null;
            }

            @Override
            public void onError(Request request, Exception e) {
                showToast("请求失败，检查您的网络是否可用");
//                dismissDialogs();
                startActivity(LoginActivity.class);
                finish();
            }

            @Override
            public void onResponse(Object response) {

            }
        });
    }


}

