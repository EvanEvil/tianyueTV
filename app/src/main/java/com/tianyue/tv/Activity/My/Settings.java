package com.tianyue.tv.Activity.My;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianyue.tv.Activity.BaseActivity;
import com.tianyue.tv.Config.SettingsConfig;
import com.tianyue.tv.MyApplication;
import com.tianyue.tv.R;
import com.tianyue.tv.Util.AppManager;
import com.tianyue.tv.Util.DataCleanManager;

import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by hasee on 2016/8/16.
 */
public class Settings extends BaseActivity {
    MyApplication app;
    @BindView(R.id.settings_wifi)
    CheckBox wifi;
    @BindView(R.id.settings_hardware)
    CheckBox hardware;
    @BindView(R.id.settings_clear_cache)
    LinearLayout clearCache;
    @BindView(R.id.settings_toolbar)
    Toolbar toolbar;
    @BindView(R.id.settings_about_us)
    TextView about;
    @BindView(R.id.settings_exit_login)
    Button exit;
    @BindView(R.id.tv_appcache)
    TextView tv_appCache;

    SettingsConfig setting;
    private Context mContext;

    private PackageManager mPm;




    @Override
    protected void initView() {
        setContentView(R.layout.settings_layout);
        mContext = getBaseContext();
        //设置缓存数据
        setAppCache();
        app = (MyApplication) getApplication();
        setting = app.getSettings();
        wifi.setChecked(setting.isWifi());
        hardware.setChecked(setting.isHardware());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 这个不太准
     */
//    private void setAppCache() {
//        try {
//            String totalCacheSize = DataCleanManager.getTotalCacheSize(this);
//            Log.e(TAG,"-------------totalsize:"+totalCacheSize);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void setAppCache() {
        //测试创建分支。。。测完删除此行代码
        mPm = getPackageManager();


        // (aidl)创建IPackageStatsObserver.Stub子类的对象,实现onGetStatsCompleted方法
        //stats.cacheSize获取缓存大小
        IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {

            public void onGetStatsCompleted(PackageStats stats,
                                            boolean succeeded) {
                //获取缓存大小，这里是在子线程，需要切换到主线程更新UI
                final String str = Formatter.formatFileSize(getApplicationContext(), stats.cacheSize);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_appCache.setText(str);
                    }
                });
            }
        };





        //1.获取指定类的字节码文件
        try {
            Class<?> clazz = Class.forName("android.content.pm.PackageManager");
            //2.获取调用方法对象
            Method method = clazz.getMethod("getPackageSizeInfo", String.class,IPackageStatsObserver.class);
            //3.获取对象调用方法
            method.invoke(mPm, getBaseContext().getPackageName(),mStatsObserver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @OnCheckedChanged({R.id.settings_wifi, R.id.settings_hardware})
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.settings_wifi:
                if (isChecked) {
                    setting.setWifi(isChecked);
                } else {
                    setting.setWifi(isChecked);
                }
                break;
            case R.id.settings_hardware:
                if (isChecked) {
                    setting.setHardware(isChecked);
                } else {
                    setting.setHardware(isChecked);
                }
                break;
        }
        app.notifyData();
    }

    @OnClick({R.id.settings_exit_login, R.id.settings_about_us, R.id.settings_clear_cache})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings_exit_login:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("提示");
                alertDialog.setMessage("您确定要退出吗？");
                alertDialog.setPositiveButton("确定", clickListener);
                alertDialog.setNegativeButton("取消", clickListener);
                alertDialog.show();
                break;
            case R.id.settings_about_us:
                startActivity(AboutUs.class);
                break;
            case R.id.settings_clear_cache://清理缓存
                clearAppCache();
                break;
        }

    }

    private void clearAppCache() {
        DataCleanManager.cleanInternalCache(mContext);//内部缓存
        DataCleanManager.cleanDatabases(mContext);//清除数据库
        DataCleanManager.cleanSharedPreference(mContext);//清除SP
        DataCleanManager.cleanFiles(mContext);//清理内部files目录
        DataCleanManager.cleanExternalCache(mContext);//清理sd本应用cache
        //显示缓存为零
        tv_appCache.setText("0.00M");
    }

    DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    /**清楚SP缓存 **/
                    SharedPreferences account = getSharedPreferences("account", MODE_PRIVATE);
                    account.edit().clear().commit();
                    AppManager.getAppManager().finishAllActivity();
                    app.clearUserInfo();



                    break;
                case DialogInterface.BUTTON_NEGATIVE:

                    break;
            }
        }
    };




}
