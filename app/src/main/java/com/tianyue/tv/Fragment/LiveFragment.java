package com.tianyue.tv.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tianyue.tv.Activity.Live.LiveDetails;
import com.tianyue.tv.Adapter.HomeRecyclerAdapter;
import com.tianyue.tv.Bean.LiveHomeColumn;
import com.tianyue.tv.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

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
        int[] resourseId = {
                R.mipmap.tu_1,
                R.mipmap.tu_2,
                R.mipmap.tu_3,
                R.mipmap.tu_4
        };
        String[] classifys = {
                "匠人","衣", "食", "住", "行", "知"
        };
        for (int i = 0; i < classifys.length; i++) {
            LiveHomeColumn column = new LiveHomeColumn();
            column.setClassify(classifys[i]);
            List<LiveHomeColumn.LiveHomeColumnContent> columnContent = new ArrayList<LiveHomeColumn.LiveHomeColumnContent>();
            for (int j = 0; j < 10; j++) {
                LiveHomeColumn.LiveHomeColumnContent content = new LiveHomeColumn.LiveHomeColumnContent();
                content.setTitle("老黄下象棋");
                content.setResourceId(resourseId[j%4]);
                content.setNickName("老黄");
                columnContent.add(content);
            }
            column.setContents(columnContent);
            liveHomeColumns.add(column);
        }

        homeRecyclerAdapter = new HomeRecyclerAdapter(context,liveHomeColumns);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        recyclerView.setAdapter(homeRecyclerAdapter);

        homeRecyclerAdapter.setOnColumnMoreListener(new HomeRecyclerAdapter.OnColumnMoreListener() {
            @Override
            public void onMoreClick(int position) {
                onColumnMoreListener.onMoreClick(position);
            }
        });

        homeRecyclerAdapter.setOnHomeRecyclerListener(new HomeRecyclerAdapter.OnHomeRecyclerListener() {
            @Override
            public void onRollItemClick(int position) {
                startActivity(LiveDetails.class);
            }
        });

        homeRecyclerAdapter.setOnColumnChildClickListener(new HomeRecyclerAdapter.OnColumnChildClickListener() {
            @Override
            public void onChildClick(int parentPosition, int childPosition) {
                startActivity(LiveDetails.class);
            }
        });
    }

    @Override
    public void finishCreateView(Bundle state) {

    }


    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rootView.setRefreshing(false);
                homeRecyclerAdapter.notifyDataSetChanged();
            }
        },2000);
    }

    public void setOnColumnMoreListener(OnColumnMoreListener onColumnMoreListener){
        this.onColumnMoreListener = onColumnMoreListener;
    }

    public interface OnColumnMoreListener{
        void onMoreClick(int position);
    }

}
