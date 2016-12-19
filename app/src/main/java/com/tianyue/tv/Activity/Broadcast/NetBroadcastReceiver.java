package com.tianyue.tv.Activity.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import com.tianyue.tv.Util.NetworkUtil;

/**
 * 网络变化接收者
 * Created by hasee on 2016/9/5.
 */
public class NetBroadcastReceiver extends BroadcastReceiver {
    NetEvevt evevt  ;
    boolean state = false;
    NetworkUtil networkUtil;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                Log.i("Net", "onReceive: 进入");
                if (evevt != null) {
                    if (networkUtil == null) {
                        networkUtil = new NetworkUtil(context);
                    }
                    if (networkUtil.checkNetworkType() == NetworkUtil.NETWORK_MOBILE ||
                                networkUtil.checkNetworkType() == NetworkUtil.NETWORK_WIFI) {
                            evevt.netState(networkUtil.checkNetworkType());
                            state = true;
                    } else if (networkUtil.checkNetworkType() == NetworkUtil.NETWORK_NOT_CONNECTED){
                        evevt.netState(networkUtil.checkNetworkType());
                        state = false;
                    }
                }
        }
    }
    public void setEvevt(NetEvevt evevt){
        this.evevt = evevt;
    }
    public interface NetEvevt{
         void netState(int netModile);
    }
}
