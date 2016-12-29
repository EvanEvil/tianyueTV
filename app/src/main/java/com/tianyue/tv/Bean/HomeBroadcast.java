package com.tianyue.tv.Bean;

import java.util.List;

/**
 * 首页直播数据模型
 * Created by hasee on 2016/12/27.
 */
public class HomeBroadcast {

    private List<DataListBean> dataList;

    public List<DataListBean> getDataList() {
        return dataList;
    }

    public void setDataList(List<DataListBean> dataList) {
        this.dataList = dataList;
    }

    public static class DataListBean {
        /**
         * ql_push_flow : rtmp://pili-play.tianyue.tv/tianyue/10010085
         * image : http://cdn.dvr.aodianyun.com/pic/hz-vod/images/demo_app.10010085.1482305050/demo_app.10010085.1482305050.1482305593/0/0
         * nickName : 天越网455
         * headUrl : http://images.tianyue.tv/2016/09/10085_5f1631a21136067b26ab0a592ff05453.jpg
         * onlineNum : 35114
         * tytypeId : 301
         * stream : 10010085
         * user_id : 10085
         * playAddress : rtmp://lssplay.tianyue.tv/demo_app/10010085
         * name : 小啊啊啊
         * id : 57
         * isPushPOM : 0
         * bctypeId : 300
         */
        /**
         * 七牛播放地址
         */
        private String ql_push_flow;
        /**
         * 直播封面
         */
        private String image;
        /**
         * 昵称
         */
        private String nickName;
        private String headUrl;
        /**
         * 在线人数
         */
        private int onlineNum;
        /**
         * 子分类
         */
        private int tytypeId;
        private String stream;
        private int user_id;
        /**
         * 奥点云播放地址
         */
        private String playAddress;
        private String focusNum;
        /**
         * 房间名
         */
        private String name;
        private int id;
        private String isPushPOM;
        /**
         * 主分类
         */
        private int bctypeId;


        public String getFocusNum() {
            return focusNum;
        }

        public void setFocusNum(String focusNum) {
            this.focusNum = focusNum;
        }

        public String getQl_push_flow() {
            return ql_push_flow;
        }

        public void setQl_push_flow(String ql_push_flow) {
            this.ql_push_flow = ql_push_flow;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getHeadUrl() {
            return headUrl;
        }

        public void setHeadUrl(String headUrl) {
            this.headUrl = headUrl;
        }

        public int getOnlineNum() {
            return onlineNum;
        }

        public void setOnlineNum(int onlineNum) {
            this.onlineNum = onlineNum;
        }

        public int getTytypeId() {
            return tytypeId;
        }

        public void setTytypeId(int tytypeId) {
            this.tytypeId = tytypeId;
        }

        public String getStream() {
            return stream;
        }

        public void setStream(String stream) {
            this.stream = stream;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getPlayAddress() {
            return playAddress;
        }

        public void setPlayAddress(String playAddress) {
            this.playAddress = playAddress;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getIsPushPOM() {
            return isPushPOM;
        }

        public void setIsPushPOM(String isPushPOM) {
            this.isPushPOM = isPushPOM;
        }

        public int getBctypeId() {
            return bctypeId;
        }

        public void setBctypeId(int bctypeId) {
            this.bctypeId = bctypeId;
        }
    }
}
