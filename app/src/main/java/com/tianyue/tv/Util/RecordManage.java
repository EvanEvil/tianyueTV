package com.tianyue.tv.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.tianyue.tv.Gson.HotGson;

import java.util.ArrayList;
import java.util.List;

/**
 * 记录管理类
 * Created by hasee on 2016/8/9.
 */
public class RecordManage {
    private Context context;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private List<String> historys;
    private List<HotGson> hots;
    private Gson gson;
    private int count = 0;
    String TAG = "result";
    /**
     * 构造函数初始化
     * @param context
     */
    public RecordManage (Context context){
        this.context = context;
        sp = context.getSharedPreferences("Record",Context.MODE_PRIVATE);
        editor = sp.edit();
        historys = new ArrayList<String>();
        hots = new ArrayList<HotGson>();
        count = sp.getInt("historyCount",0);
        if(count!=0){
            for(int i = 0;i<count;i++) {
                historys.add(sp.getString("history" + (i+1), null));
            }
        }
    }

    /**
     * 获取其存储的数据
     *
     */
    public List<String> getHistoryRecord(){
        Log.i(TAG, "getHistoryRecord: "+historys.size());
        if(historys.size()!=0){
            return historys;
        }
        return null;
    }
    /**
     * 添加历史记录
     * @param history
     */
    public void addHistoryRecord(String history){
        //集合数据为空时
        if(historys.size()==0){
            historys.add(history);
            editor.putString("history" + 1, history);
            editor.commit();
            saveListCount(1);
            return ;
        }
        //遍历数据有无重复
        for(int i = 0;i<historys.size();i++){
            String historyItem  =  sp.getString("history"+(i+1),null);
            Log.i(TAG, "addHistoryRecord: "+historyItem);
            if(historyItem!=null) {
                if (historyItem.equals(history)){
                   return;
                }
            }
        }
        //添加数据
        historys.add(history);
        editor.putString("history" + historys.size(), history);
        Log.i(TAG, "addHistoryRecord: 成功保存"+historys.size());
        saveListCount(historys.size());
        editor.commit();
    }

    /**
     * 清除历史记录
     */
    public void clearHistory(){
        editor.remove("historyCount");
        for(int i = 0;i<historys.size();i++) {
            editor.remove("history"+(i+1));
        }
        editor.commit();
    }
    /**
     * 保存集合数据个数，定位位置
     */
    private void saveListCount(int count){
        editor.putInt("historyCount",count);
        editor.commit();
    }
}
