package com.tianyue.tv.Activity;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.tianyue.mylibrary.util.ColorUtil;
import com.tianyue.tv.Bean.SearchInfo;
import com.tianyue.tv.Config.InterfaceUrl;
import com.tianyue.tv.Config.ParamConfigKey;
import com.tianyue.tv.Config.RequestConfigKey;
import com.tianyue.tv.CustomView.MyFlowLayout;
import com.tianyue.tv.R;
import com.tianyue.tv.Util.DrawableUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by hasee on 2016/8/8.
 */
public class SearchActivity extends BaseActivity {


    @BindView(R.id.history_search_flowlayout)
    public MyFlowLayout history_search_flowlayout;//历史搜索
    @BindView(R.id.hot_search_flowlayout)
    public MyFlowLayout hot_search_flowlayout;//热门搜索
    @BindView(R.id.ll_hotSearch)
    public LinearLayout ll_hotSearch;
    private ArrayList<String> nameList;


    @BindView(R.id.sv_search_title_discovery)
    public AutoCompleteTextView sv_search_title_discovery;

    @BindView(R.id.btn_search)
    public Button btn_search;
    private ArrayAdapter<String> arr_adapter;//搜索内容适配器
    private String searchContent;
    /**
     * 查询返回的正在直播的房间信息
     */
    private List<SearchInfo.BroadCastUserBean> broadCastUsers;


    /**
     * 初始化布局
     */
    @Override
    protected void initView() {
        setContentView(R.layout.search_item_layout);
        hot_search_flowlayout.setPadding(10, 10, 10, 10);
        hot_search_flowlayout.setHorizontalSpacing(12);
        hot_search_flowlayout.setVerticalSpacing(12);

        // 获取搜索记录文件内容
        SharedPreferences sp = getSharedPreferences("search_history", 0);
        String history = sp.getString("history", "暂时没有搜索记录");
        // 用逗号分割内容返回数组
        String[] history_arr = history.split(",");
        // 新建适配器，适配器数据为搜索历史文件内容
        arr_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, history_arr);
        // 保留前50条数据
        if (history_arr.length > 50) {
            String[] newArrays = new String[50];
            // 实现数组之间的复制
            System.arraycopy(history_arr, 0, newArrays, 0, 50);
            arr_adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, history_arr);
        }
        // 设置适配器
        sv_search_title_discovery.setAdapter(arr_adapter);
        // 设置监听事件，点击搜索写入搜索词
        btn_search.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                save();
                //隐藏热搜的布局
                ll_hotSearch.setVisibility(View.GONE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getDataFromServer(searchContent);
                    }
                }).start();

            }

        });

    }

    public void save() {
        // 获取搜索框信息
        searchContent = sv_search_title_discovery.getText().toString();
        SharedPreferences mysp = getSharedPreferences("search_history", 0);
        String old_text = mysp.getString("history", "暂时没有搜索记录");

        // 利用StringBuilder.append新增内容，逗号便于读取内容时用逗号拆分开
        StringBuilder builder = new StringBuilder(old_text);
        builder.append(searchContent + ",");

        // 判断搜索内容是否已经存在于历史文件，已存在则不重复添加
        if (!old_text.contains(searchContent + ",")) {
            SharedPreferences.Editor myeditor = mysp.edit();
            myeditor.putString("history", builder.toString());
            myeditor.commit();
            showToast("添加成功");
        } else {
            showToast("已存在");
        }

    }
    //清除搜索记录
    public void cleanHistory(View v){
        SharedPreferences sp =getSharedPreferences("search_history",0);
        SharedPreferences.Editor editor=sp.edit();
        editor.clear();
        editor.commit();
        Toast.makeText(this, "清除成功", Toast.LENGTH_SHORT).show();
        showToast("清除成功");

    }


    /**
     * 查询服务器数据
     * @param query
     */
    private void getDataFromServer(String query) {
        Log.e(TAG,query);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormEncodingBuilder().add(ParamConfigKey.SEARCH_NAME,query)
                .build();
        Request request = new Request.Builder().post(body).url(InterfaceUrl.SEARCH_INFO).build();
        try {
              Response response = client.newCall(request).execute();
            Log.e(TAG,"response:"+response.body().string());

            Gson gson = new Gson();
            SearchInfo searchInfo = gson.fromJson(response.body().string(), SearchInfo.class);
            if(searchInfo.getRet().equals(RequestConfigKey.REQUEST_SUCCESS)){
                //成功
                broadCastUsers = searchInfo.getBroadCastUser();
            }else{
                //失败
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    /**
     * 初始化数据
     */
    @Override
    protected void init() {
        super.init();

        nameList = new ArrayList<>();
        for(int i=0;i<50; i++){
            nameList.add("热门标签"+i);
        }
        for (int i = 0; i < nameList.size(); i++) {
             final TextView textView = new TextView(context);
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER);

            GradientDrawable normalDrawable = DrawableUtil
                    .generateDrawable(
                            ColorUtil.generateBeautifulColor(), 6);
            GradientDrawable pressedDrawable = DrawableUtil
                    .generateDrawable(Color.parseColor("#aaaaaa"),
                            6);

            textView.setBackgroundDrawable(DrawableUtil
                    .generateSelector(normalDrawable,
                            pressedDrawable));
            textView.setPadding(12, 5, 12, 5);
            textView.setText(nameList.get(i));
            hot_search_flowlayout.addView(textView);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showToast(textView.getText().toString());
                }
            });
        }

    }


}