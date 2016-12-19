package com.tianyue.tv.Activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tianyue.mylibrary.view.LoadingView;
import com.tianyue.tv.Config.InterfaceUrl;
import com.tianyue.tv.Config.ParamConfigKey;
import com.tianyue.tv.Config.RequestConfigKey;
import com.tianyue.tv.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * 忘记密码页面二
 * Created by hasee on 2016/11/4.
 */
public class ForgetTwo extends BaseActivity {

    String telephone;
    String code;
    LoadingView loadingDialog;
    @BindView(R.id.forget_two_toolbar)
    Toolbar toolbar;
    @BindView(R.id.forget_two_password)
    EditText inputPwd;
    @BindView(R.id.forget_two_repassword)
    EditText inputRePwd;
    @BindView(R.id.forget_two_finish)
    Button finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initView() {
        setContentView(R.layout.forget_password_two_layout);
        Bundle bundle = getIntent().getExtras();
        code = bundle.getString(ParamConfigKey.CODE);
        telephone = bundle.getString(ParamConfigKey.TELEPHONE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @OnClick(R.id.forget_two_finish)
    public void onClick(View v) {
        String pwd = inputPwd.getText().toString();
        String rePwd = inputRePwd.getText().toString();
        if (pwd.equals(rePwd)) {
            loadingDialog = new LoadingView(ForgetTwo.this);
            loadingDialog.setLoadingViewText("修改中");
            loadingDialog.setTouchOutside(false);
            loadingDialog.show();
            alterPwd(pwd, rePwd);
        } else {
            showToast("两次密码不一致");
        }
    }

    /**
     * 关闭加载界面
     */
    private void dismissLoadingView() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    private void alterPwd(String pwd, final String rePwd) {
        OkHttpUtils.post().url(InterfaceUrl.ALTER_PWD)
                .addParams(ParamConfigKey.TELEPHONE, telephone)
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
                            startActivity(LoginActivity.class);
                        } else {
                            dismissLoadingView();
                            showToast("修改密码失败");
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

    @OnTextChanged(value = {R.id.forget_two_password, R.id.forget_two_repassword}, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!inputPwd.getText().toString().equals("") &&
                !inputRePwd.getText().toString().equals("")) {
            finish.setEnabled(true);
        } else {
            finish.setEnabled(false);
        }
    }
}
