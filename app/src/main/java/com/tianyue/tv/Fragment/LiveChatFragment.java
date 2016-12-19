package com.tianyue.tv.Fragment;

import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.aodianyun.dms.android.DMS;
import com.tianyue.tv.Adapter.ChatListAdapter;
import com.tianyue.tv.Bean.LiveChatMessage;
import com.tianyue.tv.Interface.DmsMessageCallback;
import com.tianyue.tv.Interface.DmsMessageSendCallBack;
import com.tianyue.tv.MyApplication;
import com.tianyue.tv.R;
import com.tianyue.tv.Util.DmsUtil;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 聊天Fragment
 * Created by hasee on 2016/11/30.
 */
public class LiveChatFragment extends BaseFragment implements
        DmsUtil.SendMessageCallBack,
        DmsUtil.MessageCallBack{
    @BindView(R.id.chat_fragment_list)
    RecyclerView list;
    @BindView(R.id.chat_fragment_gift)
    ImageButton gift;
    @BindView(R.id.chat_fragment_ll)
    LinearLayout linearLayout;

    PopupWindow popupWindow;
    @BindView(R.id.chat_fragment_edit)
    EditText inputMessage;
    @BindView(R.id.chat_fragment_send)
    Button sendMessage;

    private List<LiveChatMessage> messageList = new ArrayList<>();

    private ChatListAdapter chatListAdapter;

    private String topic = "10085";

    private String pubKey = "pub_10b9de636853250321775ca9cafd79fe";

    private String subKey = "sub_150bb87bced5009531ab1cc5471c2be7";

    DmsUtil dmsUtil;
    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.chat_fragment, null);
        return view;
    }

    @Override
    protected void init() {
        Log.i(TAG, "init: ");
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.gift_popup_window, null);

        popupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        chatListAdapter = new ChatListAdapter(getActivity(), messageList);
        list.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        list.setAdapter(chatListAdapter);
        dmsUtil = DmsUtil.instance(context);
        dmsUtil.initDMS(topic);
        dmsUtil.connectDMS();
        dmsUtil.setSendMessageCallBack(this);
        dmsUtil.setMessageCallBack(this);
    }



    @OnClick({R.id.chat_fragment_gift, R.id.chat_fragment_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chat_fragment_gift:
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    popupWindow.showAtLocation(gift, Gravity.BOTTOM, 0, linearLayout.getHeight());
                }
                break;
            case R.id.chat_fragment_send:
                String message = inputMessage.getText().toString();
                if (message.equals("")) {
                    return;
                }
                dmsUtil.sendMessage(message);
                break;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onMessage(String result) {
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
        list.scrollToPosition(messageList.size() - 1);
    }

    @Override
    public void onSuccess(String result) {
        inputMessage.setText("");
    }

    @Override
    public void onFailure(String result) {

    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
    }


}
