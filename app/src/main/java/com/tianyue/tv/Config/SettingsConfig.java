package com.tianyue.tv.Config;

/**
 * Created by hasee on 2016/8/23.
 */
public class SettingsConfig {
    private boolean wifi = true;//wifi播放提醒
    private boolean hardware = true;//硬件解码

    public boolean isWifi() {
        return wifi;
    }

    public void setWifi(boolean wifi) {
        this.wifi = wifi;
    }

    public boolean isHardware() {
        return hardware;
    }

    public void setHardware(boolean hardware) {
        this.hardware = hardware;
    }
}
