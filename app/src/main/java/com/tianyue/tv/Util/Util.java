package com.tianyue.tv.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用工具类
 * Created by hasee on 2016/10/25.
 */
public class Util {
    private static Toast toast = null;
    private static long oneTime = 0;
    private static long twoTime = 0;
    private static String msg;
    /**
     * 检查内存卡是否存在
     * @return
     */
    public static boolean checkSdCard(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 检查网络是否连接
     * @param context
     *         上下文对象
     * @return
     */
    public static boolean isNetWorkConnect(Context context){
        ConnectivityManager mConnectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        return info != null && mConnectivity.getBackgroundDataSetting();
    }

    /**
     * 判断手机号是否合法
     * @param mobiles
     * @return
     */
    public static boolean isMobileNum(String mobiles) {
        Pattern p = Pattern
                .compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }


    /**
     * toast单例显示提示消息
     * @param activity
     * @param Message
     */
    public static void showToast(final Activity activity, final String Message){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = new Toast(activity);
                    toast = toast.makeText(activity,Message,Toast.LENGTH_SHORT);
                    toast.show();
                    //记录第一次显示message时间
                    oneTime = System.currentTimeMillis();
                    msg = Message;
                } else {
                    //记录第二次显示message时间
                    twoTime = System.currentTimeMillis();
                    if (Message.equals(msg)) {//对比message内容
                        if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                            toast.show();
                        }
                    }else {
                        msg = Message;
                        toast.setText(Message);
                        toast.show();
                    }

                }
                oneTime = twoTime;
            }
        });
    }
}
