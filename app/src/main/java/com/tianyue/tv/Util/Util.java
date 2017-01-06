package com.tianyue.tv.Util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.widget.Toast;

import java.lang.reflect.Field;
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
        //test
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

    /**
     * 获取屏幕的宽
     * @param activity
     * @return
     */
    public  static int getScreenWidth(Activity activity){

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    /**
     * 获取屏幕的高
     * @param activity
     * @return
     */
    public  static int getScreenHeight(Activity activity){

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            int height = context.getResources().getDimensionPixelSize(x);
            return height;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
