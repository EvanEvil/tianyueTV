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
    @BindView(R.id.clip_cancel)
    Button cancel;

    @Override
    protected void initView() {
        setContentView(R.layout.clip_layout);
        clipImageLayout.setDrawable(Drawable.createFromPath(getIntent().getStringExtra("picPath")));
    }

    @OnClick({R.id.clip_confirm,R.id.clip_cancel})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clip_cancel:
                finish();
                break;
            case R.id.clip_confirm:
                showDialogs("修改中");
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
                    UpLoadFileUtil.getInstance().setCompleteResultListener((isSuccess, result) -> {
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
                        } else {
                            showToast("修改失败");
                            dismissDialogs();
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
        OkHttpUtils.post().url(InterfaceUrl.ALTER_USER_INFO)
                .addParams("headUrl", path)
                .addParams("userId", user.getId())
                .build().execute(new Callback() {
            @Override
            public Object parseNetworkResponse(Response response) throws IOException {
                String result = response.body().string();
                try {
                    JSONObject object = new JSONObject(result);
                    String ret = object.optString("ret");
                    if (ret != null) {
                        if (ret.equals("repeat1")) {
                            showToast("用户不存在");
                            dismissDialogs();
                        } else if (ret.equals("success")){
                            String headPic = "http://images.tianyue.tv"+path;
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
