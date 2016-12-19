package com.tianyue.tv.Gson;

import java.util.List;

/**
 * Created by hasee on 2016/9/9.
 */
public class LiveListGson {
    private Integer pageCount;
    private Integer pageNo;
    private List<DataList> dataList;

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public List<DataList> getDataList() {
        return dataList;
    }

    public void setDataList(List<DataList> dataList) {
        this.dataList = dataList;
    }

    public class DataList{
        private String image;
        private String stream;
        private String nickname;
        private String name;
        private String id;
        private String onlineNum;
        private String faceImage;

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getStream() {
            return stream;
        }

        public void setStream(String stream) {
            this.stream = stream;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getOnlineNum() {
            return onlineNum;
        }

        public void setOnlineNum(String onlineNum) {
            this.onlineNum = onlineNum;
        }

        public String getFaceImage() {
            return faceImage;
        }

        public void setFaceImage(String faceImage) {
            this.faceImage = faceImage;
        }
    }
}
