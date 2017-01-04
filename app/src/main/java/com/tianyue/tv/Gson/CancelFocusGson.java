package com.tianyue.tv.Gson;

/**取消关注的JavaBean
 * Created by Evan on 2017/1/4.
 *
 *  {"ret":"success","count":18}
    {"status":"error"}
 */

public class CancelFocusGson {


    /**
     * ret : success
     * count : 18
     */

    public String ret;
    public int count;
    public String status;

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
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
