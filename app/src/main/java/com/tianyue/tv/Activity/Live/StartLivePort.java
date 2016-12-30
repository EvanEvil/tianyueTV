package com.tianyue.tv.Activity.Live;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qiniu.android.dns.DnsManager;
import com.qiniu.android.dns.IResolver;
import com.qiniu.android.dns.NetworkInfo;
import com.qiniu.android.dns.http.DnspodFree;
import com.qiniu.android.dns.local.AndroidDnsServer;
import com.qiniu.android.dns.local.Resolver;
import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.CameraStreamingSetting;
import com.qiniu.pili.droid.streaming.MediaStreamingManager;
import com.qiniu.pili.droid.streaming.MicrophoneStreamingSetting;
import com.qiniu.pili.droid.streaming.StreamStatusCallback;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;
import com.qiniu.pili.droid.streaming.widget.AspectFrameLayout;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tencent.connect.share.QQShare;
import com.tencent.open.utils.HttpUtils;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.tianyue.mylibrary.view.ChangeToolbar;
import com.tianyue.pushlive.config.Config;
import com.tianyue.pushlive.ui.CameraPreviewFrameView;
import com.tianyue.pushlive.ui.RotateLayout;
import com.tianyue.tv.Activity.BaseActivity;
import com.tianyue.tv.Adapter.LiveTabAdapter;
import com.tianyue.tv.Bean.Broadcast;
import com.tianyue.tv.Bean.User;
import com.tianyue.tv.Fragment.LiveGiftFragment;
import com.tianyue.tv.Fragment.StartLiveChatFragment;
import com.tianyue.tv.MyApplication;
import com.tianyue.tv.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 直播界面
 * Created by hasee on 2016/8/16.
 */
public class StartLivePort extends BaseActivity implements
        CameraPreviewFrameView.Listener,
        StreamingStateChangedListener,
        StreamStatusCallback {
    private boolean flashState = false;//闪光灯状态 true为开启 false为关闭
    private final int PICK_CODE = 1;
    private boolean isEncOrientationPort = true; //是否为横竖屏
    private boolean mOrientationChanged = false;
    private boolean liveState = false;
    private String publishUrlFromServer = "testUrl";
    private StreamingProfile mProfile;
    private RotateLayout rotateLayout;
    private CameraStreamingSetting cameraStreamingSetting;
    private MediaStreamingManager mediaStreamingManager;
    private int mCurrentCamFacingIndex;
    private Switcher mSwitcher = new Switcher();
    private String StreamingState;

    /***************
     * Handler消息处理
     ***************/
    //直播停止时控件处理
    private final int VIEW_STOP = 101;
    //直播开始时控件处理
    private final int VIEW_START = 102;
    //手势右划时界面处理
    private final int VIEW_LEFT_MOVE = 103;
    //麦克风相关设置
    private MicrophoneStreamingSetting mMicrophoneStreamingSetting;
    String path = null;//相册图片路径
    @BindView(R.id.start_live_stream_state)
    TextView streamState;
    @BindView(R.id.start_live_play)
    ImageButton start;
    @BindView(R.id.start_live_orientation)
    ImageButton orientation;
    @BindView(R.id.start_live_camera)
    ImageButton camera;
    @BindView(R.id.start_live_flash)
    ImageButton flash;
    @BindView(R.id.start_live_share)
    ImageButton share;
    @BindView(R.id.start_live_chat)
    ImageButton chat;
    @BindView(R.id.start_live_layout_column)
    LinearLayout column;
    @BindView(R.id.start_live_layout_bottom)
    LinearLayout bottom;
    @BindView(R.id.start_live_layout_tab)
    TabLayout tab;
    @BindView(R.id.start_live_layout_viewPage)
    ViewPager viewPager;
    @BindView(R.id.start_live_layout_toolbar)
    ChangeToolbar toolbar;
    @BindView(R.id.start_live_layout_number)
    TextView lookerNumber;
    @BindView(R.id.start_live_layout_fans)
    TextView fans;
    User user;
    //腾讯接口
    Tencent mTencent;
    String APP_ID = "1105729532";


    private final int TAKE_PHOTO_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        } else {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        super.onCreate(savedInstanceState);
        if (Config.SCREEN_ORIENTATION == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            isEncOrientationPort = true;
        } else if (Config.SCREEN_ORIENTATION == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            isEncOrientationPort = false;
        }
        setRequestedOrientation(Config.SCREEN_ORIENTATION);
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case VIEW_START:
                    liveState = true;
                    start.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.pause));
                    liveStateChange(user.getId(), "1");
                    break;
                case VIEW_STOP:
                    liveState = false;
                    start.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.play));
                    liveStateChange(user.getId(), "0");
                    break;
                case VIEW_LEFT_MOVE:

                    break;
            }

        }
    };

    /**
     * 取消默认为竖屏
     *
     * @return
     */
    @Override
    protected boolean isDefaultPort() {
        return false;
    }

    /**
     * 开启屏幕常亮
     *
     * @return
     */
    @Override
    protected boolean isKeepScreenNo() {
        return true;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initView() {
        setContentView(R.layout.start_live_layout);
//        Intent intent = getIntent();
//        if (intent != null) {
//            Broadcast broadcast = intent.getParcelableExtra("broad");
//            if (broadcast != null) {
//                fans.setText(broadcast.getFocusNum());
//                lookerNumber.setText(broadcast.getOnlineNum());
//            }
//        }
        checkPermission();
    }

    private void initCamera(){
        toolbar.setNavigationOnClickListener(v -> finish());
        user = MyApplication.instance().getUser();
        tab.setTabMode(TabLayout.MODE_FIXED);
        String[] tabs = getResources().getStringArray(R.array.start_live_tab);
        List<String> list = Arrays.asList(tabs);
        for (String s : list) {
            tab.addTab(tab.newTab().setText(s));
        }
        List<Fragment> fragments = new ArrayList<>();
        StartLiveChatFragment startLiveChatFragment = new StartLiveChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString("topic", user.getId());
        startLiveChatFragment.setArguments(bundle);
        fragments.add(startLiveChatFragment);
        fragments.add(new LiveGiftFragment());

        viewPager.setAdapter(new LiveTabAdapter(getSupportFragmentManager(), fragments, list));
        tab.setupWithViewPager(viewPager);

        publishUrlFromServer = user.getLive_streaming_address();
        //腾讯相关初始化
        mTencent = Tencent.createInstance(APP_ID, getApplicationContext());
        AspectFrameLayout afl = (AspectFrameLayout) findViewById(R.id.cameraPreview_afl);

        // Decide FULL screen or real size
        afl.setShowMode(AspectFrameLayout.SHOW_MODE.FULL);
        CameraPreviewFrameView cameraPreviewFrameView =
                (CameraPreviewFrameView) findViewById(R.id.cameraPreview_surfaceView);
        cameraPreviewFrameView.setListener(this);
        initLive(afl, cameraPreviewFrameView);
    }
    /**
     * 初始化直播预览界面
     */
    private void initLive(AspectFrameLayout afl, CameraPreviewFrameView glSurfaceView) {
        //AVProfile优先级高于Quality
        StreamingProfile.AudioProfile aProfile = new StreamingProfile.AudioProfile(44100, 96 * 1024);
        StreamingProfile.VideoProfile vProfile = new StreamingProfile.VideoProfile(30, 1920 * 1080, 48);
        StreamingProfile.AVProfile avProfile = new StreamingProfile.AVProfile(vProfile, aProfile);
        mProfile = new StreamingProfile();
        try {
            mProfile.setPublishUrl(publishUrlFromServer);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        try {
            mProfile.setVideoQuality(StreamingProfile.VIDEO_QUALITY_HIGH3)//设置视频质量参数
                    //设置音频质量参数
                    .setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM2)
                    //                .setPreferredVideoEncodingSize(960, 544)
                    .setEncodingSizeLevel(Config.ENCODING_LEVEL)
                    //设置RCMode  BITRATE_PRIORITY为码率优先   QUALITY_PRIORITY为质量优先
                    .setEncoderRCMode(StreamingProfile.EncoderRCModes.QUALITY_PRIORITY)
                    //构造音频及视频参数
                    .setAVProfile(avProfile)
                    //自定义Dns解析器
                    .setDnsManager(getMyDnsManager())
                    .setPublishUrl(publishUrlFromServer)
                    //推流信息的反馈频率 每3秒一次
                    .setStreamStatusConfig(new StreamingProfile.StreamStatusConfig(3))
                    //设置横竖屏方向
                    //  .setEncodingOrientation(StreamingProfile.ENCODING_ORIENTATION.PORT)
                    .setSendingBufferProfile(new StreamingProfile.SendingBufferProfile(0.2f, 0.8f, 3.0f, 20 * 1000));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        cameraStreamingSetting = new CameraStreamingSetting();
        CameraStreamingSetting.CAMERA_FACING_ID cameraFacingId = chooseCameraFacingId();

        /**
         - CameraStreamingSetting.FOCUS_MODE_CONTINUOUS_PICTURE // 自动对焦（Picture）
         - CameraStreamingSetting.FOCUS_MODE_CONTINUOUS_VIDEO   // 自动对焦（Video）
         - CameraStreamingSetting.FOCUS_MODE_AUTO               // 手动对焦
         */
        cameraStreamingSetting
                //设置为后置摄像头
                .setCameraId(Camera.CameraInfo.CAMERA_FACING_BACK)
                //设置自动对焦
                .setContinuousFocusModeEnabled(true)
                .setFocusMode(CameraStreamingSetting.FOCUS_MODE_CONTINUOUS_VIDEO)
//                .setFocusMode(CameraStreamingSetting.FOCUS_MODE_AUTO)
//                .setCameraFacingId(cameraFacingId)
                //当模式为手动对焦时,3000毫秒后恢复自动对焦模式
                .setBuiltInFaceBeautyEnabled(true)
                .setResetTouchFocusDelayInMs(3000)
                .setCameraPrvSizeLevel(CameraStreamingSetting.PREVIEW_SIZE_LEVEL.SMALL)
                .setCameraPrvSizeRatio(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_16_9)
                //设置高分辨率推流
                .setRecordingHint(false)
                .setFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(1.0f, 1.0f, 0.8f))
                .setVideoFilter(CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_BEAUTY);

        mMicrophoneStreamingSetting = new MicrophoneStreamingSetting();
        //不开启蓝牙支持
        mMicrophoneStreamingSetting.setBluetoothSCOEnabled(false);

        mediaStreamingManager = new MediaStreamingManager(this, afl, glSurfaceView, AVCodecType.SW_VIDEO_WITH_SW_AUDIO_CODEC);
        mediaStreamingManager.prepare(cameraStreamingSetting, mProfile);
        mediaStreamingManager.setStreamingStateListener(this);
        mediaStreamingManager.setStreamStatusCallback(this);
    }


    @OnClick({R.id.start_live_play, R.id.start_live_camera, R.id.start_live_chat,
            R.id.start_live_flash, R.id.start_live_orientation, R.id.start_live_share})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_live_play:
                if (!liveState) {
                    mediaStreamingManager.startStreaming();
                } else {
                    mediaStreamingManager.stopStreaming();
                }
                break;
            case R.id.start_live_camera:
                handler.removeCallbacks(mSwitcher);
                handler.postDelayed(mSwitcher, 100);
//                if (cameraStreamingSetting.getReqCameraId() == Camera.CameraInfo.CAMERA_FACING_BACK) {
//                    camera.setText("后摄像头");
//                }else {
//                    camera.setText("前摄像头");
//                }
                break;
            case R.id.start_live_flash:
                this.runOnUiThread(() -> {
                    if (!flashState) {
                        mediaStreamingManager.turnLightOn();
                        flashState = true;
                    } else {
                        mediaStreamingManager.turnLightOff();
                        flashState = false;
                    }
                });
                break;
            case R.id.start_live_orientation:
                mOrientationChanged = true;
                isEncOrientationPort = !isEncOrientationPort;
                mProfile.setEncodingOrientation(isEncOrientationPort ? StreamingProfile.ENCODING_ORIENTATION.PORT : StreamingProfile.ENCODING_ORIENTATION.LAND);
                mediaStreamingManager.setStreamingProfile(mProfile);
                setRequestedOrientation(isEncOrientationPort ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                runOnUiThread(() -> {
                    mediaStreamingManager.notifyActivityOrientationChanged();
//                        if (isEncOrientationPort) {
//                            orientation.setText("切换横屏");
//                        } else {
//                            orientation.setText("切换竖屏");
//                        }
                });
                mediaStreamingManager.stopStreaming();
                break;
            case R.id.start_live_share:
                User user = MyApplication.instance().getUser();
                if (user != null) {
                    shareQQ(user.getNickName());
                }
                break;
            case R.id.start_live_chat:
                if (viewPager.getVisibility() == View.VISIBLE) {
                    viewPager.setVisibility(View.GONE);
                    chat.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.start_live_show));
                } else {
                    viewPager.setVisibility(View.VISIBLE);
                    chat.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.start_live_gone));
                }
                break;
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "onConfigurationChanged: " + newConfig.orientation);
        int width = column.getWidth();
        int height = column.getHeight();
        Log.i(TAG, "onConfigurationChanged: width" + width + "height" + height);
        ViewGroup.LayoutParams params = column.getLayoutParams();
        ViewGroup.LayoutParams bottomParams = bottom.getLayoutParams();
        params.height = width;
        params.width = height;
        int portHeightDimen = (int) getResources().getDimension(R.dimen.y312);
        int landHeightDimen = (int) getResources().getDimension(R.dimen.y191);
        if (Configuration.ORIENTATION_PORTRAIT == newConfig.orientation) {
            column.setOrientation(LinearLayout.VERTICAL);
            bottomParams.height = portHeightDimen;
        } else if (Configuration.ORIENTATION_LANDSCAPE == newConfig.orientation) {
            column.setOrientation(LinearLayout.HORIZONTAL);
            bottomParams.height = landHeightDimen;
        }
        column.setLayoutParams(params);
        bottom.setLayoutParams(bottomParams);
    }

    //自定义DNS解析
    private static DnsManager getMyDnsManager() {
        IResolver r0 = new DnspodFree();
        IResolver r1 = AndroidDnsServer.defaultResolver();
        IResolver r2 = null;
        try {
            r2 = new Resolver(InetAddress.getByName("119.29.29.29"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return new DnsManager(NetworkInfo.normal, new IResolver[]{r0, r1, r2});
    }


    private CameraStreamingSetting.CAMERA_FACING_ID chooseCameraFacingId() {
        if (CameraStreamingSetting.hasCameraFacing(CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_3RD)) {
            return CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_3RD;
        } else if (CameraStreamingSetting.hasCameraFacing(CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT)) {
            return CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT;
        } else {
            return CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_BACK;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        mediaStreamingManager.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
        mediaStreamingManager.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        mediaStreamingManager.destroy();
    }


    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        mediaStreamingManager.doSingleTapUp((int) e.getX(), (int) e.getY());
        return false;
    }

    @Override
    public boolean onZoomValueChanged(float factor) {
        return false;
    }

    @Override
    public void onStateChanged(final StreamingState streamingState, Object o) {
        switch (streamingState) {
            case PREPARING:
                break;
            case READY:
                StreamingState = "直播准备";
                break;
            case CONNECTING:
                break;
            case STREAMING:
                StreamingState = "正在直播";
                handler.removeMessages(VIEW_START);
                handler.sendEmptyMessage(VIEW_START);
                showToast("开始直播");
                break;
            case SHUTDOWN:
                Log.i(TAG, "onStateChanged: " + "关闭直播流");
                if (mOrientationChanged) {
                    mOrientationChanged = false;
                    mediaStreamingManager.startStreaming();
                } else {
                    handler.removeMessages(VIEW_STOP);
                    handler.sendEmptyMessage(VIEW_STOP);
                    StreamingState = "直播停止";
                    showToast("直播停止");
                }
                break;
            case IOERROR:
                StreamingState = "Stream Exception";
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("url地址有误");
                    }
                });
                break;
            case UNKNOWN:
                break;
            case SENDING_BUFFER_EMPTY:
                break;
            case SENDING_BUFFER_FULL:
                break;
            case AUDIO_RECORDING_FAIL:
                break;
            case OPEN_CAMERA_FAIL:
                StreamingState = "打开相机失败";
                break;
            case DISCONNECTED:

                break;
            case INVALID_STREAMING_URL:

                break;
            case UNAUTHORIZED_STREAMING_URL:

                break;
            case CAMERA_SWITCHED:


                break;
            case TORCH_INFO:
                Log.i(TAG, "onStateChanged: " + streamingState.toString());
                break;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                streamState.setText(StreamingState);
            }
        });
    }

    @Override
    public void notifyStreamStatusChanged(StreamingProfile.StreamStatus streamStatus) {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

    }

    private void shareQQ(String nickName) {
        Log.i(TAG, "shareQQ: 进入");
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, nickName + "的直播");
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "以视频直播为载体体现匠人艺术价值以及艺术过程的平台。");
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://www.tianyue.tv");
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "天越TV");
        mTencent.shareToQQ(StartLivePort.this, params, new BaseUiListener());
    }

    /***************************
     * 请求数据接口
     *****************************/

    private void liveStateChange(String uid, String zid) {
        OkHttpUtils.post().url("http://www.tianyue.tv/mobileLivebutton")
                .addParams("uid", uid)
                .addParams("zid", zid).build().execute(new Callback() {
            @Override
            public Object parseNetworkResponse(Response response) throws IOException {
                Log.i(TAG, "parseNetworkResponse: 进入反馈");
                String result = response.body().string();
                try {
                    JSONObject object = new JSONObject(result);
                    if (object != null) {
                        String ret = object.optString("ret");
                        if (ret != null) {
                            Log.i(TAG, "parseNetworkResponse: " + ret);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case TAKE_PHOTO_REQUEST_CODE:
                if(grantResults.length >0 &&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    initCamera();
                    //用户同意授权
                }else{
                    //用户拒绝授权
                    finish();
                }
                break;
        }
    }

    /**
     * 检查权限是否开启
     */
    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                                          TAKE_PHOTO_REQUEST_CODE);
        } else{
            initCamera();
        }
    }

//    public void call(View v) {
//        //检查权限
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
//            //进入到这里代表没有权限.
//
//            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CALL_PHONE)){
//                //已经禁止提示了
//                showToast("您已禁止该权限，需要重新开启。");
//            }else{
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE);
//
//            }
//
//        } else {
//            initCamera();
//        }
//    }
    /*************************************
     * 实现接口类
     ********************************************/
    //改变前后摄像头
    private class Switcher implements Runnable {
        @Override
        public void run() {
            mCurrentCamFacingIndex = (mCurrentCamFacingIndex + 1) % CameraStreamingSetting.getNumberOfCameras();
            CameraStreamingSetting.CAMERA_FACING_ID facingId;
            if (mCurrentCamFacingIndex == CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_BACK.ordinal()) {
                facingId = CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_BACK;
            } else if (mCurrentCamFacingIndex == CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT.ordinal()) {
                facingId = CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT;
            } else {
                facingId = CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_3RD;
            }
            Log.i(TAG, "switchCamera:" + facingId);
            mediaStreamingManager.switchCamera(facingId);
        }
    }

    private class BaseUiListener implements IUiListener {
        protected void doComplete(JSONObject values) {
        }

        @Override
        public void onComplete(Object o) {

        }

        @Override
        public void onError(UiError e) {
            Log.i(TAG, "onError:" + "code:" + e.errorCode + ", msg:"
                    + e.errorMessage + ", detail:" + e.errorDetail);
        }

        @Override
        public void onCancel() {
            Log.i(TAG, "onCancel: ");
        }
    }

    private class BaseApiListener implements IRequestListener {
        protected void doComplete(JSONObject response, Object state) {

        }

        public void onUnknowException(Exception e, Object state) {

            // 出现未知错误时会触发此异常
        }

        @Override
        public void onComplete(JSONObject jsonObject) {

        }

        @Override
        public void onIOException(IOException e) {

        }

        @Override
        public void onMalformedURLException(MalformedURLException e) {

        }

        @Override
        public void onJSONException(JSONException e) {

        }

        @Override
        public void onConnectTimeoutException(ConnectTimeoutException e) {

        }

        @Override
        public void onSocketTimeoutException(SocketTimeoutException e) {

        }

        @Override
        public void onNetworkUnavailableException(HttpUtils.NetworkUnavailableException e) {

        }

        @Override
        public void onHttpStatusException(HttpUtils.HttpStatusException e) {

        }

        @Override
        public void onUnknowException(Exception e) {

        }
    }
}
