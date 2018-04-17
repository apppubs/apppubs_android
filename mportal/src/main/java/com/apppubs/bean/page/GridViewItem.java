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
    private String badgeURL;
    private String badgeTxt;

    public GridViewItem(String jsonStr) {
        try {
            JSONObject jo = new JSONObject(jsonStr);
            this.title = jo.getString("title");
            this.picUrl = jo.getString("picurl");
            this.action = jo.getString("url");
            this.badgeURL = jo.getString("badgeurl");
            this.badgeTxt = jo.has("badgetext") ? jo.getString("badgetext") : null;
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

    public String getBadgeURL() {
        return badgeURL;
    }

    public void setBadgeURL(String badgeURL) {
        this.badgeURL = badgeURL;
    }

    public String getBadgeTxt() {
        return badgeTxt;
    }

    public void setBadgeTxt(String badgeTxt) {
        this.badgeTxt = badgeTxt;
    }
}
