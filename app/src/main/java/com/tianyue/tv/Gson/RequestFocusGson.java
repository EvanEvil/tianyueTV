package com.tianyue.tv.Gson;

/**请求关注的JavaBean
 * Created by Evan on 2017/1/4.
 */

public class RequestFocusGson {

    /**
     * bcfocus : 250 当前关注的信息
     * count : 18    直播间的关注量的更新
     * status : success 成功 repeat 直播间不存在 出错了
     *        ：error 出错
     */

    public int bcfocus;
    public int count;
    public String status;

    public int getBcfocus() {
        return bcfocus;
    }

    public void setBcfocus(int bcfocus) {
        this.bcfocus = bcfocus;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
