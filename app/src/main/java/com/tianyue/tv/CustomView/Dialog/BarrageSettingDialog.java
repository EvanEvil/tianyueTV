package com.tianyue.tv.CustomView.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.tianyue.tv.R;

/**
 * Created by hasee on 2016/8/25.
 */
public class BarrageSettingDialog extends Dialog{
    private AudioManager audioManager;
    private Context tmpContext;
    /**
     * 加载到dialog的view
     */
    private View mView;
    /**
     * 音量调节
     */
    private SeekBar volume;
    /**
     *屏幕亮度调节
     */
    private SeekBar brightness;
    /**
     *弹幕透明度
     */
    private SeekBar alpha;
    /**
     * 弹幕大小
     */
    private SeekBar danmakuSize;
    /**
     * 弹幕显示位置
     */
    private RadioGroup location;

    public BarrageSettingDialog(Context context) {
        this(context, R.style.dialog_untran);

    }
    public BarrageSettingDialog(Context context, int theme) {
        super(context, R.style.dialog_untran);
        init(context);
        this.tmpContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        params.y = 0;

        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        this.onWindowAttributesChanged(params);

    }

    private void init(Context context){
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mView = View.inflate(context,R.layout.barrage_settings_layout,null);
        volume = (SeekBar) mView.findViewById(R.id.barrage_setting_volume_seekbar);
        brightness = (SeekBar) mView.findViewById(R.id.barrage_setting_brightness_seekbar);
        alpha = (SeekBar) mView.findViewById(R.id.barrage_setting_alpha_seekbar);
        danmakuSize= (SeekBar) mView.findViewById(R.id.barrage_setting_size_seekbar);
        location = (RadioGroup) mView.findViewById(R.id.barrage_setting_location_group);
        volume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        brightness.setMax(255);
        alpha.setMax(255);//设置弹幕透明度的最大值
        danmakuSize.setMax(15);//弹幕大小值
        setContentView(mView);
    }
    /**
     * 设置seekbar监听
     */
    public void setSeekbarOnclickListener(SeekBar.OnSeekBarChangeListener listener){
        if (listener != null) {
            volume.setOnSeekBarChangeListener(listener);
            brightness.setOnSeekBarChangeListener(listener);
            alpha.setOnSeekBarChangeListener(listener);
            danmakuSize.setOnSeekBarChangeListener(listener);
        }
    }

    /**
     * 设置弹幕位置选择监听器
     * @param listener
     */
    public void setRadioGroupCheckChangeListener(RadioGroup.OnCheckedChangeListener listener){
        if(listener != null){
            location.setOnCheckedChangeListener(listener);
        }
    }


    /**
     * 设置音量的进度
     * @param progress
     */
    public void setVolumeProgress(int progress){
        volume.setProgress(progress);
    }
    /**
     * 设置屏幕亮度
     * @param progress
     */
    public void setBrightnesseProgress(int progress){
        brightness.setProgress(progress);
    }
    /**
     * 设置透明度
     * @param progress
     */
    public void setAlphaProgress(int progress){
        alpha.setProgress(progress);
    }
    /**
     * 设置弹幕大小
     * @param progress
     */
    public void setDanmakuSizeProgress(int progress){
        danmakuSize.setProgress(progress);
    }

    /**
     * 设置弹幕位置
     * @param position
     */
    public void setDanmakuPosition(int position){
        RadioButton rb = (RadioButton) location.getChildAt(position);
        //RadioButton rb = (RadioButton) location.findViewById(R.id.barrage_setting_location_top);
        rb.setChecked(true);
    }


}
