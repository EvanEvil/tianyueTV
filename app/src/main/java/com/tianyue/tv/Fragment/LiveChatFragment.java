package com.tianyue.tv.Fragment;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tianyue.tv.Activity.Live.LiveDetails;
import com.tianyue.tv.Adapter.ChatListAdapter;
import com.tianyue.tv.Bean.EventBusBean.EventMsg;
import com.tianyue.tv.Bean.LiveChatMessage;
import com.tianyue.tv.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 聊天Fragment
 * Created by hasee on 2016/11/30.
 */
public class LiveChatFragment extends BaseFragment {
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

        popupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        chatListAdapter = new ChatListAdapter(getActivity(), messageList);
        list.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        list.setAdapter(chatListAdapter);
        inputMessage.setOnFocusChangeListener(new MyOnFocusChangeListener());
        inputMessage.setOnEditorActionListener(new MyOnEditorActionListener());
    }

    /**
     * editText焦点监听
     */
    class MyOnFocusChangeListener implements View.OnFocusChangeListener{

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                Log.e(TAG,"输入框获取到焦点");
                EventMsg eventMsg = new EventMsg();
                eventMsg.setHiddenTabLayout(true);
                EventBus.getDefault().post(eventMsg);
            }else{
                Log.e(TAG,"输入框失去焦点");
                EventMsg eventMsg = new EventMsg();
                eventMsg.setHiddenTabLayout(false);
                EventBus.getDefault().post(eventMsg);
            }
        }
    }

    /**
     * 软键盘监听
     */
    class MyOnEditorActionListener implements TextView.OnEditorActionListener{

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.e(TAG,"actionid:"+actionId);
                //点击软键盘发送的时候，隐藏软键盘，显示tablayout
                if(actionId == EditorInfo.IME_ACTION_SEND){
                    EventMsg eventMsg = new EventMsg();
                    eventMsg.setHiddenTabLayout(false);
                    EventBus.getDefault().post(eventMsg);
                    sendMsg();
                    hintKbTwo();
                }
            return false;
        }
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
                sendMsg();

                break;
        }
    }

    /**
     * 发送消息
     */
    private void sendMsg() {
        String message = inputMessage.getText().toString();
        if (message.equals("")) {

            showToast("发送的消息不能为空哦");
            return;
        }
        activity = (LiveDetails) getActivity();
        activity.dmsUtil.sendMessage(message);

        //发送弹幕


        hintKbTwo();
    }

    //此方法只是关闭软键盘
    private void hintKbTwo() {
        activity = (LiveDetails) getActivity();
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()&&activity.getCurrentFocus()!=null){
            if (activity.getCurrentFocus().getWindowToken()!=null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }


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
    }
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = false,priority = 1)
    public void onMsgSuccess(LiveChatMessage msg){
        inputMessage.setText("");
        messageList.add(msg);
        chatListAdapter.notifyDataSetChanged();
        list.scrollToPosition(messageList.size() - 1);
    }

}
