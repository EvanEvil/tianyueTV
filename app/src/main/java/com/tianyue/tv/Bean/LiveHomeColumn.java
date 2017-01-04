package com.tianyue.tv.Bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by hasee on 2016/12/7.
 */
public class LiveHomeColumn {
    private String classify;

    private List<LiveHomeColumnContent> contents;

    public List<LiveHomeColumnContent> getContents() {
        return contents;
    }

    public void setContents(List<LiveHomeColumnContent> contents) {
        this.contents = contents;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public static class LiveHomeColumnContent implements Parcelable {
        private String title;
        private String picUrl;
        private String headUrl;
        private String userId;
        private String focusNum;
        private int resourceId;
        private String nickName;
        private String number;
        private String isPushPOM;
        private String playAddress;
        private String ql_push_flow;
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFocusNum() {
            return focusNum;
        }

        public void setFocusNum(String focusNum) {
            this.focusNum = focusNum;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getHeadUrl() {
            return headUrl;
        }

        public void setHeadUrl(String headUrl) {
            this.headUrl = headUrl;
        }

        public String getIsPushPOM() {
            return isPushPOM;
        }

        public void setIsPushPOM(String isPushPOM) {
            this.isPushPOM = isPushPOM;
        }

        public String getPlayAddress() {
            return playAddress;
        }

        public void setPlayAddress(String playAddress) {
            this.playAddress = playAddress;
        }

        public String getQl_push_flow() {
            return ql_push_flow;
        }

        public void setQl_push_flow(String ql_push_flow) {
            this.ql_push_flow = ql_push_flow;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public int getResourceId() {
            return resourceId;
        }

        public void setResourceId(int resourceId) {
            this.resourceId = resourceId;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.title);
            dest.writeString(this.picUrl);
            dest.writeString(this.headUrl);
            dest.writeString(this.userId);
            dest.writeString(this.focusNum);
            dest.writeInt(this.resourceId);
            dest.writeString(this.nickName);
            dest.writeString(this.number);
            dest.writeString(this.isPushPOM);
            dest.writeString(this.playAddress);
            dest.writeString(this.ql_push_flow);
            dest.writeString(this.id);
        }

        public LiveHomeColumnContent() {
        }

        protected LiveHomeColumnContent(Parcel in) {
            this.title = in.readString();
            this.picUrl = in.readString();
            this.headUrl = in.readString();
            this.userId = in.readString();
            this.focusNum = in.readString();
            this.resourceId = in.readInt();
            this.nickName = in.readString();
            this.number = in.readString();
            this.isPushPOM = in.readString();
            this.playAddress = in.readString();
            this.ql_push_flow = in.readString();
            this.id = in.readString();
        }

        public static final Parcelable.Creator<LiveHomeColumnContent> CREATOR = new Parcelable.Creator<LiveHomeColumnContent>() {
            @Override
            public LiveHomeColumnContent createFromParcel(Parcel source) {
                return new LiveHomeColumnContent(source);
            }

            @Override
            public LiveHomeColumnContent[] newArray(int size) {
                return new LiveHomeColumnContent[size];
            }
        };
    }

}
