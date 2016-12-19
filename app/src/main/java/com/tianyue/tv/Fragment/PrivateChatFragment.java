package com.tianyue.tv.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tianyue.tv.Adapter.PrivateChatAdapter;
import com.tianyue.tv.R;

/**
 * Created by hasee on 2016/8/15.
 */
public class PrivateChatFragment extends Fragment {
    ListView chatList;
    PrivateChatAdapter chatAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.private_chat_home,null);
        initVIew(view);
        return view;
    }
    /**
     * 初始化私聊页面控件
     */
    private void initVIew(View view){
        chatList = (ListView) view.findViewById(R.id.private_chat_lists);
        chatAdapter = new PrivateChatAdapter(getActivity());
        chatList.setAdapter(chatAdapter);
    }
}

