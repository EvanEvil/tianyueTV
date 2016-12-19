package com.tianyue.tv.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.tianyue.tv.Gson.LiveListGson;
import com.tianyue.tv.R;

import java.util.List;

/**
 * Created by hasee on 2016/8/2.
 */
public class LiveHomeListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    List<LiveListGson.DataList> list;
    /**
     * 布局类型
     */
    Integer layoutType = 0;

    public LiveHomeListAdapter (Context context, List<LiveListGson.DataList> list){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
    }
    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setLayoutType(Integer layoutType){
        this.layoutType = layoutType;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (layoutType == 0) {
            convertView = inflater.inflate(R.layout.live_lists_item,null);
        }
        if (layoutType == 1){
            convertView = inflater.inflate(R.layout.my_attention_item,null);
        }
        return convertView;
    }
    private class ViewHolder{

    }
}
