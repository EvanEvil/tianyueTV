package com.tianyue.tv.Activity.Live;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.tianyue.tv.Activity.BaseActivity;
import com.tianyue.tv.Adapter.LiveTabAdapter;
import com.tianyue.tv.Bean.LiveChatMessage;
import com.tianyue.tv.Config.ParamConfigKey;
import com.tianyue.tv.CustomView.Dialog.BarrageSettingDialog;
import com.tianyue.tv.Fragment.LiveChatFragment;
import com.tianyue.tv.Fragment.LiveGiftFragment;
import com.tianyue.tv.Interface.DmsMessageCallback;
import com.tianyue.tv.Interface.DmsMessageSendCallBack;
import com.tianyue.tv.MyApplication;
import com.tianyue.tv.R;
import com.tianyue.tv.Util.DmsUtil;
import com.tianyue.tv.Util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 播放界面
 * Created by hasee on 2016/8/16.
 */
public class LiveDetails extends BaseActivity implements
        View.OnClickListener,
        PLMediaPlayer.OnPreparedListener,
        PLMediaPlayer.OnInfoListener,
        PLMediaPlayer.OnErrorListener,
        PLMediaPlayer.OnCompletionListener,
        PLMediaPlayer.OnVideoSizeChangedListener{
    /**********
     * 通用控件
     **********/
    @BindView(R.id.live_details_full)
    ImageButton fullButton;
    @BindView(R.id.live_details_playView)
    SurfaceView playView;
    @BindView(R.id.LoadingView)
    View loadingView;
    @BindView(R.id.live_details_pause)
    ImageButton playPause;
    @BindView(R.id.live_details_back)
    ImageButton back;
    PLMediaPlayer mediaPlayer;
    /*********
     * 竖屏控件
     **********/
    TabLayout tabLayout;
    ViewPager viewPager;

    LiveChatFragment liveChatFragment;
    LiveGiftFragment liveGiftFragment;

    List<Fragment> fragmentList;
    List<String> tabTitleList;

    LiveTabAdapter liveTabAdapter;

    /*********
     * 横屏控件
     **********/
    private ImageButton settings;

    private ImageButton landSend;

    private EditText landInput;

    String playPath = ParamConfigKey.TEST_PATH;
    private boolean isLivePlay = false;
    private boolean isPort = true;
    private AVOptions options;
    private int codec = 0;
    AudioManager audioManager;
    BarrageSettingDialog dialog;
    int playViewWidth;
    int playViewHeight;

    /**************************初始化部分********************************/

    /**
     * 初始化 直播详情页面 控件
     */
    @Override
    protected void initView() {
        if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            isPort = true;
        } else {
            isPort = false;
        }
        Log.e(TAG, "initView: " + isPort);
        setContentView(R.layout.live_details_layout);
        initPlaySettings();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        playView.getHolder().addCallback(surfaceCallback);

        if (isPort) {
            initPortView();
        } else {
            initLandView();
        }
    }

    @Override
    protected boolean isKeepScreenNo() {
        return true;
    }

    @Override
    protected boolean isDefaultPort() {
        return false;
    }

    /**
     * 初始化 横屏 控件
     */
    private void initLandView() {
        Log.i(TAG, "initLandView: ");
        settings = (ImageButton) findViewById(R.id.live_details_full_setting);

        landSend = (ImageButton) findViewById(R.id.live_details_send);

        landInput = (EditText) findViewById(R.id.live_details_full_input);


        landSend.setOnClickListener(this);
        settings.setOnClickListener(this);
    }

    /**
     * 初始化 竖屏 控件
     */
    private void initPortView() {
        Log.i(TAG, "initPortView: ");
        tabLayout = (TabLayout) findViewById(R.id.live_details_tab);
        viewPager = (ViewPager) findViewById(R.id.live_details_viewPage);



        liveChatFragment = new LiveChatFragment();
        liveGiftFragment = new LiveGiftFragment();

        fragmentList = new ArrayList<>();
        fragmentList.add(liveChatFragment);
        fragmentList.add(liveGiftFragment);


        String[] title = getResources().getStringArray(R.array.live_title);
        tabTitleList = Arrays.asList(title);

        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.addTab(tabLayout.newTab().setText(tabTitleList.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(tabTitleList.get(1)));

        liveTabAdapter = new LiveTabAdapter(getSupportFragmentManager(), fragmentList, tabTitleList);
        viewPager.setAdapter(liveTabAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * 初始化播放设置
     */
    private void initPlaySettings() {
        MyApplication application = (MyApplication) getApplication();
        //是否开启硬解
        if (application.getSettings().isHardware()) {
            codec = 1;
        } else {
            codec = 0;
        }
        options = new AVOptions();
        //解码方式
        options.setInteger(AVOptions.KEY_MEDIACODEC, codec);
        //准备超时时间，包括创建资源、建立连接、请求码流等，单位是 ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        //读取视频超时时间
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);
        // 当前播放的是否为在线直播，如果是，则底层会有一些播放优化
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, 1);
        // 是否开启"延时优化"，只在在线直播流中有效
        options.setInteger(AVOptions.KEY_DELAY_OPTIMIZATION, 1);
        // 默认的缓存大小，单位是 ms 默认值2000
        options.setInteger(AVOptions.KEY_CACHE_BUFFER_DURATION, 2000);
        // 最大的缓存大小，单位是 ms 默认值4000
        options.setInteger(AVOptions.KEY_MAX_CACHE_BUFFER_DURATION, 3000);
        // 是否自动启动播放，如果设置为 1，则在调用 `prepareAsync` 或者 `setVideoPath` 之后自动启动播放，无需调用 `start()`
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 0);
    }

    /**
     * 播放器准备
     */
    private void playPrepare() {
        if (mediaPlayer != null) {
            mediaPlayer.setDisplay(playView.getHolder());
            return;
        }
        try {
            mediaPlayer = new PLMediaPlayer(this, options);
            mediaPlayer.setDataSource(playPath);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnInfoListener(this);
            mediaPlayer.setOnCompletionListener(this);
//            mediaPlayer.setOnVideoSizeChangedListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setDisplay(playView.getHolder());
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 重新连接
     */
    private void reconnect() {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDisplay(playView.getHolder());
            mediaPlayer.setDataSource(playPath);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /****************
     * Activity生命周期重写部分
     *********************/

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume: ");
        super.onResume();
        if (mediaPlayer != null && !isLivePlay) {
            reconnect();
        }
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause: ");
        super.onPause();
        if (mediaPlayer != null && isLivePlay) {
            mediaPlayer.pause();
            isLivePlay = false;
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

    }


    /**
     * 更新
     */
    private void updatePlayOrPauseView() {
        if (!isLivePlay) {
            playPause.setImageDrawable(getResources().getDrawable(R.mipmap.live_details_play));
        } else {
            playPause.setImageDrawable(getResources().getDrawable(R.mipmap.live_details_pause));
        }
    }

    /**
     * 获得当前屏幕亮度值 0--255
     */
    private int getScreenBrightness() {
        int screenBrightness = 255;
        try {
            screenBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception localException) {

        }
        return screenBrightness;
    }

    /**
     * 设置当前屏幕亮度的模式
     * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
     * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
     */
    private void setScreenMode(int paramInt) {
        try {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, paramInt);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    /**
     * 保存当前的屏幕亮度值，并使之生效
     */
    private void setScreenBrightness(int paramInt) {
        Window localWindow = getWindow();
        WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
        float f = paramInt / 255.0F;
        localLayoutParams.screenBrightness = f;
        localWindow.setAttributes(localLayoutParams);
    }

    /**
     * 设置当前屏幕亮度值 0--255
     */
    private void saveScreenBrightness(int paramInt) {
        try {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, paramInt);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }


    /**
     * 沉浸式体验
     *
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19 && !isPort) {
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


    /****************
     * 接口实现部分
     *********************/
    @Override
    @OnClick({R.id.live_details_full, R.id.live_details_pause, R.id.live_details_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.live_details_full:
                if (isPort) {
                    Log.e(TAG, "onClick: 横屏");
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    Log.e(TAG, "onClick: 竖屏");
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                break;
            case R.id.live_details_pause:
                if (mediaPlayer != null) {
                    if (isLivePlay) {
                        mediaPlayer.pause();
                        isLivePlay = false;
                    } else {
                        mediaPlayer.start();
                        isLivePlay = true;
                    }
                }
                updatePlayOrPauseView();
                break;
            case R.id.live_details_full_setting:
                if (dialog == null) {
                    dialog = new BarrageSettingDialog(this);
                    dialog.setSeekbarOnclickListener(seekBarChangeListener);
                    int count = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    dialog.setVolumeProgress(count);
                    dialog.setBrightnesseProgress(getScreenBrightness());
                }
                dialog.show();
                break;
            case R.id.live_details_back:
                if (isPort) {
                    finish();
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    isPort = true;
                }
                break;
            case R.id.live_details_send:


                break;
        }
    }


    /**
     * dialog 进度条监听
     */
    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (seekBar.getId() == R.id.barrage_setting_volume_seekbar) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }
            if (seekBar.getId() == R.id.barrage_setting_brightness_seekbar) {
                setScreenMode(0);
                saveScreenBrightness(progress);
                setScreenBrightness(progress);

            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    DialogInterface.OnClickListener dialogClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    dialog.dismiss();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    finish();
                    break;
            }
        }
    };


    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            playPrepare();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            playViewWidth = width;
            playViewHeight = height;
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };

    @Override
    public void onPrepared(PLMediaPlayer plMediaPlayer) {
        mediaPlayer.start();
        isLivePlay = true;
    }

    @Override
    public boolean onInfo(PLMediaPlayer plMediaPlayer, int what, int extra) {
        switch (what) {
            case PLMediaPlayer.MEDIA_INFO_UNKNOWN:
                showToast("未知消息");
                break;
            case PLMediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (loadingView.getVisibility() == View.GONE && loadingView != null) {
                    loadingView.setVisibility(View.VISIBLE);
                }

                break;
            case PLMediaPlayer.MEDIA_INFO_BUFFERING_END:
            case PLMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                if (loadingView.getVisibility() == View.VISIBLE && loadingView != null) {
                    loadingView.setVisibility(View.GONE);
                }
                break;
        }
        return true;
    }


    /**
     * false 错误未处理 提交给onCompletion
     * true  错误已处理 不会提交给onCompletion
     *
     * @param plMediaPlayer
     * @param errorCode
     * @return
     */
//    MEDIA_ERROR_UNKNOWN		        未知错误
//    ERROR_CODE_INVALID_URI		    无效的 URL
//    ERROR_CODE_IO_ERROR		        网络异常
//    ERROR_CODE_STREAM_DISCONNECTED    与服务器连接断开
//    ERROR_CODE_EMPTY_PLAYLIST	        空的播放列表
//    ERROR_CODE_404_NOT_FOUND	        播放资源不存在
//    ERROR_CODE_CONNECTION_REFUSED	    服务器拒绝连接
//    ERROR_CODE_CONNECTION_TIMEOUT		连接超时
//    ERROR_CODE_UNAUTHORIZED	      	未授权，播放一个禁播的流
//    ERROR_CODE_PREPARE_TIMEOUT		播放器准备超时
//    ERROR_CODE_READ_FRAME_TIMEOUT		读取数据超时
    @Override
    public boolean onError(PLMediaPlayer plMediaPlayer, int errorCode) {
        boolean errorDispose = false;
        switch (errorCode) {
            case PLMediaPlayer.MEDIA_ERROR_UNKNOWN:
                showToast("未知错误");
                errorDispose = true;
                break;
            case PLMediaPlayer.ERROR_CODE_INVALID_URI:
                showToast("无效的播放地址");
                errorDispose = false;
                break;
            case PLMediaPlayer.ERROR_CODE_IO_ERROR:
                if (Util.isNetWorkConnect(this)) {
                    reconnect();
                    showToast("重连中");
                } else {
                    showToast("网络异常");
                }
                errorDispose = true;
                break;
            case PLMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
                break;
            case PLMediaPlayer.ERROR_CODE_CONNECTION_REFUSED:
                break;
            case PLMediaPlayer.ERROR_CODE_UNAUTHORIZED:
                break;
            case PLMediaPlayer.ERROR_CODE_PREPARE_TIMEOUT:
            case PLMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
//                reconnect();
                showToast("连接超时");
                break;
            case PLMediaPlayer.ERROR_CODE_READ_FRAME_TIMEOUT:
                break;
        }
        return errorDispose;
    }

    @Override
    public void onCompletion(PLMediaPlayer plMediaPlayer) {
        finish();
    }

    @Override
    public void onVideoSizeChanged(PLMediaPlayer plMediaPlayer, int width, int height) {
        if (width != 0 && height != 0) {
            float ratioW = (float) width / (float) playViewWidth;
            float ratioH = (float) height / (float) playViewHeight;
            float ratio = Math.max(ratioW, ratioH);
            width = (int) Math.ceil((float) width / ratio);
            height = (int) Math.ceil((float) height / ratio);
            ViewGroup.LayoutParams params = playView.getLayoutParams();
            params.width = width;
            params.height = height;
            playView.setLayoutParams(params);
        }
    }

}
