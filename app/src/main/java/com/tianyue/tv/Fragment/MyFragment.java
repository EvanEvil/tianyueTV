package com.tianyue.tv.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TResult;
import com.squareup.picasso.Picasso;
import com.tianyue.tv.Activity.My.AccountSecurity;
import com.tianyue.tv.Activity.My.AttentionAnchor;
import com.tianyue.tv.Activity.My.AuditFailure;
import com.tianyue.tv.Activity.My.AuditSuccess;
import com.tianyue.tv.Activity.My.Auditing;
import com.tianyue.tv.Activity.My.CertificationOne;
import com.tianyue.tv.Activity.My.Settings;
import com.tianyue.tv.Activity.My.UpLoadHeadPic;
import com.tianyue.tv.Bean.User;
import com.tianyue.tv.Config.ActivityForResultConfig;
import com.tianyue.tv.Config.AuditStateConfig;
import com.tianyue.tv.CustomView.Dialog.FormBotomDefaultDialogBuilder;
import com.tianyue.tv.MyApplication;
import com.tianyue.tv.R;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 我的Fragment
 * Created by hasee on 2016/8/15.
 */
public class MyFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.my_settings)
    TextView settings;//设置
    @BindView(R.id.my_home_nickName)
    TextView nickName;//昵称
    @BindView(R.id.my_concern)
    TextView concern;//关注主播
    @BindView(R.id.my_home_headIcon)
    CircleImageView headPic;//头像
    @BindView(R.id.my_account_security)
    TextView accountSecurity;//账号安全
    @BindView(R.id.my_certification)
    TextView certification;//主播认证
    @BindView(R.id.my_play_history)
    TextView playHistory;//播放历史
    MyApplication myApplication;

    String headUrl;
    String picPath;
    User user;//用户信息

    /**
     * 拍照相关
     */


    private TakePhoto takePhoto;
    private CropOptions cropOptions;
    private Uri imageUri;


    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.my_home, null);
    }

    @Override
    public void init() {
        fillUser();
    }

    @Override
    public void finishCreateView(Bundle state) {

    }


    /**
     * 填充用户信息
     */
    private void fillUser() {
        if (myApplication == null) {
            myApplication = MyApplication.instance();
        }
        user = myApplication.getUser();
        if (user != null) {
            headUrl = user.getHeadUrl();
            nickName.setText(user.getNickName());
        }
        if (headUrl != null && !"".equals(headUrl)) {
            Picasso.with(getActivity()).load(headUrl).into(headPic);
        }
    }

    public void upDateUser() {
        fillUser();
    }

    @OnClick({R.id.my_settings, R.id.my_account_security, R.id.my_concern, R.id.my_home_headIcon,
            R.id.my_play_history, R.id.my_certification})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_settings:
                startActivity(Settings.class);
                break;
            case R.id.my_account_security:  //账号安全
                startActivity(AccountSecurity.class);
                break;
            case R.id.my_concern:
                startActivity(AttentionAnchor.class);
                break;
            case R.id.my_certification: //主播认证
                Integer bCard = user.getbCard();//审核状态
                if (bCard == null) {
                    startActivity(CertificationOne.class);
                    return;
                }
                switch (bCard) {
                    case AuditStateConfig.AUDIT_SUCCESS:
                        startActivity(AuditSuccess.class);
                        break;
                    case AuditStateConfig.AUDIT_FAILURE:
                        startActivity(AuditFailure.class);
                        break;
                    case AuditStateConfig.AUDITING:
                        startActivity(Auditing.class);
                        break;
                    case AuditStateConfig.AUDIT_CONTINUE:
                        break;
                }
                break;
            case R.id.my_play_history:
                break;
            case R.id.my_home_headIcon:
                setTakePhoto();
                FormBotomDefaultDialogBuilder dialogBuilder = new FormBotomDefaultDialogBuilder
                        (getActivity());
                dialogBuilder.setFBFirstBtnClick(new FormBotomDefaultDialogBuilder
                        .DialogBtnCallBack() {
                    @Override
                    public void dialogBtnOnClick() {

                    }
                });
                dialogBuilder.setFBFirstBtnText("拍一张");
                dialogBuilder.setFBLastBtnText("从相册中选择");
                //头像 拍一张点击事件
                dialogBuilder.setFBFirstBtnClick(new FormBotomDefaultDialogBuilder
                        .DialogBtnCallBack() {
                    @Override
                    public void dialogBtnOnClick() {
                        openCamera();
                    }
                });
                //进入相册点击事件
                dialogBuilder.setFBLastBtnClick(new FormBotomDefaultDialogBuilder
                        .DialogBtnCallBack() {
                    @Override
                    public void dialogBtnOnClick() {
                        openPhoto();
                    }
                });
                dialogBuilder.show();
                break;
            default:
                break;
        }
    }

    private void setTakePhoto() {
        takePhoto = getTakePhoto();
        cropOptions = new CropOptions.Builder().setAspectX(1).setAspectY(1).setWithOwnCrop(true).create();
        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        imageUri = Uri.fromFile(file);
    }

    /**
     * 打开相机
     */
    private void openCamera() {


        // takePhoto.onPickFromCaptureWithCrop(imageUri, cropOptions);
        takePhoto.onPickFromCapture(imageUri);
    }


    /**
     * 打开相册
     */
    private void openPhoto() {
//        Intent photo = new Intent(Intent.ACTION_PICK, null);
//        photo.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//        startActivityForResult(photo, ActivityForResultConfig.OPEN_PHOTO);
        //  takePhoto.onPickFromGalleryWithCrop(imageUri, cropOptions);
        takePhoto.onPickFromGallery();
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == Activity.RESULT_OK) {
//            //相册
//            if (requestCode == ActivityForResultConfig.OPEN_PHOTO) {
//                Uri uri = data.getData();
//                String[] opj = {MediaStore.Images.Media.DATA};
//                if (uri != null && opj != null) {
//                    Cursor cursor = getActivity().managedQuery(uri, opj, null, null, null);
//                    cursor.moveToFirst();
//                    picPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images
//                            .Media.DATA));
//                    if (picPath != null) {
//                     Intent intent = new Intent(getActivity(), UpLoadHeadPic.class);
//                        intent.putExtra("picPath", picPath);
//                        startActivityForResult(intent, ActivityForResultConfig.UP_LOAD_HEAD);
//                    }
//                }
//            }
//           //相机回调
//
//        }
//    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == ActivityForResultConfig.UP_LOAD_HEAD){
                String headUrl = myApplication.getUser().getHeadUrl();
                if(headUrl != null){
                    Glide.with(context)
                            .load(headUrl)
                            .into(headPic);
                }
            }
        }
    }

    /**
     * 设置头像
     *
     * @param bytes
     */
    public void setHeadPic(byte[] bytes) {
        if (bytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            headPic.setImageBitmap(bitmap);
        }
    }

    /**
     * 拍照成功的回调
     *
     * @param result
     */
    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);

        String picPath = result.getImage().getOriginalPath();

        //打开 裁剪并上传头像的activity
        Intent intent = new Intent(getActivity(), UpLoadHeadPic.class);
        intent.putExtra("picPath", picPath);
        startActivityForResult(intent, ActivityForResultConfig.UP_LOAD_HEAD);
    }

    /**
     * 拍照失败的回调
     *
     * @param result
     * @param msg
     */
    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
        Log.e(TAG, "takeFail:" + msg);
    }

    /**
     * 用户取消的回调
     */
    @Override
    public void takeCancel() {
        super.takeCancel();
        Log.e(TAG, "用户取消");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"fragment被销毁了");

    }
}
