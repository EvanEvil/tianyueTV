package com.tianyue.tv.Fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tianyue.tv.Activity.Discovery.DiscoveryDetails;
import com.tianyue.tv.Activity.SearchActivity;
import com.tianyue.tv.Adapter.DiscoveryAdapter;
import com.tianyue.tv.Config.InterfaceUrl;
import com.tianyue.tv.Config.ParamConfigKey;
import com.tianyue.tv.Gson.DiscoveryGson;
import com.tianyue.tv.R;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by hasee on 2016/8/12.
 *
 */
public class DiscoveryFragment extends BaseFragment {
    @BindView(R.id.discovery_list)
    ListView discoveryListView;//发现文章列表
    @BindView(R.id.tv_search_title_discovery)
    TextView tv_search_title_discovery;//发现模块顶部搜索
    DiscoveryAdapter discoveryAdapter;//列表适配器
    List<DiscoveryGson.DataList> list;
    private final int UPDATE = 101 ;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.discovery_home, null);
        return view;
    }
    /**
     * 初始化 发现 页面控件
     *
     */
    @Override
    protected void init() {
        list = new ArrayList<DiscoveryGson.DataList>();
        discoveryAdapter = new DiscoveryAdapter(getActivity(),list);
        discoveryListView.setOnItemClickListener(itemListener);
        discoveryListView.setAdapter(discoveryAdapter);

        tv_search_title_discovery.setOnClickListener(mOnSearchClickListener);
        getDiscoveryData();
    }
    View.OnClickListener mOnSearchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, SearchActivity.class);
            startActivity(intent);

        }
    };


    AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            DiscoveryGson.DataList itemData = (DiscoveryGson.DataList) parent.getItemAtPosition(position);
            Intent intent = new Intent(getActivity(), DiscoveryDetails.class);
            intent.putExtra("discovery",itemData);
            startActivity(intent);
        }
    };

    private void getDiscoveryData(){
        OkHttpUtils.get().url(InterfaceUrl.DISCOVERY)
                .addParams(ParamConfigKey.PAGE_NO,"0").addParams(ParamConfigKey.PAGE_SIZE,"0").build().execute(new com.zhy.http.okhttp.callback.Callback() {
            @Override
            public Object parseNetworkResponse(Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                DiscoveryGson discoverys = gson.fromJson(result,DiscoveryGson.class);
                List<DiscoveryGson.DataList> data = discoverys.getDataList();
                for (DiscoveryGson.DataList dataList : data) {
                    list.add(dataList);
//                    Log.i(TAG, "parseNetworkResponse: "+dataList.getNewsTime());
                }
                handler.sendEmptyMessage(UPDATE);
                return null;
            }

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(Object response) {

            }
        });

    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE:
                    discoveryAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
}
