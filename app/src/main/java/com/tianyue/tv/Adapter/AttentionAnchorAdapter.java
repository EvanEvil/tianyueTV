package com.tianyue.tv.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianyue.tv.Bean.AttentionAnchorInfo;
import com.tianyue.tv.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by hasee on 2016/8/24.
 */
public class AttentionAnchorAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    List<Integer> itemPositions;//记录选中的位置
    List<AttentionAnchorInfo> anchorInfos;//关注的主播列表
    Map<Integer,String> itemCheckedPositions ;
    String TAG = "setItemShowCheck";
    public AttentionAnchorAdapter(Context context ,List<AttentionAnchorInfo> anchorInfos){
        this.context = context;
        inflater = LayoutInflater.from(context);
        itemCheckedPositions = new HashMap<>();
        this.anchorInfos = anchorInfos;
    }
    @Override
    public int getCount() {
        return anchorInfos==null?0:anchorInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return anchorInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.attention_anchor_item,null);
            holder = new ViewHolder();
            holder.state = (TextView) convertView.findViewById(R.id.attention_anchor_live_state);
            holder.check = (ImageView) convertView.findViewById(R.id.attention_anchor_check);
            holder.head = (CircleImageView) convertView.findViewById(R.id.attention_anchor_head);
            holder.nickname = (TextView) convertView.findViewById(R.id.attention_anchor_nickname);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        String state = itemCheckedPositions.get(position);
        if (state != null) {
            if (state.equals("Checked")) {
                holder.check.setVisibility(View.VISIBLE);
            } else {
                holder.check.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    /**
     * 选中所有item项
     */
   public void setAllItemChecked(){
      for(int i = 0;i<anchorInfos.size();i++){
          itemCheckedPositions.put(i,"Checked");
      }
       notifyDataSetChanged();
    }
    /**
     * 取消选中所有item项
     */
    public void setAllItemUnChecked(){
        for(int i = 0;i<anchorInfos.size();i++){
            itemCheckedPositions.put(i,"UnChecked");
        }
        notifyDataSetChanged();
    }
    /**
     * 设置当前item选中状态
     * @param position
     */
    public void setItemChecked(int position){
        String state = itemCheckedPositions.get(position);
            if (state!=null && state.equals("Checked")) {
                itemCheckedPositions.put(position,"unChecked");
            } else {
                itemCheckedPositions.put(position,"Checked");
            }
        notifyDataSetChanged();
    }
    private class ViewHolder{
        CircleImageView head;
        ImageView check;
        TextView nickname;
        TextView state;
    }
}
