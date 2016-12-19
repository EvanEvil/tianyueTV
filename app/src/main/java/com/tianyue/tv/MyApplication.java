package com.tianyue.tv;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.qiniu.pili.droid.streaming.StreamingEnv;
import com.tianyue.tv.Bean.User;
import com.tianyue.tv.Config.SettingsConfig;
import com.tianyue.tv.Util.DmsUtil;

/**
 * Created by hasee on 2016/8/23.
 */
public class MyApplication extends Application {
    private SettingsConfig settings = null;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private int wifi_state = 0;//0为true 1为false;
    private int hardware_state = 0;
    private User user;
    private static Context mContext;
    private static MyApplication myApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }
    public DmsUtil mDmsUtil;

    public DmsUtil getDmsUtil() {
        return mDmsUtil;
    }

    public void setDmsUtil(DmsUtil dmsUtil) {
        mDmsUtil = dmsUtil;
    }

    public static MyApplication instance() {
        if (myApplication == null) {
            synchronized (MyApplication.class) {
                if (myApplication == null) {
                    myApplication = new MyApplication();
                }
            }
        }
        return myApplication;
    }


    private void init() {
        mContext = this;
        StreamingEnv.init(getApplicationContext());
        settings = new SettingsConfig();
        sp = getSharedPreferences("settings", MODE_PRIVATE);
        editor = sp.edit();
        wifi_state = sp.getInt("wifi", 0);
        hardware_state = sp.getInt("hardware", 0);
        initSettings(wifi_state,hardware_state);
    }

    /**
     * 初始化 更新设置UI界面
     *
     * @param wifi_state
     * @param hardware_state
     */
    private void initSettings(int wifi_state, int hardware_state) {
        if (wifi_state == 0) {
            settings.setWifi(true);
        } else {
            settings.setWifi(false);
        }
        if (hardware_state == 0) {
            settings.setHardware(true);
        } else {
            settings.setHardware(false);
        }
    }

    /**
     * 获取设置状态
     *
     * @return
     */
    public SettingsConfig getSettings() {
        return settings;
    }


    /**
     * 更改设置并保存
     */
    public void notifyData() {
        if (settings.isWifi()) {
            editor.putInt("wifi", 0);
        } else {
            editor.putInt("wifi", 1);
        }
        if (settings.isHardware()) {
            editor.putInt("hardware", 0);
        } else {
            editor.putInt("hardware", 1);
        }
        editor.commit();
    }

    public User getUser() {
        if (user != null) {
            return user;
        }
        return null;
    }

    /**
     * 清理用户信息
     */
    public void clearUserInfo() {
        if (user != null) {
            user = null;
        }
    }

    public void setUser(User user) {
        this.user = user;
    }


    public static Context getAppContext() {
        return mContext.getApplicationContext();
    }
}
