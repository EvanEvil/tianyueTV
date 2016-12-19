package com.tianyue.tv.Gson;

import java.io.Serializable;

/**
 * Created by hasee on 2016/8/9.
 */
public class HotGson implements Serializable {
    private String hotSearch;

    public String getHotSearch() {
        return hotSearch;
    }

    public void setHotSearch(String hotSearch) {
        this.hotSearch = hotSearch;
    }
}
