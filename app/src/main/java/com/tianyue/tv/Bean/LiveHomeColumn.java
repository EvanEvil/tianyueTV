package com.tianyue.tv.Bean;

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

    public static class LiveHomeColumnContent {
        private String title;
        private String picUrl;
        private int resourceId;
        private String nickName;
        private Integer number;

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

        public Integer getNumber() {
            return number;
        }

        public void setNumber(Integer number) {
            this.number = number;
        }

        public int getResourceId() {
            return resourceId;
        }

        public void setResourceId(int resourceId) {
            this.resourceId = resourceId;
        }
    }

}
