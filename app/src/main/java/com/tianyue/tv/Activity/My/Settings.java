package com.tianyue.tv.Activity.My;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianyue.tv.Activity.BaseActivity;
import com.tianyue.tv.Activity.LoginActivity;
import com.tianyue.tv.Config.SettingsConfig;
import com.tianyue.tv.MyApplication;
import com.tianyue.tv.R;
import com.tianyue.tv.Util.AppManager;

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

    SettingsConfig setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.settings_layout);
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
        }

    }

    DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    AppManager.getAppManager().finishAllActivity();
                    app.clearUserInfo();
                    startActivity(LoginActivity.class);
                    break;
                case DialogInterface.BUTTON_NEGATIVE:

                    break;
            }
        }
    };
}
