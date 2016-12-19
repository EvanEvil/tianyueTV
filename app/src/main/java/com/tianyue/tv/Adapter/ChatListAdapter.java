package com.tianyue.tv.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tianyue.tv.Base.BaseViewHolder;
import com.tianyue.tv.Bean.LiveChatMessage;
import com.tianyue.tv.R;

import java.util.List;

import butterknife.BindView;

/**
 * Created by hasee on 2016/12/2.
 */
public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<LiveChatMessage> messageList;
    Context context;
    LayoutInflater inflater;

    public static final int INPUT_PLAY_TYPE = 0xff01;

    public static final int INPUT_LIVE_TYPE = 0xff02;

    private int type;

    public ChatListAdapter(Context context, List<LiveChatMessage> messageList) {
        this(context, messageList, INPUT_PLAY_TYPE);
    }

    public ChatListAdapter(Context context, List<LiveChatMessage> messageList, int type) {
        this.messageList = messageList;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.type = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.chat_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            bindMyViewHolder((MyViewHolder) holder, position);
        }
    }


    private void bindMyViewHolder(MyViewHolder myViewHolder, int position) {
        int color = 0;
        switch (type) {
            case INPUT_PLAY_TYPE:
                color = context.getResources().getColor(R.color.red);
                break;
            case INPUT_LIVE_TYPE:
                color = context.getResources().getColor(R.color.white);
                break;
        }
        if (color != 0) {
            myViewHolder.setTextColor(color);
        }
        myViewHolder.fillData(messageList.get(position));
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return messageList == null ? 0 : messageList.size();
    }

    class MyViewHolder extends BaseViewHolder {

        @BindView(R.id.chat_list_nickName)
        TextView nickName;
        @BindView(R.id.chat_list_label)
        TextView label;
        @BindView(R.id.chat_list_message)
        TextView message;

        public MyViewHolder(View itemView) {
            super(itemView);
        }

        private void setTextColor(int color) {
            nickName.setTextColor(color);
            label.setTextColor(color);
            message.setTextColor(color);
        }

        private void setTextColor(ColorStateList color) {
            nickName.setTextColor(color);
            label.setTextColor(color);
            message.setTextColor(color);
        }

        private void fillData(LiveChatMessage chatMessage) {
            nickName.setText(chatMessage.getNickName());
            message.setText(chatMessage.getMessage());
        }
    }
}
