package com.tianyue.tv.Bean;

/**
 * 关注主播的信息
 * Created by hasee on 2016/11/7.
 */
public class AttentionAnchorInfo {
    private String headUrl;//头像的URL
    private String nickName;//主播昵称
    private String uuid;//主播id
    private boolean liveState;//直播状态

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isLiveState() {
        return liveState;
    }

    public void setLiveState(boolean liveState) {
        this.liveState = liveState;
    }
}
