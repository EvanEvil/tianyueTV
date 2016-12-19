package com.tianyue.tv.Bean;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hasee on 2016/12/15.
 */
public class TypeBean {

    String TAG = this.getClass().getSimpleName();
    /**
     * 主分类
     */
    private HashMap<String, String> mainClassify;
    /**
     * 所有子分类
     */
    private HashMap<String, String> minorClassify;

    private HashMap<String, List<String>> mainAndMinorClassify;
    /**
     * 主分类typeId
     */
    private String[] mainTypeId = {"200", "300", "400", "500", "600", "700"};
    /**
     * 主分类名
     */
    private String[] mainTypeName = {"匠人", "衣", "食", "住", "行", "知"};

    /**
     * 主分类typeId
     */
    private String[] minorTypeId = {"201", "202", "203", "204",
            "301", "302","303","304", "305","306","307", "308",
            "401", "402","403","404", "405","406",
            "501", "502","503","504", "505","506","507", "508",
            "601", "602","603","604",
            "701", "702","703","704","705"};
    /**
     * 子分类名
     */
    private String[] minorTypeName = {"老字号", "名家作品", "品牌新星", "民间艺术",
            "古风", "现代", "鞋定制", "包罗万象", "点睛搭配", "布艺", "珠宝首饰", "小饰品",
            "茶韵", "私房菜", "厨艺秀", "户外美食", "烘焙西点", "器具",
            "特色客栈", "文玩藏品", "居家小物", "具象生活", "玩转乐器", "文房诗礼", "闻香识人", "天然养肤",
            "徒步主义", "驴友探险", "人文之旅", "探秘",
            "手绘", "原创音乐", "史记", "文艺漫谈", "教学"};

    private String[][] minor = {{"老字号", "名家作品", "品牌新星", "民间艺术"},
            {"古风", "现代", "鞋定制", "包罗万象", "点睛搭配", "布艺", "珠宝首饰", "小饰品"},
            {"茶韵", "私房菜", "厨艺秀", "户外美食", "烘焙西点", "器具"},
            {"特色客栈", "文玩藏品", "居家小物", "具象生活", "玩转乐器", "文房诗礼", "闻香识人", "天然养肤"},
            {"徒步主义", "驴友探险", "人文之旅", "探秘"},
            {"手绘", "原创音乐", "史记", "文艺漫谈", "教学"}};
    /**
     * 子分类集合
     */
    private List<String> minorList = new ArrayList<>();

    public TypeBean() {
        mainClassify = new HashMap<>();
        minorClassify = new HashMap<>();
        mainAndMinorClassify = new HashMap<>();
        init();
    }

    private void init() {
        for (int i = 0; i < mainTypeId.length; i++) {
            Log.i(TAG, "init: " + mainTypeId[i] + mainTypeName[i]);
            String key = mainTypeId[i];
            String value = mainTypeName[i];
            mainClassify.put(key, value);
            Log.i(TAG, "init: " + key + value + mainClassify.get(key));
        }
        for (int i = 0; i < minorTypeId.length; i++) {
            String key = minorTypeId[i];
            String value = minorTypeName[i];
            minorClassify.put(key, value);
        }
        for (int i = 0; i < minor.length; i++) {
            String[] list = minor[i];
            minorList = Arrays.asList(list);
            mainAndMinorClassify.put(mainTypeId[i], minorList);
        }

    }

    public HashMap<String, String> getMainClassify() {
        return mainClassify;
    }

    public HashMap<String, String> getMinorClassify() {
        return minorClassify;
    }

    public String[] getMainTypeId() {
        return mainTypeId;
    }

    public String[] getMainTypeName() {
        return mainTypeName;
    }

    public HashMap<String, List<String>> getMainAndMinorClassify() {
        return mainAndMinorClassify;
    }

}
