package com.tianyue.tv.Bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 用户信息
 * Created by hasee on 2016/9/7.
 */
public class User implements Parcelable {
    private String id;
    private String uuid;
    private String nickName;
    private String userName;
    private String password;
    private String telephone;
    private String identity;
    private String identityCard;
    private Integer bCard;//1通过 2审核中 0未通过 3直播延续
    private String cardImage;
    private String sex;
    private Integer baudit;
    private String age;
    private String headUrl;//头像地址
    private String province;
    private String live_streaming_address;

    public String getLive_streaming_address() {
        return live_streaming_address;
    }

    public void setLive_streaming_address(String live_streaming_address) {
        this.live_streaming_address = live_streaming_address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    public Integer getbCard() {
        return bCard;
    }

    public void setbCard(Integer bCard) {
        this.bCard = bCard;
    }

    public String getCardImage() {
        return cardImage;
    }

    public void setCardImage(String cardImage) {
        this.cardImage = cardImage;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public Integer getBaudit() {
        return baudit;
    }

    public void setBaudit(Integer baudit) {
        this.baudit = baudit;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.uuid);
        dest.writeString(this.nickName);
        dest.writeString(this.userName);
        dest.writeString(this.password);
        dest.writeString(this.telephone);
        dest.writeString(this.identity);
        dest.writeString(this.identityCard);
        dest.writeValue(this.bCard);
        dest.writeString(this.cardImage);
        dest.writeString(this.sex);
        dest.writeValue(this.baudit);
        dest.writeString(this.age);
        dest.writeString(this.headUrl);
        dest.writeString(this.province);
        dest.writeString(this.live_streaming_address);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.id = in.readString();
        this.uuid = in.readString();
        this.nickName = in.readString();
        this.userName = in.readString();
        this.password = in.readString();
        this.telephone = in.readString();
        this.identity = in.readString();
        this.identityCard = in.readString();
        this.bCard = (Integer) in.readValue(Integer.class.getClassLoader());
        this.cardImage = in.readString();
        this.sex = in.readString();
        this.baudit = (Integer) in.readValue(Integer.class.getClassLoader());
        this.age = in.readString();
        this.headUrl = in.readString();
        this.province = in.readString();
        this.live_streaming_address = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
