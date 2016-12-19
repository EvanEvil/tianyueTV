package com.tianyue.tv.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tianyue.tv.R;

import java.util.List;

/**
 * Created by hasee on 2016/8/8.
 */
public class HotSearchAdapter extends BaseAdapter {
    Context context;//上下文对象
    List<String> lists;//热搜记录
    LayoutInflater inflater;

    public HotSearchAdapter(Context context, List<String> lists) {
        this.context = context;
        this.lists = lists;
        inflater = LayoutInflater.from(context);
    }
    public HotSearchAdapter(Context context) {
         this(context,null);
    }
    @Override
    public int getCount() {
        return lists==null?0:lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.hot_search_item, null);
        TextView hot_search_item1 = (TextView) convertView.findViewById(R.id.hot_search_item_1);
//        TextView hot_search_item2 = (TextView) convertView.findViewById(R.id.hot_search_item_2);
//        TextView hot_search_item3 = (TextView) convertView.findViewById(R.id.hot_search_item_3);

//        hot_search_item1.setText(lists.get(position));

        return convertView;
    }
}
