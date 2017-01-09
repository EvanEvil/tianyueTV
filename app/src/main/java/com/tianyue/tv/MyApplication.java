package com.tianyue.tv;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.lzy.okgo.OkGo;
import com.qiniu.pili.droid.streaming.StreamingEnv;
import com.tencent.bugly.crashreport.CrashReport;
import com.tendcloud.tenddata.TCAgent;
import com.tianyue.tv.Bean.User;
import com.tianyue.tv.Config.SettingsConfig;
import com.tianyue.tv.Util.DmsUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

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
        /**okhttpUtils初始化**/
        OkGo.init(this);
        OkGo.getInstance().debug("OkGo");
       /** talkingdata初始化**/
        initTalkingData();

    }

    private void initTalkingData() {
        TCAgent.LOG_ON=true;
        // App ID: 在TalkingData创建应用后，进入数据报表页中，在“系统设置”-“编辑应用”页面里查看App ID。
        // 渠道 ID: 是渠道标识符，可通过不同渠道单独追踪数据。
        TCAgent.init(this, "542E9E10184343A0961789A4BBEB0160", null);
        // 如果已经在AndroidManifest.xml配置了App ID和渠道ID，调用TCAgent.init(this)即可；或与AndroidManifest.xml中的对应参数保持一致。
        TCAgent.setReportUncaughtExceptions(true);
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
        CrashReport.initCrashReport(getApplicationContext(), "26743c60f9", false);
        mContext = this;
        StreamingEnv.init(getApplicationContext());
        settings = new SettingsConfig();
        sp = getSharedPreferences("settings", MODE_PRIVATE);
        editor = sp.edit();
        wifi_state = sp.getInt("wifi", 0);
        hardware_state = sp.getInt("hardware", 0);
        initSettings(wifi_state, hardware_state);
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
        } else {
            return (User) getObject("user");
        }
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
        saveObject("user",user);
    }

    /**
     * 保存信息
     *
     * @param object
     */
    private void saveObject(String name, Object object) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {

            fos = mContext.openFileOutput(name,Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private Object getObject(String name) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = mContext.openFileInput(name);
            ois = new ObjectInputStream(fis);
            return ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Context getAppContext() {
        return mContext.getApplicationContext();
    }
}
