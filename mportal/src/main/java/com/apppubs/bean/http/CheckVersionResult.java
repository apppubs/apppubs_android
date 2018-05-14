package com.apppubs.bean.http;

public class CheckVersionResult implements IJsonResult {

    private int updateType;
    private String versionName;
    private String versionCode;
    private String describe;
    private String downloadURL;

    public void setUpdateType(int updateType) {
        this.updateType = updateType;
    }

    public int getUpdateType() {
        return updateType;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public String getDownloadURL() {
        return downloadURL;
    }
}
