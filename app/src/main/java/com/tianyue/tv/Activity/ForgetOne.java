package com.tianyue.tv.Activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by hasee on 2016/11/4.
 */
public class ForgetOne extends BaseActivity {

    String code;
    @BindView(R.id.forget_one_toolbar)
    Toolbar toolbar;
    @BindView(R.id.forget_one_phone_number)
    EditText inputTelephone;
    @BindView(R.id.forget_one_code)
    EditText inputCode;
    @BindView(R.id.forget_one_getCode)
    Button getCode;
    @BindView(R.id.forget_one_next)
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initView() {
        setContentView(R.layout.forget_password_one_layout);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private boolean isNull(String telephone) {
        if (telephone.equals("") || telephone == null) {
            showToast("手机号不能为空");
            inputTelephone.requestFocus();
            return true;
        }
        return false;
    }

    /**
     * 获取验证码
     *
     * @param telephone 手机号
     */
    private void getCode(String telephone) {
        OkHttpUtils.post().url(InterfaceUrl.ALTER_PWD_CODE)
                .addParams(ParamConfigKey.TELEPHONE, telephone)
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
                            runOnUiThread(() -> {
                                TimeCountUtil time = new TimeCountUtil(ForgetOne.this, 60 * 1000, 1000, getCode);
                                time.start();
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

    @OnClick({R.id.forget_one_next,R.id.forget_one_getCode})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forget_one_getCode:
                String telephone = inputTelephone.getText().toString();
                if (!isNull(telephone)) {
                    if (Util.isMobileNum(telephone)) {
                        getCode(telephone);
                    } else {
                        showToast("请输入正确的手机号");
                    }
                }
                break;
            case R.id.forget_one_next:
                if (inputCode.getText().toString().equals(code)) {
                    Bundle bundle = new Bundle();
                    bundle.putString(ParamConfigKey.CODE, code);
                    bundle.putString(ParamConfigKey.TELEPHONE, inputTelephone.getText().toString());
                    startActivity(ForgetTwo.class,bundle);
                } else {
                    showToast("验证码错误");
                }
                break;
        }
    }
    @OnTextChanged(value = {R.id.forget_one_code,R.id.forget_one_phone_number},callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!inputCode.getText().toString().equals("") &&
                !inputTelephone.getText().toString().equals("")) {
            next.setEnabled(true);
        } else {
            next.setEnabled(false);
        }

    }
}
