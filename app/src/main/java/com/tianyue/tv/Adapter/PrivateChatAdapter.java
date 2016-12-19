package com.tianyue.tv.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.tianyue.tv.R;


/**
 * Created by hasee on 2016/8/15.
 */
public class PrivateChatAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    public PrivateChatAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.private_chat_item,null);
        return convertView;
    }
}
