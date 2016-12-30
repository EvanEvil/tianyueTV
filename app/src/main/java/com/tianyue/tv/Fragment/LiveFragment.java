package com.tianyue.tv.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.tianyue.tv.Activity.AnchorEnlistActivity;
import com.tianyue.tv.Activity.Live.LiveDetails;
import com.tianyue.tv.Adapter.HomeRecyclerAdapter;
import com.tianyue.tv.Bean.HomeBroadcast;
import com.tianyue.tv.Bean.LiveHomeColumn;
import com.tianyue.tv.Bean.TypeBean;
import com.tianyue.tv.Config.InterfaceUrl;
import com.tianyue.tv.R;
import com.tianyue.tv.Util.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * 首页 推荐的Fragment
 * Created by hasee on 2016/8/15.
 */
public class LiveFragment extends BaseFragment  {

    @BindView(R.id.live_home_fragment_recycler)
    XRecyclerView mXRecyclerView;
//    @BindView(R.id.live_home_fragment_root)
//    SwipeRefreshLayout rootView;

    HomeRecyclerAdapter homeRecyclerAdapter;

    OnColumnMoreListener onColumnMoreListener;

    private List<LiveHomeColumn> liveHomeColumns;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case REFRESHING_SUCCESS:    //下拉刷新完成

                    homeRecyclerAdapter.notifyDataSetChanged();
                    mXRecyclerView.refreshComplete();
                    LogUtil.e("刷新完成");
                    break;

            }

        }
    };
    /**
     * 下拉刷新完成
     */
    public final int REFRESHING_SUCCESS = 1;
    /**
     * 第一次请求网络
     */
    public final int FIRST_REQUEST = 2;
    /**
     * 下拉刷新请求网络
     */
    public final int REFRESHING_REQUEST = 3;


    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.live_home_fragment, null);
        return view;
    }

    @Override
    protected void init() {

        //rootView.setOnRefreshListener(this);
        mXRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mXRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        //设置下拉箭头
        mXRecyclerView.setArrowImageView(R.mipmap.iconfont_downgrey);
        //加载监听
        mXRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                LogUtil.e("正在刷新");
              //  liveHomeColumns.clear();

                //开启子线程拉取数据，请求完成后，使用handler发消息通知主界面更新数据
                new Thread(() -> {
                    requestBroad();
                }).start();

            }

            @Override
            public void onLoadMore() {
                LogUtil.e("加载更多");
            }
        });
        liveHomeColumns = new ArrayList<>();

        //第一次创建适配器，没有数据，所以界面什么都不显示
        homeRecyclerAdapter = new HomeRecyclerAdapter(context, liveHomeColumns);

        //设置recyclerview
        mXRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mXRecyclerView.setAdapter(homeRecyclerAdapter);
        TextView textView = new TextView(context);
        textView.setText("第一次进入，还没访问服务器数据哦");
        mXRecyclerView.setEmptyView(textView);
        //点击更多监听
        homeRecyclerAdapter.setOnColumnMoreListener((position, liveHomeColumns1) -> onColumnMoreListener.onMoreClick(position, liveHomeColumns1));
        //轮播图的点击监听，跳转到播放页面
        homeRecyclerAdapter.setOnHomeRecyclerListener(position -> startActivity(AnchorEnlistActivity.class));
        //item的点击监听，跳转到播放页面
        homeRecyclerAdapter.setOnColumnChildClickListener((parentPosition, childPosition, liveHomeColumns1) -> {
            LiveHomeColumn.LiveHomeColumnContent content = liveHomeColumns1.get(parentPosition).getContents().get(childPosition);
            Log.i(TAG, "onChildClick: " + content.getQl_push_flow());
            Intent intent = new Intent(getActivity(), LiveDetails.class);
            intent.putExtra("live_column", content);
            startActivity(intent);
        });
        //开启线程拉取服务器的数据
        new Thread(() -> {
            requestBroad();
        }).start();
    }

    @Override
    public void finishCreateView(Bundle state) {

    }


    /**
     * 请求直播间数据
     *
     */
    private void requestBroad() {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormEncodingBuilder().build();
        Request request = new Request.Builder().url(InterfaceUrl.ALL_BROADCAST_LIVE).post(body).build();
        client.newCall(request).enqueue(new com.squareup.okhttp.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                LogUtil.e("onFailure");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String result = response.body().string();
                LogUtil.e("onResponse:"+result);
                Gson gson = new Gson();
                //解析json数据到javabean
                HomeBroadcast homeBroadcast = gson.fromJson(result, HomeBroadcast.class);
                fillBroad(homeBroadcast);
                //发消息通知主线程更新
                Message message = Message.obtain();
                message.what = REFRESHING_SUCCESS;
                mHandler.sendMessage(message);
            }
        });
    }

    //手动填充6个集合的数据（服务器返回的是一堆数据）
    private void fillBroad(HomeBroadcast homeBroadcast) {
        List<LiveHomeColumn.LiveHomeColumnContent> columnContentJR = new ArrayList<LiveHomeColumn.LiveHomeColumnContent>();
        List<LiveHomeColumn.LiveHomeColumnContent> columnContentY = new ArrayList<LiveHomeColumn.LiveHomeColumnContent>();
        List<LiveHomeColumn.LiveHomeColumnContent> columnContentS = new ArrayList<LiveHomeColumn.LiveHomeColumnContent>();
        List<LiveHomeColumn.LiveHomeColumnContent> columnContentZ = new ArrayList<LiveHomeColumn.LiveHomeColumnContent>();
        List<LiveHomeColumn.LiveHomeColumnContent> columnContentX = new ArrayList<LiveHomeColumn.LiveHomeColumnContent>();
        List<LiveHomeColumn.LiveHomeColumnContent> columnContentZI = new ArrayList<LiveHomeColumn.LiveHomeColumnContent>();


        TypeBean typeBean = new TypeBean();
        HashMap<String, String> main = typeBean.getMainClassify();
        List<HomeBroadcast.DataListBean> data = homeBroadcast.getDataList();
        for (HomeBroadcast.DataListBean bean : data) {
//            String type = main.get(bean.getBctypeId());
            Integer type = bean.getBctypeId();
            if (type == 200) {
                columnContentJR.add(fillBroadColumn(bean));
            }
            if (type == 300) {
                columnContentY.add(fillBroadColumn(bean));
            }
            if (type == 400) {
                columnContentS.add(fillBroadColumn(bean));
            }
            if (type == 500) {
                columnContentZ.add(fillBroadColumn(bean));
            }
            if (type == 600) {
                columnContentX.add(fillBroadColumn(bean));
            }
            if (type == 700) {
                columnContentZI.add(fillBroadColumn(bean));
            }
        }
        liveHomeColumns.clear();
        fillMainType(columnContentJR.size(), 200, columnContentJR);
        fillMainType(columnContentY.size(), 300, columnContentY);
        fillMainType(columnContentS.size(), 400, columnContentS);
        fillMainType(columnContentZ.size(), 500, columnContentZ);
        fillMainType(columnContentX.size(), 600, columnContentX);
        fillMainType(columnContentZI.size(), 700, columnContentZI);
//        getActivity().runOnUiThread(() -> {
//            mXRecyclerView.refreshComplete();
//            homeRecyclerAdapter.notifyDataSetChanged();
//        });


//            if (isFirst) {
//                LiveHomeColumn column = new LiveHomeColumn();
//                oneType = type;//记录第一次的类型
//                isFirst = false;
//                column.setClassify(type);
//                liveHomeColumns.add(column);
//                List<LiveHomeColumn.LiveHomeColumnContent> columnContent = new ArrayList<LiveHomeColumn.LiveHomeColumnContent>();
//                LiveHomeColumn.LiveHomeColumnContent content = new LiveHomeColumn.LiveHomeColumnContent();
//                content.setTitle(bean.getName());
//                content.setNickName(bean.getNickName());
//                content.setNumber(bean.getOnlineNum());
//                content.setPicUrl(bean.getImage());
//                columnContent.add(content);
//            } else {
//                if (!type.equals(oneType)) {
//                    LiveHomeColumn column = new LiveHomeColumn();
//                    column.setClassify(type);
//                    oneType = type;
//                    liveHomeColumns.add(column);
//                } else {
//
//                }
//        }
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

    /**
     * 填充主分类
     */
    private void fillMainType(int size, int type, List<LiveHomeColumn.LiveHomeColumnContent> list) {
        TypeBean typeBean = null;
        if (typeBean == null) {
            typeBean = new TypeBean();
        }
        HashMap<String, String> main = null;
        if (main == null) {
            main = typeBean.getMainClassify();
        }
        if (size != 0) {
            LiveHomeColumn column = new LiveHomeColumn();
            column.setClassify(main.get(type + ""));
            column.setContents(list);
            liveHomeColumns.add(column);
        }
    }




    public void setOnColumnMoreListener(OnColumnMoreListener onColumnMoreListener) {
        this.onColumnMoreListener = onColumnMoreListener;
    }

    public interface OnColumnMoreListener {
        void onMoreClick(int position, List<LiveHomeColumn> liveHomeColumns);
    }

}
