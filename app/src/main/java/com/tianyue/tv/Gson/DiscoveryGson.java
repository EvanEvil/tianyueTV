package com.tianyue.tv.Gson;


import java.io.Serializable;
import java.util.List;

/**
 * Created by hasee on 2016/9/18.
 */
public class DiscoveryGson {
    private Integer pageSize;
    private Integer pageNo;
    private Integer totalSize;
    private Integer pageCount;
    private Integer typeId;
    private String goosName;
    private String sort;
    private List<DataList> dataList;

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getGoosName() {
        return goosName;
    }

    public void setGoosName(String goosName) {
        this.goosName = goosName;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public List<DataList> getDataList() {
        return dataList;
    }

    public void setDataList(List<DataList> dataList) {
        this.dataList = dataList;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Integer totalSize) {
        this.totalSize = totalSize;
    }
    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public class DataList implements Serializable {
        private int id;
        private Integer newsType;
        private String title;
        private String newsDesc;
        private String content;
        private long newsTime;
        private Integer newsCount;
        private String newsFrom;
        private Integer praiseNum;
        private String shortImage;
        private String faceImage;
        private Integer bhot;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Integer getNewsType() {
            return newsType;
        }

        public void setNewsType(Integer newsType) {
            this.newsType = newsType;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getNewsDesc() {
            return newsDesc;
        }

        public void setNewsDesc(String newsDesc) {
            this.newsDesc = newsDesc;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public long getNewsTime() {
            return newsTime;
        }

        public void setNewsTime(long newsTime) {
            this.newsTime = newsTime;
        }

        public Integer getNewsCount() {
            return newsCount;
        }

        public void setNewsCount(Integer newsCount) {
            this.newsCount = newsCount;
        }

        public String getNewsFrom() {
            return newsFrom;
        }

        public void setNewsFrom(String newsFrom) {
            this.newsFrom = newsFrom;
        }

        public Integer getPraiseNum() {
            return praiseNum;
        }

        public void setPraiseNum(Integer praiseNum) {
            this.praiseNum = praiseNum;
        }

        public String getShortImage() {
            return shortImage;
        }

        public void setShortImage(String shortImage) {
            this.shortImage = shortImage;
        }

        public String getFaceImage() {
            return faceImage;
        }

        public void setFaceImage(String faceImage) {
            this.faceImage = faceImage;
        }

        public Integer getBhot() {
            return bhot;
        }

        public void setBhot(Integer bhot) {
            this.bhot = bhot;
        }
    }
}
