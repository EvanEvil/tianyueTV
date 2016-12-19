package com.tianyue.tv.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tianyue.tv.Config.InterfaceUrl;
import com.tianyue.tv.Config.ParamConfigKey;
import com.tianyue.tv.Config.RequestConfigKey;
import com.tianyue.tv.R;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;


/**
 * 注册界面
 * Created by hasee on 2016/8/11.
 */
public class RegisterTwo extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.register_two_nickname)
    EditText nickname;
    @BindView(R.id.register_two_password)
    EditText password;
    @BindView(R.id.register_two_state_password)
    CheckBox statePwd;
    @BindView(R.id.register_two_finish)
    Button finish;
    @BindView(R.id.register_two_toolbar)
    Toolbar toolbar;
    String telephone;
    String code;
    @Override
    protected void init() {
        super.init();
        Bundle bundle = getIntent().getExtras();
        telephone = bundle.getString(ParamConfigKey.TELEPHONE);
        code = bundle.getString(ParamConfigKey.P_CODE);
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initView(){
        setContentView(R.layout.register_layout_2);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    @OnCheckedChanged(R.id.register_two_state_password)
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }else{
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }
    @OnTextChanged(value = {R.id.register_two_nickname,R.id.register_two_password},callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void onTextChange(CharSequence text){
        if (text.toString().equals("")) {
            finish.setEnabled(false);
        }else {
            finish.setEnabled(true);
        }
    }
    @OnClick(R.id.register_two_finish)
    public void onClick(View v) {
        if (v.getId() == R.id.register_two_finish) {
            if (!isNull(nickname.getText().toString(),password.getText().toString())) {
                String nickname = this.nickname.getText().toString();
                String password = this.password.getText().toString();
                OkHttpUtils.post().url(InterfaceUrl.REGISTER_USER)
                        .addParams(ParamConfigKey.TELEPHONE,telephone)
                        .addParams(ParamConfigKey.NICK_NAME,nickname)
                        .addParams(ParamConfigKey.CODE,code)
                        .addParams(ParamConfigKey.PASSWORD,password).build().execute(new com.zhy.http.okhttp.callback.Callback() {
                    @Override
                    public Object parseNetworkResponse(Response response) throws IOException {
                        String result = response.body().string();
                        try {
                            JSONObject object = new JSONObject(result);
                            String ret = object.optString(RequestConfigKey.RET);
                            if (ret != null) {
                                if (ret.equals(RequestConfigKey.REQUEST_SUCCESS)) {
                                    showToast("注册成功");
                                    finish();
                                }else if(ret.equals(RequestConfigKey.NICKNAME_EXISTING)){
                                    showToast("昵称已存在");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    public void onError(Request request, Exception e) {
                        showToast("注册失败");
                    }

                    @Override
                    public void onResponse(Object response) {

                    }
                });
            }
        }
    }

    /**
     * 是否为空
     * @param nickname
     * @param password
     * @return
     */
    private boolean isNull(String nickname,String password){
        if (nickname.equals("") && nickname== null) {
            showToast("昵称不能为空");
            return true;
        }else if(password.equals("") && password== null){
            showToast("密码不能为空");
            return true;
        }
        return false;
    }

}
