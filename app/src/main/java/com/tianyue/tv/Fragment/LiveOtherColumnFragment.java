package com.tianyue.tv.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tianyue.tv.Adapter.ColumnContentViewAdapter;
import com.tianyue.tv.Bean.LiveHomeColumn;
import com.tianyue.tv.R;

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
    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.live_other_column_fragment, container, false);
    }

    @Override
    protected void init() {
        rootView.setOnRefreshListener(this);

        recyclerView.setLayoutManager(new GridLayoutManager(context,2,GridLayoutManager.VERTICAL,false));

        List<LiveHomeColumn.LiveHomeColumnContent> columnContent = new ArrayList<LiveHomeColumn.LiveHomeColumnContent>();

        int[] resourseId = {
                R.mipmap.tu_1,
                R.mipmap.tu_2,
                R.mipmap.tu_3,
                R.mipmap.tu_4
        };
        for (int j = 0; j < 10; j++) {
            LiveHomeColumn.LiveHomeColumnContent content = new LiveHomeColumn.LiveHomeColumnContent();
            content.setTitle("老黄下象棋");
            content.setResourceId(resourseId[j%4]);
            content.setNickName("老黄");
            columnContent.add(content);
        }
        columnContentViewAdapter = new ColumnContentViewAdapter(context,columnContent);

        recyclerView.setAdapter(columnContentViewAdapter);

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
                columnContentViewAdapter.notifyDataSetChanged();
            }
        },2000);
    }
}
