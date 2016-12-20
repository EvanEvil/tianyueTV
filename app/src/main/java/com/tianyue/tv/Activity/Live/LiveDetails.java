package com.tianyue.tv.Activity.Live;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Spanned;
import android.text.TextPaint;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.tianyue.tv.Activity.BaseActivity;
import com.tianyue.tv.Adapter.LiveTabAdapter;
import com.tianyue.tv.Bean.LiveChatMessage;
import com.tianyue.tv.Config.ParamConfigKey;
import com.tianyue.tv.CustomView.Dialog.BarrageSettingDialog;
import com.tianyue.tv.Fragment.LiveChatFragment;
import com.tianyue.tv.Fragment.LiveGiftFragment;
import com.tianyue.tv.MyApplication;
import com.tianyue.tv.R;
import com.tianyue.tv.Util.DmsUtil;
import com.tianyue.tv.Util.Util;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

import static com.tianyue.tv.R.id.barrage_setting_size_seekbar;

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
        PLMediaPlayer.OnVideoSizeChangedListener, DmsUtil.SendMessageCallBack, DmsUtil.MessageCallBack {
    private static final int HIDDEN_LAND = 1;
    private static final int HIDDEN_PORT = 2;
    /**
     * 横屏顶部和底部控制器
     */
    private LinearLayout ll_land_top_controller;
    private LinearLayout ll_land_bottom_controller;
    /**
     * 竖屏顶部底部控制器
     */
    private RelativeLayout rl_port_top_controller;
    private LinearLayout ll_port_land_bottom_controller;
    /**
     * 横屏布局隐藏
     */


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HIDDEN_LAND:
                    hideLandController();

                    break;
                case HIDDEN_PORT:
                   hidePortController();
                    break;
            }
        }
    };
    /**
     * 当前即将显示的弹幕
     */
    private BaseDanmaku danmaku;
    /**
     * 弹幕大小
     */
    private int danmakuTextSize;
    /**
     * 弹幕透明度
     */
    private int danmakuAlpha;
    private SharedPreferences danmakuSp;
    /**
     * 弹幕位置(0上,1中,2下)
     *
     */
    private int danmakuPositon;

    private void hidePortController() {
        rl_port_top_controller.setVisibility(View.INVISIBLE);
        ll_port_land_bottom_controller.setVisibility(View.INVISIBLE);
        isshowMediaController = false;
    }

    private void hideLandController() {
        ll_land_top_controller.setVisibility(View.INVISIBLE);
        ll_land_bottom_controller.setVisibility(View.INVISIBLE);
        isshowMediaController = false;
    }

    //手势识别器
    private GestureDetector detector;
    private boolean isshowMediaController = true;
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

    String playPath = ParamConfigKey.TEST_PATH;
    private boolean isLivePlay = false;
    private boolean isPort = true;
    private AVOptions options;
    private int codec = 0;
    AudioManager audioManager;
    BarrageSettingDialog dialog;
    int playViewWidth;
    int playViewHeight;
    //Evan  弹幕的三个重要变量
    private boolean showDanmaku;

    private DanmakuView danmakuView;
    /**
     * DanmakuContext可以用于对弹幕的各种全局配置进行设定，如设置字体、设置最大显示行数等。这里我们并没有什么特殊的要求，因此一切都保持默认。
     */
    private DanmakuContext danmakuContext;
    private BaseDanmakuParser parser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };
    private HashMap<Integer, Integer> maxLinesPair;// 弹幕最大行数
    private HashMap<Integer, Boolean> overlappingEnablePair;// 设置是否重叠
    /**
     * 横屏发送弹幕
     */
    public ImageButton btn_land_sendDanmaku;
    /**
     * 横屏弹幕编辑框
     */
    public EditText et_landText;
    public DmsUtil dmsUtil;
    private String topic = "10085";
    private View.OnTouchListener surfaceviewOnTouchListener;
    private List<LiveChatMessage> messageList = new ArrayList<>();


    /**************************初始化部分********************************/

    /**
     * 初始化 直播详情页面 控件
     */
    @Override
    protected void initView() {
        setContentView(R.layout.live_details_layout);

        initdmsUtil();

//         spUtil = MyApplication.instance().getSpUtil();
//        if(spUtil==null){
//            spUtil = new SpUtil("danmakuConfig");
//            MyApplication.instance().setSpUtil(spUtil);
//        }



        if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            isPort = true;
        } else {
            isPort = false;
        }
        Log.e(TAG, "initView: " + isPort);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        initPlaySettings();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        playView.getHolder().addCallback(surfaceCallback);
        surfaceviewOnTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return true;
            }
        };
        playView.setOnTouchListener(surfaceviewOnTouchListener);
        if (isPort) {
            initPortView();
        } else {
            initLandView();

        }
        //2.实例化手势识别器，并且重写双击，点击，长按
        detector = new GestureDetector(this, new MySimpleOnGestureListener());


    }
//    @Subscribe(threadMode = ThreadMode.MAIN,sticky = false,priority = 0)
//    public void getMsg(Msg msg){
//        String msg1 = msg.getMsg();
//        addDanmaku(msg1,true);
//        Toast.makeText(getApplicationContext(),"收到消息："+msg1,Toast.LENGTH_SHORT).show();
//    }

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
        danmakuSp = getSharedPreferences("danmakuConfig", MODE_PRIVATE);
        //初始化横屏弹幕大小值
        danmakuTextSize = danmakuSp.getInt("danmakuSize", 3);
        Log.e(TAG,"读取sp的值："+danmakuTextSize);
        danmakuAlpha = danmakuSp.getInt("danmakuAlpha",180);
        Log.e(TAG,"读取sp弹幕透明度的值："+danmakuAlpha);
        //弹幕默认位置:顶部弹幕
        danmakuPositon = 0;
        settings = (ImageButton) findViewById(R.id.live_details_full_setting);
        ll_land_top_controller = (LinearLayout) findViewById(R.id.ll_land_top_controller);
        ll_land_bottom_controller = (LinearLayout) findViewById(R.id.ll_land_bottom_controller);

        et_landText = (EditText) findViewById(R.id.et_landText);
        btn_land_sendDanmaku = (ImageButton) findViewById(R.id.live_details_send);

        //监听按钮
        btn_land_sendDanmaku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = et_landText.getText().toString();
                if (message.equals("")) {

                    showToast("发送的消息不能为空哦");
                    return;
                }
                sendDanmaku();
            }
        });
        settings.setOnClickListener(this);
    }
    class MyOnFocusChangeListener implements View.OnFocusChangeListener{

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){   //获得焦点
                Toast.makeText(getApplicationContext(),"获得焦点",Toast.LENGTH_SHORT).show();
                        mHandler.removeMessages(HIDDEN_LAND);
            }else{  //失去焦点
                Toast.makeText(getApplicationContext(),"失去焦点",Toast.LENGTH_SHORT).show();
                        hideLandController();
            }
        }
    }

    /**
     * 初始化 竖屏 控件
     */
    private void initPortView() {
        tabLayout = (TabLayout) findViewById(R.id.live_details_tab);
        viewPager = (ViewPager) findViewById(R.id.live_details_viewPage);
        rl_port_top_controller = (RelativeLayout) findViewById(R.id.rl_port_land_top_controller);
        ll_port_land_bottom_controller = (LinearLayout) findViewById(R.id.ll_port_land_bottom_controller);

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

    @Override
    protected void onStart() {
        Log.e(TAG,"onStart");
        super.onStart();

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
        if (danmakuView != null && danmakuView.isPrepared() && danmakuView.isPaused()) {
            danmakuView.resume();


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
        if (danmakuView != null && danmakuView.isPrepared()) {
            danmakuView.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        showDanmaku = false;
        if (danmakuView != null) {
            danmakuView.release();
            danmakuView = null;
        }
        if(mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
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
                    //获取当前系统的音量
                    int count = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    //设置音量
                    dialog.setVolumeProgress(count);
                    //设置当前亮度
                    dialog.setBrightnesseProgress(getScreenBrightness());
                    //设置当前弹幕透明度
                    dialog.setAlphaProgress(danmakuAlpha);
                    //获取并设置seekbar的弹幕值


                    dialog.setDanmakuSizeProgress(danmakuTextSize);


                    //设置当前弹幕位置
                    dialog.setDanmakuPosition(danmakuPositon);//设置被选中
                    dialog.setRadioGroupCheckChangeListener(mOnCheckedChangeListener);

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
        }
    }
    RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {


        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.barrage_setting_location_top:
                    Toast.makeText(getApplicationContext(),"顶部点击了",Toast.LENGTH_SHORT).show();
                    danmakuPositon = 0;
                    break;
                case R.id.barrage_setting_location_mid:
                    Toast.makeText(getApplicationContext(),"中间点击了",Toast.LENGTH_SHORT).show();
                    danmakuPositon =  1;
                    break;
                case R.id.barrage_setting_location_bottom:
                    Toast.makeText(getApplicationContext(),"底部点击了",Toast.LENGTH_SHORT).show();
                    danmakuPositon = 2;
                    break;
            }
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) danmakuView.getLayoutParams();
            layoutParams.setMargins(0,0,0,0);

            setDanmakuPosition();
        }
    };

    private void setDanmakuPosition() {
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        Log.e(TAG,"宽:"+width+",高:"+height);//宽:1794,高:1080
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(danmakuPositon == 0){
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) danmakuView.getLayoutParams();

                    layoutParams.bottomMargin = 600;
                    danmakuView.setLayoutParams(layoutParams);

                }else if(danmakuPositon == 1){
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) danmakuView.getLayoutParams();
                    layoutParams.topMargin =300;
                    layoutParams.bottomMargin = 300;

                    danmakuView.setLayoutParams(layoutParams);
                }else if(danmakuPositon == 2){
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) danmakuView.getLayoutParams();
                    layoutParams.topMargin = 800;

                    danmakuView.setLayoutParams(layoutParams);
                }


            }
        });


    }

    /**
     * 获取横屏editText的消息并发送弹幕
     */
    private void sendDanmaku() {

        String danmakuMsg = et_landText.getText().toString();
        dmsUtil.sendMessage(danmakuMsg);
        addDanmaku(danmakuMsg, true);

    }

    private void initdmsUtil() {

        dmsUtil = MyApplication.instance().getDmsUtil();
        if(dmsUtil == null){
            dmsUtil = DmsUtil.instance(this);
            dmsUtil.initDMS(topic);
            dmsUtil.connectDMS();
            dmsUtil.setSendMessageCallBack(this);
            dmsUtil.setMessageCallBack(this);
            MyApplication.instance().setDmsUtil(dmsUtil);
        }

    }


    /**
     * dialog 进度条监听
     */
    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (seekBar.getId()){
                case R.id.barrage_setting_volume_seekbar://音量
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                    break;
                case R.id.barrage_setting_brightness_seekbar://亮度
                    setScreenMode(0);
                    saveScreenBrightness(progress);
                    setScreenBrightness(progress);
                    break;
                case R.id.barrage_setting_alpha_seekbar://弹幕透明度
                    danmakuAlpha = progress;
                    Log.e(TAG,"拖动修改弹幕透明度："+progress);
                    break;
                case barrage_setting_size_seekbar://弹幕大小
                        //修改
                    danmakuTextSize = progress;


                    Log.e(TAG,"拖动修改弹幕大小："+progress);
                    break;

            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            switch (seekBar.getId()){
                case R.id.barrage_setting_size_seekbar:
                    //保存当前size值
                    Log.e(TAG,"保存当前值："+seekBar.getProgress());
                    getSharedPreferences("danmakuConfig", MODE_PRIVATE).edit().putInt("danmakuSize",seekBar.getProgress()).commit();
                    break;
                case R.id.barrage_setting_alpha_seekbar:
                    danmakuSp.edit().putInt("danmakuAlpha",seekBar.getProgress()).commit();
                    Log.e(TAG,"保存弹幕透明度值："+seekBar.getProgress());
                    break;
            }
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
        if (isPort) {
            mHandler.sendEmptyMessageDelayed(HIDDEN_PORT, 5000);
        } else {
            mHandler.sendEmptyMessageDelayed(HIDDEN_LAND, 5000);
        }


        if (isPort != true) {
            //Evan
            MyOnFocusChangeListener myOnFocusChangeListener = new MyOnFocusChangeListener();
            et_landText.setOnFocusChangeListener(myOnFocusChangeListener);
            danmakuView = (DanmakuView) findViewById(R.id.danmaku_view);
            danmakuView.enableDanmakuDrawingCache(true);

            danmakuView.start();
            danmakuView.setCallback(new DrawHandler.Callback() {
                @Override
                public void prepared() {

                    showDanmaku = true;
                    danmakuView.start();
                    Log.e("liveDetails", "开启线程");
                    generateSomeDanmaku();
                }

                @Override
                public void updateTimer(DanmakuTimer timer) {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {

                }

                @Override
                public void drawingFinished() {

                }
            });

            danmakuContext = DanmakuContext.create();
            initDanmakuStyle();
            danmakuView.prepare(parser, danmakuContext);//回调重写的prepared() 方法


        }

    }

    /**
     * 初始化弹幕样式
     */
    private void initDanmakuStyle() {
        // 设置最大行数,从右向左滚动(有其它方向可选)
        maxLinesPair = new HashMap<>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 2);

        //设置是否禁止重叠
        HashMap<Integer,Boolean> overlappingEnablePair = new HashMap<>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
        danmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_NONE)
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.2f)//越大速度越慢
                .setScaleTextSize(1.2f)
                .setCacheStuffer(new BackgroundCacheStuffer(), mCacheStufferAdapter)
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);



    }
    private BaseCacheStuffer.Proxy mCacheStufferAdapter = new BaseCacheStuffer.Proxy() {

        @Override
        public void prepareDrawing(final BaseDanmaku danmaku, boolean fromWorkerThread) {
//            if (danmaku.text instanceof Spanned) { // 根据你的条件检查是否需要需要更新弹幕
//            }
        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {
            // TODO 重要:清理含有ImageSpan的text中的一些占用内存的资源 例如drawable
            if (danmaku.text instanceof Spanned) {
                danmaku.text = "";
            }
        }
    };
    /**
     * 绘制背景(自定义弹幕样式)
     */
    private class BackgroundCacheStuffer extends SpannedCacheStuffer {
        // 通过扩展SimpleTextCacheStuffer或SpannedCacheStuffer个性化你的弹幕样式
        final Paint paint = new Paint();

        @Override
        public void measure(BaseDanmaku danmaku, TextPaint paint, boolean fromWorkerThread) {
//            danmaku.padding = 20;  // 在背景绘制模式下增加padding
            super.measure(danmaku, paint, fromWorkerThread);
        }

        @Override
        public void drawBackground(BaseDanmaku danmaku, Canvas canvas, float left, float top) {
            paint.setAntiAlias(true);
            if (!danmaku.isGuest) {
                paint.setColor(Color.parseColor("red"));//粉红 楼主
            }
            if (danmaku.isGuest) {//如果是赞 就不要设置背景
                paint.setColor(Color.TRANSPARENT);
            }

        }
    }

    /**
     * sp转px的方法。
     */
    public int sp2px(float spValue) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 随机生成一些弹幕内容以供测试
     */
    private void generateSomeDanmaku() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (showDanmaku) {
                    int time = new Random().nextInt(800);
                    String content = "弹幕测试" + time + time;
                    addDanmaku(content, false);
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 添加弹幕
     *
     * @param content    消息内容
     * @param withBorder 这个参数用于指定弹幕消息是否带有边框，这样才好将自己发送的弹幕和别人发送的弹幕进行区分。
     */
    public void addDanmaku(String content, boolean withBorder) {

        //TYPE_SCROLL_RL表示这是一条从右向左滚动的弹幕
        //BaseDanmaku danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL, danmakuContext);
        //设置字体颜色
        danmaku.textColor = Color.argb(danmakuAlpha,255,255,255);

        danmaku.text = content;
        danmaku.priority = 1;//0 表示可能会被各种过滤器过滤并隐藏显示 //1 表示一定会显示, 一般用于本机发送的弹幕
        danmaku.padding = 5;

        //设置弹幕字体大小
        danmaku.textSize = sp2px(danmakuTextSize+13);

        danmaku.setTime(danmakuView.getCurrentTime());
        if (withBorder) {
            danmaku.textColor = Color.RED;
            danmaku.borderColor = Color.GREEN;
        }
        //设置弹幕透明度
        danmakuView.setAlpha((float) (danmakuAlpha/255.0));



        danmakuView.addDanmaku(danmaku);
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

    @Override
    public void onSuccess(String result) {
        if(isPort){

        }else{
            et_landText.setText("");
        }

    }

    @Override
    public void onFailure(String result) {

    }

    @Override
    public void onMessage(String result) {
        String[] receives = result.split("\\{aodiandmssplit\\}");
        String nickName = receives[0];
        String sendTime = receives[1];
        String message = receives[2];

        LiveChatMessage chatMessage = new LiveChatMessage();
        chatMessage.setNickName(DmsUtil.unescape(nickName));
        chatMessage.setSendTime(sendTime);
        chatMessage.setMessage(message);
        if (messageList.size() >= 20) {
            for (int i = 0; i < 5; i++) {
                messageList.remove(i);
            }
        }
        messageList.add(chatMessage);
        EventBus.getDefault().post(chatMessage);
    }


    private class MySimpleOnGestureListener implements GestureDetector.OnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (isshowMediaController) {
                //隐藏
                hideMediaController();


            } else {
                //显示
                showMediaController();
                //发消息隐藏
               if(isPort){
                   mHandler.sendEmptyMessageDelayed(HIDDEN_PORT,5000);
               }else{
                   mHandler.sendEmptyMessageDelayed(HIDDEN_LAND,5000);
               }
            }

            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }

    private void showMediaController() {

        isshowMediaController = true;

        //显示控制器
        if(isPort){
            showPortController();
            //把隐藏消息移除
            mHandler.removeMessages(HIDDEN_PORT);
        }else{
            showLandController();
            mHandler.removeMessages(HIDDEN_LAND);
        }
    }

    private void showLandController() {
        ll_land_top_controller.setVisibility(View.VISIBLE);
         ll_land_bottom_controller.setVisibility(View.VISIBLE);
        isshowMediaController = true;
    }

    private void showPortController() {
        rl_port_top_controller.setVisibility(View.VISIBLE);
        ll_port_land_bottom_controller.setVisibility(View.VISIBLE);
        isshowMediaController = true;
    }

    private void hideMediaController() {
        //隐藏控制器
        if(isPort){
            hidePortController();
            //把隐藏消息移除
            mHandler.removeMessages(HIDDEN_PORT);
        }else{
            hideLandController();
            mHandler.removeMessages(HIDDEN_LAND);
        }

    }
}
