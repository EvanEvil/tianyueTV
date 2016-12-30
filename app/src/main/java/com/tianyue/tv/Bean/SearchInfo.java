package com.tianyue.tv.Bean;

import java.util.List;

/**
 * Created by Administrator on 2016/12/21.
 */

public class SearchInfo {

    /**
     * ret : success
     * BroadCastUser : [{"tytypeId":406,"ql_push_flow":"rtmp://pili-play.tianyue.tv/tianyue/30110339","stream":"30110339","user_id":10339,"nickName":"苍凉的鼻毛","playAddress":"rtmp://lssplay.tianyue.tv/demo_app/30110339","name":"澄百合学院","headUrl":"http://images.tianyue.tv//uploads/20161125/mdxy5n7s2deyd7dxo3ue2h8afnq27qwa.jpg","id":127,"onlineNum":283,"isPushPOM":"1","bctypeId":400}]
     */

    private String ret;
    private List<BroadCastUserBean> BroadCastUser;

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    public List<BroadCastUserBean> getBroadCastUser() {
        return BroadCastUser;
    }

    public void setBroadCastUser(List<BroadCastUserBean> BroadCastUser) {
        this.BroadCastUser = BroadCastUser;
    }

    public static class BroadCastUserBean {
        /**
         * tytypeId : 406
         * ql_push_flow : rtmp://pili-play.tianyue.tv/tianyue/30110339
         * stream : 30110339
         * user_id : 10339
         * nickName : 苍凉的鼻毛
         * playAddress : rtmp://lssplay.tianyue.tv/demo_app/30110339
         * name : 澄百合学院
         * headUrl : http://images.tianyue.tv//uploads/20161125/mdxy5n7s2deyd7dxo3ue2h8afnq27qwa.jpg
         * id : 127
         * onlineNum : 283
         * isPushPOM : 1
         * bctypeId : 400
         */

        private int tytypeId;
        private String ql_push_flow;
        private String stream;
        private int user_id;
        private String nickName;
        private String playAddress;
        private String name;
        private String headUrl;
        private int id;
        private int onlineNum;
        private String isPushPOM;
        private int bctypeId;
        private String image;
        private String focusNum;

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getFocusNum() {
            return focusNum;
        }

        public void setFocusNum(String focusNum) {
            this.focusNum = focusNum;
        }

        public int getTytypeId() {
            return tytypeId;
        }

        public void setTytypeId(int tytypeId) {
            this.tytypeId = tytypeId;
        }

        public String getQl_push_flow() {
            return ql_push_flow;
        }

        public void setQl_push_flow(String ql_push_flow) {
            this.ql_push_flow = ql_push_flow;
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

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
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

        public String getHeadUrl() {
            return headUrl;
        }

        public void setHeadUrl(String headUrl) {
            this.headUrl = headUrl;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getOnlineNum() {
            return onlineNum;
        }

        public void setOnlineNum(int onlineNum) {
            this.onlineNum = onlineNum;
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
