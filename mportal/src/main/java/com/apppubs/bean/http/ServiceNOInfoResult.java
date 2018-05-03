package com.apppubs.bean.http;

import java.util.Date;

public class ServiceNOInfoResult implements IJsonResult{

    private String id;
    private String name;
    private String picURL;
    private String desc;
    private Date createTime;
    private int order;
    private int type;
    private int receiveType;
    private boolean isSubscribed;
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setPicURL(String picURL) {
        this.picURL = picURL;
    }
    public String getPicURL() {
        return picURL;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getDesc() {
        return desc;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public Date getCreateTime() {
        return createTime;
    }

    public void setOrder(int order) {
        this.order = order;
    }
    public int getOrder() {
        return order;
    }

    public void setType(int type) {
        this.type = type;
    }
    public int getType() {
        return type;
    }

    public void setReceiveType(int receiveType) {
        this.receiveType = receiveType;
    }
    public int getReceiveType() {
        return receiveType;
    }

    public void setIsSubscribed(boolean isSubscribed) {
        this.isSubscribed = isSubscribed;
    }
    public boolean getIsSubscribed() {
        return isSubscribed;
    }
}
