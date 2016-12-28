package com.tianyue.tv.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tianyue.tv.Bean.SearchInfo;
import com.tianyue.tv.R;

import java.util.List;

/**
 * Created by Evan on 2016/12/27.
 */

public class SearchInfoAdapter extends RecyclerView.Adapter<SearchInfoAdapter.ViewHolder> implements View.OnClickListener {
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 正在直播的集合
     */
    List<SearchInfo.BroadCastUserBean> broadCastUser;
    /**
     * 条目的点击监听器
     */
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    public SearchInfoAdapter(Context context,  List<SearchInfo.BroadCastUserBean> broadCastUserInfo) {
        this.mContext = context;
        this.broadCastUser = broadCastUserInfo;
    }

    @Override
    public SearchInfoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.live_column_child_item,parent,false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchInfoAdapter.ViewHolder holder, int position) {
        SearchInfo.BroadCastUserBean liveUser = broadCastUser.get(position);
        Glide.with(mContext)
                .load(liveUser.getHeadUrl())
                .into(holder.roomPic);
        holder.roomNme.setText(liveUser.getName());
        holder.nickName.setText(liveUser.getNickName());
       int num = liveUser.getOnlineNum();
        holder.onLineNum.setText(""+liveUser.getOnlineNum());
        //将数据保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(position);

    }

    @Override
    public int getItemCount() {
        return broadCastUser==null?0:broadCastUser.size();
    }

    @Override
    public void onClick(View v) {
        if(mOnItemClickListener != null){
            mOnItemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView roomPic;   //正在直播图
        TextView roomNme;     //房间名
        TextView nickName;     //主播昵称
        TextView onLineNum; //在线观看人数
        public ViewHolder(View itemView) {
            super(itemView);
            roomPic = (ImageView) itemView.findViewById(R.id.live_column_child_pic);
            roomNme = (TextView) itemView.findViewById(R.id.live_column_child_title);
            nickName = (TextView) itemView.findViewById(R.id.live_column_child_nickName);
            onLineNum = (TextView) itemView.findViewById(R.id.live_column_child_number);
        }
    }

    /**
     * 点击事件的接口
     */
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , int postion);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
