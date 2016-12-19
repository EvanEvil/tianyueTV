package com.tianyue.tv.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.tianyue.tv.Gson.LoginGson;
import com.tianyue.tv.MyApplication;
import com.tianyue.tv.R;

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

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.my_home, null);
    }

    @Override
    protected void init() {
        fillUser();
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

    public void upDateUser(){
        fillUser();
    }

    @OnClick({R.id.my_settings, R.id.my_account_security, R.id.my_concern, R.id.my_home_headIcon, R.id.my_play_history, R.id.my_certification})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_settings:
                startActivity(Settings.class);
                break;
            case R.id.my_account_security:
                startActivity(AccountSecurity.class);
                break;
            case R.id.my_concern:
                startActivity(AttentionAnchor.class);
                break;
            case R.id.my_certification:
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
                FormBotomDefaultDialogBuilder dialogBuilder = new FormBotomDefaultDialogBuilder(getActivity());
                dialogBuilder.setFBFirstBtnClick(new FormBotomDefaultDialogBuilder.DialogBtnCallBack() {
                    @Override
                    public void dialogBtnOnClick() {

                    }
                });
                dialogBuilder.setFBFirstBtnText("拍一张");
                dialogBuilder.setFBLastBtnText("从相册中选择");
                dialogBuilder.setFBLastBtnClick(new FormBotomDefaultDialogBuilder.DialogBtnCallBack() {
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

    /**
     * 打开相册
     */
    private void openPhoto() {
        Intent photo = new Intent(Intent.ACTION_PICK, null);
        photo.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(photo, ActivityForResultConfig.OPEN_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ActivityForResultConfig.OPEN_PHOTO) {
                Uri uri = data.getData();
                String[] opj = {MediaStore.Images.Media.DATA};
                if (uri != null && opj != null) {
                    Cursor cursor = getActivity().managedQuery(uri, opj, null, null, null);
                    cursor.moveToFirst();
                    picPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                    if (picPath != null) {
                        Intent intent = new Intent(getActivity(), UpLoadHeadPic.class);
                        intent.putExtra("picPath", picPath);
                        startActivityForResult(intent, ActivityForResultConfig.UP_LOAD_HEAD);
                    }
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


}
