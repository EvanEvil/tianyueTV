package com.tianyue.tv.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tianyue.mylibrary.util.TimeConvertUtil;
import com.tianyue.tv.Gson.DiscoveryGson;
import com.tianyue.tv.R;

import java.util.List;

/**
 * Created by hasee on 2016/8/11.
 */
public class DiscoveryAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    List<DiscoveryGson.DataList> list;
    private String TAG = "DiscoveryAdapter";
    public DiscoveryAdapter (Context context,List<DiscoveryGson.DataList> list){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
    }
    @Override
    public int getCount() {
        return list == null?0:list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null ;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.discovery_list_item,null);
            viewHolder = new ViewHolder();
            viewHolder.content = (TextView) convertView.findViewById(R.id.discovery_articles_content);
            viewHolder.pic = (ImageView) convertView.findViewById(R.id.discovery_articles_preview);
            viewHolder.title = (TextView) convertView.findViewById(R.id.discovery_articles_title);
            viewHolder.time = (TextView) convertView.findViewById(R.id.discovery_articles_time);
            viewHolder.reading = (TextView) convertView.findViewById(R.id.discovery_articles_reading);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        DiscoveryGson.DataList data = list.get(position);
        long time = data.getNewsTime();
        viewHolder.time.setText(TimeConvertUtil.timestampToDate(time));
        viewHolder.content.setText(data.getNewsDesc());
        viewHolder.title.setText(data.getTitle());
        Picasso.with(context).load(data.getFaceImage()).into(viewHolder.pic);
        return convertView;
    }
    private class ViewHolder{
        TextView title;
        TextView content;
        TextView time;
        TextView reading;
        ImageView pic;
    }
}
