package com.tianyue.tv.Activity.Live;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tianyue.tv.Activity.BaseActivity;
import com.tianyue.tv.Bean.Broadcast;
import com.tianyue.tv.Bean.User;
import com.tianyue.tv.Config.InterfaceUrl;
import com.tianyue.tv.Config.RequestConfigKey;
import com.tianyue.tv.MyApplication;
import com.tianyue.tv.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 直播空间
 * Created by hasee on 2016/11/28.
 */
public class LiveBucket extends BaseActivity {
    @BindView(R.id.my_live_bucket_help)
    TextView help;
    @BindView(R.id.my_live_bucket_toolbar)
    Toolbar toolbar;
    @BindView(R.id.my_live_bucket_fans)
    TextView fans;
    @BindView(R.id.my_live_bucket_store)
    TextView store;
    @BindView(R.id.my_live_bucket_money)
    TextView money;
    @BindView(R.id.my_live_bucket_setting)
    TextView setting;
    @BindView(R.id.my_live_bucket_start)
    Button startLive;
    Broadcast broadInfo;

    private final static int UPDATE_UI = 1;
    User user;

    @Override
    protected void initView() {
        setContentView(R.layout.my_live_bucket);
        user = MyApplication.instance().getUser();
        toolbar.setNavigationOnClickListener(v -> finish());
        queryBucket();
    }

    @OnClick({R.id.my_live_bucket_help, R.id.my_live_bucket_fans, R.id.my_live_bucket_store, R.id.my_live_bucket_money, R.id.my_live_bucket_setting, R.id.my_live_bucket_start})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_live_bucket_help:
                break;
            case R.id.my_live_bucket_fans:
                break;
            case R.id.my_live_bucket_store:
                showToast("暂未开放");
                break;
            case R.id.my_live_bucket_money:
                showToast("暂未开放");
                break;
            case R.id.my_live_bucket_setting:
                startActivity(LiveSetting.class);
                break;
            case R.id.my_live_bucket_start:
                if (broadInfo != null) {
                    Intent intent = new Intent(this,StartLivePort.class);
                    intent.putExtra("broad",broadInfo);
                    startActivity(intent);
                    return;
                }
                startActivity(StartLivePort.class);
                break;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_UI:
                    Bundle bundle = msg.getData();
                    int fansNumber = bundle.getInt("fans");
                    runOnUiThread(() -> fans.setText("我的粉丝数:"+fansNumber));
                    break;
            }
        }
    };

    /**
     * 查询直播间
     */
    private void queryBucket() {
        OkHttpUtils.post()
                .url(InterfaceUrl.QUERY_BUCKET)
                .addParams("userId", user.getId())
                .build().execute(new Callback() {
            @Override
            public Object parseNetworkResponse(Response response) throws IOException {
                String result = response.body().string();
                try {
                    JSONObject object = new JSONObject(result);
                    String ret = object.optString(RequestConfigKey.RET);
                    if (ret.equals(RequestConfigKey.REQUEST_SUCCESS)) {
                        JSONArray array = object.optJSONArray("broadcast");
                        JSONObject ject = array.getJSONObject(0);
                        Gson gson = new Gson();
                        Broadcast broad = gson.fromJson(ject.toString(), Broadcast.class);
                        user.setLive_streaming_address(broad.getQl_playAddress());
                        MyApplication.instance().setUser(user);
                        broadInfo = broad;
                        //更新UI界面 显示粉丝数
                        Message message = new Message();
                        message.what = UPDATE_UI;
                        Bundle bundle = new Bundle();
                        bundle.putInt("fans", broad.getFocusNum());
                        message.setData(bundle);
                        handler.handleMessage(message);
                    } else if (ret.equals(RequestConfigKey.REQUEST_ERROR)) {
                        showToast("获取直播间信息失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onError(Request request, Exception e) {
                Log.i(TAG, "onError: " + request.body().toString());
            }

            @Override
            public void onResponse(Object response) {
                Log.i(TAG, "onResponse: ");
            }
        });
    }
}
