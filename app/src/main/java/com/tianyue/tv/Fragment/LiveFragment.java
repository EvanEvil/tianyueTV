package com.tianyue.tv.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.tianyue.tv.Activity.Live.LiveDetails;
import com.tianyue.tv.Adapter.HomeRecyclerAdapter;
import com.tianyue.tv.Bean.HomeBroadcast;
import com.tianyue.tv.Bean.LiveHomeColumn;
import com.tianyue.tv.Bean.TypeBean;
import com.tianyue.tv.Config.InterfaceUrl;
import com.tianyue.tv.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

import static com.tianyue.tv.Adapter.HomeRecyclerAdapter.*;

/**
 * 首页 推荐的Fragment
 * Created by hasee on 2016/8/15.
 */
public class LiveFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.live_home_fragment_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.live_home_fragment_root)
    SwipeRefreshLayout rootView;

    HomeRecyclerAdapter homeRecyclerAdapter;

    OnColumnMoreListener onColumnMoreListener;

    private List<LiveHomeColumn> liveHomeColumns;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.live_home_fragment, null);
        return view;
    }

    @Override
    protected void init() {

        rootView.setOnRefreshListener(this);

        liveHomeColumns = new ArrayList<>();
//        int[] resourseId = {
//                R.mipmap.tu_1,
//                R.mipmap.tu_2,
//                R.mipmap.tu_3,
//                R.mipmap.tu_4
//        };
//        String[] classifys = {
//                "匠人", "衣", "食", "住", "行", "知"
//        };
//        for (int i = 0; i < classifys.length; i++) {
//            LiveHomeColumn column = new LiveHomeColumn();
//            column.setClassify(classifys[i]);
//            List<LiveHomeColumn.LiveHomeColumnContent> columnContent = new ArrayList<LiveHomeColumn.LiveHomeColumnContent>();
//            for (int j = 0; j < 10; j++) {
//                LiveHomeColumn.LiveHomeColumnContent content = new LiveHomeColumn.LiveHomeColumnContent();
//                content.setTitle("老黄下象棋");
//                content.setResourceId(resourseId[j % 4]);
//                content.setNickName("老黄");
//                columnContent.add(content);
//            }
//            column.setContents(columnContent);
//            liveHomeColumns.add(column);
//        }

        homeRecyclerAdapter = new HomeRecyclerAdapter(context, liveHomeColumns);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        recyclerView.setAdapter(homeRecyclerAdapter);

        homeRecyclerAdapter.setOnColumnMoreListener((position, liveHomeColumns1) -> onColumnMoreListener.onMoreClick(position,liveHomeColumns1));

        homeRecyclerAdapter.setOnHomeRecyclerListener(position -> startActivity(LiveDetails.class));

        homeRecyclerAdapter.setOnColumnChildClickListener((parentPosition, childPosition, liveHomeColumns1) -> {
            LiveHomeColumn.LiveHomeColumnContent content  =  liveHomeColumns1.get(parentPosition).getContents().get(childPosition);
            Log.i(TAG, "onChildClick: "+content.getQl_push_flow());
            Intent intent = new Intent(getActivity(),LiveDetails.class);
            intent.putExtra("live_column",content);
            startActivity(intent);
        });
        new Thread(() -> {
            requestBroad();
        }).start();
    }

    @Override
    public void finishCreateView(Bundle state) {

    }


    /**
     * 请求直播间数据
     */
    private void requestBroad() {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormEncodingBuilder().build();
        Request request = new Request.Builder().url(InterfaceUrl.ALL_BROADCAST_LIVE).post(body).build();
        client.newCall(request).enqueue(new com.squareup.okhttp.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String result = response.body().string();
                Log.i(TAG, "onResponse: " + result);
                Gson gson = new Gson();
                HomeBroadcast homeBroadcast = gson.fromJson(result, HomeBroadcast.class);
                fillBroad(homeBroadcast);
            }
        });
    }


    private void fillBroad(HomeBroadcast homeBroadcast) {
        List<LiveHomeColumn.LiveHomeColumnContent> columnContentJR = new ArrayList<LiveHomeColumn.LiveHomeColumnContent>();
        List<LiveHomeColumn.LiveHomeColumnContent> columnContentY = new ArrayList<LiveHomeColumn.LiveHomeColumnContent>();
        List<LiveHomeColumn.LiveHomeColumnContent> columnContentS = new ArrayList<LiveHomeColumn.LiveHomeColumnContent>();
        List<LiveHomeColumn.LiveHomeColumnContent> columnContentZ = new ArrayList<LiveHomeColumn.LiveHomeColumnContent>();
        List<LiveHomeColumn.LiveHomeColumnContent> columnContentX = new ArrayList<LiveHomeColumn.LiveHomeColumnContent>();
        List<LiveHomeColumn.LiveHomeColumnContent> columnContentZI = new ArrayList<LiveHomeColumn.LiveHomeColumnContent>();

//        boolean isFirst = true;
//        String oneType = "";
//        TypeBean typeBean = new TypeBean();
//        HashMap<String, String> main = typeBean.getMainClassify();
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
        fillMainType(columnContentJR.size(),200,columnContentJR);
        fillMainType(columnContentY.size(),300,columnContentY);
        fillMainType(columnContentS.size(),400,columnContentS);
        fillMainType(columnContentZ.size(),500,columnContentZ);
        fillMainType(columnContentX.size(),600,columnContentX);
        fillMainType(columnContentZI.size(),700,columnContentZI);
        getActivity().runOnUiThread(() -> {
            rootView.setRefreshing(false);
            homeRecyclerAdapter.notifyDataSetChanged();
        });


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
        content.setNumber(bean.getOnlineNum()+"");
        content.setUserId(bean.getUser_id()+"");
        content.setHeadUrl(bean.getHeadUrl());
        content.setNickName(bean.getNickName());
        content.setIsPushPOM(bean.getIsPushPOM());
        content.setQl_push_flow(bean.getQl_push_flow());
        content.setPlayAddress(bean.getPlayAddress());
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
            column.setClassify(main.get(type+""));
            column.setContents(list);
            liveHomeColumns.add(column);
        }
    }


    @Override
    public void onRefresh() {
//        new Handler().postDelayed(() -> {
//            rootView.setRefreshing(false);
//            homeRecyclerAdapter.notifyDataSetChanged();
//        }, 2000);
        liveHomeColumns.clear();
        requestBroad();
    }

    public void setOnColumnMoreListener(OnColumnMoreListener onColumnMoreListener) {
        this.onColumnMoreListener = onColumnMoreListener;
    }

    public interface OnColumnMoreListener {
        void onMoreClick(int position,List<LiveHomeColumn> liveHomeColumns);
    }

}
