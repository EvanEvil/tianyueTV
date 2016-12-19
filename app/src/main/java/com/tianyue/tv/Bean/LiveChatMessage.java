package com.tianyue.tv.Bean;

/**
 * 聊天信息
 * Created by hasee on 2016/12/8.
 */
public class LiveChatMessage {
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 发送时间
     */
    private String sendTime;
    /**
     * 消息内容
     */
    private String message;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
