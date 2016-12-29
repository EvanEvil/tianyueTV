package com.tianyue.tv.Bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 直播间信息类
 * Created by hasee on 2016/11/11.
 */
public class Broadcast implements Parcelable {
    //    //直播实时截图（以判断接收七牛还是奥点云）
//    private String image;
//    //七牛播放地址
//    private String ql_push_flow;
//    //房间名
//    private String stream;
//    //奥点云播放地址
//    private String playAddress;
//    //用户名
//    private String nickName;
//    //直播房间名
//    private String name;
//    //头像url
//    private String headUrl;
//    private String id;
//    //在线人数
//    private String onlineNum;
//    //判断七牛或者奥点云直播(null和0是奥点云，1为七牛直播)
//    private Integer isPushPOM;
//    //用户id
//    private String user_id;

    private String ql_push_flow;
    //直播实时截图（以判断接收七牛还是奥点云）
    private String image;
    private String city;
    private int cityCode;
    //奥点云播放地址
    private String publishAddress;
    private int onlineNum;
    private int focusNum;
    private String keyWord;
    private String tytypeId;
    private int uid;
    private String ql_playAddress;
    //房间名
    private String stream;
    //七牛播放地址
    private String playAddress;
    private String appid;
    //直播房间名
    private String name;
    private int blive;
    private int id;
    private int endTime;
    private int beginTime;
    //判断七牛或者奥点云直播(null和0是奥点云，1为七牛直播)
    private String isPushPOM;
    private String bctypeId;

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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public String getPublishAddress() {
        return publishAddress;
    }

    public void setPublishAddress(String publishAddress) {
        this.publishAddress = publishAddress;
    }

    public int getOnlineNum() {
        return onlineNum;
    }

    public void setOnlineNum(int onlineNum) {
        this.onlineNum = onlineNum;
    }

    public int getFocusNum() {
        return focusNum;
    }

    public void setFocusNum(int focusNum) {
        this.focusNum = focusNum;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getTytypeId() {
        return tytypeId;
    }

    public void setTytypeId(String tytypeId) {
        this.tytypeId = tytypeId;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getQl_playAddress() {
        return ql_playAddress;
    }

    public void setQl_playAddress(String ql_playAddress) {
        this.ql_playAddress = ql_playAddress;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getPlayAddress() {
        return playAddress;
    }

    public void setPlayAddress(String playAddress) {
        this.playAddress = playAddress;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBlive() {
        return blive;
    }

    public void setBlive(int blive) {
        this.blive = blive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(int beginTime) {
        this.beginTime = beginTime;
    }

    public String getIsPushPOM() {
        return isPushPOM;
    }

    public void setIsPushPOM(String isPushPOM) {
        this.isPushPOM = isPushPOM;
    }

    public String getBctypeId() {
        return bctypeId;
    }

    public void setBctypeId(String bctypeId) {
        this.bctypeId = bctypeId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ql_push_flow);
        dest.writeString(this.image);
        dest.writeString(this.city);
        dest.writeInt(this.cityCode);
        dest.writeString(this.publishAddress);
        dest.writeInt(this.onlineNum);
        dest.writeInt(this.focusNum);
        dest.writeString(this.keyWord);
        dest.writeString(this.tytypeId);
        dest.writeInt(this.uid);
        dest.writeString(this.ql_playAddress);
        dest.writeString(this.stream);
        dest.writeString(this.playAddress);
        dest.writeString(this.appid);
        dest.writeString(this.name);
        dest.writeInt(this.blive);
        dest.writeInt(this.id);
        dest.writeInt(this.endTime);
        dest.writeInt(this.beginTime);
        dest.writeString(this.isPushPOM);
        dest.writeString(this.bctypeId);
    }

    public Broadcast() {
    }

    protected Broadcast(Parcel in) {
        this.ql_push_flow = in.readString();
        this.image = in.readString();
        this.city = in.readString();
        this.cityCode = in.readInt();
        this.publishAddress = in.readString();
        this.onlineNum = in.readInt();
        this.focusNum = in.readInt();
        this.keyWord = in.readString();
        this.tytypeId = in.readString();
        this.uid = in.readInt();
        this.ql_playAddress = in.readString();
        this.stream = in.readString();
        this.playAddress = in.readString();
        this.appid = in.readString();
        this.name = in.readString();
        this.blive = in.readInt();
        this.id = in.readInt();
        this.endTime = in.readInt();
        this.beginTime = in.readInt();
        this.isPushPOM = in.readString();
        this.bctypeId = in.readString();
    }

    public static final Parcelable.Creator<Broadcast> CREATOR = new Parcelable.Creator<Broadcast>() {
        @Override
        public Broadcast createFromParcel(Parcel source) {
            return new Broadcast(source);
        }

        @Override
        public Broadcast[] newArray(int size) {
            return new Broadcast[size];
        }
    };
}
