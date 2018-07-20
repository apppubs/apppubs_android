package com.apppubs.bean.http;//
//	AdbookInfoResult.java
//	Model file generated using JSONExport: https://github.com/Ahmed-Ali/JSONExport

import java.io.Serializable;

public class AdbookInfoResult implements IJsonResult, Serializable {

    private boolean chatPermission;
    private String chatPermissionStr;
    private String downloadURL;
    private boolean readPermission;
    private String readPermissionStr;
    private String rootDeptId;
    private String updateTime;
    private int version;

    public void setChatPermission(boolean chatPermission) {
        this.chatPermission = chatPermission;
    }

    public boolean isChatPermission() {
        return this.chatPermission;
    }

    public void setChatPermissionStr(String chatPermissionStr) {
        this.chatPermissionStr = chatPermissionStr;
    }

    public String getChatPermissionStr() {
        return this.chatPermissionStr;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public String getDownloadURL() {
        return this.downloadURL;
    }

    public void setReadPermission(boolean readPermission) {
        this.readPermission = readPermission;
    }

    public boolean needReadPermission() {
        return this.readPermission;
    }

    public void setReadPermissionStr(String readPermissionStr) {
        this.readPermissionStr = readPermissionStr;
    }

    public String needReadPermissionStr() {
        return this.readPermissionStr;
    }

    public void setRootDeptId(String rootDeptId) {
        this.rootDeptId = rootDeptId;
    }

    public String getRootDeptId() {
        return this.rootDeptId;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateTime() {
        return this.updateTime;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return this.version;
    }


}