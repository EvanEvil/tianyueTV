package com.tianyue.tv.Bean;
import java.io.Serializable;

/**
 * 用户信息
 * Created by hasee on 2016/9/7.
 */
public class User implements Serializable {
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

}
