package com.tianyue.tv.Activity.My;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tianyue.mylibrary.view.ClipImageLayout;
import com.tianyue.tv.Activity.BaseActivity;
import com.tianyue.tv.Activity.HomeActivity;
import com.tianyue.tv.Bean.User;
import com.tianyue.tv.Config.InterfaceUrl;
import com.tianyue.tv.MyApplication;
import com.tianyue.tv.R;
import com.tianyue.tv.Util.UpLoadFileUtil;
import com.tianyue.tv.Util.Util;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 上传头像
 * Created by hasee on 2016/11/15.
 */
public class UpLoadHeadPic extends BaseActivity {
    @BindView(R.id.clip_confirm)
    Button confirm;
    @BindView(R.id.clip_pic)
    ClipImageLayout clipImageLayout;

    @Override
    protected void initView() {
        setContentView(R.layout.clip_layout);
        clipImageLayout.setDrawable(Drawable.createFromPath(getIntent().getStringExtra("picPath")));
    }

    @OnClick(R.id.clip_confirm)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clip_confirm:
                Bitmap bitmap = clipImageLayout.clip();
                String path;
                if (Util.checkSdCard()) {
                    path = Environment.getExternalStorageDirectory().getPath() + "/headPic.jpg";
                } else {
                    path = Environment.getRootDirectory().getPath() + "/headPic.jpg";
                }
                Log.i(TAG, "onClick: " + path);
                File file = new File(path);
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    UpLoadFileUtil.getInstance().setCompleteResultListener(new UpLoadFileUtil.CompleteResultListener() {
                        @Override
                        public void result(boolean isSuccess, String result) {
                            if (isSuccess) {
                                try {
                                    JSONObject object = new JSONObject(result);
                                    int code = object.optInt("code");
                                    if (code == 200) {
                                        String headPath = object.optString("url");
                                        Log.i(TAG, "result: "+headPath);
                                        uploadHead(headPath);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    }).upLoadFile(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * 修改头像
     */
    private void uploadHead(final String path) {
        final User user = MyApplication.instance().getUser();
        Log.i(TAG, "uploadHead: "+user.getId()+user.getHeadUrl()+user.getNickName());
        showDialogs("修改中");
        OkHttpUtils.post().url(InterfaceUrl.ALTER_USER_INFO)
                .addParams("headUrl", path)
                .addParams("nickName", user.getNickName())
                .addParams("user_id", user.getId())
                .build().execute(new Callback() {
            @Override
            public Object parseNetworkResponse(Response response) throws IOException {
                String result = response.body().string();
                Log.i(TAG, "parseNetworkResponse: "+result);
                try {
                    JSONObject object = new JSONObject(result);
                    String ret = object.optString("ret");
                    if (ret != null) {
                        if (ret.equals("repeat1")) {
                            showToast("用户不存在");
                            dismissDialogs();
                        } else if (ret.equals("success")){
                            String headPic = "http://images.tianyue.tv"+path;
                            Log.i(TAG, "parseNetworkResponse: "+headPic);
                            user.setHeadUrl(headPic);
                            showToast("修改成功");
                            dismissDialogs();
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onError(Request request, Exception e) {
                Log.i(TAG, "onError: "+e.getMessage());
                showToast("修改失败");
                dismissDialogs();
            }

            @Override
            public void onResponse(Object response) {

            }
        });

    }
}
