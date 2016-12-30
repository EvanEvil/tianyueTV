package com.tianyue.tv.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.tianyue.tv.Activity.Live.LiveDetails;
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
public class LiveOtherColumnFragment extends BaseFragment  {

    @BindView(R.id.live_other_column_recycler)
    XRecyclerView recyclerView;
//    @BindView(R.id.live_other_column_root)
//    SwipeRefreshLayout rootView;

    ColumnContentViewAdapter columnContentViewAdapter;
    List<LiveHomeColumn.LiveHomeColumnContent> columnContent;
    /**
     * 下拉刷新完成
     */
    public final int REFRESHING_SUCCESS = 1;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.live_other_column_fragment, container, false);
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case REFRESHING_SUCCESS:    //下拉刷新完成


                    columnContentViewAdapter.notifyDataSetChanged();
                    recyclerView.refreshComplete();
                    LogUtil.e("刷新完成");
                    break;

            }

        }
    };
    @Override
    protected void init() {
        Bundle bundle = getArguments();
        String type = bundle.getString("type");
        LogUtil.i(type);
//        rootView.setOnRefreshListener(this);
        new Thread(() -> {
            requestBroad(type);
        }).start();
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false));
        recyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        recyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        //设置下拉箭头
        recyclerView.setArrowImageView(R.mipmap.iconfont_downgrey);
        //加载监听
        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                LogUtil.e("正在刷新");
                //  liveHomeColumns.clear();

                //开启子线程拉取数据，请求完成后，使用handler发消息通知主界面更新数据
                new Thread(() -> {
                    requestBroad(type);
                }).start();

            }

            @Override
            public void onLoadMore() {
                LogUtil.e("加载更多");
            }
        });
        columnContent = new ArrayList<>();

        columnContentViewAdapter = new ColumnContentViewAdapter(context, columnContent);

        columnContentViewAdapter.setOnColumnChildClickListener(childPosition -> {
            LiveHomeColumn.LiveHomeColumnContent content = columnContent.get(childPosition);
            Intent intent = new Intent(getActivity(), LiveDetails.class);
            intent.putExtra("live_column", content);
            startActivity(intent);
        });
        recyclerView.setAdapter(columnContentViewAdapter);

    }

    @Override
    public void finishCreateView(Bundle state) {

    }



    /**
     * 请求直播间数据
     */
    private void requestBroad(String type) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormEncodingBuilder().add("tyid", type).build();
        Request request = new Request.Builder().url(InterfaceUrl.ALL_BROADCAST_LIVE).post(body).build();
        client.newCall(request).enqueue(new com.squareup.okhttp.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String result = response.body().string();
                LogUtil.i(result);
                Gson gson = new Gson();
                HomeBroadcast homeBroadcast = gson.fromJson(result, HomeBroadcast.class);
                List<HomeBroadcast.DataListBean> list = homeBroadcast.getDataList();
                columnContent.clear();
                for (HomeBroadcast.DataListBean dataListBean : list) {
                    columnContent.add(fillBroadColumn(dataListBean));
                }
                Message message = Message.obtain();
                message.what = REFRESHING_SUCCESS;
                mHandler.sendMessage(message);
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
