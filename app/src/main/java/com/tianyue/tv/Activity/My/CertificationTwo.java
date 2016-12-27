package com.tianyue.tv.Activity.My;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tianyue.mylibrary.view.LoadingView;
import com.tianyue.tv.Activity.BaseActivity;
import com.tianyue.tv.Config.InterfaceUrl;
import com.tianyue.tv.CustomView.Dialog.FormBotomDefaultDialogBuilder;
import com.tianyue.tv.MyApplication;
import com.tianyue.tv.R;
import com.tianyue.tv.Util.UpLoadFileUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 实名认证
 * Created by hasee on 2016/8/24.
 */
public class CertificationTwo extends BaseActivity {
    @BindView(R.id.certification_toolbar)
    Toolbar toolbar;
    @BindView(R.id.certification_next)
    Button next;
    @BindView(R.id.certification_add_pic)
    ImageView addPic;
    @BindView(R.id.certification_add_pic_back)
    ImageView addPicBack;
    @BindView(R.id.certification_identity)
    EditText identity;
    @BindView(R.id.certification_name)
    EditText name;
    LoadingView loadingView = null;
    /**
     * 第一张图片
     */
    private final int FIRST_PIC = 101;
    /**
     * 第二张图片
     */
    private final int LAST_PIC = 103;

    String firstPath;
    String lastPath;

    @Override
    protected void initView() {
        setContentView(R.layout.certification_layout);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick({R.id.certification_add_pic, R.id.certification_next, R.id.certification_add_pic_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.certification_next:
                upLoadPic();
                break;
            case R.id.certification_add_pic:
                bottomDialog(FIRST_PIC);
                break;
            case R.id.certification_add_pic_back:
                bottomDialog(LAST_PIC);
                break;
        }
    }


    /**
     * 上传身份证照
     */
    private void upLoadPic() {
        if (name.getText().toString().equals("") || name.getText().toString() == null) {
            name.requestFocus();
            showToast("姓名不能为空");
            return;
        }
        if (identity.getText().toString().equals("") || identity.getText().toString() == null) {
            identity.requestFocus();
            showToast("身份证号不能为空");
            return;
        }
        if (firstPath == null) {
            showToast("请添加第一张身份证照");
            return;
        }
        if (lastPath == null) {
            showToast("请添加第二张身份证照");
            return;
        }
        showDialog();
        upLoadFirstPic();
    }

    /**
     * 上传第一张图片
     */
    private void upLoadFirstPic(){
        UpLoadFileUtil.getInstance().setCompleteResultListener((isSuccess, result) -> {
            Log.e("onComplete", "onComplete: " + isSuccess + ":" + result);
            if (isSuccess) {
                try {
                    JSONObject object = new JSONObject(result);
                    int code = object.optInt("code");
                    if (code == 200) {
                        firstPath = object.optString("url");
                        Log.i(TAG, "result: "+firstPath);
                        upLoadLastPic();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                showToast("上传照片失败");
                dismissDialog();
                return;
            }
        }).upLoadFile(new File(firstPath));
    }
    /**
     * 上传第二张图片
     */
    private void upLoadLastPic(){
        UpLoadFileUtil.getInstance().setCompleteResultListener((isSuccess, result) -> {
            if (isSuccess) {
                try {
                    JSONObject object = new JSONObject(result);
                    int code = object.optInt("code");
                    if (code == 200) {
                        lastPath = object.optString("url");
                        Log.i(TAG, "result: "+lastPath);
                        commitAttestation();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                showToast("上传照片失败");
                dismissDialog();
                return;
            }
        }).upLoadFile(new File(lastPath));
    }
    /**
     * 显示提示窗口
     */
    private void showDialog() {
        loadingView = new LoadingView(this);
        loadingView.setLoadingViewText("提交认证中...");
        loadingView.setTouchOutside(false);
        loadingView.show();
    }

    /**
     * 取消提示窗口
     */
    private void dismissDialog() {
        if (loadingView != null) {
            loadingView.dismiss();
            loadingView = null;
        }
    }

    /**
     * 提交认证
     */
    private void commitAttestation() {
        OkHttpUtils.post().url(InterfaceUrl.CERTIFICATION)
                .addParams("userName", name.getText().toString())
                .addParams("identityCard", identity.getText().toString())
                .addParams("cardImage", firstPath)
                .addParams("backImage",lastPath)
                .addParams("brandType", "0")
                .addParams("user_id", MyApplication.instance().getUser().getId())
                .build().execute(new Callback() {
            @Override
            public Object parseNetworkResponse(Response response) throws IOException {
                String result = response.body().string();
                try {
                    Log.i(TAG, "parseNetworkResponse: "+result);
                    JSONObject object = new JSONObject(result);
                    String ret = object.optString("ret");
                    if (ret != null) {
                        if (ret.equals("alreadyError")) {
                            showToast("已提交认证");
                            dismissDialog();
                            startActivity(CertificationFinish.class);
                        } else if (ret.equals("checkError")) {
                            showToast("输入正确的身份证");
                            dismissDialog();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onError(Request request, Exception e) {
                showToast("提交认证失败");
                dismissDialog();
            }

            @Override
            public void onResponse(Object response) {

            }
        });
    }

    /**
     * 弹出底部选择框
     */
    private void bottomDialog(final int code) {
        FormBotomDefaultDialogBuilder dialogBuilder = new FormBotomDefaultDialogBuilder(this);
        dialogBuilder.setAllBtnTextColor(R.color.white);
        dialogBuilder.setFBFirstBtnText("拍照");
        dialogBuilder.setFBCancelBtnText("取消");
        dialogBuilder.setFBLastBtnText("相册");
        dialogBuilder.setFBLastBtnClick(() -> openPhoto(code));
        dialogBuilder.show();
    }

    /**
     * 打开相册
     *
     * @param code
     */
    private void openPhoto(int code) {
        Intent openPhoto = new Intent(Intent.ACTION_PICK, null);
        openPhoto.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(openPhoto, code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FIRST_PIC:
                    setPic(getPath(data, FIRST_PIC), FIRST_PIC);
                    break;
                case LAST_PIC:
                    setPic(getPath(data, LAST_PIC), LAST_PIC);
                    break;
            }
        }
    }

    /**
     * 设置预览图
     */
    private void setPic(String path, int code) {
        if (path != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            if (bitmap != null) {
                switch (code) {
                    case FIRST_PIC:
                        addPic.setImageBitmap(bitmap);
                        break;
                    case LAST_PIC:
                        addPicBack.setImageBitmap(bitmap);
                        break;
                }
            }
        }
    }

    /**
     * 提取路径
     *
     * @param data
     * @param code
     * @return
     */
    private String getPath(Intent data, int code) {
        Uri picUri = data.getData();
        String[] prj = {MediaStore.Images.Media.DATA};
        if (picUri != null && prj != null) {
            Cursor cursor = managedQuery(picUri, prj, null, null, null);
            cursor.moveToFirst();
            int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (code == FIRST_PIC) {
                firstPath = cursor.getString(index);
                return firstPath;
            }
            if (code == LAST_PIC) {
                lastPath = cursor.getString(index);
                return lastPath;
            }
        }
        return null;
    }

}
