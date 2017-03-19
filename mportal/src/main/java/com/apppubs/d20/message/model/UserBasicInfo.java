package com.apppubs.d20.message.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by zhangwen on 2017/1/9.
 */

public class UserBasicInfo implements Serializable{

    @SerializedName("userid")
    private String userId;
    @SerializedName("truename")
    private String trueName;
    @SerializedName("username")
    private String username;
    @SerializedName("photourl")
    private String atatarUrl;
    @SerializedName("appcodeversion")
    private String appCodeVersion;//客户端版本代码

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAtatarUrl() {
        return atatarUrl;
    }

    public void setAtatarUrl(String atatarUrl) {
        this.atatarUrl = atatarUrl;
    }

    public String getAppCodeVersion() {
        return appCodeVersion;
    }

    public void setAppCodeVersion(String appCodeVersion) {
        this.appCodeVersion = appCodeVersion;
    }


}
