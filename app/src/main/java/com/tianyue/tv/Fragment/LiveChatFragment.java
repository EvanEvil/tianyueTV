package com.tianyue.tv.Fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.tianyue.tv.Activity.Live.LiveDetails;
import com.tianyue.tv.Adapter.ChatListAdapter;
import com.tianyue.tv.Bean.LiveChatMessage;
import com.tianyue.tv.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 聊天Fragment
 * Created by hasee on 2016/11/30.
 */
public class LiveChatFragment extends BaseFragment {
    @BindView(R.id.chat_fragment_list)
    RecyclerView list;


    PopupWindow popupWindow;


    private List<LiveChatMessage> messageList = new ArrayList<>();

    private ChatListAdapter chatListAdapter;

    private String topic = "10085";

    private String pubKey = "pub_10b9de636853250321775ca9cafd79fe";

    private String subKey = "sub_150bb87bced5009531ab1cc5471c2be7";
    private LiveDetails activity;


    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment, null);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    protected void init() {
        Log.i(TAG, "init: ");
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.gift_popup_window, null);

//        popupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        popupWindow.setOutsideTouchable(true);
//        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        chatListAdapter = new ChatListAdapter(getActivity(), messageList);
        list.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        list.setAdapter(chatListAdapter);

    }





//    @OnClick({R.id.chat_fragment_gift, R.id.chat_fragment_send})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.chat_fragment_gift:
//                if (popupWindow.isShowing()) {
//                    popupWindow.dismiss();
//                } else {
//                    popupWindow.showAtLocation(gift, Gravity.BOTTOM, 0, linearLayout.getHeight());
//                }
//                break;
//
//        }
//    }






    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void finishCreateView(Bundle state) {

    }


    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
        //反注册
        EventBus.getDefault().unregister(this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = false,priority = 1)
    public void onMsgSuccess(LiveChatMessage msg){

        messageList.add(msg);
        chatListAdapter.notifyDataSetChanged();
        list.scrollToPosition(messageList.size() - 1);
    }

}
