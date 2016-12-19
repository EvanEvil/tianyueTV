package com.tianyue.tv.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * Created by hasee on 2016/9/5.
 */
public class NetworkUtil {
    public static final int NETWORK_WIFI = 1;
    public static final int NETWORK_MOBILE = 2;
    public static final int NETWORK_NOT_CONNECTED = 0;
    private Context context;
    ConnectivityManager mConnectivity;
    TelephonyManager mTelephony;
    NetworkInfo info;
    public NetworkUtil(Context context){
        this.context = context;
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        mConnectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        mTelephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        info  = mConnectivity.getActiveNetworkInfo();
    }
    /**
     * 判断网络是否连接
     * @return
     */
    public boolean netConnections(){
        if (info == null || !mConnectivity.getBackgroundDataSetting()) {
            return false;
        }
        return true;
    }

    /**
     * 检查网络连接类型
     * @return
     */
    public int checkNetworkType(){
        if (info != null) {
            int netType = info.getType();
            int netSubtype = info.getSubtype();
            if (netType == ConnectivityManager.TYPE_WIFI && info.isConnected()) {  //WIFI
                return NETWORK_WIFI;
            } else if (netType == ConnectivityManager.TYPE_MOBILE && info.isConnected()) {   //MOBILE
                return NETWORK_MOBILE;
            }
        }
        return NETWORK_NOT_CONNECTED;
    }

    /**
     * 判断WiFi是否连接
     * @return
     */
    public  boolean isWifi() {
        if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }
        /**
         * 检查WiFi和移动网络是否连接
         * @return
         */
    public  boolean checkNetworkConnection()
    {
        final android.net.NetworkInfo wifi = mConnectivity .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile =mConnectivity .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if(wifi.isAvailable()||mobile.isAvailable())  //getState()方法是查询是否连接了数据网络
            return true;
        else
            return false;
    }
}
