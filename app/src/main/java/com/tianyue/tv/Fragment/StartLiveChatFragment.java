package com.tianyue.tv.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tianyue.tv.Adapter.ChatListAdapter;
import com.tianyue.tv.Bean.LiveChatMessage;
import com.tianyue.tv.R;
import com.tianyue.tv.Util.DmsUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 *
 * Created by hasee on 2016/12/9.
 */
public class StartLiveChatFragment extends BaseFragment {
    @BindView(R.id.start_live_chat_recycler)
    RecyclerView recycler;

    List<LiveChatMessage> messageList = new ArrayList<>();

    DmsUtil dmsUtil;

    ChatListAdapter chatListAdapter;
    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.start_live_chat_fragment, container, false);
    }

    @Override
    protected void init() {
        recycler.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        dmsUtil = DmsUtil.instance(context);
        dmsUtil.initDMS("10085");
        dmsUtil.connectDMS();
        dmsUtil.setMessageCallBack(messageCallBack);
        chatListAdapter = new ChatListAdapter(context,messageList,ChatListAdapter.INPUT_LIVE_TYPE);
        recycler.setAdapter(chatListAdapter);
    }

    DmsUtil.MessageCallBack messageCallBack = new DmsUtil.MessageCallBack() {
        @Override
        public void onMessage(String result) {
            Log.i(TAG, "onMessage: "+result);
            String[] receives = result.split("\\{aodiandmssplit\\}");
            String nickName = receives[0];
            String sendTime = receives[1];
            String message = receives[2];
            LiveChatMessage chatMessage = new LiveChatMessage();
            chatMessage.setNickName(DmsUtil.unescape(nickName));
            chatMessage.setSendTime(sendTime);
            chatMessage.setMessage(message);
            if (messageList.size() >= 20) {
                for (int i = 0; i < 5; i++) {
                    messageList.remove(i);
                }
            }
            messageList.add(chatMessage);
            chatListAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        dmsUtil.connectDMS();
    }
}
