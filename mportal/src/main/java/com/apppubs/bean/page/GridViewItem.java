package com.apppubs.bean.page;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by siger on 2018/4/17.
 */
public class GridViewItem {
    private String title;
    private String picUrl;
    private String action;
    private Integer badgeNum;

    public GridViewItem(String jsonStr) {
        try {
            JSONObject jo = new JSONObject(jsonStr);
            this.title = jo.getString("title");
            this.picUrl = jo.getString("picURL");
            this.action = jo.getString("URL");
            this.badgeNum = jo.has("badgeNum") ? jo.getInt("badgeNum") : null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getBadgeNum() {
        return badgeNum;
    }

    public void setBadgeNum(Integer badgeNum) {
        this.badgeNum = badgeNum;
    }
}
