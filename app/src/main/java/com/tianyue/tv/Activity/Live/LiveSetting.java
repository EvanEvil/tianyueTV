package com.tianyue.tv.Activity.Live;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tianyue.tv.Activity.BaseActivity;
import com.tianyue.tv.Bean.Broadcast;
import com.tianyue.tv.Bean.TypeBean;
import com.tianyue.tv.Bean.User;
import com.tianyue.tv.Config.InterfaceUrl;
import com.tianyue.tv.Config.RequestConfigKey;
import com.tianyue.tv.CustomView.Dialog.BottomScrollPickerDialog;
import com.tianyue.tv.MyApplication;
import com.tianyue.tv.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 直播间设置
 * Created by hasee on 2016/11/28.
 */
public class LiveSetting extends BaseActivity {
    @BindView(R.id.live_setting_cancel)
    TextView cancel;
    @BindView(R.id.live_setting_save)
    TextView save;
    @BindView(R.id.live_setting_toolbar)
    Toolbar toolbar;
    @BindView(R.id.my_live_setting_name)
    EditText title;
    @BindView(R.id.my_live_setting_classify)
    TextView classify;
    @BindView(R.id.my_live_setting_alter)
    ImageView alter;
    @BindView(R.id.my_live_setting_label_one)
    TextView labelOne;
    @BindView(R.id.my_live_setting_label_two)
    TextView labelTwo;
    @BindView(R.id.my_live_setting_label_three)
    TextView labelThree;
    @BindView(R.id.my_live_setting_label_four)
    TextView labelFour;


    private boolean isHaveBucket = false;
    User user;

    private String mainType;
    private String minorType;

    private String keyWord;

    @Override
    protected void initView() {
        setContentView(R.layout.my_live_setting_layout);
    }

    @Override
    protected void init() {
        Log.i(TAG, "init: ");
        user = MyApplication.instance().getUser();
        Integer baudit = user.getBaudit();
        Log.i(TAG, "init: " + baudit);
        if (baudit == null) {
            isHaveBucket = false;
            return;
        }
        switch (baudit) {
            case 1:
                isHaveBucket = true;
                queryBucket();
                break;
        }
    }

    @OnClick({R.id.live_setting_cancel, R.id.live_setting_save, R.id.my_live_setting_classify, R.id.my_live_setting_alter})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.live_setting_cancel:
                finish();
                break;
            case R.id.live_setting_save:
                init();
                if (isHaveBucket) {
                    applyForBucket();
                } else {
                    alterBucket();
                }
                break;
            case R.id.my_live_setting_classify:
                final BottomScrollPickerDialog dialog = new BottomScrollPickerDialog(this);
                dialog.setOnLastBottomScrollCallback(new BottomScrollPickerDialog.BottomScrollCallback() {
                    @Override
                    public void onBtnCallback() {
                        classify.setText(mainType + "-" + minorType);
                    }
                });
                dialog.setOnBottomScrollDataCallBack(new BottomScrollPickerDialog.BottomScrollDataCallBack() {
                    @Override
                    public void onSelected(List<String> data, int position, int type) {
                        switch (type) {
                            case BottomScrollPickerDialog.SCROLL_PICKER_ONE_TYPE:
                                mainType = data.get(position);
                                break;
                            case BottomScrollPickerDialog.SCROLL_PICKER_TWO_TYPE:
                                minorType = data.get(position);
                                break;
                        }
                    }
                });
                dialog.show();
                break;
            case R.id.my_live_setting_alter:
                Bundle bundle = new Bundle();
                bundle.putString("label",keyWord);
                Intent intent = new Intent(this,LabelManage.class);
                intent.putExtras(bundle);
                startActivityForResult(intent,2);
                break;
        }
    }

    /**
     * 申请直播间
     */
    private void applyForBucket() {
//        OkHttpUtils.post().url(InterfaceUrl.APPLY_FOR_BUCKET).addParams()

    }

    /**
     * 查询直播间
     */
    private void queryBucket() {
        Log.i(TAG, "queryBucket: " + user.getId());
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
                        Log.i(TAG, "parseNetworkResponse: " + broad.getName());
                        fillBucketInfo(broad);
                    } else if (ret.equals(RequestConfigKey.REQUEST_ERROR)) {
                        showToast("获取直播间信息失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "parseNetworkResponse: " + result);
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

    /**
     * 修改直播间
     */
    private void alterBucket() {
        /**
         * 传参：id （user_id）必传
         name  可选  不填就默认原本的传
         typeName  可选  不填就默认原本的传
         keyWord   可选  不填就默认原本的传
         */
        OkHttpUtils.post().url(InterfaceUrl.ALTER_BUCKET)
                .addParams("id", user.getId())
                .addParams("name", title.getText().toString())
                .addParams("typeName", classify.getText().toString())
                .addParams("keyWord", "").build().execute(new Callback() {
            @Override
            public Object parseNetworkResponse(Response response) throws IOException {
                return null;
            }

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(Object response) {

            }
        });

    }

    /**
     * 填充数据
     */
    private void fillBucketInfo(Broadcast broad) {
        keyWord = broad.getKeyWord();
        String name = broad.getName();
        updateView(name,broad.getBctypeId(),broad.getTytypeId());
        updateLabel(keyWord);
    }

    private void updateLabel(String keyWord){
        if (keyWord != null) {
            String[] label = keyWord.split("_");
            switch (label.length) {
                case 0:
                    goneView(labelOne);
                    goneView(labelTwo);
                    goneView(labelThree);
                    goneView(labelFour);
                    break;
                case 1:
                    labelOne.setText(label[0]);
                    showView(labelOne);
                    goneView(labelTwo);
                    goneView(labelThree);
                    goneView(labelFour);
                    break;
                case 2:
                    labelOne.setText(label[0]);
                    labelTwo.setText(label[1]);
                    showView(labelOne);
                    showView(labelTwo);
                    goneView(labelThree);
                    goneView(labelFour);
                    break;
                case 3:
                    labelOne.setText(label[0]);
                    labelTwo.setText(label[1]);
                    labelThree.setText(label[2]);
                    showView(labelOne);
                    showView(labelTwo);
                    showView(labelThree);
                    goneView(labelFour);
                    break;
                case 4:
                    labelOne.setText(label[0]);
                    labelTwo.setText(label[1]);
                    labelThree.setText(label[2]);
                    labelFour.setText(label[3]);
                    showView(labelOne);
                    showView(labelTwo);
                    showView(labelThree);
                    showView(labelFour);
                    break;
            }
        }
    }

    /**
     * 更新UI界面
     * @param name
     * @param mainTypeId
     * @param minorTypeId
     */
    private void updateView(final String name, final String mainTypeId, String minorTypeId){
        TypeBean typeBean = new TypeBean();
        HashMap<String,String> main = typeBean.getMainClassify();
        HashMap<String,String> minor = typeBean.getMinorClassify();
        final String mainTypeName = main.get(mainTypeId);
        final String minorTypeName = minor.get(minorTypeId);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                classify.setText(mainTypeName+"-"+minorTypeName);
                title.setText(name);
            }
        });
    }

    private void showView(final View view) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.VISIBLE);
            }
        });

    }

    private void goneView(final View view) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: "+requestCode+" "+resultCode);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 2:
                    Log.i(TAG, "onActivityResult: 进入");
                    Bundle bundle = data.getExtras();
                    keyWord = bundle.getString("label");
                    updateLabel(keyWord);
                    break;
            }
        }
    }
}
