package com.tianyue.tv.Activity.Live;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Spanned;
import android.text.TextPaint;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.squareup.picasso.Picasso;
import com.tianyue.tv.Activity.BaseActivity;
import com.tianyue.tv.Adapter.LiveTabAdapter;
import com.tianyue.tv.Bean.EventBusBean.EventMsg;
import com.tianyue.tv.Bean.LiveChatMessage;
import com.tianyue.tv.Bean.LiveHomeColumn;
import com.tianyue.tv.Config.InterfaceUrl;
import com.tianyue.tv.Config.ParamConfigKey;
import com.tianyue.tv.CustomView.Dialog.BarrageSettingDialog;
import com.tianyue.tv.Fragment.LiveChatFragment;
import com.tianyue.tv.Fragment.LiveGiftFragment;
import com.tianyue.tv.Gson.CancelFocusGson;
import com.tianyue.tv.Gson.FocusGson;
import com.tianyue.tv.Gson.RequestFocusGson;
import com.tianyue.tv.MyApplication;
import com.tianyue.tv.R;
import com.tianyue.tv.Util.DmsUtil;
import com.tianyue.tv.Util.KeyBoardUtil;
import com.tianyue.tv.Util.LogUtil;
import com.tianyue.tv.Util.StatusBarUtil;
import com.tianyue.tv.Util.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import de.hdodenhof.circleimageview.CircleImageView;
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
import okhttp3.Call;
import okhttp3.Response;

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
    private static final int HIDDEN_LAYOUT = 1;
    private static final int SHOWN_LAYOUT = 2;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HIDDEN_LAYOUT://隐藏布局
                   hideMediaController();

                    break;
                case SHOWN_LAYOUT://显示布局
                    showMediaController();
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
     */
    private int danmakuPositon;
    private float videoHeight;





    //手势识别器
    private GestureDetector detector;
    private boolean isShowMediaController = true;


    @BindView(R.id.ll_content)
    LinearLayout ll_content;    //视频播放器以下的布局
    @BindView(R.id.rl_shipin)
    RelativeLayout rl_shipin;   //视频播放器布局

    @BindView(R.id.live_details_landTop_title)
    TextView live_details_landTop_title;//顶部左边的标题
    @BindView(R.id.tv_landTop_personNum)
    TextView tv_landTop_personNum;//观看人数
    @BindView(R.id.live_details_playView)
    SurfaceView playView;   //播放器
    @BindView(R.id.live_details_Top_full_share)
    ImageButton share; //一键分享
    @BindView(R.id.live_details_landTop_full_setting)
    ImageButton settings;   //横屏设置按钮
    @BindView(R.id.live_port_details_pause)
    ImageButton portPlayPause;  //播放暂停
    @BindView(R.id.live_land_details_pause)
    ImageButton landPlayPause;  //播放暂停
    @BindView(R.id.ll_loadingView)
    LinearLayout loadingView;   //加载中
    @BindView(R.id.ll_landTop_controller)
    LinearLayout ll_Top_controller; //顶部控制器
    @BindView(R.id.rl_bottom_controller)
    RelativeLayout rl_landbottom_controller;//底部控制器
    @BindView(R.id.ib_landbottom_forbidden)
    ImageButton ib_landbottom_switchDanmaku;    //屏蔽弹幕
    @BindView(R.id.ll_port_bottom_controller)
    LinearLayout ll_portbottom; //竖屏底部特殊
    @BindView(R.id.ll_land_bottom_controller)
    LinearLayout rl_landbottom; //横屏底部特殊

    @BindView(R.id.et_chatMsg)
    EditText et_chatMsg;
    @BindView(R.id.btn_sendMsg) //竖屏发送消息
            Button btn_sendMsg;
//    @BindView(R.id.ll_land_bottom_controller)
//    RelativeLayout rl_landbottom; //横屏底部特殊
    @BindView(R.id.live_details_fans)
    TextView live_details_fans; //竖屏关注
    @BindView(R.id.tv_Top_fans)
    TextView tv_Top_fans;       //横屏关注数
    @BindView(R.id.ll_bottom_chatLayout)
    LinearLayout ll_bottom_chatLayout;




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


    /*********************************/
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
    private static DanmakuContext danmakuContext;
    private BaseDanmakuParser parser;
    private HashMap<Integer, Integer> maxLinesPair;// 弹幕最大行数
    private HashMap<Integer, Boolean> overlappingEnablePair;// 设置是否重叠
    /**
     * 横屏发送弹幕
     */
    @BindView(R.id.live_details_landbottom_send)
    public ImageButton btn_land_sendDanmaku;
    /**
     * 横屏弹幕编辑框
     */
    public EditText et_landText;
    public DmsUtil dmsUtil;
    private String topic = "10085";
    private View.OnTouchListener surfaceviewOnTouchListener;
    private List<LiveChatMessage> messageList = new ArrayList<>();
    private LiveHomeColumn.LiveHomeColumnContent content;
    private String roomId;  //房间id
    private int focusNum;   //关注数
    private String isFocus; //是否关注了
    private int mGuanz_id;  //关注id，给取消关注接口使用
    private boolean hasFocusOfEdtPhone; //竖屏editText是否获取焦点


    /**************************初始化部分********************************/

    /**
     * 初始化 直播详情页面 控件
     */
    @Override
    protected void initView() {


        setContentView(R.layout.live_details_layout);
        //软键盘模式
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        videoHeight = getResources().getDimension(R.dimen.x273);
        showSystemUI();
        //shareSDK初始化
        ShareSDK.initSDK(context, "1a50d1e1f069f");
        EventBus.getDefault().register(this);
        //一键分享
        share.setOnClickListener(v -> showShare());


        content = getIntent().getParcelableExtra("live_column");

        if (content != null) {
            topic = content.getUserId();
            roomId = content.getId();
            tv_Top_fans.setText(content.getFocusNum());
            live_details_landTop_title.setText(content.getTitle());
            tv_landTop_personNum.setText(content.getNumber());
            if (content.getIsPushPOM().equals("0")) {
                playPath = content.getPlayAddress();
            } else {
                playPath = content.getQl_push_flow();
            }
        }

        //奥点云初始化
        initdmsUtil();





        initPlaySettings();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        playView.getHolder().addCallback(surfaceCallback);
        surfaceviewOnTouchListener = (v, event) -> {
            detector.onTouchEvent(event);
            return true;
        };
        playView.setOnTouchListener(surfaceviewOnTouchListener);


        initPortView();

        initLandView();
        //隐藏横屏特殊控件
        hideLandView();


        //2.实例化手势识别器，并且重写双击，点击，长按
        detector = new GestureDetector(this, new MySimpleOnGestureListener());


    }

    /**
     * 初始化数据
     */
    @Override
    protected void init() {
        queryISAttentionFromServer();
    }

    /**
     * 查询是否关注了直播间
     */
    private void queryISAttentionFromServer() {
        OkGo.post(InterfaceUrl.ISATTENTION)
                .tag(this)
                .params("user_id", topic)
                .params("id",roomId)
                .execute(new StringCallback(){

                    @Override
                    public void onSuccess(String result, Call call, Response response) {
                        LogUtil.e("查询成功，结果；"+result);
                        processIsFocusJsonData(result);

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        LogUtil.e("查询失败");
                    }
                });
    }

    /**是否关注
     * 解析json数据
     * @param result    服务器返回的json字符串
     */
    private void processIsFocusJsonData(String result) {
        Gson gson = new Gson();
        FocusGson focusGson = gson.fromJson(result, FocusGson.class);
        isFocus = focusGson.getRet();
        if("1".equals(isFocus)){//关注
             cb_landTop_focus.setBackgroundResource(R.mipmap.icon_focus_select);
            Drawable drawable= getResources().getDrawable(R.mipmap.icon_focus_select);
            // 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            live_details_attention.setCompoundDrawables(drawable,null,null,null);

            mGuanz_id = focusGson.getGuanz_id();
        }else{
            //未关注
            cb_landTop_focus.setBackgroundResource(R.mipmap.live_details_like_uncheck);
            Drawable drawable= getResources().getDrawable(R.mipmap.live_details_like_uncheck);
            // 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            live_details_attention.setCompoundDrawables(drawable,null,null,null);

        }
        focusNum = focusGson.getFollowL();

        //设置粉丝值
         live_details_fans.setText("粉丝值："+focusNum);
         tv_Top_fans.setText(""+focusNum);
    }

    /**
     * 竖屏的时候
     *      隐藏横屏的一些控件
     *      显示竖屏特殊的控件
     */
    private void hideLandView() {
//        隐藏横屏的顶部和底部

        settings.setVisibility(View.GONE);

        tv_Top_fans.setVisibility(View.GONE);
        tv_landTop_personNum.setVisibility(View.GONE);
        ib_landbottom_switchDanmaku.setVisibility(View.GONE);

        rl_landbottom.setVisibility(View.GONE);
        ll_portbottom.setVisibility(View.VISIBLE);


    }
    /**
     * 横屏的时候
     *      隐藏竖屏的一些控件
     *      显示横屏控件
     */
    private void hidePortView() {
        //        隐藏竖屏的顶部和底部
        settings.setVisibility(View.VISIBLE);

        tv_Top_fans.setVisibility(View.VISIBLE);
        tv_landTop_personNum.setVisibility(View.VISIBLE);
        ib_landbottom_switchDanmaku.setVisibility(View.VISIBLE);
        //底部
        rl_landbottom.setVisibility(View.VISIBLE);
        ll_portbottom.setVisibility(View.GONE);

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

        danmakuSp = getSharedPreferences("danmakuConfig", MODE_PRIVATE);
        //初始化横屏弹幕大小值
        danmakuTextSize = danmakuSp.getInt("danmakuSize", 3);
        Log.e(TAG, "读取sp的值：" + danmakuTextSize);
        danmakuAlpha = danmakuSp.getInt("danmakuAlpha", 180);
        Log.e(TAG, "读取sp弹幕透明度的值：" + danmakuAlpha);
        //弹幕默认位置:顶部弹幕
        danmakuPositon = 0;


        et_landText = (EditText) findViewById(R.id.et_landText);
        MyOnFocusChangeListener myOnFocusChangeListener = new MyOnFocusChangeListener();
        et_landText.setOnFocusChangeListener(myOnFocusChangeListener);
        et_landText.setOnEditorActionListener(new MyOnEditorActionListener());

        initdanmaku();


        //横屏发送消息监听
        btn_land_sendDanmaku.setOnClickListener(v -> {
          sendMessage(et_landText);

        });
        settings.setOnClickListener(this);
    }

    private void initdanmaku() {
        //初始化横屏弹幕
        danmakuView = (DanmakuView) findViewById(R.id.danmaku_view);
        LogUtil.e("横屏danmakuView被初始化了" + danmakuView);
        danmakuView.enableDanmakuDrawingCache(true);
        danmakuContext = DanmakuContext.create();
        parser = new BaseDanmakuParser() {
            @Override
            protected IDanmakus parse() {
                return new Danmakus();
            }
        };
        LogUtil.e("danmakuView:" + danmakuView);
        LogUtil.e("danmakuContext:" + danmakuContext);
        LogUtil.e("parser:" + parser);
        danmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {

                showDanmaku = true;
                danmakuView.start();
                LogUtil.e("弹幕控件start");
                // generateSomeDanmaku();
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
        initDanmakuStyle();
        danmakuView.prepare(parser, danmakuContext);//回调重写的prepared() 方法

    }

    private void generateSomeDanmaku() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                while (showDanmaku) {
                    addDanmaku("弹幕测试", false);
                }
            }
        }, 2000);
    }

    class MyOnFocusChangeListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {   //获得焦点
                Toast.makeText(getApplicationContext(), "获得焦点", Toast.LENGTH_SHORT).show();
                mHandler.removeMessages(HIDDEN_LAYOUT);
            } else {  //失去焦点
                Toast.makeText(getApplicationContext(), "失去焦点", Toast.LENGTH_SHORT).show();
                hideMediaController();
            }
        }
    }

    class MyOnTabSelectedListener implements TabLayout.OnTabSelectedListener{

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            int position = tab.getPosition();
            if(position==0){
                ll_bottom_chatLayout.setVisibility(View.VISIBLE);
                viewPager.setCurrentItem(0);

            }else if(position ==1){
                ll_bottom_chatLayout.setVisibility(View.INVISIBLE);
                viewPager.setCurrentItem(1);
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }

    /**
     * 初始化 竖屏 控件
     */
    private void initPortView() {
        CircleImageView headPic = (CircleImageView) findViewById(R.id.live_details_anchorHeadIcon);
        TextView nickName = (TextView) findViewById(R.id.live_details_anchorNickName);
        if (content != null) {
            if (content.getHeadUrl() != null) {
                Picasso.with(this).load(content.getHeadUrl()).into(headPic);
            }
            nickName.setText(content.getNickName());
        }

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
        tabLayout.setOnTabSelectedListener(new MyOnTabSelectedListener());
        et_chatMsg.setOnFocusChangeListener(new MyOnPortFocusChangeListener());
        et_chatMsg.setOnEditorActionListener(new MyOnEditorActionListener());

        //竖屏发送消息监听
        btn_sendMsg.setOnClickListener(v -> {

            sendMessage(et_chatMsg);

        });
    }




    /**
     * editText焦点监听
     */
    class MyOnPortFocusChangeListener implements View.OnFocusChangeListener{

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                Log.e(TAG,"输入框获取到焦点");
               // resetSendMsgRl();


            }else{
                Log.e(TAG,"输入框失去焦点");

            }
            hasFocusOfEdtPhone = hasFocus;
        }
    }

    /**
     * 软键盘监听
     */
    class MyOnEditorActionListener implements TextView.OnEditorActionListener{

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            //点击软键盘发送的时候，隐藏软键盘
            //竖屏
            if(actionId == EditorInfo.IME_ACTION_SEND && v.getId()==R.id.et_chatMsg){

               sendMessage(et_chatMsg);

            //横屏
            }else if(actionId == EditorInfo.IME_ACTION_SEND && v.getId()==R.id.et_landText){
                sendMessage(et_landText);
            }
            return false;
        }

    }

    /**
     * 获取editText的消息并发送
     */
    private void sendMessage(EditText editText) {
        String message = editText.getText().toString();
        if (message.equals("")) {

            showToast("发送的消息不能为空哦");
            return;
        } else {


            dmsUtil.sendMessage(message);
            editText.setText("");
            KeyBoardUtil.closeKeybord(editText,context);

        }



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
            mediaPlayer.setOnVideoSizeChangedListener(this);
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
        Log.e(TAG, "onStart");
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
        LogUtil.e("onPause");
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
        LogUtil.e("onStop");
    }

    @Override
    protected void onDestroy() {
        LogUtil.e("onDestroy");
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
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        EventBus.getDefault().unregister(this);

        ShareSDK.stopSDK(context);


    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false, priority = 1)
    public void onMsgSuccess(EventMsg eventMsg) {
        if (eventMsg.isHiddenTabLayout()) {
            tabLayout.setVisibility(View.GONE);
        } else {
            tabLayout.setVisibility(View.VISIBLE);
        }


    }

    @Override
    protected void onRestart() {
        Log.i(TAG, "onRestart: ");
        super.onRestart();
    }

    /**
     * 更新
     */
    private void updatePlayOrPauseView(ImageButton button) {
        if (!isLivePlay) {
            button.setImageDrawable(getResources().getDrawable(R.mipmap.live_details_play));
        } else {
            button.setImageDrawable(getResources().getDrawable(R.mipmap.live_details_pause));
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


    //    /**
//     * 沉浸式体验
//     *
//     * @param hasFocus
//     */
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//
//        if (hasFocus && Build.VERSION.SDK_INT >= 19 && !isPort) {
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION  //Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住。
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN       //同上
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION         //隐藏虚拟按键(导航栏)。
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN              //Activity全屏显示，且状态栏被隐藏覆盖掉。
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
//    }
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        View decorView = getWindow().getDecorView();
        if(Build.VERSION.SDK_INT >= 19){
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }

    }

    private void showSystemUI() {
        StatusBarUtil.setWindowStatusBarColor(this,R.color.statusbar_black);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_VISIBLE);
    }


    /****************
     * 接口实现部分
     *********************/
    @Override
    @OnClick({R.id.live_port_details_full,R.id.live_land_details_full, R.id.live_port_details_pause,R.id.live_land_details_pause, R.id.live_details_landTop_full_setting, R.id.live_details_Top_back,R.id.live_details_attention,R.id.cb_landTop_focus,R.id.ib_landbottom_forbidden})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.live_port_details_full: //切屏
                if (isPort) {
                    Log.e(TAG, "onClick: 横屏");
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                } else {
                    Log.e(TAG, "onClick: 竖屏");
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                break;
            case R.id.live_land_details_full: //切屏
                if (isPort) {
                    Log.e(TAG, "onClick: 横屏");
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                } else {
                    Log.e(TAG, "onClick: 竖屏");
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                break;
            case R.id.live_port_details_pause:    //暫停
                if (mediaPlayer != null) {
                    if (isLivePlay) {
                        mediaPlayer.pause();
                        isLivePlay = false;
                    } else {
                        mediaPlayer.start();
                        isLivePlay = true;
                    }
                }
                updatePlayOrPauseView(portPlayPause);
                break;
            case R.id.live_land_details_pause:    //暫停
                if (mediaPlayer != null) {
                    if (isLivePlay) {
                        mediaPlayer.pause();
                        isLivePlay = false;
                    } else {
                        mediaPlayer.start();
                        isLivePlay = true;
                    }
                }
                updatePlayOrPauseView(landPlayPause);
                break;
            case R.id.live_details_landTop_full_setting://横屏播放器设置
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
            case R.id.live_details_Top_back:    //后退按钮
                if (isPort) {
                    finish();
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                }
                break;
            case R.id.cb_landTop_focus: //关注
            case R.id.live_details_attention:
                setAttention();
                break;
            case R.id.ib_landbottom_forbidden:
                switchDanmakuButton();
                break;


        }
    }

    /**
     * 设置弹幕的显示和隐藏
     */
    private void switchDanmakuButton() {
        if(showDanmaku){
            //隐藏弹幕
            showDanmaku = false;
            ib_landbottom_switchDanmaku.setBackgroundResource(R.mipmap.ib_landbottom_closedanmaku);
            danmakuView.hide();
        }else{
            //显示弹幕
            showDanmaku = true;
            ib_landbottom_switchDanmaku.setBackgroundResource(R.mipmap.ib_landbottom_opendanmaku);
            danmakuView.show();
        }

    }

    @BindView(R.id.cb_landTop_focus)
    TextView cb_landTop_focus;
    @BindView(R.id.live_details_attention)
    TextView live_details_attention;

    /**
     * 设置关注
     */
    private  synchronized void setAttention() {

        if("0".equals(isFocus)){//未关注，则关注
           //关注
            isFocus = "1";
            requestFocus();
        }else if("1".equals(isFocus)){//关注，则取消关注
            //取消关注

            showCancelDialog();

        }





    }

    /**
     * 显示取消对话框
     */
    private void showCancelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("取消订阅，将收不到主播开播信息，是否取消");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == DialogInterface.BUTTON_POSITIVE){
                    isFocus = "0";
                    cancelFocus();
                }else if(which == DialogInterface.BUTTON_NEGATIVE){
                    isFocus = "1";
                   return;
                }

            }
        });
        builder.setNegativeButton("否",null);
        builder.create().show();

    }

    /**
     * 取消关注
     */
    private void cancelFocus() {

        OkGo.post(InterfaceUrl.CANCEL_ATTENTION)
                .tag(this)
                .params("id",mGuanz_id)
                .params("bcId",content.getId())
                .execute(new StringCallback(){
                    @Override
                    public void onSuccess(String result, Call call, Response response) {
                        LogUtil.e("取消关注onSuccess："+result);

                        processCancelFocusJsonData(result);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        LogUtil.e("取消关注onError");
                        isFocus = "1";
                    }
                });
    }

    /**
     * 解析取消关注
     * @param result
     */
    private void processCancelFocusJsonData(String result) {
        Gson gson = new Gson();
        CancelFocusGson cancelFocusGson = gson.fromJson(result, CancelFocusGson.class);
        if("success".equals(cancelFocusGson.getRet())){ //取消成功
            isFocus = "0";
            showToast("取消订阅该主播");
            cb_landTop_focus.setBackgroundResource(R.mipmap.live_details_like_uncheck);
            Drawable drawable= getResources().getDrawable(R.mipmap.live_details_like_uncheck);
            // 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            live_details_attention.setCompoundDrawables(drawable,null,null,null);
            //取消后更新粉丝值
            live_details_fans.setText("粉丝值："+cancelFocusGson.getCount());
            tv_Top_fans.setText(""+cancelFocusGson.getCount());
        }else if("error".equals(cancelFocusGson.getStatus())){  //取消关注出错
            showToast("取消关注出错");
            isFocus = "1";
        }


    }

    /**
     * 关注
     */
    private void requestFocus() {

        OkGo.post(InterfaceUrl.REQUEST_ATTENTION)
                .tag(this)
                .params("user_id",content.getUserId())
                .params("bCastId",content.getId())
                .execute(new StringCallback(){
                    @Override
                    public void onSuccess(String result, Call call, Response response) {
                        LogUtil.e("关注onSuccess："+result);

                        processRequestFocusJsonData(result);

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        LogUtil.e("关注onError");
                        isFocus = "0";
                    }
                });

    }

    /**
     * 解析成功关注
     * @param result
     */
    private void processRequestFocusJsonData(String result) {
        Gson gson = new Gson();
        RequestFocusGson requestFocusGson = gson.fromJson(result, RequestFocusGson.class);
        String status = requestFocusGson.getStatus();
        if("success".equals(status)){   //关注成功
            isFocus = "1";  //更改关注状态
            showToast("订阅主播成功");
            mGuanz_id = requestFocusGson.getBcfocus();
            live_details_fans.setText("粉丝值："+requestFocusGson.getCount());
            tv_Top_fans.setText(""+requestFocusGson.getCount());
            cb_landTop_focus.setBackgroundResource(R.mipmap.icon_focus_select);
            Drawable drawable= getResources().getDrawable(R.mipmap.icon_focus_select);
            // 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            live_details_attention.setCompoundDrawables(drawable,null,null,null);
        }else if("repeat".equals(status)){  //直播间不存在
            showToast("直播间不存在，不能关注");
            isFocus = "0";
        }


    }

    RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {


        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.barrage_setting_location_top:
                    Toast.makeText(getApplicationContext(), "顶部点击了", Toast.LENGTH_SHORT).show();
                    danmakuPositon = 0;
                    break;
                case R.id.barrage_setting_location_mid:
                    Toast.makeText(getApplicationContext(), "中间点击了", Toast.LENGTH_SHORT).show();
                    danmakuPositon = 1;
                    break;
                case R.id.barrage_setting_location_bottom:
                    Toast.makeText(getApplicationContext(), "底部点击了", Toast.LENGTH_SHORT).show();
                    danmakuPositon = 2;
                    break;
            }
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) danmakuView.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 0);

            setDanmakuPosition();
        }
    };

    private void setDanmakuPosition() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        Log.e(TAG, "宽:" + width + ",高:" + height);//宽:1794,高:1080
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (danmakuPositon == 0) {
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) danmakuView.getLayoutParams();

                    layoutParams.bottomMargin = 600;
                    danmakuView.setLayoutParams(layoutParams);

                } else if (danmakuPositon == 1) {
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) danmakuView.getLayoutParams();
                    layoutParams.topMargin = 300;
                    layoutParams.bottomMargin = 300;

                    danmakuView.setLayoutParams(layoutParams);
                } else if (danmakuPositon == 2) {
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


    }

    private void initdmsUtil() {

        dmsUtil = MyApplication.instance().getDmsUtil();
        if (dmsUtil == null) {
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
            switch (seekBar.getId()) {
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
                    Log.e(TAG, "拖动修改弹幕透明度：" + progress);
                    break;
                case barrage_setting_size_seekbar://弹幕大小
                    //修改
                    danmakuTextSize = progress;


                    Log.e(TAG, "拖动修改弹幕大小：" + progress);
                    break;

            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            switch (seekBar.getId()) {
                case R.id.barrage_setting_size_seekbar:
                    //保存当前size值
                    Log.e(TAG, "保存当前值：" + seekBar.getProgress());
                    getSharedPreferences("danmakuConfig", MODE_PRIVATE).edit().putInt("danmakuSize", seekBar.getProgress()).commit();
                    break;
                case R.id.barrage_setting_alpha_seekbar:
                    danmakuSp.edit().putInt("danmakuAlpha", seekBar.getProgress()).commit();
                    Log.e(TAG, "保存弹幕透明度值：" + seekBar.getProgress());
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
//            playViewWidth = width;
//            playViewHeight = height;
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };

    @Override
    public void onPrepared(PLMediaPlayer plMediaPlayer) {
        mediaPlayer.start();
        isLivePlay = true;
        //发消息延时隐藏
        mHandler.sendEmptyMessageDelayed(HIDDEN_LAYOUT, 5000);


        //如果是横屏,则初始化弹幕资源
        if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {


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
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
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
     * 添加弹幕
     *
     * @param content    消息内容
     * @param withBorder 这个参数用于指定弹幕消息是否带有边框，这样才好将自己发送的弹幕和别人发送的弹幕进行区分。
     */
    public void addDanmaku(String content, boolean withBorder) {
        //DanmakuContext danmakuContext = DanmakuContext.create();
        showToast("log");
        if (danmakuContext != null) {
            showToast("log");
            danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL, danmakuContext);
        }

        if (danmaku == null || danmakuView == null) {
            LogUtil.e(" danmakuView == null" + danmakuView);
            return;
        }
        //设置字体颜色
        danmaku.textColor = Color.WHITE;

        danmaku.text = content;
        danmaku.priority = 1;//0 表示可能会被各种过滤器过滤并隐藏显示 //1 表示一定会显示, 一般用于本机发送的弹幕
        danmaku.padding = 5;

        //设置弹幕字体大小
        danmaku.textSize = sp2px(danmakuTextSize + 13);

        danmaku.setTime(danmakuView.getCurrentTime());
        //本机弹幕修改边框
        if (withBorder) {
            danmaku.textColor = Color.RED;
            danmaku.borderColor = Color.GREEN;
        }
        //设置弹幕透明度
        danmakuView.setAlpha((float) (danmakuAlpha / 255.0));


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
//        if (width != 0 && height != 0) {
//            float ratioW = (float) width / (float) playViewWidth;
//            float ratioH = (float) height / (float) playViewHeight;
//            float ratio = Math.max(ratioW, ratioH);
//            width = (int) Math.ceil((float) width / ratio);
//            height = (int) Math.ceil((float) height / ratio);
//            ViewGroup.LayoutParams params = playView.getLayoutParams();
//            params.width = width;
//            params.height = height;
//            playView.setLayoutParams(params);
//        }
    }

    @Override
    public void onSuccess(String result) {
        Log.e(TAG, "发送成功" + "--:屏幕:--");


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
        LogUtil.e("收到消息:" + message + "朝向:" + (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));
        //竖屏通知chatFragment更新数据
        if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {

            EventBus.getDefault().post(chatMessage);
        } else {
            //横屏时,接收到消息,发到弹幕上
            LogUtil.e("接受到消息，发到横屏");
            LogUtil.e("danmakuview是否在收到消息时为空?:" + danmakuView);

            EventBus.getDefault().post(chatMessage);
            //区分自己的消息和别人的消息
            if(nickName.equals(content.getNickName())){
                addDanmaku(message,true);
            }else{
                addDanmaku(message,false);
            }






        }


    }

    //手势识别器
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
            if (isShowMediaController) {
                //隐藏
                hideMediaController();
                if(!isPort){
                    hideSystemUI();
                }


            } else {
                //显示
                showMediaController();
                //发消息隐藏
                mHandler.sendEmptyMessageDelayed(HIDDEN_LAYOUT, 5000);
                if(!isPort){
                    showSystemUI();
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

        isShowMediaController = true;

        //显示控制器
        ll_Top_controller.setVisibility(View.VISIBLE);
        rl_landbottom_controller.setVisibility(View.VISIBLE);


    }





    //隐藏控制器
    private void hideMediaController() {
        isShowMediaController = false;

        ll_Top_controller.setVisibility(View.GONE);
        rl_landbottom_controller.setVisibility(View.GONE);
        if(!isPort){
            hideSystemUI();
        }

        mHandler.removeMessages(HIDDEN_LAYOUT);

    }

    /**
     * 一键分享
     */
    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("标题");
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(this);
    }

    /**
     * 当屏幕朝向改变的时候回调
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        isPort = !isPort;
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //横屏
            ViewGroup.LayoutParams layoutParams = rl_shipin.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            rl_shipin.setLayoutParams(layoutParams);
            ll_content.setVisibility(View.GONE);

            hidePortView();
            hideSystemUI();
        } else {
            //竖屏
            ViewGroup.LayoutParams layoutParams = rl_shipin.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = (int) videoHeight;
            rl_shipin.setLayoutParams(layoutParams);
            ll_content.setVisibility(View.VISIBLE);
            hideLandView();
            showSystemUI();
        }
        //发消息延时隐藏
        mHandler.sendEmptyMessageDelayed(HIDDEN_LAYOUT, 5000);
    }



}
