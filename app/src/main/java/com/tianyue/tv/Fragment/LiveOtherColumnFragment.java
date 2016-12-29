package com.tianyue.tv.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.tianyue.tv.Adapter.ColumnContentViewAdapter;
import com.tianyue.tv.Bean.HomeBroadcast;
import com.tianyue.tv.Bean.LiveHomeColumn;
import com.tianyue.tv.Config.InterfaceUrl;
import com.tianyue.tv.R;
import com.tianyue.tv.Util.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 首页 其他栏目的Fragment
 * Created by hasee on 2016/12/7.
 */
public class LiveOtherColumnFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.live_other_column_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.live_other_column_root)
    SwipeRefreshLayout rootView;

    ColumnContentViewAdapter columnContentViewAdapter;
    List<LiveHomeColumn.LiveHomeColumnContent> columnContent;
    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.live_other_column_fragment, container, false);
    }

    @Override
    protected void init() {
        Bundle bundle = getArguments();
        String type = bundle.getString("type");
        LogUtil.i(type);
        rootView.setOnRefreshListener(this);
        new Thread(() -> {
            requestBroad(type);
        }).start();
        recyclerView.setLayoutManager(new GridLayoutManager(context,2,GridLayoutManager.VERTICAL,false));

        columnContent = new ArrayList<>();
//
//        int[] resourseId = {
//                R.mipmap.tu_1,
//                R.mipmap.tu_2,
//                R.mipmap.tu_3,
//                R.mipmap.tu_4
//        };
//        for (int j = 0; j < 10; j++) {
//            LiveHomeColumn.LiveHomeColumnContent content = new LiveHomeColumn.LiveHomeColumnContent();
//            content.setTitle("老黄下象棋");
//            content.setResourceId(resourseId[j%4]);
//            content.setNickName("老黄");
//            columnContent.add(content);
//        }
        columnContentViewAdapter = new ColumnContentViewAdapter(context,columnContent);

        recyclerView.setAdapter(columnContentViewAdapter);

    }

    @Override
    public void finishCreateView(Bundle state) {

    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> {
            rootView.setRefreshing(false);
            columnContentViewAdapter.notifyDataSetChanged();
        },2000);
    }

    /**
     * 请求直播间数据
     */
    private void requestBroad(String type) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormEncodingBuilder().add("tyid",type).build();
        Request request = new Request.Builder().url(InterfaceUrl.ALL_BROADCAST_LIVE).post(body).build();
        client.newCall(request).enqueue(new com.squareup.okhttp.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                showToast("获取信息失败");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String result = response.body().string();
                LogUtil.i(result);
                Gson gson = new Gson();
                HomeBroadcast homeBroadcast = gson.fromJson(result, HomeBroadcast.class);
                List<HomeBroadcast.DataListBean> list = homeBroadcast.getDataList();
                for (HomeBroadcast.DataListBean dataListBean : list) {
                    columnContent.add(fillBroadColumn(dataListBean));
                }
                getActivity().runOnUiThread(() -> columnContentViewAdapter.notifyDataSetChanged());
            }
        });
    }
    /**
     * 填充单个直播间信息
     */
    private LiveHomeColumn.LiveHomeColumnContent fillBroadColumn(HomeBroadcast.DataListBean bean) {
        LiveHomeColumn.LiveHomeColumnContent content = new LiveHomeColumn.LiveHomeColumnContent();
        content.setPicUrl(bean.getImage());
        content.setTitle(bean.getName());
        content.setNumber(bean.getOnlineNum() + "");
        content.setUserId(bean.getUser_id() + "");
        content.setHeadUrl(bean.getHeadUrl());
        content.setNickName(bean.getNickName());
        content.setIsPushPOM(bean.getIsPushPOM());
        content.setQl_push_flow(bean.getQl_push_flow());
        content.setPlayAddress(bean.getPlayAddress());
        content.setFocusNum(bean.getFocusNum());
        return content;
    }
}
