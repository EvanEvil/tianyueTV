package com.tianyue.tv.Gson;

import com.tianyue.tv.Bean.User;

/**
 * Created by hasee on 2016/9/30.
 */
public class LoginGson {

    private User user;
    private String status;
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    }
