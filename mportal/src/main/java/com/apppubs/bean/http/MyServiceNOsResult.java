package com.apppubs.bean.http;

import java.util.Date;
import java.util.List;

public class MyServiceNOsResult implements IJsonResult {

    private List<MyServiceNOItem> items;

    public void setItems(List<MyServiceNOItem> items) {
        this.items = items;
    }

    public List<MyServiceNOItem> getItems() {
        return items;
    }

    public class MyServiceNOItem {
        private String id;
        private String name;
        private String picURL;
        private String desc;
        private Date createTime;
        private int type;
        private int receiveType;
        private int unreadCount;

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

        public void setUnreadCount(int unreadCount) {
            this.unreadCount = unreadCount;
        }

        public int getUnreadCount() {
            return unreadCount;
        }
    }
}

