package com.tianyue.mylibrary.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 时间转换类
 * Created by hasee on 2016/10/31.
 */
public class TimeConvertUtil {


    /**
     * 时间戳转换为日期
     * @param timeMillis
     *        时间戳的毫秒数
\    */
    public static String timestampToDate(long timeMillis){
        //输入转换类型
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //获取当前日历实例
        Calendar calendar = Calendar.getInstance();
        //将时间戳设置入日历
        calendar.setTimeInMillis(timeMillis);
        return format.format(calendar.getTime());
    }
    /**
     * 时间戳转换为日期
     * @param timeMillis
     *        时间戳的毫秒数
     */
    public static String timeToDate(long timeMillis){
        //输入转换类型
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取当前日历实例
        Calendar calendar = Calendar.getInstance();
        //将时间戳设置入日历
        calendar.setTimeInMillis(timeMillis);
        return format.format(calendar.getTime());
    }
    /**
     * 日期转换为时间戳
     * @param date
     * @return
     * @throws ParseException
     */
    public static long dateTotimestamp(String date) throws ParseException {
        DateFormat format = new SimpleDateFormat(date);
        return format.parse(date).getTime();
    }

    /**
     * 获取当前系统时间
     * @return
     */
    public static long getCurrentTime(){
        return System.currentTimeMillis();
    }
}
