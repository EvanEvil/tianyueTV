package com.tianyue.tv.Activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tianyue.tv.Config.InterfaceUrl;
import com.tianyue.tv.Config.ParamConfigKey;
import com.tianyue.tv.Config.RequestConfigKey;
import com.tianyue.tv.R;
import com.tianyue.tv.Util.TimeCountUtil;
import com.tianyue.tv.Util.Util;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * 获取验证码注册界面
 * Created by hasee on 2016/8/11.
 */
public class RegisterOne extends BaseActivity {
    /**
     * 下一步
     */
    @BindView(R.id.register_one_next)
    Button next;
    /**
     * 电话输入
     */
    @BindView(R.id.register_one_phone_number)
    EditText inputPhone;
    /**
     * 验证码输入
     */
    @BindView(R.id.register_one_code)
    EditText inputCode;
    /**
     * 获取验证码
     */
    @BindView(R.id.register_one_getCode)
    Button getCode;
    @BindView(R.id.register_one_protocol)
    CheckBox protocol;
    @BindView(R.id.register_one_toolbar)
    Toolbar toolbar;
    /**
     * 注册协议
     */
    @BindView(R.id.register_one_agreement)
    TextView agreement;
    String code;
    String telephone;

    @Override
    protected void initView() {
        setContentView(R.layout.register_layout_1);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnCheckedChanged(R.id.register_one_protocol)
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.register_one_protocol:
                if (isChecked) {
                    next.setEnabled(true);
                } else {
                    next.setEnabled(false);
                }
                break;
        }

    }

    @OnClick({R.id.register_one_next, R.id.register_one_getCode,R.id.register_one_agreement})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_one_getCode:
                if (Util.isMobileNum(inputPhone.getText().toString())) {
                    requestCode();
                } else {
                    showToast("请输入正确的手机号");
                }
                break;
            case R.id.register_one_next:
                String code = inputCode.getText().toString();
                if (code.equals(this.code)) {
                    Bundle bundle = new Bundle();
                    bundle.putString(ParamConfigKey.P_CODE, code);
                    bundle.putString(ParamConfigKey.TELEPHONE, this.telephone);
                    startActivity(ForgetTwo.class, bundle);
                    finish();
                } else {
                    showToast("验证码不正确");
                }
                break;
            case R.id.register_one_agreement:
                Bundle bundle = new Bundle();
                bundle.putString("url",InterfaceUrl.AGREEMENT);
                startActivity(LoadWebActivity.class,bundle);
                break;
        }

    }

    /**
     * 请求获取验证码
     */
    private void requestCode() {
        OkHttpUtils.post().url(InterfaceUrl.GET_CODE)
                .addParams(ParamConfigKey.TELEPHONE, inputPhone.getText().toString()).build().execute(new Callback() {
            @Override
            public Object parseNetworkResponse(Response response) throws IOException {
                String result = response.body().string();
                try {
                    JSONObject object = new JSONObject(result);
                    String ret = object.optString(RequestConfigKey.RET);
                    if (ret != null) {
                        if (ret.equals(RequestConfigKey.REQUEST_SUCCESS)) {
                            code = object.optString(ParamConfigKey.P_CODE);
                            telephone = inputPhone.getText().toString();
                            Log.i(TAG, "parseNetworkResponse: " + code);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //验证码再次发送倒计时
                                    TimeCountUtil timeCountUtil = new TimeCountUtil(RegisterOne.this, 60 * 1000, 1000, getCode);
                                    timeCountUtil.start();
                                }
                            });
                        } else if (ret.equals(RequestConfigKey.REQUEST_ERROR)) {
                            showToast("手机号已存在");
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

}
